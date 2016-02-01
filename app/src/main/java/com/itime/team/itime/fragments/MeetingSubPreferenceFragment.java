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

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.menu.ActionMenuItem;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.itime.team.itime.activities.MeetingPreferenceActivity;
import com.itime.team.itime.activities.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * TODO: Singleton class is preferable?
 */
public class MeetingSubPreferenceFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String MEETING_PREFERENCE_COUNT = "meeting_preference_count";

    private boolean mIsNewPreferences = false;
    private int mCurPrefercenIndex = -1;
    private boolean isReject;

    public MeetingSubPreferenceFragment() {
        super();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.preferences_meeting_sub);
        Bundle args = getArguments();
        // TODO: new preference does nothing, but indexed preference fetch the values
        if (args != null && args.getBoolean("AddMeetingPreference")) {
            mIsNewPreferences = true;
        }
        findPreference("starts").setOnPreferenceClickListener(this);
        findPreference("ends").setOnPreferenceClickListener(this);
        getPreferenceScreen().getPreference(2).setOnPreferenceChangeListener(this);
        findPreference("type").setOnPreferenceClickListener(this);
        Preference type = findPreference("type");
        String summary = isReject ? "Reject" : "Accept";
        type.setSummary(summary);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // MeetingSubPreferenceFragment utilize the same menu in container activity
        menu.removeItem(R.id.action_add);
        inflater.inflate(R.menu.meeting_sub_preference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            // TODO: Save preference to sharePref and update server
            saveMeetingPreferences();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMeetingPreferences() {
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int count = defaultSharedPreferences.getInt(MEETING_PREFERENCE_COUNT, 0);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }

        // New Preferences should set keys in order to persist
        if (mIsNewPreferences) {
            for (int index = 0; index < preferenceScreen.getPreferenceCount(); index++) {
                final Preference preference = preferenceScreen.getPreference(index);
                final String resid = preference.getExtras().getString("resid");
                switch (resid) {
                    case "startTime": {
                        preference.setKey("startTime_" + count);
                        preference.getEditor().putString("preference_id_"+count + "_startTime", preference.getSummary().toString()).commit();
                    } break;

                    case "endTime": {
                        preference.setKey("endTime_" + count);
                        preference.getEditor().putString("preference_id_"+count + "_endTime", preference.getSummary().toString()).commit();

                    } break;

                    case "repeat": {
                        preference.setKey("repeat_" + count);
                        preference.getEditor().putString("preference_id_"+count + "_repeat", preference.getSummary().toString()).commit();

                    } break;

                    case "type": {
                        preference.setKey("type_" + count);
                        preference.getEditor().putString("preference_id_"+count + "_type", preference.getSummary().toString()).commit();
                    }break;

                    default:
                        break;
                }

            }
            Set<String> pref_ids = defaultSharedPreferences.getStringSet("meeting_preferences_id_string_set", new HashSet<String>());
            pref_ids.add("preference_id_"+count);
            defaultSharedPreferences.edit().putStringSet("meeting_preferences_id_string_set", pref_ids).commit();
            defaultSharedPreferences.edit().putInt(MEETING_PREFERENCE_COUNT, count+1).commit();
        }
        // back to previous activity after saved
        getActivity().onBackPressed();
    }

    private void savePreference() {

    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        final Bundle extras = preference.getExtras();
        if (extras == null) {
            return false;
        }
        final String resid = extras.getString("resid");
        if (resid.equalsIgnoreCase("startTime") || resid.equalsIgnoreCase("endTime")) {
            TimePickerDialog timePicker1 = new TimePickerDialog(preference.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    preference.setSummary(hourOfDay + " : " + minute);
                }
            }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),false);
            timePicker1.show();
        }

        if (resid.equalsIgnoreCase("type")) {
            String summary = isReject ? "Accept" : "Reject";
            isReject = !isReject;
            preference.setSummary(summary);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        return true;
    }
}
