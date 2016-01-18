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

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Xuhui Chen (yorkfine) on 12/01/16.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        bindPreferenceSummaryToValue(findPreference("name"));
        bindPreferenceSummaryToValue(findPreference("iTime_id"));
        bindPreferenceSummaryToValue(findPreference("email"));
        bindPreferenceSummaryToValue(findPreference("phone"));
        bindPreferenceSummaryToValue(findPreference("default_alert_time"));
        bindOnClickPreferenceSummaryToValue(findPreference("starts"));
        bindOnClickPreferenceSummaryToValue(findPreference("ends"));
        bindPreferenceSummaryToValue(findPreference("repeats"));
        bindPreferenceSummaryToValue(findPreference("meeting_pref_type"));
    }


    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void bindOnClickPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceClickListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceClick(preference);
    }
    @Override
    public boolean onPreferenceChange(final Preference preference, Object o) {
        String value = o.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        } else {

        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
            switch (preference.getKey()) {
                case "starts":
                case "ends":
                    TimePickerDialog timePicker1 = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            preference.setSummary(hourOfDay + " : " + minute);
                        }
                    }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),false);
                    timePicker1.show();
                    break;
                case "types":
                    break;
            }
        return true;
    }
}
