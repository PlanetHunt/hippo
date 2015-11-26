/* Copyright 2002-2015 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orekit.propagation.events;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orekit.Utils;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.errors.OrekitException;
import org.orekit.errors.PropagationException;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.EquinoctialOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.EcksteinHechlerPropagator;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;

public class EventShifterTest {

    private double           mu;
    private AbsoluteDate     iniDate;
    private Propagator       propagator;
    private List<EventEntry> log;

    private double sunRadius = 696000000.;
    private double earthRadius = 6400000.;

    @Test
    public void testNegNeg() throws OrekitException {
        propagator.addEventDetector(createRawDetector("raw increasing", "raw decreasing", 2.0e-9));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("shifted increasing", "shifted decreasing", 1.0e-3),
                                                                      true, -15, -20));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("unshifted increasing", "unshifted decreasing", 1.0e-3),
                                                                      false, -5, -10));
        propagator.propagate(iniDate.shiftedBy(6000));
        Assert.assertEquals(6, log.size());
        log.get(0).checkExpected(2280.238432465, "shifted decreasing");
        log.get(1).checkExpected(2300.238432465, "unshifted decreasing");
        log.get(2).checkExpected(2300.238432465, "raw decreasing");
        log.get(3).checkExpected(4361.986163327, "shifted increasing");
        log.get(4).checkExpected(4376.986163327, "unshifted increasing");
        log.get(5).checkExpected(4376.986163327, "raw increasing");
    }

    @Test
    public void testNegPos() throws OrekitException {
        propagator.addEventDetector(createRawDetector("raw increasing", "raw decreasing", 2.0e-9));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("shifted increasing", "shifted decreasing", 1.0e-3),
                                                                      true, -15,  20));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("unshifted increasing", "unshifted decreasing", 1.0e-3),
                                                                      false, -5,  10));
        propagator.propagate(iniDate.shiftedBy(6000));
        Assert.assertEquals(6, log.size());
        log.get(0).checkExpected(2300.238432465, "raw decreasing");
        log.get(1).checkExpected(2300.238432465, "unshifted decreasing");
        log.get(2).checkExpected(2320.238432465, "shifted decreasing");
        log.get(3).checkExpected(4361.986163327, "shifted increasing");
        log.get(4).checkExpected(4376.986163327, "unshifted increasing");
        log.get(5).checkExpected(4376.986163327, "raw increasing");
    }

    @Test
    public void testPosNeg() throws OrekitException {
        propagator.addEventDetector(createRawDetector("raw increasing", "raw decreasing", 2.0e-9));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("shifted increasing", "shifted decreasing", 1.0e-3),
                                                                      true,  15, -20));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("unshifted increasing", "unshifted decreasing", 1.0e-3),
                                                                      false,  5, -10));
        propagator.propagate(iniDate.shiftedBy(6000));
        Assert.assertEquals(6, log.size());
        log.get(0).checkExpected(2280.238432465, "shifted decreasing");
        log.get(1).checkExpected(2300.238432465, "unshifted decreasing");
        log.get(2).checkExpected(2300.238432465, "raw decreasing");
        log.get(3).checkExpected(4376.986163327, "raw increasing");
        log.get(4).checkExpected(4376.986163327, "unshifted increasing");
        log.get(5).checkExpected(4391.986163327, "shifted increasing");
    }

    @Test
    public void testPosPos() throws OrekitException {
        propagator.addEventDetector(createRawDetector("raw increasing", "raw decreasing", 2.0e-9));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("shifted increasing", "shifted decreasing", 1.0e-3),
                                                                      true,  15,  20));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("unshifted increasing", "unshifted decreasing", 1.0e-3),
                                                                      false,  5,  10));
        propagator.propagate(iniDate.shiftedBy(6000));
        Assert.assertEquals(6, log.size());
        log.get(0).checkExpected(2300.238432465, "raw decreasing");
        log.get(1).checkExpected(2300.238432465, "unshifted decreasing");
        log.get(2).checkExpected(2320.238432465, "shifted decreasing");
        log.get(3).checkExpected(4376.986163327, "raw increasing");
        log.get(4).checkExpected(4376.986163327, "unshifted increasing");
        log.get(5).checkExpected(4391.986163327, "shifted increasing");
    }

    @Test
    public void testIncreasingError() throws OrekitException {
        propagator.addEventDetector(createRawDetector("raw increasing", "raw decreasing", 2.0e-9));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("-10s increasing", "-10s decreasing", 2.0e-3),
                                                                      true, -10, -10));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("-100s increasing", "-100s decreasing", 3.0e-2),
                                                                      true, -100, -100));
        propagator.addEventDetector(new EventShifter<EclipseDetector>(createRawDetector("-1000s increasing", "-1000s decreasing", 5.0),
                                                                      true, -1000, -1000));
        propagator.propagate(iniDate.shiftedBy(20000));

        // the raw eclipses (not all within the propagation range) are at times:
        // [ 2300.23843246594,   4376.986163326932]
        // [ 8210.85851802963,  10287.572940950127]
        // [14121.478252940502, 16198.159277277191]
        // [20032.097637495113, 22108.745172638683]
        // [25942.716671989547, 28019.330627364776]
        // [31853.335356719457, 33929.91564178527]
        // [ 37763.95369198012, 39840.50021622965]
        Assert.assertEquals(26, log.size());
        log.get( 0).checkExpected( 1300.238432465, "-1000s decreasing");
        log.get( 1).checkExpected( 2200.238432465, "-100s decreasing");
        log.get( 2).checkExpected( 2290.238432465, "-10s decreasing");
        log.get( 3).checkExpected( 2300.238432465, "raw decreasing");
        log.get( 4).checkExpected( 3376.986163327, "-1000s increasing");
        log.get( 5).checkExpected( 4276.986163327, "-100s increasing");
        log.get( 6).checkExpected( 4366.986163327, "-10s increasing");
        log.get( 7).checkExpected( 4376.986163327, "raw increasing");
        log.get( 8).checkExpected( 7210.858518030, "-1000s decreasing");
        log.get( 9).checkExpected( 8110.858518030, "-100s decreasing");
        log.get(10).checkExpected( 8200.858518030, "-10s decreasing");
        log.get(11).checkExpected( 8210.858518030, "raw decreasing");
        log.get(12).checkExpected( 9287.572940950, "-1000s increasing");
        log.get(13).checkExpected(10187.572940950, "-100s increasing");
        log.get(14).checkExpected(10277.572940950, "-10s increasing");
        log.get(15).checkExpected(10287.572940950, "raw increasing");
        log.get(16).checkExpected(13121.478252941, "-1000s decreasing");
        log.get(17).checkExpected(14021.478252941, "-100s decreasing");
        log.get(18).checkExpected(14111.478252941, "-10s decreasing");
        log.get(19).checkExpected(14121.478252941, "raw decreasing");
        log.get(20).checkExpected(15198.159277277, "-1000s increasing");
        log.get(21).checkExpected(16098.159277277, "-100s increasing");
        log.get(22).checkExpected(16188.159277277, "-10s increasing");
        log.get(23).checkExpected(16198.159277277, "raw increasing");
        log.get(24).checkExpected(19032.097637495, "-1000s decreasing");
        log.get(25).checkExpected(19932.097637495, "-100s decreasing");

        for (EventEntry entry : log) {
            double error = entry.getTimeError();
            if (entry.name.contains("10s")) {
                Assert.assertTrue(error > 0.00001);
                Assert.assertTrue(error < 0.0003);
            } else if (entry.name.contains("100s")) {
                Assert.assertTrue(error > 0.002);
                Assert.assertTrue(error < 0.03);
            } else if (entry.name.contains("1000s")) {
                Assert.assertTrue(error > 0.7);
                Assert.assertTrue(error < 3.3);
            }
        }
    }

    private EclipseDetector createRawDetector(final String nameIncreasing, final String nameDecreasing,
                                              final double tolerance)
        throws OrekitException {
        return new EclipseDetector(60., 1.e-10,
                                   CelestialBodyFactory.getSun(), sunRadius,
                                   CelestialBodyFactory.getEarth(), earthRadius).
                                   withHandler(new EventHandler<EclipseDetector>() {
                                       public Action eventOccurred(SpacecraftState s, EclipseDetector detector,
                                                                   boolean increasing) {
                                           log.add(new EventEntry(s.getDate().durationFrom(iniDate), tolerance,
                                                                  increasing ? nameIncreasing : nameDecreasing));
                                           return Action.CONTINUE;
                                       }
                                       public SpacecraftState resetState(EclipseDetector detector, SpacecraftState oldState) {
                                           return oldState;
                                       }
                                   });
    }

    @Before
    public void setUp() {
        try {
            Utils.setDataRoot("regular-data");
            mu  = 3.9860047e14;
            double ae  = 6.378137e6;
            double c20 = -1.08263e-3;
            double c30 = 2.54e-6;
            double c40 = 1.62e-6;
            double c50 = 2.3e-7;
            double c60 = -5.5e-7;
            final Vector3D position  = new Vector3D(-6142438.668, 3492467.560, -25767.25680);
            final Vector3D velocity  = new Vector3D(505.8479685, 942.7809215, 7435.922231);
            iniDate = new AbsoluteDate(1969, 7, 28, 4, 0, 0.0, TimeScalesFactory.getTT());
            final Orbit orbit = new EquinoctialOrbit(new PVCoordinates(position,  velocity),
                                                     FramesFactory.getGCRF(), iniDate, mu);
            propagator =
                new EcksteinHechlerPropagator(orbit, ae, mu, c20, c30, c40, c50, c60);
            log = new ArrayList<EventEntry>();
        } catch (PropagationException pe) {
            Assert.fail(pe.getLocalizedMessage());
        }
    }

    @After
    public void tearDown() {
        iniDate = null;
        propagator = null;
        log = null;
    }

    private static class EventEntry {

        private final double dt;
        private double expectedDT;
        private final double tolerance;
        private final String name;

        public EventEntry(final double dt, final double tolerance, final String name) {
            this.dt         = dt;
            this.expectedDT = Double.NaN;
            this.tolerance  = tolerance;
            this.name       = name;
        }

        public void checkExpected(final double expectedDT, final String name) {
            this.expectedDT = expectedDT;
            Assert.assertEquals(expectedDT, dt, tolerance);
            Assert.assertEquals(name, this.name);
        }

        public double getTimeError() {
            return FastMath.abs(dt - expectedDT);
        }

    }

}

