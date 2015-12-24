package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itime.team.itime.activities.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This Fragment is not used currently.
 */
public class MeetingDurationFragment extends Fragment {
    private View view;
    private ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meeting_duration,null);
        listView = (ListView) view.findViewById(R.id.meeting_duration_listview);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_expandable_list_item_1,initListview()));
        return view;
    }

    private List<String> initListview(){

        List<String> data = new ArrayList<String>();
        data.add("10mins");
        data.add("15mins");
        data.add("30mins");
        data.add("1hr");
        data.add("2hrs");
        data.add("6hrs");
        return data;
    }
}
