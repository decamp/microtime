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
class TitledParamPanel extends ParamPanel {

    private final Param mParam;


    TitledParamPanel( Param param, String title ) {
        mParam = param;
        setSize( 100, 40 );

        if( title != null && title.length() > 0 ) {
            Border border = BorderFactory.createEmptyBorder();
            border = BorderFactory.createTitledBorder( border, mParam.getName() );
            setBorder( border );
        }

        setLayout( new FillLayout());
    }
    
}
