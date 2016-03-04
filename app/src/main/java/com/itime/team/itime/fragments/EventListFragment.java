package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.itime.team.itime.R;

/**
 * Created by leveyleonhardt on 3/3/16.
 */
public class EventListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list,container,false);
        ImageButton imageButton = (ImageButton) getActivity().findViewById(R.id.event_list);
        imageButton.setVisibility(View.INVISIBLE);
        return v;
    }
}
