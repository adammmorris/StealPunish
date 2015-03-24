source("~/git/generalResearch/code/ethicsHeatMaps.R");

for (i in 1:8) {
	setwd(paste(c("/home/amm4/git/generalResearch/output/GAOutput/StealPunish/ESS/Mut/",i,"a"),collapse=""));
	heatMapToImage("GA_parsed.csv","HeatMap_Mut10");
	setwd(paste(c("/home/amm4/git/generalResearch/output/GAOutput/StealPunish/ESS/Mut/",i,"b"),collapse=""));
	heatMapToImage("GA_parsed.csv","HeatMap_Mut10");
}
