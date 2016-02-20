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
public class NewMeetingAlertDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener {
    private View mView;
    private RadioGroup mRadioGroup;
    private RadioButton mNone, mDeparture, mFive, mTen, mFifteen, mThirty, mSixty;
    private Button mAlert;
    private ArrayList<Integer> mAlertValue;
    private String mText;

    public NewMeetingAlertDialogFragment(Button mAlert, ArrayList<Integer> mAlertValue){
        this.mAlert = mAlert;
        this.mAlertValue = mAlertValue;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_new_meeting_alert_dialog, null);
        mRadioGroup = (RadioGroup) mView.findViewById(R.id.new_meeting_alert_group);
        mNone = (RadioButton) mView.findViewById(R.id.new_meeting_alert_none);
        mDeparture = (RadioButton) mView.findViewById(R.id.new_meeting_alert_departure);
        mFive = (RadioButton) mView.findViewById(R.id.new_meeting_alert_five);
        mTen = (RadioButton) mView.findViewById(R.id.new_meeting_alert_ten);
        mFifteen = (RadioButton) mView.findViewById(R.id.new_meeting_alert_fifteen);
        mThirty = (RadioButton) mView.findViewById(R.id.new_meeting_alert_thirty);
        mSixty = (RadioButton) mView.findViewById(R.id.new_meeting_alert_sixty);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRadioButton();
        mRadioGroup.setOnCheckedChangeListener(this);
        mText = getResources().getString(R.string.new_meeting_alert_button) + " ";
        return mView;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == mNone.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mNone.getText());
            mAlertValue.add(0);
        }else if(checkedId == mDeparture.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mDeparture.getText());
            mAlertValue.add(1);
        }else if(checkedId == mFive.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mFive.getText());
            mAlertValue.add(2);
        }else if(checkedId == mTen.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mTen.getText());
            mAlertValue.add(3);
        }else if(checkedId == mFifteen.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mFifteen.getText());
            mAlertValue.add(4);
        }else if(checkedId == mThirty.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mThirty.getText());
            mAlertValue.add(5);
        }else if(checkedId == mSixty.getId()){
            mAlertValue.clear();
            mAlert.setText(mText + mSixty.getText());
            mAlertValue.add(6);
        }

        NewMeetingAlertDialogFragment.this.dismiss();
    }

    private void setRadioButton(){
        if(mAlertValue.get(0) == 0){
            mNone.setChecked(true);
        }else if(mAlertValue.get(0) == 1){
            mDeparture.setChecked(true);
        }else if(mAlertValue.get(0) == 2){
            mFive.setChecked(true);
        }else if(mAlertValue.get(0) == 3){
            mTen.setChecked(true);
        }else if(mAlertValue.get(0) == 4){
            mFifteen.setChecked(true);
        }else if(mAlertValue.get(0) == 5){
            mThirty.setChecked(true);
        }else if(mAlertValue.get(0) == 6){
            mSixty.setChecked(true);
        }
    }
}
