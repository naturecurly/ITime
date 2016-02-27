/*
 * Copyright (C) 2016 Yorkfine Chan <yorkfinechan@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itime.team.itime.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.activities.LoginActivity;
import com.itime.team.itime.activities.MeetingPreferenceActivity;
import com.itime.team.itime.activities.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.UserTableHelper;

/**
 * Created by Xuhui Chen (yorkfine) on 10/01/16.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final int SETTINGS_PROFILE_ID = R.id.setting_profile;
    private static final int SETTINGS_MEETING_ID = R.id.setting_meeting;
    private static final int SETTINGS_IMPORT_ID = R.id.setting_import;
    private static final int SETTINGS_ALERT_TIME_ID = R.id.setting_dft_alert_time;
    private static final int SETTING_LOGOUT_ID = R.id.settings_btn_logout;

    private static final String SETTINGS = "Settings";
    private static final int PROFILE_SETTINGS = 1;
    private static final int MEETING_SETTINGS = 2;
    private static final int IMPORT_SETTINGS = 3;
    private static final int ALERT_TIME_SETTINGS = 4;

    View mAlertTimeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        View v1 = view.findViewById(SETTINGS_PROFILE_ID);
        v1.setOnClickListener(this);
        View v2 = view.findViewById(SETTINGS_MEETING_ID);
        v2.setOnClickListener(this);
        View v3 = view.findViewById(SETTINGS_IMPORT_ID);
        v3.setOnClickListener(this);
        mAlertTimeView = view.findViewById(SETTINGS_ALERT_TIME_ID);
        mAlertTimeView.setOnClickListener(this);
        View v4 = view.findViewById(SETTING_LOGOUT_ID);
        v4.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);

        switch (v.getId()) {
            case SETTINGS_PROFILE_ID:
                intent.putExtra(SETTINGS, PROFILE_SETTINGS);
                startActivity(intent);
                break;

            case SETTINGS_MEETING_ID:
                Intent intent1 = new Intent(getActivity(), MeetingPreferenceActivity.class);
                intent1.putExtra(SETTINGS, MEETING_SETTINGS);
                startActivity(intent1);
                break;


            case SETTINGS_IMPORT_ID:
                intent.putExtra(SETTINGS, IMPORT_SETTINGS);
                startActivity(intent);
                break;

            case SETTINGS_ALERT_TIME_ID:
                intent.putExtra(SETTINGS, ALERT_TIME_SETTINGS);
                startActivityForResult(intent, ALERT_TIME_SETTINGS);

            case SETTING_LOGOUT_ID:
                getActivity().finish();
                if(User.isRemembered) {
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    intent2.putExtra("username", User.ID);
                    updateUserTable();
                    startActivity(intent2);
                }
                break;

            default:
                break;
        }
    }

    /*
        if a user logout, the person's account will not be remembered.
     */
    private void updateUserTable(){
        UserTableHelper dbHelper = new UserTableHelper(getContext(), "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("remember", false);
        db.update("itime_user", values, "id=?", new String[]{"1"});
        dbHelper.close();
        db.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ALERT_TIME_SETTINGS) {
            String text = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("default_alert_time", "10mins");
            TextView textView = (TextView) mAlertTimeView.findViewById(R.id.setting_dft_alert_time_text);
            textView.setText(text);
        }
    }
}
