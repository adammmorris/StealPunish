package ethics.experiments.adam.game;

import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.oomdp.core.State;

public class SP_PseudoTerm implements StateConditionTest {

	@Override
	public boolean satisfies(State s) {
		return SP_Domain.isThiefsTurn(s);
	}

}

