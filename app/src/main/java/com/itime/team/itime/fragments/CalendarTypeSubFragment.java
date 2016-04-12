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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class CalendarTypeSubFragment extends Fragment {

    private static final String LOG_TAG = CalendarTypeSubFragment.class.getSimpleName();

    public static final int RESULT_ADD_CALENDAR_TYPE = 1;
    public static final String RETURN_IF_ADDED = "return_if_added";

    private EditText mCalendarType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_type_sub, null);
        mCalendarType = (EditText) view.findViewById(R.id.edit_calendar_type);

        setHasOptionsMenu(true);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("New Calendar Type");

        /*
        // Method1: show explicitly, but need show toggle off while this activity is back
        mCalendarType.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
         */

        // Method2
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // this fragment utilizes the same menu in container activity
        menu.removeItem(R.id.action_add);
        inflater.inflate(R.menu.calendar_type_sub, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            // TODO: Save new calendar tpe and update server
            updateCanlendarType();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCanlendarType() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("calendar_id", UUID.randomUUID().toString());
            jsonObject.put("user_id", User.ID);
            jsonObject.put("calendar_name", mCalendarType.getText());
            jsonObject.put("calendar_owner_id", User.ID);
            jsonObject.put("calendar_owner_name", User.ID);
            jsonObject.put("if_show", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.INSERT_OR_UPDATE_USER_CALENDAR_TYPE;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, response.toString());
                        try {
                            if (response.getString("result").equals("success")) {
                                final Intent intent = new Intent();
                                intent.putExtra(RETURN_IF_ADDED, true);
                                getActivity().setResult(RESULT_ADD_CALENDAR_TYPE, intent);
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MySingleton.getInstance(getContext()).addToRequestQueue(request);

    }
}
