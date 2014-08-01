% 1 for top-surface, 2 for dashed/solid (only bestESS), 3 for dashed/solid (all)

function makePlot(plotType,vectorScores,vectorScores_ownWorld,ESS,bestESS,vectors,xrange)

figure('WindowButtonMotionFcn', @hoverCallback,'WindowButtonDownFcn', @clickCallback);
%figure;
textHdl = text('Color', 'black', 'VerticalAlign', 'Bottom');
hoverMatrix = [];
bigNumber = 1000;

numVectors = size(vectors,1);
numx = length(xrange);

if plotType == 1
    hold all;
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
        curVector = maxVectors(prev,1);
        plot(xrange(xs),vectorScores_ownWorld(curVector,xs));
        
        for q=1:length(xs)
            hoverMatrix(end+1,:)=[xrange(xs(q)) vectorScores_ownWorld(curVector,xs(q)) curVector];
        end
        
        prev = next+1;
    end
    
    title('Scores vs. X (top-surface)');
    xlabel('X'); ylabel('Scores');
    
    % Set legend
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
elseif plotType == 2
    hold on;
    
    % bestStrats is numBestStrats x numx
    % tells, for each of best strats, whether it's ESS at a given x
    bestESS_unique = unique(bestESS,'stable');
    numBestStrats = length(bestESS_unique);
    bestStrats = ESS(bestESS_unique,:);
    
    %ColOrd = get(gca,'ColorOrder');
    
    % Set up color matrix
    %colors = rand(numBestStrats,3);
    colors = hsv(numBestStrats);
    colors = colors(randperm(length(colors)),:);
    
    % Set up legend
    h = zeros(numBestStrats,1);
    myleg = cell(numBestStrats,1);
    
    % Loop through best strats
    for i = 1:numBestStrats
        x_temp = 1;
        ESS_temp = bestStrats(i,x_temp); % the ESSness at x=1
        
        stratNum = bestESS_unique(i);
        
        % Deal w/ legend stuff
        vectorValues = vectors(stratNum,:);
        vectorNum = num2str(stratNum);
        vectorName = getVectorName(vectorValues);
        
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
        
        % Loop through x, stopping every time there's a switch of ESSness
        for x = 2:numx
            if bestStrats(i,x)~=ESS_temp || x==numx
                % Different ESS!
                % Plot the appropriate line
                
                % Line spec (solid if ESS, dashed if not)
                if ESS_temp
                    lineSpec = '-';
                    marker = 'none';
                else
                    lineSpec = 'none';
                    marker = '.';
                end
                
                % Get range & plot
                xs = x_temp:(x-1);
                h_temp = plot(xrange(xs),vectorScores_ownWorld(stratNum,xs),'LineStyle',lineSpec,'LineWidth',1,'Color',colors(i,:),'Marker',marker,'MarkerSize',4.5);
                
                x1 = xrange(xs(1)); x2 = xrange(xs(end)); y1 = vectorScores_ownWorld(stratNum,xs(1)); y2 = vectorScores_ownWorld(stratNum,xs(end));
                fakex = linspace(x1,x2,bigNumber);
                fakey = ((y2-y1)/(x2-x1))*(fakex-x1)+y1;
                for q=1:length(fakex)
                    hoverMatrix(end+1,:)=[fakex(q) fakey(q) stratNum];
                end
           
                if ESS_temp, h(i) = h_temp; end
                
                % Move up
                x_temp = x;
                ESS_temp = bestStrats(i,x_temp);
            end
        end
    end
    
    title('Scores vs. X (dashed/solid, only best ESS)');
    xlabel('X'); ylabel('Scores');
    legend(h,myleg,'Location','BestOutside');
elseif plotType == 3
    hold on;
    
    % bestStrats is numBestStrats x numx
    % tells, for each of best strats, whether it's ESS at a given x
    bestESS_unique = unique(bestESS,'stable');
    numBestStrats = length(bestESS_unique);
    bestStrats = ESS(bestESS_unique,:);
       
    % Set up color matrix
    %colors = rand(numBestStrats,3);
    colors = hsv(numBestStrats);
    colors = colors(randperm(length(colors)),:);
    
    % Set up legend
    h = zeros(numBestStrats,1);
    myleg = cell(numBestStrats,1);
    
    % Loop through best strats
    for i = 1:numBestStrats
        x_temp = 1;
        ESS_temp = bestStrats(i,x_temp); % the ESSness at x=1
        
        stratNum = bestESS_unique(i);
        
        % Deal w/ legend stuff
        vectorValues = vectors(stratNum,:);
        vectorNum = num2str(stratNum);
        vectorName = getVectorName(vectorValues);
        
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
        
        % Loop through x, stopping every time there's a switch of ESSness
        for x = 2:numx
            if bestStrats(i,x)~=ESS_temp || x==numx
                % Different ESS!
                % Plot the appropriate line
                
                % Line spec (solid if ESS, dashed if not)
                if ESS_temp
                    lineSpec = '-';
                    marker = 'none';
                else
                    lineSpec = 'none';
                    marker = '.';
                end
                
                % Get range & plot
                xs = x_temp:(x-1);
                h_temp = plot(xrange(xs),vectorScores_ownWorld(stratNum,xs),'LineStyle',lineSpec,'LineWidth',2,'Color',colors(i,:),'Marker',marker,'MarkerSize',4.6);
                
                x1 = xrange(xs(1)); x2 = xrange(xs(end)); y1 = vectorScores_ownWorld(stratNum,xs(1)); y2 = vectorScores_ownWorld(stratNum,xs(end));
                fakex = linspace(x1,x2,bigNumber);
                fakey = ((y2-y1)/(x2-x1))*(fakex-x1)+y1;
                for q=1:length(fakex)
                    hoverMatrix(end+1,:)=[fakex(q) fakey(q) stratNum];
                end
                
                if ESS_temp, h(i) = h_temp; end
                
                % Move up
                x_temp = x;
                ESS_temp = bestStrats(i,x_temp);
            end
        end
    end
    
    % Draw the rest
    for i = 1:numVectors
        if ~any(bestESS_unique==i) && ~any(vectorScores_ownWorld(i,:)<min(min(vectorScores_ownWorld(bestESS_unique,:))))
            xs = 1:numx;
            plot(xrange(xs),vectorScores_ownWorld(i,xs),'LineStyle','none','Color',[.5 .5 .5],'Marker','.','MarkerSize',1);
            
            stratNum = i;
            x1 = xrange(xs(1)); x2 = xrange(xs(end)); y1 = vectorScores_ownWorld(stratNum,xs(1)); y2 = vectorScores_ownWorld(stratNum,xs(end));
            fakex = linspace(x1,x2,bigNumber);
            fakey = ((y2-y1)/(x2-x1))*(fakex-x1)+y1;
            for q=1:length(fakex)
                hoverMatrix(end+1,:)=[fakex(q) fakey(q) stratNum];
            end
        end
    end
    
    title('Scores vs. X (dashed/solid, all)');
    xlabel('X'); ylabel('Scores');
    legend(h,myleg,'Location','BestOutside');
end

% matrix should be (numVectorsInPlot*numx) x 3
% 1st col is x value, 2nd is y value, 3rd is vector num

function hoverCallback(~, ~)

    axesHdl = gca;

    % Grab the x & y axes coordinate where the mouse is
    mousePoint = get(axesHdl, 'CurrentPoint');
    mouseX = mousePoint(1,1);
    mouseY = mousePoint(1,2);

    % Compare where data and current mouse point to find the data point
    % which is closest to the mouse point
    x = hoverMatrix(:,1);
    y = hoverMatrix(:,2);
    distancesToMouse = hypot(x - mouseX, y - mouseY);
    [~, ind] = min(abs(distancesToMouse));
    vectorNum = hoverMatrix(ind,3);
    vectorValues = vectors(vectorNum,:);

    fullValue = '(';
    for j = 1:length(vectorValues)
        fullValue = strcat(fullValue,num2str(vectorValues(j)));
        if j ~= length(vectorValues)
            fullValue = strcat(fullValue,',');
        end
    end
    fullValue = [fullValue ')'];

    % If the distance is less than some threshold, set the text
    % object's string to show the data at that point.
    xaxisrange = range(get(axesHdl, 'Xlim'));
    yaxisrange = range(get(axesHdl, 'Ylim'));
    threshold = .02;
    if abs(mouseX - x(ind)) < threshold*xaxisrange && abs(mouseY - y(ind)) < threshold*yaxisrange
        set(textHdl, 'String', [num2str(vectorNum) ':' fullValue]);
        set(textHdl, 'Position', [x(ind) + 0.01*xaxisrange, y(ind) + 0.01*yaxisrange])
    else
        set(textHdl, 'String', '')
    end
    
end

function clickCallback(~, ~)

    axesHdl = gca;

    % Grab the x & y axes coordinate where the mouse is
    mousePoint = get(axesHdl, 'CurrentPoint');
    mouseX = mousePoint(1,1);
    mouseY = mousePoint(1,2);

    % Compare where data and current mouse point to find the data point
    % which is closest to the mouse point
    x = hoverMatrix(:,1);
    y = hoverMatrix(:,2);
    distancesToMouse = hypot(x - mouseX, y - mouseY);
    [~, ind] = min(abs(distancesToMouse));
    vectorNum = hoverMatrix(ind,3);
    vectorValues = vectors(vectorNum,:);

    fullValue = '(';
    for j = 1:length(vectorValues)
        fullValue = strcat(fullValue,num2str(vectorValues(j)));
        if j ~= length(vectorValues)
            fullValue = strcat(fullValue,',');
        end
    end
    fullValue = [fullValue ')'];

    [~,realx] = min(abs(xrange-x(ind)));

    disp('-------------------');
    disp(['Vector #' num2str(vectorNum) ': ' fullValue]);
    disp(['Score in own world (@ x=' num2str(realx) '): ' num2str(vectorScores_ownWorld(vectorNum,realx))]);
    if ESS(vectorNum,realx),tempstr = 'Yes';
    else tempstr='No';
    end
    disp(['ESS (@ x=' num2str(realx) ')? ' tempstr]);

    if ~ESS(vectorNum,realx)
        [winningval,winner] = max(vectorScores(:,vectorNum,realx));
        vectorValues = vectors(winner,:);
        fullValue = '(';
        for j = 1:length(vectorValues)
            fullValue = strcat(fullValue,num2str(vectorValues(j)));
            if j ~= length(vectorValues)
                fullValue = strcat(fullValue,',');
            end
        end
        fullValue = [fullValue ')'];
        disp(['Beaten by vector #' num2str(winner) ': ' fullValue ', who scored ' num2str(winningval)]);
    end
    disp('-------------------');
end

end