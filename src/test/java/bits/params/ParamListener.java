/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

/**
 * Interface for receiving Param notifications.
 * 
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
public interface ParamListener {
    
    /**
     * Called whenever the value of a Param changes.  SHOULD NOT be called
     * when the new value of a Param is equivalent to the old value.
     *  
     * @param source The source object that caused the value change.  May be <tt>null</tt>.
     * @param param  The affected Param object.
     * @param value  The new value of <tt>param</tt>.
     */
    public void paramValueChanged( Object source, Param param, Object value );
    
    /**
     * Called whenever a non-value state parameter of a Param changes, for example,
     * when a Param is enabled or its description is changed.  SHOULD NOT be called
     * when the new state of a Param is equivalent to the old state.
     * 
     * @param source The source object that caused the state change.  May be <tt>null</tt>.
     * @param param  The affected Param object.
     * @param state  The part of the Param's state that changed.
     */
    public void paramStateChanged( Object source, Param param, Param.State state );

}
