package ethics.experiments.ir;

import java.util.HashMap;
import java.util.Map;

import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.AgentFactory;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SingleAction;

public class FixedIR extends Agent {

	protected SingleAction initiate;
	protected SingleAction respond;
	protected SingleAction donothing;
	
	protected FixedIRPolicy policy;
	
	protected String agentName;
	
	public FixedIR(SGDomain domain, SingleAction initiate, SingleAction respond, SingleAction donothing, Map<Integer,SingleAction> policy) {
		this.init(domain);
		this.initiate = initiate;
		this.respond = respond;
		this.donothing = donothing;
		this.policy = new FixedIRPolicy(policy,donothing);
	}
	
	public FixedIR(SGDomain domain, SingleAction initiate, SingleAction respond, SingleAction donothing, int[] policy) {
		this.init(domain);
		this.initiate = initiate;
		this.respond = respond;
		this.donothing = donothing;
		this.policy = new FixedIRPolicy(policy,initiate,respond,donothing);
	}
	
	@Override
	public void gameStarting() {
		// TODO Auto-generated method stub
		this.agentName = this.worldAgentName;
		this.policy.setAgentName(this.agentName);
	}

	@Override
	public GroundedSingleAction getAction(State s) {
		return (GroundedSingleAction) this.policy.getAction(s);
	}

	@Override
	public void observeOutcome(State s, JointAction jointAction,
			Map<String, Double> jointReward, State sprime, boolean isTerminal) {
		// Do nothing - this guy doesn't learn anything
	}

	@Override
	public void gameTerminated() {
		// TODO Auto-generated method stub

	}

	public static class FixedIRFactory implements AgentFactory{

		protected SingleAction initiate;
		protected SingleAction respond;
		protected SingleAction donothing;
		
		protected HashMap<Integer,SingleAction> policyMap;
		protected SGDomain domain;
		
		public FixedIRFactory(SGDomain domain, SingleAction initiate, SingleAction respond, SingleAction donothing, int[] policy){
			this.domain = domain;
			this.initiate = initiate;
			this.respond = respond;
			this.donothing = donothing;
			this.policyMap = FixedIRPolicy.generatePolicyMap(policy,initiate,respond,donothing);
		}
		
		@Override
		public Agent generateAgent() {
			return new FixedIR(this.domain, this.initiate,this.respond,this.donothing,policyMap);
		}
		
		
		
	}
}
