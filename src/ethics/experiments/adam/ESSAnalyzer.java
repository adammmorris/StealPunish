package ethics.experiments.adam;

import java.io.File;

public class ESSAnalyzer {
	public static void main(String[] x) {
		double[] values = new double[]{0,.025,.05,.075,.15,.2,.25,.5};
		String basePath = "/home/amm4/git/generalResearch/output/GAOutput/StealPunish/ESS/";
		String varName = "Mut";
		
		// Create directories
		for (int i = 1; i <= values.length; i++) {
			new File(basePath+varName+"/"+i+"a").mkdir();
			new File(basePath+varName+"/"+i+"b").mkdir();
		}
		
		String[] base = new String[]{basePath+varName+"/","",""};
		for (int i = 1; i <= values.length; i++) {
			String[] args = base.clone();
			args[0] = args[0] + i + "a";
			args[1] = String.valueOf(values[i-1]);
			args[2] = "0";
			MatchCaching_ESS.main(args);
			
			args = base.clone();
			args[0] = args[0] + i + "b";
			args[1] = String.valueOf(values[i-1]);
			args[2] = "1";
			MatchCaching_ESS.main(args);
		}
		
		base = new String[]{"5000","20",
				basePath+varName+"/",
				basePath+varName+"/",
				".1", "2", "0", "4", "1", "0", "4", "1"};
		for (int i = 1; i <= values.length; i++) {
			String[] args = base.clone();
			args[4] = String.valueOf(values[i-1]);
			args[2] = args[2] + i + "a/Cache.txt";
			args[3] = args[3] + i + "a/GA.txt";
			Optimizer.main(args);
			
			args = base.clone();
			args[4] = String.valueOf(values[i-1]);
			args[2] = args[2] + i + "b/Cache.txt";
			args[3] = args[3] + i + "b/GA.txt";
			Optimizer.main(args);
		}
	}
}
