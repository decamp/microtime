/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import java.text.DateFormat;
import java.util.*;

import bits.util.Dates;


/**
 * A time block immutably captures, (surprise!) a block of time. The start and stop time
 * are in MICROSECONDS.  TimeBlocks represent half-open intervals; although in the degenerate
 * case where the start and stop times are equal, the TimeBlock is still considered to 
 * contain the start time, which affects intersect() and contains() operations.  This is in 
 * contrast to standard half-open intervals, where degenerate cases are considered 
 * equivalent to the NULL set.  
 *
 * TimeBlocks are created by factory methods. All times are in MICROSECONDS since the epoch.
 * 
 * TimeBlocks are a drop-in replacement for edu.mit.media.bits.time.TimeBlock, with the
 * caveat that these TimeBlocks may have zero spans.
 * 
 * TimeBlock also provides a number of useful constants:
 *  MINUTE_IN_MILLISECONDS, HOUR_IN_MILLISECONDS, DAY_IN_MILLISECONDS,
 *  WEEK_IN_MILLISECONDS, MONTH_IN_MILLISECONDS, YEAR_IN_MILLISECONDS
 * 
 * @author Philip DeCamp
 * @author Rony Kubat  
 */
public class TimeBlock implements TimeRanged, Comparable<TimeRanged> {
    
    
    private final long mStartMicros;
    private final long mStopMicros;
    
    
    public TimeBlock( long startMicros, long stopMicros ) {
        if( startMicros <= stopMicros ) {
            mStartMicros = startMicros;
            mStopMicros  = stopMicros;
        } else {
            mStartMicros = stopMicros;
            mStopMicros  = startMicros;
        }
    }
    
    
    public TimeBlock( TimeRanged copy ) {
        mStartMicros = copy.startMicros();
        mStopMicros  = copy.stopMicros();
    }
    

    
    public long startMicros() {
        return mStartMicros;
    }
    
    
    public long stopMicros() {
        return mStopMicros;
    }
    
    
    public long spanMicros() {
        return mStopMicros - mStartMicros;
    }
    
    /**
     * Returns true iff the two timeblocks overlap.  Empty TimeBlocks cannot intersect.
     * 
     * @param t
     * @return
     */
    public boolean intersects( TimeRanged t ) {
        long t1 = t.startMicros();
        long t2 = t.stopMicros();
        
        if( t1 >= mStopMicros ) {
            // This is only true if mStartMicros == mStopMicros, in which case
            // mStopMicros IS in the set
            // despite being half-open.
            if( t1 == mStartMicros ) {
                return true;
            } else {
                return false;
            }
        }
        
        if( t2 <= mStartMicros ) {
            // This is only true if t1 == t2, in which case t2 IS in the set,
            // despite being half-open.
            if( t1 == mStartMicros ) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
    
    /**
     * @param micro  time in microseconds
     * @return true iff <code>micro</code> is within bounds of TimeBlock.
     */
    public boolean contains( long micro ) {
        return micro >= mStartMicros && micro < mStopMicros || micro == mStartMicros;
    }
    
    /**
     * @param t
     * @return true iff supplied TimeRanged is completely within this TimeBlock.
     */
    public boolean contains( TimeRanged t ) {
        long t1 = t.startMicros();
        long t2 = t.stopMicros();
        
        if( mStartMicros > t1 || mStopMicros < t2 ) {
            return false;
        }
        
        // THIS is not degenerate and contains the stop point by some margin,
        // OR T is not degenerate, and by the test above THIS is also not degenerate and contains it completely,   
        // OR both THIS and T are degenerate and, by the test above, equivalent.
        return mStopMicros > t2 || t1 < t2 || mStartMicros == mStopMicros;
    }
    
    /**
     * Returns the union of two TimeBlocks as a TimeBlock.
     * @param t
     * @return The union.
     */
    public TimeBlock union( TimeRanged t ) {
        long start = Math.min( t.startMicros(), mStartMicros );
        long stop  = Math.max( t.stopMicros(), mStopMicros );
        return new TimeBlock( start, stop );
    }
    
    /**
     * Returns the intersection of two TimeBlocks as a TimeBlock, or null if they don't intersect.
     * 
     * @param t
     * @return The intersection
     */
    public TimeBlock intersection( TimeRanged t ) {
        long t1 = t.startMicros();
        long t2 = t.stopMicros();
        
        if( t1 >= mStopMicros ) {
            //This is only true if mStartMicros == mStopMicros, in which case mStopMicros IS in the set 
            //despite being half-open.
            if( t1 == mStartMicros ) {
                return new TimeBlock( t1, t1 );
            } else {
                return null;
            }
        }
        
        if( t2 <= mStartMicros ) {
            //This is only true if t1 == t2, in which case t2 IS in the set,
            //despite being half-open.
            if( t1 == mStartMicros ) {
                return new TimeBlock( t1, t1 );
            } else {
                return null;
            }
        }
        
        return new TimeBlock( Math.max( t1, mStartMicros ), Math.min( t2, mStopMicros ) );
    }

    /**
     * Compares this TimeBlock to TimeRanged object first by startMicros, then stopMicros.
     */
    public int compareTo( TimeRanged t ) {
        long a = t.startMicros();
        if( mStartMicros < a ) {
            return -1;
        } else if( mStartMicros > a ) {
            return 1;
        }
        
        a = t.stopMicros();
        return mStopMicros < a ? -1 : 
               mStopMicros > a ?  1 : 0;
    }
    
    
    
    @Override
    public int hashCode() {
        return (int)(mStartMicros ^ mStopMicros);
    }
    

    @Override
    public boolean equals( Object o ) {
        if( !( o instanceof TimeBlock ) ) {
            return false;
        }
        TimeBlock t = (TimeBlock)o;
        return mStartMicros == t.mStartMicros && mStopMicros == t.mStopMicros;
    }

    
    @Override
    public String toString() {
        return toString( Dates.FORMAT_MILLISECOND_UTC );
    }

    
    public String toString( TimeZone tz ) {
        return toString( Dates.formatter( Dates.FORMAT_MILLISECOND, tz ) );
    }

    
    public String toString( DateFormat df ) {
        return String.format( "TimeBlock [%s to %s]",
                              df.format( new Date(mStartMicros / 1000L ) ),
                              df.format( new Date(mStopMicros / 1000L ) ) );
    }

}
