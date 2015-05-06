package ethics.experiments.fssimple.specialagents;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import burlap.behavior.singleagent.QValue;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQFactory;
import burlap.behavior.stochasticgame.agents.naiveq.SGQLAgent;
import burlap.debugtools.DPrint;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.common.SinglePFTF;
import burlap.oomdp.stochasticgames.AgentFactory;
import burlap.oomdp.stochasticgames.AgentType;
import burlap.oomdp.stochasticgames.GroundedSingleAction;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import burlap.oomdp.stochasticgames.WorldGenerator;
import burlap.oomdp.stochasticgames.common.AgentFactoryWithSubjectiveReward;
import burlap.oomdp.stochasticgames.tournament.common.ConstantWorldGenerator;
import domain.stocasticgames.foragesteal.TBFSAlternatingTurnSG;
import domain.stocasticgames.foragesteal.TBFSStandardMechanics;
import domain.stocasticgames.foragesteal.TBFSStandardReward;
import domain.stocasticgames.foragesteal.TBForageSteal;
import domain.stocasticgames.foragesteal.TBForageStealFAbstraction;
import domain.stocasticgames.foragesteal.simple.FSSimple;
import ethics.ParameterizedRF;
import ethics.experiments.tbforagesteal.auxiliary.TBFSSubjectiveRF;
import ethics.experiments.tbforagesteal.matchvisualizer.MatchAnalizer.QSpace;

public class MatchAnalyzer_Sarsa {

	protected SarsaAgent				agent0;
	protected SarsaAgent				agent1;
	protected World						world;
	
	protected List <State>				agent0QQueryStates;
	protected List <State>				agent1QQueryStates;
	
	protected List<JointAction>			jointActionSequence;

	protected List <QSpace>				agent0QSequence;
	protected List <QSpace>				agent1QSequence;
	
	
	protected int						maxStages = 100;
	
	protected List <Integer>			agent0Scores;
	protected List <Integer>			agent1Scores;
	
	public static void main(String [] args){
		
		TBForageSteal gen = new TBForageSteal();
		SGDomain domain = (SGDomain) gen.generateDomain();
		
		DiscreteStateHashFactory hashingFactory = new DiscreteStateHashFactory();
		hashingFactory.setAttributesForClass(TBForageSteal.CLASSAGENT, domain.getObjectClass(TBForageSteal.CLASSAGENT).attributeList);
		
		double discount = 0.99;
		double learningRate = 0.1;
		
		AgentFactory af = new SGQFactory(domain, discount, learningRate, 1.5, hashingFactory, new TBForageStealFAbstraction());

		
		JointReward rf = new TBFSStandardReward();
		JointReward punchFavored = new TBFSSubjectiveRF(rf, new double[]{0.0, 2.0, -2.0});
		
		WorldGenerator worldGenerator = new ConstantWorldGenerator(domain, new TBFSStandardMechanics(), rf, new SinglePFTF(domain.getPropFunction(TBForageSteal.PFGAMEOVER)), new TBFSAlternatingTurnSG(domain));
		
		AgentType at = new AgentType("default", domain.getObjectClass(TBForageSteal.CLASSAGENT), domain.getSingleActions());
		
		AgentFactory afpf = new AgentFactoryWithSubjectiveReward(af, punchFavored);
		
		SarsaAgent agent0 = (SarsaAgent)af.generateAgent();
		SarsaAgent agent1 = (SarsaAgent)afpf.generateAgent();
		
		World world = worldGenerator.generateWorld();
		agent0.joinWorld(world, at);
		agent1.joinWorld(world, at);
		
		
		String [] anames = new String []{agent0.getAgentName(), agent1.getAgentName()};
		List <State> agent0QueryStates = getQueryStates(domain, anames, 0);
		List <State> agent1QueryStates = getQueryStates(domain, anames, 1);
		
		
		
		DPrint.toggleCode(world.getDebugId(), false);
		
		MatchAnalyzer_Sarsa ma = new MatchAnalyzer_Sarsa(world, agent0, agent1, agent0QueryStates, agent1QueryStates);
		ma.runMatch(1000);
		
		System.out.println(ma.numQSpaceMeasure());
		System.out.println(ma.numJARecords());
		
		System.out.println(TBForageSteal.ACTIONSTEAL + ": " + ma.getQFor(0, 0, agent0QueryStates.get(0), 
				new GroundedSingleAction(agent0.getAgentName(), domain.getSingleAction(TBForageSteal.ACTIONSTEAL), "")));
		
	}
	
	
	public static List <State> getQueryStates(Domain domain, String [] anames, int forAgent){
		
		List <State> res = new ArrayList<State>();
		
		int opponent = 1;
		if(forAgent == 1){
			opponent = 0;
		}
		
		int [] arrayFA = new int[TBForageSteal.NALTS];
		for(int i = 0; i < TBForageSteal.NALTS; i++){
			arrayFA[i] = i;
		}
		
		State s0 = TBForageSteal.getGameStartState(domain, arrayFA, forAgent);
		List <ObjectInstance> agentObs = s0.getObjectsOfTrueClass(TBForageSteal.CLASSAGENT);
		s0.renameObject(agentObs.get(0), anames[0]);
		s0.renameObject(agentObs.get(1), anames[1]);
		
		res.add(s0);
		
		
		//now do when their opponent stole from them
		State s1 = s0.copy();
		ObjectInstance opponentOb = s1.getObject(anames[opponent]);
		opponentOb.setValue(TBForageSteal.ATTPTA, 2);
		
		res.add(s1);
		
		
		
		//now do when they are in punching match
		State s2 = s0.copy();
		ObjectInstance qAgent = s2.getObject(anames[forAgent]);
		qAgent.setValue(TBForageSteal.ATTPTA, 3);
		
		opponentOb = s2.getObject(anames[opponent]);
		opponentOb.setValue(TBForageSteal.ATTPTA, 3);
		
		res.add(s2);
		
		
		return res;
		
	}
	
	
	public MatchAnalyzer_Sarsa(World world, SarsaAgent agent0, SarsaAgent agent1, List <State> agent0QQueryStates, List <State> agent1QQueryStates) {
		
		this.world = world;
		
		this.agent0 = agent0;
		this.agent1 = agent1;
		
		this.agent0QQueryStates = agent0QQueryStates;
		this.agent1QQueryStates = agent1QQueryStates;
		
		this.jointActionSequence = new ArrayList<JointAction>();
		
		this.agent0QSequence = new ArrayList<MatchAnalyzer_Sarsa.QSpace>();
		this.agent1QSequence = new ArrayList<MatchAnalyzer_Sarsa.QSpace>();
		
		this.agent0Scores = new ArrayList<Integer>();
		this.agent1Scores = new ArrayList<Integer>();
		
		//this.runMatch(maxGames);
		
		
	}
	
	
	public void setMaxStages(int maxStages){
		this.maxStages = maxStages;
	}
	
	public int numQSpaceMeasure(){
		return this.agent0QSequence.size(); //same number as agent 2
	}
	
	public int numJARecords(){
		return this.jointActionSequence.size();
	}
	
	public double getObjectiveCumulativeReward(int aid){
		if(aid == 0){
			return this.world.getCumulativeRewardForAgent(agent0.getAgentName());
		}
		else{
			return this.world.getCumulativeRewardForAgent(agent1.getAgentName());
		}
	}
	
	public QSpace getQSpace(int agentInd, int timeIndex){
		if(agentInd == 0){
			return this.agent0QSequence.get(timeIndex);
		}
		else{
			return this.agent1QSequence.get(timeIndex);
		}
	}
	
	public double getQFor(int agentInd, int timeIndex, State s, GroundedSingleAction gsa){
		return this.getQSpace(agentInd, timeIndex).getQFor(s, gsa);
	}
	
	public JointAction getJointActionAtTime(int timeIndex){
		return this.jointActionSequence.get(timeIndex);
	}
	
	
	public void runMatch(int maxGames){
		
		for(int i = 0; i < maxGames; i++){
			this.runGame();
		}
		this.recordQStatus();
		
	}
	
	protected void recordQStatus(){
		
		QSpace a0Space = new QSpace();
		for(State s : agent0QQueryStates){
			QResult res = new QResult(s, agent0.getQs(s));
			a0Space.addQResult(res);
		}
		agent0QSequence.add(a0Space);
		
		QSpace a1Space = new QSpace();
		for(State s : agent1QQueryStates){
			QResult res = new QResult(s, agent1.getQs(s));
			a1Space.addQResult(res);
		}
		agent1QSequence.add(a1Space);
		
	}
	
	
	
	
	public class QSpace{
		
		public List<QResult> qResults;
		
		public QSpace(List <QResult> qResults){
			this.qResults = qResults;
		}
		
		public QSpace(){
			this.qResults = new ArrayList<MatchAnalyzer_Sarsa.QResult>();
		}
		
		public void addQResult(QResult qr){
			this.qResults.add(qr);
		}
		
		public double getQFor(State s, GroundedSingleAction gsa){
			
			for(QResult qr : qResults){
				if(qr.s.equals(s)){
					for(QValue qe : qr.qEntries){
						if(qe.a.equals(gsa)){
							return qe.q;
						}
					}
				}
			}
			
				
			throw new RuntimeErrorException(new Error("No Q index for the queried state"));
			
		}
		
	}
	
	public class QResult{
		
		public State s;
		public List <QValue> qEntries;
		
		
		public QResult(State s, List<QValue> qEntries){
			this.s = s;
			this.qEntries = new ArrayList<QValue>(qEntries.size());
			for(QValue qe : qEntries){
				this.qEntries.add(new QValue(qe));
			}
		}
		
	}
	
	public String getCSVStringUsingSubRF(){
		

		TBForageSteal gen = new TBForageSteal();
		SGDomain d = (SGDomain) gen.generateDomain();
		
		ParameterizedRF a0rf = (ParameterizedRF)agent0.getInternalRewardFunction();
		ParameterizedRF a1rf = (ParameterizedRF)agent1.getInternalRewardFunction();
		
		double [] a0p = a0rf.getParameters();
		double [] a1p = a1rf.getParameters();
		
		String a0pRep = "R0_" + this.paramRep(a0p);
		String a1pRep = "R1_" + this.paramRep(a1p);
		
		
		StringBuffer buf = new StringBuffer();
		//header
		buf.append(a0pRep).append(",").append(a1pRep).append(",").append(a0pRep + "_S").append(",").append(a1pRep + "_S").append(",");	
		buf.append(a0pRep + "_PS").append(",").append(a1pRep + "_PS").append(",").append(a0pRep + "_PP").append(",").append(a1pRep + "_PP").append("\n");
		
		
		for(int i = 0; i < agent0QSequence.size(); i++){
			if(i < agent0Scores.size()){
				buf.append(agent0Scores.get(i)).append(",").append(agent1Scores.get(i)).append(",");
			}
			else{
				buf.append(",,");
			}
			QSpace a0q = agent0QSequence.get(i);
			QSpace a1q = agent1QSequence.get(i);
			
			buf.append(a0q.getQFor(agent0QQueryStates.get(0), new GroundedSingleAction(agent0.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONSTEAL), ""))).append(",");
			buf.append(a1q.getQFor(agent1QQueryStates.get(0), new GroundedSingleAction(agent1.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONSTEAL), ""))).append(",");
			
			buf.append(a0q.getQFor(agent0QQueryStates.get(1), new GroundedSingleAction(agent0.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONPUNCH), ""))).append(",");
			buf.append(a1q.getQFor(agent1QQueryStates.get(1), new GroundedSingleAction(agent1.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONPUNCH), ""))).append(",");
			
			buf.append(a0q.getQFor(agent0QQueryStates.get(2), new GroundedSingleAction(agent0.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONPUNCH), ""))).append(",");
			buf.append(a1q.getQFor(agent1QQueryStates.get(2), new GroundedSingleAction(agent1.getAgentName(), d.getSingleAction(TBForageSteal.ACTIONPUNCH), ""))).append("\n");
			
			
			
		}
		
		return buf.toString();
		
	}
	
	
	public void runGame(){
		
		TerminalFunction tf = this.world.getTF();
		
		agent0.gameStarting();
		agent1.gameStarting();
		
		this.world.generateNewCurrentState();
		int agentToScore = this.whichAgentToScoreForStartState(this.world.getCurrentWorldState());
		String agentToScoreName = agent0.getAgentName();
		if(agentToScore == 1){
			agentToScoreName = agent1.getAgentName();
		}

		int score = 0;
		

		int t = 0;
		while(!tf.isTerminal(this.world.getCurrentWorldState()) && t < this.maxStages){
			this.recordQStatus();
			State curState = this.world.getCurrentWorldState();
			this.world.runStage();
			this.jointActionSequence.add(this.world.getLastJointAction());
			JointAction ja = this.world.getLastJointAction();
			score += this.actionScore(curState, agentToScoreName, ja, t);
			t++;
		}
		
		
		agent0.gameTerminated();
		agent1.gameTerminated();
		
		if(agentToScore == 0){
			this.agent0Scores.add(score);
		}
		else{
			this.agent1Scores.add(score);
		}
		
		
	}
	
	

	
	protected int actionScore(State s, String agentToScore, JointAction ja, int timeStep){
		
		if(timeStep > 2){
			return 0;
		}
		
		for(GroundedSingleAction gsa : ja){
			String aname = gsa.action.actionName;
			if(aname.equals(TBForageSteal.ACTIONSTEAL) || aname.equals(TBForageSteal.ACTIONPUNCH)){
				return 1;
			}
		}
		
		return 0;
	}
	
	
	protected String paramRep(double [] params){
		
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < params.length; i++){
			if(i > 0){
				buf.append("_");
			}
			buf.append(params[i]);
		}
		
		
		return buf.toString();
		
	}
	
	protected int whichAgentToScoreForStartState(State s){
		
		ObjectInstance a0Ob = s.getObject(agent0.getAgentName());
		int pn = a0Ob.getDiscValForAttribute(FSSimple.ATTPN);
		if(pn == 0){
			if(FSSimple.isRootNode(s)){
				return 0;
			}
			else{
				return 1;
			}
		}
		else{
			if(FSSimple.isRootNode(s)){
				return 1;
			}
			else{
				return 0;
			}
		}
		
	}
}
