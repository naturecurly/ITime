package com.itime.team.itime.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.GooglePlacesAutocompleteActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mac on 16/3/15.
 */
public class NewEventFragment extends Fragment {
//    private EditText mMessage;
//    private Button mStartDate, mStartTime, mEndDate, mEndTime;
//    private Button mRepeat;
//    private CheckBox mPunctual;
//    private Button mAlert;
//    private EditText mName, mVeune;
//
//    private int mStartYear;
//    private int mStartMonth;
//    private int mStartDay;
//    private int mStartHour;
//    private int mStartMin;
//    private int mEndYear;
//    private int mEndMonth;
//    private int mEndDay;
//    private int mEndHour;
//    private int mEndMin;
//    private int mDuration;
//    private String[] mFriendIDs;
//
//
//    private TimePickerDialog mTimePicker1;
//    private TimePickerDialog mTimePicker2;
//    private DatePickerDialog mDatePicker1;
//    private DatePickerDialog mDatePicker2;
//    private String mLat = "";
//    private String mLng = "";
//
//    private ScrollView mMain;
//    private boolean mIsFeasible;
//    private ArrayList<String> mRpeatValue;
//    private ArrayList<Integer> mAlertValue;
//
//    private JsonManager mJsonManager;


    private EditText event_name;
    private TextView event_venue;
    private TextView start_date;
    private TextView start_time;
    private TextView end_date;
    private TextView end_time;
    private int mYear;
    private int mMonthOfYear;
    private int mDayOfMonth;
    final Calendar c = Calendar.getInstance();
    private int mHour;
    private int mMin;
    private int mEndYear;
    private int mEndMonthOfYear;
    private int mEndDayOfMonth;
    private int mEndHour;
    private int mEndMin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mYear = c.get(Calendar.YEAR);
        mMonthOfYear = c.get(Calendar.MONTH);
        mDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.new_event_fragment, container, false);
        event_name = (EditText) view.findViewById(R.id.new_event_name);
        event_venue = (TextView) view.findViewById(R.id.new_event_venue);
        start_date = (TextView) view.findViewById(R.id.start_date);
        start_time = (TextView) view.findViewById(R.id.start_time);
        end_date = (TextView) view.findViewById(R.id.end_date);
        end_time = (TextView) view.findViewById(R.id.end_time);

        start_date.setText(DateUtil.formatDate(mDayOfMonth, mMonthOfYear, mYear));
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        start_date.setText(DateUtil.formatDate(dayOfMonth, monthOfYear, year));

                        mYear = year;
                        mMonthOfYear = monthOfYear;
                        mDayOfMonth = dayOfMonth;
                    }
                }, mYear, mMonthOfYear, mDayOfMonth).show();
            }
        });
        start_time.setText(timeFormat(mHour, 0));
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        start_time.setText(timeFormat(hourOfDay, minute));
                        mHour = hourOfDay;
                        mMin = minute;
                    }
                }, mHour, mMin, true).show();
            }
        });

        end_date.setText(DateUtil.formatDate(mDayOfMonth, mMonthOfYear, mYear));
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        end_date.setText(DateUtil.formatDate(dayOfMonth, monthOfYear, year));

                        mEndYear = year;
                        mEndMonthOfYear = monthOfYear;
                        mEndDayOfMonth = dayOfMonth;
                    }
                }, mYear, mMonthOfYear, mDayOfMonth).show();
            }
        });

        end_time.setText(timeFormat(mHour, 0));
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        end_time.setText(timeFormat(hourOfDay, minute));
                        mEndHour = hourOfDay;
                        mEndMin = minute;
                    }
                }, mHour, mMin, true).show();
            }
        });
//        init(rootView);

        return view;
    }

    //    private void init(View rootView) {
//
//
//        setEndTime();
//        getCurrentDate();
//
//        mMessage = (EditText) rootView.findViewById(R.id.new_meeting_message);
//        mMessage.setOnTouchListener(this);
//        mStartDate = (Button) rootView.findViewById(R.id.new_meeting_start_date);
//        mEndDate = (Button) rootView.findViewById(R.id.new_meeting_end_date);
//        mStartTime = (Button) rootView.findViewById(R.id.new_meeting_start_time);
//        mEndTime = (Button) rootView.findViewById(R.id.new_meeting_end_time);
//        mRepeat = (Button) rootView.findViewById(R.id.new_meeting_repeat);
//        mAlert = (Button) rootView.findViewById(R.id.new_meeting_alert);
//        mPunctual = (CheckBox) rootView.findViewById(R.id.new_meeting_punctual);
//        mName = (EditText) rootView.findViewById(R.id.new_meeting_name);
//        mMain = (ScrollView) rootView.findViewById(R.id.new_meeting_main_layout);
//        mVeune = (EditText) rootView.findViewById(R.id.new_meeting_venue);
//        mStartDate.setOnClickListener(this);
//        mEndDate.setOnClickListener(this);
//        mStartTime.setOnClickListener(this);
//        mEndTime.setOnClickListener(this);
//        mRepeat.setOnClickListener(this);
//        mPunctual.setOnCheckedChangeListener(this);
//        mMain.setOnTouchListener(this);
//        mAlert.setOnClickListener(this);
//        mVeune.setOnClickListener(this);
//
//        mStartTime.setText(timeFormat(mStartHour, mStartMin));
//        mEndTime.setText(timeFormat(mEndHour, mEndMin));
//        mStartDate.setText(dateFormat(mStartDay, mStartMonth, mStartYear));
//        mEndDate.setText(dateFormat(mEndDay, mEndMonth, mEndYear));
//
//        mRpeatValue = new ArrayList();
//        mAlertValue = new ArrayList();
//        mRpeatValue.add("One-time event");
//        mAlertValue.add(1);
//
//        mJsonManager = new JsonManager();
//
//        //simpleRequest();
//    }
//
    private String timeFormat(int hour, int min) {
        String hourReturn = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minReturn = min < 10 ? "0" + min : String.valueOf(min);

        return hourReturn + ":" + minReturn;
    }

    //
//
//    private void checkTime() {
//        if (DateUtil.isFeasible(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMin, mEndYear, mEndMonth,
//                mEndDay, mEndHour, mEndMin)) {
//            mEndDate.setTextColor(getResources().getColor(R.color.bottom_bar));
//            mEndTime.setTextColor(getResources().getColor(R.color.bottom_bar));
//            mIsFeasible = true;
//        } else {
//            mEndDate.setTextColor(getResources().getColor(R.color.colorAccent));
//            mEndTime.setTextColor(getResources().getColor(R.color.colorAccent));
//            mIsFeasible = false;
//        }
//    }
//
//    private void setEndTime() {
//        mEndYear = 2016;
//        mEndMonth = 10;
//        mEndDay = 1;
//        mEndHour = 10;
//        mEndMin = 0;
//    }
//
//    private Date getCurrentDate() {
//        Date date = null;
//        Intent receiver = getActivity().getIntent();
//        mStartYear = receiver.getIntExtra("year", 2016);
//        mStartMonth = receiver.getIntExtra("month", 1);
//        mStartDay = receiver.getIntExtra("day", 1);
//        mStartHour = receiver.getIntExtra("hour", 10);
//        mStartMin = receiver.getIntExtra("min", 0);
////        mFriendIDs = receiver.getStringArrayExtra("friendids");
////        int currentDay = receiver.getIntExtra("currentDay",0);
////        date = DateUtil.plusDay(mStartYear, mStartMonth, mStartDay, mStartHour, mStartMin, currentDay);
////        mStartYear = date.getYear() + 1900;
////        mStartMonth = date.getMonth();
////        mStartDay = date.getDate();
////        mStartHour = date.getHours();
////        mStartMin = date.getMinutes();
//        return null;
//    }
//
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.new_event, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.new_event_menu_send:
//                Toast.makeText(getContext(), "hello, it's me", Toast.LENGTH_LONG).show();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void getCoordinate(String address) {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("https://maps.googleapis.com/maps/api/geocode/json?address=");
//        String words = address.replaceAll(",", "");
//        words = words.replaceAll(" ", "+");
//        buffer.append(words).append(getResources().getString(R.string.google_web_id));
//        String url = buffer.toString();
//
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray result = response.getJSONArray("results");
//                            JSONObject object = (JSONObject) result.get(0);
//                            JSONObject geometry = (JSONObject) object.get("geometry");
//                            JSONObject localtion = (JSONObject) geometry.get("location");
//                            mLat = localtion.getString("lat");
//                            mLng = localtion.getString("lng");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO Auto-generated method stub
//                        Log.i("rerror", error.toString());
//
//                    }
//                });
//
//        MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
//    }
//
//    @Override
//    public void onClick(View v) {
//        checkTime();
//        if (v.getId() == R.id.new_meeting_start_time) {
//            mTimePicker1 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    mStartTime.setText(timeFormat(hourOfDay, minute));
//                    mStartHour = hourOfDay;
//                    mStartMin = minute;
//                    checkTime();
//                }
//            }, mStartHour, mStartMin, false);
//            mTimePicker1.show();
//        } else if (v.getId() == R.id.new_meeting_start_date) {
//            mDatePicker1 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                    mStartDate.setText(dateFormat(dayOfMonth, monthOfYear, year));
//                    mStartYear = year;
//                    mStartMonth = monthOfYear;
//                    mStartDay = dayOfMonth;
//                    checkTime();
//                }
//            }, mStartYear, mStartMonth, mStartDay);
//            mDatePicker1.show();
//        } else if (v.getId() == R.id.new_meeting_end_time) {
//            mTimePicker2 = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
//                @Override
//                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                    mEndTime.setText(timeFormat(hourOfDay, minute));
//                    mEndHour = hourOfDay;
//                    mEndMin = minute;
//                    checkTime();
//                }
//            }, mEndHour, mEndMin, false);
//            mTimePicker2.show();
//        } else if (v.getId() == R.id.new_meeting_end_date) {
//            mDatePicker2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                    mEndDate.setText(dateFormat(dayOfMonth, monthOfYear, year));
//                    mEndYear = year;
//                    mEndMonth = monthOfYear;
//                    mEndDay = dayOfMonth;
//                    checkTime();
//                }
//            }, mEndYear, mEndMonth, mEndDay);
//            mDatePicker2.show();
//        } else if (v.getId() == R.id.new_meeting_repeat) {
//            NewMeetingRepeatDialogFragment dialogFragment = new NewMeetingRepeatDialogFragment(mRepeat, mRpeatValue);
//            dialogFragment.show(getFragmentManager(), "newMeetingRepeat");
//        } else if (v.getId() == R.id.new_meeting_alert) {
//            NewMeetingAlertDialogFragment dialogFragment = new NewMeetingAlertDialogFragment(mAlert, mAlertValue);
//            dialogFragment.show(getFragmentManager(), "newMeetingAlert");
//        } else if (v.getId() == R.id.new_meeting_venue) {
//            Intent intent = new Intent(getActivity(), GooglePlacesAutocompleteActivity.class);
//            startActivityForResult(intent, 1);
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1) {
//            if (resultCode == getActivity().RESULT_OK) {
//                String address = "";
//                address = data.getStringExtra("address");
//                mVeune.setText(address);
//                mVeune.setTextSize(12);
//                getCoordinate(address);
//            }
//        }
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if (v.getId() == R.id.new_meeting_message && mMessage.isFocused()) {
//            v.getParent().requestDisallowInterceptTouchEvent(true);
//        }
//
//        return false;
//    }
//
//    public void postEvents() {
//        String startDateForPost = DateUtil.getDateWithTimeZone(mStartYear, mStartMonth + 1, mStartDay, mStartHour, mStartMin);
//        String endDateForPost = DateUtil.getDateWithTimeZone(mEndYear, mEndMonth + 1, mEndDay, mEndHour, mEndMin);
//        String comment = mMessage.getText().toString();
//        String name = mName.getText().toString();
//        String punctual = mPunctual.isChecked() ? "true" : "false";
//        String repeative = mRpeatValue.get(0);
//
//        String status = "NO CONFIRM NEW MEETING";
//        String location = "Melbourne";
//        String showLocation = "Melbourne";
//        String meetingID = UUID.randomUUID().toString();
//        String meetingToken = UUID.randomUUID().toString();
//
//        JSONArray friendID = new JSONArray();
//        friendID.put("");
//        JSONObject json = new JSONObject();
//        try {
//            json.put("event_is_punctual", punctual);
//            json.put("event_starts_datetime", URLConnectionUtil.encode(startDateForPost));
//            json.put("event_ends_datetime", URLConnectionUtil.encode(endDateForPost));
//            json.put("event_comment", comment);
//            json.put("event_name", name);
//            json.put("friends_id", friendID);
//            json.put("event_repeats_type", repeative);
//            json.put("event_latitude", mLat);
//            json.put("event_longitude_new", mLng);
//            json.put("event_venue_location", location);
//            json.put("meeting_id", meetingID);
//            json.put("meeting_valid_token", meetingToken);
//            json.put("user_id", User.ID);
//            json.put("meeting_status", status);
//            json.put("event_venue_show", showLocation);
//            json.put("is_meeting", false);
//            json.put("is_long_repeat", true);
//            Log.i("resu", json.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        final String url = URLs.SYNC;
//        Map<String, String> params = new HashMap();
//        params.put("json", json.toString());
//
//
//    }
}
