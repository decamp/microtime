/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import bits.params.*;


/**
 * @author decamp
 */
@SuppressWarnings( "rawtypes" )
class RadioButtonPanel extends TitledParamPanel implements ParamListener {

    private final ListParam                 mParam;
    private final Map<Object, JRadioButton> mButtonMap;


    RadioButtonPanel( ListParam param ) {
        super( param, param.getName() );

        mParam = param;
        mButtonMap = new HashMap<Object, JRadioButton>();

        List<?> values = param.getValueList();
        ButtonGroup group = new ButtonGroup();

        final int rowHeight = suggestRowHeight();

        JPanel panel = new JPanel();
        panel.setLayout( new WordLayout( 0, 0 ) );

        for( int i = 0; i < values.size(); i++) {
            final int idx = i;
            
            Object object = values.get(i);
            JRadioButton b = new JRadioButton(object == null ? "<null>" : object.toString());
            group.add(b);
            
            b.addActionListener(new ActionListener() {
                @SuppressWarnings("unchecked")
                public void actionPerformed(ActionEvent e) {
                    mParam.setValue(RadioButtonPanel.this, mParam.getValueFromList(idx));
                }
            });
        
            mButtonMap.put(object, b);
            
            b.setSize(100, suggestRowHeight());
            panel.add(b);
            panel.add(WordLayout.newStretch());
            panel.add(WordLayout.newNewLine());
        }
        
        
        setLayout(new FillLayout());
        add(panel);
        setSize(100, rowHeight * (mButtonMap.size() + 1));
        
        param.addListenerWeakly(this);
        
        JRadioButton selected = mButtonMap.get(param.getValue());
        if(selected != null) {
            selected.setSelected(true);
        }
    }
    
    

    public void paramStateChanged(Object source, Param param, Param.State state) {
        if(source == this || state != Param.State.ENABLE)
            return;
        
        boolean e = param.isEnabled();
        
        for(JRadioButton b: mButtonMap.values()) {
            b.setEnabled(e);
        }
    }
    
    
    public void paramValueChanged(Object source, Param param, Object value) {
        if(source == this)
            return;
        
        JRadioButton b = mButtonMap.get(value);
        if(b != null) {
            b.setSelected(true);
        }
    }
    
}
