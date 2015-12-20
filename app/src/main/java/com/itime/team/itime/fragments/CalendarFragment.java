package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itime.team.itime.R;
import com.itime.team.itime.views.adapters.CalendarPagerAdapter;

/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new CalendarPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //setRetainInstance(true);
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
