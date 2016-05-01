package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
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
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leveyleonhardt on 4/23/16.
 */
public class EventDetailFragment extends Fragment {

    private static final int DIALOG_FRAGMENT = 1;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            event.put("event_starts_datetime", "");
            event.put("event_ends_datetime", "");
            event.put("event_ends_datetime_new", "");
            event.put("event_starts_datetime_new", "");
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
        getActivity().setResult(getActivity().RESULT_OK);
        getActivity().finish();
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
        dialog.show(fm, "event_detail_repeat_delete_dialog");
    }
}
