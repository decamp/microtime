/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import bits.params.gui.*;
import bits.util.event.EventCaster;
import bits.util.event.EventSource;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Manages a set of Params. The primary function is to automatically generate
 * and manage a <i>param panel</i> gui component for displaying and adjusting
 * the children Params. A <tt>ParamListener</tt> added to a
 * <tt>ParamManager</tt> will receive notifications of all children params. This
 * functionality is also provided by <tt>CompoundParam</tt>, so if you don't
 * want to use the <i>param panel</i>, there's no requirement that you use a
 * <tt>ParamManager</tt> at all.
 * <p>
 * A ParamManager creates only one <i>param panel</i> object, which you can get
 * with <tt>getParamPanel()</tt>. Note that AWT components can only be added to
 * one <tt>Container</tt>, so if you call <tt>newParamFrame()</tt>, you MUST NOT
 * add the <i>param panel</i> to another Container.
 * 
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
public class ParamManager implements EventSource<ParamListener> {

    public static ParamManager newInstance() {
        return new ParamManager();
    }


    private final Map<Param, Node> mParamMap = new LinkedHashMap<Param, Node>();
    private final ComponentStacker           mStacker;
    private final EventCaster<ParamListener> mRepeatCaster;

    @Deprecated
    private JFrame mParamFrame = null;


    private ParamManager() {
        mStacker = new ComponentStacker();
        mStacker.setStackDirection( ComponentStacker.STACK_VERTICAL );
        mStacker.setStretch( true );
        mStacker.setMargins( 0, 0 );
        mStacker.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        mStacker.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );

        mRepeatCaster = EventCaster.create( ParamListener.class, EventCaster.THREADING_SYNCHRONOUS );
    }


    /**
     * Adds parameter to this manager.
     *
     * @param param
     * @return false iff this ParamManager already contains <tt>param</tt>.
     */
    public boolean addParam( Param<?> param ) {
        synchronized( this ) {
            if( mParamMap.containsKey( param ) )
                return false;

            addNode( new Node( param, ParamPanelFactory.newParamPanel( param ) ) );
            return true;
        }
    }

    /**
     * Adds parameter to manager and specifies the component to use in the
     * <i>param panel</i>. The desired height of the panel should be set before
     * adding it to the ParamManager.
     *
     * @param param
     *            Param to manage.
     * @param panel
     *            Panel to use in the <i>param panel</i> to represent
     *            <tt>param</tt>.
     * @return false iff this ParamManager already contains <tt>param</tt>.
     */
    public boolean addParam( Param<?> param, Component panel ) {
        synchronized( this ) {
            if( mParamMap.containsKey( param ) )
                return false;

            addNode( new Node( param, panel ) );

            return true;
        }
    }

    /**
     * Removes a param from the manager.
     * 
     * @param param
     * @return false iff this ParamManager does not contain the <tt>param</tt>.
     */
    public boolean removeParam( Param<?> param ) {
        synchronized( this ) {
            Node node = mParamMap.get( param );
            if( node == null )
                return false;

            removeNode( node );
            return true;
        }
    }

    /**
     * Returns the GUI Component view of all the managed params. This panel WILL
     * BE modified if Params are added or removed from this listener. There is
     * only one <i>param panel</i> per ParamManager.
     * 
     * @return GUI component containing interface for parameters
     */
    public Component getParamPanel() {
        return mStacker;
    }

    /**
     * Computes and returns the suggested height of the <i>param panel</i>,
     * which is generally the smallest height in which the panel can fully
     * display all the params. This value may be greater than the size of the
     * screen.
     * 
     * @return the suggested height of the param panel
     */
    public int getParamPanelSuggestedHeight() {
        int h = 0;

        for( Component c : mStacker.getInnerComponents() ) {
            h += c.getHeight();
        }

        return h;
    }

    /**
     * Creates a new JFrame and adds to it a <i>param panel</i>.
     * 
     * @param paramPanel
     *            Panel to add to frame
     * @return a newly allocated JFrame that is appropriately sized but not
     *         visible.
     */
    public JFrame newParamPanelFrame( Component paramPanel ) {
        JFrame ret = new JFrame();
        ret.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        ret.setResizable( true );
        ret.setLayout( new FillLayout() );

        int h = 30 + getParamPanelSuggestedHeight();
        ret.add( paramPanel );
        ret.pack();
        ret.setBounds( 0, 100, 450, Math.min( 1024, h ) );


        return ret;
    }

    /**
     * @deprecated Use something like
     *             <code>newParamFrame(getParamPanel()).setVisible(true)</code>
     *             instead. <br/>
     *             This method is considered dangerous because it's not obvious
     *             that the param panel component can only be added to one
     *             frame, and so <tt>getParamPanel()</tt> and
     *             <tt>setVisible()</tt> may can easily generate conflicts.
     */
    @Deprecated public void setVisible( boolean vis ) {
        if( vis ) {
            if( mParamFrame == null ) {
                mParamFrame = newParamPanelFrame( getParamPanel() );
            }

            mParamFrame.setVisible( vis );

        } else if( mParamFrame != null ) {
            mParamFrame.setVisible( vis );
        }
    }



    /**
     * Adds listener to this ParamManager, which will receive notifications from
     * all managed Params.
     * 
     * @param listener
     */
    public void addListener( ParamListener listener ) {
        mRepeatCaster.addListener( listener );
    }

    /**
     * Adds listener to this ParamManager using a weak reference. If you're not
     * familiar with weak references, do not use this method. ParamListeners
     * added to a ParamManager will receive notifications from all managed
     * Params.
     * 
     * @param listener
     */
    public void addListenerWeakly( ParamListener listener ) {
        mRepeatCaster.addListenerWeakly( listener );
    }

    /**
     * @param listener
     *            Listener to remove from this ParamManager.
     */
    public void removeListener( ParamListener listener ) {
        mRepeatCaster.removeListener( listener );
    }



    private void addNode( Node node ) {
        mParamMap.put( node.mParam, node );

        if( node.mPanel != null ) {
            mStacker.add( node.mPanel );
            mStacker.validate();
        }

        node.mParam.addListener( mRepeatCaster.cast() );
    }


    private void removeNode( Node node ) {
        mParamMap.remove( node.mParam );

        if( node.mPanel != null ) {
            mStacker.remove( node.mPanel );
        }

        node.mParam.removeListener( mRepeatCaster.cast() );
    }



    private static class Node {

        final Param mParam;
        final Component mPanel;

        Node( Param param, Component panel ) {
            mParam = param;
            mPanel = panel;
        }
    }

}
