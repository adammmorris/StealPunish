package ethics.experiments.adam;

import java.util.ArrayList;
import java.util.List;

import optimization.OVarStringRep;
import optimization.OptVariables;
import optimization.infinitega.InfiniteGA;
import optimization.infinitega.modules.InfGASoftMaxReproduce;
import optimization.infinitega.modules.RatioKillWorst;
import burlap.debugtools.DPrint;
import ethics.experiments.tbforagesteal.auxiliary.RFParamVarEnumerator;
import ethics.experiments.tbforagesteal.evaluators.InfGACachedVarEval;

public class Optimizer {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		optimiztionInfiniteGASoftMax_mutation(args);
	}
	
	// Arguments should be:
	// nGAGenerations temperature cacheFileInput outputPath mutationRate 
	// nParams param1-min param1-max param1-step param2-min param2-max param2-step ...
	public static void optimiztionInfiniteGASoftMax_mutation(String [] args){
		DPrint.toggleCode(InfGASoftMaxReproduce.debugCode, false);
		
		int nGAGen = Integer.parseInt(args[0]);
		double temperature = Double.parseDouble(args[1]);
		String cacheFileInput = args[2];
		String outputPath = args[3];
		double mutation = Double.parseDouble(args[4]);
		
		int nParams = Integer.parseInt(args[5]);
		double[][] paramset = new double[3][nParams];
		int ind = 6;
		for (int i = 0; i < nParams; i++) {
			paramset[0][i] = Double.parseDouble(args[ind]); ind++;
			paramset[1][i] = Double.parseDouble(args[ind]); ind++;
			paramset[2][i] = Double.parseDouble(args[ind]); ind++;
		}
		
		runInfinteGASoftMax_mutation(nGAGen, temperature, cacheFileInput, outputPath,mutation, paramset, nParams);
		
		
	}
	
	public static void runInfinteGASoftMax_mutation(int nGenerations, double temperature, String cacheFilePath, String outputPath, double mutation, double[][] paramset, int nParams){
		
		/* SET THIS TO RIGHT PARAMS FOR GAME */
		
		BiasEnumerator rfenum = new BiasEnumerator(paramset[0],paramset[1],paramset[2]);
		
		/* DON'T TOUCH BELOW THIS */
		
		System.out.println("Parsing CacheFile");
		InfGACachedVarEval eval = new InfGACachedVarEval(cacheFilePath);
		System.out.println("Finished Parsing CacheFile and starting GA");
				
		InfiniteGA ga = new InfiniteGA(eval, new InfGASoftMaxReproduce(temperature,mutation), new RatioKillWorst(), rfenum.allRFs, nGenerations);
		eval.setInfGA(ga);
		
		OVarStringRep rep = new OVarStringRep() {
			
			@Override
			public String getStringRep(OptVariables vars) {
				return vars.toString();
			}
		};
		ga.enableOptimzationFileRecording(2, rep, outputPath);

		ga.optimize();
		
		System.out.println("Finished\n-----------------------------");
		
	}
	
	static class VarRatio{
		
		OptVariables v;
		double r;
		
		public VarRatio(OptVariables v, double r){
			this.v = v;
			this.r = r;
		}
		
		public static List<OptVariables> getPopulation(List <VarRatio> ratios, int n){
			
			List <OptVariables> pop = new ArrayList<OptVariables>(n);
			for(VarRatio r : ratios){
				int m = (int)(n*r.r);
				//System.out.println(m);
				for(int i = 0; i < m; i++){
					pop.add(new OptVariables(r.v));
				}
			}
			
			//System.out.println(pop.size());
			
			return pop;
			
		}
		
	}
	
}