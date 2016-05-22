package com.itime.team.itime.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.itime.team.itime.utils.CalendarTypeUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by leveyleonhardt on 5/1/16.
 */
public class EventDetailEditFragment extends NewEventFragment {
    private static final int DELETE_REPEAT_EVENTS_REQUEST = 4;
    private EditText event_name;
    private EditText event_comment;
    //    private TextView event_venue;
    private TextView start_date;
    private TextView start_time;
    private TextView end_date;
    private TextView end_time;
    private TextView repeat_type;
    private TextView alert;
    private TextView calendar_type;
    private CheckBox punctual;
    private String event_id;

    private JSONObject event = new JSONObject();

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
    private String repeatString;
    private String repeatStringNew;
    private String alertString;
    private String event_longitude = "";
    private String event_latitude = "";
    private String event_start;
    private Calendar calendar = Calendar.getInstance();
    private ParcelableCalendarType calendarTypeString;
    private int is_punctual;
    private boolean advancedUpdate = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        try {
            event = new JSONObject(bundle.getString("event"));
            Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event.getString("event_starts_datetime")));
            Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event.getString("event_ends_datetime")));
            mYear = startCal.get(Calendar.YEAR);
            mMonthOfYear = startCal.get(Calendar.MONTH);
            mDayOfMonth = startCal.get(Calendar.DAY_OF_MONTH);
            mHour = startCal.get(Calendar.HOUR_OF_DAY);
            mMin = startCal.get(Calendar.MINUTE);
            mEndYear = endCal.get(Calendar.YEAR);
            mEndMonthOfYear = endCal.get(Calendar.MONTH);
            mEndDayOfMonth = endCal.get(Calendar.DAY_OF_MONTH);
            mEndHour = endCal.get(Calendar.HOUR_OF_DAY);
            mEndMin = endCal.get(Calendar.MINUTE);
            alertString = event.getString("event_alert");
            calendarTypeString = CalendarTypeUtil.findCalendarById(event.getString("calendar_id"));
            repeatString = event.getString("event_repeats_type");
            repeatStringNew = repeatString;
            event_id = event.getString("event_id");
            event_start = event.getString("event_starts_datetime");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        calendar.set(mYear, mMonthOfYear, mDayOfMonth, mHour, mMin);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        event_name = (EditText) view.findViewById(R.id.new_event_name);
        repeat_type = (TextView) view.findViewById(R.id.rep_new_event);
//        event_venue = (TextView) view.findViewById(R.id.new_event_venue);
        event_comment = (EditText) view.findViewById(R.id.new_event_comment);
        start_date = (TextView) view.findViewById(R.id.start_date);
        start_time = (TextView) view.findViewById(R.id.start_time);
        end_date = (TextView) view.findViewById(R.id.end_date);
        end_time = (TextView) view.findViewById(R.id.end_time);
        alert = (TextView) view.findViewById(R.id.new_event_alert);
        calendar_type = (TextView) view.findViewById(R.id.new_event_calendar_type);
        punctual = (CheckBox) view.findViewById(R.id.new_event_punctual);
        try {
            event_name.setText(event.getString("event_name"));
            event_venue.setText(event.getString("event_venue_location"));
            event_comment.setText(event.getString("event_comment"));
            punctual.setChecked(event.getBoolean("event_is_punctual"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        event_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                advancedUpdate = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        event_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                advancedUpdate = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        event_venue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GooglePlacesAutocompleteActivity.class);
                advancedUpdate = true;
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
                        advancedUpdate = true;
                        mYear = year;
                        mMonthOfYear = monthOfYear;
                        mDayOfMonth = dayOfMonth;
                        calendar.set(year, monthOfYear, dayOfMonth, mHour, mMin);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        mEndYear = calendar.get(Calendar.YEAR);
                        mEndMonthOfYear = calendar.get(Calendar.MONTH);
                        mEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        mEndHour = calendar.get(Calendar.HOUR_OF_DAY);
                        mEndMin = calendar.get(Calendar.MINUTE);
                        end_date.setText(DateUtil.formatDate(mEndDayOfMonth, mEndMonthOfYear, mEndYear));
                        end_time.setText(timeFormat(mEndHour, mEndMin));
                    }
                }, mYear, mMonthOfYear, mDayOfMonth).show();
            }
        });
        start_time.setText(timeFormat(mHour, mMin));
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        advancedUpdate = true;
                        start_time.setText(timeFormat(hourOfDay, minute));
                        mHour = hourOfDay;
                        mMin = minute;
                        calendar.set(mYear, mMonthOfYear, mDayOfMonth, mHour, mMin);
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        mEndYear = calendar.get(Calendar.YEAR);
                        mEndMonthOfYear = calendar.get(Calendar.MONTH);
                        mEndDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        mEndHour = calendar.get(Calendar.HOUR_OF_DAY);
                        mEndMin = calendar.get(Calendar.MINUTE);
                        end_date.setText(DateUtil.formatDate(mEndDayOfMonth, mEndMonthOfYear, mEndYear));
                        end_time.setText(timeFormat(mEndHour, mEndMin));
                    }
                }, mHour, mMin, true).show();
            }
        });

        end_date.setText(DateUtil.formatDate(mEndDayOfMonth, mEndMonthOfYear, mEndYear));
        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        end_date.setText(DateUtil.formatDate(dayOfMonth, monthOfYear, year));
                        advancedUpdate = true;

                        mEndYear = year;
                        mEndMonthOfYear = monthOfYear;
                        mEndDayOfMonth = dayOfMonth;
                    }
                }, mEndYear, mEndMonthOfYear, mEndDayOfMonth).show();
            }
        });

        end_time.setText(timeFormat(mEndHour, mEndMin));
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        advancedUpdate = true;
                        end_time.setText(timeFormat(hourOfDay, minute));
                        mEndHour = hourOfDay;
                        mEndMin = minute;
                    }
                }, mEndHour, mEndMin, true).show();
            }
        });

        alert.setText(alertString);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewEventAlertDialogFragment dialog = new NewEventAlertDialogFragment();
                final int originPosition = Arrays.asList(alertArray).indexOf(alertString);
                Bundle bundle = new Bundle();
                bundle.putInt(NewEventAlertDialogFragment.SELECTED, originPosition);
                dialog.setArguments(bundle);
                dialog.setListener(new RepeatSelectionListener() {
                    @Override
                    public void selectItem(int positon) {
                        if (positon != originPosition) {
                            advancedUpdate = true;
                        }
                        alertString = alertArray[positon];
                        alert.setText(alertArray[positon]);
                    }
                });
                dialog.show(getFragmentManager(), "alert_dialog");

            }
        });


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
        calendar_type.setText(calendarTypeString.calendarName + "-" + calendarTypeString.calendarOwnerName);
        calendar_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewEventCalendarTypeDialogFragment dialog = new NewEventCalendarTypeDialogFragment();
                final int originPosition = Events.calendarTypeList.indexOf(calendarTypeString);
                Bundle bundle = new Bundle();
                bundle.putInt(NewEventCalendarTypeDialogFragment.SELECTED, originPosition);
                dialog.setArguments(bundle);
                dialog.setListener(new RepeatSelectionListener() {
                    @Override
                    public void selectItem(int positon) {
                        if (originPosition != positon) {
                            advancedUpdate = true;
                        }
                        calendarTypeString = Events.calendarTypeList.get(positon);
                        UserUtil.setLastUserCalendarId(getActivity(), calendarTypeString.calendarId);

                        calendar_type.setText(Events.calendarTypeList.get(positon).calendarName + "-" + Events.calendarTypeList.get(positon).calendarOwnerName);
                    }
                });
                dialog.show(getFragmentManager(), "calendar_dialog");
            }
        });

        repeat_type.setText(repeatString);
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
                        repeatStringNew = repeatArray[positon];
                        repeat_type.setText(repeatArray[positon]);
                    }
                });
                dialog.show(fm, "repeat_dialog");
            }
        });


        return view;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.new_event_menu_send);
        item.setTitle("Save");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_event_menu_send) {
            if (repeatString.equals(EventUtil.ONE_TIME) && !repeatStringNew.equals(EventUtil.ONE_TIME) ) {
                postEvent(event_id, repeatStringNew);

            } else if (repeatString.equals(EventUtil.ONE_TIME) && repeatStringNew.equals(EventUtil.ONE_TIME)) {
                postEvent(event_id, repeatStringNew);
            } else if (!repeatString.equals(EventUtil.ONE_TIME)) {
                if (!repeatString.equals(repeatStringNew) || advancedUpdate) {
                    FragmentManager fm = getFragmentManager();
                    EventDetailRepeatDeleteDialogFragment dialog = new EventDetailRepeatDeleteDialogFragment();
                    dialog.setDialogTitle("Save");
                    dialog.setThisEvent("Save for this event only");
                    dialog.setFutureEvent("Save for the future events");
                    dialog.setTargetFragment(this, DELETE_REPEAT_EVENTS_REQUEST);
                    dialog.show(fm, "event_detail_repeat_delete_dialog");
                }
            }
//            } else if (!repeatString.equals(EventUtil.ONE_TIME) && !repeatStringNew.equals(EventUtil.ONE_TIME)) {
//                if (!repeatString.equals(repeatStringNew)) {
//                    FragmentManager fm = getFragmentManager();
//                    EventDetailRepeatDeleteDialogFragment dialog = new EventDetailRepeatDeleteDialogFragment();
//                    dialog.setTargetFragment(this, DELETE_REPEAT_EVENTS_REQUEST);
//                    dialog.show(fm, "event_detail_repeat_delete_dialog");
//
//                }
//
//            }
            Toast.makeText(getActivity(), "Save", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void postEvent(String id, String repeatString) {
        final JSONObject object = new JSONObject();
        try {
            object.put("event_id", id);
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
        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("edited", object.toString());
                intent.putExtras(bundle);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


    public void deleteFutureEvents() {
        JSONObject event = EventUtil.findEventById(event_id);
        try {
            event.put("is_long_repeat", 0);
            if (event.getBoolean("is_meeting")) {
                event.put("is_meeting", 1);
            } else {
                event.put("is_meeting", 0);
            }
            if (event.getBoolean("event_is_punctual_new")) {
                event.put("event_is_punctual_new", 1);
            } else {
                event.put("event_is_punctual_new", 0);
            }
            if (event.getBoolean("is_host")) {
                event.put("is_host", 1);
            } else {
                event.put("is_host", 0);
            }
            if (event.getBoolean("event_is_punctual")) {
                event.put("event_is_punctual", 1);
            } else {
                event.put("event_is_punctual", 0);
            }
            if (event.getBoolean("if_deleted")) {
                event.put("if_deleted", 1);
            } else {
                event.put("if_deleted", 0);
            }
            Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event_start));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            String reToDate = DateUtil.getDateStringFromCalendarGMT(cal);
            event.put("event_repeat_to_date", reToDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        array.put(event);
        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("local_events", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        String url = URLs.SYNC;

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                postEvent(UUID.randomUUID().toString(), repeatStringNew);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);
//        getActivity().setResult(200);
//        getActivity().finish();
        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();

    }

    public void deleteSingleEventInRepeat() {
        JSONObject jsonObject = new JSONObject();
        JSONObject ig_event = new JSONObject();
        JSONArray local_events = new JSONArray();
        try {
            ig_event.put("event_ignored_id", UUID.randomUUID().toString());
            JSONObject event = EventUtil.findEventById(event_id);
            Calendar start = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event.getString("event_starts_datetime")));
            Calendar end = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event.getString("event_ends_datetime")));
            int day = EventUtil.calDuration(event);
            Calendar current_start = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event_start));
            Calendar toBeSentCal = Calendar.getInstance();
            if (day > 0) {
                if (start.get(Calendar.HOUR_OF_DAY) == current_start.get(Calendar.HOUR_OF_DAY) && start.get(Calendar.MINUTE) == current_start.get(Calendar.MINUTE)) {
                    //This is the first day of a multi-day event
                    toBeSentCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event_start));
                } else {
                    //This is not the first day
                    boolean flag = false;
                    for (int i = 0; i <= day; i++) {
                        current_start.add(Calendar.DAY_OF_MONTH, -1);
                        List<JSONObject> eventList = EventUtil.getEventFromDate(current_start.get(Calendar.DAY_OF_MONTH), current_start.get(Calendar.MONTH) + 1, current_start.get(Calendar.YEAR));
                        for (JSONObject object : eventList) {
                            if (object.getString("event_id").equals(event_id)) {
                                Calendar preCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime")));
                                if (preCal.get(Calendar.HOUR_OF_DAY) == start.get(Calendar.HOUR_OF_DAY) && preCal.get(Calendar.MINUTE) == start.get(Calendar.MINUTE)) {
                                    toBeSentCal = preCal;
                                    flag = true;
                                }

                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                }

            } else {
                toBeSentCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(event_start));
            }
            ig_event.put("event_starts_datetime", DateUtil.getDateStringFromCalendarGMT(toBeSentCal));
            ig_event.put("event_id", event_id);
            ig_event.put("user_id", User.ID);
            ig_event.put("last_update_datetime", DateUtil.getDateStringFromCalendar(Calendar.getInstance()));
            jsonObject.put("user_id", User.ID);
            local_events.put(ig_event);
            jsonObject.put("local_events_ignored", local_events);
//            ig_event.put("event_starts_datetime", )
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        String url = URLs.SYNC_IGNORED;
        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                postEvent(UUID.randomUUID().toString(), repeatStringNew);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }


}