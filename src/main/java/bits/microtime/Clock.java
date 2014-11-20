/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

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
