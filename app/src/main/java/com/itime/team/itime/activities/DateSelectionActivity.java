package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itime.team.itime.fragments.MeetingSelectionCentralFragment;
import com.itime.team.itime.fragments.MeetingSelectionTopFragment;
import com.itime.team.itime.utils.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mac on 16/1/4.
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
    private ArrayList<String> mFriendIDs;

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
        centralMSG.putInt("duration", DURATION);
        centralMSG.putStringArrayList("friendIDs", mFriendIDs);
        centralMSG.putString("startdate",getDateWithTimeZone(STARTYEAR, STARTMONTH, STARTDAY, STARTHOUR, STARTMIN));
        centralMSG.putString("enddate",getDateWithTimeZone(ENDYEAR, ENDMONTH, ENDDAY, ENDHOUR, ENDMIN));
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

    private String getDateWithTimeZone(int year, int month, int day, int hour, int min){
        Date date = new Date();
        String timeZone = "+" + date.toString().split(" ")[4].split(":")[0].split("\\+")[1] +
                 date.toString().split(" ")[4].split(":")[1];
        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + min;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dataForReturn = year + "-" + month + "-" + day + " " + date.toString().split(" ")[3]
                + " " + timeZone;
        return dataForReturn;
    }
}