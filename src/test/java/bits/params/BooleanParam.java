/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

/**
 * @author decamp
 */
public class BooleanParam extends AbstractParam<Boolean> {


    public static BooleanParam newInstance( String name ) {
        return new BooleanParam( name, false );
    }


    public static BooleanParam newInstance( String name, boolean initValue ) {
        return new BooleanParam( name, initValue );
    }


    private BooleanParam( String name, boolean initValue ) {
        super( Boolean.class, name, null, ParamWidgetType.CHECK_BOX, initValue );
    }


    @Override
    public void setValue( Object source, Boolean value ) {
        if( value == null ) {
            return;
        }

        super.setValue( source, value );
    }

}
