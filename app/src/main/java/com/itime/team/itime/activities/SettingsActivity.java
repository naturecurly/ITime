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
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.AlertTimePreferenceFragment;
import com.itime.team.itime.fragments.CalendarTypeFragment;
import com.itime.team.itime.fragments.CalendarTypeSubFragment;
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

    public static final String SETTINGS = "Settings";
    public static final String SETTINGS_DATA = "Settings_data";
    public static final String PROFILE_USER_DATA = "profile_user_data"; // currently data is user id

    public static final int PROFILE_SETTINGS = 1;
    public static final int MEETING_SETTINGS = 2;
    public static final int IMPORT_SETTINGS = 3;
    public static final int ALERT_TIME_SETTINGS = 4;
    public static final int MEETING_SUB_SETTINGS = 5;
    public static final int CALENDAR_TYPE_SETTINGS = 7;
    public static final int CLEAR_CALENDAR_SETTINGS = 8;
    public static final int CALENDAR_TYPE_SUB_SETTINGS = CalendarTypeFragment.CALENDAR_TYPE_SUB_SETTINGS;

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
                    if (getIntent().getExtras().getString(PROFILE_USER_DATA) != null) {
                        String userId = getIntent().getExtras().getString(PROFILE_USER_DATA);
                        Log.i(LOG_TAG, userId);
                        Bundle argument = new Bundle();
                        argument.putString(ProfileFragment.USER_ID, userId);
                        pf.setArguments(argument);
                    }
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

                case CALENDAR_TYPE_SETTINGS: {

                    CalendarTypeFragment ctf = new CalendarTypeFragment();
                    Bundle args = new Bundle();
                    args.putInt(CalendarTypeFragment.CALENDAR_TYPE_ACTION_TYPE, CalendarTypeFragment.ADD_CALENDAR_TYPE);
                    ctf.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, ctf)
                            .commit();
                    break;
                }

                case CLEAR_CALENDAR_SETTINGS: {
                    CalendarTypeFragment ctf = new CalendarTypeFragment();
                    Bundle args = new Bundle();
                    args.putInt(CalendarTypeFragment.CALENDAR_TYPE_ACTION_TYPE, CalendarTypeFragment.CLEAR_CALENDAR_TYPE);
                    ctf.setArguments(args);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, ctf)
                            .commit();
                    break;
                }

                case MEETING_SUB_SETTINGS:
                    MeetingSubPreferenceFragment mspf = new MeetingSubPreferenceFragment();
                    Parcelable data = getIntent().getParcelableExtra(SETTINGS_DATA);
                    if (data != null) {
                        Bundle args = new Bundle();
                        args.putParcelable(MeetingSubPreferenceFragment.PREFERENCE_DATA, data);
                        mspf.setArguments(args);
                    }
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, mspf)
                            .commit();
                    //getSupportFragmentManager().putFragment(fragmentBundle, "MeetingSubPreferenceFragment", mspf);
                    break;

                case CALENDAR_TYPE_SUB_SETTINGS:
                    CalendarTypeSubFragment ctsf = new CalendarTypeSubFragment();
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.setting_content, ctsf)
                            .commit();
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
