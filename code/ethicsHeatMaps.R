library(gplots)
library(RColorBrewer)

gacommonheat <- function(data){

heatmap.2(t(data), col=colorRampPalette(c("#596266", "#014363", "#00ACFF", "#CFEFFF"))(n = 1000), breaks=seq(0,1,1/1000), scale="none", Rowv=NA, Colv=NA, labCol= endPointLabels(data), cexCol=1, cexRow=0.75, trace="none", dendrogram="none", density.info="none", xlab="GA Generation", ylab="Agent Type", main="Agent Type Population\nDistribution", margins=c(5, 13))

}


endPointLabels <- function(data){
names = rep("", nrow(data))
names[1] = "0"
names[nrow(data)] = paste(nrow(data)-1)
return(names)
}


heatMapsSMForLR <- function(pathToDir, shortWindow=200){
heatMapToImageWrap(paste(pathToDir, "infGA10", sep=""), shortWindow=shortWindow)
heatMapToImageWrap(paste(pathToDir, "infGA20", sep=""), shortWindow=shortWindow)
heatMapToImageWrap(paste(pathToDir, "infGA50", sep=""), shortWindow=shortWindow)
heatMapToImageWrap(paste(pathToDir, "infGA100", sep=""), shortWindow=shortWindow)
}


heatMapsForLR <- function(pathToDir, shortWindow=200){
	heatMapToImageWrap(paste(pathToDir, "infGA10_0", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA10_01", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA10_05", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA10_10", sep=""), shortWindow=shortWindow)

	heatMapToImageWrap(paste(pathToDir, "infGA25_0", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA25_01", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA25_05", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA25_10", sep=""), shortWindow=shortWindow)

	heatMapToImageWrap(paste(pathToDir, "infGA50_0", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA50_01", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA50_05", sep=""), shortWindow=shortWindow)
	heatMapToImageWrap(paste(pathToDir, "infGA50_10", sep=""), shortWindow=shortWindow)

}

heatMapToImageWrap <- function(pathToDataBaseName, shortWindow=200){
	print(paste("Performing", pathToDataBaseName))
	dataPath = paste(pathToDataBaseName, ".csv", sep="")
	heatMapToImage(dataPath, pathToDataBaseName, shortWindow=shortWindow)
}

heatMapToImage <- function(pathToData, pathToImageBaseName, shortWindow=200){

	distro = read.csv(pathToData, check.names=FALSE)

	if(nrow(distro) >= 2 && ncol(distro) >= 2){
		shortImgPath = paste(pathToImageBaseName, "_", shortWindow, ".png", sep="")
		fullImgPath = paste(pathToImageBaseName, "_", nrow(distro), ".png", sep="")

		print(paste("Rendering", fullImgPath))
		png(shortImgPath, width=640)
		gacommonheat(distro[1:shortWindow,])
		dev.off()

		png(fullImgPath, width=640)
		gacommonheat(distro)
		dev.off()
	}

}

genLR1To9 <- function(){
	heatMapsSMForLR("lr1/")
	heatMapsSMForLR("lr2/")
	heatMapsSMForLR("lr3/")
	heatMapsSMForLR("lr4/")
	heatMapsSMForLR("lr5/")
	heatMapsSMForLR("lr6/")
	heatMapsSMForLR("lr7/")
	heatMapsSMForLR("lr8/")
	heatMapsSMForLR("lr9/")
}