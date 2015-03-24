%% Analyze Data
% This script gets called by the grid, starts the whole modeling process.

%% Params
% datapath: should have id, subjMarkers, roles, opponents, myActions, rewards1, rewards2
% savepath: path to save output to
% tasknum: subj #

function AnalyzeData_SP(datapath,savepath,tasknum)

% load shit
load(datapath);

% tasknum is from 1 to numSubjects
numSubjects = length(subjMarkers);
if (tasknum < 1 || tasknum > numSubjects)
    error('tasknum must be between 1 and numSubjects');
end

numStarts = 10;
curMemory = 2;

gamma = .95;
lr = .4;
temp = 2;

% [lr,temp,steal_bias,punish_bias]
%bounds = [0 0 0 0; 1 3 5 5];
bounds = [0 0; 5 5];
model = @(x,roles,opponents,myActions,rewards1,rewards2,memory) getIndivLike_SP([lr temp x(1) x(2) gamma],roles,opponents,myActions,rewards1,rewards2,curMemory);
optParams = getIndivParams_SP(model,id,roles,opponents,myActions,rewards1,rewards2,curMemory,[],[],numStarts,bounds,tasknum);

name = [savepath '/Params_Subj' num2str(tasknum) '.txt'];
csvwrite(name,optParams);
end