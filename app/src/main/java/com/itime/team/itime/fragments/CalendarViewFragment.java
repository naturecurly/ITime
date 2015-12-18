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
public class CalendarViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_calendar_pager, container, false);


        return view;
    }

    @Nullable
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new CalendarPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
    }


}
