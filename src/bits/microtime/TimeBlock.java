package bits.microtime;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
 * TimeBlocks are a drop-in replacement for edu.mit.media.cogmac.time.TimeBlock, with the
 * caveat that these TimeBlocks may have zero spans.
 * 
 * TimeBlock also provides a number of useful constants:
 *  MINUTE_IN_MILLISECONDS, HOUR_IN_MILLISECONDS, DAY_IN_MILLISECONDS,
 *  WEEK_IN_MILLISECONDS, MONTH_IN_MILLISECONDS, YEAR_IN_MILLISECONDS
 * 
 * @author Philip DeCamp
 * @author Rony Kubat  
 */
public class TimeBlock implements TimeRanged {
    
    
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
     * @param mictro  time in MICROSECONDS
     * @return true iff <code>micro</code> is within bounds of TimeBlock.
     */
    public boolean contains( long micro ) {
        return micro >= mStartMicros && micro < mStopMicros || micro == mStartMicros;
    }
    
    /**
     * @param t
     * @return true iff supplied TimeBlock is completely within this TimeBlock.
     */
    public boolean contains(final TimeRanged t) {
        long t1 = t.startMicros();
        long t2 = t.stopMicros();
        
        if(mStartMicros > t1 || mStopMicros < t2)
            return false;
    
        //THIS is not degenerate and contains the stop point by some margin,
        //OR T is not degenerate, and by the test above THIS is also not degenerate and contains it completely,   
        //OR both THIS and T are degenerate and, by the test above, equivalent.
        return mStopMicros > t2 || t1 < t2 || mStartMicros == mStopMicros;
    }
    
    /**
     * Returns the union of two TimeBlocks as a TimeBlock.
     * @param t
     * @return The union.
     */
    public TimeBlock union( final TimeRanged t ) {
        final long start = Math.min( t.startMicros(), mStartMicros );
        final long stop  = Math.max( t.stopMicros(), mStopMicros );
        return new TimeBlock( start, stop );
    }
    
    /**
     * Returns the intersection of two TimeBlocks as a TimeBlock, or null if they don't intersect.
     * 
     * @param t
     * @return The intersection
     */
    public TimeBlock intersection( final TimeRanged t ) {
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

    
    
    @Override
    public int hashCode() {
        return (int)(mStartMicros ^ mStopMicros);
    }
    

    @Override
    public boolean equals( Object o ) {
        if( !( o instanceof TimeBlock ) )
            return false;
        
        TimeBlock t = (TimeBlock)o;
        return mStartMicros == t.mStartMicros && mStopMicros == t.mStopMicros;
    }

    
    public String toString( TimeZone tz ) {
        DateFormat df = TimeUtil.formatter( "yyyy-MM-dd HH:mm:ss.SSS", tz );
        return String.format( "TimeBlock [%s to %s]",
                df.format( new Date(mStartMicros / 1000L ) ),
                df.format( new Date(mStopMicros / 1000L ) ) );
    }
    
    
    @Override
    public String toString() {
        return toString( TimeZone.getDefault() );
    }
    



    
    @Deprecated public static final long MINUTE_IN_MILLISECONDS = 60 * 1000;
    @Deprecated public static final long HOUR_IN_MILLISECONDS   = MINUTE_IN_MILLISECONDS * 60;
    @Deprecated public static final long DAY_IN_MILLISECONDS    = HOUR_IN_MILLISECONDS * 24;
    @Deprecated public static final long WEEK_IN_MILLISECONDS   = DAY_IN_MILLISECONDS * 7;
    @Deprecated public static final long MONTH_IN_MILLISECONDS  = DAY_IN_MILLISECONDS * 30;
    @Deprecated public static final long YEAR_IN_MILLISECONDS   = (long) (DAY_IN_MILLISECONDS * 365.2425);
    
    @Deprecated public static final long MINUTE_IN_MICROSECONDS = MINUTE_IN_MILLISECONDS * 1000;
    @Deprecated public static final long HOUR_IN_MICROSECONDS   = MINUTE_IN_MICROSECONDS * 60;
    @Deprecated public static final long DAY_IN_MICROSECONDS    = HOUR_IN_MICROSECONDS * 24;
    @Deprecated public static final long WEEK_IN_MICROSECONDS   = DAY_IN_MICROSECONDS * 7;
    @Deprecated public static final long MONTH_IN_MICROSECONDS  = DAY_IN_MICROSECONDS * 30;
    @Deprecated public static final long YEAR_IN_MICROSECONDS   = (long) (DAY_IN_MICROSECONDS * 365.2425);
    

    @Deprecated public boolean containsMicro(final long micro) {
        //The last term (micro == mStartMicros) is to check for cases when
        //THIS is degenerate and mStartMicros == mStopMicros.
        return micro >= mStartMicros && micro < mStopMicros || micro == mStartMicros;
    }

    @Deprecated public boolean containsMicroT(final long t) {
        return containsMicro(t);
    }
    
    /**
     * @return The start time of the TimeBlock in MICROSECONDS since the epoch.
     */
    @Deprecated public long getStartMicros() {
        return mStartMicros;
    }
    
    /**
     * @return The end time of the TimeBlock in MICROSECONDS since the epoch.
     */
    @Deprecated public long getStopMicros() {
        return mStopMicros;
    }

    
    /**
     * Returns the duration of time in MICROSECONDS that the TimeBlock spans.
     * @return The length of time spanned in MICROSECONDS.
     */
    @Deprecated public long getSpanMicros() {
        return mStopMicros - mStartMicros;
    }
        
    
    /**
     * @return The start time of the TimeBlock in MILLISECONDS since the epoch.
     */
    @Deprecated public long getStartMillis() {
        return mStartMicros / 1000;
    }


    /**
     * @return The start time of the TimeBlock as a Date object.
     */
    @Deprecated public Date getStart() {
        return new Date(mStartMicros / 1000);
    }

    
    /**
     * @return The end time of the TimeBlock in MILLISECONDS since the epoch.
     */
    @Deprecated public long getStopMillis() {
        return mStopMicros / 1000;
    }

    
    /**
     * @return The end time of the TimeBlock as a Date object
     */
    @Deprecated public Date getStop() {
        return new Date(mStopMicros / 1000);
    }

    
    /**
     * @return The midpoint time (in MICROSECONDS) between the TimeBlock's 
     * start and end.
     */
    @Deprecated public long getMidpointMicros() {        
        return mStartMicros + (mStopMicros - mStartMicros) / 2;
    }
    
    
    /**
     * @return The midpoint time (in MILLISECONDS) between the TimeBlock's 
     * start and end.
     */
    @Deprecated public long getMidpointMillis() {        
        return getMidpointMicros() / 1000;
    }
    
    
    /**
     * Returns the duration of time in MILLISECONDS that the TimeBlock spans.
     * @return The length of time spanned in MILLISECONDS.
     */
    @Deprecated public long getSpanMillis() {
        return (mStopMicros - mStartMicros) / 1000;
    }

    
    /**
     * Returns the duration of time in SECONDS that the TimeBlock spans.
     * @return The length of time spanned in SECONDS
     */
    @Deprecated public double getSpanSeconds() {
        return (mStopMicros - mStartMicros) / 1000000.0;
    }
    
    
    /**
     * @param micro
     * @param tz (if null, defaults to US/Eastern)
     */
    @Deprecated public static TimeBlock forDayContainingMicro( long micro, TimeZone tz ) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(micro/ 1000L);
        cal.setTimeZone(tz);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final long start = cal.getTimeInMillis() * 1000L;
        
        return TimeBlock.fromMicros(start, start + DAY_IN_MICROSECONDS);
    }
    

    @Deprecated public static TimeBlock forMinuteContainingMicro(long micro) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(micro / 1000L);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final long start = cal.getTimeInMillis() * 1000L;
        
        return TimeBlock.fromMicros(start, start + TimeUnit.SECONDS.toMicros(60));
    }
    
    /**
     * Creates a new time block given a range in month and year.  Inclusive on both ends.
     * @param startMonth
     * @param startYear
     * @param stopMonth
     * @param stopYear
     * @param tz (if null, defaults to US/Eastern)
     */
    @Deprecated public static TimeBlock fromMonthAndYear(int startMonth, int startYear, int stopMonth, int stopYear, TimeZone tz) {
        if (null == tz)
            tz = TimeUtil.US_EASTERN_ZONE;
        Calendar c = Calendar.getInstance();
        c.setTimeZone(tz);
        c.set(Calendar.YEAR,startYear);
        c.set(Calendar.MONTH, startMonth);
        long b1 = TimeUtil.roundDownDateToMonth(c.getTime(), tz).getTime();
        
        
        c.set(Calendar.YEAR,stopYear);
        c.set(Calendar.MONTH, stopMonth);
        long b2 = TimeUtil.roundUpDateToMonth(c.getTime(), tz).getTime();
        
        return fromMillis(b1, b2);
    }
    
    /**
     * Returns a TimeBlock, making an educated guess whether the start
     * and stop times are in milliseconds, microseconds, or nanoseconds. 
     * @param startInMillisMicrosOrNannos
     * @param stopInMillisMicrosOrNannos
     * @return
     */
    @Deprecated public static TimeBlock fromNumbers(long startInMillisMicrosOrNanos, long stopInMillisMicrosOrNanos) {
        final long start = TimeUtil.timestampFromNumber(startInMillisMicrosOrNanos);
        final long stop  = TimeUtil.timestampFromNumber(stopInMillisMicrosOrNanos);
        return new TimeBlock(start,stop);
    }
    
    @Deprecated public static TimeBlock fromMicros(long startMicros, long stopMicros) {
        return new TimeBlock(startMicros, stopMicros);
    }
    
    @Deprecated public static TimeBlock fromMillis(long startMillis, long stopMillis) {
        return new TimeBlock(startMillis * 1000L, stopMillis * 1000L);
    }
    
    @Deprecated
    public static TimeBlock fromTimeRanged(TimeRanged tr) {
        return new TimeBlock(tr.getStartMicros(), tr.getStopMicros());
    }

    
}
