package com.itime.team.itime.listener;

import android.support.v4.view.ViewPager;

import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.CalendarView;

/**
 * Created by leveyleonhardt on 1/13/16.
 */
public class CalendarPageChangeListener implements ViewPager.OnPageChangeListener {

    private CalendarView[] views;
    private int currentIndex = 500;
    private int mDirection = 0;
    private int mShowYear;
    private int mShowMonth;
    private int mShowDay;

    public CalendarPageChangeListener(CalendarView[] views) {
        this.views = views;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        measureDirection(position);
        updateCalendarView(position);
    }

    private void updateCalendarView(int position) {
        if (mDirection == 1) {
            int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
            if (mShowDay + 7 * 3 > currentMonthDays) {
                if (mShowMonth == 12) {
                    mShowMonth = 1;
                    mShowYear += 1;
                } else {
                    mShowMonth += 1;
                }
                mShowDay = 21 - currentMonthDays + mShowDay;
                return;
            }
            mShowDay += 21;
        } else if (mDirection == -1) {
            int lastMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
            if (mShowDay - 21 < 1) {
                if (mShowMonth == 1) {
                    mShowMonth = 12;
                    mShowYear -= 1;
                } else {
                    mShowMonth -= 1;
                }
                mShowDay = lastMonthDays - 21 + mShowDay;
                return;
            }
            mShowDay -= 21;
        }
        mDirection = 0;
    }

    private void measureDirection(int position) {
        if (position > currentIndex) {
            mDirection = 1;
        } else if (position < currentIndex) {
            mDirection = -1;
        }
        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
