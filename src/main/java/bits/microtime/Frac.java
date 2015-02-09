package bits.microtime;

import static java.lang.Math.*;


/**
 * Rational numbers.
 *
 * Most of the original code by: Michael Niedermayer <michaelni@gmx.at>
 * Ported by: Philip DeCamp
 */
public class Frac implements Comparable<Frac> {

    public static final int ROUND_ZERO        = 0; ///< Round toward zero.
    public static final int ROUND_INF         = 1; ///< Round away from zero.
    public static final int ROUND_DOWN        = 2; ///< Round toward -infinity.
    public static final int ROUND_UP          = 3; ///< Round toward +infinity.
    public static final int ROUND_NEAR_INF    = 5; ///< Round to nearest and halfway cases away from zero.
    public static final int ROUND_PASS_MINMAX = 8192; ///< Flag to pass INT64_MIN/MAX through instead of rescaling, this avoids special cases for AV_NOPTS_VALUE


    /**
     * Computes greatest common divisor of two terms using Euclid's method.
     *
     * @param a Arbitrary number
     * @param b Arbitrary number
     * @return Largest number that evenly divides both a and b. <br>
     *         This number will be non-negative with one exception: <br>
     *         If a == Integer.MIN_VALUE && b == Integer.MIN_VALUE, then Integer.MIN_VALUE will be returned.
     */
    public static int gcd( int a, int b ) {
        while( b != 0 ) {
            int c = a % b;
            a = b;
            b = c;
        }
        return a >= 0 ? a : -a;
    }

    /**
     * Computes greatest common divisor of two terms using Euclid's method.
     *
     * @param a Arbitrary number
     * @param b Arbitrary number
     * @return Largest number that evenly divides both a and b. <br>
     *         This number will be non-negative with one exception: <br>
     *         If a == Long.MIN_VALUE && b == Long.MIN_VALUE, then Long.MIN_VALUE will be returned.
     */
    public static long gcd( long a, long b ) {
        while( b != 0 ) {
            long c = a % b;
            a = b;
            b = c;
        }
        return a >= 0 ? a : -a;
    }

    /**
     * Equivalent to calling {@code reduce(mNum, mDen, Integer.MAX_VALUE, out) }.
     *
     * @see #reduce( long, long, int, Frac)
     */
    public static boolean reduce( long num, long den, Frac out ) {
        return reduce( num, den, Integer.MAX_VALUE, out );
    }

    /**
     * Reduces a ratio such that it is in canonical format AND
     * numerator and denominator are below a specified maximum mVal.
     *
     * @param num Numerator of ratio
     * @param den Denominator of ratio
     * @param max Max mVal of numerator and denominator. {@code max > 0 } or results are undefined.
     * @param out Receives canonical, reduced ratio on output.
     * @return true iff answer is exact, false iff rounded.
     *
     * @see #isCanonical(int,int)
     */
    public static boolean reduce( long num, long den, int max, Frac out ) {
        assert( max > 0 );

        // -Inf, NaN, Inf, and 0 cases.
        if( den == 0 ) {
            if( num < 0 ) {
                out.mNum = -1;
            } else if( num == 0 ) {
                out.mNum = 0;
            } else {
                out.mNum = 1;
            }
            out.mDen = 0;
            return true;
        }

        if( num == 0 ) {
            out.mNum = 0;
            out.mDen = 1;
            return true;
        }

        long maxLong = max;
        long prevNum = 0;
        long prevDen = 1;
        long thisNum = 1;
        long thisDen = 0;
        boolean exact = true;

        // Preserve mSign info and move to absolute values.
        boolean negate = ( num < 0 ) ^ ( den < 0 );
        long gcd = gcd( abs( num ), abs( den ) );
        if( gcd > 1 ) {
            num = abs( num / gcd );
            den = abs( den / gcd );
        } else if( gcd != Long.MIN_VALUE ) {
            if( num != Long.MIN_VALUE && den != Long.MIN_VALUE ) {
                num = abs( num );
                den = abs( den );
            } else {
                // Can't negate Long.MIN_VALUE.
                // Check if bit is lost, then shift.
                exact = ((num | den) & 1) == 0;
                num = abs( num >> 1 );
                den = abs( den >> 1 );
            }
        } else {
            // If gcd == Long.MIN_VALUE, then mNum == Long.MIN_VALUE && mDen == Long.MIN_VALUE.
            out.mNum = 1;
            out.mDen = 1;
            return true;
        }

        // Check if approximation is needed.
        if( num <= maxLong && den <= maxLong ) {
            out.mNum = (int)( negate ? -num : num );
            out.mDen = (int)den;
            return exact;
        }

        while( den != 0 ) {
            long x        = num / den;
            long next_den = num - den * x;
            long nextNum  = x * thisNum + prevNum;
            long nextDen  = x * thisDen + prevDen;

            if (nextNum > maxLong || nextDen > maxLong) {
                if( thisNum != 0 ) x =         ( maxLong - prevNum ) / thisNum  ;
                if( thisDen != 0 ) x = min( x, (maxLong - prevDen) / thisDen );
                if( den * (2 * x * thisDen + prevDen) > num * thisDen ) {
                    thisNum = ( x * thisNum + prevNum );
                    thisDen = ( x * thisDen + prevDen );
                }
                break;
            }

            prevNum = thisNum;
            prevDen = thisDen;
            thisNum = nextNum;
            thisDen = nextDen;
            num = den;
            den = next_den;
        }

        out.mNum = (int)( negate ? -thisNum : thisNum );
        out.mDen = (int)thisDen;
        return false;
    }

    /**
     * Every rational, {@code mNum/mDen}, mVal has a single canonical representation that meets the
     * following criteria:
     *
     * <ul>
     * <li>mDen >= 0 [Sign of number determined by numerator]
     * <li>If mNum == 0, then mDen == 1. [Canonical zero is 0/1]
     * <li>If mDen == 0, then mNum is in set { -1, 0, 1 }. [Canonical -inf = -1/0, NaN = 0/0, inf = 1/0]
     * <li>The greatest common divisor of the numerator and denomanator is 1.
     * </ul>
     *
     * Note that given these rules, fractions of the form {@code x / Integer.MIN_VALUE} cannot be made
     * canonical without rounding.
     *
     * @param num Numerator
     * @param den Denominator
     * @return true iff {@code mNum/mDen} is in canonical form.
     */
    public static boolean isCanonical( int num, int den ) {
        if( den == 0 ) {
            return num >= -1 && num <= 1;
        }
        if( num == 0 ) {
            return den == 1;
        }
        if( gcd( num, den ) != 1 ) {
            return false;
        }
        return num != Integer.MIN_VALUE && den != Integer.MIN_VALUE;
    }

    /**
     * Equivalent to calling {@code multLong( a, b, c, ROUND_NEAR_INF )}.
     *
     * TODO: This method works pretty well, but for very large numbers may give incorrect answer.
     *
     * Rescale a 64-bit integer with rounding to nearest.
     * A simple a * b / c isn't possible as it can overflow.
     * @param val Value
     * @param num Numerator
     * @param den Denominator
     */
    public static long multLong( long val, int num, int den ) {
        return multLong( val, num, den, ROUND_NEAR_INF );
    }

    /**
     * TODO: This method works fairly well, but for very large numbers may not give the correct answer.
     *
     * Rescale a 64-bit integer with rounding to nearest.
     * A simple a * b / c isn't possible as it can overflow.
     * @param val Value
     * @param num Numerator
     * @param den Denominator
     * @param rnd Rounding method, from Rational.ROUND_*.
     */
    public static long multLong( long val, int num, int den, int rnd ) {
        if( den == 0 ) {
            if( val == 0 || num == 0 ) {
                return 0;
            }
            return (val < 0) ^ (num < 0) ? Long.MIN_VALUE : Long.MAX_VALUE;
        }

        if( val == 0 || num == 0 ) {
            return 0;
        }

        if( ( rnd & ROUND_PASS_MINMAX) != 0 ) {
            if( val == Long.MIN_VALUE || val == Long.MAX_VALUE ) {
                return val;
            }
            rnd -= ROUND_PASS_MINMAX;
        }

        // Try to reduce numbers as much as possible.
        long gcd = gcd( val, den );
        val /= gcd;
        den /= gcd;
        gcd = gcd( num, den );
        num /= gcd;
        den /= gcd;

        long numLong = abs((long)num);
        long denLong = abs((long)den);
        boolean negative = num < 0 ^ den < 0;

        if( val < 0 ) {
            negative = !negative;
            val = val != Long.MIN_VALUE ? -val : Long.MAX_VALUE;
        }

        if( negative ) {
            rnd ^= (rnd >> 1) & 1;
        }

        long r = 0;
        if( rnd == ROUND_NEAR_INF ) {
            r = denLong / 2;
        } else if( ( rnd & 1 ) != 0 ) {
            r = denLong - 1;
        }

        long result;
        if( numLong < Integer.MAX_VALUE && denLong < Integer.MAX_VALUE ) {
            if( val < Integer.MAX_VALUE ) {
                result = (val * numLong + r) / denLong;
            } else {
                result = val / denLong * numLong + (val % denLong * numLong + r) / denLong;
            }
        } else {
            long a0  = val & 0xFFFFFFFFL;
            long a1  = val >> 32;
            long b0  = numLong & 0xFFFFFFFFL;
            long b1  = numLong >>> 32;
            long t1  = a0 * b1 + a1 * b0;
            long t1a = t1 << 32;
            int i;

            a0 = a0 * b0 + t1a;
            a1 = a1 * b1 +  ( t1 >>> 32 ) + ( a0 < t1a ? 1 : 0 );
            a0 += r;
            a1 += ( a0 < r ? 1 : 0 );

            for( i = 63; i >= 0; i-- ) {
                a1 += a1 + ((a0 >> i) & 1);
                t1 += t1;
                if (denLong <= a1) {
                    a1 -= denLong;
                    t1++;
                }
            }
            result = t1;
        }

        return negative ? -result : result;

    }

    /**
     * Multiply two rationals.
     *
     * @param aNum Numerator of first rational
     * @param aDen Denominator of first rational.
     * @param bNum Numerator of second rational
     * @param bDen Denominator of second rational.
     * @param out Receives answer b * c.
     * @return true iff result is exact
     */
    public static boolean multFrac( int aNum, int aDen, int bNum, int bDen, Frac out ) {
        return reduce( aNum * (long)bNum,
                       aDen * (long)bDen,
                       Integer.MAX_VALUE,
                       out );
    }

    /**
     * Add two rationals.
     *
     * @param aNum Numerator of first rational
     * @param aDen Denominator of first rational.
     * @param bNum Numerator of second rational
     * @param bDen Denominator of second rational.
     * @param out Receives answer b * c.
     * @return true iff result is exact
     */
    public static boolean addQ( int aNum, int aDen, int bNum, int bDen, Frac out ) {
        return reduce( aNum * (long)bDen + bNum * (long)aDen,
                       aDen * (long)bDen,
                       Integer.MAX_VALUE,
                       out );
    }

    /**
     * Compare two rationals.
     * @param aNum Numerator of first rational
     * @param aDen Denominator of first rational.
     * @param bNum Numerator of second rational
     * @param bDen Denominator of second rational.
     * @return 0 if a==b or if one of the values is of the form 0/0, 1 if a > b, -1 if a < b.
     */
    public static int cmpQ( int aNum, int aDen, int bNum, int bDen ) {
        long tmp = aNum * (long)bDen - bNum * (long)aDen;
        if( tmp != 0 ) {
            return (int)((tmp ^ aDen ^ bDen) >> 63) | 1;
        } else if( bDen != 0 && aDen != 0 ) {
            return 0;
        } else if( aNum != 0 && bNum != 0 ) {
            return (aNum >> 31) - (bNum >> 31);
        } else {
            return 0;
        }
    }


    public static void doubleToRational( double d, Frac out ) {
        doubleToRational( d, Integer.MAX_VALUE, out );
    }

    /**
     * Convert a double precision floating point number to a rational.
     * inf is expressed as {1,0} or {-1,0} depending on the mSign.
     *
     * @param d double to convert
     * @param max the maximum allowed numerator and denominator
     * @param out receives rational version of {@code d} on return.
     */
    public static void doubleToRational( double d, int max, Frac out ) {
        long den;
        if( Double.isNaN( d ) ) {
            out.mNum = 0;
            out.mDen = 0;
            return;
        }

        if( Math.abs( d ) > Integer.MAX_VALUE + 3L ) {
            out.mNum = d < 0 ? -1 : 1;
            out.mDen = 0;
            return;
        }

        int exponent = max( Math.getExponent( d ), 0 );
        den = 1L << ( 61 - exponent );
        reduce( (long)floor( d * den + 0.5 ), den, max, out );
        if( ( out.mNum == 0 || out.mDen == 0 ) && d != 0 && max > 0 && max < Integer.MIN_VALUE ) {
            reduce( (long)floor( d * den + 0.5 ), den, Integer.MAX_VALUE, out );
        }
    }



    public int mNum;
    public int mDen;


    public Frac() {}


    public Frac( int num, int den ) {
        mNum = num;
        mDen = den;
    }


    public Frac( Frac copy ) {
        set( copy );
    }



    public void set( Frac r ) {
        mNum = r.mNum;
        mDen = r.mDen;
    }


    public boolean isCanonical() {
        return isCanonical( mNum, mDen );
    }


    public boolean reduce() {
        return reduce( mNum, mDen, Integer.MAX_VALUE, this );
    }




//    public static long rescale( long a, long b, long c ) {
//        return rescale_rnd( a, b, c, ROUND_NEAR_INF );
//    }

//    public static long rescale_q_rnd( long a, Rational bq, Rational cq,  int rnd ) {
//        long b = bq.mNum * (long)cq.mDen;
//        long c = cq.mNum * (long)bq.mDen;
//        return rescale_rnd( a, b, c, rnd );
//    }

//    public static long rescale_q( long a, Rational bq, Rational cq ) {
//        return rescale_q_rnd( a, bq, cq, ROUND_NEAR_INF );
//    }

//    public static int compare_ts( long ts_a, Rational tb_a, long ts_b, Rational tb_b ) {
//        long a = tb_a.mNum * (long)tb_b.mDen;
//        long b = tb_b.mNum * (long)tb_a.mDen;
//        if( ( abs( ts_a ) | a | abs( ts_b ) | b ) <= Integer.MAX_VALUE ) {
//            return ( ts_a * a > ts_b * b ? 1 : 0 ) - ( ts_a * a < ts_b * b ? 1 : 0 );
//        }
//        if( rescale_rnd( ts_a, a, b, ROUND_DOWN ) < ts_b ) {
//            return -1;
//        }
//        if( rescale_rnd( ts_b, b, a, ROUND_DOWN ) < ts_a ) {
//            return 1;
//        }
//        return 0;
//    }



//    public static long rescale_delta( Rational in_tb, long in_ts,  Rational fs_tb, int duration, long[] last, Rational out_tb ) {
//        long a, b, self;
//        assert( in_ts != 0x8000000000000000L );
//        assert( duration >= 0 );
//
//        while( true ) {
//            if( last[0] == 0x8000000000000000L || duration == 0 || in_tb.mNum * (long)out_tb.mDen <= out_tb.mNum * (long)in_tb.mDen ) {
//                //simple_round:
//                last[0] = rescale_q( in_ts, in_tb, fs_tb ) + duration;
//                return rescale_q( in_ts, in_tb, out_tb );
//            }
//
//            a = rescale_q_rnd( 2 * in_ts - 1, in_tb, fs_tb, ROUND_DOWN ) >> 1;
//            b = (rescale_q_rnd( 2 * in_ts + 1, in_tb, fs_tb, ROUND_UP ) + 1) >> 1;
//            if( last[0] >= 2 * a - b && last[0] <= 2 * b - a ) {
//                break;
//            }
//        }
//
//        self = clip( last[0], a, b );
//        last[0] = self + duration;
//        return rescale_q( self, fs_tb, out_tb );
//    }

//    public static long add_stable( Rational ts_tb, long ts, Rational inc_tb, long inc ) {
//        long m, d;
//        if( inc != 1 ) {
//            multFrac( inc_tb, new Rational( (int)inc, 1 ), inc_tb );
//        }
//
//        m = inc_tb.mNum * (long)ts_tb.mDen;
//        d = inc_tb.mDen * (long)ts_tb.mNum;
//
//        if (m % d == 0)
//            return ts + m / d;
//        if (m < d)
//            return ts;
//
//        {
//            long old = rescale_q( ts, ts_tb, inc_tb);
//            long old_ts = rescale_q( old, inc_tb, ts_tb);
//            return rescale_q( old + 1, inc_tb, ts_tb) + (ts - old_ts);
//        }
//    }


//    public static int main( ) {
//        Rational a,b,r;
//        for( a.mNum = -2; a.mNum <= 2; a.mNum++ ) {
//            for (a.mDen = -2; a.mDen <= 2; a.mDen++) {
//                for (b.mNum = -2; b.mNum <= 2; b.mNum++) {
//                    for (b.mDen = -2; b.mDen <= 2; b.mDen++) {
//                        int c = cmp_q( a,b );
//                        double d = q2d(a) == q2d(b) ?
//                                0 : (q2d(a) - q2d(b));
//                        if (d > 0)       d = 1;
//                        else if (d < 0)  d = -1;
//                        else if (d != d) d = Integer.MIN_VALUE;
//                        if (c != d)
//                            av_log(NULL, AV_LOG_ERROR, "%d/%d %d/%d, %d %f\n", a.mNum,
//                                   a.mDen, b.mNum, b.mDen, c,d);
//                        r = av_sub_q(av_add_q(b,a), b);
//                        if(b.mDen && (r.mNum*a.mDen != a.mNum*r.mDen || !r.mNum != !a.mNum || !r.mDen != !a.mDen))
//                            av_log(NULL, AV_LOG_ERROR, "%d/%d ", r.mNum, r.mDen);
//                    }
//                }
//            }
//        }
//
//        for (a.mNum = 1; a.mNum <= 10; a.mNum++) {
//            for (a.mDen = 1; a.mDen <= 10; a.mDen++) {
//                if (av_gcd(a.mNum, a.mDen) > 1)
//                    continue;
//                for (b.mNum = 1; b.mNum <= 10; b.mNum++) {
//                    for (b.mDen = 1; b.mDen <= 10; b.mDen++) {
//                        int start;
//                        if (av_gcd(b.mNum, b.mDen) > 1)
//                            continue;
//                        if (av_cmp_q(b, a) < 0)
//                            continue;
//                        for (start = 0; start < 10 ; start++) {
//                            int acc= start;
//                            int i;
//
//                            for (i = 0; i<100; i++) {
//                                int exact = start + av_rescale_q(i+1, b, a);
//                                acc = av_add_stable(a, acc, b, 1);
//                                if (FFABS(acc - exact) > 2) {
//                                    av_log(NULL, AV_LOG_ERROR, "%d/%d %d/%d, %d %d\n", a.mNum,
//                                           a.mDen, b.mNum, b.mDen, acc, exact);
//                                    return 1;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return 0;
//    }

    public double toDouble() {
        if( mDen != 0 ) {
            return (double)mNum / mDen;
        }
        return mNum == 0 ? Double.NaN : mNum < 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }



    @Override
    public String toString() {
        return String.format( "%d/%d", mNum, mDen );
    }

    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof Frac) ) {
            return false;
        }
        Frac r = (Frac)obj;
        return mNum == r.mNum && mDen == r.mDen;
    }

    @Override
    public int hashCode() {
        return mNum ^ mDen;
    }

    @Override
    public int compareTo( Frac r ) {
        return cmpQ( mNum, mDen, r.mNum, r.mDen );
    }


}
