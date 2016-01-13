function [result] = plotOrbitElements( timestamp, orbital_elements)
%PLOTORBITELEMENTS plots the 6 orbital elements agains time
%   timeVector = [t1 t2 ...tn]
%   oeVector = [a1 a2 ...an;
%               e1 e2 ...en;
%                i1 i2 ...in;
%                RAAN1 RAAN2 ...RAANn;
%                AoP1 AoP2 ...AoPn;
%                M1 M2 ...Mn]
%
%create matlab datetime array
t = datetime(cell2mat(timestamp'));
%put orbital eles in matrix format
oe = cell2mat(orbital_elements');

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
%title('Mean anomaly (M) vs Time');
xlabel('time (s)'); ylabel('M (radians)');

result = 1;
end

