%% getIndivParams (in steal-punish project)
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
% max_params for this start, lik for this start

%% Versions:
% Version 2 implements global params

function [max_params,lik] = getIndivParams_SP_v2(model,roles,opponents,myActions,rewards1,rewards2,memory,A,b,bounds,thisStart)

% Set patternsearch options
options = psoptimset('CompleteSearch','on','SearchMethod',{@searchlhs});
%options = optimoptions('particleswarm','UseParallel',true);

% Do patternsearch
[max_params,lik,~] = patternsearch(@(params) model(params,roles,opponents,myActions,rewards1,rewards2,memory),thisStart,A,b,[],[],bounds(1,:),bounds(2,:),options);
%[max_params,lik,~] = particleswarm(@(params) model(params,roles,opponents,myActions,rewards1,rewards2,memory),size(bounds,2),bounds(1,:),bounds(2,:),options);
%[max_params,lik,~] = fmincon(@(params) model(params,roles,opponents,myActions,rewards1,rewards2,memory),thisStart,A,b,[],[],bounds(1,:),bounds(2,:));

% Take best results
%[~,bestStart] = min(lik); % minimum likelihood
%optimalParams = [thisSubj max_params(:,bestStart)' lik(bestStart)];
end