package ethics.experiments.adam.game;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;

/**
 * This is the general joint reward class for the Steal-Punish game.
 */

public class SP_JR implements JointReward {

	double stealerReward;
	double stealeeReward;
	double punisherReward;
	double punisheeReward;
	
	public SP_JR(double stealerValue, double stealeeValue, double punisherReward, double punisheeReward){
		this.stealerReward = stealerValue;
		this.stealeeReward = stealeeValue;
		this.punisherReward = punisherReward;
		this.punisheeReward = punisheeReward;
	}
	
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		
		Map<String, Double> r = new HashMap<String, Double>();
		
		GroundedSingleAction thiefAction = null;
		GroundedSingleAction punisherAction = null;
		boolean thiefsTurn = false;
		
		ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
		
		for(GroundedSingleAction gsa : ja){
			ObjectInstance player = s.getObject(gsa.actingAgent);
			if(player.getDiscValForAttribute(SP_Domain.ATTPN) == SP_Domain.THIEFPN){
				thiefAction = gsa;
				if (sn.getDiscValForAttribute(SP_Domain.ATTISTHIEFTURN) == 1) {
					thiefsTurn = true;
				}
			}
			else{
				punisherAction = gsa;
			}
		}
		
		if (thiefsTurn == true) {
			if (thiefAction.action.actionName.equals(SP_Domain.ACTIONSTEAL)) {
				r.put(thiefAction.actingAgent, this.stealerReward);
				if (punisherAction != null) {
					r.put(punisherAction.actingAgent, this.stealeeReward);
				}
			} else {
				r.put(thiefAction.actingAgent, 0.);
				if (punisherAction != null) {
					r.put(punisherAction.actingAgent, 0.);
				}
			}
		}
		else if (thiefsTurn == false) {
			if (punisherAction.action.actionName.equals(SP_Domain.ACTIONPUNISH)) {
				r.put(punisherAction.actingAgent, this.punisherReward);
				if (thiefAction != null) {
					r.put(thiefAction.actingAgent, this.punisheeReward);
				}
			} else {
				r.put(punisherAction.actingAgent, 0.);
				if (thiefAction != null) {
					r.put(thiefAction.actingAgent, 0.);
				}
			}
		}
		
		for(Double d : r.values()){
			if(Double.isNaN(d)){
				throw new RuntimeException("NaN reward");
			}
		}
		
		
		return r;
	}

	
	public double getStealerReward() {
		return stealerReward;
	}

	public double getStealeeReward() {
		return stealeeReward;
	}

	public double getPunisherReward() {
		return punisherReward;
	}

	public double getPunisheeReward() {
		return punisheeReward;
	}
}