%plot basic mean orbit elements
%% plotting

%  oec = setChiefOrbitalElements(2);
%  oecm = repmat(oec,[1,size(timeVector)]);
error = oedm_saved-oecm_saved;
error(3,:)=rad2deg(error(3,:));
error(4,:)=rad2deg(error(4,:));
error(5,:)=rad2deg(error(5,:));
error(6,:)=rad2deg(error(6,:));
error(7,:)=rad2deg(error(7,:));

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),error(1,2:end));
title('semimajor axis (a) vs Time');
ylabel('a (m)');
legend('oedm_saved-oecm_saved')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),error(2,2:end));
title('eccentricity (e) vs Time');
ylabel('e');
legend('oedm_saved-oecm_saved')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),error(3,2:end));

title('inclination(i) vs Time');
ylabel('i (deg)');

legend('oedm_saved-oecm_saved')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),error(4,2:end));
title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (deg)');
legend('oedm_saved-oecm_saved')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),error(5,2:end));
title('RAAN (\Omega) vs Time');
ylabel('\Omega (deg)')
legend('oedm_saved-oecm_saved')


figure; clf
hold on
plot(timeVector(2:end),error(7,2:end));
title('Mean Anomaly');
ylabel('Mean Anomaly (deg)')
legend('oedm_saved-oecm_saved')