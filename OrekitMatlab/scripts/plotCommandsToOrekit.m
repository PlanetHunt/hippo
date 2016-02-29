%plot commands sent to orekit
%nextWindowType(end+1), nextWindowStart(end+1), nextWindowEnd(end+1),
%nextWindowThrustDirection addToThrustCommandQue
figure; clf
subplot(2,1,1);
title('push thrust windows and Directions to orekit')
plot(timeVector(2:end),addToThrustCommandQue(:,2:end))

%thrust direction components
subplot(2,1,2);
plot(timeVector(2:end),nextWindowThrustDirection(:,2:end))
legend('x dirn','y dirn', 'z dirn')


%% %window estimates
figure; clf
subplot(3,1,1);
title('push thrust windows and Directions to orekit')
plot(timeVector(2:end),addToThrustCommandQue(:,2:end))

subplot(3,1,2);
hold on
plot(timeVector(2:end),nextWindowStart(2:end),'+')
plot(timeVector(2:end),nextWindowEnd(2:end),'x')
legend('start estimate','end estimate');

subplot(3,1,3);
hold on
plot(timeVector(2:end),nextWindowType(2:end),'o')
legend('next window type (1 2 3 4)')

% 
% subplot(5,1,3);
% hold on
% plot(timeVector(2:end),tBBoostStartCommand(2:end),'+')
% plot(timeVector(2:end),tBBoostEndCommand(2:end),'x')
% legend('B start','B end')
% 
% subplot(5,1,4);
% hold on
% plot(timeVector(2:end),tCBoostStartCommand(2:end),'+')
% plot(timeVector(2:end),tCBoostEndCommand(2:end),'x')
% legend('C start','C end')
% 
% subplot(5,1,4);
% hold on
% plot(timeVector(2:end),tDBoostStartCommand(2:end),'+')
% plot(timeVector(2:end),tDBoostEndCommand(2:end),'x')
% legend('D start','D end')