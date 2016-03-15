function [ dV, tBoostStartCommand, tBoostEndCommand ] = updateThrustTimes( eventID, time, currentMass, Isp, thrust, burnTimeLimit, orbitElementsError, deputyMeanOE,numThrusters)
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




global mu g;
global stepSize;
switch eventID
    case 1 %A
        dV = FTCCalcPerigeeDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = 0;
    case 2 %B
        dV = FTCCalcApogeeDv(deputyMeanOE, orbitElementsError);
        true_anomalyNode = pi;
    case 3
        dV = FTCCalcLatArg180Dv(deputyMeanOE, orbitElementsError);
        thetaCritical = calcCriticalLatArg(deputyMeanOE,orbitElementsError);
        true_anomalyNode = wrapTo2Pi(thetaCritical-omegam);
%     case 3 %C
%         dV = FTCCalcLatArgZeroDv(deputyMeanOE, orbitElementsError);
%         true_anomalyNode = wrapTo2Pi(0-omegam);
%     case 4 %D
%         dV = FTCCalcLatArgNinetyDv(deputyMeanOE, orbitElementsError);
%         true_anomalyNode = wrapTo2Pi(pi/2-omegam);
end
    %calc Burn duration for this node (s)
    burnDuration = calcBurnTime(dV, currentMass, Isp, thrust, burnTimeLimit, numThrusters); % burn duration
    if(burnDuration == burnTimeLimit) %we have to compute the dV actually achieved
        %compute dV actually achieved in burnTimeLimit sec
        mDot = numThrusters*thrust/(Isp*g); %mass flow rate
        % mp in 180 sec (mp = burnDuration*mDot) 
        mp = burnDuration*mDot;%mass of propellant used
        MR = currentMass/(currentMass-mp);%mass ratio
        dVMag = Isp*g*log(MR); %calc actual dV 
        dV = dVMag.*(dV./norm(dV));
    end
    %time elapsed from perigee reference point until now 
    durationPeriToNow = solvet(am, em, true_anomalym); % a time duration in seconds
    %time elapsed from perigee ref point until node point
    durationPeriToNode = solvet(am, em, true_anomalyNode); % a time duration in seconds
    %duration of time elapsed from perigee ref point until BoostStart
    durationPeriToBoostStart = durationPeriToNode - burnDuration/2; % a time duration in seconds
    if(durationPeriToBoostStart < 0) %wrap around for the case we are detecting perigee
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
    %boost during the correct time interval. ---not anymore since we use a
    %orekit datetime detector
    tBoostStartCommand = tBoostStart;% - seconds(stepSize); %a matlab datetime object
    %the turn thruster off command
    tBoostEndCommand = tBoostStartCommand + seconds(burnDuration); %a matlab datetime object
end

