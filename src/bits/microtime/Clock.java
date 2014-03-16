package bits.microtime;

/**
 * @author decamp
 */
public interface Clock {

    public static final Clock SYSTEM_CLOCK = new Clock() {
        public long micros() {
            return System.currentTimeMillis() * 1000L;
        }
    };
    
    public long micros();
    
}
