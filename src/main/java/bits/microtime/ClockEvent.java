package bits.microtime;


/**
 * @author Philip DeCamp
 */
public class ClockEvent {

    public static final int CLOCK_START = 1;
    public static final int CLOCK_STOP  = 2;
    public static final int CLOCK_SEEK  = 3;
    public static final int CLOCK_RATE  = 4;


    public static ClockEvent createClockStart( Object source, long exec ) {
        return new ClockEvent( source, CLOCK_START, exec, Long.MIN_VALUE, null );
    }

    public static ClockEvent createClockStop( Object source, long exec ) {
        return new ClockEvent( source, CLOCK_STOP, exec, Long.MIN_VALUE, null );
    }

    public static ClockEvent createClockSeek( Object source, long exec, long seek ) {
        return new ClockEvent( source, CLOCK_SEEK, exec, seek, null );
    }

    public static ClockEvent createClockRate( Object source, long exec, Frac rate ) {
        return new ClockEvent( source, CLOCK_RATE, exec, Long.MIN_VALUE, rate );
    }



    public final Object mSource;
    public final int    mId;
    public final long   mExec;
    public final long   mSeekMicros;
    public final Frac   mRate;


    ClockEvent( Object source, int id, long exec, long seek, Frac rate ) {
        mSource = source;
        mId = id;
        mExec = exec;
        mSeekMicros = seek;
        mRate = rate == null ? null : new Frac( rate );
    }

}
