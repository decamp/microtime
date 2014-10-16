package bits.microtime;

/**
 * @author decamp
 */
public class ManualClock implements Clock {

    private volatile long mMicros;


    public ManualClock() {}


    public ManualClock( long micros ) {
        mMicros = micros;
    }



    public long micros() {
        return mMicros;
    }


    public synchronized void micros( long micros ) {
        mMicros = micros;
        notifyAll();
    }


    @Deprecated public void setMicros( long micros ) {
        micros( micros );
    }


}
