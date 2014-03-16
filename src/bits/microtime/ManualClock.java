package bits.microtime;

/**
 * @author decamp
 */
public class ManualClock implements Clock {
    
    private volatile long mMicros;
    
    
    public ManualClock() {}
    
    public ManualClock(long micros) {
        mMicros = micros;
    }


    public synchronized void setMicros(long micros) {
        mMicros = micros;
        notifyAll();
    }
    
    public long micros() {
        return mMicros;
    }
    
}
