function [ addEventToOrekitDateTimeDetectorFlag, eventThrustDirection, eventThrustWindowStart, eventThrustWindowEnd ] = matlabStepHandler( orbital_elements, position, velocity, acceleration, timestamp, current_mass, last_step_flag )
%MATLABSTEPHANDLER function to be called at every time step
%  1 event_A     perigee
%  2 event_B     apogee
%  3 event_C     theta = 0
%  4 event_D     theta = 90 deg
%   currentThrustDirecton = a unit vector in thrust direction
%   thrustFlag = fire thruster command 1 or 0
%   orbital_elements = [a;e;i;omega;raan;tru Anom; mean anom]
global timeVector;
global mass;
global stepSize maxStep maxCheck;
global duration req j2 g thrustDurationLimit; %ie the limit so that it is still an 'impulse'
global numThrusters;
global oed oec oedm oecm oeError;%orbital elements of deputy and chief arrays (also mean eles)
global inAZone inBZone inCZone inDZone fireThruster thrustVector;
global dVA dVB dVC dVD;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
global AThrustVector BThrustVector CThrustVector DThrustVector;
global Isp thrust;
global pos vel acc;
global netThrustVector

global eventTypes addEventToOrekitDateTimeDetector thrustDirection thrustWindowStart thrustWindowEnd;


current_time = datetime(timestamp);
timeVector=[timeVector;current_time];
mass = [mass; current_mass];
pos = [pos, position' ];
vel = [vel, velocity' ];
acc = [acc, acceleration'];
oec(:,end+1) = oec(:,end);
oecm(:,end+1) = oecm(:,end); %we are using a fixed chief orbit to formate on so it wont change time step to time step
oed = [oed, orbital_elements'];

%convert oscilating orbital elements to mean orbital elements
meanOE = convertOscOeToMeanOe( orbital_elements );
oedm = [oedm, meanOE];

%calculate the tracking error in orbital elements, 
%check this - should it be position of chief rel to deputy or pos deputy rel to chief?
oeError(:,end+1) = oedm(:,end)-oecm(:,end);
oeError(4,end) = 0; %dont correct omega
oeError(6,end) = 0; %dont correct TA
oeError(7,end) = 0; %dont correct MA
% % global nextWindowStart nextWindowEnd nextWindowThrustDirection nextWindowType addToThrustCommandQue flagSentForNextWindow;
% % 
% % %% before event update & flag raise
% % if(isbetween(current_time,nextWindowStart(end)-seconds(stepSize*5),nextWindowStart(end)-seconds(stepSize*4)) && flagSentForNextWindow(end) ~= 1) 
% %     [ nextWindowType(end+1), nextWindowStart(end+1), nextWindowEnd(end+1), nextWindowThrustDirection(:,(end+1)) ] = identifyNextEvent( current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end), numThrusters );
% %     addToThrustCommandQue(end+1) = 1;
% %     flagSentForNextWindow(end+1) = 1;
% % %% after event update
% % elseif(isbetween(current_time,nextWindowEnd(end)+seconds(stepSize*2),nextWindowEnd(end)+seconds(stepSize*3))) 
% %     [ nextWindowType(end+1), nextWindowStart(end+1), nextWindowEnd(end+1), nextWindowThrustDirection(:,end+1) ] = identifyNextEvent( current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end), numThrusters );
% %     addToThrustCommandQue(end+1) = 0;
% %     flagSentForNextWindow(end+1) = 0;
% % else
% %     addToThrustCommandQue(end+1) = 0;
% %     nextWindowStart(end+1) = nextWindowStart(end);
% %     nextWindowEnd(end+1) = nextWindowEnd(end);
% %     nextWindowThrustDirection(:,end+1) = nextWindowThrustDirection(:,end);
% %     nextWindowType(end+1) = nextWindowType(end);
% %     flagSentForNextWindow(end+1) = flagSentForNextWindow(end);
% % end


addEventToOrekitDateTimeDetectorFlag = 0;
inAZone(end+1) = inAZone(end);%copy last values
inBZone(end+1) = inBZone(end);
inCZone(end+1) = inCZone(end);
inDZone(end+1) = inDZone(end);
% check if we are in a thrusting period
%% check A window 
if(isbetween(current_time,tABoostStartCommand(end)-seconds(3*maxCheck),tABoostEndCommand(end))) 
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVA(:,end+1) = dVA(:,end); %=last value
    %dont change the window or the thrust while we are in the window
    tABoostStartCommand(end+1) = tABoostStartCommand(end);%append the value from last time step
    tABoostEndCommand(end+1) = tABoostEndCommand(end);
    AThrustVector(:,end+1) = dVA(:,end);%if fire commanded, compy the dV vector into the Thrust Vector
    
    if(inAZone(end) == 0) %if this is the first time we have fallen innto this window
        inAZone(end) = 1; %toggle the fireB sticky flag
        
        %send A to scheduler for parsing
        %scheduler decides if this event should be the next one sent to
        %orekit
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(1, tABoostStartCommand(end), tABoostEndCommand(end));
        addEventToOrekitDateTimeDetectorFlag = addEventToOrekitDateTimeDetector(end);
        eventTypes(end+1) = 1;
        thrustDirection(:,end+1) = dVA(:,end);
        thrustWindowStart(end+1) = tABoostStartCommand(end);
        thrustWindowEnd(end+1) = tABoostEndCommand(end);
    end
        
else
    inAZone(end) = 0; %sticky flag always off outside the trap window
    %update estimates
    [dVA(:,end+1), tABoostStartCommand(end+1), tABoostEndCommand(end+1)] = updateThrustTimes( 1, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    AThrustVector(:,end+1) = [0;0;0];
end

%% check B Zone 
if(isbetween(current_time,tBBoostStartCommand(end)-seconds(3*maxCheck),tBBoostEndCommand(end)))

    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVB(:,end+1) = dVB(:,end); %=last value
    tBBoostStartCommand(end+1) = tBBoostStartCommand(end);
    tBBoostEndCommand(end+1) = tBBoostEndCommand(end);
    BThrustVector(:,end+1) = dVB(:,end);


    if(inBZone(end) == 0) %if this is the first time we have fallen innto this window
        inBZone(end) = 1; %toggle the fireB sticky flag
        
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(2, tBBoostStartCommand(end), tBBoostEndCommand(end)); 
        addEventToOrekitDateTimeDetectorFlag = addEventToOrekitDateTimeDetector(end);
        eventTypes(end+1) = 2;
        thrustDirection(:,end+1) = dVB(:,end);
        thrustWindowStart(end+1) = tBBoostStartCommand(end);
        thrustWindowEnd(end+1) = tBBoostEndCommand(end);
    end

else
    inBZone(end) = 0; %sticky flag always off outside the trap window
    %update estimates
    [dVB(:,end+1), tBBoostStartCommand(end+1), tBBoostEndCommand(end+1)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    BThrustVector(:,end+1) = [0;0;0];
end
    
%% check C window
if(isbetween(current_time,tCBoostStartCommand(end)-seconds(3*maxCheck),tCBoostEndCommand(end)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVC(:,end+1) = dVC(:,end); %=last value
    tCBoostStartCommand(end+1) = tCBoostStartCommand(end);
    tCBoostEndCommand(end+1) = tCBoostEndCommand(end);
    CThrustVector(:,end+1) = dVC(:,end);
    
    if(inCZone(end) == 0) %if this is the first time we have fallen innto this window
        inCZone(end) = 1; %toggle the fireC sticky flag
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(3, tCBoostStartCommand(end), tCBoostEndCommand(end)); 
        addEventToOrekitDateTimeDetectorFlag = addEventToOrekitDateTimeDetector(end);
        eventTypes(end+1) = 1;
        thrustDirection(:,end+1) = dVC(:,end);
        thrustWindowStart(end+1) = tCBoostStartCommand(end);
        thrustWindowEnd(end+1) = tCBoostEndCommand(end);
    end
    
else
    inCZone(end) = 0;
    %update estimates
    [dVC(:,end+1), tCBoostStartCommand(end+1), tCBoostEndCommand(end+1)] = updateThrustTimes( 3, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    CThrustVector(:,end+1) = [0;0;0];
end

%% check D window ( we check here cause we shouldnt be in C window provided that our boost duration is only a few mins and we are not highly elliptical)
if(isbetween(current_time,tDBoostStartCommand(end)-seconds(3*maxCheck),tDBoostEndCommand(end)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVD(:,end+1) = dVD(:,end); %=last value
    tDBoostStartCommand(end+1) = tDBoostStartCommand(end);
    tDBoostEndCommand(end+1) = tDBoostEndCommand(end);
    DThrustVector(:,end+1) = dVD(:,end);
    
    if(inDZone(end) == 0) %if this is the first time we have fallen innto this window
        inDZone(end) = 1; %toggle the fireC sticky flag
        
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(4, tDBoostStartCommand(end), tDBoostEndCommand(end));
        addEventToOrekitDateTimeDetectorFlag = addEventToOrekitDateTimeDetector(end);
        eventTypes(end+1) = 4;
        thrustDirection(:,end+1) = dVD(:,end);
        thrustWindowStart(end+1) = tDBoostStartCommand(end);
        thrustWindowEnd(end+1) = tDBoostEndCommand(end);
    end
else
    inDZone(end) = 0;
    %update estimates
    [dVD(:,end+1), tDBoostStartCommand(end+1), tDBoostEndCommand(end+1)] = updateThrustTimes( 4, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    DThrustVector(:,end+1) = [0;0;0];
end
    
 
%net/overall fire the thruster flag - should we fire the thruster?
% fireThruster(end+1) = any([fireA(end),fireB(end),fireC(end),fireD(end)]); %return 1 if any of A B C D = 1

%net/overall thrust vector - what thrust should we apply ? (delta V)
thrustVector(:,end+1) = AThrustVector(:,end)+BThrustVector(:,end)+CThrustVector(:,end)+DThrustVector(:,end); %net delta V
netThrustVector(end+1) = sqrt(sum(abs(thrustVector(:,end)).^2,1));
% if(norm(thrustVector(:,end)) ~= 0)%avoid dividing by zero
%     thrustVector(:,end) = thrustVector(:,end)/norm(thrustVector(:,end)); %normalized to simply get a unit vector in the thrust direction
% end


%currentThrustDirection = (LVLH2ECICharles(pos(:,end), vel(:,end)))*[0.00;0.1;0.00];
currentThrustDirection = [0; -1; 0]; %hard coded for testing purposes
% % % % global latitudeArgument
% % % % figure(1)
% % % % latitudeArgument(end+1) = wrapTo2Pi(oedm(4,end)+oedm(6,end));
% % % % plot(timeVector(end),oedm(6,end),'xb');
% % % % hold on
% % % % %plot(timeVector(2:end),oecm(6,2:end));
% % % % plot(timeVector(end),latitudeArgument(end),'+k');
% plot(timeVector(2:end), zeros(length(timeVector(2:end))))
% title('True Anomaly and Theta');
% ylabel('True Anomaly (radians)')
% legend('TA deputy','LA deputy')
%if in last step plot everything

%make copies to return from the matlabstephandler function
%addEventToOrekitDateTimeDetectorFlag = addEventToOrekitDateTimeDetector(end);
eventThrustDirection = thrustDirection(:,end);
eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
eventThrustWindowEnd = datevec(thrustWindowEnd(end));


if(last_step_flag == 1)
    plotBasicOe;
    %plotCommandsToOrekit;
    %plotDebug;
    plotThrustSchedulerOutput;
    %plotControllerOutput;
    %plotVelocities;
end
  
end