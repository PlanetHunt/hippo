function [ dV_vector ] = FTCCalcLatArgZeroDv( orbital_elements )
%FTC_calc_LatArgZero_thrust Summary of this function goes here
%   Detailed explanation goes here

a = orbital_elements(1);
e = orbital_elements(2);
i = orbital_elements(3);
AoP = orbital_elements(4);
RAAN = orbital_elements(5);
M = orbital_elements(6);
f = orbital_elements(7);

error = orbital_elements - orbital_elements_chief;
d_a = error(1);
d_e = error(2);
d_i = error(3);
d_RAAN = error(4);
d_AoP = error(5);
d_M = error(6);
d_f = error(7);

true_latitude = argument_of_perigiee + true_anomaly;
eta = sqrt(1-e^2);
n = sqrt(mu/a^3); %mean motion of deputy
h = n*a^2*eta; %magnitude of angular momentum vector
r = a*(1-e^2)/(1+e*cos(true_anomaly)); %scalar orbit radius

%% thrust 3 - true latitude = 0
delta_v_z = d_i*h/(r*cos(true_latitude)); %(6)

dV_vector = [0; 0; delta_v_z];
end





