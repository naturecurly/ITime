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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.task.UserTask;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class CalendarTypeSubFragment extends Fragment {

    private static final String LOG_TAG = CalendarTypeSubFragment.class.getSimpleName();

    public static final int RESULT_ADD_CALENDAR_TYPE = 1;
    private static final String SETTINGS_DATA = "Settings_data";
    public static final String RETURN_IF_UPDATED = "return_if_updated";

    private EditText mCalendarType;
    private ParcelableCalendarType calendarType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_type_sub, null);
        mCalendarType = (EditText) view.findViewById(R.id.edit_calendar_type);

        calendarType = getActivity().getIntent().getParcelableExtra(SETTINGS_DATA);

        setHasOptionsMenu(true);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        if (calendarType.calendarName.isEmpty()) {
            title.setText(getString(R.string.new_calendar_type_title));
        } else {
            title.setText(getString(R.string.edit_calendar_type_title));
            mCalendarType.setText(calendarType.calendarName);
        }

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
            updateCanlendarType();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCanlendarType() {
        calendarType.calendarName = mCalendarType.getText().toString();
        UserTask userTask = UserTask.getInstance(getContext().getApplicationContext());
        UserTask.CallBackResult<String> callback = new UserTask.CallBackResult<String>() {
            @Override
            public void callback(String data) {
                if (data.equalsIgnoreCase("success")) {
                    // Fuck! No need to do this because it force to load calendar type again
                    // add new calendar type to static list
                    Events.calendarTypeList.add(calendarType);
                    // add not show calendar id to static set
                    if (!calendarType.ifShow) {
                        Events.notShownId.add(calendarType.calendarId);
                    }
                    final Intent intent = new Intent();
                    intent.putExtra(RETURN_IF_UPDATED, true);
                    getActivity().setResult(RESULT_ADD_CALENDAR_TYPE, intent);
                    getActivity().finish();
                }
            }
        };
        userTask.updateCalendarType(calendarType, callback);


    }
}
