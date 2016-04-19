package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.utils.JsonManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Weiwei Cai on 16/2/2.
 * The adapter displays all users who are searched.
 */
public class SearchFriendListViewAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, Object>> list;
    private Activity context;

    public SearchFriendListViewAdapter(Activity context, ArrayList<HashMap<String, Object>> listItem){
        this.list = listItem;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        JsonManager jsonManager = new JsonManager();
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.search_friend_listview, null);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.search_friend_profile);
        TextView id = (TextView) itemView.findViewById(R.id.search_friend_id);
        TextView name = (TextView) itemView.findViewById(R.id.search_friend_name);
        if(list.get(position).get("url") != null) {
            jsonManager.postForImage(list.get(position).get("url").toString(), imageView, context);
        }
        String textID = (String) list.get(position).get("ItemID");
        id.setText(textID);
        String textName = (String) list.get(position).get("ItemName");
        name.setText(textName);
        return itemView;
    }
}
