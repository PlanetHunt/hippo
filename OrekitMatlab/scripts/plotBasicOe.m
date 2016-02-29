%plot basic mean orbit elements
%% plotting
figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(1,2:end));
plot(timeVector(2:end),oecm(1,2:end));
title('semimajor axis (a) vs Time');
ylabel('a (m)');
legend('deputy','chief')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(2,2:end));
plot(timeVector(2:end),oecm(2,2:end));
title('eccentricity (e) vs Time');
ylabel('e');
legend('deputy','chief')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(3,2:end));
plot(timeVector(2:end),oecm(3,2:end));

title('inclination(i) vs Time');
ylabel('i (radians)');

legend('deputy','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(4,2:end));
plot(timeVector(2:end),oecm(4,2:end));
title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (radians)');
legend('deputy','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(5,2:end));
plot(timeVector(2:end),oecm(5,2:end));
title('RAAN (\Omega) vs Time');
ylabel('\Omega (radians)')
legend('deputy','chief')

latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));

figure; clf;
hold on
plot(timeVector(2:end),oedm(6,2:end));
%plot(timeVector(2:end),oecm(6,2:end));
plot(timeVector(2:end),latitudeArgument);
% plot(timeVector(2:end), zeros(length(timeVector(2:end))))
title('True Anomaly and Theta');
ylabel('True Anomaly (radians)')
legend('TA deputy','LA deputy')

figure; clf;
hold on
plot(timeVector(2:end),latitudeArgument);
title('Latitude Argument \theta');
% figure; clf;
% %subplot(6,1,1);
% hold on
% plot(timeVector(2:end),oedm(7,2:end));
% plot(timeVector(2:end),oecm(7,2:end));
% title('Mean Anomaly');
% ylabel('Mean Anomaly (radians)')
% legend('deputy','chief')