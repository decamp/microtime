/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import bits.params.*;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * @author decamp
 */
@SuppressWarnings( {"unchecked","rawtypes" } )
class ComboBoxPanel extends TitledParamPanel implements ItemListener, ParamListener {


    private final ListParam mParam;
    private final JComboBox mBox;


    ComboBoxPanel( ListParam param ) {
        super( param, param.getName() );

        mParam = param;
        mBox = new JComboBox( param.getValueList().toArray() );
        mBox.setSelectedItem( param.getValue() );

        add( mBox );
        setSize( 100, suggestRowHeight() * 2 );

        mParam.addListener( this );
        mBox.addItemListener( this );
    }


    public void itemStateChanged( ItemEvent e ) {
        mParam.setValue( this, mBox.getSelectedItem() );
    }


    public void paramStateChanged(Object source, Param param, Param.State state) {
        if(source == this)
            return;
        
        mBox.setEnabled(mParam.isEnabled());
    }


    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this)
            return;
        
        mBox.setSelectedItem(value);
    }
    
}
