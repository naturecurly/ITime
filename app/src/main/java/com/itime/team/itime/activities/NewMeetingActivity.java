package com.itime.team.itime.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TimePicker;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.fragments.NewMeetingAlertDialogFragment;
import com.itime.team.itime.fragments.NewMeetingRepeatDialogFragment;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by mac on 16/2/17.
 */
public class NewMeetingActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText mMessage;
    private Button mStartDate, mStartTime, mEndDate, mEndTime;
    private Button mRepeat;
    private CheckBox mPunctual;
    private Button mAlert;
    private EditText mName;

    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private int mStartHour;
    private int mStartMin;
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    private int mEndHour;
    private int mEndMin;
    private int mDuration;
    private String[] mFriendIDs;


    private TimePickerDialog mTimePicker1;
    private TimePickerDialog mTimePicker2;
    private DatePickerDialog mDatePicker1;
    private DatePickerDialog mDatePicker2;

    private ScrollView mMain;
    private boolean mIsFeasible;

    private ArrayList<String> mRpeatValue;
    private ArrayList<Integer> mAlertValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_meeting_activity);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_meeting, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.new_meeting_menu_send){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This part is being developed");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("Sorry");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    postInformation();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_meeting_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.new_meeting_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setEndTime();

        mMessage = (EditText) findViewById(R.id.new_meeting_message);
        mMessage.setOnTouchListener(this);
        mStartDate = (Button) findViewById(R.id.new_meeting_start_date);
        mEndDate = (Button) findViewById(R.id.new_meeting_end_date);
        mStartTime = (Button) findViewById(R.id.new_meeting_start_time);
        mEndTime = (Button) findViewById(R.id.new_meeting_end_time);
        mRepeat = (Button) findViewById(R.id.new_meeting_repeat);
        mAlert = (Button) findViewById(R.id.new_meeting_alert);
        mPunctual = (CheckBox) findViewById(R.id.new_meeting_punctual);
        mName = (EditText) findViewById(R.id.new_meeting_name);
        mMain = (ScrollView) findViewById(R.id.new_meeting_main_layout);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        mRepeat.setOnClickListener(this);
        mPunctual.setOnCheckedChangeListener(this);
        mMain.setOnTouchListener(this);
        mAlert.setOnClickListener(this);

        mStartTime.setText(timeFormat(mStartHour, mStartMin));
        mEndTime.setText(timeFormat(mEndHour, mEndMin));
        mStartDate.setText(dateFormat(mStartDay, mStartMonth, mStartYear));
        mEndDate.setText(dateFormat(mEndDay, mEndMonth, mEndYear));

        mRpeatValue = new ArrayList();
        mAlertValue = new ArrayList();
        mRpeatValue.add("One-time event");
        mAlertValue.add(1);
    }

    private Date getCurrentDate(){
        Date date = null;
        Intent receiver = getIntent();
        mStartYear = receiver.getIntExtra("year",0);
        mStartMonth = receiver.getIntExtra("month",0);
        mStartDay = receiver.getIntExtra("day",0);
        mDuration = receiver.getIntExtra("duration",0);
        mStartHour = receiver.getIntExtra("hour",0);
        mStartMin = receiver.getIntExtra("min",0);
        mFriendIDs = receiver.getStringArrayExtra("friendids");
        int currentDay = receiver.getIntExtra("currentDay",0);
        date = DateUtil.plusDay(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMin, currentDay);
        mStartYear = date.getYear() + 1900;
        mStartMonth = date.getMonth();
        mStartDay = date.getDate();
        mStartHour = date.getHours();
        mStartMin = date.getMinutes();
        return date;
    }

    private void setEndTime(){
        Date endTime = DateUtil.plusMinute(getCurrentDate(), mDuration);
        mEndYear = endTime.getYear() + 1900;
        mEndMonth = endTime.getMonth();
        mEndDay = endTime.getDate();
        mEndHour = endTime.getHours();
        mEndMin = endTime.getMinutes();
    }

    private String timeFormat(int hour, int min){
        return hour + " : " + min;
    }
    private String dateFormat(int day, int month, int year){
        return day + " " + DateUtil.month[month] + " " + year;
    }
    private void checkTime(){
        if(DateUtil.isFeasible(mStartYear,mStartMonth,mStartDay,mStartHour,mStartMin,mEndYear,mEndMonth,
                mEndDay,mEndHour,mEndMin)) {
            mEndDate.setTextColor(getResources().getColor(R.color.bottom_bar));
            mEndTime.setTextColor(getResources().getColor(R.color.bottom_bar));
            mIsFeasible = true;
        }else{
            mEndDate.setTextColor(getResources().getColor(R.color.colorAccent));
            mEndTime.setTextColor(getResources().getColor(R.color.colorAccent));
            mIsFeasible = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.new_meeting_message && mMessage.isFocused()){
            v.getParent().requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        checkTime();
        if(v.getId() == R.id.new_meeting_start_time){
            mTimePicker1 = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTime.setText(hourOfDay + " : " + minute);
                    mStartHour = hourOfDay;
                    mStartMin = minute;
                    checkTime();
                }
            },mStartHour,mStartMin,false);
            mTimePicker1.show();
        }else if (v.getId() == R.id.new_meeting_start_date){
            mDatePicker1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mStartDate.setText(dateFormat(dayOfMonth,monthOfYear,year));
                    mStartYear = year;
                    mStartMonth = monthOfYear;
                    mStartDay = dayOfMonth;
                    checkTime();
                }
            },mStartYear, mStartMonth, mStartDay);
            mDatePicker1.show();
        }else if (v.getId() == R.id.new_meeting_end_time){
            mTimePicker2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mEndTime.setText(timeFormat(hourOfDay, minute));
                    mEndHour = hourOfDay;
                    mEndMin = minute;
                    checkTime();
                }
            },mEndHour, mEndMin,false);
            mTimePicker2.show();
        }else if(v.getId() == R.id.new_meeting_end_date){
            mDatePicker2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mEndDate.setText(dateFormat(dayOfMonth, monthOfYear,year));
                    mEndYear = year;
                    mEndMonth = monthOfYear;
                    mEndDay = dayOfMonth;
                    checkTime();
                }
            },mEndYear, mEndMonth, mEndDay);
            mDatePicker2.show();
        }else if(v.getId() == R.id.new_meeting_repeat){
            NewMeetingRepeatDialogFragment dialogFragment = new NewMeetingRepeatDialogFragment(mRepeat, mRpeatValue);
            dialogFragment.show(getSupportFragmentManager(),"newMeetingRepeat");
        }else if(v.getId() == R.id.new_meeting_alert){
            NewMeetingAlertDialogFragment dialogFragment = new NewMeetingAlertDialogFragment(mAlert, mAlertValue);
            dialogFragment.show(getSupportFragmentManager(), "newMeetingAlert");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    private void postInformation(){
        String startDateForPost = DateUtil.getDateWithTimeZone(mStartYear, mStartMonth + 1, mStartDay, mStartHour, mStartMin);
        String endDateForPost = DateUtil.getDateWithTimeZone(mEndYear, mEndMonth + 1, mEndDay, mEndHour, mEndMin);
        String comment = mMessage.getText().toString();
        String name = mName.getText().toString();
        String punctual = mPunctual.isChecked() ? "true" : "false";
        String repeative = mRpeatValue.get(0);

        String latitude = "";
        String longitude = "";
        String status = "NO CONFIRM NEW MEETING";
        String location = "Melbourne";
        String showLocation = "Melbourne";
        String meetingID = UUID.randomUUID().toString();
        String meetingToken = UUID.randomUUID().toString();

        JSONArray friendID = new JSONArray();
        for(String ids : mFriendIDs){
            friendID.put(ids);
        }
        JSONObject json = new JSONObject();
        try {
            json.put("event_is_punctual",punctual);
            json.put("event_starts_datetime", URLConnectionUtil.encode(startDateForPost));
            json.put("event_ends_datetime", URLConnectionUtil.encode(endDateForPost));
            json.put("event_comment",comment);
            json.put("event_name",name);
            json.put("friends_id",friendID);
            json.put("event_repeats_type",repeative);
            json.put("event_latitude",latitude);
            json.put("event_longitude", longitude);
            json.put("event_venue_location", location);
            json.put("meeting_id",meetingID);
            json.put("meeting_valid_token",meetingToken);
            json.put("user_id", User.ID);
            json.put("meeting_status", status);
            json.put("event_venue_show",showLocation);
            Log.i("json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Log.i("Date",startDateForPost);
    }
}
