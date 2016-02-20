%this was not used in the end, we detect events according to the time rather than
%with the true anomaly

%calc the tolerance that should be used for perigee and apogeee detection (in radians)
mu = 3.986004415000000e+14;

a = 7.019120190999998e+06;
e = 0.007879000000000;
dt = 60; %orekit propogation time step

perigeeDetectionTolerance = solvef(a,e,dt/2) %tolerance in true anomaly
apogeeDetectionTolerance = pi-solvef(a,e,pi*sqrt(a^3/mu)-dt/2) %tolerance in true anomaly
thetaZeroDetectionTolerance = -omega