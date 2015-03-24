package ethics.experiments.adam.game;

import java.util.List;
import java.util.Map;

import burlap.behavior.singleagent.ValueFunctionInitialization;
import burlap.oomdp.stochasticgames.JointActionModel;

public interface SP_StateSpace {
	public JointActionModel getJAM();
	public List<String> getStateNames();
	public List<String> getThiefStates();
	public List<String> getPunisherStates();
	public String getFirstState();
	public Map<String,Double> generateParamMap(double[] params);
	public ValueFunctionInitialization getCoopEquilibriumQInit(double[] payoffs, double discount);
	public int getNumStates();
}
