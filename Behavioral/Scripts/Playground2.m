rt_pun = [];
rt_nopun = [];
rt_steal = [];
rt_nosteal = [];
for i=1:length(rt)
    if condition(i)==ROLE_THIEF
        if choice(i)==CHOICE_ACTION, rt_steal(end+1) = rt(i);
        else rt_nosteal(end+1) = rt(i); end
    else
        if choice(i)==CHOICE_ACTION, rt_steal(end+1) = rt(i);
        else rt_nosteal(end+1) = rt(i); end
    end
end

mean(rt(condition==ROLE_PUN & choice==CHOICE_ACTION & opChoice==CHOICE_ACTION))

rt_pun = zeros(numSubjects,1);
rt_nopun = zeros(numSubjects,1);
rt_steal = zeros(numSubjects,1);
rt_nosteal = zeros(numSubjects,1);
for i=1:numSubjects
    rt_pun(i) = mean(rt(id==i & condition==ROLE_PUN & choice==CHOICE_ACTION & opChoice==CHOICE_ACTION));
    rt_nopun(i) = mean(rt(id==i & condition==ROLE_PUN & choice==CHOICE_NOTHING & opChoice==CHOICE_ACTION));
    rt_steal(i) = mean(rt(id==i & condition==ROLE_THIEF & choice==CHOICE_ACTION));
    rt_nosteal(i) = mean(rt(id==i & condition==ROLE_THIEF & choice==CHOICE_NOTHING));
end