%% ParseGridOutput

path = '/home/amm4/git/generalResearch/Behavioral/Fiery Studies/Lit3/Params_ps/';
numSubjects = length(subjMarkers);
numParams = 3+2*numSubjects;
numStarts = 100;

params_all = [];
lrs = [];
temps = [];
gammas = [];
steal_biases = [];
punish_biases = [];

for i = 1:numStarts
    name = [path 'Params_Start' num2str(i) '.txt'];
    if exist(name,'file')
        params_all(end+1,:) = csvread([path 'Params_Start' num2str(i) '.txt'],0,1);
    end
end

params = zeros(numSubjects,2);
cur = 4;
for i = 1:numSubjects
    params(i,:) = params_all(73,cur:(cur+1));
    cur = cur + 2;
end