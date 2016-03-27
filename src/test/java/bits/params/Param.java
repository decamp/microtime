/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import bits.util.event.EventSource;


/**
 * Represents a single parameter and provides functionality
 * for receiving notifications when the parameter is changed.
 * Params also specify their own suggested widget type
 * for use in a graphical control panel.
 *
 * @author decamp
 */
public interface Param<V> extends EventSource<ParamListener> {


    public static enum State {
        ENABLE,
        NAME,
        DESCRIPTION;
    }


    /**
     * The name of the parameter, and the primary label used
     * when displaying the parameter.  Parameter names are
     * immutable.  MAY NOT be <tt>null</tt>.
     *
     * @return name of parameter
     */
    public String getName();

    /**
     * An description of the parameter.  The description
     * may change at any time.  MAY NOT be <tt>null</tt>.
     *
     * @return current description of the parameter.
     */
    public String getDescription();

    /**
     * Sets the description of the parameter.
     * <p/>
     * Changing the description generates a paramStateChanged()
     * notification with Param.State.DESCRIPTION as the
     * specified state.
     *
     * @param source Source object to pass to registered ParamListeners.  MAY BE <tt>null</tt>.
     * @param desc   The new description for this param.
     */
    public void setDescription( Object source, String desc );

    /**
     * Indicates the widget type that SHOULD BE used to
     * represent this parameter in an interface.
     *
     * @return suggested widget type.  MAY NOT be <tt>null</tt>.
     */
    public ParamWidgetType getWidgetType();

    /**
     * Sets the widget type that SHOULD BE used to
     * represent this parameter in an interface.
     *
     * @param widgetType Suggested widget type.
     */
    public void setWidgetType( ParamWidgetType widgetType );

    /**
     * Indicates if this param is enabled.  Disabled params
     * SHOULD NOT be adjustable by an interface, although
     * a disabled param may still be altered programmatically.
     *
     * @return true iff this Param is enabled.
     */
    public boolean isEnabled();

    /**
     * Sets whether is Param is enabled.
     * <p/>
     * Changing the enable state generates a paramStateChanged()
     * notification with Param.State.ENABLE as the specified
     * state.
     *
     * @param source Source object to pass to registered ParamListeners.  May be <tt>null</tt>.
     * @param enable The desired enable state of this Param.
     */
    public void setEnabled( Object source, boolean enable );


    /**
     * @return the current value of the Param.
     */
    public V getValue();

    /**
     * Sets the value of the Param.
     * <p/>
     * Changing the param value generates a paramValueChanged()
     * notification.
     *
     * @param source The source object to be sent to registered ParamListeners.  May be <tt>null</tt>.
     * @param value  The new value of this Param.
     */
    public void setValue( Object source, V value );

    /**
     * @return the class representing the value type of this Param.  MAY NOT be <tt>null</tt>.
     */
    public Class<V> getValueClass();


    /**
     * Adds listener to this Param.
     *
     * @param listener Listener to receive param notifications.
     */
    public void addListener( ParamListener listener );

    /**
     * Adds listener to this Param using a weak reference.
     * If you're not familiar with weak references, you should
     * not use this method.
     *
     * @param listener Listener to receive param notifications.
     */
    public void addListenerWeakly( ParamListener listener );

    /**
     * Removes a listener from this Param.
     *
     * @param listener Listener to remove.
     */
    public void removeListener( ParamListener listener );

}
