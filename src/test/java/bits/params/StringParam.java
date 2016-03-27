/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

/**
 * @author decamp
 */
public class StringParam extends AbstractParam<String> {


    public static StringParam newInstance( String name ) {
        return new StringParam( name, "" );
    }


    public static StringParam newInstance( String name, String initValue ) {
        return new StringParam( name, initValue );
    }


    private StringParam( String name, String initValue ) {
        super( String.class, name, null, ParamWidgetType.TEXT_FIELD, initValue );
    }

}
