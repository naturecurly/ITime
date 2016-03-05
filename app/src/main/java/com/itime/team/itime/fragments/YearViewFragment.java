package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itime.team.itime.R;
import com.itime.team.itime.views.CalendarView;

/**
 * Created by leveyleonhardt on 3/5/16.
 */
public class YearViewFragment extends Fragment {

    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_year_view_panel, container, false);
        //calendarView = (CalendarView) v.findViewById(R.id.year_view);
        return v;
    }
}
