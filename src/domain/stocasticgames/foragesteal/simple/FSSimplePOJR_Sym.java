package domain.stocasticgames.foragesteal.simple;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;

public class FSSimplePOJR_Sym extends FSSimpleJR_Sym {

	
	public FSSimplePOJR_Sym(double stealValue, double puncherReward, double puncheeReward, double...forageRewards){
		super(stealValue, puncherReward, puncheeReward, forageRewards);
	}
	
	/*public FSSimplePOJR(double stealerValue, double stealeeValue,double puncherReward, double puncheeReward, double...forageRewards){
		super(stealerValue, stealeeValue, puncherReward, puncheeReward, forageRewards);
	}*/
	
	
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		
		Map<String, Double> r = new HashMap<String, Double>();
		
		GroundedSingleAction player0Action = null;
		GroundedSingleAction player1Action = null;
		
		ObjectInstance player0 = null;
		ObjectInstance player1 = null;
		
		for(GroundedSingleAction gsa : ja){
			ObjectInstance player = s.getObject(gsa.actingAgent);
			if(player.getDiscValForAttribute(FSSimple_Sym.ATTPN) == 0){
				player0Action = gsa;
				player0 = player;
			}
			else{
				player1Action = gsa;
				player1 = player;
			}
		}
		
		if(FSSimple_Sym.isRootNode(s)){
			
			if(player0Action.action.actionName.startsWith(FSSimple_Sym.ACTIONFORAGEBASE)){
				int fa = this.forageAltForAction(player0Action);
				r.put(player0Action.actingAgent, this.forageRewards[fa]);
				if(player1Action != null){
					r.put(player1Action.actingAgent, 0.);
				}
			}
			else{
				//must have stolen
				r.put(player0Action.actingAgent, this.stealerReward);
				if(player1Action != null){
					//is the agent aware?
					if(player1.getDiscValForAttribute(FSSimple_Sym.ATTBACKTURNED) == 0){
						r.put(player1Action.actingAgent, this.stealeeReward);
					}
					else{
						r.put(player1Action.actingAgent, 0.);
					}
				}
			}
			
		}
		else{
			
			if(player1Action.action.actionName.equals(FSSimple_Sym.ACTIONPUNISH)){
				
				if(player0Action != null){
					//is the agent aware?
					if(player0.getDiscValForAttribute(FSSimple_Sym.ATTBACKTURNED) == 0){
						r.put(player0Action.actingAgent, this.puncheeReward);
					}
					else{
						r.put(player0Action.actingAgent, 0.);
					}
				}
				r.put(player1Action.actingAgent, this.puncherReward);
			}
			else{
				//must have done nothing
				if(player0Action != null){
					r.put(player0Action.actingAgent, 0.);
				}
				r.put(player1Action.actingAgent, 0.);
			}
			
		}
		
		
		return r;
		
		
	}

}