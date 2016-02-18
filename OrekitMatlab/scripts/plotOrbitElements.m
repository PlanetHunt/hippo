function [result] = plotOrbitElements( timestamp, orbital_elements, apogee_detections, perigee_detections, latitude_arg_ninety_detections, latitude_arg_zero_detections)
%PLOTORBITELEMENTS plots the 6 orbital elements agains time
%   timeVector = [t1 t2 ...tn]
%   oeVector = [a1 a2 ...an;
%               e1 e2 ...en;
%                i1 i2 ...in;
%                AoP1 AoP2 ...AoPn;
%                RAAN1 RAAN2 ...RAANn;
%                M1 M2 ...Mn]    
%                f1 f2 ...fn]
%
%create matlab datetime array
t = datetime(cell2mat(timestamp'));
%put orbital eles in matrix format
oe = cell2mat(orbital_elements');

%calculate radius
%formula 4.43 http://www.braeunig.us/space/orbmech.htm
%r = a*(1-e^2)/(1+e*cos(f));
r = oe(:,1).*(1-oe(:,2).^2)./(1+oe(:,2).*cos(oe(:,7)));


latitude_arg_ninety_detection_array = cell2mat(latitude_arg_ninety_detections');%convert to array
latitude_arg_ninety_detection_flags = (datetime(latitude_arg_ninety_detection_array)~=datetime([0,0,0,0,0,0])); %makes a 1xn vector with a '1' flag at every row where there is some detection event
latitude_arg_ninety_detection_rows = latitude_arg_ninety_detection_flags~=0; %returns row numbers where detection event is
latitude_arg_ninety_detection_times = datetime(latitude_arg_ninety_detection_array(latitude_arg_ninety_detection_rows,:)); %selects the subset from the detections_Array that corresoponds to the detections (filters out all of the zero rows)


latitude_arg_zero_detection_array = cell2mat(latitude_arg_zero_detections');%convert to array
latitude_arg_zero_detection_flags = (datetime(latitude_arg_zero_detection_array)~=datetime([0,0,0,0,0,0])); %makes a 1xn vector with a '1' flag at every row where there is some detection event
latitude_arg_zero_detection_rows = latitude_arg_zero_detection_flags~=0; %returns row numbers where detection event is
latitude_arg_zero_detection_times = datetime(latitude_arg_zero_detection_array(latitude_arg_zero_detection_rows,:)); %selects the subset from the detections_Array that corresoponds to the detections (filters out all of the zero rows)

%apogee_detections_array = 
%[ y m d h m s apogee_value; %detection 1
%  0 0 0 0 0 0 0;
%  0 0 0 0 0 0 0;   
% y m d h m s apogee_value; detection 2
% 0 0 0 0 0 0 0]

apogee_detections_array = cell2mat(apogee_detections');%convert to array
apogee_detection_flags = (double(apogee_detections_array(:,7))' ~= 0); %makes a 1xn vector with a '1' flag at every row where there is some detection event
apogee_detection_rows = find(apogee_detection_flags~=0); %returns row numbers where detection event is
apogee_detections_array_trimmed = apogee_detections_array(apogee_detection_rows,:); %selects the subset from the apogee_detections_Array that corresoponds to the detections (filters out all of the zero rows)
apogee_detection_times = datetime(apogee_detections_array_trimmed(:,1:6)); %convert to a matlab time object
apogee_values = apogee_detections_array_trimmed(:,7);

perigee_detections_array = cell2mat(perigee_detections');%convert to array
perigee_detection_flags = (double(perigee_detections_array(:,7))' ~= 0); %makes a 1xn vector with a '1' flag at every row where there is some detection event
perigee_detection_rows = find(perigee_detection_flags~=0); %returns row numbers where detection event is
perigee_detections_array_trimmed = perigee_detections_array(perigee_detection_rows,:); %selects the subset from the perigee_detections_Array that corresoponds to the detections (filters out all of the zero rows)
perigee_detection_times = datetime(perigee_detections_array_trimmed(:,1:6)); %convert to a matlab time object
perigee_values = perigee_detections_array_trimmed(:,7)

%% plotting
figure; clf;
%subplot(6,1,1);
plot(t,oe(:,1));
title('semimajor axis (a) vs Time');
ylabel('a (m)');

%subplot(6,1,2);
figure
plot(t,oe(:,2));
title('Eccentricity (e) vs Time');
ylabel('e (m)');

%subplot(6,1,3);
figure
plot(t,oe(:,3));
title('inclination(i) vs Time');
ylabel('i (radians)');

%subplot(6,1,4);
figure
plot(t,oe(:,5));
title('Right Ascention of Acending Node (\Omega) vs Time');
ylabel('\Omega (radians)');

%subplot(6,1,5);
figure
plot(t,oe(:,4));
%title('Argument of Perigee (\omega) vs Time');
%ylabel('\omega (radians)');
hold on
%subplot(6,1,6);
%figure
plot(t,oe(:,6));
%title('Mean anomaly (M) vs Time');
xlabel('time (s)'); ylabel('angle (radians)');
plot(t,oe(:,7));

%figure
latitudeArgument = oe(:,4)+oe(:,7);
plot(t,latitudeArgument);
plot(latitude_arg_ninety_detection_times,ones(size(latitude_arg_ninety_detection_times)),'bx');
plot(latitude_arg_zero_detection_times,ones(size(latitude_arg_zero_detection_times)),'bo');
%title('Latitude Argument (\theta) vs Time')
%xlabel('time (s)'); ylabel('\theta (radians)');
legend('Argument of Perigee (\omega)','M','f','\theta','\theta = \pi/2 detection','\theta=0 detection')

figure

plot(t,r)
hold on
plot(apogee_detection_times,apogee_values,'x');
plot(perigee_detection_times,perigee_values,'*');
legend('radius','apogee detection events','perigee detection events')
result = 1;
end

