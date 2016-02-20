function [ dV_vector ] = FTCCalcLatArgZeroDv( oeDeputy, oeerror )
%FTCCalcLatArgZeroDv calculates the thrust (delta V) required at the point
%where the argument of lattitude = 0
%   Based on the four thrust controller (FTC) described in Hans-Peter
%   Schaub's paper "Impulsive Feeback Control to Establish Specific Mean Orbit
%   Elements of SC Formations"
%   ---
%   LVLH frame of satelite:
%   thrust_vector = [v_x; v_y; v_z]; %x = radial direction, y = along track(tangential) directon, z = orbit normal
%   Shaub notation: x = r, y = theta, z = h
%   equations from the paper have the equation number in the comment, eg
%   (17)
global mu;
a = oeDeputy(1);
e = oeDeputy(2);
%i = oeDeputy(3);
omega = oeDeputy(4);
%raan = oeDeputy(5);
true_anomaly = oeDeputy(6);
%M = oeDeputy(7);

%d_a = oeerror(1);
%d_e = oeerror(2);
d_i = oeerror(3);
%d_omega = oeerror(4);
%d_raan = oeerror(5);
%d_true_anomaly = oeerror(6);
%d_M = oeerror(7);

true_latitude = omega + true_anomaly;
eta = sqrt(1-e^2);
n = sqrt(mu/a^3); %mean motion of deputy
h = n*a^2*eta; %magnitude of angular momentum vector
r = a*(1-e^2)/(1+e*cos(true_anomaly)); %scalar orbit radius

%% thrust 3 (D) - true latitude = 0
delta_v_z = d_i*h/(r*cos(true_latitude)); %(6)

dV_vector = [0; 0; delta_v_z];
end





