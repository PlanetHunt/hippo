%% This file sets initial settings for a keplerian satellite orbit and a numerical propagator in Orekit:

% UWE-3 TLE:
    a = 7019120.191;
    e = 0.007879;
    in =  97.873/180*pi;
    omega = 71.994/180*pi;
    raan = 76.949/180*pi;
    true_anomaly = 339.841/180*pi;
    start_date = [2014, 01, 01, 00, 00, 00.000];
    date = start_date;
    
% Initial numerical propagator settings:
    position_tolerance = 0.01; %m
    min_step = 0.001;
    max_step = 1000;
    step_size = 60;
    duration = 60*60*2;
    choiceofProp = 0;
    
% Desired change in orbital elements:
    D_a = 2;
    D_e = 0;
    D_in = 0;
    D_omega = 0;
    D_raan = 0;
    D_mean_anomaly = 0;
   
% Initialize orbit data:
    a_all = [];
    e_all = [];
    in_all = [];
    omega_all = [];
    raan_all = [];
    mean_anomaly_all = [];
    true_anomaly_all = [];
    Time_all = [];
    notDone = true;
    
% Target orbit: 
    a_target = 7019120.191-30000;
    a_tolerance = 20000;
%     e_target = 0.007879;
%     in_target =  97.873/180*pi;
%     omega_target = 71.994/180*pi;
%     raan_target = 76.949/180*pi;
%     mean_anomaly_target = 340.151/180*pi;

% Maneuver parameters:
    per_hoh_comm = 5;               % Percentage of half keplerian period used for apside maneuvers