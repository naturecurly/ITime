package com.itime.team.itime.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.itime.team.itime.listener.ScrollMeetingViewListener;
import com.itime.team.itime.listener.ScrollViewInterceptTouchListener;

/**
 * Created by mac on 16/3/7.
 */
public class MeetingScrollView extends ScrollView {
    private ScrollMeetingViewListener scrollViewListener = null;
    private ScrollViewInterceptTouchListener scrollViewInterceptTouchListener = null;

    public MeetingScrollView(Context context) {
        super(context);
    }

    public MeetingScrollView(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }

    public MeetingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollViewListener(ScrollMeetingViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public void setOnInterceptTouchListener(ScrollViewInterceptTouchListener scrollViewInterceptTouchListener) {
        this.scrollViewInterceptTouchListener = scrollViewInterceptTouchListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollViewInterceptTouchListener != null) {
            scrollViewInterceptTouchListener.touchEventHappend(this, ev);
        }

        return super.onInterceptTouchEvent(ev);
    }
}
