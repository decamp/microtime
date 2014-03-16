package bits.microtime;

/**
 * @author decamp
 */
public interface AsyncPlayControl extends PlayControl {
    public void playStart();
    public void playStop();
    public void seek( long seekMicros );
    public void setRate( double rate );
}
