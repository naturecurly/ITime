package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.itime.team.itime.activities.R;


/**
 * Created by mac on 15/12/18.
 */
public class MeetingDurationDialogFragment extends DialogFragment implements View.OnClickListener {
    private View durationDialog;

    private Button mTenMins;
    private Button mFifteenMins;
    private Button mThirtyMins;
    private Button mOneHour;
    private Button mTwoHours;
    private Button mSixHours;
    private Button duration;

    public MeetingDurationDialogFragment(Button duration) {
        this.duration = duration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        durationDialog = inflater.inflate(R.layout.fragment_meeting_duration_dialog_button, null);
        initVerTwo();
        return durationDialog;
    }

    private void initVerTwo(){
        mTenMins = (Button) durationDialog.findViewById(R.id.meeting_duration_tenmins);
        mFifteenMins = (Button) durationDialog.findViewById(R.id.meeting_duration_fifteenmins);
        mThirtyMins = (Button) durationDialog.findViewById(R.id.meeting_duration_thirtymins);
        mOneHour = (Button) durationDialog.findViewById(R.id.meeting_duration_onehour);
        mTwoHours = (Button) durationDialog.findViewById(R.id.meeting_duration_twohours);
        mSixHours = (Button) durationDialog.findViewById(R.id.meeting_duration_sixhours);
        mTenMins.setOnClickListener(this);
        mFifteenMins.setOnClickListener(this);
        mThirtyMins.setOnClickListener(this);
        mOneHour.setOnClickListener(this);
        mTwoHours.setOnClickListener(this);
        mSixHours.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.meeting_duration_tenmins){
            MeetingFragment.mDuration = 10;
            duration.setText("10 mins");
        }else if(v.getId() == R.id.meeting_duration_fifteenmins){
            MeetingFragment.mDuration = 15;
            duration.setText("15 mins");
        }else if(v.getId() == R.id.meeting_duration_thirtymins){
            MeetingFragment.mDuration = 30;
            duration.setText("30 mins");
        }else if(v.getId() == R.id.meeting_duration_onehour){
            MeetingFragment.mDuration = 60;
            duration.setText("1 hour");
        }else if(v.getId() == R.id.meeting_duration_twohours){
            MeetingFragment.mDuration = 120;
            duration.setText("2 hours");
        }else if(v.getId() == R.id.meeting_duration_sixhours){
            MeetingFragment.mDuration = 360;
            duration.setText("6 hours");
        }
        MeetingDurationDialogFragment.this.dismiss();
    }

}
