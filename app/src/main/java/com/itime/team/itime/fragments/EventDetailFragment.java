package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.utils.DateUtil;

/**
 * Created by leveyleonhardt on 4/23/16.
 */
public class EventDetailFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_detail, container, false);
        EditText name = (EditText) v.findViewById(R.id.event_name);
        EditText venue = (EditText) v.findViewById(R.id.event_venue);
        TextView start_text = (TextView) v.findViewById(R.id.start_text);
        TextView end_text = (TextView) v.findViewById(R.id.end_text);
        TextView type = (TextView) v.findViewById(R.id.rep_text);
        TextView alert = (TextView) v.findViewById(R.id.alert_text);
        TextView calendarType = (TextView) v.findViewById(R.id.calendar_type_text);
        Bundle bundle = getActivity().getIntent().getExtras();
        name.setText(bundle.getString("event_name"));
        venue.setText(bundle.getString("venue"));
        start_text.setText(DateUtil.formatToReadable(bundle.getString("start_time")));
        end_text.setText(DateUtil.formatToReadable(bundle.getString("end_time")));
        type.setText(bundle.getString("repeat_type"));
        alert.setText(bundle.getString("alert"));
        calendarType.setText(bundle.getString("calendar_type"));
        return v;
    }
}
