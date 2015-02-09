/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

import bits.params.*;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class LongSliderParamPanel extends TitledParamPanel implements ChangeListener, ParamListener {


    private static final Font TICK_FONT = new Font( "Verdana", Font.PLAIN, 10 );


    private final LongParam mParam;
    private final JSlider   mSlider;


    LongSliderParamPanel( LongParam param ) {
        super( param, param.getName() );

        mParam = param;
        mSlider = new JSlider( (int)param.getMinValue(), (int)param.getMaxValue(), param.getValue().intValue() );
        mSlider.setPaintLabels( true );

        long min = param.getMinValue();
        long max = param.getMaxValue();

        mSlider.setPaintTicks( true );
        mSlider.setMajorTickSpacing( (int)((max - min) / 2) );
        mSlider.setFont( TICK_FONT );

        add( mSlider );

        mSlider.addChangeListener(this);
        mParam.addListenerWeakly(this);
        
        setSize(100, suggestRowHeight() * 3);
    }

    
    
    public void stateChanged(ChangeEvent e) {
        mParam.setValue(this, (long)mSlider.getValue());
    }
    
    
    @Override
    public void paramStateChanged(Object source, Param param, Param.State state) {
        if(source == this)
            return;
        
        mSlider.setEnabled(mParam.isEnabled());
    }

    
    @Override
    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this || value == null)
            return;
        
        mSlider.setValue(((Long)value).intValue());
    }
    
}
