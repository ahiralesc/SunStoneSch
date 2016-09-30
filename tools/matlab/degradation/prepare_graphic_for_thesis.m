    % Acknowledge:
    %           Andrei Tchernykh (chernykh@cicese.mx)
    %           José Luis González García (jlgonzal@cicese.mx, mabentwickeltsich@gmail.com)
    % Functions and files created by:
    %           José Luis González García (jlgonzal@cicese.mx, mabentwickeltsich@gmail.com)


function prepare_graphic_for_thesis(handles, graphic_type, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)
%  NOTE: The parameters for each graphic prostprocessing function MUST BE
%  the parameters recived by this function in the same order



    %  Get the name of the graphic
    titulo1 = get (handles.edit_graphic_name, 'String');
    %  Get the path to save the graphic
    ruta = get (handles.edit_save_path, 'String');
    %  Get the title of the graphic
    titulo2_ing = regexprep(handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');
    %  Get the size of array
    number_of_directories_with_data = length(list_of_directories_with_data);


    %  Get the number of columns of data
    if node_flag
        if allocation_flag
            number_of_columns_of_data = handles.allocation_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{4, 1};
        else
            number_of_columns_of_data = handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{4, 1};
        end
    else
        number_of_columns_of_data = number_of_directories_with_data;
    end



%  Translate the original title to spanish, separate the title in two rows
%  set the limits of axis y and the y position of labels
switch titulo2_ing
  case 'C max' 
    titulo2 = 'Longitud del calendario';
    tituloy = {'Longitud    ' 'del calendario    '};
    set(gca,'ylim',[0 60000000]);
    yposition = 0;
  case 'Throughput'
    titulo2 = 'Rendimiento de procesamiento';
    tituloy = {'Rendimiento    ' 'de procesamiento    '};
    set(gca,'ylim',[0 0.0150]);
    yposition = 0;
  case 'Utilization' 
    titulo2 = 'Utilización del sistema';
    tituloy = {'Utilización    ' 'del sistema    '};
    set(gca,'ylim',[0 0.8000]);
    yposition = 0;
  case 'Total idle'
    titulo2 = 'Recursos no utilizados';
    tituloy = {'Recursos    ' 'no utilizados    '};
    set(gca,'ylim',[-100000000 6500000000]);
    %set(gca,'ylim',[0 7000000000]);
    yposition = -100000000;
  case 'Total work' 
    titulo2 = 'Trabajo total realizado';
    tituloy = {'Trabajo total    ' 'realizado    '};
    set(gca,'ylim',[0 800000000]);
    yposition = 0;
  case 'Mean Turnaround'
    titulo2 = 'Tiempo promedio de permanencia';
    tituloy = {'Tiempo promedio    ' 'de permanencia    '};
    set(gca,'ylim',[0 20000000]);
    yposition = 0;
  case 'Mean waiting time' 
    titulo2 = 'Tiempo promedio de espera';
    tituloy = {'Tiempo promedio    ' 'de espera    '};
    set(gca,'ylim',[0 20000000]);
    yposition = 0;
  case 'Mean response ratio'
    titulo2 = 'Cociente de respuesta promedio';
    tituloy = {'Cociente de    ' 'respuesta promedio    '};
    set(gca,'ylim',[0 1200000]);
    yposition = 0;
  case 'Mean response time' 
    titulo2 = 'Tiempo de respuesta promedio';
    tituloy = {'Tiempo de    ' 'respuesta promedio    '};
    set(gca,'ylim',[0 20000000]);
    yposition = 0;
  case 'System response ratio'
    titulo2 = 'Cociente de respuesta del sistema';
    tituloy = {'Cociente de    ' 'respuesta del sistema    '};
    set(gca,'ylim',[0 7000]);
    yposition = 0;
  case 'Competitive ratio' 
    titulo2 = 'Cociente de competitividad';
    tituloy = {'Cociente de    ' 'competitividad    '};
    set(gca,'ylim',[0 12]);
    yposition = 0;
  case 'Mean bounded response ratio'
    titulo2 = 'Cociente de respuesta promedio acotado';
    tituloy = {'Cociente de respuesta    ' 'promedio acotado    '};
    set(gca,'ylim',[0 700000]);
    yposition = 0;
  case 'Mean weighted turnaround' 
    titulo2 = 'Tiempo promedio de permanencia ponderado';
    tituloy = {'Tiempo promedio    ' 'de permanencia ponderado    '};
    set(gca,'ylim',[0 60000000]);
    yposition = 0;
  case 'Mean weighted (work) turnaround'
    titulo2 = 'Tiempo promedio de permanencia ponderado por trabajo';
    tituloy = {'Tiempo promedio de permanencia    ' 'ponderado por trabajo    '};
    set(gca,'ylim',[-30000000000 220000000000]);
    %set(gca,'ylim',[-50000000000 250000000000]);
    yposition = -30000000000;
  case 'Mean weighted waiting time' 
    titulo2 = 'Tiempo promedio de espera ponderado';
    tituloy = {'Tiempo promedio    ' 'de espera ponderado    '};
    set(gca,'ylim',[0 60000000]);
    yposition = 0;
  otherwise
    titulo2 = ['-' titulo2_ing '-'];
    tituloy = ['-' titulo2_ing '-'];
    yposition = 0;
end


    % Inicia: Estándar para gráficas de resultados de experimentos
    legend('off');
    set(gca,'xlim',[0.6 1.4]);
    posicion = get(gcf, 'Position');
    set(gcf, 'Position', [posicion(1) posicion(2) 280 200]);
    set(gca, 'FontName', 'Times New Roman');
    set(gca, 'FontSize', 10);
    
    %  Asigna las etiquetas de los ejes y el título a las gráficas
    title(['      Estrategia ' regexprep(titulo1, '_', '\\_')]);
    xlabel({'' 'Grado de admisibilidad'});
    ylabel(tituloy);

    %  Calcular posición x de las etiquetas
    reference_position = 0;
    switch number_of_columns_of_data
        case 1
            reference_position = .50;
        case 2
            reference_position = .56;
        case 3
            reference_position = .67;
        case 4
            reference_position = .72;
        case 5
            reference_position = .77;
        otherwise
            reference_position = .80;
    end
    bar_separation = reference_position / number_of_columns_of_data;
    bar_first_position = 1 - (bar_separation * number_of_columns_of_data / 2) + bar_separation / 2;
    xtick_positions=zeros(1, number_of_columns_of_data);
    xtick_labels= cell(1, number_of_columns_of_data);
    for j = 1 : number_of_columns_of_data
        xtick_positions(j) = bar_first_position + (j - 1) * bar_separation;
        xtick_labels{1, j} = [num2str((j - 1) * 10) '%'];
    end
    
    %  Calcula la posición y de las etiquetas
    xtick_ypositions=repmat(yposition, number_of_columns_of_data, 1);

    %  Crea las etiquetas verticales de las gráficas
    set(gca,'XTick',xtick_positions,'XTickLabel','')
    text(xtick_positions, xtick_ypositions, xtick_labels,'HorizontalAlignment','Right','Rotation', 90, 'FontName', 'Times New Roman', 'FontSize', 10);
    
    %  Guarda la gráfica como bmp
    saveas(gca, [ruta titulo1 ' - ' titulo2 '.bmp'], 'bmp');
    
    % Termina: Estándar para gráficas de resultados de experimentos

end
