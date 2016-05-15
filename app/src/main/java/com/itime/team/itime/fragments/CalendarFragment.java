package com.itime.team.itime.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.EventsActivity;
import com.itime.team.itime.activities.EventsDetailActivity;
import com.itime.team.itime.activities.MeetingDetaiHostlActivity;
import com.itime.team.itime.activities.MeetingDetailActivity;
import com.itime.team.itime.activities.NewEventActivity;
import com.itime.team.itime.activities.WeeklyActivity;
import com.itime.team.itime.activities.YearViewActivity;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.listener.ScrollMeetingViewListener;
import com.itime.team.itime.listener.ScrollViewInterceptTouchListener;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.utils.CalendarTypeUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.DensityUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.ITimePreferences;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.CalendarView;
import com.itime.team.itime.views.CustomizedTextView;
import com.itime.team.itime.views.EventsScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by leveyleonhardt on 12/17/15.
 */
public class CalendarFragment extends Fragment {
    private static final int JUMP_TO_EVENT_LIST = 103;
    private String action = "com.itime.team.itime.registerReciver";
    private IntentFilter filter = new IntentFilter();
    private static final int YEAR_REQUEST = 100;
    private static final int NEW_EVENT_REQUEST = 101;
    private static final int EDIT_EVENT_REQUEST = 102;
    private static final int CHANGE_MEETING = 103;
    private RecyclerView recyclerView;
    private List<Map<String, Integer>> dates = new ArrayList<>();
    private int lastPosition = -1;
    private int rowHeight;
    public int previousTotal = 0;
    public boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private EventsScrollView mScrollView;
    private boolean isExpended = false;
    private JSONArray mResponse;
    private boolean scroll_flag;
    private boolean isPress;
    private boolean shouldScrollCalendar;
    private int firstEventHour = 0;

    public String getTitle_string() {
        return title_string;
    }

    private String title_string;

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
    private boolean isStop = false;
    private int loadNum = 0;
    private int selectedPosition = 6;


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
        Events.daySelected = today.get(Calendar.DAY_OF_MONTH) + "-" + (today.get(Calendar.MONTH) + 1) + "-" + today.get(Calendar.YEAR);
        fetchEvents(mUserId, true);
        Log.d("row_height", rowHeight + "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
        setHasOptionsMenu(true);
        mUserId = User.ID;
        fillData(Calendar.getInstance());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        //CalendarView calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        //rowHeight = calendarView.getLayoutParams().height;
        title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        Calendar now = Calendar.getInstance();
        title_string = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1);
        title.setText(title_string);

        mTodayButton = (Button) getActivity().findViewById(R.id.button_today);
        mTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView.scrollToPosition(todayIndex);
                //  recyclerView.getLayoutManager().scrollToPosition(todayIndex);
                int day = today.get(Calendar.DAY_OF_MONTH);
                int month = today.get(Calendar.MONTH) + 1;
                int year = today.get(Calendar.YEAR);
                clickedDay = day;
                clickedMonth = month;
                clickedYear = year;

                Events.daySelected = day + "-" + month + "-" + year;
                title.setText(year + "-" + month);
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(todayIndex - 2, 0);
                dates.clear();
                fillData(Calendar.getInstance());
                loading = true;
                previousTotal = 0;
                EventUtil.isTodayPressed = true;

//                linearLayoutManager.scrollToPositionWithOffset(5, 0);
                loadNum = 0;
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scrollToPosition(5);
                selectedPosition = 6;
                paintLowerPanel(today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR), false);
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
                Log.d("top", recyclerView.getChildAt(0).getTop() + "");
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.scrollToPositionWithOffset(5, 0);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new CalendarAdapter(dates));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
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
        mScrollView = (EventsScrollView) view.findViewById(R.id.lower_scroll_view);
        mScrollView.setOnInterceptTouchListener(new ScrollViewInterceptTouchListener() {
            @Override
            public void touchEventHappend(ScrollView scrollView, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    isPress = false;
                }
            }
        });
        mScrollView.setOnScrollViewListener(new ScrollMeetingViewListener() {

            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
                //Toast.makeText(getActivity(), "scrolled", Toast.LENGTH_SHORT).show();
//                Log.d("if_scroll", selectedPosition + "");

                if (isExpended && isPress == false) {
                    isExpended = false;
                    if (shouldScrollCalendar) {
                        linearLayoutManager.scrollToPositionWithOffset(selectedPosition - 1, 0);
                        shouldScrollCalendar = false;
                    }
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
                    shouldScrollCalendar = true;
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
                if (newState == 0) {
                    isStop = true;
                    recyclerView.getAdapter().notifyDataSetChanged();
                    CalendarView firstVisibleView = (CalendarView) (recyclerView.getChildAt(2)).findViewById(R.id.calendar_view);
                    EventUtil.excuteAsyncTask(firstVisibleView.getmShowMonth(), firstVisibleView.getmShowYear());


                }

            }

            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isStop = false;
                CalendarView firstVisibleView = (CalendarView) (recyclerView.getChildAt(2)).findViewById(R.id.calendar_view);
                if (firstVisibleView.whetherHasFirstDay()) {
                    CalendarView.Row[] rows = firstVisibleView.getRows();
//                    TextView textView = (TextView) getActivity().findViewById(R.id.toolbar_title);
//                    CalendarView.Cell[] cells = rows[0].getCells();
//                    cells
                    title_string = firstVisibleView.getmShowYear() + "-" + firstVisibleView.getmShowMonth();
                    title.setText(title_string);
//                    textView.setText(firstVisibleView.getmShowYear() + "-" + firstVisibleView.getmShowMonth());
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
                            selectedPosition++;
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
            timeTextView.setTextColor(Color.GRAY);
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
            lineView.setBackgroundColor(Color.LTGRAY);
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
            holder.position = position;
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

                if (isStop || loadNum < 64) {
                    loadNum += 1;
                    if (Events.daysHaveEvents.contains(cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR))) {
                        ifEvents[i] = true;
//                        Log.d("testdate", eventDateList.size() + "");
                    } else if (Events.repeatEvent.size() > 0) {
                        try {
                            boolean hasRepeat = EventUtil.hasRepeatEvent(cal);
                            if (hasRepeat) {
                                Events.daysHaveEvents.add(cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR));
                            }
                            ifEvents[i] = hasRepeat;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
//                    Log.d("load_num", loadNum + "");
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
        private int position;
        private View itemView;

        public CalendarViewHolder(final View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            calendarView = (CalendarView) itemView.findViewById(R.id.calendar_view);
            calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
                List<JSONObject> objectList = new ArrayList<JSONObject>();

                @Override
                public void dateSelected(float x, float y) {
                    isPress = true;
                    int firstVisiblePosition = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
                    selectedPosition = recyclerView.getChildAdapterPosition(itemView);

                    if (recyclerView.getChildLayoutPosition(itemView) - firstVisiblePosition >= 3) {
//                        recyclerView.scrollBy(0, rowHeight * 3);
                        Log.d("position", "low 3 rows" + selectedPosition);

                        shouldScrollCalendar = true;
                    }
                    CalendarView.Row row = calendarView.getRows()[0];
                    int day = Integer.valueOf(row.getCells()[DateUtil.analysePosition(x, rowHeight)].text);
                    int month = row.getCells()[DateUtil.analysePosition(x, rowHeight)].month;
                    int year = row.getCells()[DateUtil.analysePosition(x, rowHeight)].year;
                    for (int n = 0; n < 6; n++) {
                        View v = recyclerView.getChildAt(n);
                        if (v != null) {
                            v.findViewById(R.id.calendar_view).invalidate();
                        }
                    }
//                    recyclerView.getAdapter().notifyItemRangeChanged(position - 3, position + 3, null);

                    Events.daySelected = day + "-" + month + "-" + year;
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
                    Events.daySelected = day + "-" + month + "-" + year;

                    calendarView.invalidate();
                    Calendar now = Calendar.getInstance();

                    final List<Integer> eventGroup = new ArrayList<Integer>();

//                    excuteAsyncTask(month, year);
                    selectedCalendar.set(year, month - 1, day);
                    if (now.get(Calendar.YEAR) == year && now.get(Calendar.MONTH) == month - 1 && now.get(Calendar.DAY_OF_MONTH) == day) {
                        int nowHour = now.get(Calendar.HOUR_OF_DAY);
                        View v = relativeLayout.findViewById(nowHour + 1);
                        Log.d("first_event_pixel", v.getY() + "");
                        mScrollView.smoothScrollTo(0, (int) v.getY());
                    }
                    if (row.getCells()[DateUtil.analysePosition(x, rowHeight)].hasEvents) {
                        paintLowerPanel(day, month, year, true);
                    } else {
                        firstEventHour = 0;
                        relativeLayout.removeAllViews();
                        addLowerViews(relativeLayout);

                    }


                    if (day == clickedDay && month == clickedMonth && year == clickedYear) {
                        //Toast.makeText(getActivity(), "double clicked", Toast.LENGTH_SHORT).show();
//                        FragmentTransaction ft = fm.beginTransaction();
//                        ft.replace(R.id.realtab_content, new WeeklyFragment());
//                        ft.addToBackStack(null);
//                        ft.commit();
                        if (ifFinishAsyncTask(clickedMonth, clickedYear)) {
                            clickedDay = 0;
                            clickedMonth = 0;
                            clickedYear = 0;
                            Intent intent = new Intent(getActivity(), WeeklyActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("year", year);
                            bundle.putInt("month", month);
                            bundle.putInt("day", day);
                            bundle.putInt("hour", firstEventHour);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Please wait...", Toast.LENGTH_SHORT).show();
                        }

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
                Intent start_year_intent = new Intent(getActivity(), YearViewActivity.class);
                startActivityForResult(start_year_intent, YEAR_REQUEST);
                break;
            case R.id.add_event:
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("selected_date", Events.daySelected);
//                intent.putExtras(bundle);
                startActivityForResult(intent, NEW_EVENT_REQUEST);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reSetMenuOnClickListener(ImageButton imageButton) {
        imageButton.setImageResource(R.drawable.ic_event_search);
        imageButton.setVisibility(View.VISIBLE);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((ImageButton) v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_event_search_black));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((ImageButton) v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_event_search));
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
                startActivityForResult(intent, JUMP_TO_EVENT_LIST);
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


    public void fetchEvents(String userId, final boolean update) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("local_events", "");
            jsonObject.put("if_sync_event", 1);
            jsonObject.put("if_sync_event_ignore", 1);
            jsonObject.put("if_sync_calendar_type", 1);
            jsonObject.put("if_sync_preference", 0);
            jsonObject.put("local_events_ignore", "");
            jsonObject.put("local_preferences", "");
            jsonObject.put("local_calendar_types", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.SYNCS;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Events.rawEvents = EventUtil.processRawEvents(response.getJSONArray("events"));
                    Events.calendarTypeList = LoganSquare.parseList(response.getJSONArray("calendar_types").toString(), ParcelableCalendarType.class);
                    CalendarTypeUtil.sortCalendarType();
                    Events.notShownId = EventUtil.getNotShownCalendarId();
                    Events.response = EventUtil.initialEvents(response.getJSONArray("events"));
                    Events.ignoredEventMap = EventUtil.processIgnoredEvents(response.getJSONArray("events_ignore"));
                    EventUtil.excuteAsyncTask(today.get(Calendar.MONTH) + 1, today.get(Calendar.YEAR));
                    recyclerView.getAdapter().notifyDataSetChanged();
                    Log.i("Event_response", response.getJSONArray("events").toString());
                    Log.i("Calendar_type", response.getJSONArray("calendar_types").toString());
                    Log.i("ignored_response", response.getJSONArray("events_ignore").toString());
                    if (update == true) {
                        String dateSeleted = Events.daySelected;
                        paintLowerPanel(Integer.valueOf(dateSeleted.split("-")[0]), Integer.valueOf(dateSeleted.split("-")[1]), Integer.valueOf(dateSeleted.split("-")[2]), false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
//                    Log.d("issues", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Network Issues", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }


    private void paintLowerPanel(int day, int month, int year, boolean hasEvent) {
        final List<JSONObject> objectList = EventUtil.sortEvents(EventUtil.getEventFromDate(day, month, year));
        List<Integer> eventGroup = new ArrayList<>();
        if (hasEvent || objectList.size() > 0) {
            relativeLayout.removeAllViews();
            addLowerViews(relativeLayout);
            Toast.makeText(getActivity(), "has event", Toast.LENGTH_SHORT).show();
            JSONObject firstObject = new JSONObject();
            if (objectList.size() != 0) {
                firstObject = objectList.get(0);
            }
//                        Log.d("whetherValid", objectList.toString());
            Calendar firstTimeCal = Calendar.getInstance();
            try {
                String firstTimeString = firstObject.getString("event_starts_datetime");
                Date firstTimeDate = DateUtil.getLocalDateObject(firstTimeString);
                firstTimeCal = DateUtil.getLocalDateObjectToCalendar(firstTimeDate);
                firstEventHour = firstTimeCal.get(Calendar.HOUR_OF_DAY);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("first_event_hour", firstTimeCal.get(Calendar.HOUR_OF_DAY) + "");
            final int firstPosition = firstTimeCal.get(Calendar.HOUR_OF_DAY);
            View firstEventView = relativeLayout.findViewById(100 + firstPosition);
            if (!(today.get(Calendar.YEAR) == year && today.get(Calendar.MONTH) == month - 1 && today.get(Calendar.DAY_OF_MONTH) == day)) {
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
            final List<Integer> eventGroupFinal = eventGroup;
            for (Integer integer : eventGroup) {
                Log.d("testGroup", integer + " ");
            }

            for (int i = 0; i < eventGroup.size(); i += 2) {
                if (eventGroup.get(i) == eventGroup.get(i + 1)) {
                    final int flag = i;
                    int starthour = 0;
                    int startmin = 0;
                    int endhour = 0;
                    int endmin = 0;
                    JSONObject jsonObject = objectList.get(eventGroup.get(i));

                    try {
                        String dateString = objectList.get(eventGroup.get(i)).getString("event_starts_datetime");
                        String dateStringEnd = objectList.get(eventGroup.get(i)).getString("event_ends_datetime");
                        Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                        Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                        starthour = cal.get(Calendar.HOUR_OF_DAY);
                        startmin = cal.get(Calendar.MINUTE);
//                                    if (calEnd.get(Calendar.DAY_OF_MONTH) > cal.get(Calendar.DAY_OF_MONTH)) {
//                                        endhour = 23;
//                                    } else {
                        endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                        endmin = calEnd.get(Calendar.MINUTE);
//                                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int durationMin = (60 * endhour + endmin) - (60 * starthour + startmin);
                    if (durationMin < 35) {
                        durationMin = 35;
                    }
                    CustomizedTextView eventView = new CustomizedTextView(getActivity());
                    eventView.setIncludeFontPadding(true);
                    eventView.setPadding(DensityUtil.dip2px(getActivity(), 4), 0, 0, 0);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        eventView.setZ(DensityUtil.dip2px(getActivity(), 5));
                    }
                    RelativeLayout.LayoutParams eventParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(getActivity(), (float) (4.0 / 6.0 * durationMin) - 4));
                    try {
                        if (!jsonObject.getString("meeting_id").equals("")) {
                            eventView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.event_color_02));
                        } else {
                            eventView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.event_color_01));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    eventView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            View pressedView = mScrollView.getPressedSubView();
                            if (pressedView != null) {
                                pressedView.performLongClick();
                            }
                            return true;
                        }
                    });

                    eventView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                String meeting_id = objectList.get(eventGroupFinal.get(flag)).getString("meeting_id");
                                //boolean isHost = objectList.get(eventGroup.get(flag)).getBoolean("is_host");
                                String user_id = objectList.get(eventGroupFinal.get(flag)).getString("user_id");
                                String hostID = objectList.get(eventGroupFinal.get(flag)).getString("host_id");
                                String eventID = objectList.get(eventGroupFinal.get(flag)).getString("event_id");
                                Log.i("event", eventID);
                                if (!meeting_id.equals("")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("meeting_id", meeting_id);
                                    bundle.putString("user_id", user_id);
                                    if (hostID.equals(User.ID)) {
                                        String alert = objectList.get(eventGroupFinal.get(flag)).getString("event_alert");
                                        String calendarId = objectList.get(eventGroupFinal.get(flag)).getString("calendar_id");
                                        Intent intent = new Intent(getActivity(), MeetingDetaiHostlActivity.class);
                                        intent.putExtra("arg_meeting_id", meeting_id);
                                        intent.putExtra("event_id", eventID);
                                        intent.putExtra("host_id", hostID);
                                        intent.putExtra("event_alert",alert);
                                        intent.putExtra("calendar_id",calendarId);
                                        startActivityForResult(intent, CHANGE_MEETING);
//                                        startActivity(intent);
                                    } else {
                                        String alert = objectList.get(eventGroupFinal.get(flag)).getString("event_alert");
                                        String calendarId = objectList.get(eventGroupFinal.get(flag)).getString("calendar_id");
                                        Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                                        intent.putExtra("arg_meeting_id", meeting_id);
                                        intent.putExtra("event_id", eventID);
                                        intent.putExtra("host_id", hostID);
                                        intent.putExtra("event_alert",alert);
                                        intent.putExtra("calendar_id",calendarId);
                                        startActivityForResult(intent, CHANGE_MEETING);
//                                        startActivity(intent);
                                    }
                                             /*
                                            * add intent to start activity here
                                            * */
                                } else {
                                    String event_name = objectList.get(eventGroupFinal.get(flag)).getString("event_name");
                                    String venue = objectList.get(eventGroupFinal.get(flag)).getString("event_venue_location");
//                                                String dep_time = objectList.get(eventGroup.get(flag)).getString("event_last_sug_dep_time");
                                    String start_time = objectList.get(eventGroupFinal.get(flag)).getString("event_starts_datetime");
                                    String end_time = objectList.get(eventGroupFinal.get(flag)).getString("event_ends_datetime");
//                                                boolean punctual = objectList.get(eventGroup.get(flag)).getBoolean("event_is_punctual");
                                    String repeat_type = objectList.get(eventGroupFinal.get(flag)).getString("event_repeats_type");
                                    String alert = objectList.get(eventGroupFinal.get(flag)).getString("event_alert");
                                    String calendarId = objectList.get(eventGroupFinal.get(flag)).getString("calendar_id");
                                    String calendarType = "";
                                    String jsonString = objectList.get(eventGroupFinal.get(flag)).toString();
                                    ParcelableCalendarType parcelableCalendarType = new ParcelableCalendarType();
                                    for (ParcelableCalendarType calType : Events.calendarTypeList) {
                                        if (calType.calendarId.equals(calendarId)) {
                                            calendarType = calType.calendarName;
                                            parcelableCalendarType = calType;
                                        }
                                    }
                                    Intent detailIntent = new Intent(getActivity(), EventsDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("event_name", event_name);
                                    bundle.putString("venue", venue);
                                    bundle.putString("start_time", start_time);
                                    bundle.putString("end_time", end_time);
                                    bundle.putString("repeat_type", repeat_type);
                                    bundle.putString("alert", alert);
                                    bundle.putString("calendar_type", calendarType);
                                    bundle.putParcelable("calendar_type_pacelable", parcelableCalendarType);
                                    bundle.putString("event_id", eventID);
                                    bundle.putString("json", jsonString);
                                    detailIntent.putExtras(bundle);
                                    startActivityForResult(detailIntent, EDIT_EVENT_REQUEST);
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
//                                eventParam.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                    eventParam.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                    eventParam.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);
                    eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
                    eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);

                    eventParam.setMargins(DensityUtil.dip2px(getActivity(), 1), DensityUtil.dip2px(getActivity(), (float) (startmin * 4.0 / 6.0) + 2), DensityUtil.dip2px(getActivity(), 0), DensityUtil.dip2px(getActivity(), 1));
                    relativeLayout.addView(eventView, eventParam);
                } else {
                    int overlapNumber = eventGroup.get(i + 1) - eventGroup.get(i) + 1;
                    int startNumber = eventGroup.get(i);
                    int flag = 0;
                    for (int num = eventGroup.get(i); num <= eventGroup.get(i + 1); num++) {
                        int starthour = 0;
                        int startmin = 0;
                        int endhour = 0;
                        int endmin = 0;
                        try {
                            String dateString = objectList.get(num).getString("event_starts_datetime");
                            String dateStringEnd = objectList.get(num).getString("event_ends_datetime");
                            Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                            Calendar calEnd = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStringEnd));
                            starthour = cal.get(Calendar.HOUR_OF_DAY);
                            startmin = cal.get(Calendar.MINUTE);
                            endhour = calEnd.get(Calendar.HOUR_OF_DAY);
                            endmin = calEnd.get(Calendar.MINUTE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        CustomizedTextView eventView = new CustomizedTextView(getActivity());
                        eventView.setPadding(DensityUtil.dip2px(getActivity(), 4), 0, 0, 0);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            eventView.setZ(DensityUtil.dip2px(getActivity(), 5));
                        }
                        try {
                            eventView.setText(objectList.get(num).getString("event_name"));
                            final String meeting_id = objectList.get(num).getString("meeting_id");
                            final Boolean isHost = objectList.get(num).getBoolean("is_host");
                            final String user_id = objectList.get(num).getString("user_id");
                            final String hostID = objectList.get(num).getString("host_id");
                            final String eventID = objectList.get(num).getString("event_id");
                            Log.i("evnetID", eventID);
                            final int finalNum = num;

                            eventView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    View pressedView = mScrollView.getPressedSubView();
                                    if (pressedView != null) {
                                        pressedView.performLongClick();
                                    }
                                    return true;
                                }
                            });

                            eventView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!meeting_id.equals("")) {


                                        Bundle bundle = new Bundle();
                                        bundle.putString("meeting_id", meeting_id);
                                        bundle.putString("user_id", user_id);
                                        if (hostID.equals(User.ID)) {
                                            Intent intent = new Intent(getActivity(), MeetingDetaiHostlActivity.class);
                                            try {
                                                String calendarId = objectList.get(finalNum).getString("calendar_id");
                                                String alert = objectList.get(finalNum).getString("event_alert");
                                                intent.putExtra("event_alert",alert);
                                                intent.putExtra("calendar_id",calendarId);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            intent.putExtra("arg_meeting_id", meeting_id);
                                            intent.putExtra("event_id", eventID);
                                            startActivityForResult(intent, CHANGE_MEETING);
                                        } else {
                                            Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                                            try {
                                                String calendarId = objectList.get(finalNum).getString("calendar_id");
                                                String alert = objectList.get(finalNum).getString("event_alert");
                                                intent.putExtra("event_alert",alert);
                                                intent.putExtra("calendar_id",calendarId);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            intent.putExtra("arg_meeting_id", meeting_id);
                                            intent.putExtra("event_id", eventID);
                                            startActivityForResult(intent, CHANGE_MEETING);
                                        }
                                             /*
                                            * add intent to start activity here
                                            * */
                                    } else {
                                        try {
                                            String event_name = objectList.get(finalNum).getString("event_name");
                                            String venue = objectList.get(finalNum).getString("event_venue_location");
                                            String dep_time = objectList.get(finalNum).getString("event_last_sug_dep_time");
                                            String start_time = objectList.get(finalNum).getString("event_starts_datetime");
                                            String end_time = objectList.get(finalNum).getString("event_ends_datetime");
                                            boolean punctual = objectList.get(finalNum).getBoolean("event_is_punctual");
                                            String repeat_type = objectList.get(finalNum).getString("event_repeats_type");
                                            String calendarType = "";
                                            String calendarId = objectList.get(finalNum).getString("calendar_id");
                                            ParcelableCalendarType parcelableCalendarType = new ParcelableCalendarType();
                                            String jsonString = objectList.get(finalNum).toString();
                                            String alert = objectList.get(finalNum).getString("event_alert");
                                            for (ParcelableCalendarType calType : Events.calendarTypeList) {
                                                if (calType.calendarId.equals(calendarId)) {
                                                    calendarType = calType.calendarName;
                                                    parcelableCalendarType = calType;
                                                }
                                            }
                                            Intent detailIntent = new Intent(getActivity(), EventsDetailActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("event_name", event_name);
                                            bundle.putString("venue", venue);
                                            bundle.putString("dep_time", dep_time);
                                            bundle.putString("start_time", start_time);
                                            bundle.putString("end_time", end_time);
                                            bundle.putBoolean("punctual", punctual);
                                            bundle.putString("repeat_type", repeat_type);
                                            bundle.putString("calendar_type", calendarType);
                                            bundle.putParcelable("calendar_type_pacelable", parcelableCalendarType);
                                            bundle.putString("event_id", eventID);
                                            bundle.putString("alert", alert);
                                            bundle.putString("json", jsonString);
                                            Toast.makeText(getActivity(), alert, Toast.LENGTH_SHORT).show();
                                            detailIntent.putExtras(bundle);
                                            startActivityForResult(detailIntent, EDIT_EVENT_REQUEST);
//                                                        startActivity(detailIntent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int durationMin = (60 * endhour + endmin) - (60 * starthour + startmin);
                        if (durationMin < 35) {
                            durationMin = 35;
                        }
                        RelativeLayout.LayoutParams eventParamOverlap = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(getActivity(), (float) (4.0 / 6.0 * durationMin) - 4));

                        try {
                            if (!objectList.get(num).getString("meeting_id").equals("")) {
                                eventView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.event_color_02));
                            } else {
                                eventView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.event_color_01));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                                    TextView view = (TextView) relativeLayout.findViewById(1);
//                                    View lview = relativeLayout.findViewById(100);
                        float length = screenWidth - (DensityUtil.dip2px(getActivity(), 50));
//                                    float length = lview.getWidth();
//                                    eventParamOverlap.height = DensityUtil.dip2px(getActivity(), 30);
                        eventParamOverlap.width = (int) (length / overlapNumber);
                        eventParamOverlap.addRule(RelativeLayout.ALIGN_TOP, 100 + starthour);
//                                    eventParamOverlap.addRule(RelativeLayout.ALIGN_BOTTOM, 100 + endhour);
                        eventParamOverlap.addRule(RelativeLayout.ALIGN_START, 100 + starthour);
                        eventParamOverlap.addRule(RelativeLayout.ALIGN_LEFT, 100 + starthour);

//                                    eventParam.addRule(RelativeLayout.ALIGN_RIGHT, 100 + starthour);
//                                    eventParam.addRule(RelativeLayout.ALIGN_END, 100 + starthour);
                        int leftMargin = (int) (flag * (length / overlapNumber));
                        Log.d("leftMargin", leftMargin + "");
                        eventParamOverlap.setMargins(leftMargin + flag * DensityUtil.dip2px(getActivity(), 2), DensityUtil.dip2px(getActivity(), (float) (startmin * 4.0 / 6.0) + 2), DensityUtil.dip2px(getActivity(), 0), DensityUtil.dip2px(getActivity(), 1));
                        relativeLayout.addView(eventView, eventParamOverlap);
                        flag++;
                    }
                }
            }


        } else {
            firstEventHour = 0;
            relativeLayout.removeAllViews();
            addLowerViews(relativeLayout);
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
                title_string = year + "-" + month;
                title.setText(title_string);
            }
        }
        if (requestCode == NEW_EVENT_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                Log.d("finishNewEvent", "ok");
                refresh();
            }
        }

        if (requestCode == EDIT_EVENT_REQUEST) {
            if (resultCode == getActivity().RESULT_OK) {
                refresh();
            }
        }

        if (requestCode == CHANGE_MEETING) {
            if (resultCode == getActivity().RESULT_OK) {
                refresh();
            }
            if (requestCode == JUMP_TO_EVENT_LIST) {
                Log.d("return", "return");
//            if (resultCode == getActivity().RESULT_OK) {
                refresh();
//            }
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

    public boolean ifFinishAsyncTask(int month, int year) {

        if (month - 1 == 0) {
            if (Events.eventsMonthMap.containsKey(12 + "-" + (year - 1)) && Events.eventsMonthMap.containsKey(month + "-" + year) && Events.eventsMonthMap.containsKey((month + 1) + "-" + year)) {
                return true;
            }

        } else if (month + 1 > 12) {
            if (Events.eventsMonthMap.containsKey((month - 1) + "-" + year) && Events.eventsMonthMap.containsKey(month + "-" + year) && Events.eventsMonthMap.containsKey(1 + "-" + (year + 1))) {
                return true;
            }
        } else {
            if (Events.eventsMonthMap.containsKey((month - 1) + "-" + year) && Events.eventsMonthMap.containsKey(month + "-" + year) && Events.eventsMonthMap.containsKey((month + 1) + "-" + year)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(User.hasNewMeeting || isCalendarChanged()){
            User.hasNewMeeting = false;
            refresh();
        }
    }


    public void refresh() {
        loadNum = 0;
        Events.loadingMonth.clear();
        Events.eventsMonthMap.clear();
        Events.daysHaveEvents.clear();
        fetchEvents(User.ID, true);

    }

    /**
     * you should use this method to before you load and draw your data on the view.
     * @return
     */
    public boolean isCalendarChanged() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean isChanged = sharedPreferences.getBoolean(ITimePreferences.CALENDAR_TYPE_CHANGED, false);
        Toast.makeText(getContext(), "isCalendarChanged: " + isChanged, Toast.LENGTH_SHORT).show();
        if (isChanged) {
            sharedPreferences.edit().putBoolean(ITimePreferences.CALENDAR_TYPE_CHANGED, false).apply();
        }

        return isChanged;
    }

}

