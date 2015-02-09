/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params;

import java.util.*;


/**
 * @author decamp
 */
public class ListParam<V> extends AbstractParam<V> {

    
    public static <V> ListParam<V> newInstance(Class<V> valueClass, String name, Collection<? extends V> elements) {
        Iterator<? extends V> iter = elements.iterator();
        if(iter.hasNext()) {
            return newInstance(valueClass, name, elements, iter.next());
        }else{
            return newInstance(valueClass, name, elements, null);
        }
    }

    
    public static <V> ListParam<V> newInstance(Class<V> valueClass, String name, Collection<? extends V> elements, V value) {
        List<V> list = new ArrayList<V>(elements);
        ListParam<V> ret = new ListParam<V>(valueClass, name, list);
        
        if(value != null) {
            ret.setValue(null, value);
        }
        
        return ret;
    }
    
    
    
    private final List<V> mValueList;
    
    
    private ListParam(Class<V> valueClass, String name, List<V> valueList) {
        super(valueClass, name, null, ParamWidgetType.COMBO_BOX, null); 
        mValueList = valueList;
    }
    
    

    @Override
    public void setValue(Object source, V value) {
        if(!mValueList.contains(value))
            return;
        
        super.setValue(source, value);
    }
    

    public int getValueListSize() {
        return mValueList.size();
    }
    
    
    public V getValueFromList(int idx) {
        return mValueList.get(idx);
    }

    
    public List<V> getValueList() {
        return new ArrayList<V>(mValueList);
    }
    
}
