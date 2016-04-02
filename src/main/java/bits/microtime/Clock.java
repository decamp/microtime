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

    long micros();


    /**
     * Monotonic timer.
     */
    Clock HOST_CLOCK = new Clock() {
        @Override
        public long micros() {
            return System.nanoTime() / 1000L;
        }
    };

    /**
     * Provides access to absolute time since Unix epoch. May not be monotonic if, for example, time is corrected.
     */
    Clock ABSOLUTE_CLOCK = new Clock() {
        public long micros() {
            return System.currentTimeMillis() * 1000L;
        }
    };

    @Deprecated
    Clock SYSTEM_CLOCK = ABSOLUTE_CLOCK;

}
