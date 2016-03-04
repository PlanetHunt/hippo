x = 1:1:length(eventTypes);

figure; clf; hold on;
subplot(3,1,1)
plot(x,eventTypes,'x');
legend('event type')
subplot(3,1,2)
plot(x,addEventToOrekitDateTimeDetector);
legend('+dateTimeDetector')
subplot(3,1,3)
hold on
plot(x,thrustWindowStart,'+')
plot(x,thrustWindowEnd,'x')
legend('win start','win stop')
figure
plot(x,thrustDirection(:,1:end))
legend('x thrust dirn','y thrust dirn','z thrust dirn')

%eventTypes(end+1), addEventToOrekitDateTimeDetector(end+1), thrustDirection(:,end+1), thrustWindowStart(end+1), thrustWindowEnd(end+1)
