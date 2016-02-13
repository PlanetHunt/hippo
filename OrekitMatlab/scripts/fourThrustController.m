function [ thrust_vector ] = fourThrustController( orbital_elements_deputy, true_anomaly, apside_flag, orbital_elements_chief )
%fourThrustController -This is the 4 thrust controller described by hanspeterschaub
%   see paper "Impulsive Feeback Control to Establish Specific Mean Orbit
%   Elements of SC Formations"
%   ---
%   LVLH frame of satelite:
%   thrust_vector = [v_x; v_y; v_z]; %x = radial direction, y = along track(tangential) directon, z = orbit normal
%   shaub notation: x = r, y = theta, z = h
%   
%   apside_flag = 0, 1,2 or 3. 0 = normal part of orbit, 1 = perigee, 2 = apogee, 3 = orekit failed to detect
%   apside (ie a near circular orbit)

%% get some parameters ready for later...


%calc mean orbital element tracking errors...
%from paper:"The orbit element differences describing the relative orbit are taken
%from the deputy relative to the chief satellite" - is this cheif-deupty or
%the other way?
error = orbital_elements_deputy - orbital_elements_chief;
%error = [d_a; d_e; d_i; d_RAAN; d_AoP; d_M]

%calculate sensitivities a'' and e'' and add to mean orbital element
%tracking errors above to improve the convergence rate of the controller
%(optional)...
true_latitude = argument_of_perigiee + true_anomaly;
eta = sqrt(1-e^2);
n = sqrt(mu/a^3); %mean motion of deputy
h = n*a^2*eta; %magnitude of angular momentum vector
r = a*(1-e^2)/(1+e*cos(true_anomaly); %scalar orbit radius
%% thrust 1 - perigee
%check if at perigee
%calc thrust

%radial impulse delta_v_r_p 
delta_v_x = -(n*a/4)*(((1+e)^2/eta)*(d_aop + d_RAAN*cos(i))+d_M); %(16)
%along track impulse delta_v_AT_p
delta_v_y = (n*a*eta)*(d_a/a+d_e/(1+e)); %(24)


%% thrust 2 - apogee
%check if at apogee
%calc thrust
%radial impulse delta_v_r_a
delta_v_x = (n*a/4)*(((1-e)^2/eta)*(d_aop + d_RAAN*cos(i))+d_M); %(17)
%along track impulse delta_v_AT_a
delta_v_y = (n*a*eta)*(d_a/a-d_e/(1-e)); %(25)


%% thrust 3 - true latitude = 0
%check if true latitude = 0
if(true_latitude == 0 +- tollerance) %% might have to check for multiples or 0/180?
    %calc thrust for i correction
    delta_v_z = d_i*h/(r*cos(true_latitude)); %(6)
end
%% thrust 4 - true latitude = pi/2
%check if true latitude = pi/2
if(true_latitude == pi/2 +- tollerance)
    %calc thrust for RAAN correction
    delta_v_z = d_RAAN*h*sin(i)/(r*sin(true_latitude)); %(7)
end
%%

thrust_vector = [delta_v_x;delta_v_y;delta_v_z];
end

