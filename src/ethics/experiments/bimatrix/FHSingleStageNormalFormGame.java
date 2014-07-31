package ethics.experiments.bimatrix;

import burlap.domain.stochasticgames.normalform.SingleStageNormalFormGame;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.AgentType;
import burlap.oomdp.stochasticgames.JointActionModel;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import burlap.oomdp.stochasticgames.World;
import burlap.oomdp.stochasticgames.common.ConstantSGStateGenerator;

public class FHSingleStageNormalFormGame extends SingleStageNormalFormGame implements DomainGenerator,GameGenerator {

	public static final String ACTION0NAME = "action0";
	public static final String ACTION1NAME = "action1";
	public static final int ACTION0NUMBER = 0;
	public static final int ACTION1NUMBER = 1;
	public static final String ATTLM = "lastMove";
	public static final int NOLASTMOVE = -999;
	
	protected double[][][] payoutMatrix;
	
	public FHSingleStageNormalFormGame(String[][] actionSets,
			double[][][] twoPlayerPayoutMatrix) {
		super(actionSets, twoPlayerPayoutMatrix);
		this.payoutMatrix = twoPlayerPayoutMatrix;
	}

	@Override
	public Domain generateDomain() {
		SGDomain domain = new SGDomain();
		
		Attribute att = new Attribute(domain, ATTPN, Attribute.AttributeType.DISC);
		att.setDiscValuesForRange(0, nPlayers-1, 1);
		
		Attribute attLM = new Attribute(domain, ATTLM, Attribute.AttributeType.DISC);
		attLM.setDiscValuesForRange(0, this.actionSets.size()-1, 1);
		
		ObjectClass player = new ObjectClass(domain, CLASSPLAYER);
		player.addAttribute(att);
		player.addAttribute(attLM);
		
		for(String aname : this.uniqueActionNames){
			new NFGSingleAction(domain, aname);
		}
		
		return domain;
	}
	
	public static State getState(SGDomain domain){
		State s = new State();
		
		ObjectClass pclass = domain.getObjectClass(CLASSPLAYER);
		Attribute pnAtt = pclass.getAttribute(ATTPN);
		int n = pnAtt.discValues.size(); //determines the number of players
		
		for(int i = 0; i < n; i++){
			ObjectInstance player = new ObjectInstance(pclass, CLASSPLAYER+i);
			player.setValue(ATTPN, i);
			player.setValue(ATTLM, NOLASTMOVE); // initialize last moves to -1
			s.addObject(player);
		}
		
		return s;
	}
	
	@Override
	public World createRepeatedGameWorld(SGDomain domain, Agent...agents){
		
		//action model for repeating single stage games; just returns to the same state
		JointActionModel jam = new FHStaticRepeatedActionModel(); 
		
		//grab the joint reward function from our bimatrix game in the more general BURLAP joint reward function interface
		JointReward jr = this.getJointRewardFunction(); 
		
		//game repeats forever unless manually stopped after T times.
		TerminalFunction tf = new NullTermination();
		
		//set up the initial state generator for the world, which for a bimatrix game is trivial
		SGStateGenerator sg = new ConstantSGStateGenerator(FHSingleStageNormalFormGame.getState(domain));
		
		//agent type defines the action set of players and OO-MDP class associated with their state information
		//in this case that's just their player number. We can use the same action type for all players, regardless of wether
		//each agent can play a different number of actions, because the actions have preconditions that prevent a player from taking actions
		//that don't belong to them.
		AgentType at = SingleStageNormalFormGame.getAgentTypeForAllPlayers(domain);
		
		
		//create a world to synchronize the actions of agents in this domain and record results
		World w = new World(domain, jam, jr, tf, sg);
		
		for(Agent a : agents){
			a.joinWorld(w, at);
		}
		
		return w;
		
	}
	
	// This is b/c I can't set the attribute of "lastMove" to a string
	public static int getActionNumber(String actionName) {
		if(actionName.equals(ACTION0NAME)) return ACTION0NUMBER;
		else if (actionName.equals(ACTION1NAME)) return ACTION1NUMBER;
		return NOLASTMOVE;
	}
	
	public static String getActionName(int actionNumber) {
		if(actionNumber==ACTION0NUMBER) return ACTION0NAME;
		else if(actionNumber==ACTION1NUMBER) return ACTION1NAME;
		return null;
	}
	
	// For a given agent, this returns the arbitrary # associated with the last state of the game
	// Order is: [(action0,action0),(action0,action1),(action1,action0),(action1,action1)]
	// Returns -1 if stuff is bad
	public static int getStateNumber(String myLastMove, String oppLastMove) {
		if (myLastMove.equals(ACTION0NAME)) {
			if (oppLastMove.equals(ACTION0NAME)) return 0;
			else if(oppLastMove.equals(ACTION1NAME)) return 1;
		}
		else if (myLastMove.equals(ACTION1NAME)) {
			if (oppLastMove.equals(ACTION0NAME)) return 2;
			else if(oppLastMove.equals(ACTION1NAME)) return 3;			
		}
		System.out.println("WARNING: invalid moves in FHSingleStageNormalFormGame.getStateNumber()");
		return -1;
	}
	
	// For a given agent, this returns the arbitrary # associated with the state of the game + the current action
	// Order is: [(action0,action0),(action0,action1),(action1,action0),(action1,action1)] - action0, [(action0,action0),(action0,action1),(action1,action0),(action1,action1)] - action1
	// Returns -1 if stuff is bad
	public static int getStateActionNumber(String curAction, int lastStateNum) {
		if (curAction.equals(ACTION0NAME)) return lastStateNum;
		else if (curAction.equals(ACTION1NAME)) return lastStateNum+4;
		System.out.println("WARNING: invalid moves in FHSingleStageNormalFormGame.getStateActionNumber()");
		return -1;
	}
	
	public int getActionNumberFromSANumber(int SANumber) {
		if(SANumber <= 3) return 0;
		else return 1;
	}
	
	/**
	 * Returns the maximum payout possible for a given player taking a given action.
	 * @return
	 */
	@Override
	public double getMaxPayout(int playerNum, int actionNum) {
		int[] profile0 = {actionNum,ACTION0NUMBER};
		int[] profile1 = {actionNum,ACTION1NUMBER};
		
		return Math.max(getPayout(playerNum,profile0),getPayout(playerNum,profile1));
	}
	
	public double getMaxAbsPayout(int playerNum, int actionNum) {
		int[] profile0 = {actionNum,ACTION0NUMBER};
		int[] profile1 = {actionNum,ACTION1NUMBER};
		
		return Math.max(Math.abs(getPayout(playerNum,profile0)),Math.abs(getPayout(playerNum,profile1)));
	}
	
	/**
	 * Returns the maximum payout possible for a given player.
	 * @return
	 */
	@Override
	public double getMaxPayout(int playerNum) {
		return Math.max(getMaxPayout(playerNum,ACTION0NUMBER),getMaxPayout(playerNum,ACTION1NUMBER));
	}
	
	public double getMaxAbsPayout(int playerNum) {
		return Math.max(getMaxAbsPayout(playerNum,ACTION0NUMBER),getMaxAbsPayout(playerNum,ACTION1NUMBER));
	}
	
	/**
	 * Returns the maximum (absolute) payout in the game.
	 * @return
	 */
	@Override
	public double getMaxAbsPayout() {
		double maxPayout = 0;
		for (int i = 0; i < this.actionSets.size(); i++) {
			for (int j = 0; j < this.actionSets.size(); j++) {
				for (int k = 0; k < this.actionSets.size(); k++) {
					if (Math.abs(this.payoutMatrix[i][j][k]) > maxPayout) maxPayout = Math.abs(this.payoutMatrix[i][j][k]);
				}
			}
		}
		return maxPayout;
	}
	
	public static double[][][] getPDPayoff() {
		return new double[][][]{{
			  {3, 0},
			  {5, 1}},
				
			  {{3, 5},
			  {0, 1}}};
	}
	
	public static double[][][] getChickenPayoff() {
		return new double[][][]{{
			  {0, -1},
			  {1, -3}},
				
			  {{0, 1},
			  {-1, -3}}};
	}
}
