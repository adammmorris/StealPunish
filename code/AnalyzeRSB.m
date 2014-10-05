basePath = 'C:\Personal\School\Brown\Psychology\Thesis\Code\Output\RSBExperiment\Chicken3\';
path1 = strcat(basePath,'QAgainstAllsAndSelf.txt');
path2 = strcat(basePath,'QAgainstOtherQs.txt');
path3 = strcat(basePath,'ESS.txt');
vectorPath = strcat(basePath,'vectors.txt');

plotType = 0;

% Build the set of vector scores, w/o removing non-ESS vectors
[vectorScores, winnings, maxVectors] = GetVectorScores(path1,plotType,0,vectorPath);

maxVectors_unique = unique(maxVectors(:,1),'stable');
numMaxVectors = length(maxVectors_unique);
numScores = size(winnings,2);

% Remove non-ESS vectors
csv_QvsQ = csvread(path2,1,0); % skip header

vectorNums = csv_QvsQ(:,1);
numVectors = length(vectorNums);
winnings_QvsQ = csv_QvsQ(:,2:end);
vectorScores_QvsQ = zeros(numVectors,numVectors);

for row = 1:numVectors
    vectorScores_QvsQ(row,:) = winnings_QvsQ(row,:);
    % Get self-scores from last column of previous score vector
    vectorScores_QvsQ(row,row) = winnings(row,numScores);
end

% Which are ESS?
ESS = ones(numVectors,1);
cutoff = 0;
for row = 1:numVectors
    for col = 1:numVectors
        % Did the guy playing against row do better than row did against
        % himself?
        if (vectorScores_QvsQ(col,row) - vectorScores_QvsQ(row,row)) > cutoff
            ESS(row) = 0;
        end
    end
end

% Get scores of max vectors vs. each other
vectorScores_MVs = zeros(numMaxVectors,numMaxVectors);
for i = 1:numMaxVectors
    for j = 1:numMaxVectors
        vectorScores_MVs(i,j) = vectorScores_QvsQ(i,j);
    end
end

fordisp_1 = [0;maxVectors_unique];
fordisp_2 = [maxVectors_unique';vectorScores_MVs];
fordisp = [fordisp_1 fordisp_2];
printmatrix(fordisp,0);

[vectorScores_noESS, ~] = GetVectorScores(path1,plotType,ESS,vectorPath);