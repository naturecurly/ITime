package com.itime.team.itime.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itime.team.itime.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac on 15/12/15.
 */
public class MeetingAddDialogFragment extends DialogFragment {
    private ListView listView;
    private ArrayList<HashMap<String, Object>> listItem;
    private View addDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addDialog = inflater.inflate(R.layout.fragment_meeting_adddialog,container);
        listView = (ListView) addDialog.findViewById(R.id.meeting_add_listview);
        listItem = new ArrayList<HashMap<String, Object>>();
        getDialog().setTitle("Search");
        return addDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.search);
        map.put("ItemText", "Search iTIME friends");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.contacts);
        map.put("ItemText", "Add Contacts");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.qrcode);
        map.put("ItemText", "Scan QR code");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.facebook);
        map.put("ItemText", "Add Facebook Friends");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.wechat);
        map.put("ItemText", "Add WeChat Friends");
        listItem.add(map);

        SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem,
                R.layout.fragment_meeting_adddoalog_listview,
                new String[] {"ItemImage","ItemText"},
                new int[] {R.id.meeting_add_image,R.id.meeting_add_text}
        );
        listView.setAdapter(listItemAdapter);
    }

}
