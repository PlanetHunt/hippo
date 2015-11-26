%% Simple Orbit Setup
% UWE-3                   
% 1 39446U 13066AG  15075.17710411  .00001656  00000-0  23347-3 0  9992
% 2 39446  97.7377 139.1331 0073569  84.1257 276.8334 14.76679371 69522


% a = 7018e3;       % semi major axis in meters
% e = 0.0073569;     % eccentricity
% incl = 97.74/180*pi;    % inclination
% omega = 84.13/180*pi; % perigee argument
% raan = 139.1331/180*pi;  % right ascension of ascending node
% lM = 276.8334;             % mean anomaly

%         mu = 3.986004415000000e+14;
%         n = 0.0614441*86164/360;
%         a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
%         e = 0.0075985;
%         incl = 97.7953/180*pi;
%         omega = 101.887/180*pi;
%         raan = 65.6956/180*pi;
% %         lM = 259.084;
%         lM = 4.52187;

%         mu = 3.986004415000000e+14;
%         n = 0.0614441*86164/360;
        a = 7019120.191;
        e = 0.007879;
        incl =  97.873/180*pi;
        omega = 71.994/180*pi;
        raan = 76.949/180*pi;
        lM = 340.151/180*pi;


% date = [2015, 03, 16, 04, 15, 01.000];
date = [2014, 01, 01, 00, 00, 00.000];


%% Time and Propagator Settings for Analytical Propagator

duration = 60*96*0;
step_size = 60;
type = 'Keplerian';


%% Numerical Propagator Settings
positionTolerance = 0.1; %m
minStep = 0.001;
maxstep = 1000.0;
%  duration_numeric = 24525000*2;
% duration_numeric = 60*96*5;
duration_numeric = 245250*2;
output_step_size = 60;
% output_step_size = 10;