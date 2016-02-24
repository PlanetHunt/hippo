function [ dV, tBoostStartCommand, tBoostEndCommand ] = updateThrustTimes( eventID, time, currentMass, Isp, thrust, burnTimeLimit, orbitElementsError, deputyMeanOE)
%UPDATETHRUSTTIMES calculate the time of thrusting required at each node
%   This function calculates the thrust required (delta V) at a node (A
%   B C or D) and calculated the time when the thruster should be on and
%   when it should turn off to achieve this. If the duration of thrusting
%   would violate the impulse maneuver burnTimeLimit then the duration is
%   set to the burnTimeLimit.
%   eventID = 1 is event A
%   eventID = 2 is event B
%   eventID = 3 is event C
%   eventID = 4 is event D
am = deputyMeanOE(1);
em = deputyMeanOE(2);
omegam = deputyMeanOE(4);
true_anomalym = deputyMeanOE(6);
global mu;
global step_size;
switch eventID
    case 1 %A
        dV = FTCCalcPerigeeDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = 0;
    case 2 %B
        dV = FTCCalcApogeeDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = pi;
    case 3 %C
        dV = FTCCalcLatArgZeroDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = wrapTo2Pi(0-omegam);
    case 4 %D
        dV = FTCCalcLatArgNinetyDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = wrapTo2Pi(pi/2-omegam);
end
    %calc Burn duration for this node (s)
    burnDuration = calcBurnTime(dV, currentMass, Isp, thrust, burnTimeLimit ); % burn duration
    %time elapsed from perigee reference point until now 
    durationPeriToNow = solvet(am, em, true_anomalym); % a time duration in seconds
    %time elapsed from perigee ref point until node point
    durationPeriToNode = solvet(am, em, true_anomalyNode); % a time duration in seconds
    %duration of time elapsed from perigee ref point until BoostStart
    durationPeriToBoostStart = durationPeriToNode - burnDuration/2; % a time duration in seconds
    if(durationPeriToBoostStart < 0) %wrap around
        T = 2*pi*sqrt(am^3/mu);%period
        durationPeriToBoostStart = T + durationPeriToBoostStart;
    end
    %time remaining from now until boost  start (call this durationCountDownBoost)
    durationCountDownBoost = durationPeriToBoostStart - durationPeriToNow; % a time duration in seconds
    if(durationCountDownBoost<0) %wrap around the long way
        T = 2*pi*sqrt(am^3/mu);%period
        durationCountDownBoost = T + durationCountDownBoost;
    end
    %absolute time of boost  start (dd mm yyyy hh mm ss)
    tBoostStart = time + seconds(durationCountDownBoost); %a matlab datetime object
    %the signal needs to be generated one step ahead, so orekit applies the
    %boost during the correct time interval.
    tBoostStartCommand = tBoostStart - seconds(step_size); %a matlab datetime object
    %the turn thruster off command
    tBoostEndCommand = tBoostStartCommand + seconds(burnDuration); %a matlab datetime object
end
