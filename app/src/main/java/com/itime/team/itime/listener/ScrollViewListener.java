package com.itime.team.itime.listener;

import com.itime.team.itime.views.MeetingSelectionScrollView;

/**
 * Created by mac on 15/12/28.
 */
public interface ScrollViewListener {
    void onScrollChanged(MeetingSelectionScrollView scrollView, int x, int y,
                         int oldx, int oldy);
}
