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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by Xuhui Chen (yorkfine) on 27/01/16.
 *
 * Load local personal meeting preference when there exists, otherwise fetch these data from server.
 * Add preference views dynamically
 */
public class MeetingPreferenceFragment extends Fragment {

    private static final String LOG_TAG = MeetingPreferenceFragment.class.getSimpleName();

    private View mMeetingPrefView;

    private static final String SETTINGS = "Settings";
    private static final String SETTINGS_DATA = "Settings_data";

    private static final int MEETING_SUB_SETTINGS = 5;

    private String mUserId;
    private JSONArray mData;
    private ListView mPrefListView;
    private MeetingPreferenceAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate meeting preference. Header may be a good choice
        mMeetingPrefView = inflater.inflate(R.layout.fragment_preference_meeting, null);
        mPrefListView = (ListView) mMeetingPrefView.findViewById(R.id.meeting_preference_list);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Preferences");
        setHasOptionsMenu(true);
        mUserId = User.ID;
        if (mData == null) {
            mData = new JSONArray();
        }
        mAdapter = new MeetingPreferenceAdapter(getContext(), mData);
        mPrefListView.setAdapter(mAdapter);
        mPrefListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject item = (JSONObject) mAdapter.getItem(position);
                openMeetingSubPreference(item.toString());
            }
        });

        return mMeetingPrefView;
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
            openMeetingSubPreference(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param data JSONObject data in String
     */
    private void openMeetingSubPreference(String data) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra(SETTINGS, MEETING_SUB_SETTINGS);
        if (data != null) {
            intent.putExtra(SETTINGS_DATA, data);
        }
        startActivity(intent);

    }

    private void fetchMeetingPreference() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", mUserId);
            jsonObject.put("local_preferences", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.SYNC_PREFERENCES;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(LOG_TAG, response.toString());
                        //mAdapter.notifyDataSetChanged();
                        mAdapter.refresh(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            Log.e(LOG_TAG, new String(response.data));
                        }
                        Log.e(LOG_TAG, error.toString());
                    }
                }
        );
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchMeetingPreference();
    }

    private class MeetingPreferenceAdapter extends BaseAdapter {

        private JSONArray mData;
        private Context mContext;

        public MeetingPreferenceAdapter(Context context, JSONArray data) {
            this.mContext = context;
            this.mData = data;
        }

        public void refresh(JSONArray data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return mData.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.view_meeting_pref, null);
                holder = new ViewHolder();
                holder.mPrefTypeText = (TextView)convertView.findViewById(R.id.preference_type);
                holder.mPrefStartsDateText = (TextView)convertView.findViewById(R.id.preference_starts_date);
                holder.mPrefRepeatTypeText = (TextView) convertView.findViewById(R.id.preference_repeat_type);
                holder.mPrefDurationText = (TextView) convertView.findViewById(R.id.preference_duration);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                JSONObject jsonObject = mData.getJSONObject(position);
                holder.mPrefTypeText.setText(jsonObject.getString("preference_type"));
                Calendar c = Calendar.getInstance();
                String startsDate = jsonObject.getString("starts_date");
                c.setTime(DateUtil.getLocalDateObject(startsDate));
                c.setTimeZone(TimeZone.getDefault());
                String startsDate1 = String.format("%s-%s-%s", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
                holder.mPrefStartsDateText.setText(startsDate1);
                holder.mPrefRepeatTypeText.setText(jsonObject.getString("repeat_type"));
                String startsTime = jsonObject.getString("starts_time");
                String endsTime = jsonObject.getString("ends_time");
                c.setTime(DateUtil.getLocalDateObject(startsTime));
                String starts = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                c.setTime(DateUtil.getLocalDateObject(endsTime));
                String ends = String.format("%s:%s", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

                holder.mPrefDurationText.setText(String.format("%s - %s", starts, ends));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView mPrefTypeText = null;
            private TextView mPrefStartsDateText = null;
            private TextView mPrefRepeatTypeText = null;
            private TextView mPrefDurationText = null;
        }

    }
}
