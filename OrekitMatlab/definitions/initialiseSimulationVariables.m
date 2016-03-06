function [startingDate,StartingOE,numberOfThrusters, thrustVal, startingMass, position_tolerance, min_step, max_step, duration, step_size, equivalentISP, equivalentThrust, max_check] = initialiseSimulationVariables(muValue)
%% global variables
global timerVal ; timerVal = tic;
global netThrustVector;
global timeVector;
global Isp mass;
global mu thrust numThrusters;
global req j2 g thrustDurationLimit;
% global position_tolerance min_step max_step duration step_size choiceofProp;
%global ii %loop variable
global oed oec oedm oecm oeError;%orbital elements of deputy and chief arrays (also mean eles)
global inAZone inBZone inCZone inDZone fireThruster;
global dVA dVB dVC dVD;

global AThrustVector BThrustVector CThrustVector DThrustVector thrustVector;
global tABoostStartCommand tBBoostStartCommand tCBoostStartCommand tDBoostStartCommand;
global tABoostEndCommand tBBoostEndCommand tCBoostEndCommand tDBoostEndCommand;
%setMu(mu);

global eventTypes addEventToOrekitDateTimeDetector thrustDirection thrustWindowStart thrustWindowEnd;
thrustWindowStart = datetime(0001,01,01,000,00,00);
thrustWindowEnd = datetime(0001,01,01,000,00,00);
thrustDirection = [0;0;0];
addEventToOrekitDateTimeDetector = 0;
eventTypes = 0;
global counter recentOrbit initialize; %pouyans variable
global apsideCounter
apsideCounter = 0;
initialize = 1;
counter = 0;
recentOrbit = '';
req = 6378.137; %WGS84_EARTH_EQUATORIAL_RADIUS
j2 = 1.08262668355e-3;
g = 9.80665; %m/s^2

netThrustVector = 0;
thrustVector = [0;0;0];
mu = muValue;
oeError = [0; 0; 0; 0; 0; 0; 0]; %maybe should calculate this properly for the starting conditions, dont forget to use mean elements
timeVector = datetime(0001,01,01,000,00,00);
tABoostStartCommand = datetime(0001,12,01,000,00,00);
tBBoostStartCommand = datetime(0001,12,01,000,00,00);
tCBoostStartCommand = datetime(0001,12,01,000,00,00);
tDBoostStartCommand = datetime(0001,12,01,000,00,00);
tABoostEndCommand = datetime(0001,01,12,000,00,00);
tBBoostEndCommand = datetime(0001,01,12,000,00,00);
tCBoostEndCommand = datetime(0001,01,12,000,00,00);
tDBoostEndCommand = datetime(0001,01,12,000,00,00);
AThrustVector = [0;0;0];
BThrustVector = [0;0;0];
CThrustVector = [0;0;0];
DThrustVector = [0;0;0];
fireThruster = 0;
inAZone = 0;
inBZone = 0;
inCZone = 0;
inDZone = 0; 
global oecmMatchedTime;
oecmMatchedTime = [0 0 0 0 0 0 0];
dVA = [0;0;0]; dVB = [0;0;0]; dVC = [0;0;0]; dVD = [0;0;0]; 
[position_tolerance, min_step, max_step, duration, step_size, ~, max_check] = setNumericalPropagatorSettings();
global stepSize maxStep maxCheck;
maxStep = max_step;
maxCheck = max_check;
stepSize = step_size; %matlabs copy.
global typeOfSimulation;
global pos vel;
pos = [0;0;0];
vel = [0;0;0];
mass = 0;
global tolerances;
tolerances = [1.2; 0.0000000006; deg2rad(0.006); 0; 0; 0; 0]; %a e i omega raan ta ma
%% change here to propogate the chief first
global oe oem;
%initialise propogated OE
oe = zeros(7,1);
oem = zeros(7,1);

%typeOfSimulation = 'propogateChief'; %note- should probably use a fixed
%typeOfSimulation = 'propogateDeputy';
%typeOfSimulation = 'propogateDeputyStationKeep';
typeOfSimulation = 'propogateDeputyFormationFlight'; %make sure a chief has been propogated fires
switch typeOfSimulation
    case 'propogateChief'
        %oec = setChiefOrbitalElements(2); %Chief OE from Schaubs paper =2
        
        %% OE Chief OSC
        [oec,startingDate] = setChiefOrbitalElements(6);
        oec=oec';
        oecm = [0;0;0;0;0;0;0];
        StartingOE = [oec(1:5)',oec(7)]; %label the chief as deputy. (orekit will propogate the deputy only)

    case 'propogateDeputy'
        %%
        [oed,startingDate] = setDeputyOrbitalElements(mu,6);
        oed=oed';
        oedm = [0;0;0;0;0;0;0];
        StartingOE = [oed(1:5)',oed(7)]; %label the chief as deputy. (orekit will propogate the deputy only)

    case 'propogateDeputyStationKeep'
       [oed,startingDate] = setDeputyOrbitalElements(mu,6);
        oed=oed';
        oedm = [0;0;0;0;0;0;0];
        StartingOE = [oed(1:5)',oed(7)]; %label the chief as deputy. (orekit will propogate the deputy only)
        
        [oec,~] = setChiefOrbitalElements(6);
        oec=oec';
        oecm = convertOscOeToMeanOe(oec);
        
    case 'propogateDeputyFormationFlight' %use the same fixed time step that was used to propogate the chief
       [oed,startingDate] = setDeputyOrbitalElements(mu,6);
        oed=oed';
        oedm = [0;0;0;0;0;0;0];
        StartingOE = [oed(1:5)',oed(7)]; %label the chief as deputy. (orekit will propogate the deputy only)   

        %chief OEs should already exist in workspace, since we already ran propogateChief
end
%%
%thruster operating point
Isp = 2000;%s
equivalentISP = Isp;
equivalentThrust = 0.1;% numThrusters*thrust;
numberOfThrusters = 1;
numThrusters = numberOfThrusters;
thrust = 0.1; %N per thruster
thrustVal = thrust; %this is the copy of the variable returned to orekit (cant send a global variable)
thrustDurationLimit = 180; %seconds
%mu = 3.986004415000000e+14;

startingMass = 1; %assume initial mass of sc is 1kg + the fuel of one thruster
global nextWindowStart nextWindowEnd nextWindowThrustDirection nextWindowType addToThrustCommandQue flagSentForNextWindow;
nextWindowStart = datetime(0001,01,01,000,00,00);
nextWindowEnd = datetime(0001,01,01,000,00,00);
nextWindowThrustDirection = [0;0;0];
nextWindowType = 0;
addToThrustCommandQue = 0;
flagSentForNextWindow = 0;
approximateStartingOE = [StartingOE,StartingOE(6)]; %approximate true anomaly with mean anomaly (it doesnt matter I think) 
%[ nextWindowType(end), nextWindowStart(end), nextWindowEnd(end), nextWindowThrustDirection(:,(end)) ] = identifyNextEvent( datetime(startingDate), startingMass, Isp, thrust, thrustDurationLimit, oeError(:,end),approximateStartingOE, numThrusters );
global allWindowTypes
allWindowTypes = [0;0;0;0];
global latitudeArgument
latitudeArgument = 0;
