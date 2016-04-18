package com.itime.team.itime.fragments;

import android.animation.ValueAnimator;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.EventsActivity;
import com.itime.team.itime.activities.MeetingDetaiHostlActivity;
import com.itime.team.itime.activities.MeetingDetailActivity;
import com.itime.team.itime.activities.NewEventActivity;
import com.itime.team.itime.activities.WeeklyActivity;
import com.itime.team.itime.activities.YearViewActivity;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.listener.RecyclerItemClickListener;
import com.itime.team.itime.listener.ScrollMeetingViewListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.DensityUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.CalendarView;
import com.itime.team.itime.views.MeetingScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {

    private static final int YEAR_REQUEST = 100;
    private RecyclerView recyclerView;
    private List<Map<String, Integer>> dates = new ArrayList<>();
    private int lastPosition = -1;
    private int rowHeight;
    public int previousTotal = 0;
    public boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private MeetingScrollView mScrollView;
    private boolean isExpended = false;
    private JSONArray mResponse;
    private boolean scroll_flag;

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    private LinearLayoutManager linearLayoutManager;
    private ImageButton imageButton;
    private Button mTodayButton;
    private int todayIndex;
    private Fragment currentFragment;
    private Fragment yearFragment;
    private RelativeLayout relativeLayout;
    private int clickedDay = 0;
    private int clickedMonth = 0;
    private int clickedYear = 0;
    private FragmentManager fm;
    private List<String> eventDateList = new ArrayList<>();
    private int screenWidth;
    private String mUserId;
    private final Calendar today = Calendar.getInstance();
    private CalendarView lastCalendarView = null;
    private CalendarView todayCalendarView = null;
    private TextView title;
    private Calendar selectedCalendar = Calendar.getInstance();

    public static CalendarFragment newInstance(Bundle bundle) {

        Bundle args = bundle;
        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void fillData(Calendar c) {
        //Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -7 * 7);
        c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK));
        todayIndex = 7;
        for (int i = 0; i < 15; i++) {
            c.add(Calendar.DATE, 7);
            Map<String, Integer> map = new HashMap<>();
            map.put("year", c.get(Calendar.YEAR));
            map.put("month", c.get(Calendar.MONTH) + 1);
            map.put("day", c.get(Calendar.DAY_OF_MONTH));
            dates.add(map);
        }

    }

    private void addFriendFromLink() {
        String s = getActivity().getIntent().getStringExtra("invitation");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchEvents(mUserId);
        fetchIgnoredEvents(mUserId);
        //analyseEvents(mResponse);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        setHasOptionsMenu(true);
        mUserId = User.ID;

//        if (getArguments() != null) {
//            int month = getArguments().getInt("month");
//            int year = getArguments().getInt("year");
//            Calendar c = Calendar.getInstance();
//            c.set(year, month - 1, 1);
//            fillData(c);

//        } else {
        fillData(Calendar.getInstance());

//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        //CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        //rowHeight = calendarView.getLayoutParams().height;
        title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        Calendar now = Calendar.getInstance();
        title.setText(now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1));


        mTodayButton = (Button) getActivity().findViewById(R.id.button_today);
        mTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView.scrollToPosition(todayIndex);
                //  recyclerView.getLayoutManager().scrollToPosition(todayIndex);
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(todayIndex - 2, 0);
                dates.clear();
                fillData(Calendar.getInstance());
                loading = true;
                previousTotal = 0;
                EventUtil.isTodayPressed = true;

//                linearLayoutManager.scrollToPositionWithOffset(5, 0);
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scrollToPosition(5);
//                EventUtil.isTodayPressed = false;
//                linearLayoutManager.scrollToPosition(5);
//                CalendarView todayCalendar = (CalendarView) linearLayoutManager.getChildAt(0).findViewById(R.id.calendar_view);
//                lastCalendarView = todayCalendar;
//                todayCalendar.setTodaySelected();
//                todayCalendarView.setTodaySelected();
//                Toast.makeText(getActivity(), todayCalendarView.getmShowMonth() + " " + todayCalendarView.getmShowDay(), Toast.LENGTH_SHORT).show();
//                if (todayCalendar.isTodayHasEvents()) {
//                    paintLowerPanel(today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR));
//                }
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.scrollToPositionWithOffset(5, 0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new CalendarAdapter(dates));
        //rowHeight =
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        rowHeight = screenWidth / 7;
        ViewGroup.LayoutParams recycler_layoutParams = recyclerView.getLayoutParams();
        recycler_layoutParams.height = rowHeight * 3;
        recyclerView.setLayoutParams(recycler_layoutParams);
        //recyclerView.setHorizontalFadingEdgeEnabled(true);
        //recyclerView.setLayoutParams(new ViewGroup.LayoutParams(recycler_layoutParams));
        relativeLayout = (RelativeLayout) view.findViewById(R.id.lower_relative_layout);
        addLowerViews(relativeLayout);
        //add views to relativeLayout


        //scrollView transaction
        mScrollView = (MeetingScrollView) view.findViewById(R.id.lower_scroll_view);
        mScrollView.setOnScrollViewListener(new ScrollMeetingViewListener() {
            @Override
            public void onScrollChanged(MeetingScrollView scrollView, int x, int y, int oldx, int oldy) {
                //Toast.makeText(getActivity(), "scrolled", Toast.LENGTH_SHORT).show();
                if (isExpended) {
                    isExpended = false;

                    ValueAnimator animator = ValueAnimator.ofInt(recyclerView.getMeasuredHeight(), rowHeight * 3);
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
//                if (scroll_flag) {
//                    scrollToDate(selectedCalendar);
//                    scroll_flag = false;
//                }
            }
        });
        imageButton = (ImageButton) getActivity().findViewById(R.id.event_list);
        reSetMenuOnClickListener(imageButton);


//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                //Toast.makeText(getActivity(), position + "", Toast.LENGTH_LONG).show();
//                if (lastPosition == -1) {
//                    lastPosition = position;
//                } else {
//                    if (recyclerView.findViewHolderForAdapterPosition(lastPosition) != null) {
//                        ((CalendarViewHolder) recyclerView.findViewHolderForLayoutPosition(lastPosition)).calendarView.removeSelectedDate();
//                        Log.d("dateSelected", "Cleared" + lastPosition);
//                    }
//                    lastPosition = position;
//                }
//            }
//        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1 && isExpended == false) {
                    isExpended = true;
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

                CalendarView firstVisibleView = (CalendarView) (recyclerView.getChildAt(0)).findViewById(R.id.calendar_view);
                if (firstVisibleView.whetherHasFirstDay()) {
                    CalendarView.Row[] rows = firstVisibleView.getRows();
                    TextView textView = (TextView) getActivity().findViewById(R.id.toolbar_title);
//                    CalendarView.Cell[] cells = rows[0].getCells();
//                    cells
                    textView.setText(firstVisibleView.getmShowYear() + "-" + firstVisibleView.getmShowMonth());
                }

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

    private void addLowerViews(RelativeLayout relativeLayout) {
        for (int i = 0; i < 24; i++) {
            TextView timeTextView = new TextView(getActivity());
            timeTextView.setText((i < 10 ? "0" + i : i + "") + ":00");
            timeTextView.setId(i + 1);
            //timeTextView.(DensityUtil.dip2px(getActivity(),6),DensityUtil.dip2px(getActivity(),6),DensityUtil.dip2px(getActivity(),6),DensityUtil.dip2px(getActivity(),25));
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                param.addRule(RelativeLayout.BELOW, i);
                param.setMargins(DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 15));
                relativeLayout.addView(timeTextView, param);
            }
            if (i == 0) {
                //param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                param.setMargins(DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 2), DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 15));

                relativeLayout.addView(timeTextView, param);
            }

            View lineView = new View(getActivity());
            lineView.setId(100 + i);
            RelativeLayout.LayoutParams line_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lineView.setBackgroundColor(Color.GRAY);
            line_param.height = DensityUtil.dip2px(getActivity(), 1);
            line_param.addRule(RelativeLayout.END_OF, i + 1);
            line_param.addRule(RelativeLayout.ALIGN_TOP, i + 1);
            line_param.setMargins(DensityUtil.dip2px(getActivity(), 6), DensityUtil.dip2px(getActivity(), 11), DensityUtil.dip2px(getActivity(), 0), DensityUtil.dip2px(getActivity(), 30));
            relativeLayout.addView(lineView, line_param);


        }
        for (int i = 0; i < 24; i++) {
            View occupiedView = new View(getActivity());
            occupiedView.setId(1000 + i);
//            occupiedView.setBackgroundColor(Color.BLACK);
            RelativeLayout.LayoutParams occupied_param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            occupied_param.addRule(RelativeLayout.ALIGN_TOP, 100 + i);
            occupied_param.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + i + 1);
            occupied_param.addRule(RelativeLayout.RIGHT_OF, i + 1);
            occupied_param.addRule(RelativeLayout.END_OF, i + 1);

            occupiedView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(getActivity(), NewEventActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
            relativeLayout.addView(occupiedView, occupied_param);
        }
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
//            if (holder.calendarView.isHasToday()) {
//                todayCalendarView = holder.calendarView;
//            }
            boolean flag = false;
            Map<String, Integer> c = dates.get(position);
            //Log.i("testCalendar", dates.get(position).get(Calendar.DATE) + "," + dates.get(position).get(Calendar.MONTH));
            //holder.calendarView = new CalendarView(getActivity(),dates.get(position).get(Calendar.YEAR),dates.get(position).get(Calendar.MONTH),dates.get(position).get(Calendar.DAY_OF_MONTH));
            //holder.calendarView = new CalendarView(getActivity(), 2016, 1, 1);
            //holder.calendarView = new CalendarView(getActivity());
            //c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK) - 7);
            boolean[] ifEvents = new boolean[7];
            Calendar cal = Calendar.getInstance();
            cal.set(c.get("year"), c.get("month") - 1, c.get("day"));
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // Log.d("testlist", eventDateList.get(0).toString());
            //analyseEvents(mResponse);
            for (int i = 0; i < 7; i++) {
//
//                if (eventDateList.contains(cal)) {
//                    ifEvents[i] = true;
//                }
                if (cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                    Toast.makeText(getActivity(), cal.get(Calendar.DAY_OF_MONTH) + "", Toast.LENGTH_SHORT).show();
                    flag = true;
                }
                if (Events.response != null && EventUtil.getEventFromDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)).size() > 0) {
                    ifEvents[i] = true;
                    Log.d("testdate", eventDateList.size() + "");
                } else if (Events.repeatEvent != null) {
//                    try {
//                        ifEvents[i] = EventUtil.hasRepeatEvent(cal);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }


                cal.add(Calendar.DAY_OF_MONTH, 1);

            }
            holder.calendarView.update(c.get("year"), c.get("month"), c.get("day"), ifEvents);
            if (flag) {
                todayCalendarView = holder.calendarView;
            }
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
                List<JSONObject> objectList;

                @Override
                public void dateSelected(float x, float y) {
                    if (EventUtil.isTodayPressed) {
                        EventUtil.isTodayPressed = false;
                        todayCalendarView.invalidate();
                    }
                    scroll_flag = true;

                    if (lastCalendarView == null) {
                        lastCalendarView = calendarView;
                    } else if (lastCalendarView == calendarView) {

                    } else {
                        lastCalendarView.removeSelectedDate();
                        lastCalendarView = calendarView;
                    }
                    calendarView.invalidate();
                    Calendar now = Calendar.getInstance();
                    final List<Integer> eventGroup = new ArrayList<Integer>();
                    CalendarView.Row row = calendarView.getRows()[0];
                    int day = Integer.valueOf(row.getCells()[DateUtil.analysePosition(x, rowHeight)].text);
                    int month = row.getCells()[DateUtil.analysePosition(x, rowHeight)].month;
                    int year = row.getCells()[DateUtil.analysePosition(x, rowHeight)].year;
                    selectedCalendar.set(year, month - 1, day);
                    if (now.get(Calendar.YEAR) == year && now.get(Calendar.MONTH) == month - 1 && now.get(Calendar.DAY_OF_MONTH) == day) {
                        int nowHour = now.get(Calendar.HOUR_OF_DAY);
                        View v = relativeLayout.findViewById(nowHour + 1);
                        Log.d("first_event_pixel", v.getY() + "");
                        mScrollView.smoothScrollTo(0, (int) v.getY());
                    }
                    if (row.getCells()[DateUtil.analysePosition(x, rowHeight)].hasEvents) {
                        relativeLayout.removeAllViews();
                        addLowerViews(relativeLayout);
                        //relativeLayout.invalidate();
                        Toast.makeText(getActivity(), "has event", Toast.LENGTH_SHORT).show();
                        objectList = EventUtil.getEventFromDate(day, month, year);
                        objectList = EventUtil.sortEvents(objectList);

                        JSONObject firstObject = objectList.get(0);
                        Calendar firstTimeCal = Calendar.getInstance();
                        try {
                            String firstTimeString = firstObject.getString("event_starts_datetime");
                            Date firstTimeDate = DateUtil.getLocalDateObject(firstTimeString);
                            firstTimeCal = DateUtil.getLocalDateObjectToCalendar(firstTimeDate);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("first_event_hour", firstTimeCal.get(Calendar.HOUR_OF_DAY) + "");
                        int firstPosition = firstTimeCal.get(Calendar.HOUR_OF_DAY);
                        View firstEventView = relativeLayout.findViewById(100 + firstPosition);
                        if (!(now.get(Calendar.YEAR) == year && now.get(Calendar.MONTH) == month - 1 && now.get(Calendar.DAY_OF_MONTH) == day)) {
                            mScrollView.smoothScrollTo(0, DensityUtil.dip2px(getActivity(), 30 * firstPosition));

                        }

                        String start = null;
                        String end = null;
                        try {
                            start = objectList.get(0).getString("event_starts_datetime");
                            end = objectList.get(0).getString("event_ends_datetime");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


//                        int flag = 0;
                        for (int i = 0; i < objectList.size(); ) {
                            eventGroup.add(i);
//                            i = flag;
                            //flag = i;

                            if (i == objectList.size() - 1) {
                                eventGroup.add(i);
                                i++;
                            }
                            for (int j = i + 1; j < objectList.size(); j++) {
                                try {
//                                    Date start = DateUtil.getLocalDateObject(objectList.get(i).getString("event_starts_datetime"));
//                                    Date end = DateUtil.getLocalDateObject(objectList.get(i).getString("event_ends_datetime"));
//                                    Date newStart = DateUtil.getLocalDateObject(objectList.get(j).getString("event_starts_datetime"));
//                                    Date newEnd = DateUtil.getLocalDateObject(objectList.get(j).getString("event_ends_datetime"));
                                    String newStart = objectList.get(j).getString("event_starts_datetime");
                                    String newEnd = objectList.get(j).getString("event_ends_datetime");

                                    if (newStart.compareTo(start) >= 0 && newStart.compareTo(end) < 0) {
                                        i++;
                                        //eventGroup.add(flag);
                                        if (j == objectList.size() - 1) {
                                            eventGroup.add(i);
                                            i++;
                                        }
                                        if (newEnd.compareTo(end) >= 0) {
                                            end = newEnd;
                                        }

                                    } else {
                                        eventGroup.add(i);
                                        if (j == objectList.size() - 1) {
                                            eventGroup.add(j);
                                            eventGroup.add(j);
                                            i = i + 2;
                                        }
                                        //i++;
                                        else {
                                            start = objectList.get(i + 1).getString("event_starts_datetime");
                                            end = objectList.get(i + 1).getString("event_ends_datetime");
                                            i++;
                                            break;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                        for (Integer integer : eventGroup) {
                            Log.d("testGroup", integer + " ");
                        }

                        for (int i = 0; i < eventGroup.size(); i += 2) {
                            if (eventGroup.get(i) == eventGroup.get(i + 1)) {
                                final int flag = i;
                                int starthour = 0;
                                int endhour = 0;
                                try {
                                    String dateString = objectList.get(eventGroup.get(i)).getString("event_starts_datetime");
                                    String dateStringEnd = objectList.get(eventGroup.get(i)).getString("event_ends_datetime");
                                    Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                                    Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                                    starthour = cal.get(Calendar.HOUR_OF_DAY);
                                    if (calEnd.get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH)) {
                                        endhour = 23;
                                    } else {
                                        endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                TextView eventView = new TextView(getActivity());
                                RelativeLayout.LayoutParams eventParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                eventView.setBackgroundColor(Color.CYAN);

                                eventView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String meeting_id = objectList.get(eventGroup.get(flag)).getString("meeting_id");
                                            //boolean isHost = objectList.get(eventGroup.get(flag)).getBoolean("is_host");
                                            String user_id = objectList.get(eventGroup.get(flag)).getString("user_id");
                                            String hostID = objectList.get(eventGroup.get(flag)).getString("host_id");
                                            if (!meeting_id.equals("")) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("meeting_id", meeting_id);
                                                bundle.putString("user_id", user_id);
                                                if (hostID.equals(User.ID)){
                                                    Intent intent = new Intent(getActivity(), MeetingDetaiHostlActivity.class);
                                                    intent.putExtra("arg_meeting_id",meeting_id);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                                                    intent.putExtra("arg_meeting_id",meeting_id);
                                                    Log.i("userId", user_id);
                                                    startActivity(intent);
                                                }
                                             /*
                                            * add intent to start activity here
                                            * */
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                try {
                                    eventView.setText(objectList.get(eventGroup.get(i)).getString("event_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                eventParam.height = DensityUtil.dip2px(getActivity(), 30);
//                                eventParam.width = DensityUtil.dip2px(getActivity(), 50);
                                eventParam.addRule(RelativeLayout.ALIGN_TOP, 100 + starthour);
                                eventParam.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                                eventParam.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                                eventParam.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);
                                eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
                                eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);

                                eventParam.setMargins(DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1));
                                relativeLayout.addView(eventView, eventParam);
                            } else {
                                int overlapNumber = eventGroup.get(i + 1) - eventGroup.get(i) + 1;
                                int startNumber = eventGroup.get(i);
                                int flag = 0;
                                for (int num = eventGroup.get(i); num <= eventGroup.get(i + 1); num++) {
                                    int starthour = 0;
                                    int endhour = 0;
                                    try {
                                        String dateString = objectList.get(num).getString("event_starts_datetime");
                                        String dateStringEnd = objectList.get(num).getString("event_ends_datetime");
                                        Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                                        Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                                        starthour = cal.get(Calendar.HOUR_OF_DAY);
                                        endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    TextView eventView = new TextView(getActivity());

                                    try {
                                        eventView.setText(objectList.get(num).getString("event_name"));
                                        final String meeting_id = objectList.get(eventGroup.get(num)).getString("meeting_id");
                                        final Boolean isHost = objectList.get(eventGroup.get(num)).getBoolean("is_host");
                                        final String user_id = objectList.get(eventGroup.get(num)).getString("user_id");
                                        final String hostID = objectList.get(eventGroup.get(flag)).getString("host_id");
                                        eventView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!meeting_id.equals("")) {


                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("meeting_id", meeting_id);
                                                    bundle.putString("user_id", user_id);
                                                    if (hostID.equals(User.ID)){
                                                        Intent intent = new Intent(getActivity(), MeetingDetaiHostlActivity.class);
                                                        intent.putExtra("arg_meeting_id",meeting_id);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                                                        intent.putExtra("arg_meeting_id", meeting_id);
                                                        startActivity(intent);
                                                    }
                                             /*
                                            * add intent to start activity here
                                            * */
                                                }
                                            }


                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    RelativeLayout.LayoutParams eventParamOverlap = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    eventView.setBackgroundColor(Color.CYAN);
                                    TextView view = (TextView) relativeLayout.findViewById(1);
                                    float length = screenWidth - (view.getWidth() + DensityUtil.dip2px(getActivity(), 12));
//                                    eventParamOverlap.height = DensityUtil.dip2px(getActivity(), 30);
                                    eventParamOverlap.width = (int) (length / overlapNumber);
                                    eventParamOverlap.addRule(RelativeLayout.ALIGN_TOP, 100 + starthour);
                                    eventParamOverlap.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                                    eventParamOverlap.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                                    eventParamOverlap.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);

//                                    eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
//                                    eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);
                                    int leftMargin = (int) (flag * (length / overlapNumber));
                                    Log.d("leftMargin", leftMargin + "");
                                    eventParamOverlap.setMargins(leftMargin, DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1));
                                    relativeLayout.addView(eventView, eventParamOverlap);
                                    flag++;
                                }
                            }
                        }


                    } else {
                        relativeLayout.removeAllViews();
                        addLowerViews(relativeLayout);

                    }


                    if (day == clickedDay && month == clickedMonth && year == clickedYear) {
                        //Toast.makeText(getActivity(), "double clicked", Toast.LENGTH_SHORT).show();
//                        FragmentTransaction ft = fm.beginTransaction();
//                        ft.replace(R.id.realtab_content, new WeeklyFragment());
//                        ft.addToBackStack(null);
//                        ft.commit();
                        clickedDay = 0;
                        clickedMonth = 0;
                        clickedYear = 0;
                        Intent intent = new Intent(getActivity(), WeeklyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("year", year);
                        bundle.putInt("month", month);
                        bundle.putInt("day", day);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    } else {
                        clickedDay = day;
                        clickedMonth = month;
                        clickedYear = year;
                    }
                    Toast.makeText(getActivity(), clickedDay + " " + clickedMonth + " " + clickedYear, Toast.LENGTH_SHORT).show();

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
//                Fragment fragment = new YearViewFragment();
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.hide(currentFragment);
//                if (!yearFragment.isAdded()) {
//                    ft.add(R.id.realtab_content, yearFragment);
//                } else {
//                    ft.show(yearFragment);
//                }
//                ft.commit();
//                title.setText("Years");
//                mTodayButton.setVisibility(View.GONE);
//                imageButton.setVisibility(View.GONE);
                Intent start_year_intent = new Intent(getActivity(), YearViewActivity.class);
                startActivityForResult(start_year_intent, YEAR_REQUEST);
                break;
            case R.id.add_event:
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                startActivity(intent);
                break;
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
//                Fragment fragment = new EventListFragment();
//                FragmentTransaction ft = fm.beginTransaction();
                //ft.detach(getFragmentManager().findFragmentById(R.id.realtab_content)).add(fragment,"list");
                Intent intent = new Intent(getActivity(), EventsActivity.class);
                startActivity(intent);
                //ft.replace(R.id.realtab_content, fragment);

                //ft.addToBackStack(null);
            }
        });

        mTodayButton.setVisibility(View.VISIBLE);
    }

    public void setCurrentFragment(Fragment fragment, Fragment yearFragment) {
        currentFragment = fragment;
        this.yearFragment = yearFragment;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public List<Map<String, Integer>> getList() {
        return dates;
    }


    public void fetchEvents(String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("local_events", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.SYNC;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.print("ttttttttttttttt");

                //mResponse = response;
                Events.response = EventUtil.initialEvents(response);
//                analyseEvents(response);
                Events.repeatEvent = EventUtil.getRepeatEventsFromEvents(Events.response);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.i("Event_response", response.toString());
//                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    public void fetchIgnoredEvents(String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("local_events_ignored", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.SYNC_IGNORED;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.print("ttttttttttttttt");

                //mResponse = response;
//                Events.response = response;
//                analyseEvents(response);
                EventUtil.getIgnoredEventsFromResponse(response);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.i("Event_response_ignored", response.toString());
//                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

//    public void analyseEvents(JSONArray response) {
//        for (int i = 0; i < response.length(); i++) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = response.getJSONObject(i);
//                String time = (String) jsonObject.get("event_starts_datetime");
//                Date date = DateUtil.getLocalDateObject(time);
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//                Log.d("testdate", cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
//                eventDateList.add(cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
//
//                //Log.d("Event_name", name);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }


    public void paintLowerPanel(int day, int month, int year) {

        List<Integer> eventGroup = new ArrayList<Integer>();
        relativeLayout.removeAllViews();
        addLowerViews(relativeLayout);
        //relativeLayout.invalidate();
        Toast.makeText(getActivity(), "has event", Toast.LENGTH_SHORT).show();
        List<JSONObject> objectList = EventUtil.getEventFromDate(day, month, year);
        objectList = EventUtil.sortEvents(objectList);

        JSONObject firstObject = objectList.get(0);
        Calendar firstTimeCal = Calendar.getInstance();
        try {
            String firstTimeString = firstObject.getString("event_starts_datetime");
            Date firstTimeDate = DateUtil.getLocalDateObject(firstTimeString);
            firstTimeCal = DateUtil.getLocalDateObjectToCalendar(firstTimeDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("first_event_hour", firstTimeCal.get(Calendar.HOUR_OF_DAY) + "");
        int firstPosition = firstTimeCal.get(Calendar.HOUR_OF_DAY);
        View firstEventView = relativeLayout.findViewById(100 + firstPosition);
        mScrollView.smoothScrollTo(0, DensityUtil.dip2px(getActivity(), 30 * firstPosition));

        String start = null;
        String end = null;
//                        for (int i = 0; i < objectList.size(); i++) {
//                            try {
//                                Date tempStart = DateUtil.getLocalDateObject(objectList.get(i).getString("event_starts_datetime"));
//                                Date tempEnd = DateUtil.getLocalDateObject(objectList.get(i).getString("event_ends_datetime"));
//                                eventTimeRagne.add(new Integer[]{tempStart.ge})
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
        try {
            start = objectList.get(0).getString("event_starts_datetime");
            end = objectList.get(0).getString("event_ends_datetime");

        } catch (JSONException e) {
            e.printStackTrace();
        }


//                        int flag = 0;
        for (int i = 0; i < objectList.size(); ) {
            eventGroup.add(i);
//                            i = flag;
            //flag = i;

            if (i == objectList.size() - 1) {
                eventGroup.add(i);
                i++;
            }
            for (int j = i + 1; j < objectList.size(); j++) {
                try {
//                                    Date start = DateUtil.getLocalDateObject(objectList.get(i).getString("event_starts_datetime"));
//                                    Date end = DateUtil.getLocalDateObject(objectList.get(i).getString("event_ends_datetime"));
//                                    Date newStart = DateUtil.getLocalDateObject(objectList.get(j).getString("event_starts_datetime"));
//                                    Date newEnd = DateUtil.getLocalDateObject(objectList.get(j).getString("event_ends_datetime"));
                    String newStart = objectList.get(j).getString("event_starts_datetime");
                    String newEnd = objectList.get(j).getString("event_ends_datetime");

                    if (newStart.compareTo(start) >= 0 && newStart.compareTo(end) < 0) {
                        i++;
                        //eventGroup.add(flag);
                        if (j == objectList.size() - 1) {
                            eventGroup.add(i);
                            i++;
                        }
                        if (newEnd.compareTo(end) >= 0) {
                            end = newEnd;
                        }

                    } else {
                        eventGroup.add(i);
                        if (j == objectList.size() - 1) {
                            eventGroup.add(j);
                            eventGroup.add(j);
                            i = i + 2;
                        }
                        //i++;
                        else {
                            start = objectList.get(i + 1).getString("event_starts_datetime");
                            end = objectList.get(i + 1).getString("event_ends_datetime");
                            i++;
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
        for (Integer integer : eventGroup) {
            Log.d("testGroup", integer + " ");
        }

        for (int i = 0; i < eventGroup.size(); i += 2) {
            if (eventGroup.get(i) == eventGroup.get(i + 1)) {
                int starthour = 0;
                int endhour = 0;
                try {
                    String dateString = objectList.get(eventGroup.get(i)).getString("event_starts_datetime");
                    String dateStringEnd = objectList.get(eventGroup.get(i)).getString("event_ends_datetime");
                    Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                    Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                    starthour = cal.get(Calendar.HOUR_OF_DAY);
                    endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TextView eventView = new TextView(getActivity());
                RelativeLayout.LayoutParams eventParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                eventView.setBackgroundColor(Color.CYAN);
                try {
                    eventView.setText(objectList.get(eventGroup.get(i)).getString("event_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                                eventParam.height = DensityUtil.dip2px(getActivity(), 30);
//                                eventParam.width = DensityUtil.dip2px(getActivity(), 50);
                eventParam.addRule(RelativeLayout.ALIGN_TOP, 100 + starthour);
                eventParam.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                eventParam.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                eventParam.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);
                eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
                eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);

                eventParam.setMargins(DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1));
                relativeLayout.addView(eventView, eventParam);
            } else {
                int overlapNumber = eventGroup.get(i + 1) - eventGroup.get(i) + 1;
                int startNumber = eventGroup.get(i);
                int flag = 0;
                for (int num = eventGroup.get(i); num <= eventGroup.get(i + 1); num++) {
                    int starthour = 0;
                    int endhour = 0;
                    try {
                        String dateString = objectList.get(num).getString("event_starts_datetime");
                        String dateStringEnd = objectList.get(num).getString("event_ends_datetime");
                        Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                        Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                        starthour = cal.get(Calendar.HOUR_OF_DAY);
                        endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView eventView = new TextView(getActivity());
                    try {
                        eventView.setText(objectList.get(num).getString("event_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RelativeLayout.LayoutParams eventParamOverlap = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    eventView.setBackgroundColor(Color.CYAN);
                    TextView view = (TextView) relativeLayout.findViewById(1);
                    float length = screenWidth - (view.getWidth() + DensityUtil.dip2px(getActivity(), 12));
//                                    eventParamOverlap.height = DensityUtil.dip2px(getActivity(), 30);
                    eventParamOverlap.width = (int) (length / overlapNumber);
                    eventParamOverlap.addRule(RelativeLayout.ALIGN_TOP, 100 + starthour);
                    eventParamOverlap.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                    eventParamOverlap.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                    eventParamOverlap.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);

//                                    eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
//                                    eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);
                    int leftMargin = (int) (flag * (length / overlapNumber));
                    Log.d("leftMargin", leftMargin + "");
                    eventParamOverlap.setMargins(leftMargin, DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), 1));
                    relativeLayout.addView(eventView, eventParamOverlap);
                    flag++;
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YEAR_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle date = data.getExtras();
                int month = data.getIntExtra("month", today.get(Calendar.MONTH) + 1);
                int year = data.getIntExtra("year", today.get(Calendar.YEAR));
                Toast.makeText(getActivity(), month + " " + year, Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, 1);
                getList().clear();
                fillData(calendar);
                this.loading = true;
                previousTotal = 0;
                linearLayoutManager.scrollToPositionWithOffset(5, 0);
                recyclerView.getAdapter().notifyDataSetChanged();
                title.setText(year + "-" + month);
            }
        }
    }

    public void scrollToDate(Calendar calendar) {
        getList().clear();
        fillData(calendar);
        this.loading = true;
        previousTotal = 0;
        linearLayoutManager.scrollToPositionWithOffset(5, 0);
        recyclerView.getAdapter().notifyDataSetChanged();

    }
}

