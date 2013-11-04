package cogmac.microtime;

import java.util.Comparator;

/** 
 * @author Philip DeCamp  
 */
public interface TimeStamped {
    
    public static final Comparator<TimeStamped> START_TIME_ORDER = new Comparator<TimeStamped>() {
        public int compare( TimeStamped t1, TimeStamped t2 ) {
            long v1 = t1.startMicros();
            long v2 = t2.startMicros();
            return v1 < v2 ? -1 : 
                   v1 > v2 ?  1 :
                   0;
        }
    };
    
    public long startMicros();
    
}
