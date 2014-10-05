function [vectorName] = getVectorName(vector)
numParams = length(vector);

if numParams==8
    pref = vector(1:4)-vector(5:8);
    cutoff = .1;
    for i=1:4
        if pref(i) >= cutoff
            pref(i) = 0;
        elseif pref(i) <= -cutoff
            pref(i) = 1;
        else
            pref(i) = .5;
        end
    end

    % Let's do some known ones
    if isequal(pref,[0 0 0 0]),vectorName='All0';
    elseif isequal(pref,[0 0 0 1]),vectorName='Grim1while1';
    elseif isequal(pref,[0 0 1 0]),vectorName='Grim1while0';
    elseif isequal(pref,[0 0 1 1]),vectorName='RepeatSelf';
    elseif isequal(pref,[0 1 0 1]),vectorName='DoRepeat';
    elseif isequal(pref,[0 1 1 0]),vectorName='Pavlov0';
    elseif isequal(pref,[0 1 1 1]),vectorName='Grim0while0';
    elseif isequal(pref,[1 0 0 1]),vectorName='Pavlov1';
    elseif isequal(pref,[1 0 1 0]),vectorName='DoOpposite';
    elseif isequal(pref,[1 0 1 1]),vectorName='Grim0while1';
    elseif isequal(pref,[1 1 0 0]),vectorName='OppositeSelf';
    elseif isequal(pref,[1 1 1 1]),vectorName='All1';
    else vectorName=['(' num2str(pref(1)) ',' num2str(pref(2)) ',' num2str(pref(3)) ',' num2str(pref(4)) ')'];
    end
else
    vectorName = '';
end
end