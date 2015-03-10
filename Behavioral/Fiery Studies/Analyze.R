model = glmer(DV~Predictor*Role+(1+Predictor|Subj),family=binomial,data=data);
model_null = glmer(DV~Predictor+Role+(1+Predictor|Subj),family=binomial,data=data);

# Thief t-test
numSubj_thief = length(unique(data_thief$Subj));
re_lo_thief <- vector(mode='integer',length=numSubj);
re_hi_thief = vector(mode='integer',length=numSubj);
for (i in 1:numSubj) {
	subj = unique(data_thief$Subj)[i];
	re_lo_thief[i] = mean(data_thief[data_thief$Subj==subj,]$DV[data_thief[data_thief$Subj==subj,]$Predictor==0]);
	re_hi_thief[i] = mean(data_thief[data_thief$Subj==subj,]$DV[data_thief[data_thief$Subj==subj,]$Predictor==1]);
}
keep_list = !is.nan(re_lo_thief)&!is.nan(re_hi_thief);
re_lo_thief=re_lo_thief[keep_list];
re_hi_thief=re_hi_thief[keep_list];
numSubj_thief = sum(keep_list);

t.test(re_lo_thief,re_hi_thief,paired=TRUE);
c(mean(re_hi_thief),sd(re_hi_thief)/sqrt(length(re_hi_thief)),mean(re_lo_thief),sd(re_lo_thief)/sqrt(length(re_lo_thief)),mean(re_hi_thief)-mean(re_lo_thief))
	
# Punisher t-test
numSubj_pun = length(unique(data_pun$Subj));
re_lo_pun <- vector(mode='integer',length=numSubj);
re_hi_pun = vector(mode='integer',length=numSubj);
for (i in 1:numSubj) {
	subj = unique(data_pun$Subj)[i];
	re_lo_pun[i] = mean(data_pun[data_pun$Subj==subj,]$DV[data_pun[data_pun$Subj==subj,]$Predictor==0]);
	re_hi_pun[i] = mean(data_pun[data_pun$Subj==subj,]$DV[data_pun[data_pun$Subj==subj,]$Predictor==1]);
}
keep_list = !is.nan(re_lo_pun)&!is.nan(re_hi_pun);
re_lo_pun=re_lo_pun[keep_list];
re_hi_pun=re_hi_pun[keep_list];
numSubj_pun = sum(keep_list);

t.test(re_lo_pun,re_hi_pun,paired=TRUE);
c(mean(re_hi_pun),sd(re_hi_pun)/sqrt(length(re_hi_pun)),mean(re_lo_pun),sd(re_lo_pun)/sqrt(length(re_lo_pun)),mean(re_hi_pun)-mean(re_lo_pun))

# anova
data.raw <- data.frame(id=c(rep(1:numSubj_thief,2),rep(1:numSubj_pun,2)),role=c(rep('Thief',numSubj_thief*2),rep('Punisher',numSubj_pun*2)),trials=c(rep('Low',numSubj_thief),rep('High',numSubj_thief),rep('Low',numSubj_pun),rep('High',numSubj_pun)),value=c(re_lo_thief,re_hi_thief,re_lo_pun,re_hi_pun));
aov.out=aov(value~role*trials + Error(id/(trials)),data=data.raw);
summary(aov.out)	