/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.util.event.*;


/**
 * Manages state for playback time and time selection.
 * 
 * @author decamp
 */
public class PlayController {

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

    private final EventCaster<PlayControl> mCaster;
    private final PlayMaster mPlayMaster;

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

        mCaster = EventCaster.create( PlayControl.class );
        mPlayMaster = new PlayMaster( mCaster );

        mMode = mode;
        mStartMicros = startMicros;
        mStepMicros = stepMicros;
        mRate = rate;
    }
    


    public AsyncPlayControl control() {
        return mPlayMaster;
    }

    
    public EventSource<PlayControl> caster() {
        return mCaster;
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
     * method has different effects depending on the how the PlaybackContoller
     * was constructed.
     */
    public void updateClocks() {
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



    private final class PlayMaster implements AsyncPlayControl {

        private final PlayControl mCastOut;

        PlayMaster( EventCaster<PlayControl> caster ) {
            mCastOut = caster.cast();
        }


        public synchronized void playStart() {
            playStart( mFullClock.masterMicros() );
        }

        public synchronized void playStart( long execMicros ) {
            if( mFullClock.isPlaying() ) {
                return;
            }
            mFullClock.playStart( execMicros );
            mCastOut.playStart( execMicros );
        }

        public synchronized void playStop() {
            playStop( mFullClock.masterMicros() );
        }

        public synchronized void playStop( long execMicros ) {
            if( !mFullClock.isPlaying() ) {
                return;
            }
            mFullClock.playStop( execMicros );
            mCastOut.playStop( execMicros );
        }

        public synchronized void seek( long seekMicros ) {
            seek( mFullClock.masterMicros(), seekMicros );
        }

        public synchronized void seek( long execMicros, long gotoMicros ) {
            mFullClock.seek( execMicros, gotoMicros );
            mCastOut.seek( execMicros, gotoMicros );
        }

        public synchronized void setRate( double rate ) {
            setRate( mFullClock.masterMicros(), rate );
        }

        public synchronized void setRate( long execMicros, double rate ) {
            if( mFullClock.rate() == rate ) {
                return;
            }
            mFullClock.setRate( execMicros, rate );
            mCastOut.setRate( execMicros, rate );
        }

    }



    @Deprecated public static PlayController newAutoInstance() {
        return createAuto();
    }


    @Deprecated public static PlayController newRealtimeInstance() {
        return createRealtime();
    }


    @Deprecated public static PlayController newRealtimeInstance( long startMicros, double rate ) {
        return createRealtime( startMicros, rate );
    }


    @Deprecated public static PlayController newSteppingInstance( long startMicros, long stepMicros ) {
        return createStepping( startMicros, stepMicros );
    }


    @Deprecated public static PlayController newInstance( Clock masterClock ) {
        return create( masterClock );
    }

}
