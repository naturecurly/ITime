package com.itime.team.itime.listener;

import android.view.MotionEvent;
import android.widget.ScrollView;

import com.itime.team.itime.views.MeetingScrollView;

/**
 * Created by leveyleonhardt on 4/26/16.
 */
public interface ScrollViewInterceptTouchListener {
    void touchEventHappend(ScrollView scrollView, MotionEvent ev);
}
