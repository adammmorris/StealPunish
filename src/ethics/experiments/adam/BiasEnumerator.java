package ethics.experiments.adam;

import java.util.ArrayList;
import java.util.List;

import optimization.OptVariables;

public class BiasEnumerator {

	public List<OptVariables>		allRFs;
	public double[]					lower;
	public double[]					upper;
	public double[] 				inc;
	
	
	public static void main(String [] args){
		//RFParamVarEnumerator rfenum = new RFParamVarEnumerator();
		BiasEnumerator rfenum = new BiasEnumerator(new double[]{0,-10}, new double[]{10,10}, new double[]{10,10});
		for(OptVariables v : rfenum.allRFs){
			System.out.println(v);
		}
		System.out.println(rfenum.allRFs.size());
	}
	
	public BiasEnumerator(double[] lower, double[] upper, double[] inc){
		if (lower.length != upper.length || lower.length != inc.length) {
			throw new Error("lower, upper, and inc must have the same length (i.e the # of params)!");
		}
		this.lower = lower;
		this.upper = upper;
		this.inc = inc;
		this.generate(lower.length);
	}
	
	public BiasEnumerator(double lower, double upper, double inc, int nParams){
		this.lower = new double[nParams];
		this.upper = new double[nParams];
		this.inc = new double[nParams];
		for (int i = 0; i < nParams; i++) {
			this.lower[i] = lower;
			this.upper[i] = upper;
			this.inc[i] = inc;
		}
		this.generate(nParams);
	}
	
	public void generate(int nParam){
		
		int[] n = new int[nParam];
		for (int j = 0; j < nParam; j++) {
			n[j] = (int)((upper[j]-lower[j])/inc[j]) + 1;
		}
		allRFs = new ArrayList<OptVariables>();
		double [] params = new double[nParam];
		this.recursivelyGenerateParams(params, 0, n);
		
	}
	
	protected void recursivelyGenerateParams(double [] params, int ind, int[] n){
		for(int i = 0; i < n[ind]; i++){ // # of possible values for each param
			params[ind] = lower[ind] + inc[ind]*i;
			if(ind == params.length-1){
				OptVariables vars = new OptVariables(params.clone());
				this.allRFs.add(vars);
			}
			else{
				recursivelyGenerateParams(params, ind+1, n);
			}
		}
	}
	
}