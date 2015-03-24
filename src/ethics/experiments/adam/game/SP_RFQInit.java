package ethics.experiments.adam.game;

import burlap.behavior.singleagent.ValueFunctionInitialization;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointReward;

// This class initializes Q-values to either their objective reward function
// values, or their objective RF values + their subjective RF values;
public class SP_RFQInit implements ValueFunctionInitialization {

	protected SP_SubjectiveRF		subjectiveRF;
	protected JointReward			objectiveRF;
	
	public SP_RFQInit(SP_JR objectiveRF, SP_SubjectiveRF subjectiveRF){
		this.objectiveRF = objectiveRF;
		this.subjectiveRF = subjectiveRF;
	}
	
	public SP_RFQInit(JointReward objectiveRF2) {
		this.objectiveRF = objectiveRF2;
		this.subjectiveRF = null;
	}
	
	public void setSubjectiveRF(SP_SubjectiveRF subjRF) {
		this.subjectiveRF = subjRF;
	}
	
	@Override
	public double value(State s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double qValue(State s, AbstractGroundedAction a) {
		
		GroundedSingleAction gsa = (GroundedSingleAction)a;
		double param = 0;
		
		if (this.subjectiveRF != null) {
			param = this.subjectiveRF.getParameters().get(s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE).getStringValForAttribute(SP_Domain.ATTSTATENAME));
		}
		
		if(gsa.action.actionName.equals(SP_Domain.ACTIONSTEAL)){
			return param + ((SP_JR) this.objectiveRF).getStealerReward();
		}
		else if(gsa.action.actionName.equals(SP_Domain.ACTIONPUNISH)){
			return param + ((SP_JR) this.objectiveRF).getPunisherReward();
		}
		
		return 0; //return 0 for all do nothing actions
	}
	
}
