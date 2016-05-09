package com.itime.team.itime.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.EventsDetailEditActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.CalendarTypeUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonArrayAuthRequest;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by leveyleonhardt on 4/23/16.
 */
public class EventDetailFragment extends Fragment {

    private static final int DIALOG_FRAGMENT = 1;
    private static final int DELETE_REPEAT_EVENTS_REQUEST = 2;
    private static final int EDIT_REQUEST = 3;
    private EditText name;
    private EditText venue;
    private TextView start_text;
    private TextView end_text;
    private TextView type;
    private TextView alert;
    private TextView calendarType;
    private Button delete_button;

    private String event_name;
    private String event_venue;
    private String event_start;
    private String event_end;
    private String event_type;
    private String event_alert;
    private String calendar_type;
    private String event_id;
    private String jsonString;
    private JSONObject eventJsonObject = new JSONObject();
    private boolean edited;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_detail, container, false);
        name = (EditText) v.findViewById(R.id.event_name);
        venue = (EditText) v.findViewById(R.id.event_venue);
        start_text = (TextView) v.findViewById(R.id.start_text);
        end_text = (TextView) v.findViewById(R.id.end_text);
        type = (TextView) v.findViewById(R.id.rep_text);
        alert = (TextView) v.findViewById(R.id.alert_text);
        calendarType = (TextView) v.findViewById(R.id.calendar_type_text);
        delete_button = (Button) v.findViewById(R.id.event_detail_delete_button);


        Bundle bundle = getActivity().getIntent().getExtras();
        event_name = bundle.getString("event_name");
        event_venue = bundle.getString("venue");
        event_start = bundle.getString("start_time");
        event_end = bundle.getString("end_time");
        event_type = bundle.getString("repeat_type");
        event_alert = bundle.getString("alert");
        calendar_type = bundle.getString("calendar_type");
        event_id = bundle.getString("event_id");
        jsonString = bundle.getString("json");
        try {
            eventJsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        name.setText(event_name);
        venue.setText(event_venue);
        start_text.setText(DateUtil.formatToReadable(event_start));
        end_text.setText(DateUtil.formatToReadable(event_end));
        type.setText(event_type);
        alert.setText(event_alert);
        calendarType.setText(calendar_type);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event_type.equals(EventUtil.ONE_TIME)) {
                    showDialog();
                } else {
                    showRepeatDeleteDialog();
                }
            }
        });
        return v;
    }


    public void clickOK() {
        JSONObject jsonObject = new JSONObject();
        JSONObject event = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            event.put("if_deleted", 1);
            event.put("event_id", event_id);
            event.put("event_starts_datetime", eventJsonObject.getString("event_starts_datetime"));
            event.put("event_ends_datetime", eventJsonObject.getString("event_ends_datetime"));
            event.put("event_ends_datetime_new", eventJsonObject.getString("event_ends_datetime_new"));
            event.put("event_starts_datetime_new", eventJsonObject.getString("event_starts_datetime_new"));
            event.put("event_is_punctual", 0);
            event.put("event_is_punctual_new", 0);
            event.put("is_long_repeat", 0);
            array.put(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("local_events", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        String url = URLs.SYNC;
//        JsonArrayAuthRequest request = new JsonArrayAuthRequest(Request.Method.POST, url, jsonObject.toString(), new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                getActivity().setResult(getActivity().RESULT_OK);
//                getActivity().finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                getActivity().setResult(getActivity().RESULT_OK);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
    }

    private void showDialog() {
        FragmentManager fm = getFragmentManager();
        EventDetailDeleteDialogFragment dialog = new EventDetailDeleteDialogFragment();
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(fm, "event_detail_delete_dialog");
    }

    private void showRepeatDeleteDialog() {
        FragmentManager fm = getFragmentManager();
        EventDetailRepeatDeleteDialogFragment dialog = new EventDetailRepeatDeleteDialogFragment();
        dialog.setTargetFragment(this, DELETE_REPEAT_EVENTS_REQUEST);
        dialog.show(fm, "event_detail_repeat_delete_dialog");
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
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

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
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.event_detail_edit) {
            Intent intent = new Intent(getActivity(), EventsDetailEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("event", jsonString);
            Log.d("jsonevent", jsonString);
            intent.putExtras(bundle);
            startActivityForResult(intent, EDIT_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject event = new JSONObject(data.getExtras().getString("edited"));
                    name.setText(event.getString("event_name"));
                    venue.setText(event.getString("event_venue_location"));
                    start_text.setText(DateUtil.formatToReadable(event.getString("event_starts_datetime")));
                    end_text.setText(DateUtil.formatToReadable(event.getString("event_ends_datetime")));
                    type.setText(event.getString("event_repeats_type"));
                    alert.setText(event.getString("event_alert"));
                    calendarType.setText(CalendarTypeUtil.findCalendarById(event.getString("calendar_id")).calendarName);
                    edited = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isEdited() {
        return edited;
    }
}
