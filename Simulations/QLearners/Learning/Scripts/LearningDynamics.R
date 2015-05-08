data = read.csv("LSvsAP.csv");
matplot(data,type = c("b"),pch=1,xlab="Round",ylab="Number of actions",ylim=c(0,5000));
legend("topleft", legend = c("Cumulative steals","Cumulative punishes"), col=1:4, pch=1);
title(main="LS vs AP");