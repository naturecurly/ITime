package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.itime.team.itime.R;


/**
 * Created by Weiwei Cai on 15/12/18.
 * Setting meeting duration.
 */
public class MeetingDurationDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {
    private View durationDialog;
    private RadioGroup mTime;
    private RadioButton mTenMins, mThirtyMins, mFifteenMins, mOneHour, mTwoHours, mSixHours;

    private Button duration;

    public MeetingDurationDialogFragment(Button duration) {
        this.duration = duration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        durationDialog = inflater.inflate(R.layout.fragment_meeting_duration_dialog_button, null);
        mTime = (RadioGroup) durationDialog.findViewById(R.id.meeting_duration_parent);
        mTenMins = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_tenmins);
        mFifteenMins = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_fifteenmins);
        mThirtyMins = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_thirtymins);
        mOneHour = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_onehour);
        mTwoHours = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_twohours);
        mSixHours = (RadioButton) durationDialog.findViewById(R.id.meeting_duration_sixhours);
        init();
        //getDialog().setTitle(R.string.meeting_duraiton_dialog_title);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mTime.setOnCheckedChangeListener(this);
        return durationDialog;
    }


    private void init(){
        if (MeetingFragment.mDuration == 10){
            mTenMins.setChecked(true);
        } else if(MeetingFragment.mDuration == 15){
            mFifteenMins.setChecked(true);
        } else if(MeetingFragment.mDuration == 30){
            mThirtyMins.setChecked(true);
        } else if(MeetingFragment.mDuration == 60){
            mOneHour.setChecked(true);
        } else if(MeetingFragment.mDuration == 120){
            mTwoHours.setChecked(true);
        } else if(MeetingFragment.mDuration == 360){
            mSixHours.setChecked(true);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == mTenMins.getId()){
            MeetingFragment.mDuration = 10;
            duration.setText("10 mins");
        } else if(checkedId == mFifteenMins.getId()){
            MeetingFragment.mDuration = 15;
            duration.setText("15 mins");
        } else if(checkedId == mThirtyMins.getId()){
            MeetingFragment.mDuration = 30;
            duration.setText("30 mins");
        } else if(checkedId == mOneHour.getId()){
            MeetingFragment.mDuration = 60;
            duration.setText("1 hour");
        } else if(checkedId == mTwoHours.getId()){
            MeetingFragment.mDuration = 120;
            duration.setText("2 hours");
        } else if(checkedId == mSixHours.getId()){
            MeetingFragment.mDuration = 360;
            duration.setText("6 hours");
        }
        MeetingDurationDialogFragment.this.dismiss();
    }
}
