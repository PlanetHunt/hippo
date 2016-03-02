function [ nextWindowType, nextWindowStart, nextWindowEnd, nextWindowThrustDirection ] = identifyNextEvent( current_time, current_mass, Isp, thrust, thrustDurationLimit, currentError, currentDeputyOE, numThrusters )
%IDENTIFYNEXTEVENT Summary of this function goes here
%   Detailed explanation goes here
global allWindowTypes
[thrustDirection(:,1), startTimes(1), endTimes(1)] = updateThrustTimes( 1, current_time, current_mass, Isp, thrust, thrustDurationLimit, currentError, currentDeputyOE, numThrusters);
[thrustDirection(:,2), startTimes(2), endTimes(2)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, currentError, currentDeputyOE, numThrusters);
[thrustDirection(:,3), startTimes(3), endTimes(3)] = updateThrustTimes( 3, current_time, current_mass, Isp, thrust, thrustDurationLimit, currentError, currentDeputyOE, numThrusters);
[thrustDirection(:,4), startTimes(4), endTimes(4)] = updateThrustTimes( 4, current_time, current_mass, Isp, thrust, thrustDurationLimit, currentError, currentDeputyOE, numThrusters);

%sort from earliest to latest I is the index of the original vector
[sortedStartTimes,I] = sort(startTimes);

nextWindowType = I(1);
nextWindowStart = sortedStartTimes(1);
nextWindowEnd = endTimes(I(1));
nextWindowThrustDirection = thrustDirection(I(1));

figure(2)
hold on
plot(startTimes(1),1,'b*'); plot(endTimes(1),1,'bx');
plot(startTimes(2),2,'k*'); plot(endTimes(2),2,'kx');
plot(startTimes(3),3,'g*'); plot(endTimes(3),3,'gx');
plot(startTimes(4),4,'c*'); plot(endTimes(4),4,'cx');
allWindowTypes(:,end+1) = I';
end

