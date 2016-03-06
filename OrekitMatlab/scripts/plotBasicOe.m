%plot basic mean orbit elements
%% plotting
global mu;
a= oecm(1,2);%m
T = 2*pi*sqrt(a^3/mu);%period of chief

startTime = datevec(timeVector(2));
startTime = repmat(startTime,size(timeVector,1)-1,1);
num_orbits = etime(datevec(timeVector(2:end)),startTime)/T;

% figure; clf;
% hold on
% ax1 = gca; % current axes
% ax2 = axes('Position',ax1.Position,...
%     'XAxisLocation','top',...
%     'YAxisLocation','right',...
%     'Color','none');
% 
% line(num_orbits,y2,'Parent',ax2,'Color','k')


plot(timeVector(2:end),oecmMatchedTime(1,2:end),'r','LineWidth',2);
plot(timeVector(2:end),oedm(1,2:end),'b--','LineWidth',2);
title('Semimajor axis (a) vs Time');
ylabel('a (m)');xlabel('Time'); xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
legend('Chief','Deputy')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(2,2:end));
plot(timeVector(2:end),oecmMatchedTime(2,2:end));
title('eccentricity (e) vs Time');
ylabel('e');
legend('deputy','chief')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(3,2:end));
plot(timeVector(2:end),oecmMatchedTime(3,2:end));

title('inclination(i) vs Time');
ylabel('i (radians)');

legend('deputy','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(4,2:end));
plot(timeVector(2:end),oecmMatchedTime(4,2:end));
title('Argument of Perigee (\omega) vs Time');
ylabel('\omega (radians)');
legend('deputy','chief')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(5,2:end));
plot(timeVector(2:end),oecmMatchedTime(5,2:end));
title('RAAN (\Omega) vs Time');
ylabel('\Omega (radians)')
legend('deputy','chief')

latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));

figure; clf;
plot(timeVector(2:end),oedm(6,2:end));
hold on
%plot(timeVector(2:end),oecm(6,2:end));
plot(timeVector(2:end),latitudeArgument);
% plot(timeVector(2:end), zeros(length(timeVector(2:end))))
title('True Anomaly and Theta');
ylabel('True Anomaly (radians)')
%legend('TA deputy','LA deputy')

figure; clf;
hold on
plot(timeVector(2:end),latitudeArgument);
title('Latitude Argument \theta');

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:end),oedm(7,2:end));
plot(timeVector(2:end),oecmMatchedTime(7,2:end));
title('Mean Anomaly');
ylabel('Mean Anomaly (radians)')
legend('deputy','chief')