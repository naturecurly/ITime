package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.TopAndCenterMeetingFragmentScrollViews;
import com.itime.team.itime.fragments.MeetingSelectionCentralFragment;
import com.itime.team.itime.fragments.MeetingSelectionTopFragment;
import com.itime.team.itime.utils.DateUtil;

import java.util.ArrayList;

/**
 * Created by Weiwei Cai on 16/1/4.
 *
 * This Activity handles the UI that users can find friends' available time and choose a time
 * to hold a meeting.
 */
public class DateSelectionActivity extends AppCompatActivity {
    private MeetingSelectionCentralFragment centralFragment;
    private MeetingSelectionTopFragment topFragment;


    private int STARTYEAR;
    private int STARTMONTH;
    private int STARTDAY;
    private int STARTHOUR;
    private int STARTMIN;

    private int ENDYEAR;
    private int ENDMONTH;
    private int ENDDAY;
    private int ENDHOUR;
    private int ENDMIN;
    // To store all friends' IDs who are invited in the meeting.
    private ArrayList<String> mFriendIDs;

    public int DURATION = 60;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetingselection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.meeting_selection_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.meeting_select_title);
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

        initParameters();
        init();

    }

    private void init(){

        TopAndCenterMeetingFragmentScrollViews scrollViews = new TopAndCenterMeetingFragmentScrollViews();

        centralFragment =  new MeetingSelectionCentralFragment(scrollViews);

        Bundle centralMSG = new Bundle();
        centralMSG.putInt("totaldays",
                DateUtil.diffDate(STARTYEAR, STARTMONTH, STARTDAY, ENDYEAR, ENDMONTH, ENDDAY));
        centralMSG.putInt("duration", DURATION);
        centralMSG.putStringArrayList("friendIDs", mFriendIDs);
        centralMSG.putString("startdate", DateUtil.getDateWithTimeZone(STARTYEAR, STARTMONTH, STARTDAY, STARTHOUR, STARTMIN));
        centralMSG.putString("enddate", DateUtil.getDateWithTimeZone(ENDYEAR, ENDMONTH, ENDDAY, ENDHOUR, ENDMIN));
        centralMSG.putInt("startyear", STARTYEAR);
        centralMSG.putInt("startmonth", STARTMONTH);
        centralMSG.putInt("startday", STARTDAY);
        centralFragment.setArguments(centralMSG);

        topFragment = new MeetingSelectionTopFragment(scrollViews);


        Bundle topMSG = new Bundle();
        topMSG.putInt("startyear",STARTYEAR);
        topMSG.putInt("startmonth",STARTMONTH);
        topMSG.putInt("startday",STARTDAY);
        topMSG.putInt("endyear",ENDYEAR);
        topMSG.putInt("endmonth", ENDMONTH);
        topMSG.putInt("endday", ENDDAY);
        topFragment.setArguments(topMSG);

        // The activity is divided into two fragments.
        getFragmentManager().beginTransaction().add(R.id.meeting_selection_top,
                topFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.meeting_selection_scroll,
                centralFragment).commit();
    }

    private void initParameters(){
        Intent intent = getIntent();
        STARTYEAR = intent.getIntExtra("startyear",0);
        STARTMONTH = intent.getIntExtra("startmonth",0);
        STARTDAY = intent.getIntExtra("startday",0);
        STARTHOUR = intent.getIntExtra("starthour",0);
        STARTMIN = intent.getIntExtra("startmin",0);

        ENDYEAR = intent.getIntExtra("endyear",0);
        ENDMONTH = intent.getIntExtra("endmonth",0);
        ENDDAY = intent.getIntExtra("endday",0);
        ENDHOUR = intent.getIntExtra("endhour",0);
        ENDMIN = intent.getIntExtra("endmin",0);

        DURATION = intent.getIntExtra("duration",60);
        mFriendIDs = intent.getStringArrayListExtra("friendIDs");
    }

    // If a meeting request has been sent, then close this activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                finish();
            }
        }
    }
}
