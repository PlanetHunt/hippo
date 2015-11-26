
earth_radius = 6374e3;
trajectory = [trajectory; [Position_x, Position_y, Position_z]];
% velocity_dev = [velocity_dev; [vel_x, vel_y, vel_z]];
% time_all =[time_all; Time];
% orb_period = [orb_period; period];
%% update drawing script
% translation = makehgtform('translate', [Position_x, Position_y, Position_z]);
% set(satellite_object, 'Matrix', translation);
% xlim([-1.5*earth_radius,1.5*earth_radius])
% ylim([-1.5*earth_radius,1.5*earth_radius])
% zlim([-1.5*earth_radius,1.5*earth_radius])
% 
% axis square

%%
% delete(trajectory_plot);
% % delete(light_handle);
% % Sun_Position
% trajectory_plot = plot3(globe_axes, trajectory(:,1), trajectory(:,2), trajectory(:,3));
% set(light_handle, 'Position',Sun_Position);     % adjust sunlight
% axis equal
% % EarthOrbitPlot(trajectory*1e-3);
% 
% 
% %%
% delete(position_plot);
% 
% position_plot = plot3(globe_axes, Position_x, Position_y, Position_z, '+');
% axis equal
% % EarthOrbitPlot(trajectory*1e-3);
% 
% 
% xlim([-1.5*earth_radius,1.5*earth_radius])
% ylim([-1.5*earth_radius,1.5*earth_radius])
% zlim([-1.5*earth_radius,1.5*earth_radius])

%%
% figure
% plot3(trajectory(:,1), trajectory(:,2), trajectory(:,3))