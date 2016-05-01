package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itime.team.itime.R;

/**
 * Created by leveyleonhardt on 5/1/16.
 */
public class EventDetailEditFragment extends Fragment {
    private static final int DIALOG_FRAGMENT = 1;
    private EditText name;
    private EditText venue;
    private TextView start_text;
    private TextView end_text;
    private TextView type;
    private TextView alert;
    private TextView calendarType;
    private Button delete_button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_events_detail, container, false);
        name = (EditText) v.findViewById(R.id.event_name);
        venue = (EditText) v.findViewById(R.id.event_venue);
        start_text = (TextView) v.findViewById(R.id.start_text);
        end_text = (TextView) v.findViewById(R.id.end_text);
        type = (TextView) v.findViewById(R.id.rep_text);
        alert = (TextView) v.findViewById(R.id.alert_text);
        calendarType = (TextView) v.findViewById(R.id.calendar_type_text);
        delete_button = (Button) v.findViewById(R.id.event_detail_delete_button);
        delete_button.setVisibility(View.GONE);
        return v;
    }
}
