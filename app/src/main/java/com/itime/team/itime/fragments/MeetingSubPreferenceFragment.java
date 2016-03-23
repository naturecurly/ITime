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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.api.model.Preference;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * TODO: Singleton class is preferable?
 */
public class MeetingSubPreferenceFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MeetingSubPreferenceFragment.class.getSimpleName();

    private static final String MEETING_PREFERENCE_COUNT = "meeting_preference_count";

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
    private int mCurPrefercenIndex = -1;
    private boolean isPrefer;
    private JSONObject mPreferenceJSONObject;
    private Preference mPreference; // api model synchronize with server

    // View
    private TextView mStartsDate;
    private TextView mStartsTime;
    private TextView mEndsTime;
    private TextView mRepeat;
    private TextView mType;

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
        if (args != null && args.getString(PREFERENCE_DATA) != null) {
            try {
                JSONObject data = new JSONObject(args.getString(PREFERENCE_DATA));
                mPreference = LoganSquare.parse(args.getString(PREFERENCE_DATA), Preference.class);
                //initViewsValue(data);
                mPreferenceJSONObject = data;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }

        } else {
            newPreference();
            //initViewsValue(null);
            initPreferenceJSONObject();
        }
        initViewsValue();
        return mMeetingSubPrefView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void newPreference() {
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        mPreference = new Preference();
        mPreference.id = UUID.randomUUID().toString();
        mPreference.userId = User.ID;
        mPreference.startsDate = today;
        mPreference.startsTime = today;
        mPreference.endsTime = today;
        mPreference.isLongRepeat = false;
        mPreference.repeatType = "Daily";
        mPreference.preferenceType = Type.UNAVAILABLE.getText();
        mPreference.ifDeleted = false;
        mPreference.repeatToDate = today;
        mPreference.lastUpdate = today;
    }

    private void initPreferenceJSONObject() {
        mPreferenceJSONObject = new JSONObject();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        try {
            mPreferenceJSONObject.put("preference_id", UUID.randomUUID().toString());
            mPreferenceJSONObject.put("starts_time", DateUtil.getDateWithTimeZone(year, month, date, hour, minute));
            mPreferenceJSONObject.put("ends_time", DateUtil.getDateWithTimeZone(year, month, date, hour, minute));
            mPreferenceJSONObject.put("repeat_type", "Daily");
            mPreferenceJSONObject.put("if_deleted", 0);
            mPreferenceJSONObject.put("repeat_to_date", DateUtil.getDateWithTimeZone(year, month, date, hour, minute));
            mPreferenceJSONObject.put("is_long_repeat", 1);
            mPreferenceJSONObject.put("preference_type", "Reject");
            mPreferenceJSONObject.put("starts_date", DateUtil.getDateWithTimeZone(year, month, date, hour, minute));
            mPreferenceJSONObject.put("user_id", User.ID);
            mPreferenceJSONObject.put("preference_last_update_datetime", DateUtil.getDateWithTimeZone(year, month, date, hour, minute));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void holdViews() {
        mStartsDate = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_start_date_text);
        mStartsTime = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_start_time_text);
        mEndsTime = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_end_time_text);
        mRepeat = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_repeat_text);
        mType = (TextView) mMeetingSubPrefView.findViewById(R.id.setting_meeting_type_text);
    }

    private void initViewsValue() {
        mType.setText(mPreference.preferenceType);
        mRepeat.setText(mPreference.repeatType);

        Calendar c = Calendar.getInstance();
        c.setTime(mPreference.startsDate);
        String startsDate1 = String.format("%d/%d/%d", c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
        mStartsDate.setText(startsDate1);

        c.setTime(mPreference.startsTime);
        String starts = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        mStartsTime.setText(starts);

        c.setTime(mPreference.endsTime);
        String ends = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        mEndsTime.setText(ends);

        isPrefer = mPreference.preferenceType.equalsIgnoreCase(Type.PREFER.getText());
    }

    private void initViewsValue(JSONObject values) {
        if (values != null) {
            // set existing values
            try {
                //mType.setText(values.getString("preference_type"));
                mType.setText(mPreference.preferenceType);

                Calendar c = Calendar.getInstance();
                String startsDate = values.getString("starts_date");
                c.setTime(DateUtil.getLocalDateObject(startsDate));
                c.setTimeZone(TimeZone.getDefault());
                String startsDate1 = String.format("%d/%d/%d", c.get(Calendar.DATE), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
                mStartsDate.setText(startsDate1);
                mRepeat.setText(values.getString("repeat_type"));
                isPrefer = values.getString("prefernece_type").equalsIgnoreCase("reject") ? true : false;
                String startsTime = values.getString("starts_time");
                String endsTime = values.getString("ends_time");
                c.setTime(DateUtil.getLocalDateObject(startsTime));
                String starts = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                mStartsTime.setText(starts);
                c.setTime(DateUtil.getLocalDateObject(endsTime));
                String ends = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                mEndsTime.setText(ends);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // set today values
            Calendar c = Calendar.getInstance();
            String startsDate1 = String.format("%d/%d/%d", c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
            mStartsDate.setText(startsDate1);
            String starts = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            mStartsTime.setText(starts);
            String ends = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            mEndsTime.setText(ends);
            mRepeat.setText("Daily");
            mType.setText("Reject");
        }
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", mPreference.userId);
            JSONArray jsonArray = new JSONArray();
            // set last update time to now
            mPreference.lastUpdate = Calendar.getInstance().getTime();
            String jsonString = LoganSquare.serialize(mPreference);
            jsonArray.put(new JSONObject(jsonString));
            jsonObject.put("local_preferences", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String url = URLs.SYNC_PREFERENCES;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        Log.i(LOG_TAG, jsonObject.toString());
        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(LOG_TAG, response.toString());
                        //mAdapter.notifyDataSetChanged();
                        getActivity().finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.toString());
                    }
                }
        );
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REPEAT_SETTINGS) {
            if (resultCode == RepeatPreferenceFragment.RESULT_SET_DEFAULT_ALERT) {
                String text = data.getStringExtra(RepeatPreferenceFragment.RETURN_TEXT);
                mRepeat.setText(text);
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
                        mStartsDate.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear, year));
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
                mPreference.preferenceType = text;
                break;
            }
        }
    }


}
