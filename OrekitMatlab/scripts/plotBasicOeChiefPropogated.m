%plot basic mean orbit elements
%% plotting

%  oec = setChiefOrbitalElements(2);
%  oecm = repmat(oec,[1,size(timeVector)]);
figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(1,2:end));
plot(timeVector(2:end),oecm(1,2:end));
title('semimajor axis (a) vs Time');
ylabel('a (m)');
legend('oecm_prop','chief')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(2,2:end));
plot(timeVector(2:end),oecm(2,2:end));
title('eccentricity (e) vs Time');
ylabel('e');
legend('oecm_prop','chief')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(3,2:end));
plot(timeVector(2:end),oecm(3,2:end));

title('inclination(i) vs Time');
ylabel('i (radians)');

legend('oecm_prop','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(4,2:end));
plot(timeVector(2:end),oecm(4,2:end));
title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (radians)');
legend('oecm_prop','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(5,2:end));
plot(timeVector(2:end),oecm(5,2:end));
title('RAAN (\Omega) vs Time');
ylabel('\Omega (radians)')
legend('oecm_prop','chief')

 latitudeArgument = wrapTo2Pi(oecm(4,2:end)+oecm(6,2:end));
% 
% figure; clf;
% plot(timeVector(2:end),oedm(6,2:end));
% hold on
% %plot(timeVector(2:end),oecm(6,2:end));
% plot(timeVector(2:end),latitudeArgument);
% % plot(timeVector(2:end), zeros(length(timeVector(2:end))))
% title('True Anomaly and Theta');
% ylabel('True Anomaly (radians)')
% legend('TA deputy','LA deputy')

figure; clf;
hold on
plot(timeVector(2:end),latitudeArgument);
title('Latitude Argument \theta');
figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_prop(7,2:end));
plot(timeVector(2:end),oecm(7,2:end));
title('Mean Anomaly');
ylabel('Mean Anomaly (radians)')
legend('oecm_prop','chief')