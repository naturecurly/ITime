package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.itime.team.itime.activities.R;

import java.util.ArrayList;

/**
 * Created by mac on 16/2/17.
 */
public class NewMeetingRepeatDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {
    private View mView;
    private RadioGroup mRadioGroup;
    private RadioButton mOneTime,mDaily,mWeekend,mWeekly,mBiWeekly,mMonthly,mYearly;
    private Button mRepeat;
    private ArrayList<Integer> mRepeatValue;

    public NewMeetingRepeatDialogFragment(Button mRepeat, ArrayList<Integer> mRepeatValue){
        this.mRepeat = mRepeat;
        this.mRepeatValue = mRepeatValue;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_meeting_repeat_dialog, null);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.new_meeting_repeat_group);
        mOneTime = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_onetime);
        mDaily = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_daily);
        mWeekend = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_weekend);
        mWeekly = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_weekly);
        mBiWeekly = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_biweekly);
        mMonthly = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_monthly);
        mYearly = (RadioButton) mView.findViewById(R.id.new_meeting_repeat_yearly);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRadioButton();
        mRadioGroup.setOnCheckedChangeListener(this);
        return mView;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == mOneTime.getId()){
            mRepeatValue.clear();
            mRepeat.setText("One-time event");
            mRepeatValue.add(0);
        }else if(checkedId == mDaily.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Daily");
            mRepeatValue.add(1);
        }else if(checkedId == mWeekend.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Every weekend");
            mRepeatValue.add(2);
        }else if(checkedId == mWeekly.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Weekly(Every Sat)");
            mRepeatValue.add(3);
        }else if(checkedId == mBiWeekly.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Bi-Weekly");
            mRepeatValue.add(4);
        }else if(checkedId == mMonthly.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Monthly");
            mRepeatValue.add(5);
        }else if(checkedId == mYearly.getId()){
            mRepeatValue.clear();
            mRepeat.setText("Yearly");
            mRepeatValue.add(6);
        }

        NewMeetingRepeatDialogFragment.this.dismiss();
    }

    private void setRadioButton(){
        if(mRepeatValue.get(0) == 0){
            mOneTime.setChecked(true);
        }else if(mRepeatValue.get(0) == 1){
            mDaily.setChecked(true);
        }else if(mRepeatValue.get(0) == 2){
            mWeekend.setChecked(true);
        }else if(mRepeatValue.get(0) == 3){
            mWeekly.setChecked(true);
        }else if(mRepeatValue.get(0) == 4){
            mBiWeekly.setChecked(true);
        }else if(mRepeatValue.get(0) == 5){
            mMonthly.setChecked(true);
        }else if(mRepeatValue.get(0) == 6){
            mYearly.setChecked(true);
        }
    }
}
