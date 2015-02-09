/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.microtime;

import bits.params.*;
import bits.params.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


/**
 * @author Philip DeCamp
 */
public class FullClockTest extends JPanel {


    private static final long ROT_MICROS = 10000000L;


    public static void main( String[] args ) {
        JPanel p = new FullClockTest();
        JFrame frame = new JFrame( "FullClockTest" );
        frame.setSize( 1024, 1024 );
        frame.setLocationRelativeTo( null );
        frame.setContentPane( p );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }


    List<FullClock> mClocks = new ArrayList<FullClock>();
    List<List<JPanel>> mPanels = new ArrayList<List<JPanel>>();


    public FullClockTest() {
        Clock master = Clock.SYSTEM_CLOCK;
        List<FullClock> parents = null;

        for( int i = 0; i < 3; i++ ) {
            List<FullClock> clocks = new ArrayList<FullClock>();
            if( i == 0 ) {
                clocks.add( new FullClock( master ) );
            } else {
                for( FullClock parent: parents ) {
                    for( int j = 0; j < 2; j++ ) {
                        clocks.add( parent.createChild() );
                    }
                }
            }

            List<JPanel> panels = new ArrayList<JPanel>();
            for( FullClock clock: clocks ) {
                ClockPanel panel = new ClockPanel( clock );
                panels.add( panel );
                panel.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
                add( panel );
            }

            mPanels.add( panels );
            mClocks.addAll( clocks );
            parents = clocks;
        }


        setLayout( new LayoutAdapter() {
            @Override
            public void layoutContainer( Container cont ) {
                final int w = getWidth();
                final int h = getHeight();

                final int dx = w / mPanels.size() - 4;
                int x = 0;

                for( int i = 0; i < mPanels.size(); i++ ) {
                    List<JPanel> panels = mPanels.get( i );
                    int y = 0;
                    int dy = h / panels.size() - 4;

                    for( JPanel p: panels ) {
                        p.setBounds( x, y, dx, dy );
                        y += dy + 4;
                    }

                    x += dx + 4;
                }
            }
        } );

    }


    @Override
    public void paint( Graphics g ) {
        super.paint( g );
        repaint();
    }


    private static class ClockPanel extends JPanel {

        FullClock      mClock;
        Param<Boolean> mPlayParam;
        Param<Double>  mRateParam;

        List<JComponent> mComps;
        JButton        mSkipButton;


        ClockPanel( FullClock clock ) {
            mClock = clock;
            mPlayParam = BooleanParam.newInstance( "Play", false );
            mRateParam = DoubleParam.newInstance( "Rate", 1.0, 0.0, 4.0 );
            mSkipButton = new JButton( "Skip" );
            mSkipButton.setSize( 75, 30 );

            setLayout( new WordLayout( 2, 2 ) );
            add( WordLayout.newNoWrapMode() );
            add( new ClockFace( clock ) );
            add( WordLayout.newStretch() );
            add( WordLayout.newNewLine() );
            add( ParamPanelFactory.newParamPanel( mPlayParam ) );
            add( WordLayout.newStretch() );
            add( WordLayout.newNewLine() );
            add( ParamPanelFactory.newParamPanel( mRateParam ) );
            add( WordLayout.newStretch() );
            add( WordLayout.newNewLine() );
            add( mSkipButton );
            add( WordLayout.newStretch() );

            mPlayParam.addListener( new ParamListener() {
                @Override
                public void paramValueChanged( Object source, Param param, Object value ) {
                    if( (Boolean)value ) {
                        mClock.clockStart();
                    } else {
                        mClock.clockStop();
                    }
                }

                @Override
                public void paramStateChanged( Object source, Param param, Param.State state ) {}
            } );

            mRateParam.addListener( new ParamListener() {
                @Override
                public void paramValueChanged( Object source, Param param, Object value ) {
                    Frac frac = new Frac();
                    Frac.doubleToRational( ((Number)value).doubleValue(), frac );
                    Frac.reduce( frac.mNum, frac.mDen, 1000, frac );
                    mClock.clockSetRate( frac );
                }

                @Override
                public void paramStateChanged( Object source, Param param, Param.State state ) {}
            } );

            mSkipButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    mClock.clockSeek( mClock.masterMicros(), mClock.micros() + 2500000L );
                }
            } );

        }

    }


    private static class ClockFace extends JPanel {

        private static final Stroke STROKE = new BasicStroke( 3f );
        private final Clock mClock;

        ClockFace( Clock clock ) {
            mClock = clock;
            setBackground( Color.DARK_GRAY );
            setSize( 75, 75 );
        }

        @Override
        public void paintComponent( Graphics gg ) {
            Graphics2D g = (Graphics2D)gg;
            double ang = ( mClock.micros() % ROT_MICROS ) / (double)ROT_MICROS;
            ang *= 2.0 * Math.PI;

            final int cx = getWidth() / 2;
            final int cy = getHeight() / 2;
            final int dim = Math.min( cx, cy ) - 2;

            g.setBackground( getBackground() );
            g.clearRect( 0, 0, cx * 2, cy * 2 );
            g.setColor( Color.ORANGE );
            g.setStroke( STROKE );
            g.drawLine( cx, cy, cx + (int)( dim * Math.cos( ang )), cy + (int)( dim * Math.sin( ang ) ) );
            g.setColor( Color.BLACK );
            g.fillRect( cx - 2, cy - 2, 4, 4 );
        }

    }

}
