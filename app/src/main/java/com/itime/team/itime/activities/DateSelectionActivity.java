package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itime.team.itime.fragments.MeetingSelectionCentralFragment;
import com.itime.team.itime.fragments.MeetingSelectionTopFragment;
import com.itime.team.itime.utils.DateUtil;

/**
 * Created by mac on 16/1/4.
 */
public class DateSelectionActivity extends AppCompatActivity {
    private MeetingSelectionCentralFragment centralFragment;
    private MeetingSelectionTopFragment topFragment;

    private int STARTYEAR;
    private int STARTMONTH;
    private int STARTDAY;

    private int ENDYEAR;
    private int ENDMONTH;
    private int ENDDAY;

    public int DURATION = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meetingselection);
        initParameters();
        init();
    }

    private void init(){
        centralFragment =  new MeetingSelectionCentralFragment();
        Bundle centralMSG = new Bundle();
        centralMSG.putInt("totaldays",
                DateUtil.diffDate(STARTYEAR, STARTMONTH, STARTDAY, ENDYEAR, ENDMONTH, ENDDAY));
        centralMSG.putInt("duration",DURATION);
        centralFragment.setArguments(centralMSG);
        topFragment = new MeetingSelectionTopFragment();
        Bundle topMSG = new Bundle();
        topMSG.putInt("startyear",STARTYEAR);
        topMSG.putInt("startmonth",STARTMONTH);
        topMSG.putInt("startday",STARTDAY);
        topMSG.putInt("endyear",ENDYEAR);
        topMSG.putInt("endmonth", ENDMONTH);
        topMSG.putInt("endday",ENDDAY);
        topFragment.setArguments(topMSG);
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

        ENDYEAR = intent.getIntExtra("endyear",0);
        ENDMONTH = intent.getIntExtra("endmonth",0);
        ENDDAY = intent.getIntExtra("endday",0);

        DURATION = intent.getIntExtra("duration",60);
    }
}
