package com.itime.team.itime.fragments;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.itime.team.itime.R;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.listener.RecyclerItemClickListener;
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
    private int lastPosition = -1;
    private int rowHeight;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private ImageButton imageButton;
    private Button mTodayButton;
    private int todayIndex;
    private Fragment currentFragment;
    private Fragment yearFragment;

    public static CalendarFragment newInstance(Bundle bundle) {

        Bundle args = bundle;
        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void fillData(Calendar c) {
        //Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -14 - 7 * 7);
        c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK));
        todayIndex = 8;
        for (int i = 0; i < 15; i++) {
            c.add(Calendar.DATE, 7);
            Map<String, Integer> map = new HashMap<>();
            map.put("year", c.get(Calendar.YEAR));
            map.put("month", c.get(Calendar.MONTH) + 1);
            map.put("day", c.get(Calendar.DAY_OF_MONTH));
            dates.add(map);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        imageButton = (ImageButton) getActivity().findViewById(R.id.event_list);
        if (getArguments() != null) {
            int month = getArguments().getInt("month");
            int year = getArguments().getInt("year");
            Calendar c = Calendar.getInstance();
            c.set(year, month - 1, 1);
            fillData(c);

        } else {
            fillData(Calendar.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        //CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        //rowHeight = calendarView.getLayoutParams().height;
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Calendar");


        mTodayButton = (Button) getActivity().findViewById(R.id.button_today);
        mTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView.scrollToPosition(todayIndex);
                //  recyclerView.getLayoutManager().scrollToPosition(todayIndex);
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(todayIndex - 1, 0);
                dates.clear();
                fillData(Calendar.getInstance());
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.scrollToPosition(6);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new CalendarAdapter(dates));

        reSetMenuOnClickListener(imageButton);


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getActivity(), position + "", Toast.LENGTH_LONG).show();
                if (lastPosition == -1) {
                    lastPosition = position;
                } else {
                    if (recyclerView.findViewHolderForAdapterPosition(lastPosition) != null) {
                        ((CalendarViewHolder) recyclerView.findViewHolderForLayoutPosition(lastPosition)).calendarView.removeSelectedDate();
                        Log.d("dateSelected", "Cleared" + lastPosition);
                    }
                    lastPosition = position;
                }
            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1) {
                    rowHeight = recyclerView.getChildAt(0).getHeight();
                    ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getMeasuredHeight(), rowHeight * 6);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                            layoutParams.height = value;
                            recyclerView.setLayoutParams(layoutParams);
                        }
                    });
                    animator.setDuration(500);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.start();
                }

            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
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
                            todayIndex++;
                            recyclerView.getAdapter().notifyItemInserted(0);
                        }

                        loading = true;
                    }
                }
            }
        });

        return view;
    }


    public void addItem() {
        Map<String, Integer> map = dates.get(dates.size() - 1);
        Calendar c = Calendar.getInstance();
        c.set(map.get("year"), map.get("month") - 1, map.get("day"));
        c.add(Calendar.DAY_OF_MONTH, 7);
        Map<String, Integer> mapToInsert = new HashMap<>();
        mapToInsert.put("year", c.get(Calendar.YEAR));
        mapToInsert.put("month", c.get(Calendar.MONTH) + 1);
        mapToInsert.put("day", c.get(Calendar.DAY_OF_MONTH));
        dates.add(mapToInsert);

    }

    public void insertItem() {
        Map<String, Integer> map = dates.get(0);
        Calendar c = Calendar.getInstance();
        c.set(map.get("year"), map.get("month") - 1, map.get("day"));
        c.add(Calendar.DAY_OF_MONTH, -7);
        Map<String, Integer> mapToInsert = new HashMap<>();
        mapToInsert.put("year", c.get(Calendar.YEAR));
        mapToInsert.put("month", c.get(Calendar.MONTH) + 1);
        mapToInsert.put("day", c.get(Calendar.DAY_OF_MONTH));
        dates.add(0, mapToInsert);
    }

    private class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

        private List<Map<String, Integer>> dates;

        public CalendarAdapter(List<Map<String, Integer>> dates) {
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

        public CalendarViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            calendarView = (CalendarView) itemView.findViewById(R.id.calendar_view);
            calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
                @Override
                public void dateSelected(float x, float y) {
                    Toast.makeText(getActivity(), x + " " + y, Toast.LENGTH_SHORT).show();
                }
            });
            //textView = (TextView) itemView.findViewById(R.id.test_text);
        }

        @Override
        public void onClick(View v) {
            //Log.i("testtesttest","onClick"+getAdapterPosition());
            Toast.makeText(getActivity(), getLayoutPosition() + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_calendar_option, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_year_view:
                Fragment fragment = new YearViewFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.detach(getFragmentManager().findFragmentById(R.id.realtab_content)).add(fragment,"list");
//                ft.replace(R.id.realtab_content, fragment);
                ft.hide(currentFragment);
                if (!yearFragment.isAdded()) {
                    ft.add(R.id.realtab_content, yearFragment);
                } else {
                    ft.show(yearFragment);
                }
                ft.addToBackStack(null);
                ft.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void reSetMenuOnClickListener(ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.ic_calendar_list_white);
        imageButton.setVisibility(View.VISIBLE);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((ImageButton) v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_calendar_list));

                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_calendar_list_white));
                }

                return false;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new EventListFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //ft.detach(getFragmentManager().findFragmentById(R.id.realtab_content)).add(fragment,"list");
                ft.hide(currentFragment);
                if (!fragment.isAdded()) {
                    ft.add(R.id.realtab_content, fragment);
                } else {
                    ft.show(fragment);
                }
                //ft.replace(R.id.realtab_content, fragment);

                ft.addToBackStack(null);
                ft.commit();
            }
        });

        mTodayButton.setVisibility(View.VISIBLE);
    }

    public void setCurrentFragment(Fragment fragment, Fragment yearFragment) {
        currentFragment = fragment;
        this.yearFragment = yearFragment;
    }

    public RecyclerView getRecyclerView(){
        return recyclerView;
    }

    public List<Map<String, Integer>> getList(){
        return dates;
    }

}

