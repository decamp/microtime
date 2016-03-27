/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import bits.util.event.EventCaster;


/**
 * @author decamp
 */
public abstract class AbstractParam<V> implements Param<V> {

    private final Class<V> mValueClass;
    private final String mName;
    private final EventCaster<ParamListener> mCaster;

    private String mDescription;
    private ParamWidgetType mWidgetType;
    private boolean mEnabled = true;
    private V mValue;


    protected AbstractParam(
            Class<V> valueClass,
            String name,
            String description,
            ParamWidgetType widgetType,
            V value
    ) {
        mValueClass = valueClass;
        mName = name == null ? "" : name;
        mDescription = description == null ? "" : description;
        mWidgetType = widgetType == null ? ParamWidgetType.NONE : widgetType;
        mValue = value;

        mCaster = EventCaster.create( ParamListener.class );
    }



    @Override
    public String getName() {
        return mName;
    }


    @Override
    public String getDescription() {
        return mDescription;
    }


    @Override
    public void setDescription( Object source, String desc ) {
        if( desc == null )
            desc = "";

        synchronized( this ) {
            if( desc == mDescription || desc.equals( mDescription ) ) {
                return;
            }
            mDescription = desc;
            mCaster.cast().paramStateChanged( source, this, Param.State.DESCRIPTION );
        }
    }


    public ParamWidgetType getWidgetType() {
        return mWidgetType;
    }


    public void setWidgetType( ParamWidgetType widgetType ) {
        mWidgetType = widgetType == null ? ParamWidgetType.NONE : widgetType;
    }


    @Override
    public boolean isEnabled() {
        return mEnabled;
    }


    @Override
    public void setEnabled( Object source, boolean enable ) {
        synchronized( this ) {
            if( enable == mEnabled ) {
                return;
            }
            mEnabled = enable;
            mCaster.cast().paramStateChanged( source, this, Param.State.ENABLE );
        }
    }


    @Override
    public V getValue() {
        return mValue;
    }


    @Override
    public void setValue( Object source, V value ) {
        synchronized( this ) {
            if( mValue == value || mValue != null && mValue.equals( value ) ) {
                return;
            }
            mValue = value;
            mCaster.cast().paramValueChanged( source, this, value );
        }
    }


    @Override
    public Class<V> getValueClass() {
        return mValueClass;
    }



    public void addListener( ParamListener listener ) {
        mCaster.addListener( listener );
    }


    public void addListenerWeakly( ParamListener listener ) {
        mCaster.addListener( listener );
    }


    public void removeListener( ParamListener listener ) {
        mCaster.removeListener( listener );
    }

}
