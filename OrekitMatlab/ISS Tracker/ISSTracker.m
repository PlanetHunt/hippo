%% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%                            KKKK
%                            KKKKKKKKK
%                   KK      KKKKKKKKKKKKK
%                 KKKK      KKKKKKKKKKKKKKK
%                KKKKK      KKKKKKKKKKKKKKKKK
%               KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%              KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%             KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%            KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%            KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%           KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%           KKKKKKKK     KKKKKK        KKKKK
%          KKKKKKKK      KKKK          KKKf          K
%          KKKKKKKK      KKK           KK;          KK
%          KKKKKKKK     .KK           KKK           KKK
%          KKKKKKK      KKK      :KKKKKK       KKKKKKKKG
%          KKKKKKK      KKK      KKKKKKK      KKKKKKKKKK
%         ,KKKKKKK      KKK      KKKKKKK       KKKKKKKKK
%          KKKKKK      KKKK       KKKKKKK      iKKKKKKKKK
%          KKKKKK      KKKKK       KKKKKK       KKKKKKKKK
%          KKKKKK      KKKKKK       KKKKKK       KKKKKKKK
%          KKKKKt     KKKKKKKE      EKKKKKK       KKKKKKK
%          KKKKK      KKKKKKKK       KKKKKKK      KKKKKKK
%           KKKK      KKKKKKKKK      KKKKKKK      KKKKKKK
%           KKKK     KKKKKKKKK       KKKKKK       KKKKKKK
%           KKK      KKKK           KK:           KKKKKKK
%            KK      KKKK          KKK           KKKKKKKK
%             K     :KKK          KKKK          KKKKKKKKK
%                   KKKK         KKKKK        KKKKKKKKKKK
%              KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%               KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                 KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                  KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                   KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                    KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                      KKKKKKKKKKKKKKKKKKKKKKKKKKKK
%                        KKKKKKKKKKKKKKKKKKKKKKKKK
%                          KKKKKKKKKKKKKKKKKKKKK
%                            .KKKKKKKKKKKKKKK
%                                 ,KKKKKj
%
% ISSTracker main script
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: ISSTracker.m 40 2013-07-17 23:27:42Z Rami $
%
%% GUIDE methods

% ---
function varargout = ISSTracker(varargin)
% ISSTRACKER MATLAB code for ISSTracker.fig
%      ISSTRACKER, by itself, creates a new ISSTRACKER or raises the existing
%      singleton*.
%
%      H = ISSTRACKER returns the handle to a new ISSTRACKER or the handle to
%      the existing singleton*.
%
%      ISSTRACKER('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in ISSTRACKER.M with the given input arguments.
%
%      ISSTRACKER('Property','Value',...) creates a new ISSTRACKER or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before ISSTracker_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to ISSTracker_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help ISSTracker

% Last Modified by GUIDE v2.5 09-Dec-2011 19:28:16

% check that commons math3 and orekit are loaded
if checkLibrariesStatus == 0
    % if not interrupt
    loadLibraries;
    h = warndlg('Loaded required libraries.', 'Libraries not loaded');
    set(h, 'Tag', 'ISSTW');
    button = findall(0, 'Tag', 'OKButton');
    set(button, 'String', 'Run ISSTracker again...');
    pos = get(button, 'Position');
    set(button, 'Position', [pos(1)/1.35 pos(2) pos(3)*2.7 pos(4)]);
    set(button, 'Callback', @warndlgCallback);
    
else
    % otherwise continue
    
    % Begin initialization code - DO NOT EDIT
    gui_Singleton = 1;
    gui_State = struct('gui_Name',       mfilename, ...
        'gui_Singleton',  gui_Singleton, ...
        'gui_OpeningFcn', @ISSTracker_OpeningFcn, ...
        'gui_OutputFcn',  @ISSTracker_OutputFcn, ...
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
end
end

% --- callback for warning dialog
function warndlgCallback(varargin)
close(findall(0, 'Tag', 'ISSTW'));
ISSTracker;
end

% -- load missing libraries
function loadLibraries

loaded = javaclasspath('-static');                                         % loaded = javaclasspath('-dynamic');
cmFlag = 0;
orekitFlag = 0;
for k=1:length(loaded)
 if ~isempty(strfind(loaded{k}, 'math'))
     cmFlag = 1;
 end
 if ~isempty(strfind(loaded{k}, 'orekit'))
     orekitFlag = 1;
 end
end


if cmFlag == 0
    javaaddpath(fullfile(cd, 'lib', 'commons-math3-3.2.jar'));
end
if orekitFlag == 0
    javaaddpath(fullfile(cd, 'lib', 'orekit-6.0.jar'));
end

end

% ---
function ISSTracker_OpeningFcn(hObject, ~, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to ISSTracker (see VARARGIN)

% Choose default command line output for ISSTracker
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% Call main
main(hObject, handles)

% UIWAIT makes ISSTracker wait for user response (see UIRESUME)
% uiwait(handles.ISSTracker);
end

% ---
function varargout = ISSTracker_OutputFcn(~, ~, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;

end

%% Main

% --- Calls data & GUI initialization methods

function main(hObject, handles)
clc
fprintf('  Launching ISSTracker\n  ====================\n\n')

    status = get(hObject, 'UserData');
    if isempty(status)
        
        set(handles.info, 'UserData', 0);
        set(handles.startTrackerButton, 'UserData', 0);
        
        %% setup data root
        setupDataRoot();
        
        %% Retrieve and parse online data
        fprintf('   Retrieving ISS Orbital Data...\n');
        [p, v, iD, ~] = initializeFeedData();
        
        %% Propagate orbit bulletin
        propagator = getPropagator(p, v, iD);
        
        % and store generated ephemeris
        orekitData = get(hObject, 'UserData');
        ephemeris = propagate(propagator, orekitData(1).utc);
        orekitData.generatedEphemeris = ephemeris;
        set(hObject, 'UserData', orekitData);
        
        %% 3D view initialization
        
        % Date of now
        currentTime = getCurrentTime(orekitData(1).utc);
        
        fprintf('   Initializing 3D View...\n')
        % plot data
        [~, lon, lat, ~, ~, x, y, z, id] = ...
            getGroundTrace (currentTime, 0, 1.6, orekitData, orekitData(1).frames.EME2000);
        % earth rotation
        zcomp = orekitData.frames.ITRF2005.getTransformTo(orekitData.frames.EME2000, currentTime).getRotation.getAxis.getZ;
        era = orekitData.frames.ITRF2005.getTransformTo(orekitData.frames.EME2000, currentTime).getRotation.getAngle;
        
        % init method
        initializeThDV(x, y, z, id, lon, lat, era, zcomp);
        
        %% 2D view initialization
        fprintf('   Initializing 2D View...\n')
        % plot data
        [~, lon, lat, az, el, ~, ~, ~, id] = ...
            getGroundTrace (currentTime, simulation_parameters.gtStart, simulation_parameters.gtEnd, orekitData, orekitData(1).frames.ITRF2005);
        % init method
        initializeToDV(lon, lat, id);
        
        %% Station viewpoint initialization
        % uses same time span as earlier
        fprintf('   Initializing User Frame View...\n\n')
        initializeTP(az, el, id);
        
        fprintf('   Done! Press "Start" to start tracking...\n')
    end
    


end

function status = checkLibrariesStatus() 

loaded = javaclasspath('-static');                                         % loaded = javaclasspath('-dynamic');
cmFlag = 0;
orekitFlag = 0;

for k=1:length(loaded)
    if ~isempty(strfind(loaded{k}, 'math'))
        cmFlag = 1;
    end
    if ~isempty(strfind(loaded{k}, 'orekit'))
        orekitFlag = 1;
    end
end
status = cmFlag && orekitFlag;

end

% --- Executes on button press in info.
function info_Callback(~, ~, handles)
% hObject    handle to info (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

set(handles.info, 'UserData', 1);
handleInfoEvent();

end

% --- Handles launching the info gui
function handleInfoEvent()


hObject = findall(0, 'Tag', 'ISSTracker');
handles = guidata(hObject);
wasOn = get(handles.startTrackerButton, 'UserData');
if ~wasOn
    % Otherwise the thread calls this method
    displayInfoScreen();
end


end

% --- Loads information screen
function displayInfoScreen()

hObject = findall(0, 'Tag', 'ISSTracker');
handles = guidata(hObject);
infoFig = findall(0, 'Name', 'ISSTrackerInfo');
orekitData = get(hObject, 'UserData');

% uiwait(handles.ISSTracker)

if isempty(infoFig)
    % size
    bgColor = get(hObject, 'Color');
    fgColor = get(handles.uipanel1, 'ForegroundColor');
    s = get(0, 'ScreenSize');
    w = 670;
    h = 300;
    ox = (s(3)-w)/2;
    oy = 2*(s(4)-h)/3;
    
    % new figure
    infoFig = figure('Position',[ox oy w h], 'Color',...
        bgColor, 'Name', 'ISSTrackerInfo',...
        'NumberTitle', 'off', 'Toolbar', 'none', 'MenuBar', 'none', ...
        'Units', 'normalized', 'Visible', 'off');
    
    % controls
    uicontrol('Style', 'Text', 'String', 'Information & Acknowledgments',...
        'units', 'normalized', 'position', [.2 .80 .6 .1], 'FontWeight', ...
        'bold', 'fontsize', 12, 'Backgroundcolor', bgColor, 'ForegroundColor', ...
        fgColor);
    
    infoStr = ['The ISS Real Time Tracker is a Matlab interface developed ', ...
        'with GUIDE. It allows real time station tracking by retrieving the ', ...
        'ISS orbital ephemeris from the NASA online JPL database and propagating', ...
        ' it using the ORbit Extrapolation KIT', ...
        ' (OREKIT) which is a free and open source low-level space dynamics', ...
        ' library. Many thanks go out to all the teams that have dedicated', ...
        ' much time and effort in order to provide these public data sets and', ...
        ' libraries, making smaller projects like these possible.'];
    
    uicontrol('Style', 'Text', 'String', infoStr,...
        'units', 'normalized', 'position', [.05 .40 .9 .2], ...
        'Backgroundcolor', bgColor, 'ForegroundColor', ...
        fgColor, 'HorizontalAlignment', 'left');
    
    figure(infoFig);
else
    figure(infoFig);
end
set(handles.info, 'UserData', 0);

end

%% GUI initialization and ISS data recovery methods

% --- data
function setupDataRoot

import java.lang.System;
import java.io.File;
import org.orekit.data.*;

% data pointer
DM=DataProvidersManager.getInstance();
crawler=DirectoryCrawler(File(fullfile(cd, 'data')));
DM.clearProviders();
DM.addProvider(crawler);
end

% --- Propagate orbit bulletin
function ephemeris = propagate(propagator, utc)

fprintf('   Calculating ephemeris...\n')
currentTime = getCurrentTime(utc);
propagator.propagate(currentTime.shiftedBy(simulation_parameters.ephStart * 3600));
propagator.propagate(currentTime.shiftedBy(simulation_parameters.ephEnd * 3600));

ephemeris = propagator.getGeneratedEphemeris();

end

% --- Initialization of ISS Feed Data / Executes on load
function [p, v, iD, temp] = initializeFeedData()

import java.lang.Integer;
import java.lang.Math;
import java.lang.System;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.orekit.time.*;
import org.orekit.data.*;

utc = TimeScalesFactory.getUTC;


% Calendar for conversion day in year to day of month
cal = Calendar.getInstance();
cal.setTime(Date());

% The actual Nasa feed
url = 'http://spaceflight.nasa.gov/realdata/sightings/SSapplications/Post/JavaSSOP/orbit/ISS/SVPOST.html';
try
    nasaData = urlread(url);
catch e
    error('Couldn''t retrieve orbital data. Please check your internet connection.');
end

% Split the nasaData char starting from the startStr
startStr = '    Coasting Arc';
startIdx = strfind(nasaData, startStr);

% For each part, retrieve the info we want
for k=2:length(startIdx)
    % parse into lines
    orbitalData(k-1).text.raw = nasaData(startIdx(k-1):startIdx(k)-1);
    orbitalData(k-1).text.lines = regexp(orbitalData(k-1).text.raw, '\n', 'split');
    
    % get time info
    orbitalData(k-1).time.text = orbitalData(k-1).text.lines{4};
    dateData = char(regexpi(orbitalData(k-1).time.text,...
        '\d+/\d+/\d\d:\d\d:\d\d.\d\d\d','match'));
    orbitalData(k-1).time.year = Integer.parseInt(dateData(1:4));
    orbitalData(k-1).time.dayInYear = Integer.parseInt(dateData([6 7 8]));
    cal.set(Calendar.DAY_OF_YEAR, orbitalData(k-1).time.dayInYear);
    orbitalData(k-1).time.month = cal.get(Calendar.MONTH)+1;
    orbitalData(k-1).time.day = cal.get(Calendar.DAY_OF_MONTH);
    orbitalData(k-1).time.hour = Integer.parseInt(dateData([10 11]));
    orbitalData(k-1).time.minute = Integer.parseInt(dateData([13 14])); %#ok<*AGROW>
    orbitalData(k-1).time.second = Integer.parseInt(dateData([16 17]));
    orbitalData(k-1).time.msecond = str2double(dateData([19 20 21]));
    
    % get cartesian info
    labels = {'X', 'Y', 'Z', 'XDot', 'YDot', 'ZDot'};
    for j = 22:27
        result = regexp(orbitalData(k-1).text.lines{j},'(-?[0-9]{0,}.\d+)*','match');
        orbitalData(k-1).data.(labels{j-21}) = str2double(result{2});
    end
    
    orbitalData(k-1).data.weight = str2double( regexpi(orbitalData(k-1).text.lines{6}, '\d+.\d+', 'match'));
end

cal.setTime(Date());

% Look for todays ephemeris
daysDetected = arrayfun(@(x)orbitalData(x).time.day, 1:length(orbitalData));

% Todays ephemeris
temp = orbitalData(daysDetected == int32(cal.get(Calendar.DAY_OF_MONTH)));
temp = temp(end);

% date of data feed
% temp.time.year
% temp.time.month
% temp.time.day
% methodsview('DateComponents')
% keyboard

% % dateComponents = DateComponents(temp.time.year, temp.time.month, ...
% %     temp.time.day);
% % timeComponents = TimeComponents(temp.time.hour, temp.time.minute,...
% %     temp.time.second + temp.time.msecond/1000);
% % iD = AbsoluteDate(dateComponents, timeComponents, utc);

iD = AbsoluteDate(temp.time.year, temp.time.month, ...
    temp.time.day, temp.time.hour, temp.time.minute,...
    temp.time.second + temp.time.msecond/1000, utc);

% position and velocity of ISS
p = Vector3D(temp.data.X, temp.data.Y, temp.data.Z);
v = Vector3D(temp.data.XDot, temp.data.YDot, temp.data.ZDot);

% Look for maneuvers
flagStr = '   IMPULSIVE TIG';
flag = strfind(nasaData, flagStr);

maneuver = nasaData(flag(1):startIdx(1)-10);

maneuverData.text.raw = maneuver;
maneuver = regexp(maneuver, '\n', 'split');
maneuverData.text.lines = maneuver;
dateData = char(regexpi(maneuver{5},...
    '\d+/\d\d:\d\d:\d\d.\d\d\d','match'));
% if length(maneuver) > 6
%     maneuverData.start = AbsoluteDate(DateComponents(cal.get(Calendar.YEAR),...
%         str2double(dateData(1:3))), TimeComponents(str2double(dateData(5:6)), ...
%         str2double(dateData(8:9)), str2double(dateData(11:12)) + ...
%         str2double(dateData(14:16))/1000), utc);
%     dateData = char(regexpi(maneuver{7},...
%         '\d\d:\d\d:\d\d.\d\d\d           \d.\d','match'));
%     maneuverData.duration = str2double(dateData(1:2)) * 3600 + ...
%         str2double(dateData(4:5)) * 60 + str2double(dateData(7:8)) + ...
%         str2double(dateData(10:12)) / 1000;
%     maneuverData.containsManeuver = true;
% else
maneuverData.containsManeuver = false;
% end

end

% --- Initialization of 3D View / Executes on load
function initializeThDV(x, y, z, id, lon, lat, ERA, zcomp)
% Draw Earth and ISS in ITRF Frame

hObject = findall(0,'Tag','ISSTracker');
handles = guidata(hObject);

axes(handles.ThDV);
cla

hold on

[handles.ellipsoid.X,handles.ellipsoid.Y,handles.ellipsoid.Z] =...
    ellipsoid(0,0,0,1,1,(1 - physical_parameters.f),30);

if simulation_parameters.renderEarth
    
    handles.im = imread('data\earthMapBMsmall.jpg');
    for k=1:3
        handles.im(:,:,k) = flipud(handles.im(:,:,k));
    end
    
    % Get image width in pixels
    width = size(handles.im, 2);
    
    % determine the angle per pixel
    anglePerPx = 2 * pi / width;
    
    % determine how many pixels need to be moved
    pxs = floor(ERA / anglePerPx);
    
    % Earth rotation!!
    for k=1:3
        current = handles.im(:,:,k);
        if zcomp > 0
            newCdata(:,:,k) = [current(:, width - pxs:width), current(:, 1:width - pxs - 1)];
        else
            newCdata(:,:,k) = [current(:, pxs + 1:width), current(:, 1:pxs)];
            %         newCdata(:,:,k) = [current(:, width - pxs:width), current(:, 1:width - pxs - 1)];
        end
    end
    
    handles.myEarth = surf(handles.ellipsoid.X, handles.ellipsoid.Y,...
        handles.ellipsoid.Z, 'Facecolor','texturemap',...
        'CData', newCdata, 'Edgecolor','none',...
        'FaceLighting','phong','LineSmoothing','on');
    
    
else
    p1 = mesh(handles.ellipsoid.X, handles.ellipsoid.Y,...
        handles.ellipsoid.Z,'LineSmoothing','on');
    set(p1,'tag','earth','facecolor',[0 0 1],'edgecolor',[.3 .3 1]);
    
    data = load('data\coastline');
    coast = data.coastline;
    handles.coastLineTheta = coast(:,1)*pi/180;
    handles.coastLinePhi = coast(:,2)*pi/180;
    
                theta = handles.coastLineTheta + zcomp * ERA;
            phi = handles.coastLinePhi;
            coastLine = [cos(theta).*cos(phi),...
                sin(theta).*cos(phi),...
                -sin(phi)];
            handles.myEarth = plot3(coastLine(:,1),coastLine(:,2),-coastLine(:,3));
            set(handles.myEarth,'color',[0 .9 0]);
            

end

axis equal
axis off

% Plot EME2000 frame axes
h1 = plot3([0 1.5],[0 0],[0 0],'r-','LineSmoothing','on');
h2 = plot3([0 0],[0 1.5],[0 0],'g-','LineSmoothing','on');
h3 = plot3([0 0],[0 0],[0 1.5],'b-','LineSmoothing','on');
set([h1 h2 h3],'linewidth',4);

s = .05;
plotX = [0 1 1 0 0 0;1 1 0 0 1 1;1 1 0 0 1 1;0 1 1 0 0 0]*s -.025 + x(id);
plotY = [0 0 1 1 0 0;0 1 1 0 0 0;0 1 1 0 1 1;0 0 1 1 1 1]*s -.025 + y(id);
plotZ = [0 0 0 0 0 1;0 0 0 0 0 1;1 1 1 1 0 1;1 1 1 1 0 1]*s -.025 + z(id);

set(handles.ThDV, 'View', [lon(id) + 90 + sign(zcomp) * ERA * 180 / pi, lat(id) * simulation_parameters.latCoef]);

for i=1:6
    handles.cube(i) = patch(plotX(:,i),plotY(:,i),plotZ(:,i),'w');
    set(handles.cube(i),'edgecolor','k')
end

handles.orbit = plot3(x, y, z, 'y', 'LineWidth',1,'LineSmoothing','on');

guidata(hObject, handles);

end

% --- Initialization of 2D View / Executes on load
function initializeToDV(lons, lats, id)
% Draw Earth and ISS on map


hObject = findall(0, 'Tag', 'ISSTracker');
handles = guidata(hObject);

axes(handles.ToDV); %#ok<*MAXES>

im = imread('data\earthMapBM.jpg');
image(0:360,-90:90,im);

axis equal
axis off

hold on

lons = lons + 180;
lats = -lats;

myDif = abs(lons(2:end) - lons(1:end-1));
idx = [0,find(myDif > 250),length(lons)];

for k=2:length(idx)
    plot(lons(idx(k-1)+1:idx(k)), lats(idx(k-1)+1:idx(k)), 'y','LineSmoothing','on');
end

handles.myISS2D = plot(lons(id), lats(id), 'r+','LineWidth', 2,'LineSmoothing','on');

guidata(hObject, handles);

end

% --- Initialization of telescope pointing axes
function initializeTP(azs, els, id)

hObject = findall(0,'Tag','ISSTracker');
handles = guidata(hObject);

axes(handles.TP);
set(handles.TP, 'XTickMode', 'manual');
set(handles.TP, 'XTick', -180:30:180);
set(handles.TP, 'YTickMode', 'manual');
set(handles.TP, 'YTick', [-90 -60 -30 0 30 60 90]);
hold on

gray = [1 1 1] * .3;
% darkGray = [1 1 1] * .15;
xlabel('Azimuth (in °)');
ylabel('Elevation (in °)');
plot([-180 180], [0 0], 'Color', gray, 'Linewidth', 1)
% plot([0 360], [60 60], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
% plot([0 360], [-60 -60], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
% plot([0 360], [30 30], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
% plot([0 360], [-30 -30], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
% plot([180 180], [-90 90], 'Color', gray, 'Linewidth', 1, 'LineStyle', '--')
% plot([90 90], [-90 90], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
% plot([270 270], [-90 90], 'Color', darkGray, 'Linewidth', 1, 'LineStyle', '--')
grid(gca, 'minor')

set(handles.TP, 'YColor', [.2 .2 .2]);
set(handles.TP, 'XColor', [.2 .2 .2]);

myDif = abs(azs(2:end) - azs(1:end-1));
idx = [0,find(myDif > 100),length(azs)];

for k=2:length(idx)
    plot(azs(idx(k-1)+1:idx(k)), els(idx(k-1)+1:idx(k)), 'r','LineSmoothing','on');
end

handles.ISSSta = plot(azs(id), els(id), 'y+', 'LineWidth', 2,'LineSmoothing','on');
set(handles.TP, 'Color', 'k');

axis manual
axis([-180 180 -90 90]);

guidata(hObject, handles);

end

% --- Computes ground trace
function [t, lons, lats, az, el, x, y, z, id] = getGroundTrace (time, start, endH, orekitData, frame)

import java.lang.Math;
import java.lang.System;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.orekit.time.*;

% Ground Trace
p = simulation_parameters.points;
t = unique(sort([linspace(60 * 60 * start, 60 * 60 * endH, p), 0]));
groundDate = arrayfun(@(x)time.shiftedBy(x), t, 'Un', 0);
ISSData = cellfun(@(x)getISSData(x, orekitData, frame), groundDate);
lons = [ISSData.lon];
lats = [ISSData.lat];
az = [ISSData.azimuth];
el = [ISSData.elevation];
x = [ISSData.pX];
y = [ISSData.pY];
z = [ISSData.pZ];

id = t == 0;

end

%% Simulation methods

% --- Current time method. Returns current time as a AbsoluteDate object
function currentTime = getCurrentTime(utc)

% - Java imports
import java.lang.Math;
import java.lang.System;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.orekit.time.*;

% - Date of now
date = Date();
calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone('UTC')); % creates a new calendar instance
calendar.setTime(date);   % assigns calendar to given date
year = calendar.get(Calendar.YEAR);
month = calendar.get(Calendar.MONTH)+1;
day = calendar.get(Calendar.DAY_OF_MONTH);
hour = calendar.get(Calendar.HOUR_OF_DAY); % gets hour in 24h format
minute = calendar.get(Calendar.MINUTE);
second = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND)/1000 ;

currentTime =  AbsoluteDate(DateComponents(year, month, day),...
    TimeComponents(hour, minute, second), utc);
% old = AbsoluteDate(DateComponents(2011, 12, 8),...
%     TimeComponents(23, 50, 0), utc);
% currentTime = currentTime.shiftedBy(currentTime.offsetFrom(old, utc));

end

% --- Main thread
function startTracking(hObject, handles)

orekitData = get(hObject, 'UserData');

runStatus = 1;
rtn = 1;
infoRq = 0;

while runStatus && rtn && ~infoRq
    
    % Main "thread" that is running as long as the startTrackerButton
    % is on ('UserData' is 1)
    handles = guidata(hObject);
    runStatus = get(handles.startTrackerButton, 'UserData');
    infoRq = get(handles.info, 'UserData');
    
    % The following two lines allow the user to interact with the GUI
    % during runtime
    pause(.01)
    drawnow;
    
    if runStatus
        
        % - Get updated ISS data
        currentTime = getCurrentTime(orekitData.utc);
        rtn = refreshISS(orekitData, currentTime);
        
    end
    
    % If iformation screen requested by user, stop running and call
    % appropriate method. Thread is interrupted by ~infoRq = 0
    if infoRq
        set(handles.startTrackerButton, 'UserData', 0);
        set(handles.startTrackerButton, 'String', 'Start');
        set(handles.info, 'UserData', 0);
        
        displayInfoScreen();
    end
    
end


end

% --- Computation of new ISS data
function rtn = refreshISS(orekitData, time, whichFrame)
persistent oldPxs;
oldPxs = 0;

hObject = findall(0,'Tag','ISSTracker');
if ishghandle(hObject)
    
    
    
    % get GUI data
    handles = guidata(hObject);
    
    % earth rotation, angle
    zcomp = orekitData.frames.ITRF2005.getTransformTo(orekitData.frames.EME2000, time).getRotation.getAxis.getZ;
    ERA = orekitData.frames.ITRF2005.getTransformTo(orekitData.frames.EME2000, time).getRotation.getAngle;
    
    % - Get orbital data for now
    ISSData = getISSData(time, orekitData, orekitData(1).frames.EME2000);
    
    % - Update position in 3D graph
    pos = [ISSData.pX; ISSData.pY; ISSData.pZ];
    s = .05;
    plotX = [0 1 1 0 0 0;1 1 0 0 1 1;1 1 0 0 1 1;0 1 1 0 0 0]*s -.025 + pos(1);
    plotY = [0 0 1 1 0 0;0 1 1 0 0 0;0 1 1 0 1 1;0 0 1 1 1 1]*s -.025 + pos(2);
    plotZ = [0 0 0 0 0 1;0 0 0 0 0 1;1 1 1 1 0 1;1 1 1 1 0 1]*s -.025 + pos(3);
    
    if get(handles.boundRotation, 'Value')
        set(handles.ThDV, 'View', [ISSData.lon+90 + sign(zcomp) * ERA * 180 / pi, ISSData.lat * simulation_parameters.latCoef]);
    end
    
    for i=1:6
        set(handles.cube(i),'XData',plotX(:,i));
        set(handles.cube(i),'YData',plotY(:,i));
        set(handles.cube(i),'ZData',plotZ(:,i));
    end
    
    
    if simulation_parameters.renderEarth
        
        % Get image width in pixels
        width = size(handles.im, 2);
        
        % determine the angle per pixel
        anglePerPx = 2 * pi / width;
        
        % determine how many pixels need to be moved
        pxs = floor(ERA / anglePerPx);
        
        % - Earth rotation!!
        if (pxs ~= oldPxs);
            
            for k=1:3
                current = handles.im(:,:,k);
                if zcomp > 0
                    newCdata(:,:,k) = [current(:, width - pxs:width), current(:, 1:width - pxs - 1)];
                else
                    newCdata(:,:,k) = [current(:, pxs + 1:width), current(:, 1:pxs)];
                end
            end
            set(handles.myEarth, 'CData', newCdata);
            
            oldPxs = pxs;
        end
        
        
    else
        theta = handles.coastLineTheta + zcomp * ERA;
        phi = handles.coastLinePhi;
        coastLine = [cos(theta).*cos(phi),...
            sin(theta).*cos(phi),...
            -sin(phi)];
        
        set(handles.myEarth,'XData',coastLine(:,1));
        set(handles.myEarth,'YData',coastLine(:,2));
        set(handles.myEarth,'ZData',-coastLine(:,3));
        
    end
    
    ISSData = getISSData(time, orekitData, orekitData(1).frames.ITRF2005);
    
    % - Update 2D graph
    set(handles.myISS2D, 'XData', ISSData.lon+180);
    set(handles.myISS2D, 'YData', -ISSData.lat);
    
    % - Update Text Fields
    set(handles.timeInfo, 'String', ISSData.datestr);
    set(handles.a, 'String', sprintf('%.4f', ISSData.a));
    set(handles.e, 'String', sprintf('%.7f', ISSData.e));
    set(handles.i, 'String', sprintf('%.4f', ISSData.i));
    set(handles.pa, 'String', sprintf('%.4f', ISSData.pa));
    set(handles.raan, 'String', sprintf('%.4f', ISSData.raan));
    set(handles.ma, 'String', sprintf('%.4f', mod(ISSData.ma,360)));
    set(handles.lat, 'String', sprintf('%.4f', ISSData.lat));
    set(handles.lon, 'String', sprintf('%.4f', ISSData.lon));
    set(handles.alt, 'String', sprintf('%.4f', ISSData.alt));
    set(handles.az, 'String', sprintf('%.4f', ISSData.azimuth));
    set(handles.el, 'String', sprintf('%.4f', ISSData.elevation));
    set(handles.ra, 'String', sprintf('%.4f', ISSData.range));
    set(handles.vel, 'String', sprintf('%.6f', ISSData.v.getNorm()));
    
    axes(handles.TP)
    axis([-180 180 -90 90])
    
    set(handles.ISSSta, 'XData', ISSData.azimuth);
    set(handles.ISSSta, 'YData', ISSData.elevation);
    
    %     sprintf('az : %.2f, el : %.2f, ra : %.2f', ISSData.azimuth, ISSData.elevation, ISSData.range)
    rtn = 1;
else
    rtn = 0;
    infoFig = findall(0, 'Name', 'ISSTrackerInfo');
    if ~isempty(infoFig)
        figure(infoFig)
    end
end

end

% --- Computation of new ISS data for given date
function ISSData = getISSData(currentDate, orekitData, frame)
% currentDate
% Imports
import java.lang.Math;
import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.util.*;
import org.orekit.bodies.*;
import org.orekit.data.*;
import org.orekit.errors.*;
import org.orekit.frames.*;
import org.orekit.orbits.*;
import org.orekit.propagation.analytical.*;
import org.orekit.time.*;
import org.orekit.tle.*;
import org.orekit.utils.*;

% Position and Velocity in specified frame
result = orekitData.generatedEphemeris.propagate(currentDate);
finalOrbit = KeplerianOrbit(result.getOrbit());
ISSPV = result.getPVCoordinates(frame);
ISSPosition = ISSPV.getPosition();
ISSVelocity = ISSPV.getVelocity();
ISSLatLon = orekitData.earthBody.transform(ISSPosition,...
    frame, currentDate);


% Get and Store data in ISSData structure
ISSData.datestr = char(currentDate.toString());
ISSData.lat = Math.toDegrees(ISSLatLon.getLatitude());
ISSData.lon = Math.toDegrees(ISSLatLon.getLongitude());
ISSData.alt = ISSLatLon.getAltitude();
ISSData.p = ISSPosition;
ISSData.pX = ISSPosition.getX() / physical_parameters.ae;
ISSData.pY = ISSPosition.getY() / physical_parameters.ae;
ISSData.pZ = ISSPosition.getZ() / physical_parameters.ae;
ISSData.v = ISSVelocity;
ISSData.vX = ISSVelocity.getX();
ISSData.vY = ISSVelocity.getY();
ISSData.vZ = ISSVelocity.getZ();
ISSData.a = finalOrbit.getA();
ISSData.e = finalOrbit.getE();
ISSData.i = Math.toDegrees(finalOrbit.getI());
ISSData.pa = Math.toDegrees(finalOrbit.getPerigeeArgument());
ISSData.ma = Math.toDegrees(finalOrbit.getMeanAnomaly());
ISSData.raan = Math.toDegrees(finalOrbit.getRightAscensionOfAscendingNode());
frames = orekitData.frames(1);
station = frames(1).stationFrame;

ISSData.azimuth = Math.toDegrees(station.getAzimuth ...
    (ISSPosition, frame, currentDate));

idxs = ISSData.azimuth >= 180;
ISSData.azimuth(idxs) = ISSData.azimuth(idxs) - 360;

ISSData.elevation = Math.toDegrees(station.getElevation ...
    (ISSPosition, frame, currentDate));
ISSData.range = station.getRange(ISSPosition,...
    frame, currentDate);

end

%% Used methods created by GUIDE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% --- Executes when startTrackerButton is pressed
function startTrackerButton_Callback(~, ~, ~)
% hObject    handle to startTrackerButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

hObject = findall(0,'Tag','ISSTracker');
handles = guidata(hObject);

value = get(handles.startTrackerButton,'UserData');
if ~value
    set(handles.startTrackerButton, 'String', 'Stop');
    set(handles.startTrackerButton, 'UserData', 1); % running
    drawnow;
    startTracking(hObject, handles);
else
    set(handles.startTrackerButton, 'String', 'Start');
    set(handles.startTrackerButton, 'UserData', 0); % not running
end

end

% --- Executes when user attempts to close ISSTracker.
function ISSTracker_CloseRequestFcn(hObject, ~, handles)
% hObject    handle to ISSTracker (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
set(handles.startTrackerButton, 'UserData', 0);
drawnow;
pause(.05);

infoFig = findall(0, 'Name', 'ISSTrackerInfo');
if ~isempty(infoFig)
    delete(infoFig)
end
pause(.2);

delete(hObject);

end


%% Unused methods created by GUIDE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function timeInfo_Callback(hObject, eventdata, handles)
% hObject    handle to timeInfo (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of timeInfo as text
%        str2double(get(hObject,'String')) returns contents of timeInfo as a double

end
function timeInfo_CreateFcn(hObject, eventdata, handles)
% hObject    handle to timeInfo (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

end
function a_Callback(hObject, eventdata, handles)
% hObject    handle to a (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of a as text
%        str2double(get(hObject,'String')) returns contents of a as a double
end
function a_CreateFcn(hObject, eventdata, handles)
% hObject    handle to a (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function e_Callback(hObject, eventdata, handles)
% hObject    handle to e (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of e as text
%        str2double(get(hObject,'String')) returns contents of e as a double
end
function e_CreateFcn(hObject, eventdata, handles)
% hObject    handle to e (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function i_Callback(hObject, eventdata, handles)
% hObject    handle to i (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of i as text
%        str2double(get(hObject,'String')) returns contents of i as a double
end
function i_CreateFcn(hObject, eventdata, handles)
% hObject    handle to i (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end

end
function pa_Callback(hObject, eventdata, handles)
% hObject    handle to pa (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of pa as text
%        str2double(get(hObject,'String')) returns contents of pa as a double
end
function pa_CreateFcn(hObject, eventdata, handles)
% hObject    handle to pa (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function raan_Callback(hObject, eventdata, handles)
% hObject    handle to raan (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of raan as text
%        str2double(get(hObject,'String')) returns contents of raan as a double
end
function raan_CreateFcn(hObject, eventdata, handles) %#ok<*INUSD,*DEFNU>
% hObject    handle to raan (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function ma_Callback(hObject, eventdata, handles)
% hObject    handle to ma (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ma as text
%        str2double(get(hObject,'String')) returns contents of ma as a double
end
function ma_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ma (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function lat_Callback(hObject, eventdata, handles)
% hObject    handle to lat (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of lat as text
%        str2double(get(hObject,'String')) returns contents of lat as a double
end
function lat_CreateFcn(hObject, eventdata, handles)
% hObject    handle to lat (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function lon_Callback(hObject, eventdata, handles)
% hObject    handle to lon (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of lon as text
%        str2double(get(hObject,'String')) returns contents of lon as a double
end
function lon_CreateFcn(hObject, eventdata, handles)
% hObject    handle to lon (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function alt_Callback(hObject, eventdata, handles)
% hObject    handle to alt (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of alt as text
%        str2double(get(hObject,'String')) returns contents of alt as a double
end
function alt_CreateFcn(hObject, eventdata, handles)
% hObject    handle to alt (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function el_Callback(hObject, eventdata, handles)
% hObject    handle to el (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of el as text
%        str2double(get(hObject,'String')) returns contents of el as a double
end
function el_CreateFcn(hObject, eventdata, handles)
% hObject    handle to el (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function az_Callback(hObject, eventdata, handles)
% hObject    handle to az (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of az as text
%        str2double(get(hObject,'String')) returns contents of az as a double
end
function az_CreateFcn(hObject, eventdata, handles)
% hObject    handle to az (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function ra_Callback(hObject, eventdata, handles)
% hObject    handle to ra (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ra as text
%        str2double(get(hObject,'String')) returns contents of ra as a double
end
function ra_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ra (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function vel_Callback(hObject, eventdata, handles)
% hObject    handle to vel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of vel as text
%        str2double(get(hObject,'String')) returns contents of vel as a double
end
function vel_CreateFcn(hObject, eventdata, handles)
% hObject    handle to vel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
end
function boundRotation_Callback(hObject, eventdata, handles)
% hObject    handle to boundRotation (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of boundRotation
end
