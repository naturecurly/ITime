package com.itime.team.itime.views;

/**
 * Created by leveyleonhardt on 12/17/15.
 */
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.itime.team.itime.utils.DateUtil;

public class CalendarViewPagerListener implements OnPageChangeListener {

    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    int mCurrIndex = 498;
    private static final int WEEK = 7;
    private int mShowYear;
    private int mShowMonth;
    private int mShowDay = 1;
    private CalendarView[] mShowViews;
    private int style;

    public CalendarViewPagerListener(CalendarView[] mShowViews) {
        super();
        this.mShowViews = mShowViews;
    }

    public CalendarViewPagerListener(CalendarView[] mShowViews, int style) {
        super();
        this.mShowViews = mShowViews;
        this.style = style;
        initDate();
    }

    @Override
    public void onPageSelected(int arg0) {
        measureDirection(arg0);
        updateCalendarView(arg0);
        onPageSelected(mShowYear, mShowMonth, mShowDay);
    }

    private void updateCalendarView(int arg0) {
        if (style == CalendarView.MONTH_STYLE)
            updateMonthStyleCalendarView();
        else if ((style == CalendarView.WEEK_STYLE))
            updateWeekStyleCalendarView();
        mShowViews[arg0 % mShowViews.length].update(mShowYear, mShowMonth,
                mShowDay);
    }

    private void updateWeekStyleCalendarView() {
        //int[] time = new int[3];
        if (mDirection == SildeDirection.RIGHT) {
            int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
            if(mShowDay + WEEK > currentMonthDays){
                if(mShowMonth == 12){
                    mShowMonth = 1;
                    mShowYear += 1;
                }else{
                    mShowMonth += 1;
                }
                mShowDay = WEEK -currentMonthDays + mShowDay;
                return;
            }
            mShowDay += WEEK;
        } else if (mDirection == SildeDirection.LEFT) {
            int lastMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
            if(mShowDay - WEEK < 1){
                if(mShowMonth == 1){
                    mShowMonth = 12;
                    mShowYear -= 1;
                }else{
                    mShowMonth -= 1;
                }
                mShowDay = lastMonthDays - WEEK + mShowDay;
                return;
            }
            mShowDay -= WEEK;
        }

        mDirection = SildeDirection.NO_SILDE;
    }

    private void updateMonthStyleCalendarView() {

        if (mDirection == SildeDirection.RIGHT) {

            if (mShowMonth == 12) {
                mShowMonth = 1;
                mShowYear += 1;
            } else {
                mShowMonth += 1;
            }
        } else if (mDirection == SildeDirection.LEFT) {
            if (mShowMonth == 1) {
                mShowMonth = 12;
                mShowYear -= 1;
            } else {
                mShowMonth -= 1;
            }
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    private void measureDirection(int arg0) {

        if (arg0 > mCurrIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrIndex = arg0;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    public void onPageSelected(int year, int month, int day) {

    }

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    private void initDate(){
        if(style == CalendarView.MONTH_STYLE){
            mShowYear = DateUtil.getYear();
            mShowMonth = DateUtil.getMonth();
            mShowDay = 1;
        }else if(style == CalendarView.WEEK_STYLE){
            int time[] = DateUtil.getPerviousWeekSunday();
            mShowYear = time[0];
            mShowMonth = time[1];
            mShowDay = time[2];
        }
        onPageSelected(mShowYear, mShowMonth, mShowDay);
    }

    public void setData(int style, CalendarView[] mShowViews){
        this.style = style;
        this.mShowViews = mShowViews;
    }
}