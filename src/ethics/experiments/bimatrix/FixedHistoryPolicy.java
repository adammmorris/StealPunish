package ethics.experiments.bimatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import burlap.behavior.singleagent.Policy;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SingleAction;

public class FixedHistoryPolicy extends Policy {

	/**
	 * This is always specified as {myLastMove,opponentLastMove}
	 */
	protected String lastMove;
	
	/**
	 * This is the map we will construct between last moves and next moves.
	 */
	protected HashMap<Integer, SingleAction> policy;
	
	/**
	 * Must specify the first move.
	 */
	protected SingleAction firstMove;
	
	/**
	 * Is there a way to tell just from the state object whether it's the first move or not?
	 */
	protected boolean isFirstMove;
	
	/**
	 * I'm putting this in here in order to ground shit.  But is there a better way?
	 */
	protected String agentName;
	
	/**
	 * 
	 * @param policy The map between pairs of actions from last move and the next action to be taken
	 */
	public FixedHistoryPolicy(HashMap<Integer,SingleAction> policy, SingleAction firstMove) {
		this.policy = policy;
		this.firstMove = firstMove;
	}
	
	/**
	 * This is a "shortcut" constructor.  For example, if action0 is cooperate & action1 is defect, then {0,1,0,1} is Tit-for-Tat.
	 * @param params An array of 4 integers, all of which must be either 0 or 1.  The array corresponds to the prior states: [(0,0),(0,1),(1,0),(1,1)].
	 */
	public FixedHistoryPolicy(int[] params, SingleAction action0, SingleAction action1, SingleAction firstMove) {
		this.policy = FixedHistoryPolicy.generatePolicyMap(params, action0, action1);
		this.firstMove = firstMove;
	}
	
	@Override
	public GroundedSingleAction getAction(State s) {
		List<ObjectInstance> agents = s.getObjectsOfTrueClass(FHSingleStageNormalFormGame.CLASSPLAYER);
		ObjectInstance thisAgent = null;
		ObjectInstance otherAgent = null;
		
		for(ObjectInstance a : agents) {
			if(a.getName().equals(this.agentName)) thisAgent = a;
			else otherAgent = a;
		}
		
		if(thisAgent.getDiscValForAttribute(FHSingleStageNormalFormGame.ATTLM) == FHSingleStageNormalFormGame.NOLASTMOVE) return new GroundedSingleAction(this.agentName,this.firstMove,"");
		
		String thisAgentLM = FHSingleStageNormalFormGame.getActionName(thisAgent.getDiscValForAttribute(FHSingleStageNormalFormGame.ATTLM));
		String otherAgentLM = FHSingleStageNormalFormGame.getActionName(otherAgent.getDiscValForAttribute(FHSingleStageNormalFormGame.ATTLM));
		return new GroundedSingleAction(this.agentName,this.policy.get(FHSingleStageNormalFormGame.getStateNumber(thisAgentLM, otherAgentLM)),"");
	}

	public void setFirstMove() {
		this.isFirstMove = true;
	}
	
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	/*public void setPreviousMove(SingleAction myLastMove, SingleAction oppLastMove) {
		this.isFirstMove = false;
		this.lastMove = new String(myLastMove.actionName + oppLastMove.actionName);
	}*/
	
	public static HashMap<Integer, SingleAction> generatePolicyMap(int[] params, SingleAction action0, SingleAction action1) {
		HashMap<Integer,SingleAction> policyMap = new HashMap<Integer,SingleAction>(); // keys are state #s (see FHSingleStageNormalFormGame.getStateNumber)
		
		SingleAction[] responses = new SingleAction[4];
		
		for(int i = 0; i < params.length; i++) {
			if(params[i]==0) responses[i]=action0;
			else responses[i]=action1;
			
			policyMap.put(i, responses[i]);
		}
		
		return policyMap;
	}
	
	@Override
	// Do I need to implement this?  Everything's deterministic here
	public List<ActionProb> getActionDistributionForState(State s) {
		return null;
	}

	@Override
	public boolean isStochastic() {
		return true;
	}

	@Override
	public boolean isDefinedFor(State s) {
		return true;
	}

	public static class SingleActionPair {
		public SingleAction action1;
		public SingleAction action2;
		
		public SingleActionPair(SingleAction action1, SingleAction action2) {
			this.action1 = action1;
			this.action2 = action2;
		}
		
		public boolean equals(SingleActionPair other) {
			if(other.action1.equals(this.action1) && other.action2.equals(this.action2)) return true;
			return false;
		}
	}
}
