package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.CalendarView;
import com.itime.team.itime.views.adapters.CalendarPagerAdapter;

/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarBodyFragment extends Fragment {
    private CalendarBodyFragment cbf;
    private ViewPager viewPager;
    private CalendarView calendarView;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_body, container, false);
        viewPager = (ViewPager) container;
        calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {

            @Override
            public void dateSelected(float x, float y) {
                Log.i("AAA", x + " " + y);
                textView = (TextView) getActivity().findViewById(R.id.text_display);
                textView.setText(DateUtil.analysePosition(x, calendarView.getmCellSpace()) + " " + DateUtil.analysePosition(y, calendarView.getmCellSpace()));
            }
        });
        //CalendarBodyFragment cbf = (CalendarBodyFragment) viewPager.getAdapter().instantiateItem(container, 0);

        return view;
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        cbf = ((CalendarPagerAdapter) viewPager.getAdapter()).getFragment(viewPager.getCurrentItem());
//        CalendarView cv = (CalendarView) cbf.getView();
//
//
//        //CalendarView cv = cbf.getCalendarView();
//        Log.i("TTTTT", cv.getmShowDay() + " " + cv.getmShowMonth() + " " + cv.getmShowYear());
//    }
}
