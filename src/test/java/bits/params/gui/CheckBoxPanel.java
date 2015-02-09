/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import bits.params.Param;
import bits.params.ParamListener;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class CheckBoxPanel extends ParamPanel implements ItemListener, ParamListener {


    private final Param<Boolean> mParam;
    private final JCheckBox      mBox;


    CheckBoxPanel( Param<Boolean> param ) {
        mParam = param;
        mBox = new JCheckBox( param.getName(), param.getValue() );

        setSize( 100, suggestRowHeight() * 2 );
        setLayout( new FillLayout() );
        add( mBox );

        mBox.addItemListener( this );
        mParam.addListenerWeakly( this );
    }


    public void paramStateChanged( Object source, Param param, Param.State state ) {
        if( source == this )
            return;

        mBox.setEnabled(mParam.isEnabled());
    }

    
    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this || value == null)
            return;
        
        mBox.setSelected(mParam.getValue());
    }

    
    public void itemStateChanged(ItemEvent e) {
        mParam.setValue(this, mBox.isSelected());
    }
    
}
