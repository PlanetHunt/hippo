function [ dV_vector ] = FTCCalcApogeeDv( orbital_elements )
%FTC_CALC_APOGEE_THRUST Summary of this function goes here
%   Detailed explanation goes here

a = orbital_elements(1);
e = orbital_elements(2);
in = orbital_elements(3);
omega = orbital_elements(4);
raan = orbital_elements(5);
mean_anomaly = orbital_elements(6);
true_anomaly = orbital_elements(7);

error = orbital_elements - orbital_elements_chief;
d_a = error(1);
d_e = error(2);
d_in = error(3);
d_omega = error(4);
d_raan = error(5);
d_mean_anomaly = error(6);
d_true_anomaly = error(7);

eta = sqrt(1-e^2);
n = sqrt(mu/a^3); %mean motion of deputy
h = n*a^2*eta; %magnitude of angular momentum vector
r = a*(1-e^2)/(1+e*cos(true_anomaly)); %scalar orbit radius

%% Impulse 2 - apogee
%radial impulse delta_v_r_a
delta_v_x = (n*a/4)*(((1-e)^2/eta)*(d_omega + d_raan*cos(i))+d_mean_anomaly); %(17)
%along track impulse delta_v_AT_a
delta_v_y = (n*a*eta/4)*(d_a/a-d_e/(1-e)); %(25)

dV_vector = [delta_v_x; delta_v_y; 0];
end

