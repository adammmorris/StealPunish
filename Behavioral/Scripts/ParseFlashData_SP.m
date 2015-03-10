%% ID stuff
%id=subjectid; clear subjectid;
subjMarkers = getSubjMarkers(id);
numSubjects = length(subjMarkers);

old_id = id;
id = zeros(length(old_id),1);

% Populate array (while converting to serial date #s)
for thisSubj = 1:numSubjects
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
    id(index) = thisSubj;
end

% Clean up
clear index; clear numSubjects; clear thisSubj;

%% Constants

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

%% Variables
myActions = action; clear action;
opponents = opponent; clear opponent;

roles = zeros(length(id),1);
for i = 1:length(id)
    opp = opponents{i}(2:end);
    if strcmp(opp,OPPNAME_APT) || strcmp(opp,OPPNAME_NP), roles(i) = ROLE_THIEF;
    else roles(i) = ROLE_PUNISHER;
    end
end

rewards1 = zeros(length(id),1);
rewards2 = zeros(length(id),1);
s = 1; s_prime = -1; c = -1; p = -3;
curStealing = 1;
for i = 1:length(id)
    opp = opponents{i}(2:end);
    if i == 1 || strcmp(opp,opponents{i-1}(2:end)) == 0, curStealing = 1; end
    
    if roles(i) == ROLE_THIEF
        if myActions(i)==ACTION_S
            rewards1(i) = s;
            rewards2(i) = p*(strcmp(opp,OPPNAME_APT));
        end
    elseif roles(i) == ROLE_PUNISHER
       stole = 0;
       if strcmp(opp,OPPNAME_AS) || (strcmp(opp,OPPNAME_ASS) && curStealing)
            rewards1(i) = s_prime;
            stole = 1;
       end
       if myActions(i)==ACTION_P
           rewards2(i) = c;
           if stole == 1, curStealing = 0; end
       end
    end
end