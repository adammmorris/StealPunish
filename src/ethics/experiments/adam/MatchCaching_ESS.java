package ethics.experiments.adam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import optimization.OptVariables;
import burlap.debugtools.DPrint;

public class MatchCaching_ESS {

	protected double								baseLearningRate;
	
	protected List<OptVariables>					rfParamSet;
	
	protected int									N;
	protected double								Rs;
	protected double								Rs_prime;
	protected double								Rp;
	protected double								Rp_prime;
	protected int									Bs;
	protected int									Bp;
	protected int 									numVectors;
	protected int									nParams;
	protected double[][][]							resultTable;
	
	
	
	
	protected boolean								replaceResultsWithAvgOnly = true;
		
	/**
	 * @param args Should be:
	 * [outputFile]
	 */
	public static void main(String[] args) {
		//long start = System.nanoTime();
		
		DPrint.toggleCode(284673923, false); //world printing debug code
		DPrint.toggleCode(25633, false); //tournament printing debug code
		
		String outputFile = args[0];
		double targetValue = Double.valueOf(args[1]);
		int reversed = Integer.valueOf(args[2]);
		
		MatchCaching_ESS mc = new MatchCaching_ESS(targetValue, reversed);
		
		System.out.println("Beginning");
		
		for (int q = 0; q < 25; q++) {
			mc.cacheRow(outputFile, q);
		}
		mc.compileGridOutput(outputFile, "Cache.txt");

		//System.out.println("Elapsed time: " + (System.nanoTime()-start));
	}
	
	
	public MatchCaching_ESS(double targetValue, int reversed){
		/* PARAMETERS TO SET BEFORE RUNNING */
		
		this.N = 1000;
		this.Rs = 1;
		this.Rs_prime = -1;
		this.Rp = -.5;
		this.Rp_prime = -2.5;
		if (reversed == 0) {
			this.Bs = 10;
			this.Bp = 5;
		} else {
			this.Bs = 5;
			this.Bp = 10;
		}
		
		this.rfParamSet = (new BiasEnumerator(new double[]{0.,0.},
				new double[]{4.,4.},new double[]{1,1})).allRFs;
		this.numVectors = rfParamSet.size();
		this.nParams = 5;
		
		this.resultTable = new double[5][5][2];
		resultTable[0][0][0] = N*(Rs+Rp_prime);
		resultTable[0][0][1] = N*(Rs_prime+Rp);
		resultTable[0][1][0] = N*(Rs+Rp_prime);
		resultTable[0][1][1] = N*(Rs_prime+Rp);
		resultTable[0][2][0] = N*Rs;
		resultTable[0][2][1] = N*Rs_prime;
		resultTable[0][3][0] = N*Rs;
		resultTable[0][3][1] = N*Rs_prime;
		resultTable[0][4][0] = N*Rs+Bp*Rp_prime;
		resultTable[0][4][1] = N*Rs_prime+Bp*Rp;
		
		resultTable[1][0][0] = N*(Rs+Rp_prime);
		resultTable[1][0][1] = N*(Rs_prime+Rp);
		resultTable[1][1][0] = N/2*(Rs+Rp_prime);
		resultTable[1][1][1] = N/2*(Rs_prime+Rp);
		resultTable[1][2][0] = N/2*(Rs+Rp_prime);
		resultTable[1][2][1] = N/2*(Rs_prime+Rp);
		resultTable[1][3][0] = 0;
		resultTable[1][3][1] = 0;
		resultTable[1][4][0] = Bp*(Rs+Rp_prime);
		resultTable[1][4][1] = Bp*(Rs_prime+Rp);
		
		resultTable[2][0][0] = N*Rp_prime;
		resultTable[2][0][1] = N*Rp;
		resultTable[2][1][0] = N/2*(Rs+Rp_prime);
		resultTable[2][1][1] = N/2*(Rs_prime+Rp);
		resultTable[2][2][0] = N/2*(Rs+Rp_prime);
		resultTable[2][2][1] = N/2*(Rs_prime+Rp);
		resultTable[2][3][0] = N*Rs;
		resultTable[2][3][1] = N*Rs_prime;
		
		if (Rs_prime > Rp) { // THESE TWO ARE TRICKY
			resultTable[2][4][0] = N*Rs;
			resultTable[2][4][1] = N*Rs_prime;
		} else {
			resultTable[2][4][0] = N*Rp_prime;
			resultTable[2][4][1] = N*Rp;
		}
		
		resultTable[3][0][0] = N*Rp_prime;
		resultTable[3][0][1] = N*Rp;
		resultTable[3][1][0] = 0;
		resultTable[3][1][1] = 0;
		resultTable[3][2][0] = N*Rp_prime;
		resultTable[3][2][1] = N*Rp;
		resultTable[3][3][0] = 0;
		resultTable[3][3][1] = 0;
		resultTable[3][4][0] = Bp*(Rp_prime); // TRICKY
		resultTable[3][4][1] = Bp*(Rp);

		resultTable[4][0][0] = Bs*(Rs+Rp_prime);
		resultTable[4][0][1] = Bs*(Rs_prime+Rp);
		resultTable[4][1][0] = Bs*(Rs+Rp_prime);
		resultTable[4][1][1] = Bs*(Rs_prime+Rp);
		resultTable[4][2][0] = N*Rs;
		resultTable[4][2][1] = N*Rs_prime;
		resultTable[4][3][0] = N*Rs;
		resultTable[4][3][1] = N*Rs_prime;
		
		if (Bs > Bp) { // theta = 1
			resultTable[4][4][0] = N*Rs+Bp*Rp_prime;
			resultTable[4][4][1] = N*Rs_prime+Bp*Rp;
		} else { // theta = 0
			resultTable[4][4][0] = Bs*(Rs+Rp_prime);
			resultTable[4][4][1] = Bs*(Rs_prime+Rp);
		}
	}
	
	protected void cacheRow(String outputDirectoryPath, int row){
		
		if(!outputDirectoryPath.endsWith("/")){
			outputDirectoryPath = outputDirectoryPath + "/";
		}
		
		String pathName = outputDirectoryPath + row + ".txt";
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(pathName));
			
			System.out.println("beginning row comparisons for " + row);
			OptVariables v1 = this.rfParamSet.get(row);
			for(int j = row; j < this.numVectors; j++){
				System.out.println("comparing against " + j);
				OptVariables v2 = this.rfParamSet.get(j);
				String res = this.getMatchResultString(v1,v2);
				out.write(res);
				out.write("\n");
				
			}
				
			
			
			System.out.println("Finished.");
			
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	protected void compileGridOutput(String outputDirectoryPath, String cacheName){
		if(!outputDirectoryPath.endsWith("/")){
			outputDirectoryPath = outputDirectoryPath + "/";
		}
		
		String cachePath = outputDirectoryPath + cacheName;
		BufferedWriter out = null;
		BufferedReader in = null;
		
		try {
			out = new BufferedWriter(new FileWriter(cachePath));
			
			System.out.println("beginning compilation...");
			for(int i = 0; i < numVectors; i++){
				System.out.println("beginning compilations for row " + i);
				// Read file & copy, line by line, to cache
				String curPath = outputDirectoryPath+i+".txt";
					
				try {
					in = new BufferedReader(new FileReader(curPath));

					String line = in.readLine();
					while (line != null) {
						out.write(line);
						out.newLine();
						line = in.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
				
			
			
			System.out.println("Finished.");
			
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	protected String getMatchResultString(OptVariables v1, OptVariables v2){
		
		MatchResult mr = this.getAverageMatch(v1, v2);
		
		StringBuffer buf = new StringBuffer();
		
		//format:
		//v11,v12,v13;v21,v22,v23::avgV1,stdV1
		//return11,return12,...,return1N
		//v21,v22,v23;v11,v12,v13::avgV2,stdV2
		//return21,return22,...,return2N
		
		buf.append(this.commaDelimString(v1)).append(";").append(this.commaDelimString(v2)).append("::").append(mr.avgA).append(",").append(mr.stdA).append("\n");
		if(!this.replaceResultsWithAvgOnly){
			for(int q = 0; q < mr.results.size(); q++){
				if(q > 0){
					buf.append(",");
				}
				buf.append(mr.results.get(q).a);
			}
		}
		else{
			buf.append(mr.avgA);
		}
		buf.append("\n");
		
		buf.append(this.commaDelimString(v2)).append(";").append(this.commaDelimString(v1)).append("::").append(mr.avgB).append(",").append(mr.stdB).append("\n");
		if(!this.replaceResultsWithAvgOnly){
			for(int q = 0; q < mr.results.size(); q++){
				if(q > 0){
					buf.append(",");
				}
				buf.append(mr.results.get(q).b);
			}
		}
		else{
			buf.append(mr.avgB);
		}
		
		
		return buf.toString();
	}
	
	protected MatchResult getAverageMatch(OptVariables v1, OptVariables v2){
		
		List <DoublePair> results = new ArrayList<DoublePair>(1);
	
		results.add(runMatch(v1,v2));
		
		return new MatchResult(results);
		
	}
	
	
	protected DoublePair runMatch(OptVariables v1, OptVariables v2){
		
		return this.runMatchHardCoded(v1, v2);
		
	}
	
	protected DoublePair runMatchHardCoded(OptVariables v1, OptVariables v2){
		int x1 = (int)v1.v(0);
		int y1 = (int)v1.v(1);
		int x2 = (int)v2.v(0);
		int y2 = (int)v2.v(1);
		return new DoublePair(this.resultTable[x1][y2][0]
				+this.resultTable[x2][y1][1],this.resultTable[x2][y1][0]
				+this.resultTable[x1][y2][1]);
	}
	
	protected int [] doubleToIntArray(double [] vars){
		int [] ia = new int[vars.length];
		for(int i = 0; i < vars.length; i++){
			ia[i] = (int)vars[i];
		}
		return ia;
	}
	
	
	protected String commaDelimString(OptVariables v){
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < v.vars.length; i++){
			if(i > 0){
				buf.append(",");
			}
			buf.append(v.vars[i]);
		}
		return buf.toString();
	}
	
	
	
	class MatchResult{
		
		public List <DoublePair> results;
		
		public double avgA;
		public double avgB;
		
		public double stdA;
		public double stdB;
		
		
		public MatchResult(List <DoublePair> results){
			this.results = results;
			
			
			double sumA = 0.;
			double sumB = 0.;
			for(DoublePair dp : results){
				sumA += dp.a;
				sumB += dp.b;
			}
			
			this.avgA = sumA / results.size();
			this.avgB = sumB / results.size();
			
			double sumVA = 0.;
			double sumVB = 0.;
			for(DoublePair dp : results){
				
				double diffA = dp.a - this.avgA;
				double diffB = dp.b - this.avgB;
				
				sumVA += diffA*diffA;
				sumVB += diffB*diffB;
				
			}
			
			this.stdA = Math.sqrt(sumVA / results.size());
			this.stdB = Math.sqrt(sumVB / results.size());
			
		}
		
		
	}
	
	
	class DoublePair{
		
		public double a;
		public double b;
		
		public DoublePair(double a, double b){
			this.a = a;
			this.b = b;
		}
		
		public DoublePair reverse(){
			return new DoublePair(this.b, this.a);
		}
		
	}
	

}