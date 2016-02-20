oed = [7016995.871;	0.006422; 97.859*pi/180; 145.12*pi/180; 50.485*pi/180; 68.643*pi/180; 67.959*pi/180];
%oec = [7017102.334; 0.006535; 97.858*pi/180; 145.60397*pi/180; 50.48597*pi/180; 67.871*pi/180; 67.17897*pi/180];
oed(:,2) = oed(:,1); %first step
time = datetime(2013,12,04,11,00,00);
[position_tolerance, min_step, max_step, duration, step_size, choiceofProp] = setNumericalPropagatorSettings();

 mass = 1;%kg
 Isp = 2000;%sec
 thrust = 1e-3; %1 milinewton
 burnTimeLimit = 200; %sec
 
 oed(:,3) = oed(:,2)+0.0001;