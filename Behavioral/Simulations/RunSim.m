ROLE_THIEF = 0;
ROLE_PUN = 1;
OPP_INFLEXIBLE = 0;
OPP_FLEXIBLE = 1;
CHOICE_NOTHING = 0;
CHOICE_ACTION = 1;

numSubjects = 50;
results = GenerativeModel_SP([.25 1 .95 0 0],2,.1,numSubjects);

id = results(:,1);
condition = results(:,2);
opChoice = results(:,3);
choice = results(:,4);
reward1 = results(:,5);
reward2 = results(:,6);
matchRound = results(:,7);
opType = zeros(length(id),1);