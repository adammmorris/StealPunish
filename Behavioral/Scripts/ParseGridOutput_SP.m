%% ParseGridOutput

path = '/home/amm4/git/TDRL/Human Test/Data/Analysis/Real Data v2/Take 2/ArApBrBpSE/';
numParams = 4;
numSubjects = length(subjMarkers);
params = zeros(numSubjects,numParams+2);

for i = 1:length(subjMarkers)
    params(i,:) = csvread([path 'Params_Subj' num2str(i) '.txt']);
end