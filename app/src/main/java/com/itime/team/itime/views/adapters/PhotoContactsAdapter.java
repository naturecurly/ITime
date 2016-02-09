package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itime.team.itime.activities.R;
import com.itime.team.itime.bean.Contact;

import java.util.ArrayList;

/**
 * Created by mac on 16/2/6.
 */
public class PhotoContactsAdapter extends BaseAdapter {
    private ArrayList<Contact> mContact;
    private Activity mContext;
    private Boolean[] mIsChecked;

    public PhotoContactsAdapter(Activity mContext, ArrayList<Contact> contact, Boolean[] isChecked){
        this.mContact = contact;
        this.mContext = mContext;
        this.mIsChecked = isChecked;

    }

    public void invert(){
        for(int i = 0; i < mIsChecked.length; i ++){
            if(mIsChecked[i]){
                mIsChecked[i] = false;
            }else{
                mIsChecked[i] = true;
            }
        }
    }

    private void setCheckBox(ViewHolder viewHolder, int position){
        for(int i = 0; i < mIsChecked.length; i ++){
            viewHolder.checkBox.setChecked(mIsChecked[position]);
        }
    }

    @Override
    public int getCount() {
        return mContact.size();
    }

    @Override
    public Object getItem(int position) {
        return mContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_phone_contact_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.phone_contact_name);
            viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_contact_phone);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.phone_contact_check);
            viewHolder.name.setText(mContact.get(position).getName());
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String phoneNumber = mContact.get(position).getPhotoNumber().size() == 0 ? "" :
                mContact.get(position).getPhotoNumber().get(0);
        viewHolder.phoneNumber.setText(phoneNumber);
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsChecked[position] = true;
                } else {
                    mIsChecked[position] = false;
                }
            }
        });
        setCheckBox(viewHolder,position);
        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView phoneNumber;
        CheckBox checkBox;
    }
}
