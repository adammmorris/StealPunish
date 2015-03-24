package ethics.experiments.adam.game;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;

public class SP_SubjectiveRF implements JointReward {

	protected JointReward		objectiveRewardFunction;
	protected Map<String,Double> params; // the length of this should be numStates - one param for each state
	
	
	public SP_SubjectiveRF(JointReward objectiveRewardFunction){
		this.objectiveRewardFunction = objectiveRewardFunction;
	}
	
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		
		Map<String, Double> r = new HashMap<String, Double>();
		Map<String, Double> or = this.objectiveRewardFunction.reward(s, ja, sp);
		
		ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
		String stateName = sn.getStringValForAttribute(SP_Domain.ATTSTATENAME);
		
		for(GroundedSingleAction gsa : ja){
			double nr = or.get(gsa.actingAgent);
			ObjectInstance player = s.getObject(gsa.actingAgent);
			
			// If the player did his relevant action (either stealing or punishing),
			// tack on the parameter reward for this state.
			if ((player.getDiscValForAttribute(SP_Domain.ATTPN) == SP_Domain.THIEFPN
					&& gsa.action.actionName.equals(SP_Domain.ACTIONSTEAL))
					||
					(player.getDiscValForAttribute(SP_Domain.ATTPN) == SP_Domain.PUNISHERPN
					&& gsa.action.actionName.equals(SP_Domain.ACTIONPUNISH))) {
				nr += this.params.get(stateName);
			}
			r.put(gsa.actingAgent, nr);
		}
		
		return r;
	}

	public void setParameters(Map<String,Double> params) {
		this.params = params;
	}

	public int parameterSize() {
		return params.size();
	}

	public Map<String,Double> getParameters() {
		return this.params;
	}

	public void printParameters() {
		System.out.println(this.toString());
	}
	
	public String toString(){
		return "Gotta do this..";
	}
	
	
	
	public static class SP_SubjectiveRFFactory {

		public JointReward objectiveRF;
		private int paramSize;
		
		public SP_SubjectiveRFFactory(JointReward objectiveRF){
			this.objectiveRF = objectiveRF;
		}
		
		public SP_SubjectiveRF generateRF(Map<String,Double> params) {
		
			SP_SubjectiveRF rf = new SP_SubjectiveRF(objectiveRF);
			rf.setParameters(params);
			
			this.paramSize = params.size();
			
			return rf;
		}

		public int parameterSize() {
			return paramSize;
		}
		
		
		
	}

}