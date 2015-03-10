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
           
            subj(thisRound) = thisSubj;
        end
    end
end

predictor = removerows(predictor,'ind',rowsToToss);
dv = removerows(dv,'ind',rowsToToss);
subj = removerows(subj,'ind',rowsToToss);
csvwrite('Analysis.csv',[predictor dv subj]);