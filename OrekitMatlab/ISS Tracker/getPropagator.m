%%
% ISSTracker propagator definition. File separate from ISSTracker.m for
% test purposes.
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: getPropagator.m 33 2013-07-17 21:10:00Z Rami $
%%
% --- Initialization of Orekit Data / Executes on load
function propagator = getPropagator(p, v, iD)
% s = 'call'
% - Java imports

% From java
import java.lang.Math;
import java.lang.System;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.File;

% From the Apache Commons Math Project
import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.util.*;
import org.apache.commons.math3.ode.nonstiff.*;

% From the ORbit Extrapolation KIT
import org.orekit.bodies.*;
import org.orekit.data.*;
import org.orekit.errors.*;
import org.orekit.frames.*;
import org.orekit.forces.*;
import org.orekit.forces.gravity.*;
import org.orekit.forces.gravity.potential.*;
import org.orekit.forces.radiation.*;
import org.orekit.forces.drag.*;
import org.orekit.orbits.*;
import org.orekit.propagation.*;
import org.orekit.propagation.numerical.*;
import org.orekit.time.*;
import org.orekit.tle.*;
import org.orekit.utils.*;

% data pointer
% System.setProperty(DataProvidersManager.OREKIT_DATA_PATH, 'data');
DM=DataProvidersManager.getInstance();
crawler=DirectoryCrawler(File(fullfile(cd, 'data')));
DM.clearProviders();
DM.addProvider(crawler);

% initialize UTC time scale
utc = TimeScalesFactory.getUTC();
orekitData.utc = utc;

% - GUI Data pointer
hObject = findall(0,'Tag','ISSTracker');
if ~isempty(hObject)
    handles = guidata(hObject);
end

% - ISS live data feed

% - Frames

% Frames used (EME2000 and ITRF2005)
orekitData.frames.EME2000 = FramesFactory.getEME2000();
orekitData.frames.ITRF2005 = FramesFactory.getITRF(IERSConventions.IERS_2010,false);        % orekitData.frames.ITRF2005 = FramesFactory.getITRF2005(false);

% Earth Station Location (Toulouse)
lat = simulation_parameters.userLat;
lon = simulation_parameters.userLon;
alt = simulation_parameters.userAlt;

str = 'Telescope Pointing - User location is';
if ~isempty(hObject)
    set(handles.uipanel4, 'Title', sprintf('%s (%.6f°, %.6f°)',...
        str, lat, lon));
end

stationPoint = GeodeticPoint(FastMath.toRadians(lat),...
    FastMath.toRadians(lon), alt);


% terrestrial frame
orekitData.earthBody = OneAxisEllipsoid(physical_parameters.ae,...
    physical_parameters.f, orekitData.frames.ITRF2005);
orekitData.frames.stationFrame = TopocentricFrame(orekitData.earthBody,...
    stationPoint, 'Toulouse');

% Initial Orbit
initialOrbit = CartesianOrbit(PVCoordinates(p, v),...
    orekitData(1).frames.EME2000, iD, physical_parameters.mu);
initialState = SpacecraftState(initialOrbit);


% Variable Step Dormand Prince Integration
integrator = DormandPrince853Integrator(simulation_parameters.minStep,...
    simulation_parameters.maxStep, simulation_parameters.absTolerance, simulation_parameters.relTolerance);
propagator = ...
    NumericalPropagator(integrator );
propagator.setInitialState(initialState);
propagator.setEphemerisMode();


% - Forces applied on ISS

% Solar System (except Earth)
%   Inner bodies
sunG = ThirdBodyAttraction(CelestialBodyFactory.getSun());
merG = ThirdBodyAttraction(CelestialBodyFactory.getMercury());
venG = ThirdBodyAttraction(CelestialBodyFactory.getVenus());
mooG = ThirdBodyAttraction(CelestialBodyFactory.getMoon());
marG = ThirdBodyAttraction(CelestialBodyFactory.getMars());
%   outer bodies
jupG = ThirdBodyAttraction(CelestialBodyFactory.getJupiter());
satG = ThirdBodyAttraction(CelestialBodyFactory.getSaturn());
uraG = ThirdBodyAttraction(CelestialBodyFactory.getUranus());
nepG = ThirdBodyAttraction(CelestialBodyFactory.getNeptune());
pluG = ThirdBodyAttraction(CelestialBodyFactory.getPluto());

% Earth Gravity - Holmes Featherstone Attraction Model with EIGEN-5C Gravity Model
GravityFieldFactory.clearPotentialCoefficientsReaders;
coefs = GravityFieldFactory.getNormalizedProvider(physical_parameters.degree, physical_parameters.order);
earG = HolmesFeatherstoneAttractionModel(orekitData(1).frames.ITRF2005, coefs);

% Atmosphere drag force and sun radiation pressure
% Both forces require a spacecraft model. A box and solar array
% approximation is considered here with the same physical parameters as the
sun = CelestialBodyFactory.getSun();
solarArrayRotAxis = Vector3D(physical_parameters.solarArrayRotAxis);
dim = physical_parameters.dim;
ISSmodel = BoxAndSolarArraySpacecraft(dim, dim, dim, ...
    sun, physical_parameters.solarArrayArea, solarArrayRotAxis, ...
    physical_parameters.dragCoefficient, physical_parameters.absorptionCoefficient, physical_parameters.reflectionCoefficient);

% ref density : http://heliophysics.nasa.gov/SolarMinimum24/ionoatmos/Solomon.pdf
atmosphere = SimpleExponentialAtmosphere(...
    orekitData(1).earthBody, physical_parameters.refAlt, physical_parameters.density, physical_parameters.hScale) ;

sunRadiation = SolarRadiationPressure(sun, 1.392e6, ISSmodel);
dragForce = DragForce(atmosphere, ISSmodel);

% - Add forces to propagator

% Gravitational Forces
propagator.addForceModel(earG);
propagator.addForceModel(sunG);
propagator.addForceModel(mooG);
if physical_parameters.inner
    propagator.addForceModel(merG);
    propagator.addForceModel(venG);
    propagator.addForceModel(marG);
end
if physical_parameters.outer
    propagator.addForceModel(jupG);
    propagator.addForceModel(satG);
    propagator.addForceModel(uraG);
    propagator.addForceModel(nepG);
    propagator.addForceModel(pluG);
end

propagator.setOrbitType(OrbitType.CARTESIAN);

% Drage and Solar Radiation Pressure
% propagator.addForceModel(sunRadiation);
% propagator.addForceModel(dragForce);
% Calculate the ephemeris that we will use throughout the file

% - Save data structure in GUI
set(hObject, 'UserData', orekitData);

end