function pg_juan(handles, graphic_type, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)

    %  Get the name of the graphic
    titulo1 = get (handles.edit_graphic_name, 'String');
    %  Get the path to save the graphic
    ruta = get (handles.edit_save_path, 'String');
    %  Get the title of the graphic
    titulo2_ing = regexprep(handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');



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
    xtick_label=handles.directories_list(list_of_directories_with_data(i)).name;
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


    %  Guarda la gr�fica como bmp
    saveas(gca, [ruta titulo1 ' - ' titulo2_ing '(' int2str(graphic_type) ' - ' int2str(total_number_of_graphics) ')' '.bmp'], 'bmp');
    

end

