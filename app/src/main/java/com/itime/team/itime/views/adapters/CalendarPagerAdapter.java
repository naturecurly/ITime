package com.itime.team.itime.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itime.team.itime.fragments.CalendarBodyFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leveyleonhardt on 12/18/15.
 */
public class CalendarPagerAdapter extends FragmentStatePagerAdapter {
    private Map<Integer, CalendarBodyFragment> map = new HashMap<>();

    public CalendarPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        CalendarBodyFragment fragment = new CalendarBodyFragment();
        map.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return 1000;
    }

    public CalendarBodyFragment getFragment(int index) {
        return map.get(index);
    }


}
