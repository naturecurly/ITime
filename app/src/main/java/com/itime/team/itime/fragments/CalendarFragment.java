package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itime.team.itime.R;
import com.itime.team.itime.views.CalendarView;
import com.itime.team.itime.views.adapters.CalendarPagerAdapter;

/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {

    private ViewPager viewPager;
    private CalendarPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new CalendarPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //viewPager.getCurrentItem()
        //setRetainInstance(true);
        //CalendarBodyFragment calendarBodyFragment = (CalendarBodyFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
        //View currentCalendarView = calendarBodyFragment.getView();
        //int position = viewPager.getCurrentItem();
        //PagerAdapter adapter = viewPager.getAdapter();
        //CalendarBodyFragment fragment = (CalendarBodyFragment) pagerAdapter.instantiateItem(viewPager, 0);
//        System.out.print(viewPager.getCurrentItem());
        // Log.i("TTT", "ts");
        //CalendarPagerAdapter cpa = (CalendarPagerAdapter) viewPager.getAdapter();
       // CalendarBodyFragment fragment = pagerAdapter.getFragment(viewPager.getCurrentItem());
//        CalendarView cv = fragment.getCalendarView();
//        Log.i("TTTT", cv.getmShowDay() + " " + cv.getmShowMonth() + " " + cv.getmShowYear());
        Log.i("TTT",viewPager.getChildCount()+"");
        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        //viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        if (pagerAdapter == null) {
            pagerAdapter = new CalendarPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(pagerAdapter);
        }
    }
}
