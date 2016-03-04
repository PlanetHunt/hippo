%plot basic mean orbit elements
%% plotting
figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oeError(1,2:end));
title('semimajor axis (a) Error');
ylabel('a (m)');


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oeError(2,2:end));
title('eccentricity (e) Error');
ylabel('e');


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oeError(3,2:end));
title('inclination(i) vs Time');
ylabel('i (radians) Error');

figure; clf;
plot(timeVector(2:end),oeError(4,2:end));
title('Argument of Perigee (\omega) Error');
ylabel('\omega (radians)');

figure; clf;
plot(timeVector(2:end),oeError(5,2:end));
title('RAAN (\Omega) Error');
ylabel('\Omega (radians)')

figure; clf;
plot(timeVector(2:end),oeError(6,2:end));
title('True Anomaly Error');
ylabel('True Anomaly (radians)')

figure; clf;
plot(timeVector(2:end),oeError(7,2:end));
title('Mean Anomaly Error');
ylabel('Mean Anomaly (radians)')
