package ethics.experiments.adam.game.symfm;

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
 * See SymFM.java.
 */
public class SymFM_JAM extends JointActionModel {

	private int thiefMemory;
	private int punisherMemory;
	protected double probBackTurned;
	protected Random rand;
	
	// Both memories must be at least 1.
	public SymFM_JAM(int thiefMemory, int punisherMemory, double probBackTurned){
		this.thiefMemory = thiefMemory;
		this.punisherMemory = punisherMemory;
		this.probBackTurned = probBackTurned;
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
		ObjectInstance thief = null;
		ObjectInstance punisher = null;
		
		for(GroundedSingleAction gsa : ja){
			ObjectInstance player = s.getObject(gsa.actingAgent);
			if(player.getDiscValForAttribute(SP_Domain.ATTPN) == SP_Domain.THIEFPN){
				thiefAction = gsa;
				thief = player;
			}
			else{
				punisherAction = gsa;
				punisher = player;
			}
		}
		
		ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
		String stateName = sn.getStringValForAttribute(SP_Domain.ATTSTATENAME);
		int thiefTurn = sn.getDiscValForAttribute(SP_Domain.ATTISTHIEFTURN);
		String nextStateName = stateName;
		
		if (thiefTurn == 1) {
			// Add a letter.
			if (thiefAction.action.actionName.equals(SP_Domain.ACTIONSTEAL)
					&& punisher.getDiscValForAttribute(SP_Domain.ATTBACKTURNED) == 0) {
				nextStateName += SymFM.LETTERSTEAL;
			} else {
				nextStateName += SymFM.LETTERNOSTEAL;
			}
			
			// If we're too long, chop the first character off.
			if (nextStateName.length() > punisherMemory) {
				nextStateName = nextStateName.substring(1);
			}
			
			sn.setValue(SP_Domain.ATTSTATENAME, nextStateName);
			sn.setValue(SP_Domain.ATTISTHIEFTURN, 0);
			thief.setValue(SP_Domain.ATTBACKTURNED, this.sampleBackTurnedValue());
			punisher.setValue(SP_Domain.ATTBACKTURNED, 0);
		} else {
			// Add a letter.
			if (punisherAction.action.actionName.equals(SP_Domain.ACTIONPUNISH)
					&& thief.getDiscValForAttribute(SP_Domain.ATTBACKTURNED) == 0) {
				nextStateName += SymFM.LETTERPUNISH;
			} else {
				nextStateName += SymFM.LETTERNOPUNISH;
			}
			
			// If we're too long, chop the first character off.
			if (nextStateName.length() > thiefMemory) {
				nextStateName = nextStateName.substring(1);
			}
			
			sn.setValue(SP_Domain.ATTSTATENAME, nextStateName);
			sn.setValue(SP_Domain.ATTISTHIEFTURN, 1);
			thief.setValue(SP_Domain.ATTBACKTURNED, 0);
			punisher.setValue(SP_Domain.ATTBACKTURNED, this.sampleBackTurnedValue());
		}
		
		return s;	
	}
	
	protected int sampleBackTurnedValue(){
		double roll = rand.nextDouble();
		if(roll <= this.probBackTurned){
			return 1;
		}
		return 0;
	}
}