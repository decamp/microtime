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
import java.awt.event.*;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class TextFieldPanel<T> extends TitledParamPanel implements ParamListener {

    private final Param<String> mParam;
    private final JTextField    mField;


    TextFieldPanel( Param<String> param ) {
        super( param, param.getName() );
        mParam = param;
        mField = new JTextField( param.getValue() );

        add( mField );
        param.addListenerWeakly( this );

        setSize( 100, suggestRowHeight() * 2 );

        mField.addFocusListener( new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent e ) {
                mParam.setValue( TextFieldPanel.this, mField.getText());
            }
        });
        
        mField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mParam.setValue(TextFieldPanel.this, mField.getText());
            }
        });
        
    }
    
    
    public void paramStateChanged(Object source, Param param, State state) {
        if(source == this || state != State.ENABLE)
            return;
        
        mField.setEnabled(mParam.isEnabled());
    }

    
    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this)
            return;
        
        mField.setText(mParam.getValue());
    }

}
