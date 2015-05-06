%% Versions

% Version 1 (6/22/2014):
% - Stable version, using the original definition of ESS (w/ playing
% against learners only)

% Version 2 (6/27/2014):
% - Implementing new, score-based definition of ESS & new algorithm (see
% Michael's email)
% - Also implemented top-level plot

% Version 3 (7/28/2014):
% - Implementing plot with only best ESS strategies, but showing each strat
% across entire x

%% Script

%addpath '/home/amm4/git/generalResearch';
%basePath = '/home/amm4/git/generalResearch/output/RSBOutput/PD5/';
basePath = 'C:\Personal\School\Brown\Psychology\Thesis\Code\generalResearch\output\IROutput\StealPunish4\';
path_QvsFH = strcat(basePath,'QAgainstAllsAndSelf.txt');
path_QvsQ = strcat(basePath,'QAgainstOtherQs.txt');
path_vectors = strcat(basePath,'vectors.txt');
path_save = strcat(basePath,'analyzed.mat');

% READ CSV
% What we have is the csv files, and what we need are two arrays:
%   winnings_QvsFH and winnings_QvsQ

% % Skip headers
% csv_QvsFH = csvread(path_QvsFH,1,0);
% csv_QvsQ = csvread(path_QvsQ,1,0);
%
% numVectors = size(csv_QvsFH,1);
%
% % Build winnings arrays
% winnings_QvsFH = csv_QvsFH(:,2:(end-1)); % skip vector num & final self column
% numFHs = size(winnings_QvsFH,2);
%
% winnings_QvsQ = zeros(numVectors,numVectors);
%
% for i = 1:numVectors
%     winnings_QvsQ(i,:) = csv_QvsQ(i,2:end); % skip first vector num column
%     winnings_QvsQ(i,i) = csv_QvsFH(i,size(csv_QvsFH,2));
% end

numVectors = 100;
winnings_QvsFH = zeros(numVectors,2,2); % :,:,1 are means, :,:,2 are stds
winnings_QvsQ = zeros(numVectors,numVectors,2);

for i=1:numVectors
    FHpath1 = [basePath num2str(i) 'vAll0.txt'];
    FHpath2 = [basePath num2str(i) 'vAll1.txt'];
    
    if exist(FHpath1,'file') ~= 2 || exist(FHpath2,'file') ~= 2
        winnings_QvsFH(i,1,:) = [-realmax 0];
        winnings_QvsFH(i,2,:) = [-realmax 0];
        for j = 1:numVectors
            winnings_QvsQ(i,j,:) = [-realmax 0];
        end
    else
        winnings_QvsFH(i,1,:) = csvread([basePath num2str(i) 'vAll0.txt'],1,0);
        winnings_QvsFH(i,2,:) = csvread([basePath num2str(i) 'vAll1.txt'],1,0);
        
        for j=1:numVectors
            %fprintf([num2str(i) 'v' num2str(j) '\n']);
            Qpath = [basePath num2str(i) 'v' num2str(j) '.txt'];
            
            if exist(Qpath,'file') ~= 2
                winnings_QvsQ(i,j,:) = [-realmax 0];
            else
                winnings_QvsQ(i,j,:) = csvread([basePath num2str(i) 'v' num2str(j) '.txt'],1,0);
            end
        end
    end
end

% Build the set of vector scores, w/o removing non-ESS vectors
%[vectorScores, winnings, maxVectors] = GetVectorScores(path1,plotType,0,path_vectors);

% Set x=0
xrange = linspace(0,1,100);
numx = length(xrange);

% Let's make an array containing the normal vector scores for all x
vectorScores_ownWorld = zeros(numVectors,numx);
vectorScores = zeros(numVectors,numVectors,numx);
stdScores = zeros(numVectors,numVectors,numx);
for agent = 1:numVectors
    for x = 1:numx
        for pop = 1:numVectors
            [vectorScores(agent,pop,x),stdScores(agent,pop,x)] = GetVectorScore(agent,pop,xrange(x),winnings_QvsFH,winnings_QvsQ);
        end
        vectorScores_ownWorld(agent,x) = vectorScores(agent,agent,x);
    end
end

%cutoff = 100;
ESS = zeros(numVectors,numx);
for i = 1:numVectors
    for x = 1:numx
        [a,b] = max(vectorScores(:,i,x));
        if (a-vectorScores(i,i,x)) <= 2*stdScores(i,i,x)
            ESS(i,x)=1;
        end
    end
end
ESS = logical(ESS);

bestESS = zeros(numx,1);
hadNoBestESS = zeros(numx,1);
for x=1:numx
    temparray = vectorScores_ownWorld(:,x);
    [~,best] = max(temparray);
    while any(temparray>-realmax) && ~ESS(best,x)
        temparray(best) = -realmax;
        [~,best] = max(temparray);
    end
    if ~any(temparray>-realmax)
        %fprintf(strcat('No ESS vector @ x=',num2str(xrange(x))));
        hadNoBestESS(x)=1;
        if x~=1
            bestESS(x) = bestESS(x-1);
        end
    else
        bestESS(x) = best;
    end
end

vectors = csvread(path_vectors);

% Save!
save(path_save);

%% MAKE THE PLOT

plotType = 2;
makePlot(plotType,vectorScores,vectorScores_ownWorld,ESS,bestESS,vectors,xrange);