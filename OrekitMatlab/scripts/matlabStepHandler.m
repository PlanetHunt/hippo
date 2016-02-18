function [ output_args ] = matlabStepHandler( orbital_elements, time )
%MATLABSTEPHANDLER function to be called at every time step
%   event_A     perigee
%   event_B     apogee
%   event_C     theta = 0
%   event_D     theta = 90 deg
mu = 3.986004415000000e+14;
oe = cell2mat(orbital_elements');

a = oe(1);
e = oe(2);
in = oe(3);
omega = oe(4);
raan = oe(5);
mean_anomaly = oe(6);
true_anomaly = oe(7);

[~, ~, ~, ~, step_size, ~] = setNumericalPropagatorSettings();

[tAStart, tAEnd, tBStart, tBEnd, tCStart, tCEnd, tDStart, tDEnd] = calulateEventTimes(a, e, in, omega, raan, mean_anomaly, true_anomaly, time, step_size);


end

