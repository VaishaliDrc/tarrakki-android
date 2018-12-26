package org.supportcompact.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;

public class NonSwipeableViewPager extends ViewPager {


    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public void setOffscreenPageLimit(int limit) {
        //super.setOffscreenPageLimit(limit);
        try {
            Field field;
            field = ViewPager.class.getDeclaredField("mOffscreenPageLimit");
            field.setAccessible(true); // Force to access the field
            // Set value
            field.set(this, limit);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            super.setOffscreenPageLimit(limit);
        }
    }
}
