function [ dV_vector ] = FTC_calc_LatArgNinety_thrust( orbital_elements_deputy, orbital_elements_chief )
%FTC_calc_LatArgNinety_thrust Summary of this function goes here
%   Detailed explanation goes here

a = orbital_elements_deputy(1);
e = orbital_elements_deputy(2);
i = orbital_elements_deputy(3);
RAAN = orbital_elements_deputy(4);
AoP = orbital_elements_deputy(5);
M = orbital_elements_deputy(6);
true_anomaly = orbital_elements_deputy(7);

error = orbital_elements_deputy - orbital_elements_chief;
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

%% thrust 4 - true latitude = pi/2

delta_v_z = d_RAAN*h*sin(i)/(r*sin(true_latitude)); %(7)

dV_vector = [0; 0; delta_v_z];
end





