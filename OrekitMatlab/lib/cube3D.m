trajectory = [];
earth_radius = 6374e3;

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

% ax = axes('XLim',[-1 1],'YLim',[-1 1],'ZLim',[-1 1]);
world_object = hgtransform('Parent',globe_axes);
load topo
[x,y,z] = sphere(200);          % create a sphere
s = surface(earth_radius*x,earth_radius*y,earth_radius*z, 'LineStyle', 'none', 'EdgeColor', 'none');            % plot spherical surface

s.CData = topo;                % set color data to topographic data
s.FaceColor = 'texturemap';    % use texture mapping
s.EdgeColor = 'none';          % remove edges
s.FaceLighting = 'gouraud';    % preferred lighting for curved surfaces
s.SpecularStrength = 0.4;      % change the strength of the reflected light

set(s,'Parent',world_object);

light_handle = light('Position',[-1 0 1]);     % add a light

axis square                % set axis to square and remove axis
view([-30,30])                 % set the viewing angle
set(world_object, 'Matrix', makehgtform('zrotate', 0))

set(globe_axes, 'NextPlot', 'add');
trajectory_plot = plot3(globe_axes, [],[],[]);
set(trajectory_plot,'Parent',globe_axes);

position_plot = plot3(globe_axes, [], [], []);
set(position_plot,'Parent',globe_axes);

xlabel('X [m]')
ylabel('Y [m]')
zlabel('Z [m]')

mafilename('fullpath')

%%
% x=[0 1 1 0 0 0;1 1 0 0 1 1;1 1 0 0 1 1;0 1 1 0 0 0];
% y=[0 0 1 1 0 0;0 1 1 0 0 0;0 1 1 0 1 1;0 0 1 1 1 1];
% z=[0 0 0 0 0 1;0 0 0 0 0 1;1 1 1 1 0 1;1 1 1 1 0 1];
% satellite_object = hgtransform('Parent',ax);
% 
% for i=1:6
%     h(i)=patch(x(:,i),y(:,i),z(:,i),'b');
% end
% set(h,'Parent',satellite_object);
% inital_translate = makehgtform('translate', [-0.5, -0.5, -0.5]);
% set(satellite_object, 'Matrix', inital_translate)
% view(3)
% % xlim([-1,1])
% % ylim([-1,1])
% % zlim([-1,1])
% 
% 
% xlim([-1.5*earth_radius,1.5*earth_radius])
% ylim([-1.5*earth_radius,1.5*earth_radius])
% zlim([-1.5*earth_radius,1.5*earth_radius])
% axis square;
% hold off
