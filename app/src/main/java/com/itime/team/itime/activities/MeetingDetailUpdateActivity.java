package com.itime.team.itime.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.fragments.NewEventCalendarTypeDialogFragment;
import com.itime.team.itime.fragments.NewMeetingAlertDialogFragment;
import com.itime.team.itime.fragments.NewMeetingRepeatDialogFragment;
import com.itime.team.itime.listener.RepeatSelectionListener;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 16/2/17.
 *
 */
public class MeetingDetailUpdateActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static String API_KEY = "AIzaSyBC4zDmkarugKY0Njs_n2TtEUVEyeESn0c";

    private EditText mMessage;
    private Button mStartDate, mStartTime, mEndDate, mEndTime;
    private Button mRepeat;
    private CheckBox mPunctual;
    private Button mAlert;
    private EditText mName, mVeune;
    private Button mCalendar;

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

    private int mOldStartYear;
    private int mOldStartMonth;
    private int mOldStartDay;
    private int mOldStartHour;
    private int mOldStartMin;
    private int mOldEndYear;
    private int mOldEndMonth;
    private int mOldEndDay;
    private int mOldEndHour;
    private int mOldEndMin;


    private TimePickerDialog mTimePicker1;
    private TimePickerDialog mTimePicker2;
    private DatePickerDialog mDatePicker1;
    private DatePickerDialog mDatePicker2;
    private String mLat = "";
    private String mLng = "";

    private ScrollView mMain;
    private boolean mIsFeasible;

    private ArrayList<String> mRpeatValue;
    private ArrayList<Integer> mAlertValue;

    private String mAddress;
    private String mMeetingID;
    private String mEventID;
    private String mToken;
    private Boolean mMPunctual;
    private String mMRepeat;
    private String mNote;


    private String mMeetingName;
    private String mLocation;
    private String mShow;
    private String mOldMeetingName;
    private String mOldLocation;
    private String mOldShow;
    private Boolean mOldMPunctual;
    private String mOldMRepeat;
    private String mOldNote;

    private String mHostID;
    private ParcelableCalendarType calendarTypeString = Events.calendarTypeList.get(0);

//    private JsonManager mJsonManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_meeting_activity);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meeting_detail_update, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.update_meeting){
            postInformation();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_meeting_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.edit);
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
        mVeune = (EditText) findViewById(R.id.new_meeting_venue);
        mCalendar = (Button) findViewById(R.id.new_meeting_calendar);
        mStartDate.setOnClickListener(this);
        mEndDate.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);
        mRepeat.setOnClickListener(this);
        mPunctual.setOnCheckedChangeListener(this);
        mMain.setOnTouchListener(this);
        mAlert.setOnClickListener(this);
        mVeune.setOnClickListener(this);
        mCalendar.setOnClickListener(this);

        setEndTime();

        mStartTime.setText(timeFormat(mStartHour, mStartMin));
        mEndTime.setText(timeFormat(mEndHour, mEndMin));
        mStartDate.setText(dateFormat(mStartDay, mStartMonth, mStartYear));
        mEndDate.setText(dateFormat(mEndDay, mEndMonth, mEndYear));

        mRpeatValue = new ArrayList();
        mAlertValue = new ArrayList();
        mRpeatValue.add("One-time event");
        mAlertValue.add(1);

        mAddress = mVeune.getText().toString();
        //simpleRequest();
    }

    private Date getCurrentDate(){
        Date date = null;
        Intent receiver = getIntent();
        Calendar calendar = Calendar.getInstance();
        mStartYear = receiver.getIntExtra("year", calendar.get(Calendar.YEAR));
        mStartMonth = receiver.getIntExtra("month",calendar.get(Calendar.MONTH));
        mStartDay = receiver.getIntExtra("day",calendar.get(Calendar.DATE));
        mStartHour = receiver.getIntExtra("hour",0);
        mStartMin = receiver.getIntExtra("min",0);
        return date;
    }

    private void setEndTime(){
        getCurrentDate();
        Intent receiver = getIntent();
        Calendar calendar = Calendar.getInstance();
        mEndYear = receiver.getIntExtra("e_year",calendar.get(Calendar.YEAR));
        mEndMonth = receiver.getIntExtra("e_month",calendar.get(Calendar.MONTH));
        mEndDay = receiver.getIntExtra("e_day",calendar.get(Calendar.DATE));
        mEndHour = receiver.getIntExtra("e_hour",calendar.get(Calendar.HOUR));
        mEndMin = receiver.getIntExtra("e_min", calendar.get(Calendar.MINUTE));

        mMRepeat = receiver.getStringExtra("repeat");
        mMPunctual = receiver.getBooleanExtra("punctual", false);
        mMeetingName = receiver.getStringExtra("name");
        mLocation = receiver.getStringExtra("location");
        mShow = receiver.getStringExtra("show");
        mNote = receiver.getStringExtra("note");
        mHostID = receiver.getStringExtra("host_id");

        mRepeat.setText(mMRepeat);
        mPunctual.setChecked(mMPunctual);
        mName.setText(mMeetingName);
        mVeune.setText(mShow);
        mMessage.setText(mNote);

        mMeetingID = receiver.getStringExtra("meeting_id");
        mEventID = receiver.getStringExtra("event_id");
        mToken = receiver.getStringExtra("token");


        mOldStartDay = mStartDay;
        mOldEndDay = mEndDay;
        mOldStartHour = mStartHour;
        mOldStartMin = mStartMin;
        mOldStartYear = mStartYear;
        mOldStartMonth = mStartMonth;
        mOldEndYear = mEndYear;
        mOldEndMonth = mEndMonth;
        mOldEndHour = mEndHour;
        mOldEndMin = mEndMin;
        mOldMRepeat = mMRepeat;
        mOldMPunctual = mMPunctual;
        mOldMeetingName = mMeetingName;
        mOldLocation = mLocation;
        mOldShow = mShow;
        mOldNote = mNote;
    }

    private String timeFormat(int hour, int min){
        String hourReturn = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minReturn = min < 10 ? "0" + min : String.valueOf(min);

        return hourReturn + " : " + minReturn;
    }
    private String dateFormat(int day, int month, int year){
        String dayReturn = day < 10 ? "0" + day : String.valueOf(day);
        return  DateUtil.weekNameStandardTwo[DateUtil.getDateOfWeek(year,month,day) - 1] +
                ", " + dayReturn + " " + DateUtil.month[month] + " " + year;
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
                    mStartTime.setText(timeFormat(hourOfDay, minute));
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
        }else if(v.getId() == R.id.new_meeting_venue){
            Intent intent = new Intent(this,GooglePlacesAutocompleteActivity.class);
            startActivityForResult(intent, 1);
        }else if (v.getId() == mCalendar.getId()) {
            mCalendar.setText(Events.calendarTypeList.get(0).calendarName);
            mCalendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    NewEventCalendarTypeDialogFragment dialog = new NewEventCalendarTypeDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(NewEventCalendarTypeDialogFragment.SELECTED, Events.calendarTypeList.indexOf(calendarTypeString));
                    dialog.setArguments(bundle);
                    dialog.setListener(new RepeatSelectionListener() {
                        @Override
                        public void selectItem(int positon) {
                            calendarTypeString = Events.calendarTypeList.get(positon);
                            mCalendar.setText(Events.calendarTypeList.get(positon).calendarName);
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "calendar_dialog");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                mAddress = data.getStringExtra("address");
                //mVeune.setText(mAddress);
                getLoaction(mAddress);
                //mVeune.setTextSize(12);
                //getCoordinate(mAddress);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    private void postInformation(){
        String startDateForPost = DateUtil.getDateWithTimeZone(mOldStartYear, mOldStartMonth + 1, mOldStartDay, mOldStartHour, mOldStartMin);
        String endDateForPost = DateUtil.getDateWithTimeZone(mOldEndYear, mOldEndMonth + 1, mOldEndDay, mOldEndHour, mOldEndMin);
        String comment = mMessage.getText().toString();
        String name = mName.getText().toString();
        String punctual = mPunctual.isChecked() ? "true" : "false";
        String repeative = mRpeatValue.get(0);

        String status = "NO CONFIRM NEW MEETING";
        String[] address = mAddress.split(",");
        mLocation = mAddress;
        mShow = address[0];

        JSONObject json = new JSONObject();
        try {
            String currentTime = DateUtil.formatLocalDateObject(new Date());
            Log.i("currentTime",currentTime);
            json.put("event_id", mEventID);
            json.put("event_venue_location_new", mLocation);
            json.put("event_repeats_type_new",mMRepeat);
            json.put("event_last_sug_dep_time",currentTime);
            json.put("is_long_repeat",1);
            json.put("event_starts_datetime_new",DateUtil.getDateWithTimeZone(mStartYear,mStartMonth + 1,mStartDay,mStartHour,mStartMin));
            json.put("is_host","");
            json.put("event_longitude_new",mLng);
            json.put("event_latitude_new",mLat);
            json.put("event_last_sug_dep_time_new",currentTime);
            json.put("event_ends_datetime_new",DateUtil.getDateWithTimeZone(mEndYear,mEndMonth + 1,mEndDay,mEndHour,mEndMin));
            json.put("event_name_new", mName.getText());
            json.put("event_last_distance_in_meter_new",currentTime);
            json.put("event_is_punctual_new", mPunctual.isChecked() ? 1 : 0);
            json.put("host_id",mHostID);
            json.put("event_last_time_on_way_in_second_new",currentTime);
            json.put("is_meeting",1);
            json.put("event_venue_show_new", mShow);
            json.put("event_last_time_on_way_in_second",currentTime);
            json.put("event_alert", mAlert.getText().toString().split(" -- ")[1]); //Need to be changed
            json.put("if_deleted",0);
            json.put("event_last_distance_in_meter",0);
            json.put("event_comment_new",mMessage.getText());
            json.put("event_repeat_to_date",DateUtil.getDateWithTimeZone(mEndYear, mEndMonth, mEndDay, mEndHour, mEndMin));
            json.put("calendar_id",calendarTypeString.calendarId);
            json.put("event_last_update_datetime",currentTime);
            json.put("event_is_punctual",mOldMPunctual ? 1 : 0);
            json.put("event_starts_datetime", startDateForPost);
            json.put("event_ends_datetime", endDateForPost);
            json.put("event_comment",mOldNote);
            json.put("event_name",mOldMeetingName);
            json.put("event_repeats_type",mOldMRepeat);
            json.put("event_latitude",mLat);
            json.put("event_longitude", mLng);
            json.put("event_venue_location", mOldLocation);
            json.put("meeting_id",mMeetingID);
            json.put("meeting_valid_token",mToken);
            json.put("user_id", User.ID);
            json.put("meeting_status", "NO CONFIRM UPDATE MEETING"); //Neet to add
            json.put("event_venue_show", mOldShow);

            Log.i("resu",json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.MEETING_INFO_UPDATE_REQUEST;
        Map<String, String> params = new HashMap();
        params.put("json", json.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("result").equals("success")){
                        setResult(RESULT_OK);
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.meeting_update), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void getLoaction(String address) {
        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
        sb.append("?key=" + API_KEY);
        sb.append("&&placeid=" + address);
        String url = sb.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject result = response.getJSONObject("result");
                    String locations = result.getString("formatted_address");
                    JSONObject geo = result.getJSONObject("geometry");
                    mAddress = locations;
//                    JSONObject geoLocation = geo.getJSONObject("location");
//                    event_latitude = Double.toString(geoLocation.getDouble("lat"));
//                    event_longitude = Double.toString(geoLocation.getDouble("lng"));
                    mVeune.setText(mAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void getCoordinate(String address){
        StringBuffer buffer = new StringBuffer();
        buffer.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
        String words = address.replaceAll(",","");
        words = words.replaceAll(" ","+");
        buffer.append(words).append(getResources().getString(R.string.google_web_id));
        String url = buffer.toString();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("results");
                            JSONObject object = (JSONObject) result.get(0);
                            JSONObject geometry = (JSONObject) object.get("geometry");
                            JSONObject localtion = (JSONObject) geometry.get("location");
                            mLat = localtion.getString("lat");
                            mLng = localtion.getString("lng");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("rerror",error.toString());

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
