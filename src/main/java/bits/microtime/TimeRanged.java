/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import java.util.Comparator;

/** 
 * @author Philip DeCamp  
 */
public interface TimeRanged extends TimeStamped {
    
    long startMicros();
    long stopMicros();

    
    Comparator<TimeRanged> STOP_TIME_ORDER = new Comparator<TimeRanged>() {
        public int compare(TimeRanged t1, TimeRanged t2) {
            long v1 = t1.stopMicros();
            long v2 = t2.stopMicros();
            
            return v1 < v2 ? -1:
                   v1 > v2 ?  1:
                   0;
        }
    };

    
    Comparator<TimeRanged> START_STOP_TIME_ORDER = new Comparator<TimeRanged>() {
        public int compare( TimeRanged t1, TimeRanged t2 ) {
            long v1 = t1.startMicros();
            long v2 = t2.startMicros();
            
            if(v1 < v2)
                return -1;
            
            if(v1 > v2)
                return 1;
            
            v1 = t1.stopMicros();
            v2 = t2.stopMicros();
            
            if(v1 < v2)
                return -1;
            
            if(v1 > v2)
                return 1;
            
            return 0;
        }
    };
    
}
