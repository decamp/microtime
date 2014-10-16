package bits.microtime;

/**
 * Like PlayControl, but does not require syncronization micros for each command.
 * This makes AsyncPlayControl simpler to use in instances where timings do not
 * need to be exact.
 *
 * @author decamp
 */
public interface AsyncPlayControl extends PlayControl {
    public void playStart();
    public void playStop();
    public void seek( long seekMicros );
    public void setRate( double rate );
}
