    % Acknowledge:
    %           Andrei Tchernykh (chernykh@cicese.mx)
    %           José Luis González García (jlgonzal@cicese.mx, mabentwickeltsich@gmail.com)
    % Functions and files created by:
    %           José Luis González García (jlgonzal@cicese.mx, mabentwickeltsich@gmail.com)


function varargout = gsimula_stats(varargin)
% GSIMULA_STATS M-file for gsimula_stats.fig
%      GSIMULA_STATS, by itself, creates a new GSIMULA_STATS or raises the existing
%      singleton*.
%
%      H = GSIMULA_STATS returns the handle to a new GSIMULA_STATS or the handle to
%      the existing singleton*.
%
%      GSIMULA_STATS('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in GSIMULA_STATS.M with the given input arguments.
%
%      GSIMULA_STATS('Property','Value',...) creates a new GSIMULA_STATS or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before gsimula_stats_OpeningFunction gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to gsimula_stats_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help gsimula_stats

% Last Modified by GUIDE v2.5 27-Nov-2009 14:38:55

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @gsimula_stats_OpeningFcn, ...
                   'gui_OutputFcn',  @gsimula_stats_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before gsimula_stats is made visible.
function gsimula_stats_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to gsimula_stats (see VARARGIN)

% Choose default command line output for gsimula_stats
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes gsimula_stats wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = gsimula_stats_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in pushbutton_select_source_path.
function pushbutton_select_source_path_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_select_source_path (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

    %  Get the experiments path
    source_path = uigetdir('E:\Doctorado\Proyecto GSimula\GSimula_v_4_0-23_Mar_2009(Working copy)\gsimula\source_code');

    %  If user canceled, then do nothing
    if isequal(source_path,0)
        %disp('User selected Cancel');
        %handles.experiments_path = '';
    else
        %disp(['User selected: ', fullfile(workload_path_file_name, workload_file_name)]);
        %  Stores the experiments path
        handles.experiments_path = source_path;
        set (handles.text_source_path, 'String', source_path);
        
        %  Get the list of all experiments directories
        handles.directories_list = handles.file_lib_obj.get_directories_list(source_path);
        
        %  Number of experiments directories
        handles.total_number_of_experiments_directories = length(handles.directories_list);
        
        %  Initialize variable
        directories_name_list = cell(1, 1);

        %  Make listbox multiselect
        set(handles.listbox_experiments_list,'Max',2); 
        
        %  Enable button to load data
        set (handles.pushbutton_load_experiments, 'Enable', 'on');

    end
    
    %  Update handles structure
    guidata(hObject, handles);
    



% --- Executes on selection change in listbox_experiments_list.
function listbox_experiments_list_Callback(hObject, eventdata, handles)
% hObject    handle to listbox_experiments_list (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns listbox_experiments_list contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listbox_experiments_list


% --- Executes during object creation, after setting all properties.
function listbox_experiments_list_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listbox_experiments_list (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in pushbutton_load_experiments.
function pushbutton_load_experiments_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_load_experiments (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

    %  Calculate the number of metric files
    handles.number_of_global_files = str2num(get (handles.edit_num_global_files, 'String'));
    handles.number_of_node_files = str2num(get (handles.edit_num_node_files, 'String'));
    handles.total_number_of_files = handles.number_of_global_files + handles.number_of_node_files;

    %  Update handles structure
    guidata(hObject, handles);

    %  Load all data from results files
    [hObject, handles] = handles.gsimula_stats_lib_obj.load_all_gsimula_results_files(hObject, handles);


    if strcmp(handles.execution_files{1,1}.name(1:5), 'Grid-')
        handles.grid_scheduling_levels = 3;
        set (handles.checkbox_two_level_scheduling, 'Enable', 'on');
        set (handles.checkbox_three_level_scheduling, 'Enable', 'on');
        set (handles.checkbox_super_cluster_scheduling, 'Enable', 'on');
        set (handles.checkbox_two_level_scheduling, 'Value', get (handles.checkbox_two_level_scheduling, 'Min'));
        set (handles.checkbox_three_level_scheduling, 'Value', get (handles.checkbox_three_level_scheduling, 'Min'));
        set (handles.checkbox_super_cluster_scheduling, 'Value', get (handles.checkbox_super_cluster_scheduling, 'Min'));
        test_size = size(handles.execution_files);
        handles.number_of_super_clusters = test_size(1,2)/handles.total_number_of_files - 2;
        %fprintf(1, '%1.10f\n', handles.number_of_super_clusters);
    else
        handles.grid_scheduling_levels = 2;
        set (handles.checkbox_two_level_scheduling, 'Enable', 'off');
        set (handles.checkbox_three_level_scheduling, 'Enable', 'off');
        set (handles.checkbox_super_cluster_scheduling, 'Enable', 'off');
        set (handles.checkbox_two_level_scheduling, 'Value', get (handles.checkbox_two_level_scheduling, 'Min'));
        set (handles.checkbox_three_level_scheduling, 'Value', get (handles.checkbox_three_level_scheduling, 'Min'));
        set (handles.checkbox_super_cluster_scheduling, 'Value', get (handles.checkbox_super_cluster_scheduling, 'Min'));
        clear handles.number_of_super_clusters;
    end

    %  Add the list of experiments to listbox control
    for i = 1 : handles.total_number_of_experiments_directories
        directories_name_list{1, i} = handles.directories_list(i).name;
    end
    set (handles.listbox_experiments_list, 'String', cellstr(directories_name_list))
    
    %  Initialize variable
    criterion_list = cell(1, 1);

    %  Add the list to listbox control
    total_number_of_criterion = 0;
    if handles.grid_scheduling_levels == 3
        for i = 1 : handles.number_of_global_files
            test_string = handles.execution_file_headers{1,i}{3,1};
            total_number_of_criterion = total_number_of_criterion + 1;
            criterion_list{1, total_number_of_criterion} = test_string;
        end
        for i = ((handles.number_of_super_clusters + 2) * handles.number_of_global_files + 1) : ((handles.number_of_super_clusters + 2) * handles.number_of_global_files + handles.number_of_node_files)
            test_string = handles.execution_file_headers{1,i}{3,1};
            total_number_of_criterion = total_number_of_criterion + 1;
            criterion_list{1, total_number_of_criterion} = test_string;
        end
    else
        for i = 1 : handles.number_of_files_in_directory(1,1)
            test_string = handles.execution_file_headers{1,i}{3,1};
            total_number_of_criterion = total_number_of_criterion + 1;
            criterion_list{1, total_number_of_criterion} = test_string;
        end
    end
    set (handles.listbox_criterion_list, 'String', cellstr(criterion_list));

    %  Make listbox multiselect
    set(handles.listbox_criterion_list,'Max',2); 
    
    
    msgbox('All data has been loaded', 'Load data');

    set (handles.pushbutton_build_graphics, 'Enable', 'on');
    set (handles.checkbox_execution_data, 'Enable', 'on');
    set (handles.checkbox_allocation_data, 'Enable', 'on');

    set (handles.checkbox_g_per_experiment, 'Enable', 'on');
    set (handles.checkbox_g_mean_std, 'Enable', 'on');
    set (handles.checkbox_g_percent, 'Enable', 'on');
    
    %  Disable button to load data
    set (handles.pushbutton_load_experiments, 'Enable', 'off');

    %  Update handles structure
    guidata(hObject, handles);



% --- Executes during object creation, after setting all properties.
function figure1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to figure1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

    %  Load the date, time related functions from library
    handles.datetime_lib_obj = datetime_lib;

    %  Load the job related functions from library
    handles.jobs_lib_obj = jobs_lib;

    %  Load the gsimula stats related functions from library
    handles.gsimula_stats_lib_obj = gsimula_stats_lib;

    %  Load the string related functions from library
    handles.string_lib_obj = string_lib;

    %  Load the file related functions from library
    handles.file_lib_obj = file_lib;

    %  Update handles structure
    guidata(hObject, handles);
    




% --- Executes on button press in pushbutton_build_graphics.
function pushbutton_build_graphics_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton_build_graphics (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

%     for i = 1 : length(handles.directories_list)
%         fprintf(1, '%d.- %s\n', i, handles.directories_list(i).name);
%     end
% 
%     for i = 1 : 22
%         fprintf(1, '    %d. %s\n', i, handles.execution_files{1, i}.name);
%     end


    %  Get the selected experiments
    %experiments_list_entries = get(handles.listbox_experiments_list,'String');
    experiments_index_selected = get(handles.listbox_experiments_list,'Value');
    if length(experiments_index_selected) < 1
        errordlg('You must select at last one experiment', 'Incorrect Selection','modal');
    end 
    %for i = 1 : length(experiments_index_selected)
    %    fprintf(1, '%s\n', experiments_list_entries{experiments_index_selected(i)});
    %end

    %  Get the selected criterions
    %criterion_list_entries = get(handles.listbox_criterion_list,'String');
    criterion_index_selected = get(handles.listbox_criterion_list,'Value');
    if length(criterion_index_selected) < 1
        errordlg('You must select at last one criterion', 'Incorrect Selection','modal');
    end 
    %for i = 1 : length(criterion_index_selected)
    %    fprintf(1, '%s\n', criterion_list_entries{criterion_index_selected(i)});
    %end


    %  Initialize variable
    total_number_of_graphics = 0;
    
    %  If execution data is going to be graphicated
    if (get(handles.checkbox_execution_data,'Value') == get(handles.checkbox_execution_data,'Max'))
        if handles.grid_scheduling_levels == 3
            %  If two level data is going to be graphicated
            if (get(handles.checkbox_two_level_scheduling,'Value') == get(handles.checkbox_two_level_scheduling,'Max'))
                clear criterion_index_selected_2l;
                for j = 1 : length(criterion_index_selected)
                    if criterion_index_selected(j) > handles.number_of_global_files
                        criterion_index_selected_2l(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files);
                        %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_2l(j), handles.execution_files{1, criterion_index_selected_2l(j)}.name);
                    else
                        criterion_index_selected_2l(j) = criterion_index_selected(j);
                        %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_2l(j), handles.execution_files{1, criterion_index_selected_2l(j)}.name);
                    end
                end
                %criterion_index_selected_2l
                total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_2l, 0, '2LS: ');
            end
            %  If three level data is going to be graphicated
            if (get(handles.checkbox_three_level_scheduling,'Value') == get(handles.checkbox_three_level_scheduling,'Max'))
                clear criterion_index_selected_3l;
                for j = 1 : length(criterion_index_selected)
                    if criterion_index_selected(j) > handles.number_of_global_files
                        criterion_index_selected_3l(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files + handles.number_of_node_files);
                        %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_3l(j), handles.execution_files{1, criterion_index_selected_3l(j)}.name);
                    else
                        criterion_index_selected_3l(j) = criterion_index_selected(j) + handles.number_of_global_files;
                        %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_3l(j), handles.execution_files{1, criterion_index_selected_3l(j)}.name);
                    end
                end
                %criterion_index_selected_3l
                total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_3l, 0, '3LS: ');
            end
            %  If super-cluster data is going to be graphicated
            if (get(handles.checkbox_super_cluster_scheduling,'Value') == get(handles.checkbox_super_cluster_scheduling,'Max'))
                for i = 1 : handles.number_of_super_clusters
                    clear criterion_index_selected_sc;
                    for j = 1 : length(criterion_index_selected)
                        if criterion_index_selected(j) > handles.number_of_global_files
                            criterion_index_selected_sc(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files) + ((1 + i) * handles.number_of_node_files);
                            %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_sc(j), handles.execution_files{1, criterion_index_selected_sc(j)}.name);
                        else
                            criterion_index_selected_sc(j) = criterion_index_selected(j) + handles.number_of_global_files * (i + 1);
                            %fprintf(1, '%d %d %s\n', criterion_index_selected(j), criterion_index_selected_sc(j), handles.execution_files{1, criterion_index_selected_sc(j)}.name);
                        end
                    end
                    %criterion_index_selected_sc
                    total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_sc, 0, ['SC', num2str(i, '%1.0f'), 'S: ']);
                end
            end
        else
            total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected, 0, '');
        end
    end
    
    
    %  If allocation data is going to be graphicated
    if (get(handles.checkbox_allocation_data,'Value') == get(handles.checkbox_allocation_data,'Max'))
        if handles.grid_scheduling_levels == 3
            %  If two level data is going to be graphicated
            if (get(handles.checkbox_two_level_scheduling,'Value') == get(handles.checkbox_two_level_scheduling,'Max'))
                clear criterion_index_selected_2l;
                for j = 1 : length(criterion_index_selected)
                    if criterion_index_selected(j) > handles.number_of_global_files
                        criterion_index_selected_2l(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files);
                        %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_2l(j)}.name);
                    else
                        criterion_index_selected_2l(j) = criterion_index_selected(j);
                        %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_2l(j)}.name);
                    end
                end
                %criterion_index_selected_2l
                total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_2l, 1, '2LS: ');
            end
            %  If three level data is going to be graphicated
            if (get(handles.checkbox_three_level_scheduling,'Value') == get(handles.checkbox_three_level_scheduling,'Max'))
                clear criterion_index_selected_3l;
                for j = 1 : length(criterion_index_selected)
                    if criterion_index_selected(j) > handles.number_of_global_files
                        criterion_index_selected_3l(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files + handles.number_of_node_files);
                        %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_3l(j)}.name);
                    else
                        criterion_index_selected_3l(j) = criterion_index_selected(j) + handles.number_of_global_files;
                        %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_3l(j)}.name);
                    end
                end
                %criterion_index_selected_3l
                total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_3l, 1, '3LS: ');
            end
            %  If super-cluster data is going to be graphicated
            if (get(handles.checkbox_super_cluster_scheduling,'Value') == get(handles.checkbox_super_cluster_scheduling,'Max'))
                for i = 1 : handles.number_of_super_clusters
                    clear criterion_index_selected_sc;
                    for j = 1 : length(criterion_index_selected)
                        if criterion_index_selected(j) > handles.number_of_global_files
                            criterion_index_selected_sc(j) = criterion_index_selected(j) + ((handles.number_of_super_clusters + 1) * handles.number_of_global_files) + ((1 + i) * handles.number_of_node_files);
                            %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_sc(j)}.name);
                        else
                            criterion_index_selected_sc(j) = criterion_index_selected(j) + handles.number_of_global_files * (i + 1);
                            %fprintf(1, '%s\n', handles.execution_files{1, criterion_index_selected_sc(j)}.name);
                        end
                    end
                    %criterion_index_selected_sc
                    total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected_sc, 1, ['SC', num2str(i, '%1.0f'), 'S: ']);
                end
            end
        else
            total_number_of_graphics = handles.gsimula_stats_lib_obj.graphic_results(hObject, handles, total_number_of_graphics, experiments_index_selected, criterion_index_selected, 1, '');
        end
    end





% --- Executes on selection change in listbox_criterion_list.
function listbox_criterion_list_Callback(hObject, eventdata, handles)
% hObject    handle to listbox_criterion_list (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns listbox_criterion_list contents as cell array
%        contents{get(hObject,'Value')} returns selected item from listbox_criterion_list


% --- Executes during object creation, after setting all properties.
function listbox_criterion_list_CreateFcn(hObject, eventdata, handles)
% hObject    handle to listbox_criterion_list (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end




% --- Executes on button press in checkbox_allocation_data.
function checkbox_allocation_data_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_allocation_data (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_allocation_data


% --- Executes on button press in checkbox_per_node.
function checkbox_per_node_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_per_node (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_per_node


% --- Executes on button press in checkbox_execution_data.
function checkbox_execution_data_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_execution_data (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_execution_data


% --- Executes on button press in checkbox_per_system.
function checkbox_per_system_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_per_system (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_per_system




% --- Executes on button press in checkbox_two_level_scheduling.
function checkbox_two_level_scheduling_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_two_level_scheduling (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_two_level_scheduling


% --- Executes on button press in checkbox_three_level_scheduling.
function checkbox_three_level_scheduling_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_three_level_scheduling (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_three_level_scheduling


% --- Executes on button press in checkbox_super_cluster_scheduling.
function checkbox_super_cluster_scheduling_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_super_cluster_scheduling (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_super_cluster_scheduling





function edit_num_global_files_Callback(hObject, eventdata, handles)
% hObject    handle to edit_num_global_files (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_num_global_files as text
%        str2double(get(hObject,'String')) returns contents of edit_num_global_files as a double


% --- Executes during object creation, after setting all properties.
function edit_num_global_files_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_num_global_files (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit_num_node_files_Callback(hObject, eventdata, handles)
% hObject    handle to edit_num_node_files (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_num_node_files as text
%        str2double(get(hObject,'String')) returns contents of edit_num_node_files as a double


% --- Executes during object creation, after setting all properties.
function edit_num_node_files_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_num_node_files (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end




% --- Executes on button press in checkbox_g_per_experiment.
function checkbox_g_per_experiment_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_g_per_experiment (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_g_per_experiment


% --- Executes on button press in checkbox_g_mean_std.
function checkbox_g_mean_std_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_g_mean_std (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_g_mean_std


% --- Executes on button press in checkbox_g_percent.
function checkbox_g_percent_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_g_percent (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_g_percent




% --- Executes on button press in checkbox_prepare_graphic.
function checkbox_prepare_graphic_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_prepare_graphic (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_prepare_graphic
    if (get(hObject,'Value') == get(hObject,'Max'))
        % then checkbox is checked-take approriate action
        set (handles.edit_function_name, 'Enable', 'on');
        set (handles.edit_graphic_name, 'Enable', 'on');
        set (handles.edit_save_path, 'Enable', 'on');
        set(handles.edit_function_name,'BackgroundColor',[1, 1, 1]);
        set(handles.edit_graphic_name,'BackgroundColor',[1, 1, 1]);
        set(handles.edit_save_path,'BackgroundColor',[1, 1, 1]);
        %set (handles.edit_function_name, 'String', '');
        %set (handles.edit_graphic_name, 'String', '');
        %set (handles.edit_save_path, 'String', '');
    else
        % checkbox is not checked-take approriate action
        set (handles.edit_function_name, 'Enable', 'off');
        set (handles.edit_graphic_name, 'Enable', 'off');
        set (handles.edit_save_path, 'Enable', 'off');
        set(handles.edit_function_name,'BackgroundColor',[0.831, 0.816, 0.784]);
        set(handles.edit_graphic_name,'BackgroundColor',[0.831, 0.816, 0.784]);
        set(handles.edit_save_path,'BackgroundColor',[0.831, 0.816, 0.784]);
        %set (handles.edit_function_name, 'String', '');
        %set (handles.edit_graphic_name, 'String', '');
        %set (handles.edit_save_path, 'String', '');
    end



function edit_function_name_Callback(hObject, eventdata, handles)
% hObject    handle to edit_function_name (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_function_name as text
%        str2double(get(hObject,'String')) returns contents of edit_function_name as a double


% --- Executes during object creation, after setting all properties.
function edit_function_name_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_function_name (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit_graphic_name_Callback(hObject, eventdata, handles)
% hObject    handle to edit_graphic_name (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_graphic_name as text
%        str2double(get(hObject,'String')) returns contents of edit_graphic_name as a double


% --- Executes during object creation, after setting all properties.
function edit_graphic_name_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_graphic_name (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit_save_path_Callback(hObject, eventdata, handles)
% hObject    handle to edit_save_path (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit_save_path as text
%        str2double(get(hObject,'String')) returns contents of edit_save_path as a double


% --- Executes during object creation, after setting all properties.
function edit_save_path_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit_save_path (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end




% --- Executes on button press in checkbox_two_columns.
function checkbox_two_columns_Callback(hObject, eventdata, handles)
% hObject    handle to checkbox_two_columns (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of checkbox_two_columns


% --- Executes on button press in radiobutton_vertical.
function radiobutton_vertical_Callback(hObject, eventdata, handles)
% hObject    handle to radiobutton_vertical (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of radiobutton_vertical
if (get(hObject,'Value') == get(hObject,'Max'))
	set(handles.radiobutton_horizontal,  'Value', get(hObject,'Min'));
else
	set(handles.radiobutton_horizontal,  'Value', get(hObject,'Max'));
end

% --- Executes on button press in radiobutton_horizontal.
function radiobutton_horizontal_Callback(hObject, eventdata, handles)
% hObject    handle to radiobutton_horizontal (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of radiobutton_horizontal
if (get(hObject,'Value') == get(hObject,'Max'))
	set(handles.radiobutton_vertical,  'Value', get(hObject,'Min'));
else
	set(handles.radiobutton_vertical,  'Value', get(hObject,'Max'));
end


