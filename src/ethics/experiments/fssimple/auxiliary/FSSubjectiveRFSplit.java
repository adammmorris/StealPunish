package ethics.experiments.fssimple.auxiliary;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;
import domain.stocasticgames.foragesteal.simple.FSSimple;
import ethics.ParameterizedRF;
import ethics.ParameterizedRFFactory;

public class FSSubjectiveRFSplit implements ParameterizedRF{

	protected JointReward		objectiveRewardFunction;
	protected double [] 		params; // 0 is steal bias | no pun, 1 is steal bias | pun, 2 is pun bias
	
	
	public FSSubjectiveRFSplit(JointReward objectiveRewardFunction){
		this.objectiveRewardFunction = objectiveRewardFunction;
	}
	
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		
		Map<String, Double> r = new HashMap<String, Double>();
		Map<String, Double> or = this.objectiveRewardFunction.reward(s, ja, sp);
		
		for(GroundedSingleAction gsa : ja){
			double nr = or.get(gsa.actingAgent);
			if(gsa.action.actionName.equals(FSSimple.ACTIONSTEAL)){
				if (FSSimple.stateNode(s)==0 || FSSimple.stateNode(s)==3) nr += this.params[0];
				else nr += this.params[1];
			}
			else if(gsa.action.actionName.equals(FSSimple.ACTIONPUNISH)){
				nr += this.params[2];
			}
			r.put(gsa.actingAgent, nr);
		}
		
		return r;
	}

	@Override
	public void setParameters(double[] params) {
		this.params = params;
	}

	@Override
	public int parameterSize() {
		return 2;
	}

	@Override
	public double[] getParameters() {
		return this.params;
	}

	@Override
	public void printParameters() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "Steal bias:  " + params[0] + "\nPunish bias: " + params[1];
	}
	
	
	
	public static class FSSubjectiveRFSplitFactory implements ParameterizedRFFactory{

		public JointReward objectiveRF;
		
		
		public FSSubjectiveRFSplitFactory(JointReward objectiveRF){
			this.objectiveRF = objectiveRF;
		}
		
		@Override
		public ParameterizedRF generateRF(double[] params) {
		
			FSSubjectiveRFSplit rf = new FSSubjectiveRFSplit(objectiveRF);
			rf.setParameters(params);
			
			return rf;
		}

		@Override
		public int parameterSize() {
			return 3;
		}
		
		
		
	}

}