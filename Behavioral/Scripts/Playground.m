numRounds = 7;

choices_t_across = zeros(numSubjects,numRounds);
choices_p_across = zeros(numSubjects,numRounds);

for thisSubj = 1:numSubjects
    for thisRound = 1:numRounds
        choices_t_across(thisSubj,thisRound) = mean(choice(id==thisSubj & condition == ROLE_THIEF & opType == OPP_INFLEXIBLE & matchRound == thisRound));
        choices_p_across(thisSubj,thisRound) = mean(choice(id==thisSubj & condition == ROLE_PUN & opType == OPP_INFLEXIBLE & matchRound == thisRound));
    end
end

fh = figure; hold on; col = hsv(10);

choices_t_means = zeros(1,numRounds);
choices_t_ses = zeros(1,numRounds);
choices_p_means = zeros(1,numRounds);
choices_p_ses = zeros(1,numRounds);
for i = 1:numRounds
    good = ~isnan(choices_t_across(:,i));
    choices_t_means(i) = mean(choices_t_across(good,i));
    choices_t_ses(i) = std(choices_t_across(good,i)) / sqrt(sum(good));
    
    good = ~isnan(choices_p_across(:,i));
    choices_p_means(i) = mean(choices_p_across(good,i));
    choices_p_ses(i) = std(choices_p_across(good,i)) / sqrt(sum(good));
end

errorbar(choices_t_means,choices_t_ses,'ro');
xlim([0 numRounds+1]);
ylim([0 1]);

errorbar(choices_p_means,choices_p_ses,'bo');
xlim([0 numRounds+1]);
ylim([0 1]);

plot(1:numRounds,polyval(polyfit(1:numRounds,choices_t_means,2),1:numRounds),'r');
plot(1:numRounds,polyval(polyfit(1:numRounds,choices_p_means,1),1:numRounds),'b');

legend('Thief','Punisher');
%legend('Thief');
legend('boxoff');
xlabel('Round');
ylabel('% Stealing / punishing');

pubgraph(fh,14,2,'w');
set(gca,'YTick',[0 1]);
set(gca,'YTickLabel',[0 100]);
set(gca,'XTick',[1 7]);

% for i = 1:10
%     signrank(choices_t_across(:,i),choices_p_across(:,i)) < .005
% end