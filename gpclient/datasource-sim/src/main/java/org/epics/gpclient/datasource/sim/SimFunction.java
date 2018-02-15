/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.gpclient.datasource.sim;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.epics.util.stats.TimeInterval;
import org.epics.vtype.Time;
import org.epics.vtype.VDouble;

/**
 * Base class for all simulated functions. It provide constant rate data generation
 * facilities.
 *
 * @author carcassi
 */
abstract class SimFunction<T> extends Simulation<T> {

    private static final Logger log = Logger.getLogger(SimFunction.class.getName());

    private Duration timeBetweenSamples;
    private Instant lastSampleTime;

    /**
     * Creates a new simulation function.
     *
     * @param secondsBeetwenSamples seconds between each samples
     * @param classToken simulated class
     */
    SimFunction(double secondsBeetwenSamples, Class<T> classToken) {
        // The timer only accepts interval up to the millisecond.
        // For intervals shorter than that, we calculate the extra samples
        // we need to generate within each time execution.
        super(Duration.ofMillis(Math.max((int) (secondsBeetwenSamples * 1000) / 2, 1)), classToken);

        if (secondsBeetwenSamples <= 0.0) {
            throw new IllegalArgumentException("Interval must be greater than zero (was " + secondsBeetwenSamples + ")");
        }

        if (secondsBeetwenSamples < 0.000001) {
            throw new IllegalArgumentException("Interval must be greater than 0.000001 - no faster than 100KHz (was " + secondsBeetwenSamples + ")");
        }

        timeBetweenSamples = Duration.ofNanos((long) (secondsBeetwenSamples * 1000000000));
    }

    @Override
    Instant resetTime() {
        return Instant.now().minus(timeBetweenSamples);
    }

    /**
     * Calculates and returns the next value.
     *
     * @return the next value
     */
    abstract T nextValue(Instant instant);

    /**
     * Computes all the new values in the given time slice by calling nextValue()
     * appropriately.
     *
     * @param interval the interval where the data should be generated
     * @return the new values
     */
    @Override
    List<T> createValues(TimeInterval interval) {
        List<T> values = new ArrayList<T>();
        Instant newTime;
        if (lastSampleTime != null) {
            newTime = lastSampleTime.plus(timeBetweenSamples);
        } else {
            newTime = interval.getStart();
        }

        while (interval.contains(newTime)) {
            lastSampleTime = newTime;
            values.add(nextValue(lastSampleTime));
            newTime = lastSampleTime.plus(timeBetweenSamples);
        }

        return values;
    }

    /**
     * Creating new value based on the metadata from the old value.
     *
     * @param value new numeric value
     * @param oldValue old VDouble
     * @return new VDouble
     */
    VDouble newValue(double value, VDouble oldValue) {
        return VDouble.of(value, oldValue.getDisplay().newAlarmFor(value), Time.of(lastSampleTime), oldValue.getDisplay());
    }

    /**
     * Returns the time between each sample.
     * 
     * @return a time duration
     */
    public Duration getTimeBetweenSamples() {
        return timeBetweenSamples;
    }
    
}
