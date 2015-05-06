finalScores = getFinalScores(score,subjMarkers);
bonuses = finalScores;
bonuses(bonuses<0)=0;

for i=1:length(subjMarkers)
    disp(old_id{subjMarkers(i)});
end