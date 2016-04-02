/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.vec.Frac;


/**
 * Enables control of a clock. ClockControl provides control over three pieces of state:
 * <ul>
 * <li>Time to which clock is set
 * <li>Whether clock is running or stopped.
 * <li>The getRate the clock increases (if running) relative to some basis/master clock.
 * </ul>
 *
 * @author Philip DeCamp
 */
public interface ClockControl extends SyncClockControl {

    /**
     * Starts the clock running. If clock is already running, call has no effect.
     */
    public void clockStart();

    /**
     * Stops the clock running. If clock is already stopped, call has no effect.
     */
    public void clockStop();

    /**
     * Causes clock to skip to a specified time. Seek works regardlless if clock
     * is playing or stopped.
     *
     * @param seekMicros Time to set clock to.
     */
    public void clockSeek( long seekMicros );

    /**
     * Sets the getRate of this clock relative to a parent clock.
     * If the clock's getRate is already {@code getRate}, this call has no effect.
     *
     * @param rate New getRate of clock playback.
     */
    public void clockRate( Frac rate );

}
