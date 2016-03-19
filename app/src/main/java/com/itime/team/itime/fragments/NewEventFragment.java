package com.itime.team.itime.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.new_event_fragment, container, false);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_event_menu_send:
                Toast.makeText(getContext(), "hello, it's me", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
