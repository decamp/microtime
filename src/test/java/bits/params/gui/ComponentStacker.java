/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Like a JScrollPane, but you can add a bunch of components and the
 * ComponentStacker will arrange them nicely and manage the scroll bars.  
 * Components must be properly sized before adding them.
 * 
 * <p>Please note that CompontentStacker does not work exactly like a normal 
 * container.  Instead of using getComponent, getComponents, or
 * getComponentCount, you MUST use getInnerComponent, getInnerComponents, and
 * getInnerComponentCount.  This is because JScrollPanes are incredibly 
 * annoying and I should have written this class from scratch rather than 
 * extends JScrollPane, but whatever; nobody has ready used this class other
 * than me and no one will ever read this.
 * 
 * @author Philip DeCamp
 */
public class ComponentStacker extends JScrollPane {
    
    public static final boolean STACK_VERTICAL = ScrollableLayout.STACK_VERTICAL;
    public static final boolean STACK_HORIZONTAL = ScrollableLayout.STACK_HORIZONTAL;

    
    private final JPanel mPanel = new JPanel();
    private ScrollableLayout mLayout = null;

    
    public ComponentStacker() {
        mLayout = new ScrollableLayout(this, mPanel);
        mPanel.setLayout(mLayout);
        setViewportView(mPanel);
        
        setHorizontalScrollBarPolicy(super.getHorizontalScrollBarPolicy());
        setVerticalScrollBarPolicy(super.getVerticalScrollBarPolicy());
        
        getVerticalScrollBar().setBlockIncrement(150);
        getVerticalScrollBar().setUnitIncrement(10);
        getHorizontalScrollBar().setBlockIncrement(150);
        getHorizontalScrollBar().setUnitIncrement(10);
    }


    
    public void setStackDirection(boolean stackDirection) {
        mLayout.setStackDirection(stackDirection);
    }

    public void setWrapMode(boolean wrapMode) {
        mLayout.setWrapMode(wrapMode);
    }
    
    public void setStretch(boolean stretch) {
        mLayout.setStretch(stretch);
    }
    
    public void setMargins(int horMargin, int vertMargin) {
        mLayout.setMargins(horMargin, vertMargin);
    }
    
    public void setHorizontalScrollBarPolicy(int policy) {
        super.setHorizontalScrollBarPolicy(policy);
        
        if(mLayout == null)
            return;
        
        if(policy == JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
            mLayout.setHorizontalScrollbarSpace(0);
        }else{
            mLayout.setHorizontalScrollbarSpace(24);
        }
    }
    
    public void setVerticalScrollBarPolicy(int policy) {
        super.setVerticalScrollBarPolicy(policy);
        
        if(mLayout == null)
            return;
        
        if(policy == JScrollPane.VERTICAL_SCROLLBAR_NEVER) {
            mLayout.setVerticalScrollbarSpace(0);
        }else{
            mLayout.setVerticalScrollbarSpace(24);
        }
    }
    
        
    
    public Component add(Component c) {
        return mPanel.add(c);
    }

    public Component add(Component c, int index) {
        return mPanel.add(c, index);
    }

    public void remove(Component c) {
        mPanel.remove(c);
    }

    @Override
    public void remove(int index) {
        mPanel.remove(index);
    }

    public void removeAll() {
        mPanel.removeAll();
    }

    public Component getInnerComponent(int index) {
        if(mPanel == null)
            return null;
        
        return mPanel.getComponent(index);
    }

    public Component[] getInnerComponents() {
        if(mPanel == null)
            return null;
            
        return mPanel.getComponents();
    }

    public int getInnerComponentCount() {
        if(mPanel == null)
            return 0;
        
        return mPanel.getComponentCount();
    }

    public void setBackground(Color bg) {
        if(mPanel != null)
            mPanel.setBackground(bg);
    }
    
    public void setForeground(Color fg) {
        if(mPanel != null)
            mPanel.setForeground(fg);
    }

}
