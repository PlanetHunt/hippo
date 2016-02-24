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
% 
% %%
% figure; clf;
% %subplot(6,1,1);
% hold on
% %double axis plot
% [ax,pa1,pa2] = plotyy(timeVector(2:end),tABoostStartCommand(2:end),timeVector(2:end), fireA(2:end))
% 
% pa1.Marker = '*';
% pa1.MarkerEdgeColor = 'r'
% pa1.MarkerFaceColor = 'r'
% 
% pa2.Marker = '+';
% pa2.MarkerEdgeColor = 'b'
% pa2.MarkerFaceColor = 'b'
% 
% axes(ax(1)); hold on;
% plot(timeVector(2:end),tABoostEndCommand(2:end),'rx');
% plot(timeVector(2:end),tBBoostStartCommand(2:end),'b*');
% plot(timeVector(2:end),tBBoostEndCommand(2:end),'bx');
% 
% axes(ax(2)); hold on;
% plot(timeVector(2:end), fireB(2:end),'b+');
% 
% title('boost start and end estimates + Boost Times');
% xlabel(ax(1),'Time') % label x-axis
% ylabel(ax(1),'tBBoostEndCommand') % label left y-axis
% ylabel(ax(2),'fire') % label right y-axis
% 
% legend('tABoostStartCommand','fireA','tABoostEndCommand','tbBoostStartCommand','tBBoostEndCommand','fireB');
% %%
% 
% %% same plot but with f instead of commands
% figure; clf;
% %subplot(6,1,1);
% hold on
% %double axis plot
% 
% [ax,pa1,pa2] = plotyy(timeVector(2:end),oedm(6,2:end),timeVector(2:end), fireA(2:end))
% 
% pa1.Marker = '*';
% pa1.MarkerEdgeColor = 'r'
% pa1.MarkerFaceColor = 'r'
% 
% pa2.Marker = '+';
% pa2.MarkerEdgeColor = 'b'
% pa2.MarkerFaceColor = 'b'
% 
% % axes(ax(1)); hold on;
% % plot(timeVector(2:end),tABoostEndCommand(2:end),'rx');
% % plot(timeVector(2:end),tBBoostStartCommand(2:end),'b*');
% % plot(timeVector(2:end),tBBoostEndCommand(2:end),'bx');
% 
% axes(ax(2)); hold on;
% plot(timeVector(2:end), fireB(2:end),'b+');
% 
% title('Boost Times');
% xlabel(ax(1),'Time') % label x-axis
% ylabel(ax(1),'trueAnomaly') % label left y-axis
% ylabel(ax(2),'fire') % label right y-axis
% 
% legend('true anomaly','fireA','fireB');
% %%
% 
% 
% figure; clf;
% %subplot(6,1,1);
% hold on
% %double axis plot
% [ax,pa1,pa2] = plotyy(timeVector(2:end),tCBoostStartCommand(2:end),timeVector(2:end), fireC(2:end))
% 
% pa1.Marker = '*';
% pa1.MarkerEdgeColor = 'r'
% pa1.MarkerFaceColor = 'r'
% 
% pa2.Marker = '+';
% pa2.MarkerEdgeColor = 'b'
% pa2.MarkerFaceColor = 'b'
% 
% axes(ax(1)); hold on;
% plot(timeVector(2:end),tCBoostEndCommand(2:end),'rx');
% plot(timeVector(2:end),tDBoostStartCommand(2:end),'b*');
% plot(timeVector(2:end),tDBoostEndCommand(2:end),'bx');
% 
% axes(ax(2)); hold on;
% plot(timeVector(2:end), fireD(2:end),'b+');
% 
% title('boost start and end estimates + Boost Times');
% xlabel(ax(1),'Time') % label x-axis
% ylabel(ax(1),'tBBoostEndCommand') % label left y-axis
% ylabel(ax(2),'fire') % label right y-axis
% 
% legend('tCBoostStartCommand','fireC','tCBoostEndCommand','tDBoostStartCommand','tDBoostEndCommand','fireD');
% %%
% 
% %% same plot but with THETA instead of commands
% latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));
% figure; clf;
% %subplot(6,1,1);
% hold on
% %double axis plot
% 
% [ax,pa1,pa2] = plotyy(timeVector(2:end),latitudeArgument,timeVector(2:end), fireC(2:end))
% 
% pa1.Marker = '*';
% pa1.MarkerEdgeColor = 'r'
% pa1.MarkerFaceColor = 'r'
% 
% pa2.Marker = '+';
% pa2.MarkerEdgeColor = 'b'
% pa2.MarkerFaceColor = 'b'
% 
% % axes(ax(1)); hold on;
% % plot(timeVector(2:end),tABoostEndCommand(2:end),'rx');
% % plot(timeVector(2:end),tBBoostStartCommand(2:end),'b*');
% % plot(timeVector(2:end),tBBoostEndCommand(2:end),'bx');
% 
% axes(ax(2)); hold on;
% plot(timeVector(2:end), fireD(2:end),'b+');
% 
% title('Boost Times');
% xlabel(ax(1),'Time') % label x-axis
% ylabel(ax(1),'THETA') % label left y-axis
% ylabel(ax(2),'fire') % label right y-axis
% 
% legend('THETA','fireC','fireD');
% %%
% figure
% plot(timeVector(2:end),vel(:,2:end));
% title('velocity');
% ylabel('V');
% figure
% plot(timeVector(2:end),pos(:,2:end));
% title('Position');
% ylabel('Pos');
% %%
% % latitudeArgument = wrapTo2Pi(oedm(4,2:end)+oedm(6,2:end));
% % figure
% % clf
% % hold on
% % plot(timeVector(2:end),latitudeArgument);
% % plot(timeVector(2:end),4.*fireD(2:end),'gx');
% % plot(timeVector(2:end),3.*fireC(2:end),'y*');
% % plot(tCBoostStartCommand(2:end),ones(length(tCBoostStartCommand)-1),'bx');
% % plot(tCBoostEndCommand(2:end),2.*ones(length(tCBoostStartCommand)-1),'r+');
% % legend('\theta','fireD','fireC','c boost start command','c boost end command')  
% % title('Latitude Argument (\theta) vs Time')
% % xlabel('time (s)'); ylabel('\theta (radians)');
