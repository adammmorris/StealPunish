package ethics.experiments.adam.game;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Attribute.AttributeType;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SingleAction;


/**
 * This is the general domain for the StealPunish game. Doesn't assume anything about symmetry or state space.
 * Has back-turned mechanics.
 */
public class SP_Domain implements DomainGenerator {

	public static final String				ATTPN = "playerNum";
	public static final String				ATTSTATENAME = "stateName";
	public static final String				ATTBACKTURNED = "backIsTurned";
	public static final String				ATTISTHIEFTURN = "isThiefTurn";
	
	public static final String				CLASSPLAYER = "player";
	public static final String				CLASSSTATENODE = "stateNode";
	
	public static final String				ACTIONSTEAL = "steal";
	public static final String				ACTIONPUNISH = "punish";
	public static final String				ACTIONDONOTHING = "nothing";
	
	// Player numbers
	public static final int					THIEFPN = 0;
	public static final int					PUNISHERPN = 1;
	
	private List<String> 					stateNames;
	
	public SP_Domain(int numStates) {
		this.stateNames = new ArrayList<String>();
		for (int i = 0; i < numStates; i++) {
			stateNames.add(String.valueOf(i));
		}
	}
	
	public SP_Domain(List<String> stateNames) {
		this.stateNames = stateNames;
	}
	
	@Override
	public Domain generateDomain() {
		
		SGDomain domain = new SGDomain();
		
		// Player attributes: number, back-turned, and is-turn
		Attribute pnAtt = new Attribute(domain, ATTPN, Attribute.AttributeType.DISC);
		pnAtt.setDiscValuesForRange(0, 1, 1);
		
		Attribute btAtt = new Attribute(domain, ATTBACKTURNED, AttributeType.DISC);
		btAtt.setDiscValuesForRange(0, 1, 1);
		
		ObjectClass playerClass = new ObjectClass(domain, CLASSPLAYER);
		playerClass.addAttribute(pnAtt);
		playerClass.addAttribute(btAtt);

		Attribute snAtt = new Attribute(domain, ATTSTATENAME, Attribute.AttributeType.DISC);
		snAtt.setDiscValues(stateNames);

		Attribute pitAtt = new Attribute(domain, ATTISTHIEFTURN, Attribute.AttributeType.DISC);
		pitAtt.setDiscValuesForRange(0, 1, 1);
		
		ObjectClass stateNodeClass = new ObjectClass(domain, CLASSSTATENODE);
		stateNodeClass.addAttribute(snAtt);
		stateNodeClass.addAttribute(pitAtt);
		
		new StealAction(domain);
		new PunishAction(domain);
		new DoNothingAction(domain);
		
		return domain;
	}
	
	
	public static State getInitialState(Domain domain, String player0Name, String player1Name, int backTurned, String firstState){
		
		State s = new State();
		
		ObjectInstance player0 = new ObjectInstance(domain.getObjectClass(CLASSPLAYER), player0Name);
		ObjectInstance player1 = new ObjectInstance(domain.getObjectClass(CLASSPLAYER), player1Name);
		
		player0.setValue(ATTPN, THIEFPN);
		player0.setValue(ATTBACKTURNED, 0);
		player1.setValue(ATTPN, PUNISHERPN);
		player1.setValue(ATTBACKTURNED, backTurned);
		
		s.addObject(player0);
		s.addObject(player1);
		
		ObjectInstance snode = new ObjectInstance(domain.getObjectClass(CLASSSTATENODE), CLASSSTATENODE);
		snode.setValue(ATTSTATENAME, firstState);
		snode.setValue(ATTISTHIEFTURN, 1);
		
		s.addObject(snode);
		
		return s;
		
	}
	
	public static void setStateNode(State s, int sn){
		ObjectInstance sno = s.getFirstObjectOfClass(CLASSSTATENODE);
		sno.setValue(ATTSTATENAME, sn);
	}
	
	public static void setStateNode(State s, String sn){
		ObjectInstance sno = s.getFirstObjectOfClass(CLASSSTATENODE);
		sno.setValue(ATTSTATENAME, sn);
	}
	
	public static void setThiefTurn(State s, int isThiefTurn) {
		ObjectInstance sno = s.getFirstObjectOfClass(CLASSSTATENODE);
		sno.setValue(ATTISTHIEFTURN, isThiefTurn);
	}
	
	public static int stateNode(State s){
		ObjectInstance sn = s.getFirstObjectOfClass(CLASSSTATENODE);
		int n = sn.getDiscValForAttribute(CLASSSTATENODE);
		return n;
	}
	
	public static boolean isThiefsTurn(State s) {
		ObjectInstance sn = s.getFirstObjectOfClass(CLASSSTATENODE);
		int thiefsTurn = sn.getDiscValForAttribute(ATTISTHIEFTURN);
		if (thiefsTurn==1) return true;
		return false;
	}
	
	public class StealAction extends SingleAction{

		public StealAction(SGDomain d) {
			super(d, ACTIONSTEAL);
		}

		@Override
		public boolean isApplicableInState(State s, String actingAgent,
				String[] params) {
			
			ObjectInstance p = s.getObject(actingAgent);
			ObjectInstance sn = s.getFirstObjectOfClass(CLASSSTATENODE);
			if(p.getDiscValForAttribute(ATTPN) == THIEFPN && sn.getDiscValForAttribute(ATTISTHIEFTURN) == 1){
				return true;
			}
			
			return false;
		}
		
		
		
	}
	
	public class PunishAction extends SingleAction{

		public PunishAction(SGDomain d) {
			super(d, ACTIONPUNISH);
		}

		@Override
		public boolean isApplicableInState(State s, String actingAgent,
				String[] params) {
			
			ObjectInstance p = s.getObject(actingAgent);
			ObjectInstance sn = s.getFirstObjectOfClass(CLASSSTATENODE);
			if(p.getDiscValForAttribute(ATTPN) == PUNISHERPN && sn.getDiscValForAttribute(ATTISTHIEFTURN) == 0){
				return true;
			}
			
			
			return false;
		}
		
		
		
	}
	
	public class DoNothingAction extends SingleAction{

		public DoNothingAction(SGDomain d) {
			super(d, ACTIONDONOTHING);
		}

		@Override
		public boolean isApplicableInState(State s, String actingAgent,
				String[] params) {
			return true;
		}
		
		
	}
	
	/*public static void main(String [] args){
		
		StealPunish_Base gen = new StealPunish_Base(3);
		SGDomain domain = (SGDomain)gen.generateDomain();
		//JointActionModel jam = new FSSimpleJAM();
		//JointActionModel jam = new FSSimpleBTJAM(0.5);
		JointActionModel jam = new FSSimpleBTSJAM(0.5, 0.5);
		//JointReward r = new FSSimpleJR();
		JointReward r = new FSSubjectiveRF(new FSSimplePOJR(1., -0.5, -2.5, 0.));
		((FSSubjectiveRF)r).setParameters(new double[]{0.,0.});
		
		State s = FSSimple.getInitialState(domain, "player0", "player1", 0, 0);
		
		SGTerminalExplorer exp = new SGTerminalExplorer(domain, jam);
		exp.setTrackingRF(r);
		
		for(int i = 0; i < gen.nfalts; i++){
			exp.addActionShortHand("f"+i, ACTIONFORAGEBASE+i);
		}
		exp.addActionShortHand("s", ACTIONSTEAL);
		exp.addActionShortHand("p", ACTIONPUNISH);
		exp.addActionShortHand("n", ACTIONDONOTHING);
		
		exp.exploreFromState(s);
		
	}*/

}