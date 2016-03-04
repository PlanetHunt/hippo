%plot basic mean orbit elements
%% plotting

%  oec = setChiefOrbitalElements(2);
%  oecm = repmat(oec,[1,size(timeVector)]);
figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(1,2:end));
plot(timeVector(2:end),oedm_saved(1,2:end));
title('semimajor axis (a) vs Time');
ylabel('a (m)');
legend('oecm_saved','oedm_saved')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(2,2:end));
plot(timeVector(2:end),oedm_saved(2,2:end));
title('eccentricity (e) vs Time');
ylabel('e');
legend('oecm_saved','oedm_saved')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(3,2:end));
plot(timeVector(2:end),oedm_saved(3,2:end));

title('inclination(i) vs Time');
ylabel('i (radians)');

legend('oecm_saved','oedm_saved')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(4,2:end));
plot(timeVector(2:end),oedm_saved(4,2:end));
title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (radians)');
legend('oecm_saved','oedm_saved')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(5,2:end));
plot(timeVector(2:end),oedm_saved(5,2:end));
title('RAAN (\Omega) vs Time');
ylabel('\Omega (radians)')
legend('oecm_saved','oedm_saved')


%subplot(6,1,1);
hold on
plot(timeVector(2:end),oecm_saved(7,2:end));
plot(timeVector(2:end),oedm_saved(7,2:end));
title('Mean Anomaly');
ylabel('Mean Anomaly (radians)')
legend('oecm_saved','oedm_saved')