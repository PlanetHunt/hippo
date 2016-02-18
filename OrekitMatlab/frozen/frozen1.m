% frozen1.m       April 24, 2008

% determines mean orbital eccentricity
% for frozen orbits

% Orbital Mechanics with Matlab

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clear all;

global mu j2 j3 req sma inc argper mm

dtr = pi / 180;

rtd = 180 / pi;

mu = 398600.5;

req = 6378.14;

% first zonal coefficient (non-dimensional)

j2 = 1.08262668355e-3;

% second zonal coefficient (non-dimensional)

j3 = -2.53265648533e-6;

% begin simulation

clc; home;
   
fprintf('\n          program frozen1\n');
   
fprintf('\n< orbital eccentricity of frozen orbits >\n\n');

% request mean orbital elements

fprintf('\nmean orbital elements\n');

oev = getoe([1; 0; 1; 0; 0; 0]);

% semimajor axis (kilometers)

sma = oev(1);

% orbital inclination

inc = oev(3);

% set mean argument of perigee to 90 degrees

argper = 90 * dtr;

oev(4) = argper;

% compute mean motion

mm = sqrt(mu / (sma * sma * sma));

% solve frozen orbit equation

rtol = 1.0e-12;

x1 = 1.0e-8;

x2 = 0.1;

[xroot, froot] = brent ('frzfunc', x1, x2, rtol);

oev(2) = xroot;

% print results

clc; home;

fprintf('\n     mean orbital elements\n');

oeprint1(mu, oev);

% coefficients of the frozen eccentricity equation
% (assumes mean argument of perigee = 90 degrees)

coef(1) = -(3/4)* mm * (req/sma)^2 * j2 * sin(inc) ...
          * (1 - 5 * cos(inc)^2);

coef(2) = (3/2) * mm * (req/sma)^3 * j3 * (1 - (35/4) ...
          * sin(inc)^2 * cos(inc)^2);

coef(3) = -coef(1);

coef(4) = (3/2) * mm * (req/sma)^3 * j3 * sin(inc)^2 ...
          * ((5/4) * sin(inc)^2 - 1);

fprintf('\n\n roots of the frozen eccentricity cubic equation\n');

roots(coef)
