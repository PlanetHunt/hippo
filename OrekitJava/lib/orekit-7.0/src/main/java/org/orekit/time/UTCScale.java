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
package org.orekit.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.orekit.errors.OrekitException;
import org.orekit.errors.TimeStampedCacheException;
import org.orekit.utils.Constants;
import org.orekit.utils.ImmutableTimeStampedCache;
import org.orekit.utils.TimeStampedCache;

/** Coordinated Universal Time.
 * <p>UTC is related to TAI using step adjustments from time to time
 * according to IERS (International Earth Rotation Service) rules. Before 1972,
 * these adjustments were piecewise linear offsets. Since 1972, these adjustments
 * are piecewise constant offsets, which require introduction of leap seconds.</p>
 * <p>Leap seconds are always inserted as additional seconds at the last minute
 * of the day, pushing the next day forward. Such minutes are therefore more
 * than 60 seconds long. In theory, there may be seconds removal instead of seconds
 * insertion, but up to now (2010) it has never been used. As an example, when a
 * one second leap was introduced at the end of 2005, the UTC time sequence was
 * 2005-12-31T23:59:59 UTC, followed by 2005-12-31T23:59:60 UTC, followed by
 * 2006-01-01T00:00:00 UTC.</p>
 * <p>The OREKIT library retrieves the post-1972 constant time steps data thanks
 * to the {@link org.orekit.data.DataProvidersManager DataProvidersManager} class.
 * The linear models used between 1961 and 1972 are built-in in the class itself.</p>
 * <p>This is intended to be accessed thanks to the {@link TimeScalesFactory} class,
 * so there is no public constructor. Every call to {@link TimeScalesFactory#getUTC()}
 * will create a new {@link UTCScale} instance, sharing the UTC-TAI offset table between
 * all instances.</p>
 * @author Luc Maisonobe
 * @see AbsoluteDate
 */
public class UTCScale implements TimeScale {

    /** Serializable UID. */
    private static final long serialVersionUID = 20131209L;

    /** Time steps. */
    private transient TimeStampedCache<UTCTAIOffset> cache;

    /** Package private constructor for the factory.
     * Used to create the prototype instance of this class that is used to
     * clone all subsequent instances of {@link UTCScale}. Initializes the offset
     * table that is shared among all instances.
     * @param entries user supplied entries
     * @exception OrekitException if cache cannot be set up
     */
    UTCScale(final SortedMap<DateComponents, Integer> entries) throws OrekitException {
        // create cache
        final List<UTCTAIOffset> data = new Generator(entries).getOffsets();
        cache = new ImmutableTimeStampedCache<UTCTAIOffset>(2, data);
    }

    /** Generator for leap seconds entries. */
    private static class Generator {

        /** List of {@link UTCTAIOffset} entries. */
        private final List<UTCTAIOffset> offsets;

        /** Simple constructor.
         * @param entries user supplied entries
         */
        public Generator(final SortedMap<DateComponents, Integer> entries) {

            offsets = new ArrayList<UTCTAIOffset>();

            // set up the linear offsets used between 1961-01-01 and 1971-12-31
            // excerpt from UTC-TAI.history file:
            //  1961  Jan.  1 - 1961  Aug.  1     1.422 818 0s + (MJD - 37 300) x 0.001 296s
            //        Aug.  1 - 1962  Jan.  1     1.372 818 0s +        ""
            //  1962  Jan.  1 - 1963  Nov.  1     1.845 858 0s + (MJD - 37 665) x 0.001 123 2s
            //  1963  Nov.  1 - 1964  Jan.  1     1.945 858 0s +        ""
            //  1964  Jan.  1 -       April 1     3.240 130 0s + (MJD - 38 761) x 0.001 296s
            //        April 1 -       Sept. 1     3.340 130 0s +        ""
            //        Sept. 1 - 1965  Jan.  1     3.440 130 0s +        ""
            //  1965  Jan.  1 -       March 1     3.540 130 0s +        ""
            //        March 1 -       Jul.  1     3.640 130 0s +        ""
            //        Jul.  1 -       Sept. 1     3.740 130 0s +        ""
            //        Sept. 1 - 1966  Jan.  1     3.840 130 0s +        ""
            //  1966  Jan.  1 - 1968  Feb.  1     4.313 170 0s + (MJD - 39 126) x 0.002 592s
            //  1968  Feb.  1 - 1972  Jan.  1     4.213 170 0s +        ""
            addOffsetModel(new DateComponents(1961,  1, 1), 37300, 1.4228180, 0.0012960);
            addOffsetModel(new DateComponents(1961,  8, 1), 37300, 1.3728180, 0.0012960);
            addOffsetModel(new DateComponents(1962,  1, 1), 37665, 1.8458580, 0.0011232);
            addOffsetModel(new DateComponents(1963, 11, 1), 37665, 1.9458580, 0.0011232);
            addOffsetModel(new DateComponents(1964,  1, 1), 38761, 3.2401300, 0.0012960);
            addOffsetModel(new DateComponents(1964,  4, 1), 38761, 3.3401300, 0.0012960);
            addOffsetModel(new DateComponents(1964,  9, 1), 38761, 3.4401300, 0.0012960);
            addOffsetModel(new DateComponents(1965,  1, 1), 38761, 3.5401300, 0.0012960);
            addOffsetModel(new DateComponents(1965,  3, 1), 38761, 3.6401300, 0.0012960);
            addOffsetModel(new DateComponents(1965,  7, 1), 38761, 3.7401300, 0.0012960);
            addOffsetModel(new DateComponents(1965,  9, 1), 38761, 3.8401300, 0.0012960);
            addOffsetModel(new DateComponents(1966,  1, 1), 39126, 4.3131700, 0.0025920);
            addOffsetModel(new DateComponents(1968,  2, 1), 39126, 4.2131700, 0.0025920);

            // add leap second entries in chronological order
            for (Map.Entry<DateComponents, Integer> entry : entries.entrySet()) {
                addOffsetModel(entry.getKey(), 0, entry.getValue(), 0);
            }

        }

        /** Retrieve the generated offsets.
         *
         * @return the {@link UTCTAIOffset}s.
         */
        public List<UTCTAIOffset> getOffsets() {
            return this.offsets;
        }

        /** Add an offset model.
         * <p>
         * This method <em>must</em> be called in chronological order.
         * </p>
         * @param date date of the constant offset model start
         * @param mjdRef reference date of the linear model as a modified julian day
         * @param offset offset at reference date in seconds (TAI minus UTC)
         * @param slope offset slope in seconds per UTC day (TAI minus UTC / dUTC)
         */
        private void addOffsetModel(final DateComponents date, final int mjdRef,
                                    final double offset, final double slope) {

            final TimeScale tai = TimeScalesFactory.getTAI();

            // start of the leap
            final UTCTAIOffset previous    = offsets.isEmpty() ? null : offsets.get(offsets.size() - 1);
            final double previousOffset    = (previous == null) ? 0.0 : previous.getOffset(date, TimeComponents.H00);
            final AbsoluteDate leapStart   = new AbsoluteDate(date, tai).shiftedBy(previousOffset);

            // end of the leap
            final double startOffset       = offset + slope * (date.getMJD() - mjdRef);
            final AbsoluteDate leapEnd     = new AbsoluteDate(date, tai).shiftedBy(startOffset);

            // leap computed at leap start and in UTC scale
            final double normalizedSlope   = slope / Constants.JULIAN_DAY;
            final double leap              = leapEnd.durationFrom(leapStart) / (1 + normalizedSlope);

            if (previous != null) {
                previous.setValidityEnd(leapStart);
            }
            offsets.add(new UTCTAIOffset(leapStart, date.getMJD(), leap, offset, mjdRef, normalizedSlope));

        }

    }

    /** {@inheritDoc} */
    public double offsetFromTAI(final AbsoluteDate date) {
        if (cache.getEarliest().getDate().compareTo(date) > 0) {
            // the date is before the first known leap
            return 0;
        } else if (cache.getLatest().getDate().compareTo(date) < 0) {
            // the date is after the last known leap
            return -cache.getLatest().getOffset(date);
        } else {
            // the date is nominally bracketed by two leaps
            try {
                return -cache.getNeighbors(date).get(0).getOffset(date);
            } catch (TimeStampedCacheException tce) {
                // this should never happen as boundaries have been handled in the previous statements
                throw OrekitException.createInternalError(tce);
            }
        }
    }

    /** {@inheritDoc} */
    public double offsetToTAI(final DateComponents date,
                              final TimeComponents time) {

        if (cache.getEarliest().getMJD() > date.getMJD()) {
            // the date is before the first known leap
            return 0;
        } else if (cache.getLatest().getMJD() <= date.getMJD()) {
            // the date is after the last known leap
            return cache.getLatest().getOffset(date, time);
        } else {
            // the date is nominally bracketed by two leaps
            try {
                // find close neighbors, assuming date in TAI, i.e a date earlier than real UTC date
                final List<UTCTAIOffset> neighbors =
                        cache.getNeighbors(new AbsoluteDate(date, time, TimeScalesFactory.getTAI()));
                if (neighbors.get(1).getMJD() <= date.getMJD()) {
                    // the date is in fact just after a leap second!
                    return neighbors.get(1).getOffset(date, time);
                } else {
                    return neighbors.get(0).getOffset(date, time);
                }
            } catch (TimeStampedCacheException tce) {
                // this should never happen as boundaries have been handled in the previous statements
                throw OrekitException.createInternalError(tce);
            }
        }

    }

    /** {@inheritDoc} */
    public String getName() {
        return "UTC";
    }

    /** {@inheritDoc} */
    public String toString() {
        return getName();
    }

    /** Get the date of the first known leap second.
     * @return date of the first known leap second
     */
    public AbsoluteDate getFirstKnownLeapSecond() {
        return cache.getEarliest().getDate();
    }

    /** Get the date of the last known leap second.
     * @return date of the last known leap second
     */
    public AbsoluteDate getLastKnownLeapSecond() {
        return cache.getLatest().getDate();
    }

    /** Check if date is within a leap second introduction.
     * @param date date to check
     * @return true if time is within a leap second introduction
     */
    public boolean insideLeap(final AbsoluteDate date) {
        if (cache.getEarliest().getDate().compareTo(date) > 0) {
            // the date is before the first known leap
            return false;
        } else if (cache.getLatest().getDate().compareTo(date) < 0) {
            // the date is after the last known leap
            return date.compareTo(cache.getLatest().getValidityStart()) < 0;
        } else {
            // the date is nominally bracketed by two leaps
            try {
                return date.compareTo(cache.getNeighbors(date).get(0).getValidityStart()) < 0;
            } catch (TimeStampedCacheException tce) {
                // this should never happen as boundaries have been handled in the previous statements
                throw OrekitException.createInternalError(tce);
            }
        }
    }

    /** Get the value of the previous leap.
     * @param date date to check
     * @return value of the previous leap
     */
    public double getLeap(final AbsoluteDate date) {
        if (cache.getEarliest().getDate().compareTo(date) > 0) {
            return 0;
        } else if (cache.getLatest().getDate().compareTo(date) < 0) {
            // the date is after the last known leap
            return cache.getLatest().getLeap();
        } else {
            // the date is nominally bracketed by two leaps
            try {
                return cache.getNeighbors(date).get(0).getLeap();
            } catch (TimeStampedCacheException tce) {
                // this should never happen as boundaries have been handled in the previous statements
                throw OrekitException.createInternalError(tce);
            }
        }
    }

    /** Replace the instance with a data transfer object for serialization.
     * <p>
     * This intermediate class serializes only the frame key.
     * </p>
     * @return data transfer object that will be serialized
     */
    private Object writeReplace() {
        return new DataTransferObject();
    }

    /** Internal class used only for serialization. */
    private static class DataTransferObject implements Serializable {

        /** Serializable UID. */
        private static final long serialVersionUID = 20131209L;

        /** Replace the deserialized data transfer object with a {@link UTCScale}.
         * @return replacement {@link UTCScale}
         */
        private Object readResolve() {
            try {
                return TimeScalesFactory.getUTC();
            } catch (OrekitException oe) {
                throw OrekitException.createInternalError(oe);
            }
        }

    }

}
