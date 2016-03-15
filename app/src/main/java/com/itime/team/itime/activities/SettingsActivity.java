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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.AlertTimePreferenceFragment;
import com.itime.team.itime.fragments.InputDialogFragment;
import com.itime.team.itime.fragments.MeetingPreferenceFragment;
import com.itime.team.itime.fragments.MeetingSubPreferenceFragment;
import com.itime.team.itime.fragments.ProfileFragment;
import com.itime.team.itime.fragments.RepeatPreferenceFragment;

/**
 * Created by Xuhui Chen (yorkfine) on 12/01/16.
 */
// TODO: Refactor to DetaiedSettingActivity
public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    private static final String SETTINGS = "Settings";
    private static final String SETTINGS_DATA = "Settings_data";

    private static final int PROFILE_SETTINGS = 1;
    private static final int MEETING_SETTINGS = 2;
    private static final int IMPORT_SETTINGS = 3;
    private static final int ALERT_TIME_SETTINGS = 4;
    private static final int MEETING_SUB_SETTINGS = 5;

    private static Bundle fragmentBundle = new Bundle();


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
                    Fragment pf = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, pf)
                            .commit();
                    //getSupportFragmentManager().putFragment(fragmentBundle, "ProfileFragment", pf);
                    break;

                case MEETING_SETTINGS:
                    MeetingPreferenceFragment mpf = new MeetingPreferenceFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, mpf)
                            .commit();
                    //getSupportFragmentManager().putFragment(fragmentBundle, "MeetingPrefrenceFragment", mpf);
                    break;

                case IMPORT_SETTINGS:
                    break;

                case ALERT_TIME_SETTINGS:
                    AlertTimePreferenceFragment apf = new AlertTimePreferenceFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, apf)
                            .commit();
                    //getSupportFragmentManager().putFragment(fragmentBundle, "AlertTimePreferenceFragment", apf);
                    break;

                // TODO: 15/03/16 refactor. Not put it here
                case 6:
                    RepeatPreferenceFragment rpf = new RepeatPreferenceFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, rpf)
                            .commit();
                    break;

                case MEETING_SUB_SETTINGS:
                    MeetingSubPreferenceFragment mspf = new MeetingSubPreferenceFragment();
                    String data = getIntent().getStringExtra(SETTINGS_DATA);
                    if (data != null) {
                        Bundle args = new Bundle();
                        args.putString(MeetingSubPreferenceFragment.PREFERENCE_DATA, data);
                        mspf.setArguments(args);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, mspf)
                            .commit();
                    //getSupportFragmentManager().putFragment(fragmentBundle, "MeetingSubPreferenceFragment", mspf);
                    break;
                default:
                    Log.e(LOG_TAG, "Unknows setting item");
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.meeting_preference, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
