numSubjects = length(subjMarkers);
numRounds = length(id);

predictor = zeros(numRounds,1);
dv = zeros(numRounds,1);
subj = zeros(numRounds,1);

rowsToToss = [];

for thisSubj = 1:numSubjects
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
    for thisRound = index
        if order(thisRound)==1
            rowsToToss(end+1) = thisRound;
        else
            % 0 is didn't punish, 1 is punish
            dv(thisRound) = behavior(thisRound)-1; % in game, 2 is punished & 1 is didn't
            
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
            
            if found==0, rowsToToss(end+1) = thisRound; end
            
            subj(thisRound) = thisSubj;
        end
    end
end

predictor = removerows(predictor,'ind',rowsToToss);
dv = removerows(dv,'ind',rowsToToss);
subj = removerows(subj,'ind',rowsToToss);
csvwrite('Analysis.csv',[predictor dv subj]);