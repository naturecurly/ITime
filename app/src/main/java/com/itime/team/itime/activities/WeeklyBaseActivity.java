package com.itime.team.itime.activities;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.itime.team.itime.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by leveyleonhardt on 3/11/16.
 */
public abstract class WeeklyBaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_WEEK_VIEW;
    private WeekView mWeekView;
    private boolean isWeekView = true;
    private Menu mMenu;
    private boolean isLoaded = false;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Toast.makeText(this, "test" + bundle.getInt("day"), Toast.LENGTH_SHORT).show();
        int day = bundle.getInt("day");
        int month = bundle.getInt("month");
        int year = bundle.getInt("year");
        calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.week_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setNumberOfVisibleDays(5);

        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));


        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        mWeekView.goToDate(calendar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_weekly, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id) {
//            case R.id.action_today:
//                mWeekView.goToToday();
//                return true;
            case R.id.action_day_view:
                if (isWeekView == true) {
                    isLoaded = true;
                    invalidateOptionsMenu();
                    if (mWeekViewType != TYPE_DAY_VIEW) {
                        item.setChecked(!item.isChecked());
                        mWeekViewType = TYPE_DAY_VIEW;
                        mWeekView.setNumberOfVisibleDays(1);

                        // Lets change some dimensions to best fit the view.
                        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        mWeekView.goToDate(calendar);
                        Calendar now = Calendar.getInstance();
                        Calendar visibleDay = mWeekView.getFirstVisibleDay();
                        if (visibleDay.get(Calendar.YEAR)==now.get(Calendar.YEAR)&&visibleDay.get(Calendar.MONTH)==now.get(Calendar.MONTH)&&visibleDay.get(Calendar.DAY_OF_MONTH)==now.get(Calendar.DAY_OF_MONTH)){
                            mWeekView.goToHour(now.get(Calendar.HOUR_OF_DAY));
                        }

                        isWeekView = false;


                    }
                } else {

                    if (mWeekViewType != TYPE_WEEK_VIEW) {
                        isLoaded = true;
                        invalidateOptionsMenu();
                        item.setChecked(!item.isChecked());
                        mWeekViewType = TYPE_WEEK_VIEW;
                        mWeekView.setNumberOfVisibleDays(5);

                        // Lets change some dimensions to best fit the view.
                        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                        mWeekView.goToDate(calendar);
                        isWeekView = true;

                    }

                }
                return true;
//            case R.id.action_three_day_view:
//                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_THREE_DAY_VIEW;
//                    mWeekView.setNumberOfVisibleDays(3);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case R.id.action_week_view:
//                if (mWeekViewType != TYPE_WEEK_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_WEEK_VIEW;
//                    mWeekView.setNumberOfVisibleDays(7);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                }
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isLoaded) {
            if (!isWeekView) {
                MenuItem menuItem = mMenu.findItem(R.id.action_day_view);
                menuItem.setTitle("Weekly");
            } else {
                MenuItem menuItem = mMenu.findItem(R.id.action_day_view);
                menuItem.setTitle("Daily");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
//                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
                return (hour < 10 ? 0 + "" + hour : hour) + ":00";
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

}
