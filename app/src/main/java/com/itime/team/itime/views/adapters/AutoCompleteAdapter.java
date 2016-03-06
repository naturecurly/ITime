package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itime.team.itime.R;

import java.util.ArrayList;

/**
 * Created by mac on 16/3/6.
 */
public class AutoCompleteAdapter extends BaseAdapter {
    private ArrayList<String> mList;
    private Activity mContext;

    public AutoCompleteAdapter(Activity context, ArrayList<String> list){
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.auto_complete_listview, null);
        TextView textView = (TextView) view.findViewById(R.id.auto_complete_listview_item);
        textView.setText(mList.get(position));
        return view;
    }
}
