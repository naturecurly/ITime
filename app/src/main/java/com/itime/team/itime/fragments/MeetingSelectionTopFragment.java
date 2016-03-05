package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.TopAndCenterMeetingFragmentScrollViews;
import com.itime.team.itime.listener.ScrollViewListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.MeetingSelectionScrollView;

//import com.itime.team.itime.activities.R;

/**
 * Created by mac on 15/12/28.
 */
public class MeetingSelectionTopFragment extends Fragment implements ScrollViewListener {
    private LinearLayout mChild;
    private View mParent;
    private TextView mMonth;
    private TextView[] mDates;
    private MeetingSelectionScrollView mCentralScrollView;
    private View mCentralFragment;
    private TopAndCenterMeetingFragmentScrollViews mScrollView;

    private int DATE;
    private int WIDTH = 141;
    private int WIDTHOFMONTHVIEW = 100;

    private int STARTYEAR;
    private int STARTMONTH;
    private int STARTDAY;

    private int ENDYEAR;
    private int ENDMONTH;
    private int ENDDAY;

    private int mInitDays;
    private int[] currentDay;

    public MeetingSelectionTopFragment(TopAndCenterMeetingFragmentScrollViews scrollViews){
        this.mScrollView = scrollViews;
    }
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
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        MeetingSelectionCentralFragment.WIDTHOFCENTERLAYOUT = dm.widthPixels - WIDTHOFMONTHVIEW - 150;
        if(DATE < 6 && DATE > 0){
            WIDTH = MeetingSelectionCentralFragment.WIDTHOFCENTERLAYOUT / DATE + 10;
        }
        mMonth = (TextView) mParent.findViewById(R.id.meeting_selection_top_textview);
        mMonth.setWidth(WIDTHOFMONTHVIEW);
        mMonth.setText(DateUtil.month[STARTMONTH - 1]);
        mDates = new TextView[DATE];
        currentDay = new int[3];
        mScrollView.setTopScollView((MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_top_scroll));
        mScrollView.getTopScollView().setOnScrollViewListener(this);
    }

    private void initTopScrollView(){
        String show;
        currentDay[0] = STARTYEAR;
        currentDay[1] = STARTMONTH;
        currentDay[2] = STARTDAY;
        mInitDays = DATE > 12 ? 12 : DATE;
        for(int i = 0; i < mInitDays; i ++){
            show = DateUtil.weekName[DateUtil.getDateOfWeek(currentDay[0], currentDay[1], currentDay[2]) - 1] + "\n"
                        + currentDay[2];
            mDates[i] = new TextView(getActivity());
            mDates[i].setWidth(WIDTH);
            mDates[i].setText(show);
            mDates[i].setGravity(Gravity.CENTER_HORIZONTAL);
            mDates[i].setTextColor(getResources().getColor(R.color.weeks));
            TextPaint tp = mDates[i].getPaint();
            tp.setFakeBoldText(true);
            mChild.addView(mDates[i]);
            currentDay = DateUtil.addDaysBasedOnCalendar(currentDay[0], currentDay[1], currentDay[2], 1);
        }
    }

    private void addView(){
        if(mInitDays < DATE){
            mDates[mInitDays] = new TextView(getActivity());
            mDates[mInitDays].setText(DateUtil.weekName[DateUtil.getDateOfWeek(currentDay[0],
                    currentDay[1], currentDay[2]) - 1] + "\n" + currentDay[2]);
            mDates[mInitDays].setWidth(WIDTH);
            mDates[mInitDays].setGravity(Gravity.CENTER_HORIZONTAL);
            mChild.addView(mDates[mInitDays]);
            currentDay = DateUtil.addDaysBasedOnCalendar(currentDay[0], currentDay[1], currentDay[2], 1);
        }
        mInitDays ++;
    }


    @Override
    public void onScrollChanged(MeetingSelectionScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == mScrollView.getTopScollView()) {
            if(mInitDays * WIDTH <= x + (12 * WIDTH) && mInitDays < DATE){
                addView();
            }
            mMonth.setText(DateUtil.month[DateUtil.plusDay(STARTYEAR,STARTMONTH, STARTDAY,x / WIDTH).getMonth()]);
            mScrollView.setCenterScollViewPosition(x,y);
        }
    }
}
