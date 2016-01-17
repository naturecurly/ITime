package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
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
    private int WIDTH = 141;

    private int STARTYEAR;
    private int STARTMONTH;
    private int STARTDAY;

    private int ENDYEAR;
    private int ENDMONTH;
    private int ENDDAY;

    private int mInitDays;
    private int[] currentDay;
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
        if(DATE < 6 && DATE > 0){
            WIDTH = MeetingSelectionCentralFragment.WIDTHOFCENTERLAYOUT / DATE;
        }
        mMonth = (TextView) mParent.findViewById(R.id.meeting_selection_top_textview);
        mMonth.setWidth(100);
        mMonth.setText(DateUtil.month[STARTMONTH - 1]);
        mDates = new TextView[DATE];
        currentDay = new int[3];
        mScrollView = (MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_top_scroll);
        mScrollView.setOnScrollViewListener(this);
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

    public static void setPosition(int x, int y){
        mScrollView.scrollTo(x, y);
    }

    @Override
    public void onScrollChanged(MeetingSelectionScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == mScrollView) {
            if(mInitDays * WIDTH <= x + (12 * WIDTH) && mInitDays < DATE){
                addView();
            }
            mMonth.setText(DateUtil.month[DateUtil.plusDay(STARTYEAR,STARTMONTH, STARTDAY,x / WIDTH).getMonth()]);
            MeetingSelectionCentralFragment.setPosition(x,y);
        }
    }
}
