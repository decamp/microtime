/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;


/**
 * Manages state for playback time and time selection.
 * 
 * @author decamp
 */
public class PlayController implements Ticker {

    /**
     * @param masterClock  Clock used by this controller. If clock requires updating, this must be performed by the user.
     * @return playback controller that does not update clock itself
     */
    public static PlayController create( Clock masterClock ) {
        FullClock state = new FullClock( masterClock );
        return new PlayController( null, state, Mode.AUTO, 0, 0, 1.0 );
    }

    /**
     * @return a PlayController that does not require time updates.
     */
    public static PlayController createAuto() {
        FullClock state = new FullClock( Clock.SYSTEM_CLOCK );
        return new PlayController( null, state, Mode.AUTO, 0, 0, 1.0 );
    }

    /**
     * @return a PlaybackContoller that syncs time to the system clock on each time update.
     */
    public static PlayController createRealtime() {
        ManualClock clock = new ManualClock( System.currentTimeMillis() * 1000L );
        FullClock state = new FullClock( clock );
        return new PlayController( clock, state, Mode.REALTIME, Long.MIN_VALUE, 0, 1.0 );
    }

    /**
     * @param startMicros  Time of clock upon first update.
     * @param rate         Rate of time relative to real time.
     *
     * @return a PlaybackContoller that syncs time to the system clock on each time update.
     */
    public static PlayController createRealtime( long startMicros, double rate ) {
        ManualClock clock = new ManualClock( startMicros );
        FullClock state = new FullClock( clock );

        if( rate == 1.0 ) {
            return new PlayController( clock, state, Mode.REALTIME, startMicros, 0, 1.0 );
        } else {
            return new PlayController( clock, state, Mode.REALTIME_SCALED, startMicros, 0, rate );
        }
    }

    /**
     * @param startMicros  Start micros of play clock.
     * @param stepMicros   Amount to increment play clock on each update.
     * @return a PlayController with a clock that increments by a set amount on each updatem.
     */
    public static PlayController createStepping( long startMicros, long stepMicros ) {
        ManualClock clock = new ManualClock( startMicros );
        FullClock state = new FullClock( clock );
        return new PlayController( clock, state, Mode.STEPPING, startMicros, stepMicros, 1.0 );
    }


    private static enum Mode {
        AUTO,
        STEPPING,
        REALTIME,
        REALTIME_SCALED
    }


    private final ManualClock mUpdateClock;
    private final FullClock mFullClock;

    private final Mode mMode;
    private final long mStartMicros;
    private final long mStepMicros;
    private final double mRate;

    private long mSystemOffset = Long.MIN_VALUE;
    private int mStep = 0;


    private PlayController( ManualClock updateClock,
                            FullClock state,
                            Mode mode,
                            long startMicros,
                            long stepMicros,
                            double rate )
    {
        mUpdateClock = updateClock;
        mFullClock = state;

        mMode = mode;
        mStartMicros = startMicros;
        mStepMicros = stepMicros;
        mRate = rate;
    }
    


    public ClockControl control() {
        return mFullClock;
    }


    public Clock masterClock() {
        return mFullClock.masterClock();
    }


    public PlayClock clock() {
        return mFullClock;
    }


    public boolean isStepping() {
        return mMode == Mode.STEPPING;
    }

    /**
     * Causes PlaybackControllor to update clocks / play state. Calling this
     * method has different effects depending on the how the PlayController.
     * was constructed.
     */
    public void tick() {
        long t = 0;

        switch( mMode ) {
        case REALTIME:
            if( mSystemOffset == Long.MIN_VALUE ) {
                if( mStartMicros == Long.MIN_VALUE ) {
                    mSystemOffset = 0L;
                    t = System.currentTimeMillis() * 1000L;
                } else {
                    mSystemOffset = System.currentTimeMillis() * 1000L - mStartMicros;
                    t = mStartMicros;
                }
            } else {
                t = System.currentTimeMillis() * 1000L - mSystemOffset;
            }
            break;

        case REALTIME_SCALED:
            if( mSystemOffset == Long.MIN_VALUE ) {
                mSystemOffset = System.currentTimeMillis() * 1000L;
                t = mStartMicros;
            } else {
                t = (long)((System.currentTimeMillis() * 1000L - mSystemOffset) * mRate) + mStartMicros;
            }
            break;

        case STEPPING:
            t = mStartMicros + mStepMicros * mStep++;
            break;

        case AUTO:
        default:
            return;
        }

        mUpdateClock.micros( t );
    }


}
