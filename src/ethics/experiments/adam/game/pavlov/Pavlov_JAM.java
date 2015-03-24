package ethics.experiments.adam.game.pavlov;

import java.util.List;
import java.util.Random;

import ethics.experiments.adam.game.SP_Domain;
import burlap.debugtools.RandomFactory;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointActionModel;

/**
 * See Pavlov.java.
 */
public class Pavlov_JAM extends JointActionModel {

	protected Random rand;
	
	// Both memories must be at least 1.
	public Pavlov_JAM(){
		rand = RandomFactory.getMapped(0);
	}
	
	
	@Override
	public List<TransitionProbability> transitionProbsFor(State s,
			JointAction ja) {
		throw new RuntimeException("Transition probabilities currently not provided for back turned joint action model.");
	}

	@Override
	protected State actionHelper(State s, JointAction ja) {
		
		GroundedSingleAction thiefAction = null;
		GroundedSingleAction punisherAction = null;
		
		for(GroundedSingleAction gsa : ja){
			ObjectInstance player = s.getObject(gsa.actingAgent);
			if(player.getDiscValForAttribute(SP_Domain.ATTPN) == SP_Domain.THIEFPN){
				thiefAction = gsa;
			}
			else{
				punisherAction = gsa;
			}
		}
		
		ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
		String stateName = sn.getStringValForAttribute(SP_Domain.ATTSTATENAME);
		String nextStateName = null;
		
		if (stateName == Pavlov.STATEA) {
			// Add a letter.
			if (thiefAction.action.actionName.equals(SP_Domain.ACTIONSTEAL)) {
				nextStateName = Pavlov.STATED;
			} else {
				nextStateName = Pavlov.STATEC;
			}
			
			sn.setValue(SP_Domain.ATTISTHIEFTURN, 0);
		} else if (stateName == Pavlov.STATEB) {
			// Add a letter.
			if (thiefAction.action.actionName.equals(SP_Domain.ACTIONSTEAL)) {
				nextStateName = Pavlov.STATEC;
			} else {
				nextStateName = Pavlov.STATED;
			}

			sn.setValue(SP_Domain.ATTISTHIEFTURN, 0);
		} else if (stateName == Pavlov.STATEC) {
			if (punisherAction.action.actionName.equals(SP_Domain.ACTIONPUNISH)) {
				nextStateName = Pavlov.STATEB;
			} else {
				nextStateName = Pavlov.STATEA;
			}
			
			sn.setValue(SP_Domain.ATTISTHIEFTURN, 1);
		} else if (stateName == Pavlov.STATED) {
			if (punisherAction.action.actionName.equals(SP_Domain.ACTIONPUNISH)) {
				nextStateName = Pavlov.STATEA;
			} else {
				nextStateName = Pavlov.STATEB;
			}
			
			sn.setValue(SP_Domain.ATTISTHIEFTURN, 1);
		}
		
		sn.setValue(SP_Domain.ATTSTATENAME, nextStateName);

		return s;	
	}
}