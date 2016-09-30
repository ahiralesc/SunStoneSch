function pg_history_all_conditions
figure(1);
load results_all_conditions.mat
m=matrix_all_hist_conditions(:,3:22);
plot(m');
hold all;
realruntime = matrix_all_hist_conditions(1,1);
userruntime = matrix_all_hist_conditions(1,2);
plot(0,realruntime,'o');
hold all;
plot(0,userruntime,'o');
legend_={'No condition','p"_j=p''_j','s_j=s_i','p"_j=p''_j && s_j=s_i','Real','User'};
legend(legend_);
grid on;
title({'Comparison of all history conditions','using Hrecent with 0 to 20 jobs','Performance degradation averages'});
xlabel('Window size');
ylabel('Metrics average');
axislimits_fig1=axis;
%axis([-1,20,axislimits(3),axislimits(4)]);
set(gca, 'FontName', 'Arial');
set(gca, 'FontSize', 8);

%
%---------------------------------------------------------------------------------------------
%
figure(2);
load results_all_conditions.mat
m=matrix_all_hist_conditions(:,3:22);
plot(m');
hold all;
realruntime = matrix_all_hist_conditions(1,1);
userruntime = matrix_all_hist_conditions(1,2);
plot(0,realruntime,'o');
hold all;
plot(0,userruntime,'o');
legend_={'No condition','p"_j=p''_j','s_j=s_i','p"_j=p''_j && s_j=s_i','Real','User'};
legend(legend_);
grid on;
title({'Comparison of all history conditions','using Hk with 0 to 20 jobs','Performance degradation averages'});
xlabel('Window size');
ylabel('Metrics average');
axislimits_fig2=axis;
if (axislimits_fig1(3)<axislimits_fig2(3))
    ymin = axislimits_fig1(3);
else
    ymin = axislimits_fig2(3);
end
if (axislimits_fig1(4)>axislimits_fig2(4))
    ymax = axislimits_fig1(4);
else
    ymax = axislimits_fig2(4);
end
figure(1);
axis([-1,20,ymin,ymax]);
figure(2);
axis([-1,20,ymin,ymax]);

set(gca, 'FontName', 'Arial');
set(gca, 'FontSize', 8);

