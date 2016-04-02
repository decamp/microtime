/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.util.event.EventCaster;
import bits.vec.Frac;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Philip DeCamp
 */
public class FullClock implements PlayClock, ClockControl {

    private final Object    mLock;
    private final Clock     mMaster;
    private final PlayClock mParent;

    private       boolean mParentPlaying = true;
    private final Frac    mParentRate    = new Frac( 1, 1 );

    private       boolean mRequestPlaying = false;
    private final Frac    mRequestRate    = new Frac( 1, 1 );

    private final ClockState mState = new ClockState();

    private long mExecDelayMicros = 50000L;


    private LinkedList<Reference<FullClock>> mChildren = new LinkedList<>();
    private EventCaster<SyncClockControl>    mCaster   = null;


    public FullClock( Clock master ) {
        mLock = new Object();
        mMaster = master;
        mParent = null;
    }


    FullClock( Object lock, Clock root, PlayClock parent ) {
        mLock = lock;
        mMaster = root;
        mParent = parent;
        mParentPlaying = parent.isPlaying();
        mParentRate.set( parent.rate() );
        mState.mRate.set( mParentRate );
    }


    /**
     * One difficulty presented by the the AsyncClockControl is that it
     * does not specify when precisely a command should be come into
     * effect. By default, the time would be the current time.
     * However, this does not give any users of the clock time
     * to prepare for changes. An audio player, for example,
     * cannot skip to another point in an audio file in 0 seconds.
     * This method gives the user the ability to specify the default
     * delay used on all AsyncClockControl methods to mitigate this
     * probelm.
     *
     * @param delay Number of micros to delay timing of clock change commands.
     */
    public void asyncDelayMicros( long delay ) {
        mExecDelayMicros = delay;
    }

    /**
     * @return the number of microseconds that are automatically added to the
     *         execution time of any calls made to {@link ClockControl} methods.
     * @see #asyncDelayMicros( long )
     */
    public long asyncDelayMicros() {
        return mExecDelayMicros;
    }



    //////// PlayControl methods ////////

    @Override
    public long micros() {
        synchronized( mLock ) {
            return fromMaster( masterMicros() );
        }
    }

    @Override
    public boolean isPlaying() {
        synchronized( mLock ) {
            return mState.mPlaying;
        }
    }

    @Override
    public Frac rate() {
        return new Frac( mState.mRate );
    }


    @Override
    public long masterMicros() {
        return mMaster.micros();
    }

    @Override
    public Clock masterClock() {
        return mMaster;
    }

    @Override
    public long toMaster( long dataMicros ) {
        synchronized( mLock ) {
            return mState.toMaster( dataMicros );
        }
    }

    @Override
    public long fromMaster( long playMicros ) {
        synchronized( mLock ) {
            return mState.fromMaster( playMicros );
        }
    }


    @Override
    public void addListener( SyncClockControl listener ) {
        synchronized( mLock ) {
            if( mCaster == null ) {
                mCaster = EventCaster.create( SyncClockControl.class );
            }
            mCaster.addListener( listener );
        }
    }

    @Override
    public void removeListener( SyncClockControl listener ) {
        synchronized( mLock ) {
            mCaster.removeListener( listener );
            if( mCaster.listenerCount() == 0 ) {
                mCaster = null;
            }
        }
    }

    @Override
    public void applyTo( SyncClockControl target ) {
        synchronized( mLock ) {
            mState.applyTo( target );
        }
    }

    @Override
    public void applyTo( long exec, SyncClockControl target ) {
        synchronized( mLock ) {
            mState.applyTo( exec, target );
        }
    }


    @Override
    public FullClock createChild() {
        synchronized( mLock ) {
            FullClock clock = new FullClock( mLock, mMaster, this );
            mChildren.add( new WeakReference<>( clock ) );
            return clock;
        }
    }

    @Override
    public PlayClock parentClock() {
        return mParent;
    }

    @Override
    public boolean isPlayingRelativeToParent() {
        return mRequestPlaying;
    }

    @Override
    public void rateRelativeToParent( Frac out ) {
        out.set( mRequestRate );
    }


    @Override
    public long timeBasis() {
        synchronized( this ) {
            return mState.mTimeBasis;
        }
    }

    @Override
    public long masterBasis() {
        synchronized( this ) {
            return mState.mMasterBasis;
        }
    }


    //////// SyncClockControl methods ////////

    @Override
    public void clockStart( long exec ) {
        synchronized( mLock ) {
            if( mRequestPlaying ) {
                return;
            }
            mRequestPlaying = true;
            if( !mParentPlaying ) {
                return;
            }
            mState.clockStart( exec );
            castClockStart( exec );
        }
    }

    @Override
    public void clockStop( long exec ) {
        synchronized( mLock ) {
            if( !mRequestPlaying ) {
                return;
            }
            mRequestPlaying = false;
            if( !mParentPlaying ) {
                return;
            }
            mState.clockStop( exec );
            castClockStop( exec );
        }
    }

    @Override
    public void clockSeek( long exec, long seek ) {
        synchronized( mLock ) {
            mState.updateBases( exec );
            long masterDelta = Frac.multLong( seek - mState.mTimeBasis, mState.mRate.mDen, mState.mRate.mNum );
            mState.clockSeek( exec, seek );
            castClockSeek( exec, masterDelta, seek );
        }
    }

    @Override
    public void clockRate( long exec, Frac rate ) {
        synchronized( mLock ) {
            if( mRequestRate.equals( rate ) ) {
                return;
            }
            mState.updateBases( exec );
            mRequestRate.set( rate );
            Frac.mult( mParentRate.mNum, mParentRate.mDen, mRequestRate.mNum, mRequestRate.mDen, mState.mRate );
            castClockRate( exec, new Frac( mState.mRate ) );
        }
    }


    //////// PlayControl methods ////////

    @Override
    public void clockStart() {
        synchronized( mLock ) {
            clockStart( masterMicros() + mExecDelayMicros );
        }
    }

    @Override
    public void clockStop() {
        synchronized( mLock ) {
            clockStop( masterMicros() + mExecDelayMicros );
        }
    }

    @Override
    public void clockSeek( long seekMicros ) {
        synchronized( mLock ) {
            clockSeek( masterMicros() + mExecDelayMicros, seekMicros );
        }
    }

    @Override
    public void clockRate( Frac rate ) {
        synchronized( mLock ) {
            clockRate( masterMicros() + mExecDelayMicros, rate );
        }
    }



    //********************************************************
    // Methods for processing events from parent.
    // Calling thread MUST own mLock.
    //********************************************************

    void parentClockStart( long exec ) {
        if( mParentPlaying ) {
            return;
        }
        mParentPlaying = true;
        if( !mRequestPlaying ) {
            return;
        }
        mState.clockStart( exec );
        castClockStart( exec );
    }


    void parentClockStop( long exec ) {
        if( !mParentPlaying ) {
            return;
        }
        mParentPlaying = false;
        if( !mRequestPlaying ) {
            return;
        }
        mState.clockStop( exec );
        castClockStop( exec );
    }


    void parentClockSkip( long exec, long masterDelta ) {
        if( !mRequestPlaying ) {
            return;
        }
        mState.updateBases( exec );
        mState.mTimeBasis += Frac.multLong( masterDelta, mState.mRate.mNum, mState.mRate.mDen );
        castClockSeek( exec, masterDelta, mState.mTimeBasis );
    }


    void parentClockRate( long exec, Frac rate ) {
        if( mParentRate.equals( rate ) ) {
            return;
        }
        mParentRate.set( rate );
        mState.updateBases( exec );
        Frac.mult( mParentRate.mNum, mParentRate.mDen, mRequestRate.mNum, mRequestRate.mDen, mState.mRate );
        castClockRate( exec, new Frac( mState.mRate ) );
    }


    void castClockStart( long exec ) {
        // Send out listener notifications before child notifications for a more consistent event queue.
        if( mCaster != null ) {
            mCaster.cast().clockStart( exec );
        }

        // Notify children.
        if( mChildren != null ) {
            Iterator<Reference<FullClock>> iter = mChildren.iterator();
            while( iter.hasNext() ) {
                FullClock clock = iter.next().get();
                if( clock == null ) {
                    iter.remove();
                } else {
                    clock.parentClockStart( exec );
                }
            }
        }
    }


    void castClockStop( long exec ) {
        // Send out listener notifications before child notifications for a more consistent event queue.
        if( mCaster != null ) {
            mCaster.cast().clockStop( exec );
        }

        // Notify children.
        if( mChildren != null ) {
            Iterator<Reference<FullClock>> iter = mChildren.iterator();
            while( iter.hasNext() ) {
                FullClock clock = iter.next().get();
                if( clock == null ) {
                    iter.remove();
                } else {
                    clock.parentClockStop( exec );
                }
            }
        }
    }


    void castClockSeek( long exec, long masterDelta, long micros ) {
        // Send out listener notifications before child notifications for a more consistent event queue.
        if( mCaster != null ) {
            mCaster.cast().clockSeek( exec, micros );
        }

        // Notify children.
        if( mChildren != null ) {
            Iterator<Reference<FullClock>> iter = mChildren.iterator();
            while( iter.hasNext() ) {
                FullClock clock = iter.next().get();
                if( clock == null ) {
                    iter.remove();
                } else {
                    clock.parentClockSkip( exec, masterDelta );
                }
            }
        }
    }


    void castClockRate( long exec, Frac rate ) {
        // Send out listener notifications before child notifications for a more consistent event queue.
        if( mCaster != null ) {
            mCaster.cast().clockRate( exec, rate );
        }

        // Notify children.
        if( mChildren != null ) {
            Iterator<Reference<FullClock>> iter = mChildren.iterator();
            while( iter.hasNext() ) {
                FullClock clock = iter.next().get();
                if( clock == null ) {
                    iter.remove();
                } else {
                    clock.parentClockRate( exec, rate );
                }
            }
        }
    }


}
