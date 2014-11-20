/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

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
