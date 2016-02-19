package com.itime.team.itime.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TimePicker;

import com.itime.team.itime.fragments.NewMeetingRepeatDialogFragment;
import com.itime.team.itime.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;

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


    private TimePickerDialog mTimePicker1;
    private TimePickerDialog mTimePicker2;
    private DatePickerDialog mDatePicker1;
    private DatePickerDialog mDatePicker2;

    private ScrollView mMain;
    private boolean mIsFeasible;

    private ArrayList<Integer> mRpeatValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_meeting_activity);
        init();
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
        mEndDate.setText(dateFormat(mStartDay, mStartMonth, mStartYear));

        mRpeatValue = new ArrayList();
        mRpeatValue.add(0);
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
        int currentDay = receiver.getIntExtra("currentDay",0);
        Log.i("Curent", String.valueOf(currentDay));
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
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}
