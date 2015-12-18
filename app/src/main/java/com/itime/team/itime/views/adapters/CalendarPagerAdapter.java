package com.itime.team.itime.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itime.team.itime.fragments.CalendarBodyFragment;

/**
 * Created by leveyleonhardt on 12/18/15.
 */
public class CalendarPagerAdapter extends FragmentStatePagerAdapter {
    public CalendarPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new CalendarBodyFragment();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
