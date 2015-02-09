/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.math3d.Frac;


/**
 * @author Philip DeCamp
 */
class PlayState {

    public       boolean mPlaying    = false;
    public final Frac    mRate       = new Frac( 1, 1 );
    public       long    mMasterSync = 0;
    public       long    mSlaveSync  = 0;


    public PlayState() {}


    public PlayState( PlayState copy ) {
        set( copy );
    }


    public void set( PlayState c ) {
        mPlaying = c.mPlaying;
        mRate.set( c.mRate );
        mMasterSync = c.mMasterSync;
        mSlaveSync = c.mSlaveSync;
    }



    public void clockStart( long exec ) {
        if( mPlaying ) {
            return;
        }
        mPlaying = true;
        mMasterSync = exec;
    }


    public void clockStop( long exec ) {
        if( !mPlaying ) {
            return;
        }
        clockSync( exec );
        mPlaying = false;
    }


    public void clockSeek( long exec, long seek ) {
        mMasterSync = exec;
        mSlaveSync  = seek;
    }


    public void clockRate( long exec, Frac rate ) {
        if( mRate.equals( rate ) ) {
            return;
        }
        clockSync( exec );
        mRate.set( rate );
    }


    public void clockSync( long exec ) {
        if( mPlaying ) {
            mSlaveSync += Frac.multLong( exec - mMasterSync, mRate.mNum, mRate.mDen );
        }
        mMasterSync = exec;
    }



    public synchronized long convertSlaveToMaster( long slaveTime ) {
        if( mPlaying ) {
            return Frac.multLong( slaveTime - mSlaveSync, mRate.mDen, mRate.mNum ) + mMasterSync;
        }
        if( slaveTime < mSlaveSync ) {
            return Long.MIN_VALUE;
        } else if( slaveTime > mSlaveSync ) {
            return Long.MAX_VALUE;
        } else {
            return mMasterSync;
        }
    }


    public synchronized long convertMasterToSlave( long masterTime ) {
        if( mPlaying ) {
            return Frac.multLong( masterTime - mMasterSync, mRate.mNum, mRate.mDen ) + mSlaveSync;
        } else {
            return mSlaveSync;
        }
    }


    public void combine( PlayState master, PlayState slave ) {
        if( master == null ) {
            if( slave == null ) {
                return;
            } else {
                set( slave );
            }
        } else if( slave == null ) {
            set( master );
        }

        mPlaying = master.mPlaying && slave.mPlaying;
        Frac mr = master.mRate;
        Frac sr = slave.mRate;
        Frac.multFrac( mr.mNum, mr.mDen, sr.mNum, sr.mDen, mRate );
        mMasterSync = slave.mMasterSync;
        mSlaveSync = slave.mSlaveSync;
    }

}
