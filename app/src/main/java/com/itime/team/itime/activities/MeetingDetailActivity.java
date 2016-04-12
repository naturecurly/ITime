package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.itime.team.itime.fragments.MeetingDetailReasonDialogFragment;
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
public class MeetingDetailActivity extends FragmentActivity implements OnMapReadyCallback, RadioGroup.OnCheckedChangeListener {
    private MapFragment mMapFragment;
    private RadioGroup mRadioGroup;
    private RadioButton mAccept, mMaybe, mDecline;
    private MeetingInfo mMeetingInfo;
    private TextView mMeetingName, mMeetingAddress, mMeetingCity, mName,mID;
    private Button mAttendee;
    private TextView mDeparture, mStart, mEnd, mRepeat;
    private CheckBox mPunctual;
    private EditText mNote;
    private Button mEmail, mQuit;
    private ImageView mImage;

    private float mLat;
    private float mLog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);
        init();


        loadMeetingInfo();
    }

    private void init(){
        mRadioGroup = (RadioGroup) findViewById(R.id.meeting_detail_radio_group);
        mAccept = (RadioButton) findViewById(R.id.meeting_detail_radio_accept);
        mMaybe = (RadioButton) findViewById(R.id.meeting_detail_radio_maybe);
        mDecline = (RadioButton) findViewById(R.id.meeting_detail_radio_decline);
        mRadioGroup.setOnCheckedChangeListener(this);

        mMeetingName = (TextView) findViewById(R.id.meeting_detail_event_name);
        mMeetingAddress = (TextView) findViewById(R.id.meeting_detail_address);
        mMeetingCity = (TextView) findViewById(R.id.meeting_detail_city);
        mName = (TextView) findViewById(R.id.meeting_detail_name);
        mID = (TextView) findViewById(R.id.meeting_detail_id);
        mAttendee = (Button) findViewById(R.id.meeting_detail_attendee);
        mDeparture = (TextView) findViewById(R.id.meeting_detail_depture);
        mStart = (TextView) findViewById(R.id.meeting_detail_starts);
        mEnd = (TextView) findViewById(R.id.meeting_detail_ends);
        mRepeat = (TextView) findViewById(R.id.meeting_detail_repeats);
        mPunctual = (CheckBox) findViewById(R.id.meeting_detail_punctual);
        mEmail = (Button) findViewById(R.id.meeting_detail_email);
        mQuit = (Button) findViewById(R.id.quit);

        mNote = (EditText) findViewById(R.id.meeting_detail_note);
        mNote.clearFocus();

        mImage = (ImageView) findViewById(R.id.meeting_detail_image);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(mLog, mLat);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void loadMap(){
        mLat = mMeetingInfo.getLatitude() == "" ? 144 : Float.valueOf(mMeetingInfo.getLatitude());
        mLog = mMeetingInfo.getLongitude() == "" ? -37 : Float.valueOf(mMeetingInfo.getLatitude());
        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.meeting_map);
        mMapFragment.getMapAsync(this);
    }

    private void loadMeetingInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", "23C5D415-91E1-4E50-BDF9-26AECFE28506");
            jsonObject.put("user_id", "1@2.com");
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
            mID.setText(mMeetingInfo.getHostID());
            mName.setText(mMeetingInfo.getHostID());
            mDeparture.setText(dateOutputFormat(mMeetingInfo.getStart()));
            mStart.setText(dateOutputFormat(mMeetingInfo.getStart()));
            mEnd.setText(dateOutputFormat(mMeetingInfo.getEnd()));
            mRepeat.setText(mMeetingInfo.getRepeat());
            mPunctual.setChecked(mMeetingInfo.getPunctual());

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
            jsonObject.put("meeting_id", "23C5D415-91E1-4E50-BDF9-26AECFE28506");
            jsonObject.put("user_id", "1@2.com");
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == mAccept.getId()){
            mAccept.setBackgroundColor(getResources().getColor(R.color.grey));
            mMaybe.setBackgroundColor(getResources().getColor(R.color.white));
            mDecline.setBackgroundColor(getResources().getColor(R.color.white));

            responseMeeting("Accept");
        } else if(checkedId == mMaybe.getId()){
            mAccept.setBackgroundColor(getResources().getColor(R.color.white));
            mMaybe.setBackgroundColor(getResources().getColor(R.color.grey));
            mDecline.setBackgroundColor(getResources().getColor(R.color.white));

            responseMeeting("Maybe");
        } else if(checkedId == mDecline.getId()){
            mAccept.setBackgroundColor(getResources().getColor(R.color.white));
            mMaybe.setBackgroundColor(getResources().getColor(R.color.white));
            mDecline.setBackgroundColor(getResources().getColor(R.color.grey));

            MeetingDetailReasonDialogFragment dialog = new MeetingDetailReasonDialogFragment();
            dialog.show(getSupportFragmentManager(),"reasonDialog");
        }
    }

}
