%%
% ISSTracker simulation parameters
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: simulation_parameters.m 33 2013-07-17 21:10:00Z Rami $
%%
classdef simulation_parameters
    properties (Constant)
        % ephemeris times
        ephStart = -2;
        ephEnd = 5;
        
        % ground trace points
        points = 750;
        dh = 2.4;
        gtStart = - simulation_parameters.dh / 3;
        gtEnd = simulation_parameters.dh * 5 / 3;
        
        % user location - degrees
        userLat = 43.608514;
        userLon = 1.4231264;
        userAlt = 150;
        
        % propagator
        minStep  = 0.1;
        maxStep  = 50;
        absTolerance = [1e-10 1e-10 1e-10 1e-13 1e-13 1e-13 1e-10];
        relTolerance = [1e-6 1e-6 1e-6 1e-9 1e-9 1e-9 1e-7];
        
        % 3D viewpoint
        latCoef = .5;
        
        % Render earth
        renderEarth = true;
    end
end

