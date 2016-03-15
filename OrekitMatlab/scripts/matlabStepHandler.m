% function [ addEventToOrekitDateTimeDetectorFlag, eventThrustDirection, eventThrustWindowStart, eventThrustWindowEnd ] = matlabStepHandler( orbital_elements, position, velocity, acceleration, timestamp, current_mass, last_step_flag )
function [ returnMatrix ] = matlabStepHandler( orbital_elements, timestamp, current_mass, last_step_flag )
global timerVal;
%MATLABSTEPHANDLER function to be called at every time step
%  1 event_A     perigee
%  2 event_B     apogee
%  3 event_C     theta = theta_critical
%

%   orbital_elements = [a;e;i;omega;raan;tru Anom; mean anom]
global timeVector;
global mass;
global maxStep ;
global thrustDurationLimit; %ie the limit so that it is still an 'impulse'
global numThrusters;
global oed oec oedm oecm oeError oeErrorController;%orbital elements of deputy and chief arrays (also mean eles)
global inAZone inBZone inCZone inDZone  thrustVector;
global dVA dVB dVC dVD;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
global AThrustVector BThrustVector CThrustVector DThrustVector;
global Isp thrust;
global netThrustVector
global oecmMatchedTime chiefTimeVectorNum;
global eventTypes addEventToOrekitDateTimeDetector thrustDirection thrustWindowStart thrustWindowEnd;
global typeOfSimulation;
global oe oem;
global tolerances;
%global apsideCounter;
global eventCounter;
current_time = datetime(timestamp);
timeVector=[timeVector;current_time];
mass = [mass; current_mass];

    meanOE = convertOscOeToMeanOe( orbital_elements ); %orbital_elements';%
    oem = [oem, meanOE];
    oscOE = orbital_elements';
    oe = [oe, oscOE];

switch typeOfSimulation
    case 'propogateChief' %use this to gather the chief OE time history
       %oeError(:,end+1) = [0;0;0;0;0;0;0];
       oecm = oem; %the only task is to save the chief Mean OEs, nothing else.
    case 'propogateDeputy'
        oed= oe;
       oedm = oem;%the only task is to save the deputy Mean OEs, nothing else.
       stepIndex = size(oedm,2);
        oecmMatchedTime(stepIndex,:) = interp1(chiefTimeVectorNum,oecm',datenum(current_time),'pchip');
        [oeErrorController(:,end+1),oeError(:,end+1)] = calcOeError(oedm(:,end),oecmMatchedTime(stepIndex,:)');
    case 'propogateDeputyStationKeep'
        
        %copy over last chief OEs
        oec(:,end+1) = oec(:,end);
        oecm(:,end+1) = oecm(:,end); %we are using a fixed chief orbit to formate on so it wont change time step to time step
        %calculate the tracking error in orbital elements, 
        %check this - should it be position of chief rel to deputy or pos deputy rel to chief?
        
        oed = oe;
        oedm = oem;
        oeError(:,end+1) = calcOeError(oedm(:,end),oecm(:,end));
%         oeError(6,end) = 0;
%         oeError(7,end) = 0; %dont correct mean anomaly errors
    case 'propogateDeputyFormationFlight' %run this option after you have done a full propogateChief
        oed = oe;
        oedm = oem;
        stepIndex = size(oedm,2);
        oecmMatchedTime(stepIndex,:) = interp1(chiefTimeVectorNum,oecm',datenum(current_time),'pchip');
        [oeErrorController(:,end+1),oeError(:,end+1)] = calcOeError(oedm(:,end),oecmMatchedTime(stepIndex,:)');

end
impulsive=0;

addEventToOrekitDateTimeDetectorFlag = 0;
%default values if there is no event (the calues dont matter since
%addEventToOrekitDateTimeDetectorFlag = 0 so orekit wont process the values
eventThrustDirection = [0; 0; 0]';
eventThrustWindowStart = [0 0 0 0 0 0];
eventThrustWindowEnd = [0 0 0 0 0 0];
returnMatrix = [addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];
%% debug theta critical
% global theta_critical_check;
% i = oedm(3,end);
% d_i = oeError(3,end);
% d_raan = oeError(5,end);
% theta_critical_check(end+1) = atan2(d_raan*sin(i),d_i);
% if(theta_crit<0)
%     theta_crit = pi+theta_crit
% end
% 
% theta = wrapTo2Pi(oedm(4,end)+oedm(6,end));
% X = sprintf('t= %d tc= %d tan = %d',theta, theta_crit,true_anomalyNode');
% disp(X)

%%

if(strcmp(typeOfSimulation,'propogateDeputyStationKeep') || strcmp(typeOfSimulation,'propogateDeputyFormationFlight')) %doesnt make sense to calculate errors if we are only propogating the chief
inAZone(end+1) = inAZone(end);%copy last values
inBZone(end+1) = inBZone(end);
inCZone(end+1) = inCZone(end);
inDZone(end+1) = inDZone(end);
preWindowDetectionRange = 3*maxStep;



% check if we are in a thrusting period
%% check A window 
if(isbetween(current_time,tABoostStartCommand(end)-seconds(preWindowDetectionRange),tABoostEndCommand(end)+seconds(60*60))) 
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVA(:,end+1) = dVA(:,end); %=last value
    %dont change the window or the thrust while we are in the window
    tABoostStartCommand(end+1) = tABoostStartCommand(end);%append the value from last time step
    tABoostEndCommand(end+1) = tABoostEndCommand(end);
    AThrustVector(:,end+1) = dVA(:,end);%if fire commanded, compy the dV vector into the Thrust Vector
     
    if(inAZone(end) == 0) %if this is the first time we have fallen innto this window
        inAZone(end) = 1; %toggle the fireA sticky flag
       
        %send A to scheduler for parsing
        %scheduler decides if this event should be the next one sent to
        %orekit
        
        %historical data for plotting
        if(eventCounter~=2) %simple method to alternate events like this: 2 events (A or B) then 1 event (C)
            eventCounter = eventCounter+1;
            addEventToOrekitDateTimeDetector(end+1) = 1;
        else
            addEventToOrekitDateTimeDetector(end+1) = 0;
        end
%         addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(1, tABoostStartCommand(end), tABoostEndCommand(end),impulsive);
        eventTypes(end+1) = 1;
        thrustDirection(:,end+1) = dVA(:,end);
        thrustWindowStart(end+1) = tABoostStartCommand(end);
        thrustWindowEnd(end+1) = tABoostEndCommand(end);
            
        if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
            %set the variables to return to orekit
            addEventToOrekitDateTimeDetectorFlag = 1;

            eventThrustDirection = thrustDirection(:,end)';

            if(norm(eventThrustDirection) ~= 0)%avoid dividing by zero
                eventThrustDirection= eventThrustDirection/norm(eventThrustDirection); %normalized to simply get a unit vector in the thrust direction
            end
 
            eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
            eventThrustWindowEnd = datevec(thrustWindowEnd(end));
            returnMatrix = [returnMatrix, addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];
        end
    end
        
else
    inAZone(end) = 0; %sticky flag always off outside the trap window
    %update estimates
    [dVA(:,end+1), tABoostStartCommand(end+1), tABoostEndCommand(end+1)] = updateThrustTimes( 1, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeErrorController(:,end), oedm(:,end),numThrusters);
    AThrustVector(:,end+1) = [0;0;0];
end

%% check B Zone 
if(isbetween(current_time,tBBoostStartCommand(end)-seconds(preWindowDetectionRange),tBBoostEndCommand(end)+seconds(60*60)))

    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVB(:,end+1) = dVB(:,end); %=last value
    tBBoostStartCommand(end+1) = tBBoostStartCommand(end);
    tBBoostEndCommand(end+1) = tBBoostEndCommand(end);
    BThrustVector(:,end+1) = dVB(:,end);


    if(inBZone(end) == 0) %if this is the first time we have fallen innto this window
        inBZone(end) = 1; %toggle the fireB sticky flag
      
        if(eventCounter~=2)
            eventCounter = eventCounter+1;
            addEventToOrekitDateTimeDetector(end+1) = 1;
        else
            addEventToOrekitDateTimeDetector(end+1) = 0;
        end
        %addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(2, tBBoostStartCommand(end), tBBoostEndCommand(end),impulsive); 
        eventTypes(end+1) = 2;
        thrustDirection(:,end+1) = dVB(:,end);
        thrustWindowStart(end+1) = tBBoostStartCommand(end);
        thrustWindowEnd(end+1) = tBBoostEndCommand(end);
        
        if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
            addEventToOrekitDateTimeDetectorFlag = 1;
            eventThrustDirection = thrustDirection(:,end)';

            %%normalize thrust direction
            if(norm(eventThrustDirection) ~= 0)%avoid dividing by zero
                eventThrustDirection= eventThrustDirection/norm(eventThrustDirection); %normalized to simply get a unit vector in the thrust direction
            end             
            eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
            eventThrustWindowEnd = datevec(thrustWindowEnd(end));
           
            returnMatrix = [returnMatrix, addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];

        end
    end

else
    inBZone(end) = 0; %sticky flag always off outside the trap window
    %update estimates
    [dVB(:,end+1), tBBoostStartCommand(end+1), tBBoostEndCommand(end+1)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeErrorController(:,end), oedm(:,end),numThrusters);
    BThrustVector(:,end+1) = [0;0;0];
end
    
%% check C window
if(isbetween(current_time,tCBoostStartCommand(end)-seconds(preWindowDetectionRange),tCBoostEndCommand(end)+seconds(60*60)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVC(:,end+1) = dVC(:,end); %=last value
    tCBoostStartCommand(end+1) = tCBoostStartCommand(end);
    tCBoostEndCommand(end+1) = tCBoostEndCommand(end);
    CThrustVector(:,end+1) = dVC(:,end);
    if(abs(oeErrorController(3,end))>tolerances(3) || abs(oeErrorController(5,end))>tolerances(5)) %only register an event if there is still room to correct. If we try to register an event window in orekit that is too short, it breaks because it cant find it. (like this "Exception in thread "main" org.orekit.errors.PropagationException: function values at endpoints do not have different signs, endpoints: [24,438.526, 24,440.526], values: [-55.306, -57.306]"
        if(inCZone(end) == 0) %if this is the first time we have fallen innto this window
            inCZone(end) = 1; %toggle the fireC sticky flag
            
            if(eventCounter == 2)
                eventCounter = 0;
                addEventToOrekitDateTimeDetector(end+1) = 1;
            else
                addEventToOrekitDateTimeDetector(end+1) = 0;
            end
            %addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(3, tCBoostStartCommand(end), tCBoostEndCommand(end),impulsive); 
            eventTypes(end+1) = 3;
            thrustDirection(:,end+1) = dVC(:,end);
            thrustWindowStart(end+1) = tCBoostStartCommand(end);
            thrustWindowEnd(end+1) = tCBoostEndCommand(end);

            if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
                addEventToOrekitDateTimeDetectorFlag = 1;
                eventThrustDirection = thrustDirection(:,end)';
                if(norm(eventThrustDirection) ~= 0)%avoid dividing by zero
                    eventThrustDirection= eventThrustDirection/norm(eventThrustDirection); %normalized to simply get a unit vector in the thrust direction
                end
                %eventThrustDirection=[0 1 0]
                eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
                eventThrustWindowEnd = datevec(thrustWindowEnd(end));
                returnMatrix = [returnMatrix, addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];

            end
        end
    end
else
    %if we are close to perigee or apogee, there is a small region where
    %the error caluclated is way off, due to a lack of points in the
    %interpolation. (see big spikes in error graphs near apogee and
    %perigee) so in these areas, the error is unreliable
    if(~isbetween(current_time,tABoostStartCommand(end)-seconds(preWindowDetectionRange),tABoostEndCommand(end)+seconds(preWindowDetectionRange)))
        if(~isbetween(current_time,tBBoostStartCommand(end)-seconds(preWindowDetectionRange),tBBoostEndCommand(end)+seconds(preWindowDetectionRange)))
            inCZone(end) = 0;
            %update estimates
            [dVC(:,end+1), tCBoostStartCommand(end+1), tCBoostEndCommand(end+1)] = updateThrustTimes( 3, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeErrorController(:,end), oedm(:,end),numThrusters);
            CThrustVector(:,end+1) = [0;0;0];
        end
    end
    
    
end





if(last_step_flag == 1)
    elapsedTime = toc(timerVal)
    oecmMatchedTime = oecmMatchedTime';
    plotBasicOe;
    %plotMeanOeErrors;
    %plotCommandsToOrekit;
    %plotDebug;
    %plotThrustSchedulerOutput;
    %plotControllerOutput;
    %plotVelocities;
    %plotBasicOeChiefPropogated;
    %plotMeanOeErrors;
    plotOEerrors2;
end
end

end