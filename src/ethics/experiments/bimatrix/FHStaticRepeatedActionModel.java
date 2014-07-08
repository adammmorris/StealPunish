package ethics.experiments.bimatrix;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointActionModel;

public class FHStaticRepeatedActionModel extends JointActionModel {

	@Override
	public List<TransitionProbability> transitionProbsFor(State s,
			JointAction ja) {
		List <TransitionProbability> res = new ArrayList<TransitionProbability>();
		TransitionProbability tp = new TransitionProbability(s.copy(), 1.);
		res.add(tp);
		
		return res;
	}

	@Override
	protected State actionHelper(State s, JointAction ja) {
		State sp = s.copy();
		List<String> agents = ja.getAgentNames();
		String agent0name = agents.get(0);
		String agent1name = agents.get(1);
		
		ObjectInstance agent0 = sp.getObject(agent0name);
		ObjectInstance agent1 = sp.getObject(agent1name);
		
		agent0.setValue(FHSingleStageNormalFormGame.ATTLM, FHSingleStageNormalFormGame.getActionNumber(ja.actions.get(agent0name).actionName()));
		agent1.setValue(FHSingleStageNormalFormGame.ATTLM, FHSingleStageNormalFormGame.getActionNumber(ja.actions.get(agent1name).actionName()));
		
		return sp;
	}

}
