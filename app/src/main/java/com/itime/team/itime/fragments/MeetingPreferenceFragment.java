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

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;


/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * Load local personal meeting preference when there exists, otherwise fetch these data from server.
 * Add preference views dynamically
 */
public class MeetingPreferenceFragment extends Fragment
        implements Preference.OnPreferenceClickListener {

    private View mMeetingPrefView;

    private static final String SETTINGS = "Settings";
    private static final int MEETING_SUB_SETTINGS = 5;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate meeting preference. Header may be a good choice
        mMeetingPrefView = inflater.inflate(R.layout.fragment_preference_meeting, null);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Preferences");
        setHasOptionsMenu(true);
        return mMeetingPrefView;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.setting_content, new MeetingSubPreferenceFragment())
//                .commit();
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meeting_preference, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra(SETTINGS, MEETING_SUB_SETTINGS);
        startActivity(intent);

    }
}
