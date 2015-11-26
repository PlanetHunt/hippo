%%
% est script. Provides 24h propagation errors for each bulletin.
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: run.m 35 2013-07-17 21:13:51Z Rami $
%%
close all
clear all
clear java
% clc
loaded = javaclasspath('-dynamic');
cmFlag = 0;
for k=1:length(loaded)
 if ~isempty(strfind(loaded{k}, 'orekit'))
     cmFlag = 1;
 end
end
orekitFlag = 0;
for k=1:length(loaded)
    if ~isempty(strfind(loaded{k}, 'orekit'))
        orekitFlag = 1;
    end
end

if cmFlag == 0
    javaaddpath(fullfile(cd, '..', 'lib', 'commons-math3-3.2.jar'));
end
if orekitFlag == 0
    javaaddpath(fullfile(cd, '..', 'lib', 'orekit-6.0.jar'));
end

% From java
import java.lang.Math;
import java.lang.System;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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

current = cd;
cd('..');

DM=org.orekit.data.DataProvidersManager.getInstance();
crawler=org.orekit.data.DirectoryCrawler(File([cd ,'\data\']));
DM.clearProviders();
DM.addProvider(crawler);

cd(current);

[issData, header] = parseISSData();

for start = 1:length(issData) - 1

    clear propagator
    
pt1 = issData(start);
pt2 = issData(start + 1);

pt1pv = pt1.data;
pt2pv = pt2.data;

pt1t = pt1.time;
pt2t = pt2.time;

p1 = Vector3D(pt1pv.X, pt1pv.Y, pt1pv.Z);
v1 = Vector3D(pt1pv.XDot, pt1pv.YDot, pt1pv.ZDot);

p2 = Vector3D(pt2pv.X, pt2pv.Y, pt2pv.Z);
v2 = Vector3D(pt2pv.XDot, pt2pv.YDot, pt2pv.ZDot);

utc = TimeScalesFactory.getUTC();
d1 = AbsoluteDate(DateComponents(pt1t.year, pt1t.doy), TimeComponents(pt1t.hour, pt1t.minute, pt1t.second), utc);
d2 = AbsoluteDate(DateComponents(pt2t.year, pt2t.doy), TimeComponents(pt2t.hour, pt2t.minute, pt2t.second), utc);

cd('..');

propagator = getPropagator(p1, v1, d1);

cd(current);

%% propagate
startT = System.currentTimeMillis;
finalSpacecraftState = propagator.propagate(d2);
endT = System.currentTimeMillis;

pv = finalSpacecraftState.getPVCoordinates();
p = pv.getPosition();
v = pv.getVelocity();

ep = p.subtract(p2).getNorm();
ev = v.subtract(v2).getNorm();

fprintf('Run #%i : %.2f s\n', start, (endT - startT)/1000);
fprintf('Orbit bulletin dated %s UTC\n', char(d1.toString()));
fprintf('Errors after 24h : %d m and %d m/s\n', ep, ev);

end
