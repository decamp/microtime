/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;


/**
 * SyncClockControl is like ClockControl, but each method requires a
 * reference time that specifies precisely when it is to be executed,
 * allowing perfect synchronization between multiple clocks.
 *
 * Note that SyncClockControl DOES NOT perform actions out of order,
 * regardless of {@code execMicros} params. SyncClockControl is
 * compatible withdecreasing or non-linear master clocks.
 *
 * @author Philip DeCamp
 */
public interface SyncClockControl {
    /**
     * Starts the clock running. If clock is already running, call has no effect.
     *
     * @param execMicros Time on master clock when play begins.
     */
    void clockStart( long execMicros );

    /**
     * Stops the clock running. If clock is already stopped, call has no effect.
     *
     * @param execMicros Time on master clock to when play stops.
     */
    void clockStop( long execMicros );

    /**
     * Causes clock to skip to a specified time. Seek works regardlless if clock
     * is playing or stopped.
     *
     * @param execMicros Time on master clock when seek executes.
     * @param seekMicros Time to set clock to.
     */
    void clockSeek( long execMicros, long seekMicros );

    /**
     * Sets the getRate of this clock relative to a parent clock.
     *
     * @param execMicros Time on master clock when change executes.
     * @param rate New getRate of clock playback.
     */
    void clockRate( long execMicros, Frac rate );
}
