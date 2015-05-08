package optimization.infinitega.modules;

import java.util.ArrayList;
import java.util.List;

import optimization.infinitega.GenomeRatio;
import optimization.infinitega.GenomeRatioFitness;
import optimization.infinitega.RatioReproduce;
import optimization.infinitega.RepResult;
import burlap.datastructures.BoltzmannDistribution;
import burlap.debugtools.DPrint;

public class InfGASoftMaxReproduce implements RatioReproduce {

	public static final	int		debugCode = 94723;
	
	protected boolean			normalize = false;
	protected double			temperature = 1.;
	protected boolean			usePopulationRatio = true;
	protected double			mutation = 0.;
	
	public InfGASoftMaxReproduce(double temp){
		this.temperature = temp;
	}
	
	public InfGASoftMaxReproduce(double temp,double mutation){
		this.temperature = temp;
		this.mutation = mutation;
	}
	
	public InfGASoftMaxReproduce(double temp, boolean normalize){
		this.temperature = temp;
		this.normalize = normalize;
	}
	
	public InfGASoftMaxReproduce(double temp, boolean normalize, double mutation){
		this.temperature = temp;
		this.normalize = normalize;
		this.mutation = mutation;
	}
	
	/*@Override
	public RepResult ratioReproduce(List<GenomeRatioFitness> popDist) {
		
		double [] fitArray = this.getFitnessArray(popDist);
		BoltzmannDistribution bd = new BoltzmannDistribution(fitArray, this.temperature);
		double [] fitProb = bd.getProbabilities();
		
		RepResult res = new RepResult(0., new ArrayList<GenomeRatioFitness>(popDist));
		double sumPopIncrease = 0.;
		for(int i = 0; i < fitProb.length; i++){
			//double children = fitProb[i] * popDist.get(i).gr.ratio;
			double children = fitProb[i];
			if(this.usePopulationRatio){
				children *= popDist.get(i).gr.ratio;
			}
			sumPopIncrease += children;
			GenomeRatio gr = res.nextPop.get(i).gr;
			gr.ratio += children;
			
			DPrint.cf(debugCode, "%.3f %.2f\n", fitProb[i], res.nextPop.get(i).fitness);
		}
		
		res.repChange = sumPopIncrease;
		
		//renormalize
		double newSum = 0.;
		for(int i = 0; i < popDist.size(); i++){
			res.nextPop.get(i).gr.ratio /= (1. + sumPopIncrease);
			newSum += res.nextPop.get(i).gr.ratio;
		}
		
		if(Math.abs(1. - newSum) > 0.000001){
			throw new RuntimeException("New population doesn't sum to 1...");
		}
		
		return res;
	}*/
	
	@Override
	public RepResult ratioReproduce(List<GenomeRatioFitness> popDist) {
		double [] fitArray = this.getFitnessArray(popDist);
		BoltzmannDistribution bd = new BoltzmannDistribution(fitArray, this.temperature);
		double [] fitProb = bd.getProbabilities();

		RepResult res = new RepResult(0., new ArrayList<GenomeRatioFitness>(popDist));
		double sumPopIncrease = 0.;

		double [] gainedFromMutation = new double[fitProb.length];
		for(int i = 0; i < fitProb.length; i++){

			double chanceReproduce = fitProb[i]*popDist.get(i).gr.ratio;
			double toOthers = chanceReproduce * mutation / (double)(fitProb.length - 1);

			for(int j = 0; j < gainedFromMutation.length; j++){
				if(j == i){
					continue;
				}
				else{
					gainedFromMutation[j] += toOthers;
				}
			}

		}



		for(int i = 0; i < fitProb.length; i++){
			//double children = fitProb[i] * popDist.get(i).gr.ratio;
			double children = fitProb[i];
			if(this.usePopulationRatio){
				children *= popDist.get(i).gr.ratio*(1.-mutation);
			}
			sumPopIncrease += children + gainedFromMutation[i];
			GenomeRatio gr = res.nextPop.get(i).gr;
			gr.ratio += children + gainedFromMutation[i];

			DPrint.cf(debugCode, "%.3f %.2f\n", fitProb[i], res.nextPop.get(i).fitness);
		}

		res.repChange = sumPopIncrease;

		//renormalize
		double newSum = 0.;
		for(int i = 0; i < popDist.size(); i++){
			res.nextPop.get(i).gr.ratio /= (1. + sumPopIncrease);
			newSum += res.nextPop.get(i).gr.ratio;
		}

		if(Math.abs(1. - newSum) > 0.000001){
			throw new RuntimeException("New population doesn't sum to 1...");
		}

		return res;
	}
	
	
	protected double[] getFitnessArray(List<GenomeRatioFitness> pop){
		
		double [] res = new double[pop.size()];
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
		for(int i = 0; i < pop.size(); i++){
			double f = pop.get(i).fitness;
			res[i] = f;
			max = Math.max(max, f);
			min = Math.min(min, f);
		}
		
		double range = max - min;
		
		if(this.normalize){
			for(int i = 0; i < res.length; i++){
				res[i] = (res[i] - min) / range;
			}
		}
		
		return res;
		
	}

}
