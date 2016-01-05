package com.itime.team.itime.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.itime.team.itime.listener.ScrollViewListener;

/**
 * Created by mac on 15/12/28.
 */
public class MeetingSelectionScrollView extends HorizontalScrollView {
    private ScrollViewListener scrollViewListener = null;

    public MeetingSelectionScrollView(Context context) {
        super(context);
    }

    public MeetingSelectionScrollView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
    }
    public MeetingSelectionScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}

