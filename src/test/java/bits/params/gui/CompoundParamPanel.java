/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import bits.params.CompoundParam;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;


/**
 * @author decamp
 */
class CompoundParamPanel extends ParamPanel {

    @SuppressWarnings( "unused" )
    private final CompoundParam mParam;

    private final Border mBorder;
    private final JLabel mLabel;
    private final ListPanel mListPanel;


    CompoundParamPanel( CompoundParam compoundParam, List<ParamPanel> panels, boolean border ) {
        mParam = compoundParam;

        mListPanel = new ListPanel( panels );
        int h = mListPanel.getHeight();

        if( border ) {
            mLabel = null;
            mBorder = BorderFactory.createTitledBorder( compoundParam.getName() );
            setSize( 100, 100 );

            Insets insets = mBorder.getBorderInsets( this );
            h += insets.top + insets.bottom;

            setLayout( new FillLayout() );
            setBorder( mBorder );
        } else {
            mBorder = null;
            mLabel = new JLabel( compoundParam.getName() );
            mLabel.setSize( 100, suggestRowHeight() );
            h += mLabel.getHeight();

            setLayout( new Layout() );
            add( mLabel );
        }

        add( mListPanel );
        setSize( 100, h );
    }



    private final class ListPanel extends JPanel {

        ListPanel( List<ParamPanel> params ) {
            setLayout( new WordLayout( 0, 0 ) );
            add( WordLayout.newNoWrapMode() );

            int h = 0;

            for( ParamPanel p : params ) {
                add( p );
                add( WordLayout.newStretch() );
                h += p.getHeight();
            }

            setSize( 100, h );
        }

    }


    private final class Layout extends LayoutAdapter {

        @Override
        public void layoutContainer( Container cont ) {
            int w = cont.getWidth();
            int h = cont.getHeight();
            int y = mLabel.getHeight();

            mLabel.setBounds( 0, 0, w, y );
            mListPanel.setBounds( 3, y, w - 3, h - y );
        }

    }

}
