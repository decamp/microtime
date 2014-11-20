/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

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
