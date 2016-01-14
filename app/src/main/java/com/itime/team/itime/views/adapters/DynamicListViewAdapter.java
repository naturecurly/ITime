package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.itime.team.itime.activities.R;
import com.itime.team.itime.utils.JsonManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac on 16/1/12.
 */
public class DynamicListViewAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> list;
    private Activity context;
    private Boolean[] checkbox;
    public DynamicListViewAdapter(Activity context, ArrayList<HashMap<String, Object>> listItem){
        list = listItem;
        this.context = context;
        this.checkbox = checkbox;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        JsonManager jsonManager = new JsonManager();
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.fragment_meeting_listview, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.meeting_profile);
        final CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.meeting_invite);
        TextView ID = (TextView) itemView.findViewById(R.id.meeting_id);
        TextView name = (TextView) itemView.findViewById(R.id.meeting_name);


        if(list.get(position).get("url") != null) {
            jsonManager.postForImage(list.get(position).get("url").toString(), imageView, context);
        }
        String textID = (String) list.get(position).get("ItemID");
        ID.setText(textID);
        String textName = (String) list.get(position).get("ItemName");
        name.setText(textName);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    list.get(position).put("CheckBox",true);
                } else {
                    list.get(position).put("CheckBox",false);
                }
            }
        });
        return itemView;
    }
}
