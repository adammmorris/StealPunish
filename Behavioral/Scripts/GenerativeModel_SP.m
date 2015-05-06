%% GenerativeModel_SP

%% Params
% x: [lr, temp, gamma, steal bias, punish bias]
% memory = memory of state space

%% Outputs
% results: [id roles oppChoices choices reward1 reward2 matchRound]

function [results] = GenerativeModel_SP(x, memory, epsilon, numAgents)

if nargin < 4, numAgents = 1; end
if nargin < 3, epsilon = .1; end
if nargin < 2, memory = 2; end
if nargin < 1, x=[.25 1 .95 0 0]; end

%% ENVIRONMENT PARAMETERS

% Info about his board
numMatches = 8;
numRoundsPerMatch = 50;
numRounds = numMatches*numRoundsPerMatch;
numStates = 2^(memory+1);
numActions = 2;

s = 2;
sprime = -2;
c = -1;
p = -5;

results = zeros(numAgents*numRounds,7);

%% AGENT PARAMETERS

if size(x,1) == numAgents
    lrs = x(:,1);
    temps = x(:,2);
    gammas = x(:,3);
    steal_biases = x(:,4);
    punish_biases = x(:,5);
elseif size(x,1) == 1
    lrs = repmat(x(1),numAgents,1);
    temps = repmat(x(2),numAgents,1);
    gammas = repmat(x(3),numAgents,1);
    steal_biases = repmat(x(4),numAgents,1);
    punish_biases = repmat(x(5),numAgents,1);
end

% Set up Q0
Q0 = zeros(numStates,numActions);
init_bonus = 0;

%% PLAY THE BOARD

% Roles
ROLE_THIEF = 0;
ROLE_PUNISHER = 1;

% Actions
CHOICE_NOTHING = 0;
CHOICE_ACTION = 1;
actionLabels = [CHOICE_NOTHING CHOICE_ACTION];

% Initial states
lastActions0 = zeros(memory,1);

resultCounter = 1;

for thisAgent = 1:numAgents
    roles = round(rand(numMatches,1));
    lr = lrs(thisAgent,1);
    temp = temps(thisAgent,1);
    gamma = gammas(thisAgent,1);
    steal_bias = steal_biases(thisAgent,1);
    punish_bias = punish_biases(thisAgent,1);
    
    for thisMatch = 1:numMatches
        role = roles(thisMatch);
        oppRole = -role+1;
        
        Q = Q0;
        lastActions = lastActions0;
        
        if role==ROLE_THIEF
            curState = getStateNum(role,lastActions);
        else
            curState = getStateNum(oppRole,lastActions); % start with opponent
        end
        
        % Initialize better Q values
        if role==ROLE_THIEF
            states = [getStateNum(role,[0 0]) getStateNum(role,[0 1]) getStateNum(role,[1 0]) getStateNum(role,[1 1])];
        else
            states = [getStateNum(role,[1 0]) getStateNum(role,[1 1])];
        end
        Q(states,actionLabels==CHOICE_ACTION) = Q(states,actionLabels==CHOICE_ACTION)+init_bonus;
        
        for thisRound = 1:numRoundsPerMatch
            % FIRST TURN
            if role == ROLE_THIEF
                % Do my action
                probs = exp(temp*Q(curState,:))/sum(exp(temp*Q(curState,:)));
                myAction = randsample(numActions,1,true,probs);
%                 if rand() < epsilon
%                     if Q(curState,1)==Q(curState,2),myAction=randsample(numActions,1);
%                     else [~,myAction]=min(Q(curState,:)); end
%                 else
%                     [~,myAction] = max(Q(curState,:));
%                 end
                
                reward1 = (s+steal_bias)*(actionLabels(myAction)==CHOICE_ACTION);
                
                lastActions = [actionLabels(myAction) lastActions(1:(end-1))];
                newState = getStateNum(oppRole,lastActions);
                
                % Update
                delta = reward1 + gamma*max(Q(newState,:)) - Q(curState,myAction);
                Q(curState,myAction) = Q(curState,myAction) + lr*delta;
                
                %             for i = 1:memory
                %                 Q(curState,lastActions(i)) = Q(curState,lastActions(i)) + (elig^(i-1))*lr*delta;
                %             end
            else
                oppAction = find(actionLabels==CHOICE_ACTION);
                lastActions = [actionLabels(oppAction) lastActions(1:(end-1))];
                newState = getStateNum(role,lastActions);
                
                reward1 = sprime;
                
                % In opponent states, both actions are the same
                for i = 1:2
                    Q(curState,i) = Q(curState,i) + lr*(reward1 + gamma*max(Q(newState,:)) - Q(curState,i));
                end
            end
            
            curState = newState;
            
            % SECOND TURN
            if role == ROLE_PUNISHER
                % Do my action & update
                probs = exp(temp*Q(curState,:))/sum(exp(temp*Q(curState,:)));
                myAction = randsample(numActions,1,true,probs);
%                 if rand() < epsilon
%                     if Q(curState,1)==Q(curState,2),myAction=randsample(numActions,1);
%                     else [~,myAction]=min(Q(curState,:)); end
%                 else
%                     [~,myAction] = max(Q(curState,:));
%                 end
                
                reward2 = c*(actionLabels(myAction)==CHOICE_ACTION)+punish_bias*(actionLabels(myAction)==CHOICE_ACTION & lastActions(1)==CHOICE_ACTION);
                
                lastActions = [actionLabels(myAction) lastActions(1:(end-1))];
                newState = getStateNum(oppRole,lastActions);
                
                delta = reward2 + gamma*max(Q(newState,:)) - Q(curState,myAction);
                Q(curState,myAction) = Q(curState,myAction) + lr*delta;
                %             for i = 1:memory
                %                 Q(curState,lastActions(i)) = Q(curState,lastActions(i)) + (elig^(i-1))*lr*delta;
                %             end
            else
                oppAction = myAction;
                lastActions = [actionLabels(oppAction) lastActions(1:(end-1))]; % punisher does whatever simulated thief did (action or nothing)
                newState = getStateNum(role,lastActions);
                
                reward2 = p*(lastActions(1) == CHOICE_ACTION);
                
                % In opponent states, both actions are the same
                for i = 1:2
                    Q(curState,i) = Q(curState,i) + lr*(reward2 + gamma*max(Q(newState,:)) - Q(curState,i));
                end
            end
            
            curState = newState;
            
            % Record results
            results(resultCounter,:) = [thisAgent role actionLabels(oppAction) actionLabels(myAction) reward1 reward2 thisRound];
            resultCounter = resultCounter + 1;
        end
    end
end
end
