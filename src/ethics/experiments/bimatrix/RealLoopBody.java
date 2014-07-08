package ethics.experiments.bimatrix;

public class RealLoopBody implements LoopBody<Integer> {
	public double[][] winnings;
	protected RSBExperiment experiment;
	protected double[] bonusVector0;
	protected double[] bonusVector1;
	
	@Override
	public void run(Integer i) {
		//System.out.println("Elapsed time (beginning match " + i + "): " + (System.nanoTime() - experiment.startTime));
		winnings[i] = experiment.runMatch_QvsQ(bonusVector0, bonusVector1);
		//System.out.println("Elapsed time (ending match " + i + "): " + (System.nanoTime() - experiment.startTime));
	}

	public RealLoopBody(RSBExperiment experiment, double[] bonusVector0, double[] bonusVector1) {
		this.winnings = new double[experiment.numMatchesPerTourn][2];
		this.experiment = experiment;
		this.bonusVector0 = bonusVector0;
		this.bonusVector1 = bonusVector1;
	}
	
	/*public double[] getWinnings() {
		return winnings;
	}*/
}
