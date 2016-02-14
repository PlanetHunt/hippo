function [result] = plotOrbitElements( timestamp, orbital_elements, apogee_detections, perigee_detections, latitude_arg_ninety_detections, latitude_arg_zero_detections)
%PLOTORBITELEMENTS plots the 6 orbital elements agains time
%   timeVector = [t1 t2 ...tn]
%   oeVector = [a1 a2 ...an;
%               e1 e2 ...en;
%                i1 i2 ...in;
%                RAAN1 RAAN2 ...RAANn;
%                AoP1 AoP2 ...AoPn;
%                M1 M2 ...Mn]    
%                f1 f2 ...fn]
%
%create matlab datetime array
t = datetime(cell2mat(timestamp'));
%put orbital eles in matrix format
oe = cell2mat(orbital_elements');
%convert detections into flags at the appropriate time index
%
latitude_arg_ninety_detection_flags = double(datetime(cell2mat(latitude_arg_ninety_detections')) ~= datetime([0,0,0,0,0,0]));
latitude_arg_zero_detection_flags = double(datetime(cell2mat(latitude_arg_zero_detections')) ~= datetime([0,0,0,0,0,0]));

apogee_detections_array = cell2mat(apogee_detections');
perigee_detections_array = cell2mat(perigee_detections');

apogee_detection_flags = double(apogee_detections_array(:,7) ~= 0);
perigee_detection_flags = double(perigee_detections_array(:,7) ~= 0);


%% plotting
figure; clf;
subplot(6,1,1);
plot(t,oe(:,1));
%title('semimajor axis (a) vs Time');
ylabel('a (m)');

subplot(6,1,2);
plot(t,oe(:,2));
%title('Eccentricity (e) vs Time');
ylabel('e (m)');

subplot(6,1,3);
plot(t,oe(:,3));
%title('inclination(i) vs Time');
ylabel('i (radians)');

subplot(6,1,4);
plot(t,oe(:,4));
%title('Right Ascention of Acending Node (\Omega) vs Time');
ylabel('\Omega (radians)');

subplot(6,1,5);
plot(t,oe(:,5));
%title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (radians)');

subplot(6,1,6);
plot(t,oe(:,6));
%title('True anomaly (f) vs Time');
xlabel('time (s)'); ylabel('f (radians)');

figure
hold on
latitudeArgument = oe(:,5)+oe(:,7);
plot(t,latitudeArgument);
plot(t,latitude_arg_zero_detection_flags,'bo');
plot(t,latitude_arg_ninety_detection_flags,'bx');
title('Latitude Argument (\theta) vs Time')
xlabel('time (s)'); ylabel('\theta (radians)');
legend('\theta','\theta=0 detection','\theta = \pi/2 detection')
result = 1;
end

