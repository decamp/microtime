/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import java.util.Date;


/**
 * @author decamp
 */
public class DateParam extends AbstractParam<Date> {

    public static DateParam newInstance( String name, Date initValue ) {
        return new DateParam( name, initValue );
    }


    private DateParam( String name, Date initValue ) {
        super( Date.class, name, "", ParamWidgetType.TEXT_FIELD, initValue );
    }

}
