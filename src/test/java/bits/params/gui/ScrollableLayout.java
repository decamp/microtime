/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import java.awt.LayoutManager;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Component;

public class ScrollableLayout implements LayoutManager {
    
    public static final boolean STACK_VERTICAL = true;
    public static final boolean STACK_HORIZONTAL = false;

    
    private JPanel mScrollPanel;
    private JScrollPane mScrollPane;
    
    private int mHorizontalMargin    = 4;
    private int mVerticalMargin      = 4;
    private boolean mWrapMode        = true;
    private boolean mStackDirection  = STACK_VERTICAL;
    private boolean mStretch         = true;
    private int mVertScrollSpace     = 0;
    private int mHorScrollSpace      = 0;


    public ScrollableLayout( JScrollPane scrollPane, JPanel scrollPanel) {
        mScrollPane = scrollPane;
        mScrollPanel = scrollPanel;
    }
    

    
    public void setStackDirection(boolean stackDirection) {
        mStackDirection = stackDirection;
    }
    
    public void setStretch(boolean stretch) {
        mStretch = stretch;
    }

    public void setMargins(int horMargin, int vertMargin) {
        mHorizontalMargin = horMargin;
        mVerticalMargin = vertMargin;
    }
    
    public void setVerticalScrollbarSpace(int vss) {
        mVertScrollSpace = vss;
    }

    public void setHorizontalScrollbarSpace(int hss) {
        mHorScrollSpace = hss;
    }

    public void setWrapMode(boolean wrapMode) {
        mWrapMode = wrapMode;
    }
    
    
    
    public void layoutContainer(Container container) {
        int x = mHorizontalMargin;
        int y = mVerticalMargin;
        int width = mScrollPane.getWidth() - mVertScrollSpace;
        int height = mScrollPane.getHeight() - mHorScrollSpace;

        Component c;

        if(mStackDirection) {
            int maxY = height;
            int outerX = 0;

            for(int i = 0; i < mScrollPanel.getComponentCount(); i++){
                c = mScrollPanel.getComponent(i);

                if(mStretch) {
                    c.setSize(width - x, c.getHeight());
                }else if(mWrapMode && c.getHeight() + y > maxY && i > 0){
                    x = outerX;
                    y = mVerticalMargin;
                }

                c.setLocation(x, y);
                outerX = Math.max(outerX, x + c.getWidth() + mHorizontalMargin);
                y = y + c.getHeight() + mVerticalMargin;
            }

            x = outerX;
        }else{
            int maxX = width;
            int outerY = 0;

            for(int i = 0; i < mScrollPanel.getComponentCount(); i++){
                c = mScrollPanel.getComponent(i);

                if(mStretch){
                    c.setSize(c.getWidth(), height);
                }else if(mWrapMode && c.getWidth() + x > maxX &&  i > 0){
                    y = outerY;
                    x = mHorizontalMargin;
                }

                c.setLocation(x, y);
                outerY = Math.max(outerY, y + c.getHeight() + mVerticalMargin);
                x = x + c.getWidth() + mHorizontalMargin;
            }

            y = outerY;
        }

        Dimension d = new Dimension(x, y);
        mScrollPanel.setPreferredSize(d);
        mScrollPanel.setMinimumSize(d);
    }

    public Dimension minimumLayoutSize(Container c) {
        return new Dimension(10, 10);
    }

    public Dimension preferredLayoutSize(Container c) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    
    public void addLayoutComponent(String name, Component comp) {}

    public void removeLayoutComponent(Component comp) {}

}
