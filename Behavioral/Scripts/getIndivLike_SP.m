%% getIndivLike_SP

%% Params
% x: [lr, temp, steal bias, punish bias, gamma]
% roles: 1 = thief, 2 = punisher
% opponents: 'pun','suc','con','non'
% myActions: 1 = do nothing, 2 = steal/punish
% rewards1 = rewards from first half of round
% rewards2 = rewards from second half of round
% memory = memory of state space

function [likelihood] = getIndivLike_SP(x, roles, opponents, myActions, rewards1, rewards2, memory)

%% ENVIRONMENT PARAMETERS

% Info about his board
numRounds = length(myActions);
numRoles = 2;
numStates = 2^(memory+1);
numActions = 2;

% Data variables:
% id, A1, S2, A2, Re

%% AGENT PARAMETERS

lr = x(1);
temp = x(2);
steal_bias = x(3);
punish_bias = x(4);
gamma = x(5);

% Set up initial state/action preference matrix (actor) and initial value
%   matrix (critic)
Q0 = zeros(numStates,numActions,numRoles);

%% PLAY THE BOARD

% Calculate likelihoods
likelihood = 0;
Q = Q0;

% Roles
ROLE_THIEF = 1;
ROLE_PUNISHER = 2;

% Actions
ACTION_S = 2;
ACTION_NS = 1;
ACTION_P = 2;
ACTION_NP = 1;

% Opponents
OPPNAME_APT = 'pun';
OPPNAME_NP = 'suc';
OPPNAME_ASS = 'con';
OPPNAME_AS = 'non';

oppAction = 0;
curStealing = 0; % for ASS strategy

% Initial states
lastActions0_thief = zeros(memory,1);
thiefTurn = 0;
for i = 1:memory
    if thiefTurn==0
        lastActions0_thief(i) = ACTION_NP;
        thiefTurn = 1;
    else
        lastActions0_thief(i) = ACTION_NS;
        thiefTurn = 0;
    end
end

lastActions0_pun = zeros(memory,1);
thiefTurn = 1;
for i = 1:memory
    if thiefTurn==0
        lastActions0_pun(i) = ACTION_NP;
        thiefTurn = 1;
    else
        lastActions0_pun(i) = ACTION_NS;
        thiefTurn = 0;
    end
end

curState = getStateNum(ROLE_THIEF,lastActions0_thief); % Initialize to all NSs & NPs - doesn't matter if _thief or _pun

lastActions = zeros(memory,1);

% Loop through each of the rounds
for thisRound = 1:numRounds
    % SETUP
    
    % What condition are we in? What opponent are we facing?
    role = roles(thisRound); % 1 = thief, 2 = punisher
    opponent = opponents{thisRound}(2:end);
    
    % Do we have a new opponent?
    if thisRound == 1 || strcmp(opponent,opponents{thisRound-1}(2:end)) == 0
        if strcmp(opponent,OPPNAME_ASS) == 1
            curStealing = 1;
        end
        
        if role == ROLE_THIEF
            lastActions = lastActions0_thief;
        else
            lastActions = lastActions0_pun;
        end
    end
    
    % FIRST TURN
    
    reward1 = rewards1(thisRound);
    
    if role == ROLE_THIEF
        % Do my action
        myAction = myActions(thisRound); % should be 2 for steal/punish, 1 for do nothing
        lastActions = [myAction lastActions(1:(end-1))];
        newState = getStateNum(role,lastActions);
        
        reward1 = reward1 + steal_bias*(myAction==ACTION_S);

        % Do likelihoods
        probs = exp(temp*Q(curState,:,role))/sum(exp(temp*Q(curState,:,role)));
        likelihood = likelihood + log(probs(myAction));
        
        % Update
        Q(curState,myAction,role) = Q(curState,myAction,role) + lr*(reward1 + gamma*max(Q(newState,:,role)) - Q(curState,myAction,role));
    else
        % Do opponent's action & update
        if (strcmp(opponent,OPPNAME_ASS) == 1 && curStealing == 1) || strcmp(opponent,OPPNAME_AS) == 1
            oppAction = ACTION_S;
        else
            oppAction = ACTION_NS;
        end
        
        lastActions = [oppAction lastActions(1:(end-1))];
        newState = getStateNum(role,lastActions);
        
        % In opponent states, both actions are the same
        for i = 1:2
            Q(curState,i,role) = Q(curState,i,role) + lr*(reward1 + gamma*max(Q(newState,:,role)) - Q(curState,i,role));
        end
    end
    
    % SECOND TURN
    reward2 = rewards2(thisRound);
    
    if role == ROLE_PUNISHER
        % Do my action & update
        myAction = myActions(thisRound); % should be 2 for steal/punish, 1 for do nothing
        lastActions = [myAction lastActions(1:(end-1))];
        newState = getStateNum(role,lastActions);

        reward2 = reward2 + punish_bias*(myAction==ACTION_P & lastActions(1)==ACTION_S);
        
        % Do likelihoods
        probs = exp(temp*Q(curState,:,role))/sum(exp(temp*Q(curState,:,role)));
        likelihood = likelihood + log(probs(myAction));
        
        Q(curState,myAction) = Q(curState,myAction,role) + lr*(reward2 + gamma*max(Q(newState,:,role)) - Q(curState,myAction,role));
       
        % If ASS stole & was punished, turn off stealing
        if strcmp(opponent,OPPNAME_ASS) && oppAction == ACTION_S && myAction == ACTION_P
            curStealing = 0;
        end
    else
        % Do opponent's action & update
        if strcmp(opponent,OPPNAME_APT) == 1 && myAction == ACTION_S
            oppAction = ACTION_P;
        else
            oppAction = ACTION_NP;
        end
        
        lastActions = [oppAction lastActions(1:(end-1))];
        newState = getStateNum(role,lastActions);
        
        % In opponent states, both actions are the same
        for i = 1:2
            Q(curState,i,role) = Q(curState,i,role) + lr*(reward2 + gamma*max(Q(newState,:,role)) - Q(curState,i,role));
        end
    end
        
    curState = newState;
end

likelihood = -likelihood; % for patternsearch (or fmincon)
end