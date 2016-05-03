package com.itime.team.itime.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.itime.team.itime.listener.ScrollMeetingViewListener;
import com.itime.team.itime.listener.ScrollViewInterceptTouchListener;

import java.util.ArrayList;

/**
 * Created by mac on 16/3/7.
 */
public class MeetingScrollView extends ScrollView {
    private ScrollMeetingViewListener scrollViewListener = null;
    private ScrollViewInterceptTouchListener scrollViewInterceptTouchListener = null;

    private final static String TAG = "MeetingScrollView";

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

        if (ev.getAction() == MotionEvent.ACTION_UP){
            Log.d(TAG, ev.getAction() + "----");
            RelativeLayout subLayout = (RelativeLayout)this.getChildAt(0);
            int subCount = subLayout.getChildCount();
            ArrayList<CustomizedTextView> viewList = new ArrayList<CustomizedTextView>();
            for(int i = 0; i < subCount; i++){
                View textView = subLayout.getChildAt(i);
                if (textView instanceof CustomizedTextView){
                    viewList.add((CustomizedTextView)textView);
                }
            }
            int y = (int)ev.getY();
            for (CustomizedTextView textView : viewList){
                int top = textView.getTop();
                int bottom = textView.getBottom();
                int sy = this.getScrollY();
                int pointY = y + sy;
                if (top <= pointY && pointY <= bottom){
                    // if pointer is in event area, deal with text view
                    return super.onInterceptTouchEvent(ev);
                }
            }
            // if pointer is not int event area, deal with scroll view
            // add an listener here to handle blank area click event
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
