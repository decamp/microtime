/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import bits.params.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Hashtable;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class DoubleSliderParamPanel extends TitledParamPanel implements ChangeListener, ParamListener {


    private static final Font TICK_FONT = new Font( "Verdana", Font.PLAIN, 10 );

    private final DoubleParam mParam;
    private final JSlider     mSlider;

    private final double mValMin;
    private final double mValMax;
    private final int    mStepMin;
    private final int    mStepMax;


    DoubleSliderParamPanel( DoubleParam param ) {
        super( param, param.getName() );

        mParam = param;

        mValMin = param.getMinValue();
        mValMax = param.getMaxValue();
        double step = param.getStepValue();
        boolean snap = false;
        Hashtable<Integer, Component> labels = new Hashtable<Integer, Component>();

        if( step > 0 ) {
            mStepMin = 0;
            mStepMax = (int)Math.round( (mValMax - mValMin) / step );
        } else {
            mStepMin = 0;
            mStepMax = 1000;
        }

        labels.put( mStepMin, new JLabel( String.format( "%.1f", mValMin ) ) );
        labels.put( (mStepMin + mStepMax) / 2, new JLabel( String.format( "%.1f", (mValMin + mValMax) * 0.5 ) ) );
        labels.put( mStepMax, new JLabel( String.format( "%.1f", mValMax ) ) );

        for( Component comp : labels.values() ) {
            comp.setFont( TICK_FONT );
        }

        mSlider = new JSlider( mStepMin, mStepMax, valueToStep( param.getValue() ) );
        mSlider.setPaintTicks( true );
        mSlider.setMajorTickSpacing( (mStepMax - mStepMin) / 2 );
        mSlider.setLabelTable( labels );
        mSlider.setSnapToTicks( snap );
        mSlider.setPaintLabels( true );

        add( mSlider );

        mSlider.addChangeListener( this );
        mParam.addListenerWeakly( this );

        setSize( 100, suggestRowHeight() * 3 );
    }



    @Override
    public void stateChanged( ChangeEvent e ) {
        mParam.setValue( this, stepToValue( mSlider.getValue() ) );
    }


    @Override
    public void paramStateChanged( Object source, Param param, Param.State state ) {
        if( source == this ) {
            return;
        }
        mSlider.setEnabled( mParam.isEnabled() );
    }


    @Override
    public void paramValueChanged( Object source, Param param, Object value ) {
        if( source == this || value == null ) {
            return;
        }
        mSlider.setValue( valueToStep( (Double)value ) );
    }



    private double stepToValue( int step ) {
        return (step - mStepMin) * (mValMax - mValMin) / (mStepMax - mStepMin) + mValMin;
    }


    private int valueToStep( double value ) {
        value = (value - mValMin) * (mStepMax - mStepMin) / (mValMax - mValMin) + mStepMin;
        return (int)Math.round( value );
    }

}
