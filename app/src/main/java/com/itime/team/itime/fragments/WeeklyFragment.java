package com.itime.team.itime.fragments;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.itime.team.itime.R;
import com.itime.team.itime.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leveyleonhardt on 3/10/16.
 */
public class WeeklyFragment extends Fragment {
    private WeekView mWeekView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_panel, container, false);
        mWeekView = (WeekView) view.findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {

            }
        });

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                return null;
            }
        });

// Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });
//        recyclerViewBody = (RecyclerView) view.findViewById(R.id.weekly_recycler_view_body);
//        nestedScrollView = (NestedScrollView) view.findViewById(R.id.weekly_scroll_view);
//        nestedScrollView.setFillViewport(true);
//        nestedScrollView.setNestedScrollingEnabled(true);
//        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewBody.setLayoutManager(linearLayoutManager);
//        recyclerViewBody.setAdapter(new WeeklyAdapter(list));
        return view;

    }

//    public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyViewHolder> {
//
//        private List<Integer> list;
//
//        public WeeklyAdapter(List<Integer> list) {
//            this.list = list;
//        }
//
//        @Override
//        public WeeklyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
//            View view = layoutInflater.inflate(R.layout.item_weekly_view, parent, false);
//
//            return new WeeklyViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(WeeklyViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//    }
//
//    public class WeeklyViewHolder extends RecyclerView.ViewHolder {
//
//        public WeeklyViewHolder(View itemView) {
//            super(itemView);
//        }
//    }

}
