data = matrix(c(80.4,11.4,8.2,0,3.4,94.5,1.1,1.0),ncol=2);
colnames(data) = c("Costly","Not Costly");
rownames(data) = c("Thief Learns","Punisher Learns","Both Learn","Neither Learns");
barplot(data,xlab="Type of Punishment",ylab="Percent of Matches",legend=rownames(data),col=c("darkblue","green","red","black"),xlim=c(0, ncol(data) + 2.5));