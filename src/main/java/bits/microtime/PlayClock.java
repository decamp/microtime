/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

/**
 * Maintains state of clock that can be stopped and started or that has 
 * variable speed relative to some MASTER clock. A PlayClock has three basic
 * state variables: micros, isPlaying, rate.  
 * 
 * @author decamp
 */
public interface PlayClock extends Clock {

    /**
     * @return true iff playback state is playing
     */
    public boolean isPlaying();

    /**
     * @return time of this clock
     */
    public long micros();

    /**
     * @return current time of the master of this clock
     */
    public long masterMicros();

    /**
     * @return time when playback state last changed.
     */
    public long syncMicros();

    /**
     * Time of the master clock when playback state last changed.
     * 
     * @return play time when the playback state last changed.
     */
    public long masterSyncMicros();

    /**
     * Computes the time on the master clock for a time on this PlayClock.
     * If <code>isPlaying()</code>, this is equivalent to: <br />
     * <code>
     * ( dataMicros - dataStartMicros() ) / playbackRate() + playStartMicros();
     * </code>
     * 
     * @param micros
     * @return playback time when data time would be displayed.
     */
    public long toMasterMicros( long micros );

    /**
     * Computes the time on this PlayClock for a time on the master clock.
     * If <code>isPlaying()</code>, this is equivalent to: <br/>
     * <code> 
     * ( playMicros - playStartMicros() ) * playbackRate() + dataStartMicros();
     * </code>
     * 
     * @param playMicros
     * @return data time to be display at specified play time.
     */
    public long fromMasterMicros( long playMicros );

    /**
     * @return rate of this clock compared to master clock.
     */
    public double rate();

    /**
     * @return clock The master of this PlayClock.
     */
    public Clock masterClock();

    /**
     * Applies full state of this PlayClock to some control.
     */
    public void applyTo( PlayControl control );

}
