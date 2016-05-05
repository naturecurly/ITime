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
public class MeetingScrollView extends ScrollView{
    private ScrollMeetingViewListener scrollViewListener = null;
    private ScrollViewInterceptTouchListener scrollViewInterceptTouchListener = null;
    private View pressedSubView = null;
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

    public View getPressedSubView(){
        return this.pressedSubView;
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

        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            RelativeLayout subLayout = (RelativeLayout)this.getChildAt(0);
            int subCount = subLayout.getChildCount();
            ArrayList<View> viewList = new ArrayList<View>();
            for(int i = 0; i < subCount; i++){
                View occupiedView = subLayout.getChildAt(i);
                if (occupiedView.getId()  >= 1000){
                    //only select occupied view
                    viewList.add(occupiedView);
                }
            }
            int y = (int)ev.getY();
            int sy = this.getScrollY();
            int pointY = y + sy;
            int pointX = (int)ev.getX();
            for (View occupiedView : viewList){
                int top = occupiedView.getTop();
                int bottom = occupiedView.getBottom();
                int left = occupiedView.getLeft();
                int right = occupiedView.getRight();
                if (top <= pointY && pointY <= bottom && left <= pointX && pointX <= right){
                    pressedSubView = occupiedView;
                    Log.d(TAG, "pressed view id:" + pressedSubView.getId());
                    break;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
