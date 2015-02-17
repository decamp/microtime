/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.util.event.EventCaster;

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
//    private       boolean mPlaying     = false;
//    private final Frac    mRate        = new Frac( 1, 1 );
//    private       long    mMasterBasis = 0;
//    private       long    mThisBasis   = 0;


    private LinkedList<Reference<FullClock>> mChildren = new LinkedList<Reference<FullClock>>();
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
    public void applyTo( SyncClockControl control ) {
        synchronized( mLock ) {
            mState.applyTo( control );
        }
    }


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
    public FullClock createChild() {
        synchronized( mLock ) {
            FullClock clock = new FullClock( mLock, mMaster, this );
            mChildren.add( new WeakReference<FullClock>( clock ) );
            return clock;
        }
    }

    /**
     * @return true if clock is set to play if parent is playing.
     */
    public PlayClock parentClock() {
        return mParent;
    }

    /**
     * @return true iff this clock plays when parent plays.
     */
    public boolean isPlayingRelativeToParent() {
        return mRequestPlaying;
    }

    /**
     * @param out receives getRate of this clock compared to parent clock.
     */
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
            Frac.multFrac( mParentRate.mNum, mParentRate.mDen, mRequestRate.mNum, mRequestRate.mDen, mState.mRate );
            castClockRate( exec, new Frac( mState.mRate ) );
        }
    }


    //////// PlayControl methods ////////

    @Override
    public void clockStart() {
        synchronized( mLock ) {
            clockStart( masterMicros() );
        }
    }

    @Override
    public void clockStop() {
        synchronized( mLock ) {
            clockStop( masterMicros() );
        }
    }

    @Override
    public void clockSeek( long seekMicros ) {
        synchronized( mLock ) {
            clockSeek( masterMicros(), seekMicros );
        }
    }

    @Override
    public void clockRate( Frac rate ) {
        synchronized( mLock ) {
            clockRate( masterMicros(), rate );
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


    void parentClockSkip( long exec, long rootDelta ) {
        if( !mRequestPlaying ) {
            return;
        }
        mState.updateBases( exec );
        mState.mTimeBasis += Frac.multLong( rootDelta, mState.mRate.mNum, mState.mRate.mDen, Frac.ROUND_NEAR_INF );
        castClockSeek( exec, rootDelta, mState.mTimeBasis );
    }


    void parentClockRate( long exec, Frac rate ) {
        if( mParentRate.equals( rate ) ) {
            return;
        }
        mParentRate.set( rate );
        mState.updateBases( exec );
        Frac.multFrac( mParentRate.mNum, mParentRate.mDen, mRequestRate.mNum, mRequestRate.mDen, mState.mRate );
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
