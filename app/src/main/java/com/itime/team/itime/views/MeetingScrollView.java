package com.itime.team.itime.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.itime.team.itime.listener.ScrollMeetingViewListener;

/**
 * Created by mac on 16/3/7.
 */
public class MeetingScrollView extends ScrollView {
    private ScrollMeetingViewListener scrollViewListener = null;

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

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
