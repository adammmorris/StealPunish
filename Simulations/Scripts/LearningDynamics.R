data = read.csv("test3.csv");
matplot(data,type = c("b"),pch=1);
legend("topleft", legend = 1:2, col=1:4, pch=1)