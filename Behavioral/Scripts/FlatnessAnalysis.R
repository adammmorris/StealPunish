setwd("C:/Personal/School/Brown/Psychology/Thesis/Code/generalResearch/Behavioral/Fiery Studies/Data/Lit5");
require(lme4);

data = read.csv("flatness.csv");
model = glmer(Choice~Role*Round+(1+Role*Round|Subj),family=binomial,data=data);
model_null = glmer(Choice~Role+Round+(1+Role+Round|Subj),family=binomial,data=data);
model_null2 = glmer(Choice~Round+(1+Round|Subj),family=binomial,data=data);