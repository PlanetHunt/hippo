function [ thrustFlag, currentThrustDirection ] = matlabStepHandler( orbital_elements, position, velocity, timestamp, current_mass )
%MATLABSTEPHANDLER function to be called at every time step
%   event_A     perigee
%   event_B     apogee
%   event_C     theta = 0
%   event_D     theta = 90 deg
%   currentThrustDirecton = a unit vector in thrust direction
%   thrustFlag = fire thruster command 1 or 0
global timeVector;
global mass;
global step_size duration req j2 g thrustDurationLimit;
global ii %loop variable
global oed oec oedm oecm oeError;%orbital elements of deputy and chief arrays (also mean eles)
global fireA fireB fireC fireD fireThruster thrustVector;
global dVA dVB dVC dVD;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
global Isp thrust;
global pos vel;
pos = [pos, position' ];
vel = [vel, velocity' ];
current_time = datetime(timestamp);
timeVector(ii) = current_time;
oed(:,ii) = (orbital_elements');
oed(4,ii) = wrapTo2Pi(oed(4,ii));
%convert oscilating oe to mean oe
a = oed(1,ii);
e = oed(2,ii);
in = oed(3,ii);
omega = oed(4,ii);
raan = oed(5,ii);
true_anomaly = oed(6,ii);
mean_anomaly = oed(7,ii);

%deputyMeanElements = [a; e; in; omega; raan; true_anomaly; mean_anomaly];
deputyMeanElements = osc2mean([a*10^-3,e,wrapToPi(in),wrapTo2Pi(omega),wrapTo2Pi(raan),wrapTo2Pi(true_anomaly)]);
am = deputyMeanElements(1)*10^3; %convert back to meters; 
em = deputyMeanElements(2);
inm = deputyMeanElements(3);
omegam = deputyMeanElements(4);
raanm = deputyMeanElements(5);
true_anomalym = deputyMeanElements(6);

%eccentric_anomalym = acos((cos(true_anomalym) + em)/(1 + em*cos(true_anomalym)));
eccentric_anomalym=atan2(sqrt((1-em^2))*sin(true_anomalym),em+cos(true_anomalym));
eccentric_anomalym=wrapTo2Pi(eccentric_anomalym);
mean_anomalym = eccentric_anomalym - em*sin(eccentric_anomalym);
mean_anomalym=wrapTo2Pi(mean_anomalym);

mass(ii) = current_mass;

%orbital elements deputy (mean)
oedm(:,ii) = [am; em; inm; omegam; raanm; true_anomalym; mean_anomalym];
oeError(:,ii) = oecm(:,ii)-oedm(:,ii); %%% check this - should it be position of chief rel to deputy or pos deputy rel to chief?

%%
%Initialize variables for Fire
%%
fireA(ii)=0;
fireB(ii)=0;
fireC(ii)=0;
fireD(ii)=0;


% check if we are in a thrusting period
%% check A window - check if we are in the last A window (most recent estimate of A window from last time step, thats why we have ii-1 )
if(isbetween(current_time,tABoostStartCommand(ii-1),tABoostEndCommand(ii-1))) 
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVA(:,ii) = dVA(:,ii-1); %=last value
    tABoostStartCommand(ii) = tABoostStartCommand(ii-1);
    tABoostEndCommand(ii) = tABoostEndCommand(ii-1);
    
    fireA(ii) = 1;
    AThrustVector(:,ii) = dVA(:,ii);
    [dVB(:,ii), tBBoostStartCommand(ii), tBBoostEndCommand(ii)] = updateThrustTimes( 2, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
    fireB(ii) = 0;
    BThrustVector(:,ii) = [0;0;0];
else
    %update estimates
    [dVA(:,ii), tABoostStartCommand(ii), tABoostEndCommand(ii)] = updateThrustTimes( 1, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
    fireA(ii) = 0;
    AThrustVector(:,ii) = [0;0;0];
    
    %% check B window (only makes sense to check B window if we are sure we are not in A. (we cant be in the perigee and apogee boost windows at the same time.)
    if(isbetween(current_time,tBBoostStartCommand(ii-1),tBBoostEndCommand(ii-1)))
        % we are in the thrustingwindow, so keep thrust, and start and end
        % times constant
        dVB(:,ii) = dVB(:,ii-1); %=last value
        tBBoostStartCommand(ii) = tBBoostStartCommand(ii-1);
        tBBoostEndCommand(ii) = tBBoostEndCommand(ii-1);

        fireB(ii) = 1;
        BThrustVector(:,ii) = dVB(:,ii);
    else
        %update estimates
        [dVB(:,ii), tBBoostStartCommand(ii), tBBoostEndCommand(ii)] = updateThrustTimes( 2, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
        fireB(ii) = 0;
        BThrustVector(:,ii) = [0;0;0];
    end

end

%% check C window
if(isbetween(current_time,tCBoostStartCommand(ii-1),tCBoostEndCommand(ii-1)))
    % we are in the thrustingwindow, so keep thrust, and start and end
    % times constant
    dVC(:,ii) = dVC(:,ii-1); %=last value
    tCBoostStartCommand(ii) = tCBoostStartCommand(ii-1);
    tCBoostEndCommand(ii) = tCBoostEndCommand(ii-1);
    
    fireC(ii) = 1;
    CThrustVector(:,ii) = dVC(:,ii);
    [dVD(:,ii), tDBoostStartCommand(ii), tDBoostEndCommand(ii)] = updateThrustTimes( 4, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
    fireD(ii) = 0;
    DThrustVector(:,ii) = [0;0;0];
else
    %update estimates
    [dVC(:,ii), tCBoostStartCommand(ii), tCBoostEndCommand(ii)] = updateThrustTimes( 3, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
    fireC(ii) = 0;
    CThrustVector(:,ii) = [0;0;0];
    
    %% check D window ( we check here cause we shouldnt be in C window provided that our boost duration is only a few mins and we are not highly elliptical)
    if(isbetween(current_time,tDBoostStartCommand(ii-1),tDBoostEndCommand(ii-1)))
        % we are in the thrustingwindow, so keep thrust, and start and end
        % times constant
        dVD(:,ii) = dVD(:,ii-1); %=last value
        tDBoostStartCommand(ii) = tDBoostStartCommand(ii-1);
        tDBoostEndCommand(ii) = tDBoostEndCommand(ii-1);

        fireD(ii) = 1;
        DThrustVector(:,ii) = dVD(:,ii);
    else
        %update estimates
        [dVD(:,ii), tDBoostStartCommand(ii), tDBoostEndCommand(ii)] = updateThrustTimes( 4, current_time, am, em, inm, omegam, raanm, true_anomalym, mean_anomalym, current_mass, Isp, thrust, thrustDurationLimit, oeError(:,ii));
        fireD(ii) = 0;
        DThrustVector(:,ii) = [0;0;0];
    end
end


fireThruster(ii) = any([fireA(ii),fireB(ii),fireC(ii),fireD(ii)]); %return 1 if any of A B C D = 1

thrustVector(:,ii) = AThrustVector(:,ii)+BThrustVector(:,ii)+CThrustVector(:,ii)+DThrustVector(:,ii); %net delta V
if(norm(thrustVector(:,ii)) ~= 0)%avoid dividing by zero
    thrustVector(:,ii) = thrustVector(:,ii)/norm(thrustVector(:,ii)); %normalized 
end
%need this for the output arguments, matlab wont allow it directly
thrustFlag = fireThruster(ii);
currentThrustDirection = thrustVector(:,ii);
%currentThrustDirection = [0; 1; 0];
%if in last step plot everything
if(ii == ceil(duration/step_size)+1)
    plotEverything;
end
ii=ii+1;
  
end

