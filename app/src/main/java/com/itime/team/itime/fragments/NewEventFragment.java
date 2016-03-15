package com.itime.team.itime.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.itime.team.itime.R;

/**
 * Created by mac on 16/3/15.
 */
public class NewEventFragment extends Fragment {
    private EditText mMessage;
    private Button mStartDate, mStartTime, mEndDate, mEndTime;
    private Button mRepeat;
    private CheckBox mPunctual;
    private Button mAlert;
    private EditText mName, mVeune;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.new_event_fragment, container, false);

        return rootView;
    }
}
