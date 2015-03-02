/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.util.event.EventSource;


/**
 * Represents a clock that can be paused and that can run at
 * an arbitrary getRate compared to some reference clock.
 *
 * <p>PlayClocks actually have two possible references:
 * <p>1. All PlayClocks have a "master clock" that should closely follow system time. <br>
 * <p>2. Optionally, some PlayClocks may have a "parent PlayClock" that can provide for heirarchical
 * control. While all clocks in a tree will base time readings on the master clock, their
 * playback state can be affected by accessing any of it's parent clocks. Stopping a PlayClock
 * will cause all children to stop, and doubling the getRate of a PlayClock will cause
 * all it's children to double their rates.
 *
 * @author Philip DeCamp
 */
public interface PlayClock extends Clock, ClockControl, EventSource<SyncClockControl> {

    /**
     * @return time of this clock
     */
    public long micros();

    /**
     * @return true iff playback state is playing
     */
    public boolean isPlaying();

    /**
     * @return the rate of this clock relative to the master clock.
     */
    public Frac rate();


    /**
     * @return current time of the master of this clock
     */
    public long masterMicros();

    /**
     * The master clock is considered the root timekeeping clock.
     * The master clock: <br>
     * SHOULD be in sync with a system clock, <br>
     * SHOULD be linear and increasing, <br>
     * SHOULD be useable for timing threads or external processes.
     *
     * @return clock The master of this PlayClock.
     */
    public Clock masterClock();

    /**
     * Computes the time on the master clock for a time on this PlayClock.
     * If <code>isPlaying()</code>, this is equivalent to: <br />
     * <code>
     * ( dataMicros - dataStartMicros() ) / playbackRate() + playStartMicros();
     * </code>
     *
     * @param micros Some time
     * @return playback time when data time would be displayed.
     */
    public long toMaster( long micros );

    /**
     * Computes the time on this PlayClock for a time on the master clock.
     * If <code>isPlaying()</code>, this is equivalent to: <br/>
     * <code>
     * ( playMicros - playStartMicros() ) * playbackRate() + dataStartMicros();
     * </code>
     *
     * @param micros Some time.
     * @return data time to be display at specified play time.
     */
    public long fromMaster( long micros );


    /**
     * @param listener Listener to receive all state change notifications.
     */
    public void addListener( SyncClockControl listener );

    /**
     * @param listener Listener to remove from state change notifications.
     */
    public void removeListener( SyncClockControl listener );

    /**
     * Applies full state of this PlayClock to some control.
     */
    public void applyTo( SyncClockControl control );


    /**
     * @return time when playback state last changed.
     */
    public long timeBasis();

    /**
     * Time of the master clock when playback state last changed.
     *
     * @return play time when the playback state last changed.
     */
    public long masterBasis();


    /**
     * PlayClocks actually have two input references:
     * <p>1. All PlayClocks have a "master clock" that should closely follow system time. <br>
     * <p>2. Optionally, some PlayClocks may have a "parent PlayClock" that can provide for heirarchical
     * control. While all clocks in a tree will base time readings on the master clock, their
     * playback state can be affected by accessing any of it's parent clocks. Stopping a PlayClock
     * will cause all children to stop, and doubling the getRate of a PlayClock will cause
     * all it's children to double their rates.
     *
     * @return newly created child clock.
     */
    public PlayClock createChild();

    /**
     * @return parent clock used as reference time for this clock. May be {@code null}. This is not the same as
     *         the master clock.
     * @see #createChild()
     */
    public PlayClock parentClock();

    /**
     * @return true iff this clock plays when parent plays.
     */
    public boolean isPlayingRelativeToParent();

    /**
     * @param out receives getRate of this clock compared to parent clock.
     */
    public void rateRelativeToParent( Frac out );

}
