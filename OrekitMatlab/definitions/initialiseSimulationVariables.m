function [startingDate,deputyStartingOE,numberOfThrusters, thrustVal, startingMass, position_tolerance, min_step, max_step, duration, step_size, equivalentISP, equivalentThrust] = initialiseSimulationVariables(muValue)
%% global variables
global netThrustVector;
global timeVector;
global Isp mass;
global mu thrust numThrusters;
global req j2 g thrustDurationLimit;
% global position_tolerance min_step max_step duration step_size choiceofProp;
%global ii %loop variable
global oed oec oedm oecm oeError;%orbital elements of deputy and chief arrays (also mean eles)
global fireA fireB fireC fireD fireThruster;
global dVA dVB dVC dVD;

global AThrustVector BThrustVector CThrustVector DThrustVector thrustVector;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
%setMu(mu);
netThrustVector = 0;
thrustVector = [0;0;0];
mu = muValue;
oeError = [0; 0; 0; 0; 0; 0; 0]; %maybe should calculate this properly for the starting conditions, dont forget to use mean elements
timeVector = datetime(0001,01,01,000,00,00);
tABoostStartCommand = datetime(0001,01,01,000,00,00);
tBBoostStartCommand = datetime(0001,01,01,000,00,00);
tCBoostStartCommand = datetime(0001,01,01,000,00,00);
tDBoostStartCommand = datetime(0001,01,01,000,00,00);
tABoostEndCommand = datetime(0001,01,01,000,00,00);
tBBoostEndCommand = datetime(0001,01,01,000,00,00);
tCBoostEndCommand = datetime(0001,01,01,000,00,00);
tDBoostEndCommand = datetime(0001,01,01,000,00,00);
AThrustVector = [0;0;0];
BThrustVector = [0;0;0];
CThrustVector = [0;0;0];
DThrustVector = [0;0;0];
fireThruster = 0;
fireA = 0; fireB = 0; fireC = 0; fireD = 0;
dVA = [0;0;0]; dVB = [0;0;0]; dVC = [0;0;0]; dVD = [0;0;0]; 
[position_tolerance, min_step, max_step, duration, step_size, ~] = setNumericalPropagatorSettings();
global stepSize;
stepSize = step_size; %matlabs copy.

global pos vel;
pos = [0;0;0];
vel = [0;0;0];
mass = 0;
%chief
%hincubeOE on 4/12/2013
%oec = setChiefOrbitalElements(1)

%Chief OE from Schaubs paper =2
oec = setChiefOrbitalElements(2);
oecm = oec; %just assume this for now - later this hsould be changed to get the actual mean cheif orbital elements
%initialise deputy OE
oed = zeros(7,1);
oedm = zeros(7,1);
%set initial deputy OEs and the date
[deputyStartingOE,startingDate] = setDeputyOrbitalElements(mu,5); %5 - shaubs deputy



%thruster operating point
Isp = 2000;%s
equivalentISP = Isp;
equivalentThrust = 0.1;% numThrusters*thrust;
numberOfThrusters = 1000;
numThrusters = numberOfThrusters;
thrust = 19e-6; %N per thruster
thrustVal = thrust; %this is the copy of the variable returned to orekit (cant send a global variable)
thrustDurationLimit = 180; %seconds
%mu = 3.986004415000000e+14;
req = 6378.137; %WGS84_EARTH_EQUATORIAL_RADIUS
j2 = 1.08262668355e-3;
g = 9.80665; %m/s^2
startingMass = 1; %assume initial mass of sc is 1kg + the fuel of one thruster
global nextWindowStart nextWindowEnd nextWindowThrustDirection nextWindowType addToThrustCommandQue flagSentForNextWindow;
nextWindowStart = datetime(0001,01,01,000,00,00);
nextWindowEnd = datetime(0001,01,01,000,00,00);
nextWindowThrustDirection = [0;0;0];
nextWindowType = 0;
addToThrustCommandQue = 0;
flagSentForNextWindow = 0;
approximateStartingOE = [deputyStartingOE,deputyStartingOE(6)]; %approximate true anomaly with mean anomaly (it doesnt matter I think) 
[ nextWindowType(end), nextWindowStart(end), nextWindowEnd(end), nextWindowThrustDirection(:,(end)) ] = identifyNextEvent( datetime(startingDate), startingMass, Isp, thrust, thrustDurationLimit, oeError(:,end),approximateStartingOE, numThrusters );
global allWindowTypes
allWindowTypes = [0;0;0;0];
global latitudeArgument
latitudeArgument = 0;
