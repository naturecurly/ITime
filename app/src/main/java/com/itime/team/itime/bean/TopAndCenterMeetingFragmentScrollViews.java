package com.itime.team.itime.bean;

import com.itime.team.itime.views.MeetingSelectionScrollView;

/**
 * Created by mac on 16/2/5.
 */
public class TopAndCenterMeetingFragmentScrollViews {
    private MeetingSelectionScrollView topScollView;
    private MeetingSelectionScrollView centerScollView;

    public MeetingSelectionScrollView getCenterScollView() {
        return centerScollView;
    }

    public MeetingSelectionScrollView getTopScollView() {
        return topScollView;
    }

    public void setCenterScollView(MeetingSelectionScrollView centerView) {
        this.centerScollView = centerView;
    }

    public void setTopScollView(MeetingSelectionScrollView topScollView) {
        this.topScollView = topScollView;
    }

    public void setTopScollViewPosition(int x, int y){
        topScollView.scrollTo(x, y);
    }

    public void setCenterScollViewPosition(int x, int y){
        centerScollView.scrollTo(x,y);
    }


}
