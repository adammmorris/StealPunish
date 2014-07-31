package ethics.experiments.ir;

import java.util.List;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;

public class IRStateGenerator extends SGStateGenerator {

	private SGDomain domain;
	
	public IRStateGenerator(SGDomain domain) {
		this.domain = domain;
	}
	
	@Override
	public State generateState(List<Agent> agents) {
		State s = new State();
		ObjectInstance a0 = this.getAgentObjectInstance(agents.get(0));
		a0.setValue(IRGame.ATTPN, 0);
		//a0.setValue(IRGame.ATTBACKTURNED, 0);
		s.addObject(a0);
		
		ObjectInstance a1 = this.getAgentObjectInstance(agents.get(1));
		a1.setValue(IRGame.ATTPN, 1);
		//a1.setValue(IRGame.ATTBACKTURNED, 0);
		s.addObject(a1);
		
		/*ObjectInstance f = new ObjectInstance(domain.getObjectClass(IRGame.CLASSFALT), IRGame.CLASSFALT+IRGame.FORAGEALT);
		f.setValue(IRGame.ATTFA,IRGame.FORAGEALT);
		s.addObject(f);*/

		ObjectInstance snode = new ObjectInstance(domain.getObjectClass(IRGame.CLASSSTATENODE), IRGame.CLASSSTATENODE);
		snode.setValue(IRGame.ATTSTATENODE, 0);
		s.addObject(snode);
		
		return s;
	}

}
