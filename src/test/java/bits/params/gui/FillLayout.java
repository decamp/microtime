/*
 * Copyright (c) 2015. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.params.gui;

import java.awt.Container;
import java.awt.Component;
import java.awt.Insets;


/** 
 * @author Philip DeCamp  
 */
public class FillLayout extends LayoutAdapter {
    
    
    private final int mLeft;
    private final int mTop;
    private final int mRight;
    private final int mBottom;
    private final boolean mIgnoreInsets;
    
    
    public FillLayout() {
        this(0, 0, 0, 0, false);
    }
    
    
    public FillLayout(int margin) {
        this(margin, margin, margin, margin, false);
    }
    
    
    public FillLayout(int margin, boolean ignoreContainerInsets) {
        this(margin, margin, margin, margin, ignoreContainerInsets);
    }
    
    
    public FillLayout( int leftMargin, 
                       int topMargin, 
                       int rightMargin, 
                       int bottomMargin) 
    {
        this(leftMargin, topMargin, rightMargin, bottomMargin, false);
    }
    
    
    public FillLayout( int leftMargin, 
                       int topMargin, 
                       int rightMargin, 
                       int bottomMargin, 
                       boolean ignoreContainerInsets) 
    {
        mLeft   = leftMargin;
        mTop    = topMargin;
        mRight  = rightMargin;
        mBottom = bottomMargin;

        mIgnoreInsets = ignoreContainerInsets;
    }
    
        
    
    public void layoutContainer(Container cont) {
        final int count = cont.getComponentCount();
        final int w = cont.getWidth();
        final int h = cont.getHeight();

        //Compute insets of container.
        int left;
        int top;
        int right;
        int bottom;
        
        Insets insets = cont.getInsets();
        
        if(!mIgnoreInsets && insets != null) {
            left   = insets.left;
            top    = insets.top;
            right  = insets.right;
            bottom = insets.bottom;
        }else{
            left   = 0;
            top    = 0;
            right  = 0;
            bottom = 0;
        }

        //No space.
        if(w - right - left <= 0 || h - top - bottom <= 0) {
            for(int i = 0; i < count; i++) {
                Component c = cont.getComponent(i);
                if(c != null) {
                    c.setBounds(left, top, 0, 0);
                }
            }
            
            return;
        }
        
        //Scale down component insets as necessary.
        if(w - left - right - mLeft - mRight < 0) {
            int ww = w - left - right;
            int nn = mRight * ww / (mLeft + mRight);
            
            left  += ww - nn;
            right += nn;
        }else{
            left  += mLeft;
            right += mRight;
        }
        
        if(h - top - bottom - mTop - mBottom < 0) {
            int hh = h - top - bottom;
            int nn = mBottom * hh / (mTop + mBottom);

            top    += hh - nn;
            bottom += nn;
        }else{
            top    += mTop;
            bottom += mBottom;
        }
                
        //Resize components.
        for(int i = 0; i < count; i++) {
            Component c = cont.getComponent(i);
            if(c != null) {
                c.setBounds(left, top, w - left - right, h - top - bottom);
            }
        }
    }

}
