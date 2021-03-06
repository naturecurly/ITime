package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 16/2/6.
 * This adapter is not used right now.
 */
public class PhotoContactsAdapter extends BaseAdapter {
    private ArrayList<Contact> mContact;
    private Activity mContext;
    private Boolean[] mIsChecked;
    private LayoutInflater inflater;
    Map<Integer, Boolean> isCheckMap;

    public PhotoContactsAdapter(Activity mContext, ArrayList<Contact> contact, Boolean[] isChecked){
        this.mContact = contact;
        this.mContext = mContext;
        this.mIsChecked = isChecked;
        inflater = LayoutInflater.from(mContext);
        isCheckMap =  new HashMap<Integer, Boolean>();

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

/*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        final View itemView = inflater.inflate(R.layout.activity_phone_contact_listview, null);
        final int index = position;
        ViewHolder viewHolder;

        //itemView = LayoutInflater.from(mContext).inflate(R.layout.activity_phone_contact_listview, null);
        viewHolder = new ViewHolder();
        viewHolder.name = (TextView) itemView.findViewById(R.id.phone_contact_name);
        viewHolder.phoneNumber = (TextView) itemView.findViewById(R.id.phone_contact_phone);
        viewHolder.checkBox = (CheckBox) itemView.findViewById(R.id.phone_contact_check);

        itemView.setTag(viewHolder);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsChecked[index] = true;
                } else {
                    mIsChecked[index] = false;
                }
            }
        });
        String phoneNumber = mContact.get(index).getPhotoNumber().size() == 0 ? "" :
                mContact.get(index).getPhotoNumber().get(0);
        viewHolder.phoneNumber.setText(phoneNumber);
        setCheckBox(viewHolder,index);
        viewHolder.name.setText(mContact.get(position).getName());
        return itemView;
    }

*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int index = position;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_phone_contact_listview, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.phone_contact_name);
            viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phone_contact_phone);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.phone_contact_check);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsChecked[index] = true;
                } else {
                    mIsChecked[index] = false;
                }
            }
        });
        String phoneNumber = mContact.get(position).getPhotoNumber().size() == 0 ? "" :
                mContact.get(position).getPhotoNumber().get(0);
        viewHolder.phoneNumber.setText(phoneNumber);
        setCheckBox(viewHolder, index);
        viewHolder.name.setText(mContact.get(position).getName());


        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView phoneNumber;
        CheckBox checkBox;
    }
}
