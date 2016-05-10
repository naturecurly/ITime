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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.task.UserTask;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonArrayAuthRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.adapters.CalendarClearAdapter;
import com.itime.team.itime.views.adapters.CalendarTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class CalendarTypeFragment extends Fragment {

    private static final String LOG_TAG = CalendarTypeFragment.class.getSimpleName();

    private static final String SETTINGS = "Settings";
    private static final String SETTINGS_DATA = "Settings_data";
    private static final int REQUEST_EDIT_CALENDAR_TYPE = 1;
    public static final int CALENDAR_TYPE_SUB_SETTINGS = 9;

    public static final String CALENDAR_TYPE_ACTION_TYPE = "calendar_type_action_type";
    public static final int ADD_CALENDAR_TYPE = 1;
    public static final int CLEAR_CALENDAR_TYPE = 2;

    private View mCalendarTypeView;
    private ListView  mCalendarTypeListView;
    private CalendarTypeAdapter mAdapter;
    private List<ParcelableCalendarType> mData;

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
            mData = new ArrayList<>();
        }

        if (getArguments() != null) {
            if (getArguments().getInt(CALENDAR_TYPE_ACTION_TYPE, 0) == ADD_CALENDAR_TYPE) {
                initCalendarTypeAdapter();
            } else if (getArguments().getInt(CALENDAR_TYPE_ACTION_TYPE, 0) == CLEAR_CALENDAR_TYPE) {
                initCalendarClearAdapter();
            }
        }

        mUserId = User.ID;
        return mCalendarTypeView;
    }

    private void initCalendarTypeAdapter() {
        mAdapter = new CalendarTypeAdapter(getContext(), mData);
        mCalendarTypeListView.setAdapter(mAdapter);
        mCalendarTypeListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        // click
        mCalendarTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableCalendarType calType = (ParcelableCalendarType) mAdapter.getItem(position);
                toggleIfShow(calType);
            }
        });
        // long click
        mCalendarTypeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableCalendarType calType = (ParcelableCalendarType) mAdapter.getItem(position);
                editCalendarType(calType);
                return false;
            }
        });
        // delete button click
        mAdapter.setOnDeleteClickListener(new CalendarTypeAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClickListener(View view, int position) {

            }
        });
    }

    private void initCalendarClearAdapter() {
        mAdapter = new CalendarClearAdapter(getContext(), mData);
        mCalendarTypeListView.setAdapter(mAdapter);
        // click
        mCalendarTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableCalendarType calType = (ParcelableCalendarType) mAdapter.getItem(position);
                toggleIfClear(calType);
            }
        });
    }

    private void toggleIfShow(final ParcelableCalendarType calendarType) {
        calendarType.ifShow = !calendarType.ifShow;
        UserTask userTask = UserTask.getInstance(getContext().getApplicationContext());
        UserTask.CallBackResult<String> callback = new UserTask.CallBackResult<String>() {
            @Override
            public void callback(String data) {
                if (data.equalsIgnoreCase("success")) {
                    final String s = calendarType.calendarName + (calendarType.ifShow ? " shows" : " does not show");
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();

                    // add not show calendar id to static set
                    if (calendarType.ifShow) {
                        Events.notShownId.remove(calendarType.calendarId);
                    } else {
                        Events.notShownId.add(calendarType.calendarId);
                    }

                }
            }
        };
        userTask.updateCalendarType(calendarType, callback);
    }

    private void toggleIfClear(final ParcelableCalendarType calendarType) {
        ((CalendarClearAdapter) mAdapter).toggleIfClear(calendarType);
        for (ParcelableCalendarType c : ((CalendarClearAdapter) mAdapter).getClearSet()) {
            Log.i(LOG_TAG, c.calendarId);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadCalendarType();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments() != null) {
            if (getArguments().getInt(CALENDAR_TYPE_ACTION_TYPE, 0) == ADD_CALENDAR_TYPE) {
                inflater.inflate(R.menu.calendar_type, menu);
            } else if (getArguments().getInt(CALENDAR_TYPE_ACTION_TYPE, 0) == CLEAR_CALENDAR_TYPE) {
                inflater.inflate(R.menu.calendar_type_clear, menu);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            ParcelableCalendarType calType = new ParcelableCalendarType(mUserId);
            calType.calendarId = UUID.randomUUID().toString();
            editCalendarType(calType);
            return true;
        }

        if (id == R.id.action_clear) {
            clearCalendar(((CalendarClearAdapter) mAdapter).getClearSet());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param data ParcelableCalendarType data
     */
    private void editCalendarType(ParcelableCalendarType data) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra(SETTINGS, CALENDAR_TYPE_SUB_SETTINGS);
        if (data != null) {
            intent.putExtra(SETTINGS_DATA, data);
        }
        startActivityForResult(intent, REQUEST_EDIT_CALENDAR_TYPE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_CALENDAR_TYPE) {
            if (resultCode == CalendarTypeSubFragment.RESULT_ADD_CALENDAR_TYPE) {
                if (data != null && data.getBooleanExtra(CalendarTypeSubFragment.RETURN_IF_ADDED, false)) {
                    // reload data from server
                    loadCalendarType();
                }
            }
        }
    }

    private void loadCalendarType() {
        UserTask userTask = UserTask.getInstance(getActivity().getApplicationContext());
        UserTask.CallBackCalType callback = new UserTask.CallBackCalType() {
            @Override
            public void callback(List<ParcelableCalendarType> calendarType) {
                // add new calendar type to static list
                Events.calendarTypeList.clear();
                for (ParcelableCalendarType c : calendarType) {
                    Events.calendarTypeList.add(c);
                    // add not show calendar id to static set
                    if (!c.ifShow) {
                        Events.notShownId.add(c.calendarId);
                    }
                }
                mAdapter.refresh(calendarType);

            }

            @Override
            public void callbackError(VolleyError error) {
                Toast.makeText(getContext(), "Can not load calendars", Toast.LENGTH_SHORT).show();
            }
        };
        userTask.loadCalendarType(mUserId, callback);
    }

    private void clearCalendar(Set<ParcelableCalendarType> calendarTypes) {
        Set<String> calendarTypeIds = new HashSet<>();
        for (ParcelableCalendarType c : calendarTypes) {
            calendarTypeIds.add(c.calendarId);
        }
        Set<String> clearEvents = EventUtil.findEventsByCalendarType(calendarTypeIds);
        Log.i(LOG_TAG, "clearEvents size: " + clearEvents.size());

        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        String date = DateUtil.serverDateformatter.format(new Date());
        for (String id : clearEvents) {
            try {
                JSONObject event = new JSONObject();
                event.put("if_deleted", 1);
                event.put("event_id", id);
                event.put("event_starts_datetime", date);
                event.put("event_ends_datetime", date);
                event.put("event_ends_datetime_new", date);
                event.put("event_starts_datetime_new", date);
                event.put("event_is_punctual", 0);
                event.put("event_is_punctual_new", 0);
                event.put("is_long_repeat", 0);
                array.put(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("local_events", array);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = URLs.SYNC;

        JsonArrayAuthRequest request = new JsonArrayAuthRequest(Request.Method.POST, url, jsonObject.toString(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(LOG_TAG, "Clear calendar success");
                Toast.makeText(getActivity(), getString(R.string.clear_calendars_success), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Clear calendar failed");
                Log.e(LOG_TAG, error.toString());

                if (error.getMessage() != null) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getActivity(), getString(R.string.clear_calendars_fail), Toast.LENGTH_SHORT).show();

            }
        });
        int socketTimeout = 10000;//10 seconds - 5 seconds test pass but double it for insurance
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
    }
}
