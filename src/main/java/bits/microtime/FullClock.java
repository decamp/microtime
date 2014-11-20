/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

/**
 * @author decamp
 */
public class FullClock implements PlayClock, AsyncPlayControl {

    private final Clock mMasterClock;

    private boolean mPlaying;
    private long mMasterSyncMicros;
    private long mSyncMicros;
    private double mRate;


    public FullClock() {
        this( null, null );
    }


    public FullClock( Clock clock ) {
        this( clock, null );
    }


    public FullClock( Clock clock, PlayClock init ) {
        mMasterClock = (clock == null ? Clock.SYSTEM_CLOCK : clock);

        if( init == null ) {
            mPlaying = false;
            mMasterSyncMicros = mMasterClock.micros();
            mSyncMicros = 0;
            mRate = 1.0;
        } else {
            mPlaying = init.isPlaying();
            mMasterSyncMicros = init.masterSyncMicros();
            mSyncMicros = init.syncMicros();
            mRate = init.rate();
        }
    }



    //////// PlayControl methods //////// 

    @Override
    public synchronized void playStart( long execMicros ) {
        if( mPlaying ) {
            return;
        }

        mPlaying = true;
        mMasterSyncMicros = execMicros;
    }

    @Override
    public synchronized void playStop( long execMicros ) {
        if( !mPlaying ) {
            return;
        }

        mPlaying = false;
        mSyncMicros += (long)((execMicros - mMasterSyncMicros) * mRate + 0.5);
        mMasterSyncMicros = execMicros;
    }

    @Override
    public synchronized void seek( long execMicros, long seekMicros ) {
        mMasterSyncMicros = execMicros;
        mSyncMicros = seekMicros;
    }

    @Override
    public synchronized void setRate( long execMicros, double rate ) {
        if( rate == mRate ) {
            return;
        }

        if( mPlaying ) {
            mSyncMicros += (long)((execMicros - mMasterSyncMicros) * mRate + 0.5);
            mMasterSyncMicros = execMicros;
        }

        mRate = rate;
    }


    //////// AsyncPlayControl methods //////// 
    
    @Override
    public synchronized void playStart() {
        playStart( masterMicros() );
    }

    @Override
    public synchronized void playStop() {
        playStop( masterMicros() );
    }

    @Override
    public synchronized void seek( long seekMicros ) {
        seek( masterMicros(), seekMicros );
    }

    @Override
    public synchronized void setRate( double rate ) {
        setRate( masterMicros(), rate );
    }


    //////// PlayClock methods //////// 
    
    @Override
    public boolean isPlaying() {
        return mPlaying;
    }

    @Override
    public synchronized long micros() {
        return fromMasterMicros( masterMicros() );
    }

    @Override
    public synchronized long masterMicros() {
        return mMasterClock.micros();
    }

    @Override
    public long masterSyncMicros() {
        return mMasterSyncMicros;
    }

    @Override
    public long syncMicros() {
        return mSyncMicros;
    }

    @Override
    public double rate() {
        return mRate;
    }

    @Override
    public synchronized long toMasterMicros( long dataMicros ) {
        if( mPlaying ) {
            return (long)((dataMicros - mSyncMicros) / mRate + 0.5) + mMasterSyncMicros;
        }


        if( dataMicros < mSyncMicros ) {
            return Long.MIN_VALUE;

        } else if( dataMicros > mSyncMicros ) {
            return Long.MAX_VALUE;

        } else {
            return mMasterSyncMicros;
        }
    }

    @Override
    public synchronized long fromMasterMicros( long playMicros ) {
        if( mPlaying ) {
            return (long)((playMicros - mMasterSyncMicros) * mRate + 0.5) + mSyncMicros;
        } else {
            return mSyncMicros;
        }
    }

    @Override
    public Clock masterClock() {
        return mMasterClock;
    }


    @Override
    public synchronized void applyTo( PlayControl control ) {
        if( mPlaying ) {
            control.setRate( mMasterSyncMicros, mRate );
            control.seek( mMasterSyncMicros, mSyncMicros );
            control.playStart( mMasterSyncMicros );
        } else {
            control.playStop( mMasterSyncMicros );
            control.setRate( mMasterSyncMicros, mRate );
            control.seek( mMasterSyncMicros, mSyncMicros );
        }
    }

}
