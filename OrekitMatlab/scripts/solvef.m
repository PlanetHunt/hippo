function [ f ] = solvef( a, e, dt)
%SOLVEF solves the true anomaly
%   a 	semi-major axis (m)
%   e   eccentricity
%   dt  time from perigee (s)

%   f   true anomaly (rad)

tolerance = 0.00001;
mu = 3.986004415000000e+14;

T = 2*pi*sqrt(a^3/mu);%period
M = (dt/T)*2*pi; %mean anomaly
E_old = 0; %Eccentric Anomaly
E = M + e*sin(M); %Eccentric Anomaly

while (abs(E-E_old) > tolerance) %iterate to solve eccentric anomaly
    E_old = E;
    E = M + e*sin(E);
end

f = acos( (cos(E)-e) / (1-e*cos(E)) ); %true anomaly (rad)
end