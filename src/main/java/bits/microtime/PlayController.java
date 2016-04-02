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
        return new PlayController( null, state, Mode.AUTO, 0, 0 );
    }

    /**
     * @return a PlayController that does not require time updates.
     */
    public static PlayController createAuto() {
        FullClock state = new FullClock( Clock.HOST_CLOCK );
        return new PlayController( null, state, Mode.AUTO, 0, 0 );
    }

    /**
     * @return a PlaybackContoller that syncs time to the system clock on each time update.
     */
    public static PlayController createRealtime() {
        ManualClock clock = new ManualClock( System.currentTimeMillis() * 1000L );
        FullClock state = new FullClock( clock );
        return new PlayController( clock, state, Mode.REALTIME, Long.MIN_VALUE, 0 );
    }

    /**
     * @param startMicros  Start micros of play clock.
     * @param stepMicros   Amount to increment play clock on each update.
     * @return a PlayController with a clock that increments by a set amount on each updatem.
     */
    public static PlayController createStepping( long startMicros, long stepMicros ) {
        ManualClock clock = new ManualClock( startMicros );
        FullClock state = new FullClock( clock );
        return new PlayController( clock, state, Mode.STEPPING, startMicros, stepMicros );
    }


    private enum Mode {
        AUTO,
        STEPPING,
        REALTIME
    }


    private final ManualClock mUpdateClock;
    private final FullClock   mFullClock;

    private final Mode mMode;
    private final long mStartMicros;
    private final long mStepMicros;

    private int mTickCount = 0;

    private volatile Ticker vTickCaster;


    private PlayController( ManualClock updateClock,
                            FullClock state,
                            Mode mode,
                            long startMicros,
                            long stepMicros )
    {
        mUpdateClock = updateClock;
        mFullClock = state;
        mMode = mode;
        mStartMicros = startMicros;
        mStepMicros = stepMicros;
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
        long t;

        switch( mMode ) {
        case REALTIME:
            t = System.currentTimeMillis() * 1000L;
            mUpdateClock.micros( t );
            break;

        case STEPPING:
            t = mStartMicros + mStepMicros * mTickCount++;
            mUpdateClock.micros( t );
            break;

        case AUTO:
        default:
            break;
        }

        Ticker cast = vTickCaster;
        if( cast != null ) {
            cast.tick();
        }
    }


    public synchronized void addTicker( Ticker t ) {
        vTickCaster = TickCaster.add( vTickCaster, t );
    }


    public synchronized void removeTicker( Ticker t ) {
        vTickCaster = TickCaster.remove( vTickCaster, t );
    }

}
