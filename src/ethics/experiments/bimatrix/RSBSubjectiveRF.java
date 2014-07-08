package ethics.experiments.bimatrix;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;
import ethics.ParameterizedRF;

public class RSBSubjectiveRF implements ParameterizedRF {

	protected JointReward objectiveRF;
	protected double[] params;
	protected int numParams = 8;
	
	public RSBSubjectiveRF(JointReward objectiveRF) {
		this.objectiveRF = objectiveRF;
		params = new double[numParams];
	}
	
	public RSBSubjectiveRF(JointReward objectiveRF, double[] params) {
		this.objectiveRF = objectiveRF;
		this.params = params.clone();
		if(params.length != numParams) System.out.println("WARNING: params should have " + numParams + " values (in RSBSubjectiveRF())");
	}
	
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		List<String> agents = ja.getAgentNames();
		String agent0name = agents.get(0);
		String agent1name = agents.get(1);
		
		ObjectInstance agent0 = s.getObject(agent0name);
		ObjectInstance agent1 = s.getObject(agent1name);
		
		Map<String,Double> objectiveRewards = this.objectiveRF.reward(s, ja, sp);
		double agent0or = objectiveRewards.get(agent0name);
		double agent1or = objectiveRewards.get(agent1name);
		
		// Last moves
		int agent0LM = agent0.getDiscValForAttribute(FHSingleStageNormalFormGame.ATTLM);
		int agent1LM = agent1.getDiscValForAttribute(FHSingleStageNormalFormGame.ATTLM);
		
		if (agent0LM == FHSingleStageNormalFormGame.NOLASTMOVE || agent1LM == FHSingleStageNormalFormGame.NOLASTMOVE) return objectiveRewards;
		
		String strAgent0LM = FHSingleStageNormalFormGame.getActionName(agent0LM);
		String strAgent1LM = FHSingleStageNormalFormGame.getActionName(agent1LM);
		
		double agent0sr = agent0or + this.getSubjectiveBias(ja.action(agent0name).actionName(), strAgent0LM, strAgent1LM);
		double agent1sr = agent1or + this.getSubjectiveBias(ja.action(agent1name).actionName(), strAgent1LM, strAgent0LM);
		
		Map <String, Double> subjectiveRewards = new HashMap<String, Double>();
		subjectiveRewards.put(agent0name, agent0sr);
		subjectiveRewards.put(agent1name, agent1sr);
		
		return subjectiveRewards;
	}
	
	protected double getSubjectiveBias(String actionName, String myLastMove, String oppLastMove) {
		return this.params[FHSingleStageNormalFormGame.getStateActionNumber(actionName, FHSingleStageNormalFormGame.getStateNumber(myLastMove, oppLastMove))];
	}

	@Override
	public void setParameters(double[] params) {
		this.params = params.clone();
	}

	@Override
	public int parameterSize() {
		return numParams;
	}

	@Override
	public double[] getParameters() {
		return params.clone();
	}

	@Override
	public void printParameters() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("Action0 bias given (Action0,Action0): ").append(params[0]).append("\n");
		sbuf.append("Action0 bias given (Action0,Action1): ").append(params[1]).append("\n");
		sbuf.append("Action0 bias given (Action1,Action0): ").append(params[2]).append("\n");
		sbuf.append("Action0 bias given (Action1,Action1): ").append(params[3]).append("\n");
		sbuf.append("Action1 bias given (Action0,Action0): ").append(params[0]).append("\n");
		sbuf.append("Action1 bias given (Action0,Action1): ").append(params[1]).append("\n");
		sbuf.append("Action1 bias given (Action1,Action0): ").append(params[2]).append("\n");
		sbuf.append("Action1 bias given (Action1,Action1): ").append(params[3]).append("\n");
		
		return sbuf.toString();
		
	}
}
