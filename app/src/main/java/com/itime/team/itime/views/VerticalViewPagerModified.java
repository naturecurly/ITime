package com.itime.team.itime.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by leveyleonhardt on 1/12/16.
 */
public class VerticalViewPagerModified extends VerticalViewPager {
    public VerticalViewPagerModified(Context context) {
        super(context);
    }

    public VerticalViewPagerModified(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childSize = getChildCount();
        int maxHeight = 0;
        for (int i = 0; i < childSize; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            if (child.getMeasuredHeight() > maxHeight) {
                maxHeight = child.getMeasuredHeight();
            }
        }

        if (maxHeight > 0) {
            setMeasuredDimension(getMeasuredWidth(), maxHeight);
        }

    }
}
