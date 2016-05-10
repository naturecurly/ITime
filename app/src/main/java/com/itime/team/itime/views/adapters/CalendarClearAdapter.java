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

package com.itime.team.itime.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.model.ParcelableCalendarType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xuhui Chen (yorkfine) on 10/05/16.
 */
public class CalendarClearAdapter extends CalendarTypeAdapter {

    private Set<ParcelableCalendarType> clearSet;

    public CalendarClearAdapter(Context context, List<ParcelableCalendarType> data) {
        super(context, data);
        clearSet = new HashSet<>();
    }

    public Set<ParcelableCalendarType> getClearSet() {
        return clearSet;
    }

    public void toggleIfClear(ParcelableCalendarType parcelableCalendarType) {
        if (clearSet.contains(parcelableCalendarType)) {
            clearSet.remove(parcelableCalendarType);
        } else {
            clearSet.add(parcelableCalendarType);
        }
        notifyDataSetChanged();
    }

    @Override
    public void fillValues(int position, View convertView) {
        final ParcelableCalendarType item = (ParcelableCalendarType) getItem(position);
        if (getItemViewType(position) == 0) {
            TextView calTypeTitleText = (TextView)convertView.findViewById(R.id.calendar_type_title);

            calTypeTitleText.setText(item.calendarOwnerName);
        } else {
            TextView calTypeText = (TextView)convertView.findViewById(R.id.setting_list_left_text);
            CheckBox isShow = (CheckBox) convertView.findViewWithTag("isShow");

            calTypeText.setText(item.calendarName);
            if (clearSet.contains(item)) {
                isShow.setChecked(true);
                isShow.setVisibility(View.VISIBLE);
            } else {
                isShow.setVisibility(View.GONE);
            }
        }
    }
}
