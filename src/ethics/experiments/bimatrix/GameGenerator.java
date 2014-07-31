package ethics.experiments.bimatrix;

import burlap.oomdp.stochasticgames.Agent;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;

public interface GameGenerator {
public JointReward getJointRewardFunction();
public double getMaxPayout(int playerNum,int actionNum);
public double getMaxPayout(int playerNum);
public double getMaxAbsPayout();
public World createRepeatedGameWorld(SGDomain domain, Agent...agents);
}
