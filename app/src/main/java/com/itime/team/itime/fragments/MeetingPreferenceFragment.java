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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itime.team.itime.R;


/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * Load local personal meeting preference when there exists, otherwise fetch these data from server.
 * Add preference views dynamically
 */
public class MeetingPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate meeting preference. Header may be a good choice
        addPreferencesFromResource(R.xml.preferences_meeting);
        findPreference("Reject").setOnPreferenceClickListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        getFragmentManager().beginTransaction()
                .add(R.id.setting_content, new MeetingSubPreferenceFragment())
                .commit();
        return false;
    }
}
