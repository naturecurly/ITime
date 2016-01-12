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

import com.itime.team.itime.activities.R;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.CalendarView;

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
//        viewPager = (ViewPager) container;
//        calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
//        calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
//
//            @Override
//            public void dateSelected(float x, float y) {
//                Log.i("AAA", x + " " + y);
//                textView = (TextView) getActivity().findViewById(R.id.text_display);
//                int xPosition, yPosition;
//                xPosition = DateUtil.analysePosition(x, calendarView.getmCellSpace());
//                yPosition = DateUtil.analysePosition(y, calendarView.getmCellSpace());
//                CalendarView.Row[] row = calendarView.getRows();
//                CalendarView.Cell[] cell = row[yPosition].getCells();
//                int selectedMonth = calendarView.getmShowMonth();
//                int selectedYear = calendarView.getmShowYear();
//                String selectedDay = cell[xPosition].getText();
//                if (cell[xPosition].getState() == CalendarView.State.PAST_MONTH_DAY) {
//                    selectedMonth--;
//                } else if (cell[xPosition].getState() == CalendarView.State.NEXT_MONTH_DAY) {
//                    selectedMonth++;
//                }
//                if (selectedMonth == 13) {
//                    selectedMonth = 1;
//                    selectedYear++;
//                } else if (selectedMonth == 0) {
//                    selectedMonth = 12;
//                    selectedYear--;
//                }
//                textView.setText(selectedDay + ", " + selectedMonth + ", " + selectedYear);
//            }
//        });
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
