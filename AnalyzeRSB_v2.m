%% Versions

% Version 1 (6/22/2014):
% - Stable version, using the original definition of ESS (w/ playing
% against learners only)

% Version 2 (6/27/2014):
% - Implementing new, score-based definition of ESS & new algorithm (see
% Michael's email)

%% Script

basePath = 'C:\Personal\School\Brown\Psychology\Thesis\Code\Output\IRExperiment\StealPunish1\';
path_QvsFH = strcat(basePath,'QAgainstAllsAndSelf.txt');
path_QvsQ = strcat(basePath,'QAgainstOtherQs.txt');
path_vectors = strcat(basePath,'vectors.txt');

plotType = 0;

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

%% MAKE THE PLOT

figure; hold all;
maxVectors = ones(numx,2); % 2nd column is 1 if it's the endpoint of this vector
maxVectors(1,1) = bestESS(1);
for x=2:numx
    maxVectors(x,1) = bestESS(x);
    maxVectors(x-1,2) = (maxVectors(x,1)~=maxVectors(x-1,1));
end
% last point is always an endpoint
maxVectors(numx,2) = 1;
% Make line segments & plot them
prev = 1;
endpoints = find(maxVectors(:,2)==1);
for i = 1:length(endpoints)
    next = endpoints(i);
    xs = prev:next;
    plot(xrange(xs),vectorScores_ownWorld(maxVectors(prev,1),xs));
    prev = next+1;
end

title('Scores vs. X (w/ only ESS vectors)');
xlabel('X'); ylabel('Scores');

% Set legend
vectors = csvread(path_vectors);
numEndPoints = length(endpoints);
myleg = cell(numEndPoints,1);
for i = 1:numEndPoints
    curMaxVector = maxVectors(endpoints(i),1);
    vectorValues = vectors(curMaxVector,:);
    vectorNum = num2str(curMaxVector);
    vectorName = getVectorName(vectorValues);
    %myleg{i} = [strcat(num2str(curMaxVector),':') getVectorName(vectorValues)];
    %myleg{i} = strcat(num2str(curMaxVector),':(');
    fullValue = '(';
    for j = 1:length(vectorValues)
        fullValue = strcat(fullValue,num2str(vectorValues(j)));
        if j ~= length(vectorValues)
            fullValue = strcat(fullValue,',');
        end
    end
    fullValue = [fullValue ')'];
    %myleg{i} = [vectorNum ':' vectorName '  [detail:' fullValue ']'];
    myleg{i} = [vectorNum ':' fullValue];
end
legend(myleg,'Location','BestOutside');