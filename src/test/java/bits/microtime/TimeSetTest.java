package bits.microtime;

import java.util.*;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * @author Philip DeCamp
 */
public class TimeSetTest {

    public TimeSetTest() {}

    @Test
    public void testIntersect() {
        TimeSet set = new TimeSet();

        set.add( 100, 200 );
        set.add( 300, 400 );
        set.add( 500, 600 );

        TimeBlock tb;
        List<TimeBlock> list = set.intersect( 150, 550 );

        assertEquals( 3, list.size() );

        tb = list.get( 0 );
        assertEquals( 150L, tb.startMicros() );
        assertEquals( 200L, tb.stopMicros() );

        tb = list.get( 1 );
        assertEquals( 300L, tb.startMicros() );
        assertEquals( 400L, tb.stopMicros() );

        tb = list.get( 2 );
        assertEquals( 500L, tb.startMicros() );
        assertEquals( 550L, tb.stopMicros() );


        list = set.intersect( 50, 650 );
        assertEquals( 3, list.size() );

        tb = list.get( 0 );
        assertEquals( 100L, tb.startMicros() );
        assertEquals( 200L, tb.stopMicros() );

        tb = list.get( 1 );
        assertEquals( 300L, tb.startMicros() );
        assertEquals( 400L, tb.stopMicros() );

        tb = list.get( 2 );
        assertEquals( 500L, tb.startMicros() );
        assertEquals( 600L, tb.stopMicros() );


        list = set.intersect( 0, 50 );
        assertEquals( 0, list.size() );

        list = set.intersect( 550, 560 );
        assertEquals( 1, list.size() );
        tb = list.get( 0 );
        assertEquals( 550L, tb.startMicros() );
        assertEquals( 560L, tb.stopMicros() );
    }

    @Test
    public void testSubtractFrom() {
        TimeSet set = new TimeSet();

        set.add( 100, 200 );
        set.add( 300, 400 );
        set.add( 500, 600 );

        List<TimeBlock> list = set.subtractFrom( new TimeBlock( 150, 550 ) );
        assertEquals( 2, list.size() );

        TimeBlock tb = list.get( 0 );
        assertEquals( 200L, tb.startMicros() );
        assertEquals( 300L, tb.stopMicros() );
        tb = list.get( 1 );
        assertEquals( 400L, tb.startMicros() );
        assertEquals( 500L, tb.stopMicros() );

        list = set.subtractFrom( new TimeBlock( 50, 650 ) );
        assertEquals( 4, list.size() );

        tb = list.get( 0 );
        assertEquals( 50L, tb.startMicros() );
        assertEquals( 100L, tb.stopMicros() );

        tb = list.get( 1 );
        assertEquals( 200L, tb.startMicros() );
        assertEquals( 300L, tb.stopMicros() );

        tb = list.get( 2 );
        assertEquals( 400L, tb.startMicros() );
        assertEquals( 500L, tb.stopMicros() );

        tb = list.get( 3 );
        assertEquals( 600L, tb.startMicros() );
        assertEquals( 650L, tb.stopMicros() );

        list = set.subtractFrom( new TimeBlock( 120, 160 ) );
        assertEquals( 0, list.size() );

        list = set.subtractFrom( new TimeBlock( 1000, 2000 ) );
        assertEquals( 1, list.size() );

        tb = list.get( 0 );
        assertEquals( 1000L, tb.startMicros() );
        assertEquals( 2000L, tb.stopMicros() );

    }

}
