package ethics.experiments.ir;

import ethics.experiments.bimatrix.GameGenerator;
import burlap.domain.stochasticgames.normalform.SingleStageNormalFormGame;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.AgentType;
import burlap.oomdp.stochasticgames.JointActionModel;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import burlap.oomdp.stochasticgames.World;

/**
 * State #s: 0 is initiator's turn | no response, 1 is initiator's turn | response, 2 is responder's turn.
 * Action #s: 0 is don't initiate/don't respond, 1 is initiate/respond.
 * @author amm4
 *
 */
public class IRGame extends IRDomain implements DomainGenerator,GameGenerator {

	protected double initiatorReward;
	protected double initiateeReward;
	protected double responderReward;
	protected double respondeeReward;
	//protected double[] forageRewards;
	protected JointReward objectiveRF;
	
	public static String PLAYER0NAME = "initiator";
	public static String PLAYER1NAME = "responder";
	public static int FORAGEALT = 1;
	
	public IRGame(double initiatorReward, double initiateeReward, double responderReward, double respondeeReward) {
		super();
		this.initiatorReward = initiatorReward;
		this.initiateeReward = initiateeReward;
		this.responderReward = responderReward;
		this.respondeeReward = respondeeReward;
		//this.forageRewards = forageRewards;
		this.objectiveRF = new IRJR(initiatorReward, initiateeReward, responderReward, respondeeReward);
	}
	
	public JointReward getJointRewardFunction() {
		return this.objectiveRF;
	}
	
	
	/**
	 * Creates world for specified FS-type game.  Agent0 is always the initiator, Agent1 is always the responder.
	 * @param domain
	 * @param agents
	 * @return
	 */
	public World createRepeatedGameWorld(SGDomain domain, Agent...agents){
		
		JointActionModel jam = new IRJAM(); 
		
		TerminalFunction tf = new NullTermination();
		
		SGStateGenerator sg = new IRStateGenerator(domain);
		
		// We can use this - actions themselves define who can use them
		AgentType at = SingleStageNormalFormGame.getAgentTypeForAllPlayers(domain);
		
		//create a world to synchronize the actions of agents in this domain and record results
		World w = new World(domain, jam, this.objectiveRF, tf, sg);
		
		for(Agent a : agents){
			a.joinWorld(w, at);
		}
		
		return w;
	}
	
	/**
	 * Returns the maximum payout possible for a given player taking a given action.
	 * @return
	 */
	public double getMaxPayout(int playerNum, int actionNum) {
		if (playerNum==0) {
			// Initiator
			if (actionNum == 0) return 0;
			else return Math.max(initiatorReward, initiatorReward+respondeeReward);
		} else {
			// Responder
			if (actionNum == 0) return initiateeReward;
			else return Math.max(initiateeReward, initiateeReward+responderReward);
		}
	}
	
	public double getMaxAbsPayout(int playerNum, int actionNum) {
		if (playerNum==0) {
			// Initiator
			if (actionNum == 0) return 0;
			else return Math.max(Math.abs(initiatorReward), Math.abs(initiatorReward+respondeeReward));
		} else {
			// Responder
			if (actionNum == 0) return Math.abs(initiateeReward);
			else return Math.max(Math.abs(initiateeReward), Math.abs(initiateeReward+responderReward));
		}
	}
	
	/**
	 * Returns the maximum payout possible for a given player.
	 * @return
	 */
	public double getMaxPayout(int playerNum) {
		return Math.max(getMaxPayout(playerNum,0),getMaxPayout(playerNum,1));
	}
	
	public double getMaxAbsPayout(int playerNum) {
		return Math.max(getMaxAbsPayout(playerNum,0),getMaxAbsPayout(playerNum,1));
	}
	
	/**
	 * Returns the maximum (absolute) payout in the game.
	 * @return
	 */
	public double getMaxAbsPayout() {
		return Math.max(getMaxAbsPayout(0),getMaxAbsPayout(1));
	}
}
