% Load ThiefID, ThiefStrat, PunID, and PunStrat before running this

%% Need to determine the payoff to each opponent, from each match
fileNames = cell(2,1);
fileNames{1} = 'C:\Personal\Psychology\Projects\StealPunish\Code\git\Behavioral\Fiery Studies\Data\Lit4\data_processed.mat';
fileNames{2} = 'C:\Personal\Psychology\Projects\StealPunish\Code\git\Behavioral\Fiery Studies\Data\Lit5\data_processed.mat';

finalThief_flex = [];
finalThief_inflex = [];
finalPun_flex = [];
finalPun_inflex = [];

INIT_SCORE = 25;

for fileName=1:length(fileNames)
    load(fileNames{fileName});
    
    oppSum = INIT_SCORE;
    for i = 1:length(id)
        oppSum = oppSum + payoffOpp1(i) + payoffOpp2(i);

        % If the next round is a new one..
        if i==length(id) || matchRound(i+1)==1
            if condition(i)==ROLE_THIEF
                % Opp is punisher
                if opType(i)==OPP_INFLEXIBLE
                    finalPun_inflex(end+1) = oppSum;
                else
                    finalPun_flex(end+1) = oppSum;
                end
            else
                % Opp is thief
                if opType(i)==OPP_INFLEXIBLE
                    finalThief_inflex(end+1) = oppSum;
                else
                    finalThief_flex(end+1) = oppSum;
                end
            end
            oppSum = INIT_SCORE;
        end
    end
end

%% Randomly assign
ThiefScore = zeros(length(ThiefID),1);
PunScore = zeros(length(PunID),1);

for i=1:length(finalThief_inflex)
    winner = randsample(find(ThiefStrat==1),1);
    ThiefScore(winner) = ThiefScore(winner)+finalThief_inflex(i);
end

for i=1:length(finalThief_flex)
    winner = randsample(find(ThiefStrat==2),1);
    ThiefScore(winner) = ThiefScore(winner)+finalThief_flex(i);
end

for i=1:length(finalPun_inflex)
    winner = randsample(find(PunStrat==1),1);
    PunScore(winner) = PunScore(winner)+finalPun_inflex(i);
end

for i=1:length(finalPun_flex)
    winner = randsample(find(PunStrat==2),1);
    PunScore(winner) = PunScore(winner)+finalPun_flex(i);
end

%% Print
for i=1:length(ThiefID)
    disp(ThiefID{i});
end

for i=1:length(PunID)
    disp(PunID{i});
end