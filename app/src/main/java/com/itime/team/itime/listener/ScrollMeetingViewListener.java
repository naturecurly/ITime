package com.itime.team.itime.listener;

import android.widget.ScrollView;

import com.itime.team.itime.views.MeetingScrollView;

/**
 * Created by Weiwei Cai on 15/12/28.
 */
public interface ScrollMeetingViewListener {
    void onScrollChanged(ScrollView scrollView, int x, int y,
                         int oldx, int oldy);
}
