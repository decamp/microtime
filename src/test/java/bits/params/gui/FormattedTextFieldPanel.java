/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import bits.params.Param;
import bits.params.Param.State;
import bits.params.ParamListener;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.*;
import java.util.Date;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
abstract class FormattedTextFieldPanel<T> extends TitledParamPanel implements ParamListener, PropertyChangeListener {
    
    
    static FormattedTextFieldPanel<Double> newDoubleInstance(Param<Double> param) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(1);
        format.setMinimumIntegerDigits(1);
        
        if(format instanceof DecimalFormat) {
            ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(true);
        }
        
        return new FormattedTextFieldPanel<Double>(param, format) {
            protected Double fieldToParamValue(Object obj) {
                return ((Number)obj).doubleValue();
            }
        };
    }

    
    static FormattedTextFieldPanel<Long> newLongInstance(Param<Long> param) {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(0);
        
        
        if(format instanceof DecimalFormat) {
            ((DecimalFormat)format).setDecimalSeparatorAlwaysShown(false);
        }
        
        return new FormattedTextFieldPanel<Long>(param, format) {
            protected Long fieldToParamValue(Object obj) {
                return ((Number)obj).longValue();
            }
        };
    }
    

    static FormattedTextFieldPanel<Date> newDateInstance(Param<Date> param) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        return new FormattedTextFieldPanel<Date>(param, format) {
            protected Date fieldToParamValue(Object object) {
                return (Date)object;
            }
        };
    }

    
    
    private final Param<T> mParam;
    private final JFormattedTextField mField;

    
    private FormattedTextFieldPanel(Param<T> param, Format format) {
        super(param, param.getName());
        mParam = param;
        mField = new JFormattedTextField(format);
        mField.setValue(param.getValue());
        
        add(mField);
        
        mField.addPropertyChangeListener("value", this);
        param.addListenerWeakly(this);
        
        setSize(100, suggestRowHeight() * 2);
    }

    
    
    public void paramStateChanged(Object source, Param param, State state) {
        if(source == this || state != State.ENABLE)
            return;
        
        mField.setEnabled(mParam.isEnabled());
    }

    
    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this)
            return;
        
        mField.setValue(paramToFieldValue(mParam.getValue()));
    }

    
    public void propertyChange(PropertyChangeEvent evt) {
        Object inValue = fieldToParamValue(evt.getNewValue());
        T outValue = fieldToParamValue(inValue);
        
        mParam.setValue(this, fieldToParamValue(evt.getNewValue()));
        
        T outValue2 = mParam.getValue();
        
        if(outValue == outValue2 || outValue != null && outValue.equals(outValue2))
            return;
        
        mField.setValue(paramToFieldValue(outValue2));
    }
    

    
    protected abstract T fieldToParamValue(Object object);

    
    protected Object paramToFieldValue(T value) {
        return value;
    }
    
}
