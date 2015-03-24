% Loop through subjects
% Need role, actions, & round #

numSubjects = length(subjMarkers);
numDataPoints = length(id);

curRoles = zeros(numDataPoints,1);
curChoices = zeros(numDataPoints,1);
roundNums = zeros(numDataPoints,1);
subjIDs = zeros(numDataPoints,1);
keep = false(numDataPoints,1);

for thisSubj = 1:numSubjects
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
    roundAgainstOpp = 1;
    for i=index
        if i == 1 || ~strcmp(opponents{i}(2:end),opponents{i-1}(2:end))
            roundAgainstOpp = 1;
        end
        
        if (roles(i)==ROLE_THIEF && strcmp(opponents{i}(2:end),'pun')) || (roles(i)==ROLE_PUNISHER && strcmp(opponents{i}(2:end),'non'))
            keep(i) = true;
            if (roles(i)==ROLE_THIEF), curRoles(i) = 1;
            else curRoles(i) = 0; end
            curChoices(i) = myActions(i)-1;
            roundNums(i) = roundAgainstOpp;
            subjIDs(i) = id(i);
        end
        
        roundAgainstOpp = roundAgainstOpp + 1;
    end
end

csvwrite('/home/amm4/git/generalResearch/Behavioral/Fiery Studies/Lit3/Flatness.csv',[curRoles(keep) curChoices(keep) roundNums(keep) subjIDs(keep)]);