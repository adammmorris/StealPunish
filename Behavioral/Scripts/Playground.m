% choices_t = zeros(numSubjects,1);
% choices_p = zeros(numSubjects,1);
% choices_t_init = zeros(numSubjects,1);
% choices_p_init = zeros(numSubjects,1);

choices_t_across = zeros(numSubjects,10);
choices_p_across = zeros(numSubjects,10);

% Loop through subjects
for thisSubj = 1:numSubjects
    % Get index
    if thisSubj < length(subjMarkers)
        index = subjMarkers(thisSubj):(subjMarkers(thisSubj + 1) - 1);
    else
        index = subjMarkers(thisSubj):length(id);
    end
    
%     choices_t_temp = [];
%     choices_p_temp = [];
%     choices_t_init_temp = [];
%     choices_p_init_temp = [];
    choices_t_across_temp = zeros(length(index),10);
    choices_p_across_temp = zeros(length(index),10);
    keep_t = false(length(index),10);
    keep_p = false(length(index),10);
    
    roundAgainstOpp = 1;
    for i = index
        if i>1 && ~strcmp(opponents{i}(2:end),opponents{i-1}(2:end))
            roundAgainstOpp = 1;
        end
        
        if roles(i)==ROLE_THIEF && strcmp(opponents{i}(2:end),'pun')
%             if strcmp(opponents{i}(2:end),'pun') && roundAgainstOpp > 1
%                 choices_t_temp(end+1) = myActions(i)-1;
%             elseif roundAgainstOpp == 1
%                 choices_t_init_temp(end+1) = myActions(i)-1;
%             end
            
            choices_t_across_temp(i,roundAgainstOpp) = myActions(i)-1;
            keep_t(i,roundAgainstOpp) = true;
        elseif roles(i)==ROLE_PUNISHER && strcmp(opponents{i}(2:end),'non')
%             if strcmp(opponents{i}(2:end),'non') && i > 1 && strcmp(opponents{i-1}(2:end),'non')
%                 choices_p_temp(end+1) = myActions(i)-1;
%             elseif i==1 || ~strcmp(opponents{i}(2:end),opponents{i-1}(2:end))
%                 choices_p_init_temp(end+1) = myActions(i)-1;
%             end

            choices_p_across_temp(i,roundAgainstOpp) = myActions(i)-1;
            keep_p(i,roundAgainstOpp) = true;
        end
        
        roundAgainstOpp = roundAgainstOpp + 1;
    end
    
%     choices_t(thisSubj) = mean(choices_t_temp);
%     choices_p(thisSubj) = mean(choices_p_temp);
%     choices_t_init(thisSubj) = mean(choices_t_init_temp);
%     choices_p_init(thisSubj) = mean(choices_p_init_temp);
    for j = 1:10
        choices_t_across(thisSubj,j) = mean(choices_t_across_temp(keep_t(:,j),j));
        choices_p_across(thisSubj,j) = mean(choices_p_across_temp(keep_p(:,j),j));
    end
end

% subplot(2,2,1);
% boxplot(choices_t);
% title('% stealing when facing "always punish transgressions"');
% 
% subplot(2,2,2);
% boxplot(choices_p);
% title('% punishing when facing "always steal"');
% 
% subplot(2,2,3);
% boxplot(choices_t_init);
% title('% stealing on first round against new opponent');
% 
% subplot(2,2,4);
% boxplot(choices_p_init);
% title('% punishing on first round against new opponent');

figure; hold on; col = hsv(10);
choices_t_means = mean(choices_t_across,1);
choices_t_ses = std(choices_t_across) / sqrt(40);

choices_p_means = mean(choices_p_across,1);
choices_p_ses = std(choices_p_across) / sqrt(40);

errorbar(choices_p_means,choices_p_ses,'color','blue');
ylim([0 1]);

errorbar(choices_t_means,choices_t_ses,'color','red');
ylim([0 1]);

legend('Punisher facing "always steal"','Thief facing "always punish transgressions"');
xlabel('Trial');
ylabel('% stealing/punishing');