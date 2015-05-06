package ethics.experiments.fssimple.specialagents;

import burlap.behavior.statehashing.StateHashFactory;
import burlap.oomdp.auxiliary.StateAbstraction;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.AgentFactory;
import burlap.oomdp.stochasticgames.SGDomain;


/**
 * And agent factor that produces QLearning agents.
 * @author James MacGlashan
 *
 */
public class SarsaAgentFactory implements AgentFactory {

	/**
	 * The stochastic games domain in which the agent will act
	 */
	protected SGDomain													domain;
	
	/**
	 * The discount rate the Q-learning algorithm will use
	 */
	protected double													discount;
	
	/**
	 * The learning rate the Q-learning algorithm will use
	 */
	protected double													learningRate;
	
	/**
	 * The default Q-value to which Q-values will be initialized
	 */
	protected double													defaultQ;
	
	
	/**
	 * The state hashing factory the Q-learning algorithm will use
	 */
	protected StateHashFactory											stateHash; 
	
	
	/**
	 * The state abstract the Q-learning algorithm will use
	 */
	protected StateAbstraction											storedAbstraction;
	protected double lambda;
	
	/**
	 * Initializes the factory. No state abstraction is set to be used.
	 * @param domain The stochastic games domain in which the agent will act
	 * @param discount The discount rate the Q-learning algorithm will use
	 * @param learningRate The learning rate the Q-learning algorithm will use
	 * @param defaultQ The default Q-value to which Q-values will be initialized
	 * @param stateHash The state hashing factory the Q-learning algorithm will use
	 * @param storedAbstraction the state abstraction the Q-learning algorithm will use
	 */
	public SarsaAgentFactory(SGDomain d, double discount, double learningRate, double defaultQ, StateHashFactory hashFactory, double lambda, StateAbstraction storedAbstraction) {
		this.domain = d;
		this.discount = discount;
		this.learningRate = learningRate;
		this.defaultQ = defaultQ;
		this.stateHash = hashFactory;
		this.lambda = lambda;
		this.storedAbstraction = storedAbstraction;
	}
	
	
	/**
	 * Sets the factory to provide Q-learning algorithms with the given state abstraction.
	 * @param abs the state abstraction to use
	 */
	public void setStoredAbstraction(StateAbstraction abs){
		this.storedAbstraction = abs;
	}

	@Override
	public Agent generateAgent() {
		SarsaAgent agent = new SarsaAgent(domain, discount, learningRate, defaultQ, stateHash, lambda);
		if(storedAbstraction != null){
			agent.setStoredMapAbstraction(storedAbstraction);
		}
		return agent;
	}

}