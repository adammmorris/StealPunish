fh = figure; hold on;

% When punishing is more difficult
gen = 1:2:100;
p1 = scatter(gen,LSAPT_c(gen),100,'b','LineWidth',2);
p2 = scatter(gen,1-LSAPT_c(gen),100,'black','LineWidth',2);

%xlabel('Generation');
set(gca,'XTick',[]);
ylabel('% of Population');
set(gca,'YTick',[0 1]);

legend({'Steal bias = 0, punish bias = 10', 'Other'},'Position',[.55 .55 .25 .1]);
legend('boxoff');

axs = findall(fh,'Type','axes');
set(axs, 'FontSize', 21, 'LineWidth', 2);
set(findall(fh, 'Type', 'text'), 'FontSize', 21); 	%make all other text correct ,'FontName','Calibri'
set(findall(fh, 'Type', 'line'), 'LineWidth',2);	%make all other lines correct

% When stealing is more difficult
fh = figure; hold on;
gen = 2:2:100;
p3 = scatter(gen,ASLP_nc(gen),100,'filled','rd','LineWidth',2);
p4 = scatter(gen,1-ASLP_nc(gen),100,'filled','kd','LineWidth',2);
legend([p3 p4],{'Steal bias = 10, punish bias = 0','Other'},'Position',[.55 .55 .25 .1]);
legend('boxoff');

axs = findall(fh,'Type','axes');
set(axs, 'FontSize', 21, 'LineWidth', 2);
set(findall(fh, 'Type', 'text'), 'FontSize', 21); 	%make all other text correct ,'FontName','Calibri'
set(findall(fh, 'Type', 'line'), 'LineWidth',2);	%make all other lines correct
xlabel('Generation');
set(gca,'XTick',[0 100]);
ylabel('% of Population');
set(gca,'YTick',[0 1]);