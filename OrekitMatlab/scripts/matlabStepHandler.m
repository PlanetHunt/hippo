function [ thrustFlag, currentThrustDirection ] = matlabStepHandler( orbital_elements, position, velocity, timestamp, current_mass, last_step_flag )
%MATLABSTEPHANDLER function to be called at every time step
%   event_A     perigee
%   event_B     apogee
%   event_C     theta = 0
%   event_D     theta = 90 deg
%   currentThrustDirecton = a unit vector in thrust direction
%   thrustFlag = fire thruster command 1 or 0
%   orbital_elements = [a;e;i;omega;raan;tru Anom; mean anom]
global timeVector;
global mass;
global stepSize duration req j2 g thrustDurationLimit; %ie the limit so that it is still an 'impulse'
global numThrusters;
global oed oec oedm oecm oeError;%orbital elements of deputy and chief arrays (also mean eles)
global fireA fireB fireC fireD fireThruster thrustVector;
global dVA dVB dVC dVD;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
global AThrustVector BThrustVector CThrustVector DThrustVector;
global Isp thrust;
global pos vel;
global netThrustVector
current_time = datetime(timestamp);
timeVector=[timeVector;current_time];
mass = [mass; current_mass];
pos = [pos, position' ];
vel = [vel, velocity' ];
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

% check if we are in a thrusting period
%% check A window - check if we are in the last A window (most recent estimate of A window from last time step, thats why we have ii-1 )
if(isbetween(current_time,tABoostStartCommand(end),tABoostEndCommand(end))) 
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVA(:,end+1) = dVA(:,end); %=last value
    %dont change the window or the thrust while we are in the window
    tABoostStartCommand(end+1) = tABoostStartCommand(end);%append the value from last time step
    tABoostEndCommand(end+1) = tABoostEndCommand(end);
    fireA(end+1) = 1;
    AThrustVector(:,end+1) = dVA(:,end);%if fire commanded, compy the dV vector into the Thrust Vector
    
    %if we are in event A we can't be in B, so set the B thrust to zero
    [dVB(:,end+1), tBBoostStartCommand(end+1), tBBoostEndCommand(end+1)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end), numThrusters);
    fireB(end+1) = 0;
    BThrustVector(:,end+1) = [0;0;0];
else
    %update estimates
    [dVA(:,end+1), tABoostStartCommand(end+1), tABoostEndCommand(end+1)] = updateThrustTimes( 1, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    fireA(end+1) = 0;
    AThrustVector(:,end+1) = [0;0;0];
    
    %% check B window (only makes sense to check B window if we are sure we are not in A. (we cant be in the perigee and apogee boost windows at the same time.)
    if(isbetween(current_time,tBBoostStartCommand(end),tBBoostEndCommand(end)))
        % we are in the thrustingwindow, so keep thrust, and start and end
        % times constant
        dVB(:,end+1) = dVB(:,end); %=last value
        tBBoostStartCommand(end+1) = tBBoostStartCommand(end);
        tBBoostEndCommand(end+1) = tBBoostEndCommand(end);

        fireB(end+1) = 1;
        BThrustVector(:,end+1) = dVB(:,end);
    else
        %update estimates
        [dVB(:,end+1), tBBoostStartCommand(end+1), tBBoostEndCommand(end+1)] = updateThrustTimes( 2, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
        fireB(end+1) = 0;
        BThrustVector(:,end+1) = [0;0;0];
    end

end

%% check C window
if(isbetween(current_time,tCBoostStartCommand(end),tCBoostEndCommand(end)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVC(:,end+1) = dVC(:,end); %=last value
    tCBoostStartCommand(end+1) = tCBoostStartCommand(end);
    tCBoostEndCommand(end+1) = tCBoostEndCommand(end);
    
    fireC(end+1) = 1;
    CThrustVector(:,end+1) = dVC(:,end);
    %cant be in b, so update it
    [dVD(:,end+1), tDBoostStartCommand(end+1), tDBoostEndCommand(end+1)] = updateThrustTimes( 4, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    fireD(end+1) = 0;
    DThrustVector(:,end+1) = [0;0;0];
else
    %update estimates
    [dVC(:,end+1), tCBoostStartCommand(end+1), tCBoostEndCommand(end+1)] = updateThrustTimes( 3, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
    fireC(end+1) = 0;
    CThrustVector(:,end+1) = [0;0;0];
    
    %% check D window ( we check here cause we shouldnt be in C window provided that our boost duration is only a few mins and we are not highly elliptical)
    if(isbetween(current_time,tDBoostStartCommand(end),tDBoostEndCommand(end)))
        % we are in the thrustingwindow, so keep thrust, and start and end
        % times constant
        dVD(:,end+1) = dVD(:,end); %=last value
        tDBoostStartCommand(end+1) = tDBoostStartCommand(end);
        tDBoostEndCommand(end+1) = tDBoostEndCommand(end);

        fireD(end+1) = 1;
        DThrustVector(:,end+1) = dVD(:,end);
    else
        %update estimates
        [dVD(:,end+1), tDBoostStartCommand(end+1), tDBoostEndCommand(end+1)] = updateThrustTimes( 4, current_time, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,end), oedm(:,end),numThrusters);
        fireD(end+1) = 0;
        DThrustVector(:,end+1) = [0;0;0];
    end
end

%net/overall fire the thruster flag - should we fire the thruster?
fireThruster(end+1) = any([fireA(end),fireB(end),fireC(end),fireD(end)]); %return 1 if any of A B C D = 1
%net/overall thrust vector - what thrust should we apply ? (delta V)
thrustVector(:,end+1) = AThrustVector(:,end)+BThrustVector(:,end)+CThrustVector(:,end)+DThrustVector(:,end); %net delta V
netThrustVector(end+1) = sqrt(sum(abs(thrustVector(:,end)).^2,1));
% if(norm(thrustVector(:,end)) ~= 0)%avoid dividing by zero
%     thrustVector(:,end) = thrustVector(:,end)/norm(thrustVector(:,end)); %normalized to simply get a unit vector in the thrust direction
% end

%need this for the output arguments, matlab wont allow it directly
thrustFlag =fireThruster(end);
%if(length(netThrustVector)==40)
%    thrustFlag = 1;
%else
%    thrustFlag = 0;
%end
%currentThrustDirection = [1e-6;0;0]; %thrustVector(:,end); %unit vector in the thrust direction
currentThrustDirection = (LVLH2ECICharles(pos(:,end), vel(:,end)))*[0.00;0.01;0.00];
% currentThrustDirection(3) = -currentThrustDirection(3);
%currentThrustDirection = [0; 1; 0]; %hard coded for testing purposes

%if in last step plot everything
if(last_step_flag == 1)
    plotEverything;
end
  
end