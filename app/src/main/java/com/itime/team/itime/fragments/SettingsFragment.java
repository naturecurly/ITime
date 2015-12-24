package com.itime.team.itime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itime.team.itime.activities.ProfileActivity;
import com.itime.team.itime.activities.R;
import com.itime.team.itime.views.widget.ImageButton_Image_Text;

/**
 * Created by mac on 15/12/19.
 */
public class SettingsFragment extends Fragment{
    private View mSettings;
    private ImageButton_Image_Text mName;
    private ImageButton_Image_Text mPreference;
    private ImageButton_Image_Text mImport;
    private LinearLayout mNameLayout;
    private LinearLayout mPreferenceLayout;
    private LinearLayout mImportLayout;

    private ProfileFragment mProfileFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSettings = inflater.inflate(R.layout.fragment_settings,null);
        init();
        return mSettings;
    }

    private void init(){
        mName = new ImageButton_Image_Text(getActivity(), R.mipmap.ic_launcher, "YY");
        mNameLayout = (LinearLayout) mSettings.findViewById(R.id.setting_name);
        mNameLayout.addView(mName);
        mProfileFragment = new ProfileFragment();
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        mPreference = new ImageButton_Image_Text(getActivity(),R.mipmap.ic_launcher, R.string.settings_preference);
        mPreferenceLayout = (LinearLayout) mSettings.findViewById(R.id.setting_preferences);
        mPreferenceLayout.addView(mPreference);
        mPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Preference","aaa");
            }
        });

        mImport = new ImageButton_Image_Text(getActivity(),R.mipmap.ic_launcher,R.string.settings_import);
        mImportLayout = (LinearLayout) mSettings.findViewById(R.id.setting_import);
        mImportLayout.addView(mImport);
        mImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Import","bbbbb");
            }
        });
    }
}