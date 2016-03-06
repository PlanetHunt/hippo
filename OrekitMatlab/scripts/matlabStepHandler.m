% function [ addEventToOrekitDateTimeDetectorFlag, eventThrustDirection, eventThrustWindowStart, eventThrustWindowEnd ] = matlabStepHandler( orbital_elements, position, velocity, acceleration, timestamp, current_mass, last_step_flag )
function [ returnMatrix ] = matlabStepHandler( orbital_elements, timestamp, current_mass, last_step_flag )
global timerVal;
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
global pos vel;
global netThrustVector
global oecmMatchedTime chiefTimeVectorNum;
global eventTypes addEventToOrekitDateTimeDetector thrustDirection thrustWindowStart thrustWindowEnd;
global typeOfSimulation;
global oe oem;
global apsideCounter;
global tolerances;
current_time = datetime(timestamp);
timeVector=[timeVector;current_time];
mass = [mass; current_mass];

%convert oscilating orbital elements to mean orbital elements
% if(size(timeVector,1)==2) %this is the time zero timestep
%     %at time zero we just get back to mean OE from orkit
%     oscOE = convertMeanOeToOscOe(orbital_elements);
%     %oe = [oe, orbital_elements'];
%     oe = [oe, oscOE];
%     
%     meanOE = orbital_elements'; %
%     oem = [oem, meanOE];
% else
    meanOE = convertOscOeToMeanOe( orbital_elements ); %orbital_elements';%
    oem = [oem, meanOE];
    oscOE = orbital_elements';
    oe = [oe, oscOE];
%  end
%%
switch typeOfSimulation
    case 'propogateChief' %use this to gather the chief OE time history
       %oeError(:,end+1) = [0;0;0;0;0;0;0];
       oecm = oem; %the only task is to save the chief Mean OEs, nothing else.
    case 'propogateDeputy'
       oedm = oem;%the only task is to save the deputy Mean OEs, nothing else.
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
        oeError(:,end+1) = calcOeError(oedm(:,end),oecmMatchedTime(stepIndex,:)');
% chiefTimeVectorNum = datenum(timeVector); global chiefTimeVectorNum         
%         oeError(3,end) = 0; %dont correct i errors
%         oeError(4,end) = 0; %AoP error = 0
%         oeError(5,end) = 0; %RAAN error = 0
%         oeError(6,end) = 0;
%         oeError(7,end) = 0; %dont correct mean anomaly errors
end
impulsive=1;

addEventToOrekitDateTimeDetectorFlag = 0;
%default values if there is no event (the calues dont matter since
%addEventToOrekitDateTimeDetectorFlag = 0 so orekit wont process the values
eventThrustDirection = [0; 0; 0]';
eventThrustWindowStart = [0 0 0 0 0 0];
eventThrustWindowEnd = [0 0 0 0 0 0];
returnMatrix = [addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];



if(strcmp(typeOfSimulation,'propogateDeputyStationKeep') || strcmp(typeOfSimulation,'propogateDeputyFormationFlight')) %doesnt make sense to calculate errors if we are only propogating the chief
inAZone(end+1) = inAZone(end);%copy last values
inBZone(end+1) = inBZone(end);
inCZone(end+1) = inCZone(end);
inDZone(end+1) = inDZone(end);
preWindowDetectionRange = 3*maxStep;

%theta=oedm(4,end)+oedm(6,end)

% check if we are in a thrusting period
%% check A window 
if(isbetween(current_time,tABoostStartCommand(end)-seconds(preWindowDetectionRange),tABoostEndCommand(end)+seconds(50*60))) 
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
        
        %historical data for plotting
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(1, tABoostStartCommand(end), tABoostEndCommand(end),impulsive);
        eventTypes(end+1) = 1;
        thrustDirection(:,end+1) = dVA(:,end);
        thrustWindowStart(end+1) = tABoostStartCommand(end);
        thrustWindowEnd(end+1) = tABoostEndCommand(end);
            
        if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
            %set the variables to return to orekit
            addEventToOrekitDateTimeDetectorFlag = 1;

            eventThrustDirection = thrustDirection(:,end)';

              %% code to alternate X and Y thrusts
%             if(apsideCounter == 0 || apsideCounter == 1)
%                 %only thrust Y thrust
%                 eventThrustDirection(1) = 0;
%             elseif(apsideCounter == 2 || apsideCounter == 3)
%                 %only thrust in X directon
%                 eventThrustDirection(2) = 0;
%             else
%                 apsideCounter=0; %reset counter
%                 eventThrustDirection(1) = 0;
%             end
%              apsideCounter=apsideCounter+1;
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
    [dVA(:,end+1), tABoostStartCommand(end+1), tABoostEndCommand(end+1)] = updateThrustTimes( 1, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    AThrustVector(:,end+1) = [0;0;0];
end

%% check B Zone 
if(isbetween(current_time,tBBoostStartCommand(end)-seconds(preWindowDetectionRange),tBBoostEndCommand(end)+seconds(50*60)))

    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVB(:,end+1) = dVB(:,end); %=last value
    tBBoostStartCommand(end+1) = tBBoostStartCommand(end);
    tBBoostEndCommand(end+1) = tBBoostEndCommand(end);
    BThrustVector(:,end+1) = dVB(:,end);


    if(inBZone(end) == 0) %if this is the first time we have fallen innto this window
        inBZone(end) = 1; %toggle the fireB sticky flag
      
        
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(2, tBBoostStartCommand(end), tBBoostEndCommand(end),impulsive); 
        eventTypes(end+1) = 2;
        thrustDirection(:,end+1) = dVB(:,end);
        thrustWindowStart(end+1) = tBBoostStartCommand(end);
        thrustWindowEnd(end+1) = tBBoostEndCommand(end);
        
        if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
            addEventToOrekitDateTimeDetectorFlag = 1;
            eventThrustDirection = thrustDirection(:,end)';
            

            
            %%this code alternates x and y thrustings
%             if(apsideCounter == 0 || apsideCounter == 1)
%                 %only thrust Y thrust
%                 eventThrustDirection(1) = 0;
%             elseif(apsideCounter == 2 || apsideCounter == 3)
%                 %only thrust in X directon
%                 eventThrustDirection(2) = 0;
%             else
%                 apsideCounter=0; %reset counter
%                 eventThrustDirection(1) = 0;
%             end
%             apsideCounter=apsideCounter+1;
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
    [dVB(:,end+1), tBBoostStartCommand(end+1), tBBoostEndCommand(end+1)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    BThrustVector(:,end+1) = [0;0;0];
end
    
%% check C window
if(isbetween(current_time,tCBoostStartCommand(end)-seconds(preWindowDetectionRange),tCBoostEndCommand(end)+seconds(50*60)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVC(:,end+1) = dVC(:,end); %=last value
    tCBoostStartCommand(end+1) = tCBoostStartCommand(end);
    tCBoostEndCommand(end+1) = tCBoostEndCommand(end);
    CThrustVector(:,end+1) = dVC(:,end);
    
    if(inCZone(end) == 0) %if this is the first time we have fallen innto this window
        inCZone(end) = 1; %toggle the fireC sticky flag
        
        addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(3, tCBoostStartCommand(end), tCBoostEndCommand(end),impulsive); 
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
    
else
    inCZone(end) = 0;
    %update estimates
    [dVC(:,end+1), tCBoostStartCommand(end+1), tCBoostEndCommand(end+1)] = updateThrustTimes( 3, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    CThrustVector(:,end+1) = [0;0;0];
end



        
    

%% check D window ( we check here cause we shouldnt be in C window provided that our boost duration is only a few mins and we are not highly elliptical)
% if(isbetween(current_time,tDBoostStartCommand(end)-seconds(preWindowDetectionRange),tDBoostEndCommand(end)))
%     % we are in the thrustingwindow, so keep thrust, and start and end
%     % times constant
%     dVD(:,end+1) = dVD(:,end); %=last value
%     tDBoostStartCommand(end+1) = tDBoostStartCommand(end);
%     tDBoostEndCommand(end+1) = tDBoostEndCommand(end);
%     DThrustVector(:,end+1) = dVD(:,end);
%     
%     if(inDZone(end) == 0) %if this is the first time we have fallen innto this window
%         inDZone(end) = 1; %toggle the fireC sticky flag
%         
%         addEventToOrekitDateTimeDetector(end+1) = thrustScheduler(4, tDBoostStartCommand(end), tDBoostEndCommand(end),impulsive);
%         eventTypes(end+1) = 4;
%         thrustDirection(:,end+1) = dVD(:,end);
%         thrustWindowStart(end+1) = tDBoostStartCommand(end);
%         thrustWindowEnd(end+1) = tDBoostEndCommand(end);
%         
%         if(addEventToOrekitDateTimeDetector(end)==1)%if its this events turn to take place
%             addEventToOrekitDateTimeDetectorFlag = 1;
%             eventThrustDirection = thrustDirection(:,end)';
%             eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
%             eventThrustWindowEnd = datevec(thrustWindowEnd(end));
%             returnMatrix = [returnMatrix, addEventToOrekitDateTimeDetectorFlag,eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];
%         end
%         
%     end
% else
%     inDZone(end) = 0;
%     %update estimates
%     [dVD(:,end+1), tDBoostStartCommand(end+1), tDBoostEndCommand(end+1)] = updateThrustTimes( 4, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
%     DThrustVector(:,end+1) = [0;0;0];
% end

%     
% if(norm(eventThrustDirection) ~= 0)%avoid dividing by zero
%     eventThrustDirection= eventThrustDirection/norm(eventThrustDirection); %normalized to simply get a unit vector in the thrust direction
% end


%net/overall fire the thruster flag - should we fire the thruster?
% fireThruster(end+1) = any([fireA(end),fireB(end),fireC(end),fireD(end)]); %return 1 if any of A B C D = 1

%net/overall thrust vector - what thrust should we apply ? (delta V)
thrustVector(:,end+1) = AThrustVector(:,end)+BThrustVector(:,end)+CThrustVector(:,end)+DThrustVector(:,end); %net delta V
netThrustVector(end+1) = sqrt(sum(abs(thrustVector(:,end)).^2,1));
% if(norm(thrustVector(:,end)) ~= 0)%avoid dividing by zero
%     thrustVector(:,end) = thrustVector(:,end)/norm(thrustVector(:,end)); %normalized to simply get a unit vector in the thrust direction
% end


%currentThrustDirection = (LVLH2ECICharles(pos(:,end), vel(:,end)))*[0.00;0.1;0.00];
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
% % % eventThrustDirection = thrustDirection(:,end);
% % % eventThrustWindowStart = datevec(thrustWindowStart(end)); %orekit needs a date array, not a matlab datetime object
% % % eventThrustWindowEnd = datevec(thrustWindowEnd(end));


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
    plotMeanOeErrors;
end
end

% % % % %% transform verification
% % % % eventThrustDirection'
% % % % R = LVLH2ECICharles(position', velocity')
% % % % vector = R*eventThrustDirection'
% % % % R_alex = eci2lvlh(position', velocity');
% % % % R_alex=R_alex'
% % % % vector_alex = R_alex*eventThrustDirection'
%returnMatrix = [returnMatrix, addEventToOrekitDateTimeDetectorFlag',eventThrustDirection,eventThrustWindowStart,eventThrustWindowEnd];
%returnMatrix = repmat(returnMatrix,2,1);
end