package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.itime.team.itime.R;

/**
 * Created by mac on 15/12/19.
 */
public class SettingsFragment extends Fragment {
    private View mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSettings = inflater.inflate(R.layout.fragment_settings,null);
        ImageView b = (ImageView) mSettings.findViewById(R.id.setting_name);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return mSettings;
    }
}