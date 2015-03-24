package ethics.experiments.adam.game.pavlov;

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
 * This is the Pavlov space (see James & Michael's game).
 * It has four states: A,B,C,D (see James & Michael's diagram).
 * Starts on State A.
 */
public class Pavlov implements SP_StateSpace {

	// For state naming
	public static final String STATEA = "A";
	public static final String STATEB = "B";
	public static final String STATEC = "C";
	public static final String STATED = "D";
	
	private List<String> stateNames;
	private List<String> thiefStates;
	private List<String> punisherStates;
	
	public Pavlov() {
		this.generateStateNames();
	}
	
	@Override
	public JointActionModel getJAM() {
		return new Pavlov_JAM();
	}

	@Override
	public List<String> getStateNames() {
		return this.stateNames;
	}
	
	@Override
	public List<String> getThiefStates() {
		return this.thiefStates;
	}
	
	@Override
	public List<String> getPunisherStates() {
		return this.punisherStates;
	}
	
	private void generateStateNames() {
		this.stateNames = new ArrayList<String>(4);
		this.thiefStates = new ArrayList<String>(2);
		this.punisherStates = new ArrayList<String>(2);
		
		this.stateNames.add(STATEA);
		this.stateNames.add(STATEB);
		this.stateNames.add(STATEC);
		this.stateNames.add(STATED);
		
		this.thiefStates.add(STATEA);
		this.thiefStates.add(STATEB);
		
		this.punisherStates.add(STATEC);
		this.punisherStates.add(STATED);
	}

	@Override
	public String getFirstState() {
		return STATEA;
	}

	// Currently, this only takes a params array of size 2: steal bias & punish bias
	@Override
	public Map<String,Double> generateParamMap(double[] params) {
		int numStates = this.stateNames.size();
		HashMap<String,Double> paramMap = new HashMap<String,Double>(numStates);
		
		if (params.length == 2) {
			paramMap.put(STATEA, params[0]);
			paramMap.put(STATEB, params[0]);
			paramMap.put(STATEC, params[1]);
			paramMap.put(STATED, params[1]);
		} else if (params.length == 3) {
			paramMap.put(STATEA, params[0]);
			paramMap.put(STATEB, params[0]);
			paramMap.put(STATEC, params[1]);
			paramMap.put(STATED, params[1]);
		} else if (params.length == 4) {
			paramMap.put(STATEA, params[0]);
			paramMap.put(STATEB, params[1]);
			paramMap.put(STATEC, params[2]);
			paramMap.put(STATED, params[3]);
		} else {
			throw new Error("Only 2 or 4 params are supported right now.");
		}
		
		return paramMap;
	}
	
	@Override
	public ValueFunctionInitialization getCoopEquilibriumQInit(double[] payoffs, double discount) {
		return new Pavlov_QInit(payoffs, discount);
	}
	
	private class Pavlov_QInit implements ValueFunctionInitialization {

		// Rewards
		private double stealer;
		private double stealee;
		private double punisher;
		private double punishee;
		private double discount;
		
		public Pavlov_QInit(double[] payoffs, double gamma) {
			this.stealer = payoffs[0];
			this.stealee = payoffs[1];
			this.punisher = payoffs[2];
			this.punishee = payoffs[3];
			this.discount = gamma;
		}
		
		@Override
		public double value(State s) {
			// TODO Auto-generated method stub
			return 0;
		}

		// This is taken directly from Michael & James's graph
		@Override
		public double qValue(State s, AbstractGroundedAction a) {
			ObjectInstance sn = s.getFirstObjectOfClass(SP_Domain.CLASSSTATENODE);
			String stateName = sn.getStringValForAttribute(SP_Domain.ATTSTATENAME);
			GroundedSingleAction gsa = (GroundedSingleAction)a;

			if (stateName == Pavlov.STATEA) {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONDONOTHING)) {
					return 0;
				} else {
					return stealer+discount*punishee;
				}
			} else if (stateName == Pavlov.STATEB) {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONDONOTHING)) {
					return discount*punishee;
				} else {
					return stealer;
				}
			} else if (stateName == Pavlov.STATEC) {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONDONOTHING)) {
					return 0;
				} else {
					return punisher+discount*stealee;
				}
			} else if (stateName == Pavlov.STATED) {
				if (gsa.action.actionName.equals(SP_Domain.ACTIONDONOTHING)) {
					return discount*stealee;
				} else {
					return punisher;
				}
			}
			
			return 0;
		}
	}
	
	public int getNumStates() {
		return this.stateNames.size();
	}
}