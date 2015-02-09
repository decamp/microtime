/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import bits.params.Param;

import javax.swing.*;
import javax.swing.border.Border;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class BorderedParamPanel extends ParamPanel {

    private final Param mParam;


    BorderedParamPanel( Param param, String title ) {
        mParam = param;
        setSize( 100, 40 );

        Border border;

        if( title == null || title.length() == 0 ) {
            border = BorderFactory.createEtchedBorder();
        } else {
            border = BorderFactory.createTitledBorder( mParam.getName() );
        }

        setBorder(border);
        setLayout(new FillLayout());
    }
    
}
