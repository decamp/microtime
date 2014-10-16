package bits.microtime;

/**
 * @author Philip DeCamp
 */
public interface PlayControl {
    public void playStart( long execMicros );
    public void playStop( long execMicros );
    public void seek( long execMicros, long gotoMicros );
    public void setRate( long execMicros, double rate );
}
