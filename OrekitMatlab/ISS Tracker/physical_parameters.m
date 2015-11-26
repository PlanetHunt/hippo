%%
% ISSTracker physical parameters
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: physical_parameters.m 33 2013-07-17 21:10:00Z Rami $
%%
classdef physical_parameters
    properties (Constant)
        % earth
        f = 1/298.257223563;
        ae = 6378136.46;
        mu = 3.986004415e14;
        
        % degree and order of earth potential
        degree = 10;
        order = 10;
        
        % atmosphere
        density = 2e-12;
        refAlt = 400e3;
        hScale = .05;
        
        % ISS
        pressurizedVolume = 937; % m^3
        dim = physical_parameters.pressurizedVolume^(1/3);
        solarArrayArea = 34 * 12 * 8;
        solarArrayRotAxis = [0, 0, 1];
        dragCoefficient = 2;
        absorptionCoefficient = .1;
        reflectionCoefficient = .9;
        
        % third bodies
        inner = false;
        outer = false;
        
        % potential model
        potential_model = 'grim5c1.gfc';
    end
    
end
