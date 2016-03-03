package com.itime.team.itime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Contact;

import java.util.ArrayList;

/**
 * Created by mac on 16/2/13.
 */
public class PhoneContactAddWayDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener{
    private View mParent;
    private Boolean[] mIsChecked;
    private StringBuffer mPhoneNumbers,mFriendEmail;
    private ArrayList<Contact> mContact;
    private RadioGroup mRadioGroup;
    private RadioButton mMessage,mEmail;

    public PhoneContactAddWayDialogFragment(Boolean[] mIsChecked, ArrayList<Contact> mContact){
        this.mIsChecked = mIsChecked;
        this.mContact = mContact;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mParent = inflater.inflate(R.layout.phone_contact_addway_dialog,container);
        mRadioGroup = (RadioGroup) mParent.findViewById(R.id.phone_contact_way);
        mMessage = (RadioButton) mParent.findViewById(R.id.phone_contact_message);
        mEmail = (RadioButton) mParent.findViewById(R.id.phone_contact_email);
        mRadioGroup.setOnCheckedChangeListener(this);
        init();
        return mParent;
    }

    private void init(){
        mPhoneNumbers = new StringBuffer();
        mFriendEmail = new StringBuffer();
    }

    private void addFriends(){
        String phone = "";
        String email = "";
        mPhoneNumbers.delete(0,mPhoneNumbers.length());
        mFriendEmail.delete(0, mFriendEmail.length());
        for(int i = 0; i < mIsChecked.length; i ++){
            if(mIsChecked[i]){
                phone = mContact.get(i).getPhotoNumber().size() == 0 ? "" :
                        mContact.get(i).getPhotoNumber().get(0);
                mPhoneNumbers.append(phone).append(";");
                email = mContact.get(i).geteMail() == null ? "" : mContact.get(i).geteMail();
                mFriendEmail.append(email).append(";");
            }
        }
    }

    private void sendMSG(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", mPhoneNumbers.toString());
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("sms_body", "happy new year");
        startActivity(intent);
    }

    private void sendEmail(){
        String[] reciver = mFriendEmail.toString().split(";");
        String mySbuject = "Add Friend";
        String myCc = "cc";
        String mybody = "Happy New Year";
        Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
        myIntent.setType("plain/text");
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
        startActivity(Intent.createChooser(myIntent, "mail"));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == mMessage.getId()){
            addFriends();
            sendMSG();
        }else if(checkedId == mEmail.getId()){
            addFriends();
            sendEmail();
        }
        PhoneContactAddWayDialogFragment.this.dismiss();
    }
}
