package ethics.experiments.adam.game.symfm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burlap.behavior.singleagent.ValueFunctionInitialization;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointActionModel;
import ethics.experiments.adam.game.SP_Domain;
import ethics.experiments.adam.game.SP_StateSpace;

/**
 * This is the "symmetric finite-memory" state space.
 * Memories must be equal for both players right now.
 * State names are based on memory. To parse states, read backwards (right to left) according to the letters in this class.
 * For example, "spnq" means that (a) players have memory 4, (b) it's the thief's turn,
 * and (c) what happened in the last four turns was the thief stole, the punisher punished, the thief didn't steal,
 * and then the punisher didn't punish.
 * 
 * The starting state is the thief's turn, where nobody has done anything (e.g. "nqnq").
 */
public class SymFM implements SP_StateSpace {

	// For state naming
	public static final String LETTERSTEAL = "s";
	public static final String LETTERNOSTEAL = "n";
	public static final String LETTERPUNISH = "p";
	public static final String LETTERNOPUNISH = "q";
	
	private int thiefMemory;
	private int punisherMemory;
	private double probBT;
	private List<String> stateNames;
	private List<String> thiefStates;
	private List<String> punisherStates;
	
	public SymFM(int thiefMemory, int punisherMemory, double probBT) {
		this.thiefMemory = thiefMemory;
		this.punisherMemory = punisherMemory;
		this.probBT = probBT;
		this.generateStateNames();
	}
	
	@Override
	public JointActionModel getJAM() {
		return new SymFM_JAM(thiefMemory, punisherMemory, probBT);
	}

	@Override
	public List<String> getStateNames() {
		return this.stateNames;
	}
	
	// States on which it's the thief's turn
	@Override
	public List<String> getThiefStates() {
		return this.thiefStates;
	}
	
	// Punisher's turn
	@Override
	public List<String> getPunisherStates() {
		return this.punisherStates;
	}
	
	private void generateStateNames() {
		int numStates = (int) (Math.pow(2., (double)thiefMemory)+Math.pow(2., (double)punisherMemory));
		stateNames = new ArrayList<String>(numStates);
		thiefStates = new ArrayList<String>();
		punisherStates = new ArrayList<String>();

		// Do thief
		ArrayList<String> temp = new ArrayList<String>();
		int curSize = 0;
		int prevSize = 0;
		
		temp.add(SymFM.LETTERNOPUNISH);
		
		if (thiefMemory > 1) {
			temp.add(SymFM.LETTERPUNISH);
			
			int flip = 1;
			String letter1 = null;
			String letter2 = null;
			for (int i = 1; i <= (thiefMemory-1); i++) {
				if (flip == 1) {
					letter1 = SymFM.LETTERNOSTEAL;
					letter2 = SymFM.LETTERSTEAL;
					flip = 0;
				} else {
					letter1 = SymFM.LETTERNOPUNISH;
					letter2 = SymFM.LETTERPUNISH;
					flip = 1;
				}
				
				curSize = temp.size();
				for (int j = prevSize; j < curSize; j++) {
					if (i == (thiefMemory - 1)) {
						stateNames.add(letter1 + temp.get(j));
						stateNames.add(letter2 + temp.get(j));
						thiefStates.add(letter1 + temp.get(j));
						thiefStates.add(letter2 + temp.get(j));
					} else {
						temp.add(letter1 + temp.get(j));
						temp.add(letter2 + temp.get(j));
					}
				}
				prevSize = curSize;
			}
		} else {
			stateNames.add(SymFM.LETTERNOPUNISH);
			stateNames.add(SymFM.LETTERPUNISH);
			thiefStates.add(SymFM.LETTERNOPUNISH);
			thiefStates.add(SymFM.LETTERPUNISH);
		}
		
		// Do punisher
		ArrayList<String> temp2 = new ArrayList<String>();
		temp2.add(SymFM.LETTERNOSTEAL);
		prevSize = 0;
		
		if (punisherMemory > 1) {
			temp2.add(SymFM.LETTERSTEAL);
			
			int flip = 1;
			String letter1 = null;
			String letter2 = null;
			for (int i = 1; i <= (punisherMemory-1); i++) {
				if (flip == 0) {
					letter1 = SymFM.LETTERNOSTEAL;
					letter2 = SymFM.LETTERSTEAL;
					flip = 1;
				} else {
					letter1 = SymFM.LETTERNOPUNISH;
					letter2 = SymFM.LETTERPUNISH;
					flip = 0;
				}
				
				curSize = temp2.size();
				for (int j = prevSize; j < curSize; j++) {
					if (i == (punisherMemory - 1)) {
						stateNames.add(letter1 + temp2.get(j));
						stateNames.add(letter2 + temp2.get(j));
						punisherStates.add(letter1 + temp2.get(j));
						punisherStates.add(letter2 + temp2.get(j));
					} else {
						temp2.add(letter1 + temp2.get(j));
						temp2.add(letter2 + temp2.get(j));
					}
				}
				prevSize = curSize;
			}
		} else {
			stateNames.add(SymFM.LETTERNOSTEAL);
			stateNames.add(SymFM.LETTERSTEAL);
			punisherStates.add(SymFM.LETTERNOSTEAL);
			punisherStates.add(SymFM.LETTERSTEAL);
		}
	}

	@Override
	public String getFirstState() {
		String out = SymFM.LETTERNOPUNISH;
		int flip = 1;
		for (int i = 0; i < (thiefMemory-1); i++) {
			if (flip == 0) {
				out = SymFM.LETTERNOPUNISH + out;
				flip = 1;
			} else {
				out = SymFM.LETTERNOSTEAL + out;
				flip = 0;
			}
		}
		return out;
	}

	// Currently, this only takes a params array of size 2:
	// steal bias & punish bias
	// And it applies the steal bias to all thief states, and
	// the punish bias only to punisher states in which the punisher
	// was just stolen from.
	@Override
	public Map<String,Double> generateParamMap(double[] params) {
		int numStates = this.stateNames.size();
		HashMap<String,Double> paramMap = new HashMap<String,Double>(numStates);
		
		if (params.length == 2) {
			for (int i = 0; i < numStates; i++) {
				String stateName = this.stateNames.get(i);
				//if (stateName.endsWith(SymFM.LETTERPUNISH) || stateName.endsWith(SymFM.LETTERNOPUNISH)) {
				if (this.thiefStates.contains(stateName)) {
					paramMap.put(stateName, params[0]);
				} else if (this.punisherStates.contains(stateName) && stateName.endsWith(SymFM.LETTERSTEAL)) {
					paramMap.put(stateName, params[1]);
				} else {
					paramMap.put(stateName, 0.);
				}
			}
		} else if (params.length == 4) {
			for (int i = 0; i < numStates; i++) {
				String stateName = this.stateNames.get(i);
				//if (stateName.endsWith(SymFM.LETTERPUNISH) || stateName.endsWith(SymFM.LETTERNOPUNISH)) {
				if (this.thiefStates.contains(stateName)) {
					if (stateName.endsWith(SymFM.LETTERNOPUNISH)) {
						paramMap.put(stateName, params[0]);
					} else {
						paramMap.put(stateName, params[1]);
					}
				} else if (this.punisherStates.contains(stateName)) {
					if (stateName.endsWith(SymFM.LETTERNOSTEAL)) {
						paramMap.put(stateName, params[2]);
					} else {
						paramMap.put(stateName, params[3]);
					}
				}
			}
		} else {
			throw new Error("Only 2 or 4 params are supported right now.");
		}
		
		return paramMap;
	}
	
	@Override
	public ValueFunctionInitialization getCoopEquilibriumQInit(double[] payoffs, double discount) {
		return new SymFM_QInit(payoffs,discount);
	}
	
	private class SymFM_QInit implements ValueFunctionInitialization {

		private double stealer;
		private double stealee;
		private double punisher;
		private double punishee;
		private double discount;
		
		public SymFM_QInit(double[] payoffs, double discount) {
			stealer = payoffs[0];
			stealee = payoffs[1];
			punisher = payoffs[2];
			punishee = payoffs[3];
			this.discount = discount;
		}
		
		@Override
		public double value(State s) {
			// TODO Auto-generated method stub
			return 0;
		}

		// This assumes: thiefs never steal, and punisher always punish theft.
		@Override
		public double qValue(State s, AbstractGroundedAction a) {
			ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
			String stateName = sn.getStringValForAttribute(SP_Domain.ATTSTATENAME);
			int isThiefTurn = sn.getDiscValForAttribute(SP_Domain.ATTISTHIEFTURN);
			GroundedSingleAction gsa = (GroundedSingleAction)a;

			if (isThiefTurn == 1) {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONSTEAL) && stateName.endsWith(LETTERPUNISH)) {
					//return stealer+discount*punishee;
					return -5;
				} else {
					return 0;
				}
			} else {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONPUNISH) && stateName.endsWith(LETTERSTEAL)) {
					//return punisher;
					return 5;
				} else {
					return 0;
				}
			}
		}
	}
	
	public int getNumStates() {
		return this.stateNames.size();
	}
}
