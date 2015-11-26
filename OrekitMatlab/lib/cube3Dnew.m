trajectory = [];
npanels = 180;   % Number of globe panels around the equator deg/panel = 360/npanels
alpha   = 1; % globe transparency level, 1 = opaque, through 0 = invisible
%GMST0 = []; % Don't set up rotatable globe (ECEF)
%GMST0 = 4.89496121282306; % Set up a rotatable globe at J2000.0

% Earth texture image
% Anything imread() will handle, but needs to be a 2:1 unprojected globe
% image.

image_file = 'http://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/Land_ocean_ice_2048.jpg/1024px-Land_ocean_ice_2048.jpg';

% Mean spherical earth

erad    = 6378160; % equatorial radius (meters)6371008.7714;
prad    = 6356775; % polar radius (meters)6371008.7714;
%erot    = 7.2921158553e-5; % earth rotation rate (radians/sec)

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

% Turn off the normal axes

set(globe_axes, 'NextPlot','add');%, 'Visible','off');

% axis equal;
% axis auto;
axis square;

% Set initial view
view([-30,30])
% view(0,30);

axis vis3d;

%% Create wireframe globe

% Create a 3D meshgrid of the sphere points using the ellipsoid function

[x, y, z] = ellipsoid(0, 0, 0, erad, erad, prad, npanels);

globe = surf(globe_axes, x, y, -z, 'FaceColor', 'none', 'EdgeColor', 0.5*[1 1 1]);
xlabel('X [m]')
ylabel('Y [m]')
zlabel('Z [m]')

% if ~isempty(GMST0)
%     hgx = hgtransform;
%     set(hgx,'Matrix', makehgtform('zrotate',GMST0));
%     set(globe,'Parent',hgx);
% end

%% Add sun light:
light_handle = light('Position',[-1 0 1]);     % add a light

%% Texturemap the globe

% Load Earth image for texture map

cdata = imread(image_file);
cdata_adjust = imadjust(cdata,[0 0 0; 1 1 1],[0 0 0; 1 1 1],0.6);

% Set image as color data (cdata) property, and set face color to indicate
% a texturemap, which Matlab expects to be in cdata. Turn off the mesh edges.

set(globe, 'FaceColor', 'texturemap', 'CData', cdata_adjust, 'FaceAlpha', alpha, 'EdgeColor', 'none');

%%
set(globe_axes, 'NextPlot', 'add');
trajectory_plot = plot3(globe_axes, [],[],[]);
set(trajectory_plot,'Parent',globe_axes);

position_plot = plot3(globe_axes, [], [], []);
set(position_plot,'Parent',globe_axes);