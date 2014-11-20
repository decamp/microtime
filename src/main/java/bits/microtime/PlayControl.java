/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

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
