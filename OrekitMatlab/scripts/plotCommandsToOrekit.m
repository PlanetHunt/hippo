%plot commands sent to orekit
%nextWindowType(end+1), nextWindowStart(end+1), nextWindowEnd(end+1),
%nextWindowThrustDirection addToThrustCommandQue
numberOfIndexes = length(eventTypes);
x=1:1:numberOfIndexes;
figure; clf
subplot(2,1,1);
title('add')
plot(x(2:end),addEventToOrekitDateTimeDetector(:,2:end))

%thrust direction components
subplot(2,1,2);
plot(x(2:end),thrustDirection(:,2:end))
legend('x dirn','y dirn', 'z dirn')


%% %window estimates
figure; clf
subplot(2,1,1);

plot(x(2:end),eventTypes(:,2:end))
legend('type');
subplot(2,1,2);
hold on
plot(x(2:end),thrustWindowStart(2:end),'+')
plot(x(2:end),thrustWindowEnd(2:end),'x')
legend('start estimate','end estimate');


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