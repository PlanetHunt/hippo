%dep-chief = -100 a 0.05 i -0.01 raan deg
clear all
%test script to configure starting oes
global req 
global j2 
global g 
 req = 6378.137; %WGS84_EARTH_EQUATORIAL_RADIUS
 j2 = 1.08262668355e-3;
 g = 9.80665; %m/s^2
%% OE Chief OSC
oecBase = [7555000;0.0500000000000000;deg2rad(48);deg2rad(10);deg2rad(20);0;deg2rad(120)];
true_anomaly = meanAnomToTrueAnom(oecBase(2), oecBase(7) );
        oecBase(6) = true_anomaly;
 delta = [-63.38115; 5.6267e-05; -8.7266e-06; 5.6267e-03; -4.8267e-04; 0; 0];
% delta = [-100-12.87; 0; 0.05*pi/180-8.7266e-06; 0; -0.01*pi/180-1.2043e-04; 0; 0];
%delta = [-100; 0; 0.05*pi/180; 0; -0.01*pi/180; 0; 0];

oec = oecBase+delta;
%oec = [7554936.61885000;0.0500562670000000;0.837749314357278;0.180159625199433;0.348583180398866;2.17827223098421;2.09439510239320];
oecm  = convertOscOeToMeanOe( oec );

makeZero = oecBase-oecm
%% deputy oe
oed_delta = [-100+0.649+0.180328887887299-0.187677412293851;1.087970624098766e-06;deg2rad(0.05);-2.102567626557916e-05;deg2rad(-0.01);2.054491208403064e-05;2.169732792900447e-05];

oed=oec+oed_delta

oedm = convertOscOeToMeanOe( oed );
 error = oedm-oecm;
 targetError = [-100;0;deg2rad(0.05);0;deg2rad(-0.01);0;0];
 makeZero2 = targetError-error