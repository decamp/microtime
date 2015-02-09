/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import bits.util.event.EventCaster;


/**
 * Special param that holds no value, but can be used to group multiple child
 * Params. ParamListeners added to a CompoundParam will receive notifications
 * from all child Params, plus state change notifications from the
 * CompoundParam.
 * 
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
public class CompoundParam implements Param<Void> {


    public static CompoundParam newInstance( String name, Param... params ) {
        return new CompoundParam( name, params.clone() );
    }


    private final String mName;
    private final Param[] mParams;
    private final boolean[] mParamEnabled;

    private final EventCaster<ParamListener> mNewCaster;
    private final EventCaster<ParamListener> mRepeatCaster;

    private String mDescription = null;
    private boolean mEnabled = true;


    private CompoundParam( String name, Param[] params ) {
        mName = name;
        mParams = params;
        mParamEnabled = new boolean[params.length];

        mNewCaster    = EventCaster.create( ParamListener.class, EventCaster.THREADING_AWT );
        mRepeatCaster = EventCaster.create( ParamListener.class, EventCaster.THREADING_SYNCHRONOUS );

        for( Param p : params ) {
            p.addListener( mRepeatCaster.cast() );
        }
    }



    public int getParamCount() {
        return mParams.length;
    }


    public Param getParam( int idx ) {
        return mParams[idx];
    }


    public Param[] getParams() {
        return mParams.clone();
    }



    public String getName() {
        return mName;
    }


    public String getDescription() {
        return mDescription;
    }


    public void setDescription( Object source, String desc ) {
        synchronized( this ) {
            if( desc == mDescription || desc != null && desc.equals( mDescription ) )
                return;

            mDescription = desc;
            mNewCaster.cast().paramStateChanged( source, this, Param.State.DESCRIPTION );
        }
    }


    public ParamWidgetType getWidgetType() {
        return ParamWidgetType.COMPOUND;
    }


    public void setWidgetType( ParamWidgetType widgetType ) {}


    public boolean isEnabled() {
        return mEnabled;
    }


    public void setEnabled( Object source, boolean enabled ) {
        synchronized( this ) {
            if( enabled == mEnabled )
                return;

            mEnabled = enabled;
            mNewCaster.cast().paramStateChanged( source, this, Param.State.ENABLE );

            if( enabled ) {
                for( int i = 0; i < mParams.length; i++ ) {
                    mParams[i].setEnabled( source, mParamEnabled[i] );
                }
            } else {
                for( int i = 0; i < mParams.length; i++ ) {
                    mParamEnabled[i] = mParams[i].isEnabled();
                    mParams[i].setEnabled( source, false );
                }
            }
        }
    }


    public Void getValue() {
        return null;
    }


    public void setValue( Object source, Void value ) {}


    public Class<Void> getValueClass() {
        return Void.class;
    }


    public void addListener( ParamListener listener ) {
        synchronized( this ) {
            mNewCaster.addListener( listener );
            mRepeatCaster.addListener( listener );
        }
    }


    public void addListenerWeakly( ParamListener listener ) {
        synchronized( this ) {
            mNewCaster.addListenerWeakly( listener );
            mRepeatCaster.addListenerWeakly( listener );
        }
    }


    public void removeListener( ParamListener listener ) {
        synchronized( this ) {
            mNewCaster.removeListener( listener );
            mRepeatCaster.removeListener( listener );
        }
    }

}
