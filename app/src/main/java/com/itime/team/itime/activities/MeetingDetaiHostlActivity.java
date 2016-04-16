package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.MeetingInfo;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/4/5.
 */
public class MeetingDetaiHostlActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener {
    private MapFragment mMapFragment;
    private MeetingInfo mMeetingInfo;
    private TextView mMeetingName, mMeetingAddress, mMeetingCity;
    private TextView mNewMeetingName, mNewMeetingAddress;
    private Button mAttendee;
    private TextView mDeparture, mStart, mEnd, mRepeat;
    private TextView mNewStart, mNewEnd, mNewRepeat, mNewPunctual;
    private CheckBox mPunctual;
    private EditText mNote;
    private EditText mNewNote;
    private Button mEmail, mQuit;
    private ImageView mImage;
    private Button mConfirm, mReset;

    private LinearLayout mLNewName, mLNewVenue, mLNewStart, mLNewEnd, mLNewRepeat, mLNewPunctual, mLNewNote;

    private float mLat;
    private float mLog;

    public static final String ARG_MEETING_ID = "arg_meeting_id";
    private String mMeetingId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail_host);
        init();
        loadMeetingInfo();
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.meeting_detail_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.meeting_detail_title);
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

        mMeetingId = getIntent().getStringExtra(ARG_MEETING_ID);



        mMeetingName = (TextView) findViewById(R.id.meeting_detail_event_name);
        mMeetingAddress = (TextView) findViewById(R.id.meeting_detail_address);
        mMeetingCity = (TextView) findViewById(R.id.meeting_detail_city);
        mAttendee = (Button) findViewById(R.id.meeting_detail_attendee);
        mDeparture = (TextView) findViewById(R.id.meeting_detail_depture);
        mStart = (TextView) findViewById(R.id.meeting_detail_starts);
        mEnd = (TextView) findViewById(R.id.meeting_detail_ends);
        mRepeat = (TextView) findViewById(R.id.meeting_detail_repeats);
        mPunctual = (CheckBox) findViewById(R.id.meeting_detail_punctual);
        mEmail = (Button) findViewById(R.id.meeting_detail_email);
        mQuit = (Button) findViewById(R.id.quit);

        mConfirm = (Button) findViewById(R.id.meeting_detail_confirm);
        mConfirm.setOnClickListener(this);
        mReset = (Button) findViewById(R.id.meeting_detail_reset);
        mReset.setOnClickListener(this);

        mNote = (EditText) findViewById(R.id.meeting_detail_note);
        mNote.clearFocus();

        mImage = (ImageView) findViewById(R.id.meeting_detail_image);
        mAttendee.setOnClickListener(this);

        mNewNote = (EditText) findViewById(R.id.meeting_detail_note_new);
        mNewEnd = (TextView) findViewById(R.id.meeting_detail_event_end_new);
        mNewStart = (TextView) findViewById(R.id.meeting_detail_event_start_new);
        mNewRepeat = (TextView) findViewById(R.id.meeting_detail_event_repeat_new);
        mNewMeetingAddress = (TextView) findViewById(R.id.meeting_detail_event_venue_new);
        mNewMeetingName = (TextView) findViewById(R.id.meeting_detail_event_name_new);
        mNewPunctual = (TextView) findViewById(R.id.meeting_detail_event_punctual_new);

        mLNewName = (LinearLayout) findViewById(R.id.meeting_detail_event_name_layout);
        mLNewVenue = (LinearLayout) findViewById(R.id.meeting_detail_event_venue_layout);
        mLNewStart = (LinearLayout) findViewById(R.id.meeting_detail_event_start_layout);
        mLNewEnd = (LinearLayout) findViewById(R.id.meeting_detail_event_end_layout);
        mLNewPunctual = (LinearLayout) findViewById(R.id.meeting_detail_event_punctual_layout);
        mLNewRepeat = (LinearLayout) findViewById(R.id.meeting_detail_event_repeat_layout);
        mLNewNote = (LinearLayout) findViewById(R.id.meeting_detail_note_layout);

    }

    private void showLayout(MeetingInfo meetingInfo){
        if (!meetingInfo.getName().equals(meetingInfo.getNewName())){
            mLNewName.setVisibility(View.VISIBLE);
        }
        if(!meetingInfo.getVenue().equals(meetingInfo.getNewVenue()) || !meetingInfo.getLocation().equals(meetingInfo.getNewLocation())){
            mLNewVenue.setVisibility(View.VISIBLE);
        }
        if(!dateOutputFormat(meetingInfo.getStart()).equals(dateOutputFormat(meetingInfo.getNewStart()))){
            mLNewStart.setVisibility(View.VISIBLE);
        }
        if(!dateOutputFormat(meetingInfo.getEnd()).equals(dateOutputFormat(meetingInfo.getNewEnd()))){
            mLNewEnd.setVisibility(View.VISIBLE);
        }
        if(!meetingInfo.getRepeat().equals(meetingInfo.getNewRepeat())){
            mLNewRepeat.setVisibility(View.VISIBLE);
        }
        if(meetingInfo.getPunctual() != meetingInfo.getNewPunctual()){
            mLNewPunctual.setVisibility(View.VISIBLE);
        }
        if(!meetingInfo.getComment().equals(meetingInfo.getNewComment())){
            mLNewNote.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(mLog, mLat);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void loadMap(){
        mLat = mMeetingInfo.getLatitude().equals("") ? 144 : Float.valueOf(mMeetingInfo.getLatitude());
        mLog = mMeetingInfo.getLongitude().equals("") ? -37 : Float.valueOf(mMeetingInfo.getLatitude());
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.meeting_map);
        mMapFragment.getMapAsync(this);
    }

    private void loadMeetingInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", mMeetingId);
            jsonObject.put("user_id", User.ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.LOAD_MEETING_INFO;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleMeetingInfo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void handleMeetingInfo(JSONObject json){
        mMeetingInfo = new MeetingInfo();
        try {
            mMeetingInfo.setLatitude(json.getString("event_latitude"));
            mMeetingInfo.setNewLocation(json.getString("event_venue_location_new"));
            mMeetingInfo.setNewName(json.getString("event_name_new"));
            mMeetingInfo.setStatus(json.getString("meeting_status"));
            mMeetingInfo.setNewRepeat(json.getString("event_repeats_type_new"));
            mMeetingInfo.setLongitude(json.getString("event_longitude"));
            mMeetingInfo.setLocation(json.getString("event_venue_location"));
            mMeetingInfo.setNewPunctual(json.getBoolean("event_is_punctual_new"));
            mMeetingInfo.setComment(json.getString("event_comment"));
            mMeetingInfo.setPunctual(json.getBoolean("event_is_punctual"));
            mMeetingInfo.setEnd(json.getString("event_ends_datetime"));
            mMeetingInfo.setId(json.getString("meeting_id"));
            mMeetingInfo.setVenue(json.getString("event_venue_show"));
            mMeetingInfo.setHostID(json.getString("host_id"));
            mMeetingInfo.setRepeat(json.getString("event_repeats_type"));
            mMeetingInfo.setNewStart(json.getString("event_starts_datetime_new"));
            mMeetingInfo.setNewVenue(json.getString("event_venue_show_new"));
            mMeetingInfo.setStart(json.getString("event_starts_datetime"));
            mMeetingInfo.setNewLongitude(json.getString("event_longitude_new"));
            mMeetingInfo.setName(json.getString("event_name"));
            mMeetingInfo.setNewLatitude(json.getString("event_latitude_new"));
            mMeetingInfo.setToken(json.getString("meeting_valid_token"));
            mMeetingInfo.setNewEnd(json.getString("event_ends_datetime_new"));
            mMeetingInfo.setNewComment(json.getString("event_comment_new"));

            mMeetingName.setText(mMeetingInfo.getName());
            mMeetingCity.setText(mMeetingInfo.getLocation());
            mMeetingAddress.setText(mMeetingInfo.getVenue());
            mDeparture.setText(dateOutputFormat(mMeetingInfo.getStart()));
            mStart.setText(dateOutputFormat(mMeetingInfo.getStart()));
            mEnd.setText(dateOutputFormat(mMeetingInfo.getEnd()));
            mRepeat.setText(mMeetingInfo.getRepeat());
            mPunctual.setChecked(mMeetingInfo.getPunctual());
            mNote.setText(mMeetingInfo.getComment());

            mNewMeetingName.setText(mMeetingInfo.getNewName());
            mNewMeetingAddress.setText(mMeetingInfo.getNewLocation() + mMeetingInfo.getNewVenue());
            mNewRepeat.setText(mMeetingInfo.getNewRepeat());
            mNewStart.setText(dateOutputFormat(mMeetingInfo.getNewStart()));
            mNewEnd.setText(dateOutputFormat(mMeetingInfo.getNewEnd()));
            mNewNote.setText(mMeetingInfo.getNewComment());
            mNewPunctual.setText(mMeetingInfo.getPunctual() ? getString(R.string.yes) : getString(R.string.no));

            showLayout(mMeetingInfo);
            loadMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String dateOutputFormat(Calendar calendar){
        String time = new SimpleDateFormat("yyyy hh:mm").format(calendar.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String  week = DateUtil.weekName[calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH)];
        String month = DateUtil.month[calendar.get(Calendar.MONTH)];
        String output = week + ", " + day + " " + month + " " + time;
        return output;

    }

    private void responseMeeting(String status){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", mMeetingId);
            jsonObject.put("user_id", User.ID);
            jsonObject.put("meeting_status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.MEETING_STATUS_CHANGE;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("result").equals("success")){
                        Toast.makeText(getApplicationContext(), getString(R.string.time_out), Toast.LENGTH_LONG);
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

    private void confirm(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", mMeetingId);
            jsonObject.put("user_id", User.ID);
            jsonObject.put("meeting_valid_token", mMeetingInfo.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.HOST_SEND_CONFIRM_MEETING_ANYWAY;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("result").equals("success")){
                        Toast.makeText(getApplicationContext(), getString(R.string.time_out), Toast.LENGTH_LONG);
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

    private void reset(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", mMeetingId);
            jsonObject.put("user_id", User.ID);
            jsonObject.put("meeting_valid_token", mMeetingInfo.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.HOST_SEND_RESET_MEETING_ANYWAY;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("result").equals("success")){
                        Toast.makeText(getApplicationContext(), getString(R.string.time_out), Toast.LENGTH_LONG);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == mAttendee.getId()){
            Intent intent = new Intent(this, MeetingAttendeesActivity.class);
            intent.putExtra("arg_meeting_id", mMeetingId);
            startActivity(intent);
        } else if (v.getId() == mConfirm.getId()) {
            confirm();
        } else if (v.getId() == mReset.getId()) {
            reset();
        }
    }
}
