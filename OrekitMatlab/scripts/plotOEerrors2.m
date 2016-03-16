%plot basic mean orbit elements


%% plotting
% % global mu;
% % a= oecm(1,2);%m
% % T = 2*pi*sqrt(a^3/mu);%period of chief
 length = size(oecmMatchedTime,2);
% % 
% % startTime = datevec(timeVector(2));
% % startTime = repmat(startTime,size(timeVector,1)-1,1);
% % num_orbits = etime(datevec(timeVector(2:length)),startTime)/T;

figure; clf;
hold on
% ax1 = gca; % current axes
% ax2 = axes('Position',ax1.Position,...
%     'XAxisLocation','top',...
%     'YAxisLocation','right',...
%     'Color','none');
% 
% line(num_orbits,y2,'Parent',ax2,'Color','k')

plot(timeVector(2:length),oeError(1,2:end),'r','LineWidth',1);
hold on;
%plot(timeVector(2:length),oeErrorController(1,2:end),'b--','LineWidth',1);
title('Semimajor axis (a) Error');
ylabel('a (m)');xlabel('Time'); xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
%legend('error','controller input error term')

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:length),oeError(2,2:end),'r','LineWidth',1);
%plot(timeVector(2:length),oeErrorController(2,2:end),'b--','LineWidth',1);
xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
%legend('error','controller input error term')
title('eccentricity (e) Error');
ylabel('e');


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:length),oeError(3,2:end),'r','LineWidth',1);
%plot(timeVector(2:length),oeErrorController(3,2:end),'b--','LineWidth',1);
xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
title('inclination (i) Error');
ylabel('i (radians)');
%legend('error','controller input error term')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:length),oeError(4,2:end),'r','LineWidth',1);
%plot(timeVector(2:length),oeErrorController(4,2:end),'b--','LineWidth',1);
xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
title('Argument of Perigee (\omega) Error');
ylabel('\omega (radians)');
%legend('error','controller input error term')


figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:length),oeError(5,2:end),'r','LineWidth',1);
%plot(timeVector(2:length),oeErrorController(5,2:end),'b--','LineWidth',1);
xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
title('RAAN (\Omega) Error');
ylabel('\Omega (radians)')
%legend('error','controller input error term')

%latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));
% 
% figure; clf;
% plot(timeVector(2:length),oedm(6,2:end));
% hold on
% %plot(timeVector(2:length),oecm(6,2:end));
% plot(timeVector(2:length),latitudeArgument);
% % plot(timeVector(2:length), zeros(length(timeVector(2:length))))
% title('True Anomaly and Theta');
% ylabel('True Anomaly (radians)')
% %legend('TA deputy','LA deputy')
% 
% figure; clf;
% hold on
% plot(timeVector(2:length),latitudeArgument);
% title('Latitude Argument \theta');

figure; clf;
%subplot(6,1,1);
hold on
plot(timeVector(2:length),oeError(7,2:end),'r','LineWidth',1);
%plot(timeVector(2:length),oeErrorController(7,2:end),'b--','LineWidth',1);
xlim([datenum(timeVector(2)) datenum(timeVector(end))]) ;
title('Mean Anomaly Error');
ylabel('Mean Anomaly (radians)')
%legend('error','controller input error term')