%% This script creates a 3D plot of the earth as ellipsoid with an earth map. 

%% Create figure

if exist('WorldPlot', 'var')
    if ~isvalid(WorldFigure)
        WorldFigure = figure;
        globe_axes = axes();
    else
        clf(WorldFigure);
        globe_axes = axes();
    end
else
    WorldFigure = figure;
    globe_axes = axes();
end


hold on;
%% Setup ellipsoid for earth model:

num_panel = 180;                                                            % Number of earth panels °/panel = 360/num_panels

% Mean spherical earth

rad_eq    = 6378160;                                                        % equatorial radius (meters)
rad_po    = 6356775;                                                        % polar radius (meters)
%erot    = 7.2921158553e-5;                                                 % earth rotation rate (radians/sec)
%GMST0 = 4.89496121282306;                                                  % Set up a rotatable globe at J2000.0

% Set axis properties

set(globe_axes, 'NextPlot','add');%, 'Visible','off');
axis equal;
axis square;

% Set view
view([-30,30])

axis vis3d;
axis([-6500000 6500000 -6500000 6500000 -6500000 6500000]);

%% Create earth model ellipsoid

% Create a 3D meshgrid of the sphere points using the ellipsoid function

[x, y, z] = ellipsoid(0, 0, 0, rad_eq, rad_eq, rad_po, num_panel);

globe = surf(globe_axes, x, y, -z, 'FaceColor', 'none', 'EdgeColor', 0.5*[1 1 1]);
xlabel('X [m]')
ylabel('Y [m]')
zlabel('Z [m]')

% if ~isempty(GMST0)
%     hgx = hgtransform;
%     set(hgx,'Matrix', makehgtform('zrotate',GMST0));
%     set(globe,'Parent',hgx);
% end


%% Texturemap the earth model

% Load Earth image for texture map
[folder, ~,~] = fileparts(which('earthModel3D'));
image_file = fullfile(folder, 'Nasa_land_ocean_ice_8192.jpg');
cdata = imread(image_file);
gamma   = 0.6;                                                              % adjust color brigthness of world image
cdata_adjust = imadjust(cdata,[0 0 0; 1 1 1],[0 0 0; 1 1 1], gamma);

% Set image as color data (cdata) property, and set face color to indicate
% a texturemap, which Matlab expects to be in cdata. Turn off the mesh edges.

set(globe, 'FaceColor', 'texturemap', 'CData', cdata_adjust, 'FaceAlpha', 1, 'EdgeColor', 'none');

%% Add sun light:
light_handle = light('Position',[-1 0 1]);                                  % add a light handle for the sun_light

%%
set(globe_axes, 'NextPlot', 'add');
trajectory_plot = plot3(globe_axes, [],[],[]);
set(trajectory_plot,'Parent',globe_axes);

position_plot = plot3(globe_axes, [], [], []);
set(position_plot,'Parent',globe_axes);

trajectory = [];
% velocity_dev = [];
% time_all = [];
% orb_period = [];