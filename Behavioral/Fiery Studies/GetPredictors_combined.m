numSubjects = length(subjMarkers);
numRounds = length(id);

predictor = zeros(numRounds,1);
dv = zeros(numRounds,1);
subjRole = zeros(numRounds,1);
subj = zeros(numRounds,1);

keep = false(numRounds,1);

for thisSubj = 1:numSubjects
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
    for thisRound = index
        if order(thisRound)~=1
            keep(thisRound) = true;
            
            % Thief role
            if role(thisRound)==0
                % 0 is didn't steal, 1 is steal
                dv(thisRound) = behavior(thisRound);

                if dv(thisRound)==2, dv(thisRound)=0;
                else dv(thisRound)=1;
                end

                % 0 is didn't punish, 1 is punish
                predictor(thisRound) = reply(thisRound-1);

                if predictor(thisRound) == -2, predictor(thisRound) = 1; % 1 is punish
                else predictor(thisRound) = 0;
                end
            
            % Punisher role
            elseif role(thisRound)==1
                dv(thisRound) = behavior(thisRound)-1; % in game, 2 is punished & 1 is didn't.  So 1 is punished, 0 is didn't.
            
                % For predictor, we must find last time we stole
                found = 0;
                counter = 1;
                while found == 0 && (thisRound - counter) >= index(1)
                    if behavior(thisRound-counter)==2 % punishing
                        found = 1;
                        % Did the guy steal in the next round?
                        if reply(thisRound-counter+1) == -1
                            predictor(thisRound) = 1; % yes, he stole
                        else
                            predictor(thisRound) = 0; % no, he didn't
                        end
                    end
                    counter = counter+1;
                end

                if found==0, keep(thisRound) = false; end
            end
           
            subjRole(thisRound) = role(thisRound);
            subj(thisRound) = thisSubj;
        end
    end
end

predictor = predictor(keep);
dv = dv(keep);
subjRole = subjRole(keep);
subj = subj(keep);
csvwrite('Analysis.csv',[predictor dv subjRole subj]);