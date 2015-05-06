package ethics.experiments.adam.moran;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import optimization.OptVariables;
import burlap.behavior.singleagent.ValueFunctionInitialization;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQFactory;
import burlap.datastructures.BoltzmannDistribution;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.stochasticgames.AgentType;
import burlap.oomdp.stochasticgames.JointActionModel;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import ethics.experiments.adam.BiasEnumerator;
import ethics.experiments.adam.game.SP_Domain;
import ethics.experiments.adam.game.SP_JR;
import ethics.experiments.adam.game.SP_PseudoTerm;
import ethics.experiments.adam.game.SP_StateGenerator;
import ethics.experiments.adam.game.SP_SubjectiveRF;
import ethics.experiments.adam.game.symfm.SymFM;
import ethics.experiments.fssimple.auxiliary.ConsantPsudoTermWorldGenerator;
import ethics.experiments.tbforagesteal.evaluators.FullyCachedMatchEvaluation;

public class MoranProcess {
		
	public static void main(String[] args) {
		String cacheFileInput = args[0];
		String outputPath = args[1];
		
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
		
		//MoranProcess_Dynamic process = new MoranProcess_Dynamic(learningRate, probBackTurned, rewards,paramset,nParams);
		
		runSimulation(nAgents,nGAGen,temperature,cacheFileInput,outputPath,mutation,paramset,nParams);
	}
	
	public static void runSimulation(int nAgents, int nGenerations, double temperature, String cacheFilePath, String outputPath, double mutation, double[][] paramset, int nParams) {
		BiasEnumerator rfenum = new BiasEnumerator(paramset[0],paramset[1],paramset[2]);
		FullyCachedMatchEvaluation eval = new FullyCachedMatchEvaluation(cacheFilePath,false);
		int numGenomes = rfenum.allRFs.size();
		
		double[][] payoffs = new double[numGenomes][numGenomes];
		payoffs = getPayoffs_static(rfenum, eval);
		
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
		
		// Run generations
		for (int thisGen = 0; thisGen < nGenerations; thisGen++) {
			// Calculate relative fitness of each player
			double[] fitArray = new double[nAgents];
						
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
			}
			
			if (thisGen % 10000 == 0) {
			//if (true) {
				int sum = 0;
				for (int i = 0; i < numGenomes; i++) {
					sum += curDist[i];
				}
				
				// Output
				for (int i = 0; i < numGenomes; i++) {
					System.out.println(rfenum.allRFs.get(i).toString() + ": " + (curDist[i] / sum));
				}
				
				System.out.println();
			}
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
		System.out.println("Final:");
		for (int i = 0; i < numGenomes; i++) {
			System.out.println();
			System.out.println(rfenum.allRFs.get(i).toString() + ": " + totalDist[i]);
		}
	}
	
	private static double[][] getPayoffs_static(BiasEnumerator rfenum, FullyCachedMatchEvaluation eval) {
		int numGenomes = rfenum.allRFs.size();

		double[][] payoffs = new double[numGenomes][numGenomes];
		for (int i = 0; i < numGenomes; i++) {
			for (int j = i; j < numGenomes; j++) {
				List<OptVariables> temp = new ArrayList<OptVariables>();
				temp.add(rfenum.allRFs.get(i));
				temp.add(rfenum.allRFs.get(j));
				
				List<Double> evalResults = eval.evaluate(temp);
				payoffs[i][j] = evalResults.get(0);
				payoffs[j][i] = evalResults.get(1);
			}
		}
		
		return payoffs;
	}
}
