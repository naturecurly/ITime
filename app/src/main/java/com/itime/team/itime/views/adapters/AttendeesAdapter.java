package com.itime.team.itime.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itime.team.itime.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac on 16/4/12.
 */
public class AttendeesAdapter extends BaseAdapter {
    private ArrayList<HashMap<String,String>> mItems;
    private Context mContext;
    private LayoutInflater inflater;
    private ImageView mImage;
    private TextView mName;
    private TextView mId;

    public AttendeesAdapter(Context mContext, ArrayList<HashMap<String,String>> mItems){
        this.mContext = mContext;
        this.mItems = mItems;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String,String> map = mItems.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.meeting_detail_listview, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.meeting_attendee_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.meeting_attendee_name);
            viewHolder.id = (TextView) convertView.findViewById(R.id.meeting_attendee_id);
            viewHolder.status = (TextView) convertView.findViewById(R.id.meeting_attendee_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.image.setImageResource(R.drawable.default_profile_image);
        viewHolder.name.setText(map.get("name"));
        viewHolder.id.setText(map.get("id"));
        viewHolder.status.setText(map.get("status"));
        if(!Boolean.valueOf(map.get("isFriend"))){
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
        }
        return convertView;
    }

    private class ViewHolder{
        TextView name;
        TextView id;
        TextView status;
        ImageView image;
    }
}
