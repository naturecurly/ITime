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

package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.AlertTimePreferenceFragment;
import com.itime.team.itime.fragments.MeetingPreferenceFragment;
import com.itime.team.itime.fragments.MeetingSubPreferenceFragment;
import com.itime.team.itime.fragments.ProfileFragment;

/**
 * Created by Xuhui Chen (yorkfine) on 12/01/16.
 */
// TODO: Refactor to DetaiedSettingActivity
public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    private static final String SETTINGS = "Settings";
    private static final int PROFILE_SETTINGS = 1;
    private static final int MEETING_SETTINGS = 2;
    private static final int IMPORT_SETTINGS = 3;
    private static final int ALERT_TIME_SETTINGS = 4;
    private static final int MEETING_SUB_SETTINGS = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null) {
            int settings = getIntent().getIntExtra(SETTINGS, PROFILE_SETTINGS);
            switch (settings) {
                case PROFILE_SETTINGS:
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, new ProfileFragment())
                            .commit();
                    break;

                case MEETING_SETTINGS:
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, new MeetingPreferenceFragment())
                            .commit();
                    break;

                case IMPORT_SETTINGS:
                    break;

                case ALERT_TIME_SETTINGS:
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, new AlertTimePreferenceFragment())
                            .commit();
                    break;

                case MEETING_SUB_SETTINGS:
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, new MeetingSubPreferenceFragment())
                            .commit();
                    break;
                default:
                    Log.e(LOG_TAG, "Unknows setting item");
            }

        }
    }
}
