package com.itime.team.itime.fragments;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itime.team.itime.R;
import com.itime.team.itime.activities.MainActivity;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.views.CalendarView;
import com.itime.team.itime.views.MonthCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by leveyleonhardt on 3/5/16.
 */
public class YearViewFragment extends Fragment {

    private MonthCalendarView calendarView;
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
//        Button mToday = (Button) getActivity().findViewById(R.id.button_today);
//        mToday.setVisibility(View.GONE);
//        ImageButton mEventList = (ImageButton) getActivity().findViewById(R.id.event_list);
//        mEventList.setVisibility(View.GONE);
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
        private MonthCalendarView[] views = new MonthCalendarView[12];
        private TextView textView;

        public YearViewHoder(View itemView) {
            super(itemView);
            for (int i = 0; i < views.length; i++) {
                views[i] = (MonthCalendarView) itemView.findViewById(yearViewId[i]);
                views[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                        MonthCalendarView c = (MonthCalendarView) v;
//                        Toast.makeText(getActivity(), ((MonthCalendarView) v).getmShowMonth() + "", Toast.LENGTH_SHORT).show();
                        int year = c.getmShowYear();
                        int month = c.getmShowMonth();
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        Intent intent = getActivity().getIntent();
                        Bundle bundle = new Bundle();
                        bundle.putInt("year", year);
                        bundle.putInt("month", month);
                        intent.putExtras(bundle);
                        getActivity().setResult(getActivity().RESULT_OK,intent);
//                        startActivity(intent);
                        getActivity().finish();
//                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

//                        //fragmentTransaction.replace(R.id.realtab_content, CalendarFragment.newInstance(bundle));
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(year, month - 1, 1);
//                        ((CalendarFragment) fragment).getList().clear();
//                        ((CalendarFragment) fragment).fillData(calendar);
//                        ((CalendarFragment) fragment).loading = true;
//                        ((CalendarFragment) fragment).previousTotal = 0;
//                        ((CalendarFragment) fragment).getLinearLayoutManager().scrollToPositionWithOffset(5, 0);
//                        ((CalendarFragment) fragment).getRecyclerView().getAdapter().notifyDataSetChanged();
//                        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
//                        title.setText(year + "-" + month);
//                        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.event_list);
//                        imageButton.setVisibility(View.VISIBLE);
//                        Button mTodayButton = (Button) getActivity().findViewById(R.id.button_today);
//                        mTodayButton.setVisibility(View.VISIBLE);
//                        imageButton.setVisibility(View.VISIBLE);
//                        fragmentTransaction.hide(yearFragment);
//                        fragmentTransaction.show(fragment);
//                        //fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
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
