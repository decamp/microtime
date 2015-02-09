/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

/**
 * Basic interface for a clock service.
 *
 * @author Philip DeCamp
 */
public interface Clock {

    public static final Clock SYSTEM_CLOCK = new Clock() {
        public long micros() {
            return System.currentTimeMillis() * 1000L;
        }
    };

    public long micros();

}
