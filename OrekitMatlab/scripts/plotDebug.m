figure; clf;
hold on
global allWindowTypes
x = 1:1:length(allWindowTypes)
plot(x,allWindowTypes,'--x');
title('all window types sorted');
legend('1st soonest event','2nd soonest event','3rd soonest event','4th soonest event')