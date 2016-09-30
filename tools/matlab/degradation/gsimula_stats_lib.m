    % Acknowledge:
    %           Andrei Tchernykh (chernykh@cicese.mx)
    %           Adan Hirales Carbajal (ahirales@cicese.mx,ahiralesc@hotmail.com)
    %           José Luis González García (jlgonzal@cicese.mx, mabentwickeltsich@gmail.com)
    % Functions and files created by:
    %           Adan Hirales Carbajal
    %           José Luis González García 



    %  Library file with gsimula_stats related functions

    %  Create the object with all functions
    %  file_lib is the name of this file/function
    %  file_lib_obj is the returning object with all functions
function  gsimula_stats_lib_obj = gsimula_stats_lib
    %  gsimula_stats_lib_obj is the object that will contain all functions
    %  .name_of_function is the visible name to exterior scripts
    %  @name_of_function is the local function name

    gsimula_stats_lib_obj.load_all_gsimula_results_files = @load_all_gsimula_results_files;
    gsimula_stats_lib_obj.graphic_results = @graphic_results;
end



%  Load all gsimula results files
function [hObject, handles] = load_all_gsimula_results_files(hObject, handles)

    %  Initialize cells that will contain all experiments data
    handles.execution_data = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    handles.allocation_data = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    %  Initialize cells that will contain all results files names
    handles.execution_files = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    handles.allocation_files = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    %  Initialize cells that will contain all file headers
    handles.execution_file_headers = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    handles.allocation_file_headers = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    %  Initialize matrix that will contain the number of execution and
    %  allocation files in each directory (column1=exec, column2=alloc)
    handles.number_of_files_in_directory (1:handles.total_number_of_experiments_directories, 1:handles.total_number_of_files) = zeros;

    %  Initialize cells that will contain all mean values
    handles.execution_mean = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    handles.allocation_mean = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    %  Initialize cells that will contain all standard deviation values
    handles.execution_std = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);
    handles.allocation_std = cell(handles.total_number_of_experiments_directories, handles.total_number_of_files);

    
    %  All experiments directories
    for i = 1 : handles.total_number_of_experiments_directories
        
        %  All results files
        result_files_list = handles.file_lib_obj.get_files_list([handles.experiments_path '\' handles.directories_list(i).name], '*.txt');
        
        %  Allocation results files
        alloc_result_files_list = handles.file_lib_obj.get_files_list([handles.experiments_path '\' handles.directories_list(i).name], '*allocation.txt');
        if length(alloc_result_files_list) > 0
            alloc_result_files_list = set_alloc_node_results_at_end(alloc_result_files_list);
        end

        %  Initialize variable
        total_number_of_execution_results_files = 0;
        
        %  Gets the execution results files
        for j = 1 : length(result_files_list)
            %  Initialize flag
            file_found = 0;
            
            %  Verifies in all allocation results files
            for k = 1 : length(alloc_result_files_list)
                %  Verifies that the file is not in the list
                if strcmp(result_files_list(j).name, alloc_result_files_list(k).name)
                    file_found = 1;
                end
            end

            %  If the file was not found in allocation results files
            if ~file_found
                %  Increments counter
                total_number_of_execution_results_files = total_number_of_execution_results_files + 1;
                %  Add the file to the list of execution results files
                exec_result_files_list(total_number_of_execution_results_files) = result_files_list(j);
            end
        end

        if length(exec_result_files_list) > 0
            exec_result_files_list = set_exec_node_results_at_end(exec_result_files_list);
        end
        
        
        %fprintf(1, '%s:  Exec=%d, Alloc=%d\n', handles.directories_list(i).name, length(exec_result_files_list), length(alloc_result_files_list))
        
        
        %  Copy all execution files to the cell
        for j = 1 : length(exec_result_files_list)
            handles.execution_files{i, j} = exec_result_files_list(j);
        end
        
        %  Copy all allocation files to the cell
        for j = 1 : length(alloc_result_files_list)
            handles.allocation_files{i, j} = alloc_result_files_list(j);
        end
        
        %  Stores the number of files in the current directory
        handles.number_of_files_in_directory(i, 1) = length(exec_result_files_list);
        handles.number_of_files_in_directory(i, 2) = length(alloc_result_files_list);
        

        %  Copy all execution files headers to the cell
        for j = 1 : length(exec_result_files_list)
            clear header_data;
            clear data;
            header_data = cell(4, 1);
            file_name = [handles.experiments_path '\' handles.directories_list(i).name '\' handles.execution_files{i,j}.name];
            %if i == 1
            %   fprintf(1, '%1.0f.- %s\n', j, handles.execution_files{i,j}.name);
            %end
            [header_data{1, 1}, header_data{2, 1}, header_data{3, 1}, header_data{4, 1}, data] = handles.file_lib_obj.load_gsimula_results_file(file_name);
            handles.execution_file_headers{i, j} = header_data;
            handles.execution_data{i, j} = data;
        end
        
        %  Copy all allocation files headers to the cell
        for j = 1 : length(alloc_result_files_list)
            clear header_data;
            clear data;
            header_data = cell(4, 1);
            file_name = [handles.experiments_path '\' handles.directories_list(i).name '\' handles.allocation_files{i,j}.name];
            %if i == 1
            %   fprintf(1, '%1.0f.- %s\n', j, handles.allocation_files{i,j}.name);
            %end
            [header_data{1, 1}, header_data{2, 1}, header_data{3, 1}, header_data{4, 1}, data] = handles.file_lib_obj.load_gsimula_results_file(file_name);
            handles.allocation_file_headers{i, j} = header_data;
            handles.allocation_data{i, j} = data;
        end
        
        
        %  Calculate all execution standard deviation values
        for j = 1 : length(exec_result_files_list)
            clear std_matrix;
            std_matrix (1:handles.execution_file_headers{i, j}{4,1}) = zeros;
            std_matrix = std(handles.execution_data{i, j}, 0, 1);
            handles.execution_std{i, j} = std_matrix;
        end
    
        %  Calculate all allocation standard deviation values
        for j = 1 : length(alloc_result_files_list)
            clear std_matrix;
            std_matrix (1:handles.allocation_file_headers{i, j}{4,1}) = zeros;
            std_matrix = std(handles.allocation_data{i, j}, 0, 1);
            handles.allocation_std{i, j} = std_matrix;
        end
    
    
        %  Calculate all execution mean values
        for j = 1 : length(exec_result_files_list)
            clear mean_matrix;
            mean_matrix (1:handles.execution_file_headers{i, j}{4,1}) = zeros;
            mean_matrix = mean(handles.execution_data{i, j}, 1);
            handles.execution_mean{i, j} = mean_matrix;
        end
    
        %  Calculate all allocation mean values
        for j = 1 : length(alloc_result_files_list)
            clear mean_matrix;
            mean_matrix (1:handles.allocation_file_headers{i, j}{4,1}) = zeros;
            mean_matrix = mean(handles.allocation_data{i, j}, 1);
            handles.allocation_mean{i, j} = mean_matrix;
        end
    end
end




%  Graphic results
function total_number_of_graphics =  graphic_results(hObject, handles, total_number_of_graphics, experiments_list, criterion_list, allocation_flag, sub_legend)

    %  Identifies system results and node results
    %  because they are graphicated in a different way
    clear system_criterion_list;
    clear node_criterion_list;
    total_number_of_system_criterion_list = 0;
    total_number_of_node_criterion_list = 0;
    if handles.grid_scheduling_levels == 3
        for i = 1 : length(criterion_list)
            if criterion_list(i) > ((handles.number_of_super_clusters + 2) * handles.number_of_global_files)
                total_number_of_node_criterion_list = total_number_of_node_criterion_list + 1;
                node_criterion_list(total_number_of_node_criterion_list) = criterion_list(i);
                %fprintf(1, 'NODE: %1.0f.- %s\n', criterion_list(i), handles.execution_files{1, criterion_list(i)}.name);
            else
                total_number_of_system_criterion_list = total_number_of_system_criterion_list + 1;
                system_criterion_list(total_number_of_system_criterion_list) = criterion_list(i);
                %fprintf(1, 'SYST: %1.0f.- %s\n', criterion_list(i), handles.execution_files{1, criterion_list(i)}.name);
            end
        end
    else
        for i = 1 : length(criterion_list)
            if criterion_list(i) > handles.number_of_global_files
                total_number_of_node_criterion_list = total_number_of_node_criterion_list + 1;
                node_criterion_list(total_number_of_node_criterion_list) = criterion_list(i);
                %fprintf(1, 'NODE: %1.0f.- %s\n', criterion_list(i), handles.execution_files{1, criterion_list(i)}.name);
            else
                total_number_of_system_criterion_list = total_number_of_system_criterion_list + 1;
                system_criterion_list(total_number_of_system_criterion_list) = criterion_list(i);
                %fprintf(1, 'SYST: %1.0f.- %s\n', criterion_list(i), handles.execution_files{1, criterion_list(i)}.name);
            end
        end
    end



    %  All system criterions to be graphicated
    for i = 1 : total_number_of_system_criterion_list
    
        %  Initialize variables
        number_of_experiments = 0;
        number_of_directories_with_data = 0;
        clear list_of_directories_with_data;

        %  Find all directories with data for actual criterion
        for j = 1 : length(experiments_list)
            if allocation_flag
                test_size = size(handles.allocation_data{experiments_list(j), system_criterion_list(i)});
            else
                test_size = size(handles.execution_data{experiments_list(j), system_criterion_list(i)});
            end
            if number_of_experiments < test_size(1, 1)
                number_of_experiments = test_size(1, 1);
            end
            if test_size(1, 1) > 0 && test_size(1, 2) > 0
                number_of_directories_with_data = number_of_directories_with_data + 1;
                list_of_directories_with_data(number_of_directories_with_data) = experiments_list(j);
            end
        end

        % If at last one directory has data, then graphic data
        if number_of_directories_with_data > 0

            node_flag = 0;

            %  If graphic per experiment is going to be graphicated
            if (get(handles.checkbox_g_per_experiment,'Value') == get(handles.checkbox_g_per_experiment,'Max'))
                total_number_of_graphics = build_histogram_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, system_criterion_list(i), node_flag, allocation_flag, sub_legend);
            end

            %  If graphic of meand and std is going to be graphicated
            if (get(handles.checkbox_g_mean_std,'Value') == get(handles.checkbox_g_mean_std,'Max'))
                total_number_of_graphics = build_mean_std_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, system_criterion_list(i), node_flag, allocation_flag, sub_legend);
            end

            %  If graphic per percent is going to be graphicated
            if (get(handles.checkbox_g_percent,'Value') == get(handles.checkbox_g_percent,'Max'))
                total_number_of_graphics = build_percent_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, system_criterion_list(i), node_flag, allocation_flag, sub_legend,total_number_of_system_criterion_list);
            end


        end
    end

    
    %  All node criterions to be graphicated
    for i = 1 : total_number_of_node_criterion_list
    
        %  Initialize variables
        number_of_experiments = 0;
        number_of_directories_with_data = 0;
        clear list_of_directories_with_data;

        %  Find all directories with data for actual criterion
        for j = 1 : length(experiments_list)
            if allocation_flag
                test_size = size(handles.allocation_data{experiments_list(j), node_criterion_list(i)});
            else
                test_size = size(handles.execution_data{experiments_list(j), node_criterion_list(i)});
            end
            if number_of_experiments < test_size(1, 1)
                number_of_experiments = test_size(1, 1);
            end
            if test_size(1, 1) > 0 && test_size(1, 2) > 0
                number_of_directories_with_data = number_of_directories_with_data + 1;
                list_of_directories_with_data(number_of_directories_with_data) = experiments_list(j);
            end
        end

        % If at last one directory has data, then graphic data
        if number_of_directories_with_data > 0

            node_flag = 1;
            for j = 1: number_of_directories_with_data
                list_of_dirs(1) = list_of_directories_with_data(j);
                
                %  If graphic per experiment is going to be graphicated
                if (get(handles.checkbox_g_per_experiment,'Value') == get(handles.checkbox_g_per_experiment,'Max'))
                    total_number_of_graphics = build_histogram_results(handles, total_number_of_graphics, number_of_experiments, list_of_dirs, node_criterion_list(i), node_flag, allocation_flag, sub_legend);
                end

                %  If graphic of meand and std is going to be graphicated
                if (get(handles.checkbox_g_mean_std,'Value') == get(handles.checkbox_g_mean_std,'Max'))
                    total_number_of_graphics = build_mean_std_results(handles, total_number_of_graphics, number_of_experiments, list_of_dirs, node_criterion_list(i), node_flag, allocation_flag, sub_legend);
                end

                %  If graphic per percent is going to be graphicated
                if (get(handles.checkbox_g_percent,'Value') == get(handles.checkbox_g_percent,'Max'))
                    total_number_of_graphics = build_percent_results(handles, total_number_of_graphics, number_of_experiments, list_of_dirs, node_criterion_list(i), node_flag, allocation_flag, sub_legend);
                end
            end

        end
    end
    
    
    
    
end



function [hObject, handles] = to_permanent_storage(hObject, handles)
  disp('Damit it works');
  %fp = fopen('c:\data.txt','w+');
  %fwrite(fid,magic(5),'integer*4');
  %fclose(fp);
end



%%%%%%%%%%%%%%%%%%%%%%%
%  Local functions
%%%%%%%%%%%%%%%%%%%%%%%

%  Sort the list of execution result files,
%  all node result files will be at the end
function sorted_file_list = set_exec_node_results_at_end(file_list)

    total_number_of_files_copied = 0;
    for i = 1 : length(file_list)
        test_length = length(file_list(i).name);
        if test_length > 8
            if ~strcmp(file_list(i).name((test_length - 7):test_length), 'node.txt')
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
            end
        else
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
        end
    end

    
    for i = 1 : length(file_list)
        test_length = length(file_list(i).name);
        if test_length > 8
            if strcmp(file_list(i).name((test_length - 7):test_length), 'node.txt')
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
            end
        end
    end
end


%  Sort the list of allocation result files,
%  all node result files will be at the end
function sorted_file_list = set_alloc_node_results_at_end(file_list)

    total_number_of_files_copied = 0;
    for i = 1 : length(file_list)
        test_length = length(file_list(i).name);
        if test_length > 19
            if ~strcmp(file_list(i).name((test_length - 18):test_length), 'node_allocation.txt')
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
            end
        else
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
        end
    end

    
    for i = 1 : length(file_list)
        test_length = length(file_list(i).name);
        if test_length > 19
            if strcmp(file_list(i).name((test_length - 18):test_length), 'node_allocation.txt')
                total_number_of_files_copied = total_number_of_files_copied + 1;
                sorted_file_list(total_number_of_files_copied) = file_list(i);
            end
        end
    end
end






function total_number_of_graphics = build_histogram_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)

            %  Histogram main results
            
            
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
            
            %  Get the data from the cell
            clear matrix_of_results;
            if node_flag
                if allocation_flag
                    matrix_of_results = handles.allocation_data{list_of_directories_with_data(1), criterion_to_graphic};
                else
                    matrix_of_results = handles.execution_data{list_of_directories_with_data(1), criterion_to_graphic};
                end
            else
                matrix_of_results (number_of_experiments, number_of_columns_of_data) = zeros;
                for j = 1 : number_of_columns_of_data
                    if allocation_flag
                        matrix_of_results (:, j) = handles.allocation_data{list_of_directories_with_data(j), criterion_to_graphic};
                    else
                        matrix_of_results (:, j) = handles.execution_data{list_of_directories_with_data(j), criterion_to_graphic};
                    end
                end
            end

            %  The number of the graphic
            total_number_of_graphics = total_number_of_graphics + 1;
            figure(total_number_of_graphics);

            %  Graphic data
            graphic_bar = bar(matrix_of_results);
            
            %  Set the font name and size
            set(gca, 'FontName', 'Times New Roman');
            set(gca, 'FontSize', 10);

            %  Add titles
            if allocation_flag
                title(replace_char(handles.allocation_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' '));
            else
                title(replace_char(handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' '));
            end
            ylabel('');
            xlabel('EXPERIMENT');
            
            if (get(handles.checkbox_two_columns,'Value') == get(handles.checkbox_two_columns,'Max'))
                if (number_of_columns_of_data > 1)
                    two_columns_legend = 1;
                else
                    two_columns_legend = 0;
                end
            else
                two_columns_legend = 0;
            end
            if (get(handles.radiobutton_vertical,'Value') == get(handles.radiobutton_vertical,'Max'))
                vertical_legend = 1;
            else
                vertical_legend = 0;
            end
            if (two_columns_legend)
                position_gcf = get(gcf,'Position');
                new_position_gcf = position_gcf;
                if (vertical_legend)
                    new_position_gcf(4) = new_position_gcf(4) + 25 + (19 * (ceil(number_of_columns_of_data / 2) - 1));
                    new_position_gcf(2) = new_position_gcf(2) - 25 - (19 * (ceil(number_of_columns_of_data / 2) - 1));
                else
                    new_position_gcf(4) = new_position_gcf(4) + 50;
                    new_position_gcf(2) = new_position_gcf(2) - 50;
                end
                set(gcf,'Position', new_position_gcf);
                set(gca,'OuterPosition', [0, (1-(position_gcf(4)/new_position_gcf(4))), 1, (position_gcf(4)/new_position_gcf(4))]);
            end
            
            %  Set the legend to the graphic
            clear legend_cell;
            clear legend_cell2;
            if (two_columns_legend)
                legend_cell_half1 = cell(1, ceil(number_of_columns_of_data / 2));
                legend_cell_half2 = cell(1, (number_of_columns_of_data - ceil(number_of_columns_of_data / 2)));
            else
                legend_cell = cell(1, number_of_columns_of_data);
            end
            legend_counter = 0;
            legend_row = 1;
            for j = 1 : number_of_columns_of_data
                legend_counter = legend_counter + 1;
                if node_flag
                    if (two_columns_legend)
                        if (legend_row == 1)
                            legend_cell_half1{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(1)).name ': Node ' num2str(j, '%1.0f')];
                        else
                            legend_cell_half2{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(1)).name ': Node ' num2str(j, '%1.0f')];
                        end
                        if (j == ceil(number_of_columns_of_data / 2))
                            legend_counter = 0;
                            legend_row = 2;
                        end
                    else
                        legend_cell{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(1)).name ': Node ' num2str(j, '%1.0f')];
                    end
                else
                    if (two_columns_legend)
                        if (legend_row == 1)
                            legend_cell_half1{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(j)).name];
                        else
                            legend_cell_half2{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(j)).name];
                        end
                        if (j == ceil(number_of_columns_of_data / 2))
                            legend_counter = 0;
                            legend_row = 2;
                        end
                    else
                        legend_cell{1, legend_counter} = [sub_legend, handles.directories_list(list_of_directories_with_data(j)).name];
                    end
                end
            end
            
            if (two_columns_legend)
                if (vertical_legend)
                    [graphic_legend, graphic_h] = legend(graphic_bar([(1):(ceil(number_of_columns_of_data / 2))]), legend_cell_half1{1, :}, 'Location','South','Orientation','Vertical');
                else
                    [graphic_legend, graphic_h] = legend(graphic_bar([(1):(ceil(number_of_columns_of_data / 2))]), legend_cell_half1{1, :}, 'Location','South','Orientation','Horizontal');
                end
                set(graphic_legend,'Interpreter','none');
                graphic_legend_position = get(graphic_legend,'Position');
                
                new_graphic_legend = copyobj(graphic_legend, gcf);
                if (vertical_legend)
                    set(new_graphic_legend,'Interpreter','none');
                else
                    set(new_graphic_legend,'orientation','Vertical');
                    set(new_graphic_legend,'Orientation','Horizontal');
                    set(new_graphic_legend,'Interpreter','none');
                    new_graphic_legend_position = get(new_graphic_legend,'Position');
                    set(new_graphic_legend,'Position', [new_graphic_legend_position(1), new_graphic_legend_position(2), new_graphic_legend_position(3), graphic_legend_position(4)]);
                end

                if (vertical_legend)
                    [new_graphic_legend, graphic_h] = legend(graphic_bar([(ceil(number_of_columns_of_data / 2) + 1):(number_of_columns_of_data)]), legend_cell_half2{1, :}, 'Location','South','Orientation','Vertical');
                else
                    [new_graphic_legend, graphic_h] = legend(graphic_bar([(ceil(number_of_columns_of_data / 2) + 1):(number_of_columns_of_data)]), legend_cell_half2{1, :}, 'Location','South','Orientation','Horizontal');
                end
                set(new_graphic_legend,'Interpreter','none');
            else
                if (vertical_legend)
                    [graphic_legend, graphic_h] = legend(cellstr(legend_cell),number_of_columns_of_data, 'Location', 'SouthOutside','Orientation','Vertical');
                else
                    [graphic_legend, graphic_h] = legend(cellstr(legend_cell),number_of_columns_of_data, 'Location', 'SouthOutside','Orientation','Horizontal');
                end
                set(graphic_legend,'Interpreter','none');
            end
  
            %axes_position = get(gca,'Position');
            %axes_position(2) = axes_position(2) + .05 
            %set(gca,'Position', axes_position);

            %  Function to prepare and save the graphic
            if (get(handles.checkbox_prepare_graphic,'Value') == get(handles.checkbox_prepare_graphic,'Max'))
                function_name = get (handles.edit_function_name, 'String');
                eval([function_name '(handles, 1, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)']);
            end

end





function total_number_of_graphics = build_mean_std_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)

            %  Mean and standard deviation
            
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

            %  Get the data from the cell
            clear matrix_of_mean;
            clear matrix_of_std;
            if node_flag
                matrix_of_mean (1, number_of_columns_of_data) = zeros;
                matrix_of_std (1, number_of_columns_of_data) = zeros;
                if allocation_flag
                    matrix_of_mean = handles.allocation_mean{list_of_directories_with_data(1), criterion_to_graphic};
                    matrix_of_std = handles.allocation_std{list_of_directories_with_data(1), criterion_to_graphic};
                else
                    matrix_of_mean = handles.execution_mean{list_of_directories_with_data(1), criterion_to_graphic};
                    matrix_of_std = handles.execution_std{list_of_directories_with_data(1), criterion_to_graphic};
                end
                matrix_of_mean (2, :) = matrix_of_mean (1, :);
                matrix_of_std (2, :) = matrix_of_std (1, :);
            else
                matrix_of_mean (1, number_of_columns_of_data) = zeros;
                for j = 1 : number_of_columns_of_data
                    if allocation_flag
                        matrix_of_mean (:, j) = handles.allocation_mean{list_of_directories_with_data(j), criterion_to_graphic};
                    else
                        matrix_of_mean (:, j) = handles.execution_mean{list_of_directories_with_data(j), criterion_to_graphic};
                    end
                end
                matrix_of_mean (2, :) = matrix_of_mean (1, :);

                matrix_of_std (1, number_of_columns_of_data) = zeros;
                for j = 1 : number_of_columns_of_data
                    if allocation_flag
                        matrix_of_std (:, j) = handles.allocation_std{list_of_directories_with_data(j), criterion_to_graphic};
                    else
                        matrix_of_std (:, j) = handles.execution_std{list_of_directories_with_data(j), criterion_to_graphic};
                    end
                end
                matrix_of_std (2, :) = matrix_of_std (1, :);
            end

            %  The number of the graphic
            total_number_of_graphics = total_number_of_graphics + 1;
            figure(total_number_of_graphics);

            hold on;
            %  Graphic data
%            graphic_bar = bar(matrix_of_mean);
            errorbar(matrix_of_mean(1,:),matrix_of_std(1,:),'oK','LineWidth',1,'MarkerSize',5);

            xtick_positions = 1 : number_of_columns_of_data;
            set(gca,'xtick',xtick_positions);

            %  Add titles
            if allocation_flag
                title_of_char = replace_char(handles.allocation_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');
            else
                title_of_char = replace_char(handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');
            end
            title(title_of_char);
            ylabel('');
            xlabel('');
            set(gcf,'name',title_of_char);
            hold off;

            %  Function to prepare and save the graphic
            if (get(handles.checkbox_prepare_graphic,'Value') == get(handles.checkbox_prepare_graphic,'Max'))
                function_name = get (handles.edit_function_name, 'String');
                eval([function_name '(handles, 2, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)']);
            end

end




function total_number_of_graphics = build_percent_results(handles, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend,total_number_of_system_criterion_list)

            %  Percent values
            
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

            %  Get the data from the cell
            clear matrix_of_mean;
            if node_flag
                matrix_of_mean (1, number_of_columns_of_data) = zeros;
                if allocation_flag
                    matrix_of_mean = handles.allocation_mean{list_of_directories_with_data(1), criterion_to_graphic};
                else
                    matrix_of_mean = handles.execution_mean{list_of_directories_with_data(1), criterion_to_graphic};
                end
            else
                matrix_of_mean (1, number_of_columns_of_data) = zeros;
                for j = 1 : number_of_columns_of_data
                    if allocation_flag
                        matrix_of_mean (:, j) = handles.allocation_mean{list_of_directories_with_data(j), criterion_to_graphic};
                    else
                        matrix_of_mean (:, j) = handles.execution_mean{list_of_directories_with_data(j), criterion_to_graphic};
                    end
                end
            end

            if allocation_flag
                evaluation_criterion = handles.allocation_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{2, 1};
            else
                evaluation_criterion = handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{2, 1};
            end
            if strcmp(evaluation_criterion, 'Minimize');
                opt_mean_value = max(matrix_of_mean);
                for j = 1 : length(matrix_of_mean)
                    if (matrix_of_mean(1, j) ~= 0) && (matrix_of_mean(1, j) < opt_mean_value)
                        opt_mean_value = matrix_of_mean(1, j);
                    end
                end    
                %opt_mean_value = min(matrix_of_mean);
            else
                opt_mean_value = max(matrix_of_mean);
            end

            clear matrix_of_percent;
            matrix_of_percent (1, number_of_columns_of_data) = zeros;
            if strcmp(evaluation_criterion, 'Minimize');            
                for j = 1 : number_of_columns_of_data
                    matrix_of_percent (1, j) = (matrix_of_mean(1, j) / opt_mean_value * 100)-100;
                end
            else
                 for j = 1 : number_of_columns_of_data
                    matrix_of_percent (1, j) =  abs((matrix_of_mean(1, j) / opt_mean_value * 100)-100);        %FOR MAXIMIZATION
                end
            end
         
            %  The number of the graphic
            total_number_of_graphics = total_number_of_graphics + 1;
            figure(total_number_of_graphics);
            
%--------------------------------------------------------------------------
            if(total_number_of_graphics ~= 1)
                load results_for_rank matrix_of_percent_overall;
            end

            matrix_of_percent_overall(total_number_of_graphics, :) = matrix_of_percent(1,:);

            save results_for_rank matrix_of_percent_overall;
%--------------------------------------------------------------------------            

            %  Graphic data
%           graphic_bar = bar(matrix_of_percent);
            plot(matrix_of_percent(1,:),'oK','LineWidth',1,'MarkerSize',5);

            xtick_positions = 1 : number_of_columns_of_data;
            set(gca,'xtick',xtick_positions);

            
%             %  Set the font name and size
%             set(gca, 'FontName', 'Times New Roman');
%             set(gca, 'FontSize', 10);

            %  Add titles
            if allocation_flag
                title_of_char = replace_char(handles.allocation_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');
            else
                title_of_char = replace_char(handles.execution_file_headers{list_of_directories_with_data(1), criterion_to_graphic}{3, 1}, '_', ' ');
            end

           % title({title_of_char,'Performance degradation'});
            ylabel('PERCENT');
            xlabel('');
            title_of_char = strcat(title_of_char,' P_degradation');
            set(gcf,'name',title_of_char);



            %  Function to prepare and save the graphic
            if (get(handles.checkbox_prepare_graphic,'Value') == get(handles.checkbox_prepare_graphic,'Max'))
                function_name = get (handles.edit_function_name, 'String');
                eval([function_name '(handles, 3, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)']);
            end
            
            %8888888888888888888888888888888888888888888888888888888888
            if(total_number_of_graphics == total_number_of_system_criterion_list)
                load results_for_rank matrix_of_percent_overall;

                
                matrix_for_rank(1,:) = mean(matrix_of_percent_overall,1);
                matrix_for_rank(2,:) = list_of_directories_with_data(:);
                matrix_for_rank(3,:) = std(matrix_of_percent_overall,1,1);
                
%                save other_results matrix_for_rank;

                [matrix_for_rank_sorted,sorted_index] = sort(matrix_for_rank,2);
                              
                for k=1:number_of_columns_of_data;
                    matrix_for_rank_sorted(2,k) = matrix_for_rank(2,sorted_index(1,k));
                    matrix_for_rank_sorted(3,k) = matrix_for_rank(3,sorted_index(1,k));
%                     labels_ordered(k) = matrix_for_rank_sorted(sorted_index(k,1),2);
%                     std_dev_ordered(k) = C(IX(k,1),2);
                end
                
                figure(total_number_of_graphics+1);

                errorbar(matrix_for_rank_sorted(1,:),matrix_for_rank_sorted(3,:),'oK','LineWidth',1,'MarkerSize',5);               

                xtick_positions = 1 : number_of_columns_of_data;
                set(gca,'xtick',xtick_positions);


                set(gca,'XTickLabel',matrix_for_rank_sorted(2,:));

                title({'Average Performance Degradation'});
                ylabel('PERCENT');
                xlabel('');

                if (get(handles.checkbox_prepare_graphic,'Value') == get(handles.checkbox_prepare_graphic,'Max'))
                    function_name = get (handles.edit_function_name, 'String');
                    %eval([function_name '(handles, 3, total_number_of_graphics, number_of_experiments, list_of_directories_with_data, criterion_to_graphic, node_flag, allocation_flag, sub_legend)']);
                    eval([function_name '(handles, 3, total_number_of_graphics, number_of_experiments, matrix_for_rank_sorted(2,:), criterion_to_graphic, node_flag, allocation_flag, sub_legend)']);
                end
                
              %%%%%%%%%%%%%%%%
              button = questdlg('Save average results?','Average resuts','Yes','No','Yes');
              if (strcmp(button,'Yes')== 1)
                 file_exists = fopen('results_all_conditions.mat', 'r');

                  if(file_exists==-1)
                      matrix_all_hist_conditions  = matrix_for_rank;
                      save results_all_conditions matrix_all_hist_conditions;
                      directories_list = handles.directories_list;
                      save directories_list_file directories_list;
                  else
                      fclose(file_exists);
                      button = questdlg('File ''results_all_conditions.mat'' already exists. Add data or rewrite it?','File existing','Add','Rewrite','Cancel');
                      switch(button)
                          case 'Add'
                              load results_all_conditions matrix_all_hist_conditions;
                              [mrows,mcols] = size(matrix_all_hist_conditions);
                              matrix_all_hist_conditions(mrows+1, :) = matrix_for_rank(1,:) ;
                              save results_all_conditions matrix_all_hist_conditions;
                          case 'Rewrite'
                              matrix_all_hist_conditions(1,:)  = matrix_for_rank(1, :);
                              save results_all_conditions matrix_all_hist_conditions;
                          otherwise
                              load results_all_conditions;
                      end
                  end
                  button = questdlg ('Show graph with comparison of all conditions?','Show final graph','Yes');
                  if (strcmp(button,'Yes')==1)
                      figure(total_number_of_graphics+2);
                      plot(matrix_all_hist_conditions');
                  end
              end
              %%%%%%%%%%%%%%%%
            
              delete results_for_rank.m;
            end
            %8888888888888888888888888888888888888888888888888888888888
            %

end


function modified_string = replace_char(original_string, original_char, new_char)
    for i = 1 : length(original_string)
        if strcmp(original_string(i:i), original_char(1))
            modified_string(i) = new_char(1);
        else
            modified_string(i) = original_string(i);
        end
    end
end



%%%%%%%%%%%%%%%%%%%%%%%
%  Saves data to a text file
%%%%%%%%%%%%%%%%%%%%%%%

%  Sort the list of execution result files,
%  all node result files will be at the end

