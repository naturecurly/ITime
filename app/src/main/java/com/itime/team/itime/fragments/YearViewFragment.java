package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itime.team.itime.R;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.CalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by leveyleonhardt on 3/5/16.
 */
public class YearViewFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView mYearRecyclerView;
    private int[] yearViewId = new int[]{R.id.year_view_1, R.id.year_view_2, R.id.year_view_3, R.id.year_view_4, R.id.year_view_5, R.id.year_view_6, R.id.year_view_7, R.id.year_view_8, R.id.year_view_9, R.id.year_view_10, R.id.year_view_11, R.id.year_view_12};
    private List<Integer> list = new ArrayList<>();
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 2;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private Fragment fragment;
    private Fragment yearFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < 5; i++) {
            list.add(DateUtil.getYear() - 2 + i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_year_view, container, false);
        //calendarView = (CalendarView) v.findViewById(R.id.year_view);
        mYearRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_year_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mYearRecyclerView.setLayoutManager(linearLayoutManager);
        mYearRecyclerView.setAdapter(new YearViewAdapter(list));
        linearLayoutManager.scrollToPosition(2);
        mYearRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = linearLayoutManager.getChildCount();
                //Log.i("Vcount", visibleItemCount + "");
                totalItemCount = linearLayoutManager.getItemCount();
                //Log.i("Tcount", totalItemCount + "");
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                //Log.i("Fcount", firstVisibleItem + "");

                if (dy > 0) {


                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {

                        Log.i("...", "end called");
                        for (int i = 0; i < 5; i++) {
                            addItem();
                        }
                        recyclerView.getAdapter().notifyDataSetChanged();


                        loading = true;
                    }
                }
                if (dy < 0) {
                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && firstVisibleItem <= visibleThreshold) {
                        Log.i("...", "first called");
                        for (int i = 0; i < 5; i++) {
                            insertItem();
                            recyclerView.getAdapter().notifyItemInserted(0);
                        }
                        loading = true;
                    }
                }

            }
        });

        return v;
    }

    private void insertItem() {
        int year = list.get(0);
        list.add(0, year - 1);
    }

    private void addItem() {
        int year = list.get(list.size() - 1);
        list.add(year + 1);
    }


    public class YearViewAdapter extends RecyclerView.Adapter<YearViewHoder> {

        private List<Integer> list;

        public YearViewAdapter(List<Integer> list) {
            this.list = list;
        }

        @Override
        public YearViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_year_view_panel, parent, false);
            return new YearViewHoder(v);

        }

        @Override
        public void onBindViewHolder(YearViewHoder holder, int position) {
            //TextView textView = (TextView) holder.itemView.findViewById(R.id.year_text_view);
            //textView.setText(list.get(position));
            holder.textView.setText(list.get(position) + "");
            for (int i = 0; i < 12; i++) {
                holder.views[i].update(list.get(position), i + 1, 1);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class YearViewHoder extends RecyclerView.ViewHolder {
        private CalendarView[] views = new CalendarView[12];
        private TextView textView;

        public YearViewHoder(View itemView) {
            super(itemView);
            for (int i = 0; i < views.length; i++) {
                views[i] = (CalendarView) itemView.findViewById(yearViewId[i]);
                views[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                        CalendarView c = (CalendarView) v;
                        Toast.makeText(getActivity(), ((CalendarView) v).getmShowMonth() + "", Toast.LENGTH_SHORT).show();
                        int year = c.getmShowYear();
                        int month = c.getmShowMonth();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putInt("year", year);
                        bundle.putInt("month", month);
                        //fragmentTransaction.replace(R.id.realtab_content, CalendarFragment.newInstance(bundle));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, 1);
                        ((CalendarFragment) fragment).getList().clear();
                        ((CalendarFragment) fragment).fillData(calendar);
                        ((CalendarFragment) fragment).getRecyclerView().getAdapter().notifyDataSetChanged();
                        fragmentTransaction.hide(yearFragment);
                        fragmentTransaction.show(fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            textView = (TextView) itemView.findViewById(R.id.year_text_view);
        }
    }


    public void setCalendarFragment(Fragment fragment, Fragment yearFragment) {
        this.fragment = fragment;
        this.yearFragment = yearFragment;
    }
}
