package com.itime.team.itime.views.adapters;

/**
 * Created by leveyleonhardt on 12/17/15.
 */
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.itime.team.itime.fragments.CalendarFragment;

public class CalendarViewPagerAdapter extends FragmentStatePagerAdapter {

    private View[] views;

    public CalendarViewPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.views = views
    }


//    public CalendarViewPagerAdapter(View[] views) {
//        super();
//        this.views = views;
//    }


    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        if (((ViewPager) arg0).getChildCount() == views.length) {
            ((ViewPager) arg0).removeView(views[arg1 % views.length]);
        }
        ((ViewPager) arg0).addView(views[arg1 % views.length], 0);

        return views[arg1 % views.length];
    }

    @Override
    public Fragment getItem(int position) {
        return new CalendarFragment();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startUpdate(View arg0) {
    }

}
