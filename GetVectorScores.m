% set makePlot = 1 for all vector plot, or 2 for upper surface plot
function [vectorScores, winnings, maxVectors] = GetVectorScores(path,makePlot,ESS,vectorPath)
csv = csvread(path,1,0); % skip header
numSteps = 100;
xrange = linspace(0,1,numSteps);
numx = length(xrange);
numVectors = size(csv,1);
notESS_value = -realmax;

vectorNums = csv(:,1);
winnings = csv(:,2:end);
numOpponents = size(winnings,2);

vectorScores = zeros(numVectors,numx);

% If we weren't given an ESS list
givenESS = (ESS ~= 0);
if ~givenESS
    ESS = ones(numVectors,1);
end

if (makePlot > 0)
    figure; hold all;
end

for vector = 1:numVectors
    if ESS(vector) == 1
        for x = 1:numx
            for score = 1:(numOpponents-1)
                vectorScores(vector,x) = vectorScores(vector,x) + (xrange(x)/(numOpponents-1))*winnings(vector,score);
            end
            vectorScores(vector,x) = vectorScores(vector,x) + (1-xrange(x))*winnings(vector,numOpponents);
        end
        
        if (makePlot==1)
            plot(xrange,vectorScores(vector,:));
        end
    else
        vectorScores(vector,:) = notESS_value;
    end
end

% Do fancy upper surface plot
% Get max vectors at each
maxVectors = ones(numx,2); % 2nd column is 1 if it's the endpoint of this vector
[~,maxVectors(1,1)] = max(vectorScores(:,1));
for x=2:numx
    [~,maxVectors(x,1)] = max(vectorScores(:,x));
    maxVectors(x-1,2) = (maxVectors(x,1)~=maxVectors(x-1,1));
end
% last point is always an endpoint
maxVectors(numx,2) = 1;

if (makePlot==2)
    % Make line segments & plot them
    prev = 1;
    endpoints = find(maxVectors(:,2)==1);
    for i = 1:length(endpoints)
        next = endpoints(i);
        xs = prev:next;
        plot(xrange(xs),vectorScores(maxVectors(prev,1),xs));
        prev = next+1;
    end
    
    if (~givenESS)
        title('Scores vs. X (w/ all vectors)');
    else
        title('Scores vs. X (w/ only ESS vectors)');
    end
    xlabel('X'); ylabel('Scores');
    
    % Set legend
    vectors = csvread(vectorPath);
    numEndPoints = length(endpoints);
    myleg = cell(numEndPoints,1);
    for i = 1:numEndPoints
        curMaxVector = maxVectors(endpoints(i),1);
        vectorValues = vectors(curMaxVector,:);
        myleg{i} = strcat(num2str(curMaxVector),':(');
        for j = 1:length(vectorValues)
            myleg{i} = strcat(myleg{i},num2str(vectorValues(j)));
            if j ~= length(vectorValues)
                myleg{i} = strcat(myleg{i},',');
            end
        end
        myleg{i} = strcat(myleg{i},')');
    end
    legend(myleg,'Location','BestOutside');
    %legend(strcat(num2str(unique(maxVectors(:,1),'stable'))),'Location','BestOutside');
end
end