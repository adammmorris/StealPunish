package ethics.experiments.ir;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;

/**
 * Similar to FSSimpleJR, excepts allows for asymmetric initiation.
 * @author amm4
 *
 */
public class IRJR implements JointReward {

double initiatorReward;
double initiateeReward;
double responderReward;
double respondeeReward;
//double [] forageRewards;

public IRJR(double initiatorReward, double initiateeReward, double responderReward, double respondeeReward) {
	this.initiatorReward = initiatorReward;
	this.initiateeReward = initiateeReward;
	this.responderReward = responderReward;
	this.respondeeReward = respondeeReward;
	//this.forageRewards = forageValues;
}

@Override
public Map<String, Double> reward(State s, JointAction ja, State sp) {
	
	Map<String, Double> r = new HashMap<String, Double>();
	
	GroundedSingleAction player0Action = null;
	GroundedSingleAction player1Action = null;
	
	for(GroundedSingleAction gsa : ja){
		ObjectInstance player = s.getObject(gsa.actingAgent);
		if(player.getDiscValForAttribute(IRGame.ATTPN) == 0){
			player0Action = gsa;
		}
		else{
			player1Action = gsa;
		}
	}
	
	if(IRGame.isRootNode(s)){
		
		if(player0Action.action.actionName.equals(IRDomain.ACTIONDONOTHING)){
			//int fa = this.forageAltForAction(player0Action);
			r.put(player0Action.actingAgent, 0.);
			if(player1Action != null){
				r.put(player1Action.actingAgent, 0.);
			}
		}
		else{
			//must have stolen
			r.put(player0Action.actingAgent, this.initiatorReward);
			if(player1Action != null){
				r.put(player1Action.actingAgent, this.initiateeReward);
			}
		}
		
	}
	else{
		
		if(player1Action.action.actionName.equals(IRDomain.ACTIONRESPOND)){
			
			if(player0Action != null){
				r.put(player0Action.actingAgent, this.respondeeReward);
			}
			r.put(player1Action.actingAgent, this.responderReward);
		}
		else{
			//must have done nothing
			if(player0Action != null){
				r.put(player0Action.actingAgent, 0.);
			}
			r.put(player1Action.actingAgent, 0.);
		}
		
	}
	
	for(Double d : r.values()){
		if(Double.isNaN(d)){
			throw new RuntimeException("NaN reward");
		}
	}
	
	
	return r;
}


public double getInitiatorReward() {
	return initiatorReward;
}

public double getInitiateeReward() {
	return initiateeReward;
}

public double getResponderReward() {
	return responderReward;
}

public double getRespondeeReward() {
	return respondeeReward;
}

/*public double[] getForageRewards() {
	return forageRewards;
}*/

/*protected int forageAltForAction(GroundedSingleAction gsa){
	ForageAction sa = (ForageAction)gsa.action;
	return sa.falt;
}*/

}
