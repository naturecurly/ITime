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
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class CalendarTypeFragment extends Fragment {

    private static final String LOG_TAG = CalendarTypeFragment.class.getSimpleName();

    private static final String SETTINGS = "Settings";
    private static final String SETTINGS_DATA = "Settings_data";
    public static final int CALENDAR_TYPE_SUB_SETTINGS = 9;

    private View mCalendarTypeView;
    private ListView  mCalendarTypeListView;
    private CalendarTypeAdapter mAdapter;
    private JSONArray mData;

    private String mUserId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCalendarTypeView = inflater.inflate(R.layout.fragment_calendar_type, null);
        mCalendarTypeListView = (ListView) mCalendarTypeView.findViewById(R.id.setting_calendar_type_list);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Calendar Type");
        setHasOptionsMenu(true);


        if (mData == null) {
            mData = new JSONArray();
        }
        mAdapter = new CalendarTypeAdapter(getContext(), mData);
        mCalendarTypeListView.setAdapter(mAdapter);
        mCalendarTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 22/03/16 change if show

            }
        });

        mUserId = User.ID;
        return mCalendarTypeView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchCalendarType();
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
            addNewCalendarType(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param data JSONObject data in String, null for add new calendar type
     */
    private void addNewCalendarType(String data) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra(SETTINGS, CALENDAR_TYPE_SUB_SETTINGS);
        if (data != null) {
            intent.putExtra(SETTINGS_DATA, data);
        }
        startActivity(intent);

    }

    /**
     * local_calendar_types
     */
    private void fetchCalendarType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", mUserId);
            jsonObject.put("local_preferences", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.LOAD_USER_CALENDAR_TYPES;
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
                        Log.e(LOG_TAG, error.toString());
                    }
                }
        );
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private class CalendarTypeAdapter extends BaseAdapter {

        private JSONArray mData;
        private Context mContext;

        public CalendarTypeAdapter(Context context, JSONArray data) {
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
                return null;
            }
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
                convertView = inflater.inflate(R.layout.view_setting_list, null);
                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.setting_list_right);
                ImageView isShowImage = new ImageView(mContext);
                linearLayout.addView(isShowImage);
                holder = new ViewHolder();
                holder.mCalTypeText = (TextView)convertView.findViewById(R.id.setting_list_left_text);
                holder.mIsShow = isShowImage;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                final JSONObject item = (JSONObject) getItem(position);
                holder.mCalTypeText.setText(item.getString("calendar_name"));
                if (item.getBoolean("if_show")) {
                    holder.mIsShow.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView mCalTypeText = null;
            private ImageView mIsShow = null;
        }
    }
}
