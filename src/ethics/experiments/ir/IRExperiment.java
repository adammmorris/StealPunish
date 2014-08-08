package ethics.experiments.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import burlap.behavior.singleagent.planning.commonpolicies.EpsilonGreedy;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQLAgent;
import burlap.debugtools.DPrint;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import ethics.experiments.bimatrix.BonusVectorList;
import ethics.experiments.bimatrix.Experiment;
import ethics.experiments.bimatrix.Parallel;
import ethics.experiments.bimatrix.RealLoopBody;
import ethics.experiments.ir.FixedIR.FixedIRFactory;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Experiments w/ Initiator-Responder games
 * NOTE: The "order" of states (for arrays & such) is [(action0,action0),(action0,action1),(action1,action0),(action1,action1)]
 * @author Adam Morris
 *
 */
public class IRExperiment implements Experiment {
	/**
	 * SET THIS TO false BEFORE RUNNING FOR REAL
	 */
	private boolean practice = true; // set this to true if we're doing practice stuff, otherwise set to false
	
	protected final int numParams = 2;
	protected boolean runParallel = false;
	protected IRGame game;
	protected SGDomain domain;
	
	public int numBonusVectors; // this must be at least numParams + 1
	public int numRoundsPerMatch; // whatever you put here, it will actually be double, because they have to switch roles
	public int numMatchesPerTourn;
	
	protected boolean printStuff;
	protected final int debugId = 0;
	
	protected double gamma;
	protected double lr;
	protected double epsilon;
	protected double r_max;
	protected double q_max;
	protected double q_init;
	
	protected SGQFactory qFactory;
	protected FixedIRFactory all0Factory;
	protected FixedIRFactory all1Factory;
	
	//protected List<double[]> bonusVectors;

	protected String pathToResultCache;
	
	public static void main(String[] args) {
		//long startTime = System.nanoTime();
		//System.out.println(args[1]);
		if(args.length != 1 && args.length != 2){
			System.out.println("Wrong format. Use:\n\tpathToResultFolder (pathToVectorCache)");
			System.exit(-1);
		}
		
		// Set up the payoffs
		// STEAL-PUNISH
		/*double initiatorReward = 1;
		double initiateeReward = -1;
		double responderReward = -.5;
		double respondeeReward = -2.5;*/
		
		// SHARE-RECIPROCATE
		double initiatorReward = -.5;
		double initiateeReward = 1;
		double responderReward = -.5;
		double respondeeReward = 1;
		
		IRGame game = new IRGame(initiatorReward,initiateeReward,responderReward,respondeeReward);
		IRExperiment experiment = new IRExperiment(game);
		
		BonusVectorList bonusVectors;
		// Does the bonus vector list exist already?
		File file = new File(args[0]+"vectors.txt");
		//System.out.println(file.getAbsolutePath());
		if (file.exists()) {
			bonusVectors = new BonusVectorList(args[0]+"vectors.txt",experiment.numParams);
		} else {
			HashMap<Integer,Integer> paramToActionNum = new HashMap<Integer,Integer>();
			paramToActionNum.put(0, 1);
			paramToActionNum.put(1, 1);
			HashMap<Integer,Integer> paramToPlayerNum = new HashMap<Integer,Integer>();
			paramToPlayerNum.put(0, 0);
			paramToPlayerNum.put(1, 1);
			bonusVectors = new BonusVectorList(experiment.numBonusVectors,experiment.numParams,experiment.game,paramToPlayerNum,paramToActionNum);
			bonusVectors.writeVectorList(args[0]+"vectors.txt");
		}
		
		// Are we running on the grid?
		// Should be 5250 games
		if (args.length == 2) {
			int tasknum = Integer.parseInt(args[1]); // 1-indexed
			if (tasknum < 1 || tasknum > 5250) {
				System.out.println("Error: Tasknum is out of range");
				System.exit(-1);
			}
			
			// Are we doing Q vs FHs?
			if (tasknum <= experiment.numBonusVectors*2) {
				double winnings[] = null;
				String title = null;

				if (tasknum <= experiment.numBonusVectors) { // First FH
					winnings = experiment.runTourn_QvsFH(bonusVectors.getVector(tasknum),experiment.all0Factory);
					title = ""+(tasknum)+"vAll0";
				} else if (tasknum <= (experiment.numBonusVectors*2)) { // Second FH
					tasknum = tasknum - 100;
					winnings = experiment.runTourn_QvsFH(bonusVectors.getVector(tasknum),experiment.all1Factory);
					title = ""+(tasknum)+"vAll1";
				}
				
				experiment.writeResults(args[0]+title+".txt", winnings, title);
			} else { // QvsQs
				tasknum = tasknum - 200;
				int start = 0;
				int Q1 = 0;
				int Q2 = 0;
				for (int i = 1; i <= experiment.numBonusVectors; i++) {
					start = (202-i)*(i-1)/2;
					if (start < tasknum && tasknum <= (start+100-i+1)) {
						Q1 = i;
						break;
					}
				}
				Q2 = tasknum - start - 1 + Q1;
				double[][] winnings_tp = transpose(experiment.runTourn_QvsQ(bonusVectors.getVector(Q1),bonusVectors.getVector(Q2)));
				String title1 = ""+Q1+"v"+Q2;
				String title2 = ""+Q2+"v"+Q1;
				
				experiment.writeResults(args[0]+title1+".txt", winnings_tp[0], title1);
				if (Q1!=Q2) experiment.writeResults(args[0]+title2+".txt", winnings_tp[1], title2);
			}
		}
		
		//double[] bonusVector = {100,100,100,100,0,0,0,0};
		//experiment.runTourn_QvsQ(bonusVectors.getVector(8),bonusVectors.getVector(89));
		//System.out.println(System.nanoTime() - experiment.startTime);
		
		/*double[] bonusVector = {.75,-2.5};
		double[] bonusVector2 = {.75,-2.5};
		//experiment.runTourn_QvsFH(bonusVector, experiment.all1Factory);
		experiment.runTourn_QvsQ(bonusVector, bonusVector2);*/
		
		/*List<double[]> bonusVectorList = new ArrayList<double[]>();
		double[] bonusVector1 = {100,100,100,100,0,0,0,0};
		double[] bonusVector2 = {0,0,0,0,100,100,100,100};
		bonusVectorList.add(bonusVector1);
		bonusVectorList.add(bonusVector2);
		bonusVectors = new BonusVectorList(bonusVectorList);*/
		
		/*List<List<double[]>> winnings = experiment.runQAgainstAllsAndSelf(bonusVectors.getVectorList());
		String[] titles = {"QvsAll0","QvsAll1","QvsSelf"};
		experiment.writeResults(args[0]+"/QAgainstAllsAndSelf.txt",winnings, titles);
		
		List<List<double[]>> winnings2 = experiment.runQAgainstOtherQs(bonusVectors.getVectorList());
		String[] titles2 = new String[numBonusVectors];
		for (int i = 0; i < numBonusVectors; i++) titles2[i] = "Qvs"+i;
		experiment.writeResults(args[0]+"/QAgainstOtherQs.txt",winnings2, titles2);*/
		
		//System.out.println(System.nanoTime() - startTime);
	}
		
	public IRExperiment(IRGame game) {
		// Set up domain
		this.game = game;
		this.domain = (SGDomain)game.generateDomain();
			
		// Set parameters
		this.gamma = .99;
		this.lr = .1;
		this.epsilon = .1;
		
		if (this.practice) {
			// PRACTICE NUMBERS
			this.numBonusVectors = 100;
			this.numRoundsPerMatch = 2000;
			this.numMatchesPerTourn = 1;
			
			this.printStuff = true;
		} else {
			// REAL NUMBERS
			this.numBonusVectors = 100;
			this.numRoundsPerMatch = 2500;
			this.numMatchesPerTourn = 5000;
			
			this.printStuff = false;
		}
				
		// Set up printing stuff
		if (!this.printStuff) {
			DPrint.toggleCode(this.debugId, false);
		} else {
			DPrint.toggleCode(this.debugId, true);
		}
		
		this.r_max = this.game.getMaxAbsPayout();
		this.q_max = this.r_max / (1-this.gamma);
		
		this.q_init = (this.r_max+this.q_max)/50;
		
		// Set up Q-factory
		this.qFactory = new SGQFactory(domain, gamma, lr, q_init, new DiscreteStateHashFactory());

		// Set up fixed-history factories
		int[] policy_all0 = {0,0,0};
		int[] policy_all1 = {1,1,1};
		all0Factory = this.getFIRFactory(policy_all0);
		all1Factory = this.getFIRFactory(policy_all1);
	}
	
	/**
	 * 
	 * @param bonusVectors
	 * @return Outer list is rows (vectors), inner list is columns (opponents).
	 */
	protected List<List<double[]>> runQAgainstAllsAndSelf(List<double[]> bonusVectors) {
		List<List<double[]>> winnings = new ArrayList<List<double[]>>();
		
		// Run Q vs. All0
		for (int vectorNum = 0; vectorNum < bonusVectors.size(); vectorNum++) {
			List<double[]> winnings_i = new ArrayList<double[]>();
			winnings_i.add(runTourn_QvsFH(bonusVectors.get(vectorNum),all0Factory));
			winnings_i.add(runTourn_QvsFH(bonusVectors.get(vectorNum),all1Factory));
			winnings_i.add(runTourn_QvsQ(bonusVectors.get(vectorNum),bonusVectors.get(vectorNum))[0]);
			winnings.add(winnings_i);
		}

		return winnings;
	}
	
	/**
	 * 
	 * @param bonusVectors
	 * @return Outer list is rows (agent0 in each game), inner list is columns (the opponent of agent0 in each game), double[] is all the match results
	 */
	protected List<List<double[]>> runQAgainstOtherQs(List<double[]> bonusVectors) {
		List<List<double[]>> winnings = new ArrayList<List<double[]>>();
		
		double[] na = new double[numMatchesPerTourn];
		for (int i = 0; i < numMatchesPerTourn; i++) na[i] = -999;
		
		double[][][] storage = new double[numBonusVectors][numBonusVectors][numMatchesPerTourn];
		
		for (int row = 0; row < bonusVectors.size(); row++) {
			// Start at i+1, because we already have all Qs versus themselves from first step
			for (int col = 0; col < bonusVectors.size(); col++) {
				if (row<col) {
					double[][] results = runTourn_QvsQ(bonusVectors.get(row),bonusVectors.get(col));
					double[][] results_tp = transpose(results);
					storage[row][col] = results_tp[0];
					storage[col][row] = results_tp[1];
				}
				else if (row==col) storage[row][col] = na;
			}
		}
		
		for (int row = 0; row < bonusVectors.size(); row++) {
			List<double[]> winnings_row = new ArrayList<double[]>();
			for (int col = 0; col < bonusVectors.size(); col++) {
				winnings_row.add(storage[row][col]);
			}
			winnings.add(winnings_row);
		}
		return winnings;
	}
	
	protected double[] runTourn_QvsFH(double[] bonusVector, FixedIRFactory fixedFactory) {

		double[] winnings = new double[numMatchesPerTourn];
		for (int match = 0; match < numMatchesPerTourn; match++) {
			winnings[match] = runMatch_QvsFH(bonusVector,fixedFactory);
		}
		
		DescriptiveStatistics stats = new DescriptiveStatistics(winnings);
		System.out.println("On average, Agent scored: " + stats.getMean() + " with std dev " + stats.getStandardDeviation());
		
		return winnings;
	}
	
	protected double[][] runTourn_QvsQ(double[] bonusVector0, double[] bonusVector1) {
		double[][] winnings = new double[numMatchesPerTourn][2];

		if (!runParallel) {
			for (int match = 0; match < numMatchesPerTourn; match++) {
				//System.out.println("Elapsed time (beginning match " + match + "): " + (System.nanoTime() - startTime));
				winnings[match] = runMatch_QvsQ(bonusVector0,bonusVector1);
				//System.out.println("Elapsed time (ending match " + match + "): " + (System.nanoTime() - startTime));
			}
			
			double[][] winnings_tp = transpose(winnings);
			DescriptiveStatistics stats0 = new DescriptiveStatistics(winnings_tp[0]);
			DescriptiveStatistics stats1 = new DescriptiveStatistics(winnings_tp[1]);
			System.out.println("On average, Agent 0 scored: " + stats0.getMean() + " with std dev " + stats0.getStandardDeviation());
			System.out.println("On average, Agent 1 scored: " + stats1.getMean() + " with std dev " + stats1.getStandardDeviation());
			
			return winnings;
		} else {
			RealLoopBody loop = new RealLoopBody(this,bonusVector0,bonusVector1);
			Parallel.withIndex(0, numMatchesPerTourn, loop);
			/*double[][] winnings_tp = transpose(loop.winnings);
			DescriptiveStatistics stats0 = new DescriptiveStatistics(winnings_tp[0]);
			DescriptiveStatistics stats1 = new DescriptiveStatistics(winnings_tp[1]);
			System.out.println("On average, Agent 0 scored: " + stats0.getMean() + " with std dev " + stats0.getStandardDeviation());
			System.out.println("On average, Agent 1 scored: " + stats1.getMean() + " with std dev " + stats1.getStandardDeviation());*/
			return loop.winnings;
		}
	}
	
	protected double runMatch_QvsFH(double[] bonusVector, FixedIRFactory fixedFactory) {
		double winnings = 0;
		
		// Create the subjective RF construct
		IRSubjectiveRF subjRF = new IRSubjectiveRF(game.getJointRewardFunction());
		subjRF.setParameters(bonusVector);
		
		// FIRST HALF: Q IS INITIATOR
		
		// Generate our agents
		SGQLAgent qagent = (SGQLAgent) qFactory.generateAgent();
		qagent.setStrategy(new EpsilonGreedy(qagent,this.epsilon));
		qagent.setInternalRewardFunction(subjRF);
		
		Agent fagent = fixedFactory.generateAgent();

		World w = game.createRepeatedGameWorld(domain, qagent, fagent);
		w.setDebugId(debugId);

		//have our world run for 1000 time steps
		w.runGame(numRoundsPerMatch);
		winnings += w.getCumulativeRewardForAgent(qagent.getAgentName());
		
		// SECOND HALF: Q IS RESPONDER
		
		// Generate new agents
		fagent = fixedFactory.generateAgent();
		
		qagent = (SGQLAgent) qFactory.generateAgent();
		qagent.setStrategy(new EpsilonGreedy(qagent,this.epsilon));
		qagent.setInternalRewardFunction(subjRF);

		World w2 = game.createRepeatedGameWorld(domain, fagent, qagent);
		w2.setDebugId(debugId);

		//have our world run for 1000 time steps
		w2.runGame(numRoundsPerMatch);
		winnings += w2.getCumulativeRewardForAgent(qagent.getAgentName());
		
		//print final performance (as cumulative reward)
		//System.out.println("Agent 0 scored: " + w.getCumulativeRewardForAgent(a0.getAgentName()));
		//System.out.println("Agent 1 scored: " + w.getCumulativeRewardForAgent(a1.getAgentName()));
		
		//List<QValue> Qs = a1.getQs(w.getCurrentWorldState());
		//for (QValue q : Qs) System.out.println(q.q);
		
		return winnings;
	}
	
	public double[] runMatch_QvsQ(double[] bonusVector0, double[] bonusVector1) {
		double[] winnings = {0.,0.};
		
		// Create the subjective RF construct
		IRSubjectiveRF subjRF0 = new IRSubjectiveRF(game.getJointRewardFunction());
		subjRF0.setParameters(bonusVector0);
		IRSubjectiveRF subjRF1 = new IRSubjectiveRF(game.getJointRewardFunction());
		subjRF1.setParameters(bonusVector1);
		
		// FIRST HALF
		
		// Generate our agents
		SGQLAgent a0 = (SGQLAgent) qFactory.generateAgent();
		a0.setStrategy(new EpsilonGreedy(a0,this.epsilon));
		a0.setInternalRewardFunction(subjRF0);
		
		SGQLAgent a1 = (SGQLAgent) qFactory.generateAgent();
		a1.setStrategy(new EpsilonGreedy(a1,this.epsilon));
		a1.setInternalRewardFunction(subjRF1);

		World w = game.createRepeatedGameWorld(domain, a0, a1);
		w.setDebugId(debugId);

		//have our world run for 1000 time steps
		w.runGame(numRoundsPerMatch);
		winnings[0] += w.getCumulativeRewardForAgent(a0.getAgentName());
		winnings[1] += w.getCumulativeRewardForAgent(a1.getAgentName());
		
		// SECOND HALF

		// Generate our agents
		a0 = (SGQLAgent) qFactory.generateAgent();
		a0.setStrategy(new EpsilonGreedy(a0,this.epsilon));
		a0.setInternalRewardFunction(subjRF0);

		a1 = (SGQLAgent) qFactory.generateAgent();
		a1.setStrategy(new EpsilonGreedy(a1,this.epsilon));
		a1.setInternalRewardFunction(subjRF1);

		World w2 = game.createRepeatedGameWorld(domain, a1, a0);
		w2.setDebugId(debugId);

		//have our world run for 1000 time steps
		w2.runGame(numRoundsPerMatch);
		winnings[0] += w2.getCumulativeRewardForAgent(a0.getAgentName());
		winnings[1] += w2.getCumulativeRewardForAgent(a1.getAgentName());

		
		//List<JointAction> actions = gameAnalysis.getJointActions();
		/*for (int i = 0; i < actions.size(); i++) {
			System.out.println(gameAnalysis.getState(i).toString());
			System.out.println(gameAnalysis.getJointAction(i).toString());
		}*/
		//for (JointAction action : actions) {
		//	System.out.println(action.toString());
		//}
		
		//print final performance (as cumulative reward)
		//System.out.println("Agent 0 scored: " + w.getCumulativeRewardForAgent(a0.getAgentName()));
		//System.out.println("Agent 1 scored: " + w.getCumulativeRewardForAgent(a1.getAgentName()));
		
		//List<QValue> Qs = a1.getQs(w.getCurrentWorldState());
		//for (QValue q : Qs) System.out.println(q.q);
		
		return winnings;
	}
	
	protected FixedIRFactory getFIRFactory(int[] policy) {
		return new FixedIRFactory(domain,domain.getSingleAction(IRGame.ACTIONINITIATE),domain.getSingleAction(IRGame.ACTIONRESPOND),domain.getSingleAction(IRGame.ACTIONDONOTHING),policy);
	}
	
	protected void writeResults(String path, List<List<double[]>> winnings, String[] winningTitles) {
		// Print/write results
		//DecimalFormat dec = new DecimalFormat("#.##");
		BufferedWriter out = null;
		File file = null;
		
		try {
			file = new File(path);
			// Don't want to override..
			if (!file.exists()) {
				out = new BufferedWriter(new FileWriter(path));
				System.out.println("Writing results to file..");
				
				// Header
				String toWrite = "vectorNum";
				for (String title : winningTitles) toWrite += "," + title;
				out.write(toWrite + "\n");
	
				for (int row = 0; row < winnings.size(); row++) {
					String toPrint = new String(String.valueOf(row));
					for (int col = 0; col < winnings.get(0).size(); col++) {
						DescriptiveStatistics stats = new DescriptiveStatistics(winnings.get(row).get(col));
						double avg = stats.getMean(); // only recording mean right now
						toPrint += "," + String.valueOf(avg);
					}
	
					//System.out.println(toPrint);
					out.write(toPrint);
					out.write("\n");
				}
	
				System.out.println("Finished");
				out.close();
			} else {
				System.out.println("Output file already exists, did not write results");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Writes the result of a single tournament.  This is useful for grid stuff.
	 * @param path
	 * @param winnings
	 * @param winningTitle
	 */
	protected void writeResults(String path, double[] winnings, String winningTitle) {
		// Print/write results
		//DecimalFormat dec = new DecimalFormat("#.##");
		BufferedWriter out = null;
		File file = null;
		
		try {
			file = new File(path);
			// Don't want to override..
			if (!file.exists()) {
				out = new BufferedWriter(new FileWriter(path));
				System.out.println("Writing results of " + winningTitle + " to file..");
				
				// Header
				String toWrite = "Mean,Std\n";
				out.write(toWrite);
	
				DescriptiveStatistics stats = new DescriptiveStatistics(winnings);
				String avg = String.valueOf(stats.getMean()); // only recording mean right now
				String std = String.valueOf(stats.getStandardDeviation());
				out.write(avg + "," + std);
				System.out.println(avg);
	
				System.out.println("Finished");
				out.close();
			} else {
				System.out.println("Output file already exists, did not write results of " + winningTitle + "");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	protected static double[][] transpose(double[][] input) {
		int size1 = input.length;
		int size2 = input[0].length;
		double[][] output = new double[size2][size1];
		for (int i = 0; i < size1; i++) {
			for (int j = 0; j < size2; j++) {
				output[j][i] = input[i][j];
			}
		}
		return output;
	}

	@Override
	public int getNumMatchesPerTourn() {
		return this.numMatchesPerTourn;
	}
}
