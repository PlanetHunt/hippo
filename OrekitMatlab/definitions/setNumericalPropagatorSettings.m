function [position_tolerance, min_step, max_step, duration, step_size, choiceofProp, max_check] = setNumericalPropagatorSettings()

position_tolerance = 10; %m
min_step = 0.001;
max_step = 120;
step_size = 60;
duration = 108*60*4%3600*24*7;
choiceofProp = 0;
max_check = 1; %this is for the date detector
% duration = 24525000*2;