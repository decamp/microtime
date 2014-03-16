package bits.microtime;

/**
 * @author decamp
 */
public class ConstPlayClock implements PlayClock {

    private final boolean mPlaying;

    private final long mMasterMicros;
    private final long mMicros;
    private final long mMasterSyncMicros;
    private final long mSyncMicros;

    private final double mRate;

    private Clock mMasterClock = null;


    public ConstPlayClock( PlayClock copy ) {
        mPlaying = copy.isPlaying();
        mMasterMicros = copy.masterMicros();
        mMicros = copy.micros();
        mMasterSyncMicros = copy.masterSyncMicros();
        mSyncMicros = copy.syncMicros();
        mRate = copy.rate();
    }

    public ConstPlayClock( boolean playing,
                           long micros,
                           long masterMicros,
                           long syncMicros,
                           long masterSyncMicros,
                           double rate )
    {
        mPlaying = playing;
        mMasterMicros = masterMicros;
        mMicros = micros;
        mMasterSyncMicros = masterSyncMicros;
        mSyncMicros = syncMicros;
        mRate = rate;
    }



    public boolean isPlaying() {
        return mPlaying;
    }

    public long masterMicros() {
        return mMasterMicros;
    }

    public long micros() {
        return mMicros;
    }

    public long syncMicros() {
        return mSyncMicros;
    }

    public long masterSyncMicros() {
        return mMasterSyncMicros;
    }

    public long toMasterMicros( long dataMicros ) {
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

    public long fromMasterMicros( long playMicros ) {
        if( mPlaying ) {
            return (long)((playMicros - mMasterSyncMicros) * mRate + 0.5) + mSyncMicros;
        } else {
            return mSyncMicros;
        }
    }

    public double rate() {
        return mRate;
    }

    public Clock masterClock() {
        Clock ret = mMasterClock;

        if( ret == null ) {
            ret = new ConstClock( mMasterMicros );
            mMasterClock = ret;
        }

        return ret;
    }

    public void applyTo( PlayControl control ) {
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
