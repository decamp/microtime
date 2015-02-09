/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

/**
 * @author decamp
 */
public class LongParam extends AbstractParam<Long> {

    
    public static LongParam newInstance(String name) {
        return new LongParam(name, 0);
    }
    
    
    public static LongParam newInstance(String name, long initValue) {
        return new LongParam(name, initValue);
    }
    
    
    public static LongParam newInstance(String name, long initValue, long minValue, long maxValue) {
        return new LongParam(name, initValue, minValue, maxValue);
    }
    
    
    
    private final boolean mHasRange;
    private final long mMinValue;
    private final long mMaxValue;
    
    
    private LongParam(String name, long initValue) {
        super(Long.class, name, null, ParamWidgetType.TEXT_FIELD, initValue);
        mHasRange = false;
        mMinValue = 0;
        mMaxValue = 0;
    }

    
    private LongParam(String name, long initValue, long minValue, long maxValue) {
        super(Long.class, name, null, ParamWidgetType.SLIDER, initValue);
        mHasRange  = true;
        mMinValue  = minValue;
        mMaxValue  = maxValue;
    }
    
    

    @Override
    public void setValue(Object source, Long value) {
        if(value == null)
            return;
    
        if(mHasRange) {
            if(value < mMinValue) {
                value = mMinValue;
            }else if(value > mMaxValue) {
                value = mMaxValue;
            }
        }
        
        super.setValue(source, value);
    }

    /**
     * @return true iff the value of this parameter has a specified range
     */
    public boolean hasRange() {
        return mHasRange;
    }

    /**
     * @return If <tt>hasRange() == true</tt>, the minimum accepted value of this Param. <br/>
     *         Otherwise, return value is not defined.
     */
    public long getMinValue() {
        return mMinValue;
    }

    /**
     * @return If <tt>hasRange() == true</tt>, the maximum accepted value of this Param. <br/>
     *         Otherwise, return value is not defined.
     */
    public long getMaxValue() {
        return mMaxValue;
    }

}
