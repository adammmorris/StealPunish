%% ID stuff
id=subject; clear subject;
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
clear index; clear thisSubj;

%% Constants

% Roles
ROLE_THIEF = 0;
ROLE_PUN = 1;

% Opponents
OPP_INFLEXIBLE = 0;
OPP_FLEXIBLE = 1;

% Actions
CHOICE_NOTHING = 0;
CHOICE_ACTION = 1;

%% Variables
% matchMarkers = ones(1);
% curMatch = 1;
% 
% for thisDataPt = 2:length(id)
%     if matchRound(thisDataPt) == 1
%         curMatch = curMatch + 1;
%         matchMarkers(curMatch) = thisDataPt;
%     end
% end
% 
% % For each match, figure out who opponent was
% for thisMatch = 1:length(matchMarkers)
%     % Get index
%     if thisMatch < length(matchMarkers)
%         index = matchMarkers(thisMatch):(matchMarkers(thisMatch + 1) - 1);
%     else
%         index = matchMarkers(thisMatch):length(id);
%     end
%     
%     if condition(index(1)) == ROLE_PUN
%         % Opponent's a thief
%         
%         % Did the person ever punish?
%         if all(choice(index)==CHOICE_NOTHING)
%             % Assign randomly
%             if rand() < .5, opType(index) = OPP_FLEXIBLE;
%             else opType(index) = OPP_INFLEXIBLE; end
%         else
%             if all(opChoice(index)==CHOICE_ACTION), opType(index) = OPP_INFLEXIBLE;
%             else opType(index) = OPP_FLEXIBLE; end
%         end
%     else
%         % Opponent is a punisher
%         
%         % Did the person steal more than once?
%         if sum(choice(index)==CHOICE_ACTION) <= 1
%             if rand() < .5, opType(index) = OPP_FLEXIBLE;
%             else opType(index) = OPP_INFLEXIBLE; end
%         else
%             % Did the opponent punish more than once?
%             if sum(opChoice(index)==CHOICE_ACTION) > 1, opType(index) = OPP_INFLEXIBLE;
%             else opType(index) = OPP_FLEXIBLE;
%             end
%         end
%     end
% end