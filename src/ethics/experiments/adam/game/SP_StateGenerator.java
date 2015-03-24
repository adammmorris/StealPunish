package ethics.experiments.adam.game;

import java.util.List;

import burlap.debugtools.RandomFactory;
import burlap.oomdp.core.State;
import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.SGStateGenerator;

public class SP_StateGenerator extends SGStateGenerator {

	SGDomain domain;
	double probBT;
	private String firstState;
	
	public SP_StateGenerator(SGDomain domain, double probBackTurned, String firstState){
		this.domain = domain;
		this.probBT = probBackTurned;
		this.firstState = firstState;
	}
	
	@Override
	public State generateState(List<Agent> agents) {
		
		String p0Name = agents.get(0).getAgentName();
		String p1Name = agents.get(1).getAgentName();
		
		int bt = 0;
		double r = RandomFactory.getMapped(0).nextDouble();
		if(r < this.probBT){
			bt = 1;
		}
		
		State s = SP_Domain.getInitialState(this.domain, p0Name, p1Name, bt, firstState);
		
		return s;
	}

}