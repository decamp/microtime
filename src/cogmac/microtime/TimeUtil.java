package cogmac.microtime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A class for time utilities.
 * @author kubat
 *
 */
public class TimeUtil {

    public static final String FORMAT_SECOND               = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_MILLISECOND          = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_SECOND_TIMEZONE      = "yyyy-MM-dd HH:mm:ss z";
    public static final String FORMAT_MILLISECOND_TIMEZONE = "yyyy-MM-dd HH:mm:ss.SSS z";
    
    
    public static DateFormat formatter( String format, TimeZone tz ) {
        SimpleDateFormat result = new SimpleDateFormat( format );
        result.setTimeZone( tz );
        return result;
    }
    
    public static DateFormat localFormatter( String format ) {
        return formatter( format, TimeZone.getDefault() );
    }
    
    public static DateFormat secondFormatter( TimeZone tz ) {
        return formatter( FORMAT_SECOND_TIMEZONE, tz );
    }
    
    public static DateFormat millisecondFormatter( TimeZone tz ) {
        return formatter( FORMAT_MILLISECOND_TIMEZONE, tz );
    }
    
    
    
    
    @Deprecated public static final TimeZone ZONE_UTC         = TimeZone.getTimeZone( "UTC" );
    @Deprecated public static final TimeZone ZONE_US_PACIFIC  = TimeZone.getTimeZone( "US/Pacific" );
    @Deprecated public static final TimeZone ZONE_US_MOUNTAIN = TimeZone.getTimeZone( "US/Mountain" );
    @Deprecated public static final TimeZone ZONE_US_CENTRAL  = TimeZone.getTimeZone( "US/Central" );
    @Deprecated public static final TimeZone ZONE_US_EASTERN  = TimeZone.getTimeZone( "US/Eastern" );
    
    @Deprecated public static final String     SECOND_FORMAT_STRING           = "yyyy-MM-dd HH:mm:ss";
    @Deprecated public static final String     MILLISECOND_FORMAT_STRING      = "yyyy-MM-dd HH:mm:ss.SSS";
    @Deprecated public static final String     SECOND_FORMAT_STRING_W_TZ      = "yyyy-MM-dd HH:mm:ss z";
    @Deprecated public static final String     MILLISECOND_FORMAT_STRING_W_TZ = "yyyy-MM-dd HH:mm:ss.SSS z";
    @Deprecated public static final TimeZone   US_EASTERN_ZONE                = TimeZone.getTimeZone("US/Eastern");
    @Deprecated public static final TimeZone   US_CENTRAL_ZONE                = TimeZone.getTimeZone("US/Central");
    @Deprecated public static final TimeZone   US_MOUNTAIN_ZONE               = TimeZone.getTimeZone("US/Mountain");
    @Deprecated public static final TimeZone   US_PACIFIC_ZONE                = TimeZone.getTimeZone("US/Pacific");

 
    /**
     * Use roundDownDateToDay(Date, TimeZone)
     * This version uses US/Eastern
     */
    @Deprecated public static Date roundDownDateToDay(Date d) {
        return roundDownDateToDay(d, US_EASTERN_ZONE);
    }
    
    @Deprecated public static long hoursInMicros(double hours) {
        return (long) (hours * 60 * 60 * 1000 * 1000);
    }
    
    @Deprecated public static long minutesInMicros(double minutes) {
        return (long) (minutes * 60 * 1000 * 1000);
    }
    
    @Deprecated public static long secondsInMicros(double seconds) {
        return (long) (seconds * 1000 * 1000);
    }

    
    @Deprecated public static <T extends TimeRanged> T earlierStart(T a, T b) {
        if (TimeRanged.START_STOP_TIME_ORDER.compare(a, b) <= 0)
            return a;
        
        return b;
    }
    
    @Deprecated public static <T extends TimeRanged> T laterStart(T a, T b) {
        if (TimeRanged.START_STOP_TIME_ORDER.compare(a, b) > 0)
            return a;
        
        return b;
    }

    @Deprecated public static <T extends TimeRanged> T earlierStop(T a, T b) {
        int v = TimeRanged.STOP_TIME_ORDER.compare(a, b);
        
        if (v < 0) return a;
        if (v > 0) return b;
        
        v = TimeRanged.START_TIME_ORDER.compare(a, b);
        
        if (v < 0) return a;
        if (v > 0) return b;
        
        return a;
    }
    
    @Deprecated public static <T extends TimeRanged> T laterStop(T a, T b) {
        int v = TimeRanged.STOP_TIME_ORDER.compare(a, b);
        
        if (v > 0) return a;
        if (v < 0) return b;
        
        v = TimeRanged.START_TIME_ORDER.compare(a, b);
        
        if (v > 0) return a;
        if (v < 0) return b;
        
        return b;
    }
    

    @Deprecated public static long timestampFromNumber(long timeAsMicrosMillisOrNanos) {
        if (timeAsMicrosMillisOrNanos < lower) {
            return timestampFromNumber(timeAsMicrosMillisOrNanos * 1000);
        } else if (timeAsMicrosMillisOrNanos > upper) {
            return timestampFromNumber(timeAsMicrosMillisOrNanos / 1000);
        } else {
            return timeAsMicrosMillisOrNanos;
        }
    }
    
    /**
     * Use roundDownDateToMonth(Date, TimeZone)
     * This version uses US/Eastern
     */
    @Deprecated public static Date roundDownDateToMonth(Date d) {
        return roundDownDateToMonth(d, US_EASTERN_ZONE);
    }
    
    @Deprecated public static Date roundDownDateToMonth(Date d, TimeZone tz) {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(d);
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        tempCal.setTimeZone(tz);
        return roundDownDateToDay(tempCal.getTime(), tz);
    }

    /**
     * Use roundUpDateToMonth(Date, TimeZone)
     * This version uses US/Eastern
     */
    @Deprecated public static Date roundUpDateToMonth(Date d) {
        return roundUpDateToMonth(d, US_EASTERN_ZONE);
    }    
    
    @Deprecated public static Date roundUpDateToMonth(Date d, TimeZone tz) {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(d);
        tempCal.set(Calendar.DAY_OF_MONTH, tempCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        tempCal.setTimeZone(tz);
        return roundDownDateToDay(tempCal.getTime(), tz);
    }
    
    @Deprecated public static Date roundDownDateToDay(Date d, TimeZone tz) {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(d);
        tempCal.set(Calendar.HOUR_OF_DAY, 0);
        tempCal.set(Calendar.MINUTE, 0);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        tempCal.setTimeZone(tz);
        return tempCal.getTime();
    }

    /**
     * Use roundUpDateToDay(Date, TimeZone)
     * This version uses US/Eastern
     */
    @Deprecated public static Date roundUpDateToDay(Date d) {
        return roundUpDateToDay(d, US_EASTERN_ZONE);
    }
    
    @Deprecated public static Date roundUpDateToDay(Date d, TimeZone tz) {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(d);
        tempCal.set(Calendar.HOUR_OF_DAY, 0);
        tempCal.set(Calendar.MINUTE, 0);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        tempCal.add(Calendar.DAY_OF_YEAR, 1);
        tempCal.setTimeZone(tz);
        return tempCal.getTime();        
    }

    @Deprecated public static Date roundDownDateToMinute(Date d) {
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(d);
        tempCal.set(Calendar.SECOND, 0);
        tempCal.set(Calendar.MILLISECOND, 0);
        return tempCal.getTime();        
    }

 
    /**
     * Default timezone is set to US/Eastern.
     * @deprecated Use SECOND_FORMAT_TZ
     */
    @Deprecated public static final DateFormat SECOND_FORMAT      = new SimpleDateFormat( SECOND_FORMAT_STRING );

    /**
     * Default timezone is set to US/Eastern.
     * @deprecated Use MILLISECOND_FORMAT_TZ instead.
     */
    @Deprecated public static final DateFormat MILLISECOND_FORMAT = new SimpleDateFormat( MILLISECOND_FORMAT_STRING);    
    
    /**
     * Sets the time zone for TimeUtil.SECOND_FORMAT and TimeUtil.MILLISECOND_FORMAT
     * @param tz
     */
    @Deprecated public static void setDateFormatTimeZone(TimeZone tz) {
        if (null == tz) return;
        
        SECOND_FORMAT.setTimeZone(tz);
        MILLISECOND_FORMAT.setTimeZone(tz);
    }
    
    
    static {
        SECOND_FORMAT.setTimeZone( ZONE_US_EASTERN );
        MILLISECOND_FORMAT.setTimeZone( ZONE_US_EASTERN );
    }
    
    
    private static final long lower;
    private static final long upper;    
    
    static {   
        long tempLower = 0;
        long tempUpper = 0;
        try {
            tempLower = (new SimpleDateFormat("yyyy")).parse("2000").getTime() * 1000;
            tempUpper = (new SimpleDateFormat("yyyy")).parse("2050").getTime() * 1000;
        } catch (ParseException e) { }
        lower = tempLower;
        upper = tempUpper;
        assert(lower != 0);
        assert(upper != 0);
        assert(lower < upper);
    }

}