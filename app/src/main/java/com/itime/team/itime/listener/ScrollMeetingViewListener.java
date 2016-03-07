package com.itime.team.itime.listener;

import com.itime.team.itime.views.MeetingScrollView;

/**
 * Created by mac on 15/12/28.
 */
public interface ScrollMeetingViewListener {
    void onScrollChanged(MeetingScrollView scrollView, int x, int y,
                         int oldx, int oldy);
}
