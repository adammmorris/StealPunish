%% ParseGridOutput

path = 'C:\Personal\School\Brown\Psychology\Thesis\Code\generalResearch\Behavioral\Fiery Studies\Lit3\Params\';
numParams = 2;
numSubjects = length(subjMarkers);
params = zeros(numSubjects,numParams+2);

for i = 1:length(subjMarkers)
    params(i,:) = csvread([path 'Params_Subj' num2str(i) '.txt']);
end