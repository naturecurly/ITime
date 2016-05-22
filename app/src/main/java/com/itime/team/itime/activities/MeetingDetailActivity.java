package com.itime.team.itime.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.MapFragment;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.MeetingInfo;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.fragments.MeetingDetailCancelReasonDialogFragment;
import com.itime.team.itime.fragments.MeetingDetailReasonDialogFragment;
import com.itime.team.itime.fragments.NewEventAlertDialogFragment;
import com.itime.team.itime.fragments.NewEventCalendarTypeDialogFragment;
import com.itime.team.itime.listener.RepeatSelectionListener;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.utils.CalendarTypeUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.ICS;
import com.itime.team.itime.utils.ITimePreferences;
import com.itime.team.itime.utils.Invitation;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Weiwei Cai on 16/4/5.
 * This activity shows all meeting details for meeting attendees.
 * The most of information has two types, the first one the current information, the second one is
 * new information. If the current information and new information is the same, then the new information
 * will not be represented.
 * Since the the majority of the code is the same as MeetingDetailHostActivity, the details can refer
 * it.
 *
 */
public class MeetingDetailActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener {
    private MapFragment mMapFragment;
    private RadioGroup mRadioGroup;
    private RadioButton mAccept, mMaybe, mDecline;
    private MeetingInfo mMeetingInfo;
    private TextView mMeetingName, mMeetingAddress, mMeetingCity, mName,mID;
    private TextView mNewMeetingName, mNewMeetingAddress;
    private Button mAttendee;
    private TextView mDeparture, mStart, mEnd, mRepeat;
    private TextView mNewStart, mNewEnd, mNewRepeat, mNewPunctual;
    private CheckBox mPunctual;
    private EditText mNote;
    private EditText mNewNote;
    private Button mQuit;
    private LinearLayout mEmail;
    private ImageView mImage;
    private String mEventId;
    private File ICSFile;
    private LinearLayout mAlertLayout, mCalendarLayout;
    private TextView mAlertText,mCalendarText;
    private ParcelableCalendarType calendarTypeString = Events.calendarTypeList.get(0);
    private String mCalendarID,mAlertID;


    private String alertString;
    private LinearLayout mLNewName, mLNewVenue, mLNewStart, mLNewEnd, mLNewRepeat, mLNewPunctual, mLNewNote;

    private float mLat;
    private float mLog;

    public static final String ARG_MEETING_ID = "arg_meeting_id";
    private String mMeetingId;

//    private Map<Integer,String> positionMap;
    private Map<Integer, String> repeatMap;
    private int mPosition = 1;
    private int mRepeatPosition = 0;
    private int mCalendarPosition = 0;
//    private Map<String,Integer> positionRecordMap;
    private Map<String, Integer> repeatRecordMap;

    private String[] alertArray;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_detail);
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

//        positionMap = new HashMap<>();
//        positionMap.put(0,"None");
//        positionMap.put(1, "At time of Departure");
//        positionMap.put(2, "5 minutes before");
//        positionMap.put(3, "10 minutes before");
//        positionMap.put(4, "15 minutes before");
//        positionMap.put(5, "30 minutes before");
//        positionMap.put(6, "1 hour before");
        repeatMap = new HashMap<>();
        repeatMap.put(0,"One-time event");
        repeatMap.put(1,"Daily");
        repeatMap.put(2,"Weekly");
        repeatMap.put(3,"Bi-Weekly");
        repeatMap.put(4,"Monthly");
        repeatMap.put(5,"Yearly");

//        positionRecordMap = new HashMap<>();
//        positionRecordMap.put("None",0);
//        positionRecordMap.put("At time of Event",1);
//        positionRecordMap.put("5 minutes before",2);
//        positionRecordMap.put("10 minutes before",3);
//        positionRecordMap.put("15 minutes before",4);
//        positionRecordMap.put("30 minutes before",5);
//        positionRecordMap.put("1 hour before",6);
        repeatRecordMap = new HashMap<>();
        repeatRecordMap.put("One-time event",0);
        repeatRecordMap.put("Daily",1);
        repeatRecordMap.put("Weekly",2);
        repeatRecordMap.put("Bi-Weekly",3);
        repeatRecordMap.put("Monthly",4);
        repeatRecordMap.put("Yearly",5);

        Resources resources = getResources();
        alertArray = resources.getStringArray(R.array.entry_default_alert_time);


        mMeetingId = getIntent().getStringExtra(ARG_MEETING_ID);
        mEventId = getIntent().getStringExtra("event_id");
        mCalendarID = getIntent().getStringExtra("calendar_id");
        mAlertID = getIntent().getStringExtra("event_alert");
//        if (mAlertID != null && mAlertID.equals("At time of Departure")){
//            mAlertID = getString(R.string.alert_default);
//        }
//        if(mAlertID != null){
//            if(mAlertID.equals("At time of Departure")){
//                mAlertID = "At time of Event";
//            }
//        }else{
//            mAlertID = "At time of Event";
//        }


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
        mEmail = (LinearLayout) findViewById(R.id.meeting_detail_email);
        mEmail.setOnClickListener(this);
        mQuit = (Button) findViewById(R.id.meeting_detail_quit);
        mQuit.setOnClickListener(this);

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

        alertString = getString(R.string.alert_default);
        mAlertLayout = (LinearLayout) findViewById(R.id.meeting_detail_alert_layout);
        mAlertLayout.setOnClickListener(this);
        mCalendarLayout = (LinearLayout) findViewById(R.id.meeting_detail_calendar_layout);
        mCalendarLayout.setOnClickListener(this);
        mAlertText = (TextView) findViewById(R.id.meeting_detail_alert);
        mCalendarText = (TextView) findViewById(R.id.meeting_detail_calendar);

        mCalendarText.setText(Events.calendarTypeList.get(0).calendarName);
        try {
            if (mAlertID == null) {
                mAlertID = UserUtil.getDefaultAlert(this);
            } else {
//                mAlertText.setText(mAlertID);
//
//                mPosition = positionRecordMap.get(mAlertID);

                String alertString = mAlertID;
                mPosition = Arrays.asList(alertArray).indexOf(alertString);
                mAlertText.setText(alertString);
            }
        } catch (Exception e){
            mAlertText.setText(getString(R.string.alert_default));
            mPosition = 1;
        }
        if (mCalendarID != null && !mCalendarID.equals("")) {
            if (CalendarTypeUtil.findCalendarById(mCalendarID) == null) {
                mCalendarText.setText(getString(R.string.Calendar));
            }else {
                mCalendarText.setText(CalendarTypeUtil.findCalendarById(mCalendarID).calendarName + "--" +
                        CalendarTypeUtil.findCalendarById(mCalendarID).calendarOwnerName);
                mCalendarPosition = Events.calendarTypeList.indexOf(CalendarTypeUtil.findCalendarById(mCalendarID));
            }
        }else{
            mCalendarText.setText(getString(R.string.Calendar));
        }
//        mAlertText.setText((mAlertID == null || mAlertID.equals("")) ? getString(R.string.alert_default) : mAlertID);
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
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLng sydney = new LatLng(mLog, mLat);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//
//    private void loadMap(){
//        mLat = mMeetingInfo.getLatitude().equals("") ? 144 : Float.valueOf(mMeetingInfo.getLatitude());
//        mLog = mMeetingInfo.getLongitude().equals("") ? -37 : Float.valueOf(mMeetingInfo.getLatitude());
//        mMapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.meeting_map);
//        mMapFragment.getMapAsync(this);
//    }

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
                createICS();
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
            mNote.setText(mMeetingInfo.getComment());

            mNewMeetingName.setText(mMeetingInfo.getNewName());
            mNewMeetingAddress.setText(mMeetingInfo.getNewLocation() + mMeetingInfo.getNewVenue());
            mNewRepeat.setText(mMeetingInfo.getNewRepeat());
            mNewStart.setText(dateOutputFormat(mMeetingInfo.getNewStart()));
            mNewEnd.setText(dateOutputFormat(mMeetingInfo.getNewEnd()));
            mNewNote.setText(mMeetingInfo.getNewComment());
            mNewPunctual.setText(mMeetingInfo.getPunctual() ? getString(R.string.yes) : getString(R.string.no));

            showLayout(mMeetingInfo);
            //loadMap();
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

            MeetingDetailReasonDialogFragment dialog = new MeetingDetailReasonDialogFragment(mMeetingId);
            dialog.show(getSupportFragmentManager(),"reasonDialog");
        }
    }

    private void email(){

        File file = new File(getFilesDir(), "NewMeeing.ics");
        Uri fileUri = FileProvider.getUriForFile(this, "com.itime.team.itime.fileprovider", file);
        String mySbuject = getString(R.string.add_friend);
        String myCc = "cc";
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
        myIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.meeting_detail_email_content));
        myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        myIntent.putExtra(Intent.EXTRA_STREAM, fileUri);


        startActivity(Intent.createChooser(myIntent, "mail"));
    }

    private void createICS(){
        String eventID = mEventId;
        String eventName = mMeetingName.getText().toString();
        String description = mNote.getText().toString();
        String start = "";
        String end = "";
        if(mMeetingInfo!=null && !mMeetingInfo.getName().equals("")){
            start = DateUtil.getICSTime(mMeetingInfo.getStart());
            end = DateUtil.getICSTime(mMeetingInfo.getEnd());
        }


        ICS ics = new ICS(eventID, eventName, description, start, end,this);
        Invitation host = new Invitation(User.ID, User.ID);
        ics.attachInvitation(host);
        ICSFile = ics.createICS("NewMeeing.ics");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mAttendee.getId()){
            Intent intent = new Intent(this, MeetingAttendeesActivity.class);
            intent.putExtra("arg_meeting_id", mMeetingId);
            startActivity(intent);
        } else if (v.getId() == mQuit.getId()) {
            deleteMeeting();
        } else if (v.getId() == mEmail.getId()) {
            email();
        } else if (v.getId() == mCalendarLayout.getId()) {
            NewEventCalendarTypeDialogFragment dialog = new NewEventCalendarTypeDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(NewEventCalendarTypeDialogFragment.SELECTED, mCalendarPosition);
            dialog.setArguments(bundle);
            dialog.setListener(new RepeatSelectionListener() {
                @Override
                public void selectItem(int positon) {
                    calendarTypeString = Events.calendarTypeList.get(positon);
                    mCalendarText.setText(Events.calendarTypeList.get(positon).calendarName + "--" +
                            Events.calendarTypeList.get(positon).calendarOwnerName);
                    mCalendarID = Events.calendarTypeList.get(positon).calendarId;
                    postEvent(getApplicationContext(),mEventId,mMeetingInfo);
                    mCalendarPosition = Events.calendarTypeList.indexOf(CalendarTypeUtil.findCalendarById(mCalendarID));
                }
            });
            dialog.show(getSupportFragmentManager(), "calendar_dialog");
        } else if (v.getId() == mAlertLayout.getId()) {
            NewEventAlertDialogFragment dialog = new NewEventAlertDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(NewEventAlertDialogFragment.SELECTED, mPosition);
            dialog.setArguments(bundle);
            dialog.setListener(new RepeatSelectionListener() {
                @Override
                public void selectItem(int positon) {
                    alertString = alertArray[positon];
                    mAlertText.setText(alertArray[positon]);
                    mAlertID = alertArray[positon];
                    postEvent(getApplicationContext(),mEventId,mMeetingInfo);
                    // post event, refresh meeting in calendarFragment
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    sharedPreferences.edit().putBoolean(ITimePreferences.CALENDAR_TYPE_CHANGED, true).apply();
                    mPosition = positon;

                }
            });
            dialog.show(getSupportFragmentManager(), "alert_dialog");
        }
    }

    private void deleteMeeting(){
        MeetingDetailCancelReasonDialogFragment dialog
                = new MeetingDetailCancelReasonDialogFragment(mMeetingId, mMeetingInfo.getToken(), mEventId ,false);
        dialog.show(getSupportFragmentManager(),"reasonQuitDialog");
    }

    private void postEvent(Context context, String meetingID, MeetingInfo meetingInfo) {
        String startDateForPost = DateUtil.getDateStringFromCalendar(meetingInfo.getNewStart());
        String endDateForPost = DateUtil.getDateStringFromCalendar(meetingInfo.getNewEnd());
        String comment = meetingInfo.getNewComment();
        String name = meetingInfo.getNewName();
        String punctual = "false";
        String repeative = meetingInfo.getRepeat();

        String address = meetingInfo.getNewVenue();
        String location = meetingInfo.getLocation();

//        String location = mAddress.equals("") ? getString(R.string.post_null) : mAddress;
//        String showLocation = address[0].equals("") ? getString(R.string.post_null) : address[0];

        JSONObject object = new JSONObject();
        try {
            object.put("event_id", meetingID);
            object.put("user_id", User.ID);
            object.put("host_id", meetingInfo.getHostID());
            object.put("meeting_id", meetingID);

            object.put("event_name", name.equals("") ? context.getString(R.string.new_meeting) : name);
            object.put("event_comment", comment);
            object.put("event_starts_datetime", startDateForPost);
            object.put("event_ends_datetime", endDateForPost);

            object.put("event_venue_show", address);
            object.put("event_venue_location", location);

            object.put("event_repeats_type", repeative);

            object.put("event_latitude", 0);
            object.put("event_longitude", 0);

            object.put("event_last_sug_dep_time", startDateForPost);
            object.put("event_last_time_on_way_in_second", "0");
            object.put("event_last_distance_in_meter", "0");

            object.put("event_name_new", name);
            object.put("event_comment_new", comment);

            object.put("event_starts_datetime_new", startDateForPost);
            object.put("event_ends_datetime_new", endDateForPost);

            object.put("event_venue_show_new", address);
            object.put("event_venue_location_new", location);

            object.put("event_repeats_type_new", repeative);
            //punctual
            object.put("event_latitude_new", 0);
            object.put("event_longitude_new", 0);

            object.put("event_last_sug_dep_time_new", startDateForPost);
            object.put("event_last_time_on_way_in_second_new", "0");
            object.put("event_last_distance_in_meter_new", "0");

            object.put("is_meeting", 1);
            object.put("is_host", 1);

            object.put("meeting_status", "");
            object.put("meeting_valid_token", UUID.randomUUID().toString());

            object.put("event_repeat_to_date", endDateForPost);


            if (!repeative.equals("One-time event")) {
                object.put("is_long_repeat", 1);
            } else {
                object.put("is_long_repeat", 0);
            }
            object.put("event_alert",mAlertID);
            object.put("calendar_id", mCalendarID);


            object.put("event_last_update_datetime", DateUtil.getDateStringFromCalendarGMT(Calendar.getInstance()));
            object.put("if_deleted", 0);

            object.put("event_is_punctual", punctual);
            object.put("event_is_punctual_new", punctual);

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
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }

}
