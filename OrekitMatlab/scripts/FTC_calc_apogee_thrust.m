function [ dV_vector ] = FTC_calc_apogee_thrust( orbital_elements_deputy, orbital_elements_chief )
%FTC_CALC_APOGEE_THRUST Summary of this function goes here
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

%% thrust 2 - apogee
%check if at apogee
%calc thrust
%radial impulse delta_v_r_a
delta_v_x = (n*a/4)*(((1-e)^2/eta)*(d_aop + d_RAAN*cos(i))+d_M); %(17)
%along track impulse delta_v_AT_a
delta_v_y = (n*a*eta)*(d_a/a-d_e/(1-e)); %(25)

dV_vector = [delta_v_x; delta_v_y; 0];
end

