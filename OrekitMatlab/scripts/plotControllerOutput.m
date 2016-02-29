%plot output of controller algorithms

%% input to the controller (error terms)

figure; clf;
plot(timeVector(2:end),oeError(1,2:end));
title('semimajor axis ERROR (a) vs Time');
ylabel('a (m)');

figure; clf;
plot(timeVector(2:end),oeError(2,2:end));
title('eccentricity ERROR(e) vs Time');
ylabel('e');

figure; clf;
plot(timeVector(2:end),oeError(3,2:end));
title('inclination(i) ERROR vs Time');
ylabel('i (radians)');

figure; clf;
plot(timeVector(2:end),oeError(4,2:end));
title('Argument of Perigee (\omega) ERROR vs Time');
ylabel('\omega (radians)');

figure; clf;
plot(timeVector(2:end),oeError(5,2:end));
title('RAAN (\Omega) ERROR vs Time');
ylabel('\Omega (radians)')

figure; clf;
plot(timeVector(2:end),oeError(6,2:end));
title('True Anomaly ERROR');
ylabel('True Anomaly (radians)')

figure; clf;
plot(timeVector(2:end),oeError(7,2:end));
title('Mean Anomaly ERROR');
ylabel('Mean Anomaly (radians)')