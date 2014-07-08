package ethics.experiments.bimatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ethics.experiments.bimatrix.FixedHistoryPolicy.SingleActionPair;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.AgentFactory;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SingleAction;


/**
 * A class for an agent that plays tit-for-tat. The agent starts by playing a "cooperate" action. If their opponent "defects" on them, then
 * this agent plays their "defect" action in the next turn. If their opponent "cooperates" again, then this agent will cooperate in the next turn.
 * The corresponding "cooperate" and "defect" actions for each agent need to be specified. When a new game starts, this agent will reset
 * to trying cooperate.
 * @author James MacGlashan
 *
 */
public class FixedHistory extends Agent {

	/**
	 * This agent's two actions
	 */
	protected SingleAction action0;
	protected SingleAction action1;
	
	/**
	 * Opponent's two actions - right now, these are the same (b/c game is symmetric)
	 */
	protected SingleAction oppAction1;
	protected SingleAction oppAction2;
	
	/**
	 * This agent's policy
	 */
	protected FixedHistoryPolicy policy;
		
	/**
	 * Initializes with the specified cooperate and defect actions for both players.
	 * @param domain the domain in which this agent will play.
	 * @param coop the cooperate action for both players
	 * @param defect the defect action for both players
	 */
	public FixedHistory(SGDomain domain, SingleAction act0, SingleAction act1, HashMap<Integer,SingleAction> policyIn, SingleAction firstMove){
		this.init(domain);
		
		this.action0 = act0;
		this.action1 = act1;
		this.oppAction1 = act0;
		this.oppAction2 = act1;
		this.policy = new FixedHistoryPolicy(policyIn, firstMove);
	}
	
	public FixedHistory(SGDomain domain, SingleAction act0, SingleAction act1, int[] policyIn, SingleAction firstMove){
		this.init(domain);
		
		this.action0 = act0;
		this.action1 = act1;
		this.oppAction1 = act0;
		this.oppAction2 = act1;
		this.policy = new FixedHistoryPolicy(policyIn, this.action0, this.action1, firstMove);
	}
	
	
	@Override
	public void gameStarting() {
		this.policy.setFirstMove();
		this.policy.setAgentName(this.worldAgentName);
	}

	@Override
	public GroundedSingleAction getAction(State s) {
		return this.policy.getAction(s);
	}

	@Override
	public void observeOutcome(State s, JointAction jointAction, Map<String, Double> jointReward, State sprime, boolean isTerminal) {
		/*SingleAction lastOpponentMove = null;
		SingleAction lastMove = null;
		for(GroundedSingleAction gsa : jointAction){
			if(!gsa.actingAgent.equals(this.worldAgentName)){
				lastOpponentMove = gsa.action;
			} else {
				lastMove = gsa.action;
			}
		}
		
		this.policy.setPreviousMove(lastMove, lastOpponentMove);*/
	}

	@Override
	public void gameTerminated() {

	}
	
	
	/**
	 * An agent factory for a TitForTat player.
	 * @author James MacGlashan
	 *
	 */
	public static class FixedHistoryDeterministicAgentFactory implements AgentFactory{

		/**
		 * This agent's two actions
		 */
		protected SingleAction action0;
		protected SingleAction action1;
		
		/**
		 * Opponent's two actions - right now, these are the same (b/c game is symmetric)
		 */
		protected SingleAction oppAction1;
		protected SingleAction oppAction2;
		
		/**
		 * This agent's policy
		 */
		protected HashMap<Integer, SingleAction> policyMap;
		protected SingleAction firstMove;
		
		/**
		 * This agent's domain
		 */
		protected SGDomain domain;
		
		public FixedHistoryDeterministicAgentFactory(SGDomain domain, SingleAction act0, SingleAction act1, HashMap<Integer,SingleAction> policyIn, SingleAction firstMove){
			this.domain = domain;
			this.action0 = act0;
			this.action1 = act1;
			this.oppAction1 = act0;
			this.oppAction2 = act1;
			this.policyMap = policyIn;
			this.firstMove = firstMove;
		}
		
		public FixedHistoryDeterministicAgentFactory(SGDomain domain, SingleAction act0, SingleAction act1, int[] policyIn, SingleAction firstMove){
			this.domain = domain;
			this.action0 = act0;
			this.action1 = act1;
			this.oppAction1 = act0;
			this.oppAction2 = act1;
			this.policyMap = FixedHistoryPolicy.generatePolicyMap(policyIn,act0,act1);
			this.firstMove = firstMove;
		}
		
		@Override
		public Agent generateAgent() {
			return new FixedHistory(this.domain, this.action0, this.action1, this.policyMap, this.firstMove);
		}
		
		
		
	}
}