package cogmac.microtime;

import java.util.Comparator;

/** 
 * @author Philip DeCamp  
 */
public interface Timestamped {
    
    public static final long DISTANT_FUTURE = Long.MAX_VALUE;
    public static final long DISTANT_PAST   = Long.MIN_VALUE;
    
    
    public static final Comparator<Timestamped> TIME_ORDER = new Comparator<Timestamped>() {
        public int compare( Timestamped t1, Timestamped t2 ) {
            long v1 = t1.timestampMicros();
            long v2 = t2.timestampMicros();
            
            return v1 < v2 ? -1 : 
                   v1 > v2 ?  1 :
                   0;
        }
    };
    
    
    public long timestampMicros();
    
    
    @Deprecated public long getTimestampMicros();
    
}
