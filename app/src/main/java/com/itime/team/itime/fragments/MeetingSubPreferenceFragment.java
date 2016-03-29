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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelablePreference;
import com.itime.team.itime.task.PreferenceTask;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * TODO: Singleton class is preferable?
 */
public class MeetingSubPreferenceFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MeetingSubPreferenceFragment.class.getSimpleName();

    public static final String PREFERENCE_DATA = "preference_data";

    public static final int REPEAT_SETTINGS = 0;

    public enum Type {
        UNAVAILABLE ("Unavailable"),
        PREFER ("Prefer");

        private final String text;
        Type(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private View mMeetingSubPrefView;
    private boolean mIsNewPreferences = false;
    private boolean isPrefer;
    private ParcelablePreference mParcelablePreference; // api model synchronize with server

    // View
    private TextView mStartsDate;
    private TextView mStartsTime;
    private TextView mEndsTime;
    private TextView mRepeat;
    private TextView mType;

    // Data
    // Starts time and end times are associated with the starts date, it is convenient
    // to generate the final data before uploading
    private Calendar mStartsDateCalendar;
    private int mStartsTimeHour;
    private int mStartsTimeMinute;
    private int mEndsTimeHour;
    private int mEndsTimeMinute;


    public MeetingSubPreferenceFragment() {
        super();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMeetingSubPrefView = inflater.inflate(R.layout.fragment_sub_preference_meeting, null);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Edit Preference");

        Bundle args = getArguments();
        // TODO: new preference does nothing, but indexed preference fetch the values
        if (args != null && args.getBoolean("AddMeetingPreference")) {
            mIsNewPreferences = true;
        }
        if (args != null && !mIsNewPreferences) {
            // TODO: set preferences from last fragment
        }

        bindListener();
        holdViews();
        if (args != null && args.get(PREFERENCE_DATA) != null) {
            mParcelablePreference = args.getParcelable(PREFERENCE_DATA);
        } else {
            newPreference();
        }
        initDataValue();
        initViewsValue();
        return mMeetingSubPrefView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * new meeting preference, set all the date to today and random a preference id
     */
    private void newPreference() {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        mParcelablePreference = new ParcelablePreference();
        mParcelablePreference.id = UUID.randomUUID().toString();
        mParcelablePreference.userId = User.ID;
        mParcelablePreference.startsDate = today;
        mParcelablePreference.startsTime = today;
        mParcelablePreference.endsTime = today;
        mParcelablePreference.isLongRepeat = false;
        mParcelablePreference.repeatType = "Daily";
        mParcelablePreference.preferenceType = Type.UNAVAILABLE.getText();
        mParcelablePreference.ifDeleted = false;
        mParcelablePreference.repeatToDate = today;
        mParcelablePreference.lastUpdate = today;
    }


    private void holdViews() {
        mStartsDate = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_start_date_text);
        mStartsTime = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_start_time_text);
        mEndsTime = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_end_time_text);
        mRepeat = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_repeat_text);
        mType = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_type_text);
    }

    private void initDataValue() {
        Calendar c = Calendar.getInstance();
        c.setTime(mParcelablePreference.startsDate);
        mStartsDateCalendar = c;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(mParcelablePreference.startsTime);
        mStartsTimeHour = c1.get(Calendar.HOUR_OF_DAY);
        mStartsTimeMinute = c1.get(Calendar.MINUTE);
        c1.setTime(mParcelablePreference.endsTime);
        mEndsTimeHour = c1.get(Calendar.HOUR_OF_DAY);
        mEndsTimeMinute = c1.get(Calendar.MINUTE);

    }
    private void initViewsValue() {
        mType.setText(mParcelablePreference.preferenceType);
        mRepeat.setText(mParcelablePreference.repeatType);

        Calendar c = Calendar.getInstance();
        c.setTime(mParcelablePreference.startsDate);
        String startsDate1 = String.format("%d/%d/%d", c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        mStartsDate.setText(startsDate1);

        c.setTime(mParcelablePreference.startsTime);
        String starts = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        mStartsTime.setText(starts);

        c.setTime(mParcelablePreference.endsTime);
        String ends = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        mEndsTime.setText(ends);

        isPrefer = mParcelablePreference.preferenceType.equalsIgnoreCase(Type.PREFER.getText());
    }


    private void bindListener() {
        if (mMeetingSubPrefView == null) return;
        final int [] viewIds = new int[]{
                R.id.setting_meeting_start_date,
                R.id.setting_meeting_start_time,
                R.id.setting_meeting_end_time,
                R.id.setting_meeting_repeat,
                R.id.setting_meeting_type
        };
        for (int id : viewIds) {
            View v = mMeetingSubPrefView.findViewById(id);
            if (v != null) {
                v.setOnClickListener(this);
            }
        }

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
            // TODO: Save preference and update server
            saveMeetingPreferences();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMeetingPreferences() {
        class PreferenceCallback implements PreferenceTask.Callback {

            @Override
            public void callback(ParcelablePreference[] preferences) {
                getActivity().finish();
            }

            @Override
            public void callbackError(VolleyError error) {

            }
        }
        PreferenceCallback callback = new PreferenceCallback();
        // set starts date, start time and end time
        mParcelablePreference.startsDate = mStartsDateCalendar.getTime();
        int year = mStartsDateCalendar.get(Calendar.YEAR);
        int month = mStartsDateCalendar.get(Calendar.MONTH);
        int day = mStartsDateCalendar.get(Calendar.DATE);
        mStartsDateCalendar.set(year, month, day, mStartsTimeHour, mStartsTimeMinute);
        mParcelablePreference.startsTime = mStartsDateCalendar.getTime();
        mStartsDateCalendar.set(year, month, day, mEndsTimeHour, mEndsTimeMinute);
        mParcelablePreference.endsTime = mStartsDateCalendar.getTime();
        // set last update time to now
        mParcelablePreference.lastUpdate = Calendar.getInstance().getTime();
        PreferenceTask.getInstance(getContext()).updatePreference(User.ID, mParcelablePreference, callback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REPEAT_SETTINGS) {
            if (resultCode == RepeatPreferenceFragment.RESULT_SET_DEFAULT_ALERT) {
                String text = data.getStringExtra(RepeatPreferenceFragment.RETURN_TEXT);
                mRepeat.setText(text);
                mParcelablePreference.repeatType = text;
            }

        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.setting_meeting_start_date: {
                final Calendar cal = Calendar.getInstance();
                DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mStartsDate.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
                        mStartsDateCalendar.set(year, monthOfYear, dayOfMonth);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;
            }
            case R.id.setting_meeting_start_time:
            case R.id.setting_meeting_end_time: {
                final Calendar cal = Calendar.getInstance();
                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView time = id == R.id.setting_meeting_start_time ? mStartsTime : mEndsTime;

                        if (id == R.id.setting_meeting_start_time) {
                            mStartsTimeHour = hourOfDay;
                            mStartsTimeMinute = minute;
                        } else {
                            mEndsTimeHour = hourOfDay;
                            mEndsTimeMinute = minute;
                        }

                        time.setText(hourOfDay + ":" + minute);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),false);
                timePicker.show();
                break;
            }
            case R.id.setting_meeting_repeat: {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra("Settings", 6);
                startActivityForResult(intent, REPEAT_SETTINGS);

                break;
            }
            case R.id.setting_meeting_type: {
                String text = isPrefer ? Type.UNAVAILABLE.getText() : Type.PREFER.getText();
                isPrefer = !isPrefer;
                mType.setText(text);
                mParcelablePreference.preferenceType = text;
                break;
            }
        }
    }
}
