% matrix should be (numVectorsInPlot*numx) x 3
% 1st col is x value, 2nd is y value, 3rd is vector num

function hoverCallback(~, ~, matrix,vectors)

axesHdl = axes;
textHdl = text('Color', 'black', 'VerticalAlign', 'Bottom');

% Grab the x & y axes coordinate where the mouse is
mousePoint = get(axesHdl, 'CurrentPoint');
mouseX = mousePoint(1,1);
mouseY = mousePoint(1,2);

% Compare where data and current mouse point to find the data point
% which is closest to the mouse point
distancesToMouse = hypot(matrix(:,1) - mouseX, matrix(:,2) - mouseY);
[~, ind] = min(abs(distancesToMouse));
vectorValues = vectors(matrix(ind,3),:);

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
xrange = range(get(axesHdl, 'Xlim'));
yrange = range(get(axesHdl, 'Ylim'));
if abs(mouseX - x(ind)) < 0.02*xrange && abs(mouseY - y(ind)) < 0.02*yrange
    set(textHdl, 'String', fullValue);
    set(textHdl, 'Position', [x(ind) + 0.01*xrange, y(ind) + 0.01*yrange])
else
    set(textHdl, 'String', '')
end
end