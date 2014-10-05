%% GetVectorScore
% Returns the score of vectorNum_agent playing in a world characterized by
% (x/numFHs)% all0, (x/numFHs)% all1, and (1-x)% vectorNum_pop

%% Inputs
% vectorNum_agent: # (1-indexed) of acting agent
% vectorNum_pop: # (1-indexed) of agent in population
% x: between 0 and 1 (give the actual #, not the index)
% vectorScores_QvsFH: should be numVectors x numFHs (FHs must be
%   deterministic)
% vectorScores_QvsQ: should be numVectors x numVectors

function [score,stdScore] = GetVectorScore(vectorNum_agent,vectorNum_pop,x,vectorScores_QvsFH,vectorScores_QvsQ)
score = 0;
stdScore = 0;
numFHs = size(vectorScores_QvsFH,2);

% Do FHs first
for fh = 1:numFHs
    score = score + (x/numFHs)*vectorScores_QvsFH(vectorNum_agent,fh,1);
    stdScore = stdScore + (x/numFHs)*vectorScores_QvsFH(vectorNum_agent,fh,2);
end

% Then do QvQ
score = score + (1-x)*vectorScores_QvsQ(vectorNum_agent,vectorNum_pop,1);
stdScore = stdScore + (1-x)*vectorScores_QvsQ(vectorNum_agent,vectorNum_pop,2);
end