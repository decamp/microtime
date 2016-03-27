/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.util.event.EventSource;


/**
 * Represents a clock that can be paused and that can run at
 * an arbitrary rate compared to some reference clock.
 *
 * <p>PlayClocks actually have two possible references:
 * <p>1. All PlayClocks have a "master clock" that should closely follow system time. <br>
 * <p>2. Optionally, some PlayClocks may have a "parent PlayClock" that can provide for heirarchical
 * control. While all clocks in a tree will base time readings on the master clock, their
 * playback state can be affected by accessing any of it's parent clocks. Stopping a PlayClock
 * will cause all children to stop, and doubling the rate of a PlayClock will cause
 * all it's children to double their rates.
 *
 * @author Philip DeCamp
 */
public interface PlayClock extends Clock, ClockControl, EventSource<SyncClockControl> {

    /**
     * @return time of this clock
     */
    long micros();

    /**
     * @return true iff playback state is playing
     */
    boolean isPlaying();

    /**
     * @return the rate of this clock relative to the master clock.
     */
    Frac rate();


    /**
     * @return current time of the master of this clock
     */
    long masterMicros();

    /**
     * The master clock is considered the root timekeeping clock.
     * The master clock: <br>
     * SHOULD be in sync with a system clock, <br>
     * SHOULD be linear and increasing, <br>
     * SHOULD be useable for timing threads or external processes.
     *
     * @return clock The master of this PlayClock.
     */
    Clock masterClock();

    /**
     * Computes the time on the master clock for a time on this PlayClock.
     * If {@code isPlaying()}, this is equivalent to: <br>
     * {@code
     * ( dataMicros - dataStartMicros() ) / playbackRate() + playStartMicros();
     * }
     *
     * @param micros Some time
     * @return playback time when data time would be displayed.
     */
    long toMaster( long micros );

    /**
     * Computes the time on this PlayClock for a time on the master clock.
     * If {@code isPlaying()}, this is equivalent to: <br>
     * {@code
     * ( playMicros - playStartMicros() ) * playbackRate() + dataStartMicros();
     * }
     *
     * @param micros Some time.
     * @return data time to be display at specified play time.
     */
    long fromMaster( long micros );


    /**
     * @param listener Listener to receive all state change notifications.
     */
    void addListener( SyncClockControl listener );

    /**
     * @param listener Listener to remove from state change notifications.
     */
    void removeListener( SyncClockControl listener );

    /**
     * Applies full state of this PlayClock as if that control
     * received the commands at the same time as this clock.
     */
    void applyTo( SyncClockControl target );

    /**
     * Applies full state of this PlayClock to another control
     * but potentially with different execution time.
     * For the most part, the target should have nearly identical
     * state to this clock, but with small rounding errors.
     */
    void applyTo( long exec, SyncClockControl target );


    /**
     * @return time when playback state last changed.
     */
    long timeBasis();

    /**
     * Time of the master clock when playback state last changed.
     *
     * @return play time when the playback state last changed.
     */
    long masterBasis();


    /**
     * PlayClocks actually have two input references:
     * <p>1. All PlayClocks have a "master clock" that should closely follow system time. <br>
     * <p>2. Optionally, some PlayClocks may have a "parent PlayClock" that can provide for heirarchical
     * control. While all clocks in a tree will base time readings on the master clock, their
     * playback state can be affected by accessing any of it's parent clocks. Stopping a PlayClock
     * will cause all children to stop, and doubling the rate of a PlayClock will cause
     * all it's children to double their rates.
     *
     * @return newly created child clock.
     */
    PlayClock createChild();

    /**
     * @return parent clock used as reference time for this clock. May be {@code null}. This is not the same as
     *         the master clock.
     * @see #createChild()
     */
    PlayClock parentClock();

    /**
     * @return true iff this clock plays when parent plays.
     */
    boolean isPlayingRelativeToParent();

    /**
     * @param out receives rate of this clock compared to parent clock.
     */
    void rateRelativeToParent( Frac out );

}
