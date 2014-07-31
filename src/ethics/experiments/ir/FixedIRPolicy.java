package ethics.experiments.ir;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import burlap.behavior.singleagent.Policy;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.SingleAction;

public class FixedIRPolicy extends Policy {

	protected Map<Integer, SingleAction> policy;
	protected String agentName;
	protected SingleAction donothing;
	
	public FixedIRPolicy(Map<Integer,SingleAction> policy,SingleAction donothing) {
		this.policy = policy;
		this.donothing = donothing;
	}
	
	public FixedIRPolicy(int[] policy, SingleAction initiate, SingleAction respond, SingleAction donothing) {
		this.policy = FixedIRPolicy.generatePolicyMap(policy,initiate,respond,donothing);
		this.donothing = donothing;
	}
	
	public static HashMap<Integer,SingleAction> generatePolicyMap(int[] params, SingleAction initiate, SingleAction respond, SingleAction donothing) {
		HashMap<Integer,SingleAction> policyMap = new HashMap<Integer,SingleAction>(); // keys are state #s
		
		SingleAction[] responses = new SingleAction[3];
		
		for(int i = 0; i < params.length; i++) {
			if (i==0|i==1) {
				if (params[i]==0) responses[i] = donothing;
				else responses[i] = initiate;
			} else {
				if (params[i]==0) responses[i] = donothing;
				else responses[i] = respond;
			}
			
			policyMap.put(i, responses[i]);
		}
		
		return policyMap;
	}
	
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	@Override
	public AbstractGroundedAction getAction(State s) {
		/*int agentNum = getAgentNum(s);

		ObjectInstance stateNode = s.getFirstObjectOfClass(FSSimple.CLASSSTATENODE);
		int stateNum = stateNode.getDiscValForAttribute(FSSimple.ATTSTATENODE);
		
		if (agentNum==0) {
			if (stateNum==0 || stateNum==1) return new GroundedSingleAction(this.agentName,this.policy.get(stateNum),"");
		} else {
			if (stateNum==2) return new GroundedSingleAction(this.agentName,this.policy.get(stateNum),"");
		}*/
		int agentNum = getAgentNum(s);
		
		ObjectInstance stateNode = s.getFirstObjectOfClass(IRGame.CLASSSTATENODE);
		int stateNum = stateNode.getDiscValForAttribute(IRGame.ATTSTATENODE);
		
		if ((agentNum==1 && IRDomain.isRootNode(s)) || (agentNum==0 && !IRDomain.isRootNode(s))) return new GroundedSingleAction(this.agentName,this.donothing,"");
		return new GroundedSingleAction(this.agentName,this.policy.get(stateNum),"");
	}
	
	private int getAgentNum(State s) {
		List<ObjectInstance> agents = s.getObjectsOfTrueClass(IRGame.CLASSPLAYER);
		ObjectInstance initiator = null;
		ObjectInstance responder = null;
		for (ObjectInstance a:agents) {
			if (a.getDiscValForAttribute(IRGame.ATTPN)==0) initiator = a;
			else responder = a;
		}
		
		int agentNum = -1;
		if (initiator.getName().equals(this.agentName)) agentNum = 0;
		else if (responder.getName().equals(this.agentName)) agentNum = 1;

		return agentNum;
	}

	@Override
	public List<ActionProb> getActionDistributionForState(State s) {
		return null;
	}

	@Override
	public boolean isStochastic() {
		return false;
	}

	@Override
	public boolean isDefinedFor(State s) {
		int agentNum = getAgentNum(s);
		
		List<ObjectInstance> stateNode = s.getObjectsOfTrueClass(IRGame.CLASSSTATENODE);
		int stateNum = stateNode.get(0).getDiscValForAttribute(IRGame.ATTSTATENODE);
		
		if (agentNum==0 && (stateNum==0||stateNum==1)) return true;
		else if (agentNum==1 && stateNum==2) return true;
		return false;
	}

}
