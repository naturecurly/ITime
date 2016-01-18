package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.activities.R;

import com.itime.team.itime.views.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Calendar> dates = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 50; i++) {
            dates.add(Calendar.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CalendarAdapter(dates));
        return view;
    }

    private class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

        private List<Calendar> dates;

        public CalendarAdapter(List<Calendar> dates) {
            this.dates = dates;
        }

        @Override
        public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.fragment_calendar_body, parent, false);
            return new CalendarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CalendarViewHolder holder, int position) {
            Calendar c = dates.get(position);
            //holder.calendarView = new CalendarView(getActivity()).update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            holder.calendarView = new CalendarView(getActivity());
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }

    private class CalendarViewHolder extends RecyclerView.ViewHolder {
        private CalendarView calendarView;
        private TextView textView;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            calendarView = (CalendarView) itemView.findViewById(R.id.calendar_view);
            //textView = (TextView) itemView.findViewById(R.id.test_text);
        }
    }
}

