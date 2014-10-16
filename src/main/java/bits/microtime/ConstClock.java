package bits.microtime;

/**
 * @author decamp
 */
public class ConstClock implements Clock {

    private final long mMicros;

    public ConstClock( long micros ) {
        mMicros = micros;
    }

    public long micros() {
        return mMicros;
    }

}
