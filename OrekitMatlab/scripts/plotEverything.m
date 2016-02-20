%plot orb eles
%plot mass
%plot thrust start times and end times
%plot thrust direcitons

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


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(6,2:end));
plot(timeVector(2:end),oecm(6,2:end));
plot(timeVector(2:end),fireA(2:end),'r*');
plot(timeVector(2:end),fireB(2:end).*2,'k*');
plot(tABoostStartCommand(2:end),ones(length(tABoostStartCommand)-1),'bx');
plot(tABoostEndCommand(2:end),ones(length(tABoostEndCommand)-1),'bo');

title('True anomaly (f) vs Time');
xlabel('time (s)'); ylabel('true anomaly (radians)');
legend('deputy','chief','fireA','fireB','tABoostStartCommand','tABoostEndCommand')


figure
latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));
hold on
plot(timeVector(2:end),latitudeArgument);

plot(tCBoostStartCommand(2:end),ones(length(tCBoostStartCommand)-1),'bx');
plot(tCBoostEndCommand(2:end),ones(length(tDBoostStartCommand)-1),'bo');
plot(timeVector(2:end),fireC(2:end),'k*');
title('Latitude Argument (\theta) vs Time')
xlabel('time (s)'); ylabel('\theta (radians)');
legend('\theta','c boost start command','c boost end command','fireC')


% 
% %subplot(6,1,2);
% figure
% plot(t,oe(:,2));
% title('Eccentricity (e) vs Time');
% ylabel('e (m)');
% 
% %subplot(6,1,3);
% figure
% plot(t,oe(:,3));
% title('inclination(i) vs Time');
% ylabel('i (radians)');
% 
% %subplot(6,1,4);
% figure
% plot(t,oe(:,5));
% title('Right Ascention of Acending Node (\Omega) vs Time');
% ylabel('\Omega (radians)');
% 
% %subplot(6,1,5);
% figure
% plot(t,oe(:,4));
% %title('Argument of Perigee (\omega) vs Time');
% %ylabel('\omega (radians)');
% hold on
% %subplot(6,1,6);
% %figure
% plot(t,oe(:,6));
% %title('Mean anomaly (M) vs Time');
% xlabel('time (s)'); ylabel('angle (radians)');
% plot(t,oe(:,7));
% 
% %figure
% latitudeArgument = oe(:,4)+oe(:,7);
% plot(t,latitudeArgument);
% plot(latitude_arg_ninety_detection_times,ones(size(latitude_arg_ninety_detection_times)),'bx');
% plot(latitude_arg_zero_detection_times,ones(size(latitude_arg_zero_detection_times)),'bo');
% %title('Latitude Argument (\theta) vs Time')
% %xlabel('time (s)'); ylabel('\theta (radians)');
% legend('Argument of Perigee (\omega)','M','f','\theta','\theta = \pi/2 detection','\theta=0 detection')

