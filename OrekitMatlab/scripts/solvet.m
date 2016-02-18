function [ t ] = solvet( a, e, f)
%SOLVEF solves the time given the true anomaly
%   a 	semi-major axis (m)
%   e   eccentricity
%   dt  time from perigee (s)

%   f   true anomaly (rad)
mu = 3.986004415000000e+14;
T = 2*pi*sqrt(a^3/mu);%period

E = acos((cos(f) + e)/(1 + e*cos(f)));
M = E - e*sin(E);
t = M/(2*pi)*T;
end