%% getIndivParams (in loss aversion project)
% This function finds the optimal individual parameters using the specified
%   model
% Uses patternsearch

%% Inputs:
% model should be a handle to a function that:
%   takes a single subject's info as input
%   and outputs the negLL
%   more specifically, it should be
%   model(x,actions,states,rewards,round#,combined)
% starts should be a k x numParams matrix, where k is the number of
%   different starts we want to do (and then take the best of)
% A and b are the linear constraint vectors
% bounds should be a 2 x numParams matrix, where the first row has the
%   lower limit & the second row has the upper limit

%% Outputs:
% optimalParams is a numSubjects x (numParams+2) matrix
% First column is id, last is negLL

function [optimalParams] = getIndivParams_SP(model,id,roles,opponents,myActions,rewards1,rewards2,memory,A,b,numStarts,bounds,thisSubj)
% Get the list of subjects
subjMarkers = getSubjMarkers(id);

numParams = size(bounds,2);

% Set patternsearch options
options = psoptimset('CompleteSearch','on','SearchMethod',{@searchlhs},'UseParallel','Never');

% Temporary variables
max_params = zeros(numParams,numStarts);
lik = zeros(numStarts,1);

starts = zeros(numStarts,numParams);
% Generate starts
for i=1:numParams
    starts(:,i) = linspace(bounds(1,i),bounds(2,i),numStarts);
end

%% Loop through starts
for thisStart = 1:numStarts
    % Loop through subjects
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
    % Do patternsearch
    [max_params(:,thisStart),lik(thisStart),~] = patternsearch(@(params) model(params,roles(index),opponents(index),myActions(index),rewards1(index),rewards2(index),memory),starts(thisStart,:),A,b,[],[],bounds(1,:),bounds(2,:),options);
end

% Take best results

[~,bestStart] = min(lik); % minimum likelihood
optimalParams = [thisSubj max_params(:,bestStart)' lik(bestStart)];
end