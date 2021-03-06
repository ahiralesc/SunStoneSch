function grafica_promedio

load('directories_list_file_G1.mat');
dir_list_G1 = directories_list;
clear directories_list;
load('directories_list_file_G2.mat');
dir_list_G2 = directories_list;
clear directories_list;



if length(dir_list_G1) == length(dir_list_G2)
    fprintf(1, 'Los directorios son iguales\n\n');
else
    fprintf(1, '�PRECAUCI�N!\nLos directorios son diferentes\n\n');
end


for i=1:length(dir_list_G1)
    fprintf(1, '%s\n', dir_list_G1(i).name);
    fprintf(1, '%s\n\n', dir_list_G2(i).name);
end
    
load('results_all_conditions_G1.mat');
matrix_G1 = matrix_all_hist_conditions;
clear directories_list;
load('results_all_conditions_G2.mat');
matrix_G2 = matrix_all_hist_conditions;
clear matrix_all_hist_conditions;

if matrix_G1(1,:) == matrix_G1(1,:)
    fprintf(1, 'Las matrices son iguales\n\n');
else
    fprintf(1, '�PRECAUCI�N!\nLas matrices son diferentes\n\n');
end

matrix_G1G2(1,:) = matrix_G1(1,:);
matrix_G1G2(2,:) = matrix_G2(1,:);
%matrix_G1G2
average_matrix_G1G2 = mean(matrix_G1G2);
total_values = length(average_matrix_G1G2);
average_matrix_G1G2(2,:) = matrix_G1(2,:);
%average_matrix_G1G2
sorted_average_matrix_G1G2 =transpose(sortrows(transpose(average_matrix_G1G2), 1));
%sorted_average_matrix_G1G2

plot(sorted_average_matrix_G1G2(1,:),'oK','LineWidth',1,'MarkerSize',5);

xtick_positions = 1 : total_values;
set(gca,'xtick',xtick_positions);


set(gca,'XTickLabel',sorted_average_matrix_G1G2(2,:));

title({'Average Performance Degradation' 'Average of Grid1 and Grid2'});
ylabel('PERCENT');
xlabel('');





 % Inicia: Est�ndar para gr�ficas de resultados de experimentos
    legend('off');
%    set(gca,'xlim',[1 25]);
    posiciongcf = get(gcf, 'Position');
    set(gcf, 'Position', [posiciongcf(1) posiciongcf(2) 360 390]);
    posiciongca = get(gca,'Position');
    set(gca, 'Position', [posiciongca(1) posiciongca(2)+0.04 posiciongca(3) posiciongca(4)]);
    set(gca, 'FontName', 'Arial');
    set(gca, 'FontSize', 8);
%    set(gca, 'PlotBoxAspectRatioMode','Manual');
    set(gca, 'PlotBoxAspectRatio',[4 4 1]);
    
rot = 90;

%xtick_labels(1:25) = handles.directories_list(list_of_directories_with_data).name;
set(gca,'XTickLabel',[]);
b=get(gca,'XTick');
c=get(gca,'YTick');

for i=1:length(b);
    xtick_label=dir_list_G1(sorted_average_matrix_G1G2(2,i)).name;
    [firstword, remain] = strtok(xtick_label,'-');
    [secondword, remain] = strtok(remain,'-');
    if (strcmp(secondword,'FCFS')==0)
        new_xtick_label = firstword;        
    else
        new_xtick_label = strcat(firstword, '-s');
    end
    text(b(i),c(1)-.1*(c(2)-c(1)),new_xtick_label,'HorizontalAlignment','right','rotation',rot,'FontSize',8,'FontName', 'Arial');
end

set(gca,'YGrid','on')
