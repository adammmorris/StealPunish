function [finalScores] = getFinalScores(scores, subjMarkers)
numSubjects = length(subjMarkers);
finalScores = zeros(numSubjects,1);
for i = 1:(numSubjects - 1)
    finalScores(i) = scores(subjMarkers(i+1)-1);
end
finalScores(numSubjects) = scores(end);
end