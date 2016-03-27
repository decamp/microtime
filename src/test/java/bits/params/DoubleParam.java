/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

/**
 * @author decamp
 */
public class DoubleParam extends AbstractParam<Double> {


    public static DoubleParam newInstance( String name ) {
        return new DoubleParam( name, 0.0 );
    }


    public static DoubleParam newInstance( String name, double initValue ) {
        return new DoubleParam( name, initValue );
    }


    public static DoubleParam newInstance( String name, double initValue, double min, double max ) {
        return new DoubleParam( name, initValue, min, max, 0 );
    }


    public static DoubleParam newInstance( String name, double initValue, double min, double max, double step ) {
        return new DoubleParam( name, initValue, min, max, step );
    }


    private final boolean mHasRange;
    private final double  mMinValue;
    private final double  mMaxValue;
    private final double  mStepValue;


    private DoubleParam( String name, double initValue ) {
        super( Double.class, name, null, ParamWidgetType.TEXT_FIELD, initValue );
        mHasRange = false;
        mMinValue = 0;
        mMaxValue = 0;
        mStepValue = 0;
    }


    private DoubleParam( String name, double initValue, double minValue, double maxValue, double stepValue ) {
        super( Double.class, name, null, ParamWidgetType.SLIDER, initValue );
        mHasRange = true;
        mMinValue = minValue;

        if( stepValue != 0.0 ) {
            double n = Math.round( (maxValue - minValue) / stepValue );
            maxValue = minValue + n * stepValue;
        }

        mMaxValue = maxValue;
        mStepValue = stepValue;
    }


    @Override
    public void setValue( Object source, Double value ) {
        if( value == null ) {
            return;
        }

        double v = value.doubleValue();

        if( mHasRange ) {
            if( mStepValue != 0.0 ) {
                double n = Math.round( (v - mMinValue) / mStepValue );
                v = mMinValue + n * mStepValue;
            }

            if( v < mMinValue ) {
                v = mMinValue;
            } else if( value > mMaxValue ) {
                v = mMaxValue;
            }
        }

        super.setValue( source, v );
    }


    /**
     * @return true iff the value of this parameter has a specified range
     */
    public boolean hasRange() {
        return mHasRange;
    }


    /**
     * DoubleParam may optionally "be stepped" in that the accepted value is
     * forced to be <tt>minValue + n * stepValue</tt>, where <tt>n</tt> is
     * a whole number.
     *
     * @return true iff this parameter has a specified step value.
     */
    public boolean isStepped() {
        return mHasRange && mStepValue != 0;
    }


    /**
     * @return If <tt>hasRange() == true</tt>, the minimum accepted value of this Param. <br/>
     * Otherwise, return value is not defined.
     */
    public double getMinValue() {
        return mMinValue;
    }


    /**
     * @return If <tt>hasRange() == true</tt>, the maximum accepted value of this Param. <br/>
     * Otherwise, return value is not defined.
     */
    public double getMaxValue() {
        return mMaxValue;
    }


    /**
     * @return If <tt>isStepped() == true</tt>, the step value of the Param.  <br/>
     * Otherwise, return value is not defined.
     */
    public double getStepValue() {
        return mStepValue;
    }

}
