package ethics.experiments.adam.moran;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import optimization.OptVariables;
import burlap.behavior.learningrate.ExponentialDecayLR;
import burlap.behavior.singleagent.ValueFunctionInitialization;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQLAgent;
import burlap.datastructures.BoltzmannDistribution;
import burlap.debugtools.DPrint;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.stochasticgames.AgentFactory;
import burlap.oomdp.stochasticgames.AgentType;
import burlap.oomdp.stochasticgames.JointActionModel;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import burlap.oomdp.stochasticgames.common.AgentFactoryWithSubjectiveReward;
import ethics.experiments.adam.BiasEnumerator;
import ethics.experiments.adam.game.SP_Domain;
import ethics.experiments.adam.game.SP_JR;
import ethics.experiments.adam.game.SP_PseudoTerm;
import ethics.experiments.adam.game.SP_StateGenerator;
import ethics.experiments.adam.game.SP_StateSpace;
import ethics.experiments.adam.game.SP_SubjectiveRF;
import ethics.experiments.adam.game.SP_SubjectiveRF.SP_SubjectiveRFFactory;
import ethics.experiments.adam.game.symfm.SymFM;
import ethics.experiments.fssimple.auxiliary.ConsantPsudoTermWorldGenerator;
import ethics.experiments.fssimple.auxiliary.PseudoGameCountWorld;

public class MoranProcess_Dynamic {
	protected double								baseLearningRate;
	
	protected List<OptVariables>					rfParamSet;
	protected ConsantPsudoTermWorldGenerator		worldGenerator;
	protected JointReward							objectiveReward;
	protected SP_SubjectiveRFFactory				rewardFactory;

	protected AgentFactory							baseFactory;
	
	protected AgentType								fsAgentType;
	
	protected int									nGames;
	protected int 									numVectors;
	
	protected SGDomain 								domain;
	private SP_StateSpace							statespace;
	private ValueFunctionInitialization				qInit;
	
	private static final boolean					decayLR = false;
	
	public static void main(String[] args) {
		// Args: outputPath
		
		/*int nAgents = Integer.parseInt(args[0]);
		int nGAGen = Integer.parseInt(args[1]);
		double temperature = Double.parseDouble(args[2]);
		double learningRate = Double.parseDouble(args[3]);
		String outputPath = args[4];
		double mutation = Double.parseDouble(args[5]);
		
		int nParams = Integer.parseInt(args[6]);
		double[][] paramset = new double[3][nParams];
		int ind = 7;
		for (int i = 0; i < nParams; i++) {
			paramset[0][i] = Double.parseDouble(args[ind]); ind++;
			paramset[1][i] = Double.parseDouble(args[ind]); ind++;
			paramset[2][i] = Double.parseDouble(args[ind]); ind++;
		}*/
		
		String outputPath = args[0];
		
		int nAgents = 50;
		int nGAGen = 100000;
		double temperature = 20;
		double mutation = .01;
		double learningRate = .1;
		double probBackTurned = 0;
		double[] rewards = {1,-1,-.5,-2.5};
		int nParams = 2;
		
		// Steal bias: lo hi inc
		// Punish bias: lo hi inc
		double[][] paramset = {{0,0},{2,1},{2,1}};
		
		MoranProcess_Dynamic process = new MoranProcess_Dynamic(learningRate, probBackTurned, rewards,paramset,nParams);
		
		
		process.runSimulation(nAgents,nGAGen,temperature,outputPath,mutation,paramset,nParams);
	}
	
	public MoranProcess_Dynamic(double learningRate, double probBackTurned, double[] rewards, double[][] paramset, int nParams) {
		DPrint.toggleCode(284673923, false); //world printing debug code
		DPrint.toggleCode(25633, false); //tournament printing debug code
		
		this.nGames = 1000;

		double discount = 0.95;
		
		int memory = 2;
		this.statespace = new SymFM(memory, memory, probBackTurned);
		//this.statespace = new Pavlov();
		
		this.qInit = new ValueFunctionInitialization.ConstantValueFunctionInitialization(0.);
		//this.qInit = statespace.getCoopEquilibriumQInit(rewards, discount);
		
		
		/* PARAMETERS NOT TO TOUCH */
		
		this.baseLearningRate = learningRate;
		this.rfParamSet = (new BiasEnumerator(paramset[0],paramset[1],paramset[2])).allRFs;
		this.numVectors = this.rfParamSet.size();
		this.objectiveReward = new SP_JR(rewards[0],rewards[1],rewards[2],rewards[3]);
		this.rewardFactory = new SP_SubjectiveRF.SP_SubjectiveRFFactory(new SP_JR(rewards[0],rewards[1],rewards[2],rewards[3]));
				
		SP_Domain dgen = new SP_Domain(statespace.getStateNames());
		this.domain = (SGDomain)dgen.generateDomain();
		JointActionModel jam = statespace.getJAM();
		
		DiscreteStateHashFactory hashingFactory = new DiscreteStateHashFactory();
		this.baseFactory = new SGQFactory(domain, discount, learningRate, 0, hashingFactory);
		SGStateGenerator sg = new SP_StateGenerator(domain, probBackTurned, this.statespace.getFirstState());
		
		this.worldGenerator = new ConsantPsudoTermWorldGenerator(domain, jam, objectiveReward, new NullTermination(), sg, new SP_PseudoTerm());
		this.fsAgentType = new AgentType("player", domain.getObjectClass(SP_Domain.CLASSPLAYER), domain.getSingleActions());
	}
	
	public void runSimulation(int nAgents, int nGenerations, double temperature, String outputPath, double mutation, double[][] paramset, int nParams) {
		BiasEnumerator rfenum = new BiasEnumerator(paramset[0],paramset[1],paramset[2]);
		int numGenomes = rfenum.allRFs.size();
		double[][] payoffs = new double[numGenomes][numGenomes];
				
		Random random = new Random();
		
		// Randomly initialize population
		int[] population = new int[nAgents];
		for (int i = 0; i < population.length; i++) {
			population[i] = random.nextInt(numGenomes);
			//population[i] = 1;
		}
		
		double[] totalDist = new double[numGenomes];
		double[] curDist = new double[numGenomes];
		
		// Get initial dist
		for (int i = 0; i < nAgents; i++) {
			for (int j = 0; j < numGenomes; j++) {
				if (population[i] == j) {
					curDist[j] = curDist[j]+1;
				}
			}
		}
		
		// Output: nGenerations x (current normalized dist for all genomes)
		double[][] output = new double[nGenerations][numGenomes];
		
		// Run generations
		for (int thisGen = 0; thisGen < nGenerations; thisGen++) {
			// Calculate relative fitness of each player
			double[] fitArray = new double[nAgents];
			
			payoffs = this.getPayoffs_dynamic(numGenomes);
			
			for (int i = 0; i < nAgents; i++) {
				for (int genome = 0; genome < numGenomes; genome++) {
					if (population[i]==genome) fitArray[i] += payoffs[population[i]][genome]*(curDist[genome]-1);
					else fitArray[i] += payoffs[population[i]][genome]*curDist[genome];
				}
				fitArray[i] = fitArray[i] / (nAgents);
			}
			
			BoltzmannDistribution bd = new BoltzmannDistribution(fitArray, temperature);
			double [] fitProb = bd.getProbabilities();
			
			// Pick player to reproduce
			int agentReproduce = 0;
			double rand = random.nextDouble();
			double cur = 0;
			for (int i = 0; i < nAgents; i++) {
				cur += fitProb[i];
				if (rand < cur) {
					agentReproduce = i;
					break;
				}
			}
			
			int agentDie = random.nextInt(nAgents);
			
			// Mutation?
			if (random.nextDouble() < mutation) {
				// Pick new random strategy, update population
				population[agentDie] = random.nextInt(numGenomes);
			} else {
				// Copy
				population[agentDie] = population[agentReproduce];
			}
			
			// Update distribution
			for (int i = 0; i < numGenomes; i++) {
				curDist[i] = 0;
				
				for (int j = 0; j < nAgents; j++) {
					if (population[j] == i) {
						totalDist[i]++;
						curDist[i]++;
					}
				}
				
				output[thisGen][i] = curDist[i]/nAgents;
			}
			
			/*if (thisGen % 1 == 0) {
				// Output
				
				for (int i = 0; i < numGenomes; i++) {
					System.out.println(rfenum.allRFs.get(i).toString() + ": " + (curDist[i] / nAgents));
				}
				System.out.println();
			}*/
			
		}
		
		// Normalize distribution
		int sum = 0;
		for (int i = 0; i < numGenomes; i++) {
			sum += totalDist[i];
		}
		for (int i = 0; i < numGenomes; i++) {
			totalDist[i] = totalDist[i] / sum;
		}
		
		// Output
		/*System.out.println("Final:");
		for (int i = 0; i < numGenomes; i++) {
			System.out.println();
			System.out.println(rfenum.allRFs.get(i).toString() + ": " + totalDist[i]);
		}*/
		
		try {
			FileWriter writer = new FileWriter(outputPath);
			
			for (int gen = 0; gen < nGenerations; gen++) {
				writer.append(output[gen][0]+"");
				for (int i = 1; i < numGenomes; i++) {
					writer.append(","+output[gen][i]);
				}
				writer.append('\n');
			}

			writer.flush();
			writer.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private double[][] getPayoffs_dynamic(int nGenomes) {
		double[][] payoffs = new double[nGenomes][nGenomes];
		
		for (int i = 0; i < nGenomes; i++) {
			for (int j = i; j < nGenomes; j++) {
				double[] result = this.runMatch(this.rfParamSet.get(i),this.rfParamSet.get(j));
				payoffs[i][j] = result[0];
				payoffs[j][i] = result[1];
			}
		}
		
		return payoffs;
	}
	
	protected double[] runMatch(OptVariables v1, OptVariables v2){
		JointReward subjectiveRewardV1 = this.rewardFactory.generateRF(this.statespace.generateParamMap(v1.vars));
		AgentFactory factV1 = new AgentFactoryWithSubjectiveReward(baseFactory, subjectiveRewardV1);
		
		JointReward subjectiveRewardV2 = this.rewardFactory.generateRF(this.statespace.generateParamMap(v2.vars));
		AgentFactory factV2 = new AgentFactoryWithSubjectiveReward(baseFactory, subjectiveRewardV2);
		
		//role 1
		
		SGQLAgent a1 = (SGQLAgent)factV1.generateAgent();
		a1.setQValueInitializer(this.qInit);
		//if (decayLR) a1.setLearningRate(new ExponentialDecayLR(this.baseLearningRate, 0.999, 0.01));
		
		SGQLAgent a2 = (SGQLAgent)factV2.generateAgent();
		a2.setQValueInitializer(this.qInit);
		//if (decayLR) a2.setLearningRate(new ExponentialDecayLR(this.baseLearningRate, 0.999, 0.01));
		
		PseudoGameCountWorld w1 = (PseudoGameCountWorld)this.worldGenerator.generateWorld();
		a1.joinWorld(w1, this.fsAgentType);
		a2.joinWorld(w1, this.fsAgentType);
		
		w1.runGame(Integer.MAX_VALUE, nGames);
		
		double a1r1 = w1.getCumulativeRewardForAgent(a1.getAgentName());
		double a2r1 = w1.getCumulativeRewardForAgent(a2.getAgentName());
		
		//role 2
		
		SGQLAgent a12 = (SGQLAgent)factV1.generateAgent();
		a12.setQValueInitializer(this.qInit);
		//if (decayLR) a12.setLearningRate(new ExponentialDecayLR(this.baseLearningRate, 0.999, 0.01));
		
		SGQLAgent a22 = (SGQLAgent)factV2.generateAgent();
		a22.setQValueInitializer(this.qInit);
		//if (decayLR) a22.setLearningRate(new ExponentialDecayLR(this.baseLearningRate, 0.999, 0.01));
		
		PseudoGameCountWorld w2 = (PseudoGameCountWorld)this.worldGenerator.generateWorld();
		a22.joinWorld(w2, this.fsAgentType); //switch join order
		a12.joinWorld(w2, this.fsAgentType);
		
		w2.runGame(Integer.MAX_VALUE, nGames);
		
		double a1r2 = w2.getCumulativeRewardForAgent(a12.getAgentName());
		double a2r2 = w2.getCumulativeRewardForAgent(a22.getAgentName());
		
		double a1r = a1r1 + a1r2;
		double a2r = a2r1 + a2r2;
		
		double[] res = {a1r,a2r};
		
		return res;
	}
}
