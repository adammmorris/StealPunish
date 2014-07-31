function dataHover

% Define some data
x = -100:100;
y = x.^3;

% Set up a figure with a callback that executes on mouse motion, a set of
% axes, plot something in the axes, define a text object for later use.
figHdl = figure('WindowButtonMotionFcn', @hoverCallback);
axesHdl = axes;
lineHdl = plot(x, y, 'LineStyle', 'none', 'Marker', '.', 'Parent', axesHdl);
textHdl = text('Color', 'black', 'VerticalAlign', 'Bottom');

    function hoverCallback(src, evt)
        % Grab the x & y axes coordinate where the mouse is
        mousePoint = get(axesHdl, 'CurrentPoint');
        mouseX = mousePoint(1,1);
        mouseY = mousePoint(1,2);
        
        % Compare where data and current mouse point to find the data point
        % which is closest to the mouse point
        distancesToMouse = hypot(x - mouseX, y - mouseY);
        [val, ind] = min(abs(distancesToMouse));
        
        % If the distance is less than some threshold, set the text
        % object's string to show the data at that point.
        xrange = range(get(axesHdl, 'Xlim'));
        yrange = range(get(axesHdl, 'Ylim'));
        if abs(mouseX - x(ind)) < 0.02*xrange && abs(mouseY - y(ind)) < 0.02*yrange
            set(textHdl, 'String', {['x = ', num2str(x(ind))], ['y = ', num2str(y(ind))]});
            set(textHdl, 'Position', [x(ind) + 0.01*xrange, y(ind) + 0.01*yrange])
        else
            set(textHdl, 'String', '')
        end
    end

end