% role should be 1 for thief, 2 for punisher
% lastActions should be memory x 1
%   2 for steal/punish, 1 for do nothing
%   in the following order: [lastAction, actionBeforeThat,
%   actionBeforeThat, ...]

function [stateNum] = getStateNum(role,lastActions)
memory = length(lastActions);
cur = cutInHalf(1:(2^(memory+1)),role-1);
for i = 1:memory
    cur = cutInHalf(cur,lastActions(i)-1);
end
stateNum = cur;
end

% length(in) must be even
function [out] = cutInHalf(in,upper)
cut = length(in)/2;
if upper == 1
    out = in((cut+1):end);
else
    out = in(1:cut);
end
end