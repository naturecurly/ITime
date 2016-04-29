package com.itime.team.itime.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.GooglePlacesAutocompleteActivity;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.listener.RepeatSelectionListener;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mac on 16/3/15.
 */
public class NewEventFragment extends Fragment {
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_DETAILS = "/details";
    private static final String OUT_JSON = "/json";
    private static String API_KEY = "AIzaSyBC4zDmkarugKY0Njs_n2TtEUVEyeESn0c";

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

    final private String[] repeatArray = {"One-time event", "Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
    final private String[] alertArray = {"None", "At time of Departure", "5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before"};

    private EditText event_name;
    private EditText event_comment;
    private TextView event_venue;
    private TextView start_date;
    private TextView start_time;
    private TextView end_date;
    private TextView end_time;
    private TextView repeat_type;
    private TextView alert;
    private TextView calendar_type;
    private CheckBox punctual;

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
    private String repeatString = "One-time event";
    private String alertString = "At time of Departure";
    private String event_longitude = "";
    private String event_latitude = "";

    private ParcelableCalendarType calendarTypeString = Events.calendarTypeList.get(0);
    private String event_venue_location = "";
    private int is_punctual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        mYear = c.get(Calendar.YEAR);
        mMonthOfYear = c.get(Calendar.MONTH);
        mDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mEndYear = mYear;
        mEndMonthOfYear = mMonthOfYear;
        mEndDayOfMonth = mDayOfMonth;
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.new_event_fragment, container, false);
        repeat_type = (TextView) view.findViewById(R.id.rep_new_event);
        event_name = (EditText) view.findViewById(R.id.new_event_name);
        event_venue = (TextView) view.findViewById(R.id.new_event_venue);
        event_comment = (EditText) view.findViewById(R.id.new_event_comment);
        start_date = (TextView) view.findViewById(R.id.start_date);
        start_time = (TextView) view.findViewById(R.id.start_time);
        end_date = (TextView) view.findViewById(R.id.end_date);
        end_time = (TextView) view.findViewById(R.id.end_time);
        alert = (TextView) view.findViewById(R.id.new_event_alert);
        calendar_type = (TextView) view.findViewById(R.id.new_event_calendar_type);
        punctual = (CheckBox) view.findViewById(R.id.new_event_punctual);

        punctual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_punctual = 1;
                } else {
                    is_punctual = 0;
                }
            }
        });

        event_venue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GooglePlacesAutocompleteActivity.class);
                startActivityForResult(intent, 1);
            }
        });
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

        repeat_type.setText("One-time event");
        repeat_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                NewEventRepeatDialogFragment dialog = new NewEventRepeatDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(NewEventRepeatDialogFragment.SELECTED, Arrays.asList(repeatArray).indexOf(repeatString));
                dialog.setArguments(bundle);
                dialog.setListener(new RepeatSelectionListener() {
                    @Override
                    public void selectItem(int positon) {
                        repeatString = repeatArray[positon];
                        repeat_type.setText(repeatArray[positon]);
                    }
                });
                dialog.show(fm, "repeat_dialog");
            }
        });

        alert.setText("At time of Departure");
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NewEventRepeatDialogFragment dialogFragment = new NewEventRepeatDialogFragment();
                NewEventAlertDialogFragment dialog = new NewEventAlertDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(NewEventAlertDialogFragment.SELECTED, Arrays.asList(alertArray).indexOf(alertString));
                dialog.setArguments(bundle);
                dialog.setListener(new RepeatSelectionListener() {
                    @Override
                    public void selectItem(int positon) {
                        alertString = alertArray[positon];
                        alert.setText(alertArray[positon]);
                    }
                });
                dialog.show(getFragmentManager(), "alert_dialog");

            }
        });
        calendar_type.setText(Events.calendarTypeList.get(0).calendarName);
        calendar_type.setOnClickListener(new View.OnClickListener() {
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
                        calendar_type.setText(Events.calendarTypeList.get(positon).calendarName);
                    }
                });
                dialog.show(getFragmentManager(), "calendar_dialog");
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
//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_event_menu_send:
                Toast.makeText(getContext(), "hello, it's me", Toast.LENGTH_LONG).show();
                postEvent();
                getActivity().setResult(getActivity().RESULT_OK, new Intent());
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postEvent() {
        JSONObject object = new JSONObject();
        try {
            object.put("event_id", UUID.randomUUID().toString());
            object.put("user_id", User.ID);
            object.put("host_id", "");
            object.put("meeting_id", "");

            object.put("event_name", event_name.getText());
            object.put("event_comment", event_comment.getText());
            Calendar start_calendar = Calendar.getInstance();
            start_calendar.set(mYear, mMonthOfYear, mDayOfMonth, mHour, mMin);
            String start_datetime = DateUtil.getDateStringFromCalendarGMT(start_calendar);
            object.put("event_starts_datetime", start_datetime);

            Calendar end_calendar = Calendar.getInstance();
            end_calendar.set(mEndYear, mEndMonthOfYear, mEndDayOfMonth, mEndHour, mEndMin);
            String end_datetime = DateUtil.getDateStringFromCalendarGMT(end_calendar);
            object.put("event_ends_datetime", end_datetime);

            object.put("event_venue_show", event_venue_location);
            object.put("event_venue_location", event_venue_location);

            object.put("event_repeats_type", repeatString);

            object.put("event_latitude", event_latitude);
            object.put("event_longitude", event_longitude);

            object.put("event_last_sug_dep_time", start_datetime);
            object.put("event_last_time_on_way_in_second", "0");
            object.put("event_last_distance_in_meter", "0");

            object.put("event_name_new", event_name.getText());
            object.put("event_comment_new", event_comment.getText());

            object.put("event_starts_datetime_new", start_datetime);
            object.put("event_ends_datetime_new", end_datetime);

            object.put("event_venue_show_new", event_venue_location);
            object.put("event_venue_location_new", event_venue_location);

            object.put("event_repeats_type_new", repeatString);
            //punctual
            object.put("event_latitude_new", event_latitude);
            object.put("event_longitude_new", event_longitude);

            object.put("event_last_sug_dep_time_new", start_datetime);
            object.put("event_last_time_on_way_in_second_new", "0");
            object.put("event_last_distance_in_meter_new", "0");

            object.put("is_meeting", 0);
            object.put("is_host", 0);

            object.put("meeting_status", "");
            object.put("meeting_valid_token", UUID.randomUUID().toString());

            object.put("event_repeat_to_date", end_datetime);


            if (!repeatString.equals("One-time event")) {
                object.put("is_long_repeat", 1);
            } else {
                object.put("is_long_repeat", 0);
            }
            object.put("event_alert", alertString);
            object.put("calendar_id", calendarTypeString.calendarId);

            object.put("event_last_update_datetime", DateUtil.getDateStringFromCalendarGMT(Calendar.getInstance()));
            object.put("if_deleted", 0);

            object.put("event_is_punctual", is_punctual);
            object.put("event_is_punctual_new", is_punctual);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("newEvent", mEndYear + "-" + mEndMonthOfYear + "-" + mDayOfMonth);
        Log.d("newEvent", object.toString());
        final String url = URLs.SYNC;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(object);
        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("local_events", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    //
//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                String address = "";
                address = data.getStringExtra("address");
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
                            JSONObject geoLocation = geo.getJSONObject("location");
                            event_latitude = Double.toString(geoLocation.getDouble("lat"));
                            event_longitude = Double.toString(geoLocation.getDouble("lng"));
                            event_venue_location = locations;
                            event_venue.setText(locations);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
            }
        }
    }
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
