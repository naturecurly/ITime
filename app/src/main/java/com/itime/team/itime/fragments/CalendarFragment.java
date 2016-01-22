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
import android.widget.Toast;

import com.itime.team.itime.activities.R;

import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.views.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Map<String, Integer>> dates = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -14);
        c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK));
        for (int i = 0; i < 15; i++) {
            c.add(Calendar.DATE, 7);
            Map<String, Integer> map = new HashMap<>();
            map.put("year", c.get(Calendar.YEAR));
            map.put("month", c.get(Calendar.MONTH) + 1);
            map.put("day", c.get(Calendar.DAY_OF_MONTH));
            dates.add(map);
            //c.add(Calendar.DATE, 21);
            //Log.i("testtest",c.get(Calendar.DAY_OF_MONTH)+"");
        }

        for (Map<String, Integer> calendar : dates) {
            Log.i("testest", calendar.get("day") + "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CalendarAdapter(dates, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {

            }
        }));
        return view;
    }

    private class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

        private List<Map<String, Integer>> dates;
        private RecyclerViewClickListener itemListener;

        public CalendarAdapter(List<Map<String, Integer>> dates, RecyclerViewClickListener itemListener) {
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
            Map<String, Integer> c = dates.get(position);
            //Log.i("testCalendar", dates.get(position).get(Calendar.DATE) + "," + dates.get(position).get(Calendar.MONTH));
            //holder.calendarView = new CalendarView(getActivity(),dates.get(position).get(Calendar.YEAR),dates.get(position).get(Calendar.MONTH),dates.get(position).get(Calendar.DAY_OF_MONTH));
            //holder.calendarView = new CalendarView(getActivity(), 2016, 1, 1);
            //holder.calendarView = new CalendarView(getActivity());
            //c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK) - 7);
            holder.calendarView.update(c.get("year"), c.get("month"), c.get("day"));

            //holder.calendarView.update(c);
            //holder.calendarView.update(dates.get(position).get(Calendar.YEAR),dates.get(position).get(Calendar.MONTH),dates.get(position).get(Calendar.DAY_OF_MONTH));

        }

        @Override
        public int getItemCount() {
            return dates.size();
        }
    }

    private class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CalendarView calendarView;
        private TextView textView;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            calendarView = (CalendarView) itemView.findViewById(R.id.calendar_view);
            //textView = (TextView) itemView.findViewById(R.id.test_text);
        }

        @Override
        public void onClick(View v) {
            //Log.i("testtesttest","onClick"+getAdapterPosition());
            Toast.makeText(getActivity(), getLayoutPosition() + "", Toast.LENGTH_LONG);
        }
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(View v, int position);
    }
}

