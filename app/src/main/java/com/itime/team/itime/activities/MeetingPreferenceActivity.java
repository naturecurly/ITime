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

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.itime.team.itime.activities.support.AppCompatPreferenceActivity;
import com.itime.team.itime.fragments.MeetingSubPreferenceFragment;

import java.util.Formatter;
import java.util.List;
import java.util.Set;

/**
 * Created by Xuhui Chen (yorkfine) on 28/01/16.
 *
 * PreferenceActivity with Toolbar from v7 support:
 * <a href="http://stackoverflow.com/a/30140171/5654852">How to add ToolBar in PreferenceActivity?
 * </a> for official example usage.
 * <a href="http://stackoverflow.com/a/27422401/5654852">Creating a Preference Screen with support
 * (v21) Toolbar</a>
 */
public class MeetingPreferenceActivity extends AppCompatPreferenceActivity {

    private static final String MEETING_PREFERENCE_COUNT = "meeting_preference_count";
    private static final String MEETING_PREFERENCES_ID_STRING_SET = "meeting_preferences_id_string_set";
    private static final String MEETING_SUB_PREFERENCE_CLASS = MeetingSubPreferenceFragment.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // layoutResId is activity_settings layout which does not contain any list with id list
        //setContentView(R.layout.activity_settings);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.setting_profile_toolbar);
        setSupportActionBar(toolbar);

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
    }

    /**
     * Override {@link android.support.v7.app.AppCompatDelegateImplV7} setContentView
     * Called by {@link PreferenceActivity} onCreate method which pass a layoutResID generated from
     * Preference_Activity Resource.
     * @param layoutResID
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_settings, null);
        getLayoutInflater().inflate(layoutResID, (ViewGroup) ll.findViewById(R.id.setting_content), true);
        super.setContentView(ll);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.settings_headers, target);
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Set<String> meetingPreferencesIdSet = defaultSharedPreferences.getStringSet(MEETING_PREFERENCES_ID_STRING_SET, null);
        if (meetingPreferencesIdSet == null) {
            return;
        }
        for (String preferenceId : meetingPreferencesIdSet) {
            target.add(createHeader(preferenceId));
        }
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        // TODO: bind extra to fragment that would be started
        super.onHeaderClick(header, position);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        final Class<?> cls;
        try {
            cls = Class.forName(fragmentName);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return Fragment.class.isAssignableFrom(cls);
    }

    private Header createHeader(String id) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Header header = new Header();
        header.fragment = MEETING_SUB_PREFERENCE_CLASS;
        header.title = sp.getString(id+"_type", "Reject");
        String startTime = sp.getString(id+"_startTime", "00:00");
        String endTime = sp.getString(id+"_endTime", "08:00");
        String repeat = sp.getString(id+"_repeat", "Daily");
        header.summary = String.format("%s~%s\t%s", startTime, endTime, repeat);
        if (header.fragmentArguments == null) {
            header.fragmentArguments = new Bundle();
        }
        // TODO: put preference Object
        header.fragmentArguments.putString("preference_id", id);

        return header;
    }

    /**
     * {@inheritDoc}
     * Override {@link PreferenceActivity} startWithFragment in order to start activity with result
     */
    @Override
    public void startWithFragment(String fragmentName, Bundle args,
                                  Fragment resultTo, int resultRequestCode, int titleRes, int shortTitleRes) {
        Intent intent = onBuildStartFragmentIntent(fragmentName, args, titleRes, shortTitleRes);
        if (resultTo == null) {
            startActivityForResult(intent, resultRequestCode);
        } else {
            resultTo.startActivityForResult(intent, resultRequestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: invalidateHeaders according to requestCode, resultCode and data
        invalidateHeaders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meeting_preference, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            openMeetingSubPreference();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMeetingSubPreference() {
        // Special header to start a empty sub meeting preference
        Header addHeader = createHeader("new_preference");
        if (addHeader.fragmentArguments == null) {
            addHeader.fragmentArguments = new Bundle();
        }
        addHeader.fragmentArguments.putBoolean("AddMeetingPreference", true);
        // It is not a decent way to call the listener method manually
        onHeaderClick(addHeader, 0);
    }

}
