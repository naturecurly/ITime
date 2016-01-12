package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itime.team.itime.activities.R;
import com.itime.team.itime.listener.ScrollViewListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.MeetingSelectionScrollView;

/**
 * Created by mac on 15/12/28.
 */
public class MeetingSelectionTopFragment extends Fragment implements ScrollViewListener {
    private LinearLayout mChild;
    private View mParent;
    private TextView mMonth;
    private TextView[] mDates;
    private static MeetingSelectionScrollView mScrollView;
    private MeetingSelectionScrollView mCentralScrollView;
    private View mCentralFragment;

    private int DATE;
    private int WIDTH = 151;

    private int STARTYEAR;
    private int STARTMONTH;
    private int STARTDAY;

    private int ENDYEAR;
    private int ENDMONTH;
    private int ENDDAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getParameters(savedInstanceState);
        DATE = DateUtil.diffDate(STARTYEAR, STARTMONTH, STARTDAY, ENDYEAR, ENDMONTH, ENDDAY);
        mParent = inflater.inflate(R.layout.meeting_selection_top_fragment, null);
        mCentralFragment = inflater.inflate(R.layout.meeting_selection_center_fragment, null);
        init();
        initTopScrollView();
        return mParent;
    }

    private void getParameters(Bundle savedInstanceState){
        savedInstanceState = getArguments();
        STARTYEAR = savedInstanceState.getInt("startyear");
        STARTMONTH = savedInstanceState.getInt("startmonth");
        STARTDAY = savedInstanceState.getInt("startday");
        ENDYEAR = savedInstanceState.getInt("endyear");
        ENDMONTH = savedInstanceState.getInt("endmonth");
        ENDDAY = savedInstanceState.getInt("endday");
    }

    private void init(){
        mCentralScrollView = (MeetingSelectionScrollView) mCentralFragment.findViewById(R.id.meeting_selection_center_scroll);

        mChild = (LinearLayout) mParent.findViewById(R.id.meeting_selection_top_scroll_child);
        mMonth = (TextView) mParent.findViewById(R.id.meeting_selection_top_textview);
        mMonth.setWidth(WIDTH);
        mMonth.setText(DateUtil.month[STARTMONTH - 1]);
        mDates = new TextView[DATE];
        mScrollView = (MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_top_scroll);
        mScrollView.setOnScrollViewListener(this);
    }

    private void initTopScrollView(){
        String show;
        int[] date = {STARTYEAR,STARTMONTH,STARTDAY};
        for(int i = 0; i < DATE; i ++){
            show = DateUtil.weekName[DateUtil.getDateOfWeek(date[0], date[1], date[2]) - 1] + "\n"
                        + date[2];
            mDates[i] = new TextView(getActivity());
            mDates[i].setText(show);
            mDates[i].setWidth(WIDTH);
            mChild.addView(mDates[i]);
            date = DateUtil.addDaysBasedOnCalendar(date[0], date[1], date[2], 1);
        }
    }

    public static void setPosition(int x, int y){
        mScrollView.scrollTo(x, y);
    }

    @Override
    public void onScrollChanged(MeetingSelectionScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == mScrollView) {
            MeetingSelectionCentralFragment.setPosition(x,y);
        }
    }
}
