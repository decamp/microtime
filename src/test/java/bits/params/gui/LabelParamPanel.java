/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import javax.swing.*;

import bits.params.Param;
import bits.params.ParamListener;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class LabelParamPanel extends TitledParamPanel implements ParamListener {

    @SuppressWarnings( "unused" )
    private final Param  mParam;
    private final JLabel mLabel;


    LabelParamPanel( Param param ) {
        super( param, param.getName() );
        mParam = param;
        mLabel = new JLabel();

        Object val = param.getValue();
        if( val != null ) {
            mLabel.setText( val.toString() );
        }

        add( mLabel );
        param.addListenerWeakly( this );
    }


    public void paramStateChanged( Object source, Param param, Param.State state ) {}


    public void paramValueChanged( Object source, Param param, Object value ) {
        mLabel.setText( value == null ? "" : value.toString() );
    }

}
