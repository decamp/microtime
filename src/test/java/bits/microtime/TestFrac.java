/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.*;


/**
 * @author Philip DeCamp
 */
public class TestFrac {

    @Test
    public void testMultFracs() {
        Frac c = new Frac();
        assertTrue( Frac.multFrac( 1, 4, 1, 4, c ) );
        assertEquals( new Frac( 1, 16 ), c );

        assertTrue( Frac.multFrac( 4, 1, 1, 4, c ) );
        assertEquals( new Frac( 1, 1 ), c );

        assertFalse( Frac.multFrac( Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 1, c ) );
        assertEquals( new Frac( 0x7FFFFFFF, 1 ), c );

        assertFalse( Frac.multFrac( Integer.MIN_VALUE, 1, Integer.MAX_VALUE, 1, c ) );
        assertEquals( new Frac( 0x80000001, 1 ), c );

        assertTrue( Frac.multFrac( Integer.MIN_VALUE, 0, Integer.MAX_VALUE, 1, c ) );
        assertEquals( new Frac( -1, 0 ), c );

        assertTrue( Frac.multFrac( Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 1, c ) );
        assertEquals( new Frac( 1, 0 ), c );

        assertTrue( Frac.multFrac( -1, 0, Integer.MAX_VALUE, 1, c ) );
        assertEquals( new Frac( -1, 0 ), c );

        assertTrue( Frac.multFrac( -1, 0, 1, 0, c ) );
        assertEquals( new Frac( -1, 0 ), c );

    }

    @Test
    public void testMultLong() {
        assertEquals( 0, Frac.multLong( 0, 1, 1 ) );
        assertEquals( 0, Frac.multLong( 1, 0, 1 ) );
        assertEquals( 0, Frac.multLong( 0, 1, 0 ) );
        assertEquals( Long.MAX_VALUE, Frac.multLong( 1, 1, 0 ) );
        assertEquals( Long.MIN_VALUE, Frac.multLong( -1, 1, 0 ) );

        int round = Frac.ROUND_NEAR_INF | Frac.ROUND_PASS_MINMAX;
        assertEquals( Long.MIN_VALUE, Frac.multLong( Long.MIN_VALUE, 100, 100, round ) );
        assertEquals( Long.MAX_VALUE, Frac.multLong( Long.MAX_VALUE, 100, 100, round ) );

        round = Frac.ROUND_NEAR_INF;
        Random rand = new Random( 15 );

        for( int i = 0; i < 10000; i++ ) {
            long va;
            int vb, vc;
            switch( i % 3 ) {
            default: va = rand.nextLong(); break;
            }

            switch( i / 3 % 3 ) {
            case 0: vb = rand.nextInt() >> 16; break;
            case 1: vb = Short.MIN_VALUE; break;
            default: vb = Short.MAX_VALUE; break;
            }

            switch( i / 3 % 3 ) {
            case 0: vc = rand.nextInt() >> 16; break;
            case 1: vc = Short.MIN_VALUE; break;
            default: vc = Short.MAX_VALUE; break;
            }

            round = randRound( rand );
            long a0 = Frac.multLong( va, vb, vc, round );
            BigInteger a1 = bigMult( va, vb, vc, round );

            assertEquals( a1.longValue(), a0 );
        }
    }

    @Ignore @Test
    public void testMultLong2() {
        assertEquals( 0, Frac.multLong( 0, 1, 1 ) );
        assertEquals( 0, Frac.multLong( 1, 0, 1 ) );
        assertEquals( 0, Frac.multLong( 0, 1, 0 ) );
        assertEquals( Long.MAX_VALUE, Frac.multLong( 1, 1, 0 ) );
        assertEquals( Long.MIN_VALUE, Frac.multLong( -1, 1, 0 ) );

        int round = Frac.ROUND_NEAR_INF | Frac.ROUND_PASS_MINMAX;
        assertEquals( Long.MIN_VALUE, Frac.multLong( Long.MIN_VALUE, 100, 100, round ) );
        assertEquals( Long.MAX_VALUE, Frac.multLong( Long.MAX_VALUE, 100, 100, round ) );

        round = Frac.ROUND_NEAR_INF;
        Random rand = new Random( 15 );

        for( int i = 0; i < 3 * 3 * 3; i++ ) {
            long va;
            int vb;
            int vc;

            switch( i % 3 ) {
            case 0: va = rand.nextLong(); break;
            case 1: va = Long.MIN_VALUE; break;
            default: va = Long.MAX_VALUE; break;
            }

            switch( i / 3 % 3 ) {
            case 0: vb = rand.nextInt(); break;
            case 1: vb = Integer.MIN_VALUE; break;
            default: vb = Integer.MAX_VALUE; break;
            }

            switch( i / 9 % 3 ) {
            case 0: vc = rand.nextInt(); break;
            case 1: vc = Integer.MIN_VALUE; break;
            default: vc = Integer.MAX_VALUE; break;
            }

            //round = randRound( rand );
            round = Frac.ROUND_DOWN;

            long a0 = Frac.multLong( va, vb, vc, round );
            BigInteger a1 = bigMult( va, vb, vc, round );

            System.out.println( va + " * " + vb + " / " + vc + " = " + "\t" + ( a0 - a1.longValue() ) + "\t" + a0 + " ### " + a1.longValue() );

//            if( a1.longValue() != a0 ) {
//                System.out.println( a0 );
//                System.out.println( a1.longValue() );
//                System.out.println( a1 );
//                System.out.println( round );
//                a1 = bigMult( va, vb, vc, round );
//                a0 = Rational.multLong( va, vb, vc, round );
//            }
//            assertEquals( a1.longValue(), a0 );
        }
    }

    @Test
    public void testGcd() {
        assertEquals( 4, Frac.gcd( 4 * 7, 4 * 13 ) );
        assertEquals( 4, Frac.gcd( 4 * 13, 4 * 7 ) );
        assertEquals( 2, Frac.gcd( 2, Long.MIN_VALUE ) );
        assertEquals( 2, Frac.gcd( Long.MIN_VALUE, Long.MAX_VALUE - 1 ) );
        assertEquals( 2, Frac.gcd( Long.MAX_VALUE - 1, Long.MIN_VALUE ) );
        assertEquals( Long.MAX_VALUE, Frac.gcd( Long.MAX_VALUE, Long.MIN_VALUE + 1 ) );
        assertEquals( Long.MIN_VALUE, Frac.gcd( Long.MIN_VALUE, Long.MIN_VALUE ) );
        assertEquals( Long.MAX_VALUE, Frac.gcd( Long.MAX_VALUE, Long.MAX_VALUE ) );
    }

    @Test
    public void testToDouble() {
        assertTrue( Double.isNaN( new Frac( 0, 0 ).toDouble() ) );
        assertTrue( Double.POSITIVE_INFINITY == new Frac( 1, 0 ).toDouble() );
        assertTrue( Double.NEGATIVE_INFINITY == new Frac( -1, 0 ).toDouble() );
    }

    @Test
    public void testReduce() {
        Frac out = new Frac();

        // Normal operation.
        Frac.reduce( 4, 8, Integer.MAX_VALUE, out );
        assertCanonical( 1, 2, out );
        Frac.reduce( 8, 4, Integer.MAX_VALUE, out );
        assertCanonical( 2, 1, out );
        Frac.reduce( 137, 48, Integer.MAX_VALUE, out );
        assertCanonical( 137, 48, out );
        Frac.reduce( 137, 48, 32, out );
        assertCanonical( 20, 7, out );
        Frac.reduce( 48, 137, 32, out );
        assertCanonical( 7, 20, out );

        // Special cases
        assertTrue( Frac.reduce( 10000, 0, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 0, out );
        assertTrue( Frac.reduce( -10000, 0, Integer.MAX_VALUE, out ) );
        assertCanonical( -1, 0, out );
        assertTrue( Frac.reduce( 0, 0, Integer.MAX_VALUE, out ) );
        assertCanonical( 0, 0, out );

        // Bit limit tests.
        assertFalse( Frac.reduce( Long.MAX_VALUE, Long.MAX_VALUE - 1, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 1, out );
        assertFalse( Frac.reduce( Long.MIN_VALUE, Long.MIN_VALUE + 1, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 1, out );
        assertTrue( Frac.reduce( Long.MIN_VALUE, Long.MIN_VALUE >> 1, Integer.MAX_VALUE, out ) );
        assertCanonical( 2, 1, out );
        assertTrue( Frac.reduce( Long.MIN_VALUE >> 1, Long.MIN_VALUE, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 2, out );
        assertFalse( Frac.reduce( Long.MAX_VALUE >> 1, Long.MIN_VALUE, Integer.MAX_VALUE, out ) );
        assertCanonical( -1, 2, out );
        assertFalse( Frac.reduce( Long.MIN_VALUE >> 1, Long.MAX_VALUE, Integer.MAX_VALUE, out ) );
        assertCanonical( -1, 2, out );

        assertTrue( Frac.reduce( Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 1, out );
        assertTrue( Frac.reduce( Integer.MAX_VALUE, 1, Integer.MAX_VALUE, out ) );
        assertCanonical( Integer.MAX_VALUE, 1, out );
        assertTrue( Frac.reduce( Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, out ) );
        assertCanonical( 1, 1, out );
        assertFalse( Frac.reduce( Integer.MIN_VALUE, 1, Integer.MAX_VALUE, out ) );
        assertCanonical( -Integer.MAX_VALUE, 1, out );
        assertFalse( Frac.reduce( Integer.MIN_VALUE, 3, Integer.MAX_VALUE, out ) );
        assertCanonical( -1431655765, 2, out );
    }

    @Test
    public void testDoubleToRational() {
        Random rand = new Random( 10 );
        Frac ra = new Frac();

        Frac.doubleToRational( Double.NEGATIVE_INFINITY, ra );
        assertCanonical( -1, 0, ra );

        Frac.doubleToRational( Double.NaN, ra );
        assertCanonical( 0, 0, ra );

        Frac.doubleToRational( Double.POSITIVE_INFINITY, ra );
        assertCanonical( 1, 0, ra );

        Frac.doubleToRational( 0.0, ra );
        assertCanonical( 0, 1, ra );

        Frac.doubleToRational( 0.5, ra );
        assertCanonical( 1, 2, ra );

        Frac.doubleToRational( -0.5, ra );
        assertCanonical( -1, 2, ra );

        Frac.doubleToRational( Math.PI, ra );
        assertCanonical( 1881244168, 598818617, ra );
    }

    @Test
    public void testPrint() {
        System.out.println( Frac.multLong( Long.MAX_VALUE, Integer.MAX_VALUE, 1 ) );
    }


    static void assertCanonical( int a, int b, Frac rat ) {
        assertTrue( rat.isCanonical() );
        assertEquals( new Frac( a, b ), rat );
    }



    static BigInteger bigMult( long a, long b, long c, int round ) {
        BigInteger ia = BigInteger.valueOf( a );
        BigInteger ib = BigInteger.valueOf( b );
        BigInteger ic = BigInteger.valueOf( c );
        BigInteger[] vals = ia.multiply( ib ).divideAndRemainder( ic );

        if( vals[1].longValue() == 0 ) {
            return vals[0];
        }

        boolean negative = a < 0 ^ b < 0 ^ c < 0;

        switch( round ) {
        case Frac.ROUND_ZERO:
            return vals[0];

        case Frac.ROUND_NEAR_INF: {
            BigInteger halfc = BigInteger.valueOf( Math.abs( c / 2 ) );
            int cmp = vals[1].abs().compareTo( halfc );
            if( cmp < 0 || c == 0 && (c & 1) == 0 ) {
                return vals[0];
            }

            // Remainder is more than half of divisor. Round up
            // Fallthrough
        }
        case Frac.ROUND_INF:
            if( !negative ) {
                return vals[0].add( BigInteger.ONE );
            } else {
                return vals[0].subtract( BigInteger.ONE );
            }

        case Frac.ROUND_DOWN:
            return !negative ? vals[0] : vals[0].subtract( BigInteger.ONE );

        case Frac.ROUND_UP:
            return !negative ? vals[0].add( BigInteger.ONE ) : vals[0];
        }

        throw new IllegalArgumentException( "Invalid rounding type.");
    }


    static int randRound( Random rand ) {
        int v = rand.nextInt( 5 );
        switch( rand.nextInt( 5 ) ) {
        case 0:
            return Frac.ROUND_ZERO;
        case 1:
            return Frac.ROUND_INF;
        case 2:
            return Frac.ROUND_DOWN;
        case 3:
            return Frac.ROUND_UP;
        case 4:
        default:
            return Frac.ROUND_NEAR_INF;
        }
    }


    static byte[] longToBytes( long v ) {
        return new byte[]{ (byte)( v >>> 56 ),
                           (byte)( v >>> 48 ),
                           (byte)( v >>> 40 ),
                           (byte)( v >>> 32 ),
                           (byte)( v >>> 24 ),
                           (byte)( v >>> 16 ),
                           (byte)( v >>>  8 ),
                           (byte)( v ) };
    }
}
