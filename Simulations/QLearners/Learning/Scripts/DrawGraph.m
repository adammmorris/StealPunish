fh = figure;
bar([3.4 94.5; 80.4 11.4]);
legend('Stealing equilibrium', 'Punishing equilibrium','Location','northeastoutside');
set(gca,'XTick',[1 2]);
set(gca,'XTickLabel',{'Not costly','Costly'});
set(gca,'YTick',[0 100]);
ylabel('% of Games');

%pubgraph(fh,14,2,'w');
axs = findall(fh,'Type','axes');
set(axs, 'FontSize', 24, 'LineWidth', 2);
set(findall(fh, 'Type', 'text'), 'FontSize', 24); 	%make all other text correct ,'FontName','Calibri'
set(findall(fh, 'Type', 'line'), 'LineWidth',2);	%make all other lines correct
legend('boxoff');