package bits.microtime;

/**
 * @author Philip DeCamp
 */
public class ClockState implements SyncClockControl {

    public boolean mPlaying;
    public final Frac mRate = new Frac( 1, 1 );
    public long mTimeBasis;
    public long mMasterBasis;


    @Override
    public void clockStart( long exec ) {
        if( mPlaying ) {
            return;
        }
        mPlaying = true;
        mMasterBasis = exec;
    }

    @Override
    public void clockStop( long exec ) {
        if( !mPlaying ) {
            return;
        }
        updateBases( exec );
        mPlaying = false;
    }

    @Override
    public void clockSeek( long exec, long seek ) {
        mMasterBasis = exec;
        mTimeBasis = seek;
    }

    @Override
    public void clockRate( long exec, Frac rate ) {
        if( mRate.equals( rate ) ) {
            return;
        }
        updateBases( exec );
        mRate.set( rate );
    }


    public long toMaster( long micros ) {
        if( mPlaying ) {
            return Frac.multLong( micros - mTimeBasis, mRate.mDen, mRate.mNum ) + mMasterBasis;
        } else if( micros < mTimeBasis ) {
            return Long.MIN_VALUE;
        } else if( micros > mTimeBasis ) {
            return Long.MAX_VALUE;
        } else {
            return mMasterBasis;
        }
    }


    public long fromMaster( long playMicros ) {
        if( mPlaying ) {
            return Frac.multLong( playMicros - mMasterBasis, mRate.mNum, mRate.mDen ) + mTimeBasis;
        } else {
            return mTimeBasis;
        }
    }


    public void applyTo( SyncClockControl target ) {
        applyTo( mMasterBasis, target );
    }


    public void applyTo( long exec, SyncClockControl target ) {
        long seek = exec == mMasterBasis ? mTimeBasis : fromMaster( exec );
        if( mPlaying ) {
            target.clockRate( exec, mRate );
            target.clockSeek( exec, seek );
            target.clockStart( exec );
        } else {
            target.clockStop( exec );
            target.clockRate( exec, mRate );
            target.clockSeek( exec, seek );
        }
    }



    void updateBases( long exec ) {
        if( mPlaying ) {
            mTimeBasis += Frac.multLong( exec - mMasterBasis, mRate.mNum, mRate.mDen );
        }
        mMasterBasis = exec;
    }

}
