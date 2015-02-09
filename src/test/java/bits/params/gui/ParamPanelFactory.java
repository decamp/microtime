/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import java.util.*;

import bits.params.*;


/**
 * Generates ParamPanel objects for given Param.
 * 
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
public final class ParamPanelFactory {
    
    @SuppressWarnings("unchecked")
    public static ParamPanel newParamPanel(Param param) {
        ParamWidgetType suggest = param.getWidgetType();
        Class<?> clazz          = param.getValueClass();
        
        if(suggest == ParamWidgetType.NONE) {
            return null;
        }
        
        if(suggest == ParamWidgetType.LABEL) {
            return new LabelParamPanel(param);
        }
        
        if(param instanceof ListParam ) {
            if(suggest == ParamWidgetType.RADIO_BUTTON) {
                return new RadioButtonPanel((ListParam)param);
            }
            
            return new ComboBoxPanel((ListParam)param);
        }
        
        if(clazz == Boolean.class) {
            return new CheckBoxPanel(param);
        }
        
        if(clazz == Long.class) {
            if( suggest == ParamWidgetType.TEXT_FIELD || 
                !(param instanceof LongParam)         ||
                !((LongParam)param).hasRange()) 
            {
                return FormattedTextFieldPanel.newLongInstance(param);
            }
            
            return new LongSliderParamPanel((LongParam)param);
        }
        
        if(clazz == Double.class) {
            if( suggest == ParamWidgetType.TEXT_FIELD || 
                !(param instanceof DoubleParam)       ||
                !((DoubleParam)param).hasRange())
            {
                return FormattedTextFieldPanel.newDoubleInstance(param);
            }
            
            return new DoubleSliderParamPanel((DoubleParam)param);
        }

        if(clazz == String.class) {
            return new TextFieldPanel(param);
        }
        
        if(clazz == Date.class) {
            return FormattedTextFieldPanel.newDateInstance(param);
        }
        
        if(param instanceof CompoundParam ) {
            CompoundParam cp = (CompoundParam)param;
            List<ParamPanel> list = new ArrayList<ParamPanel>();
            
            for(int i = 0; i < cp.getParamCount(); i++) {
                ParamPanel panel = newParamPanel(cp.getParam(i));
                if(panel != null) {
                    list.add(panel);
                }
            }
            
            return new CompoundParamPanel(cp, list, true);
        }
        
        return new LabelParamPanel(param);
        
    }
    
    private ParamPanelFactory() {}
    
}
