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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.itime.team.itime.R;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.model.ParcelableMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xuhui Chen (yorkfine) on 23/04/16.
 */
public class CalendarTypeAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<ParcelableCalendarType> mData;

    public CalendarTypeAdapter(Context context, List<ParcelableCalendarType> data) {
        mContext = context;
        mData = data;
        processCalendarList();
    }

    public interface OnDeleteClickListener {
        void onDeleteClickListener(View view, int position);
    }

    public OnDeleteClickListener mOnDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.mOnDeleteClickListener = onDeleteClickListener;
    }

    public void refresh(List<ParcelableCalendarType> data) {
        mData = data;
        processCalendarList();
        notifyDataSetChanged();
    }

    public static final Comparator<ParcelableCalendarType> CALENDAR_TYPE_COMPARATOR =
            new Comparator<ParcelableCalendarType>() {
                public int compare(ParcelableCalendarType m1, ParcelableCalendarType m2) {
                    if (m1.calendarOwnerName.compareTo(m2.calendarOwnerName) != 0) {
                        return m2.calendarOwnerName.compareTo(m1.calendarOwnerName);
                    } else {
                        return m1.calendarName.compareTo(m2.calendarName);
                    }
                }
            };

    private void processCalendarList() {
        // split into different kinds of calendars, e.g. ITime, Google, Apple...
        // currently, there are three kinds of calendars
        Set<ParcelableCalendarType> iTimeCalendar = new HashSet();
        Set<ParcelableCalendarType> googleCalendar = new HashSet<>();
        Set<ParcelableCalendarType> appleCalendar = new HashSet<>();

        for (ParcelableCalendarType cal : mData) {
            if (cal.calendarOwnerName.equalsIgnoreCase("iTIME")) {
                iTimeCalendar.add(cal);
            } else if (cal.calendarOwnerName.equalsIgnoreCase("Google")) {
                googleCalendar.add(cal);
            } else if (cal.calendarOwnerName.equalsIgnoreCase("Apple")) {
                appleCalendar.add(cal);
            }
        }

        // join them into a new list for this list view adapter
        mData.clear();
        if (!iTimeCalendar.isEmpty()) {
            // add extra title
            ParcelableCalendarType iTimeTitle = new ParcelableCalendarType();
            iTimeTitle.calendarId = "";
            iTimeTitle.calendarOwnerName = "iTIME";
            iTimeTitle.calendarName = "iTIME";
            mData.add(iTimeTitle);
            List<ParcelableCalendarType> list = new ArrayList<>();
            for (ParcelableCalendarType p : iTimeCalendar) {
                list.add(p);
            }
            // Group by ownerName and then sort by name
            Collections.sort(list, CALENDAR_TYPE_COMPARATOR);
            mData.addAll(list);
        }

        if (!googleCalendar.isEmpty()) {
            ParcelableCalendarType googleCalendarTitle = new ParcelableCalendarType();
            googleCalendarTitle.calendarId = "";
            googleCalendarTitle.calendarOwnerName = "Google";
            googleCalendarTitle.calendarName = "Google";
            mData.add(googleCalendarTitle);
            List<ParcelableCalendarType> list = new ArrayList<>();
            for (ParcelableCalendarType p : googleCalendar) {
                list.add(p);
            }
            // Group by ownerName and then sort by name
            Collections.sort(list, CALENDAR_TYPE_COMPARATOR);
            mData.addAll(list);
        }

        if (!appleCalendar.isEmpty()) {
            ParcelableCalendarType appleCalendarTitle = new ParcelableCalendarType();
            appleCalendarTitle.calendarId = "";
            appleCalendarTitle.calendarOwnerName = "Apple";
            appleCalendarTitle.calendarName = "Apple";
            mData.add(appleCalendarTitle);
            List<ParcelableCalendarType> list = new ArrayList<>();
            for (ParcelableCalendarType p : appleCalendar) {
                list.add(p);
            }
            // Group by ownerName and then sort by name
            Collections.sort(list, CALENDAR_TYPE_COMPARATOR);
            mData.addAll(list);
        }

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v;
        if (getItemViewType(position) == 0) {
            // title
            v = LayoutInflater.from(mContext).inflate(R.layout.view_calendar_type_title, null);
            v.setClickable(false);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.view_setting_list, null);
            // add a checkbox and hide it
            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.setting_list_right);
            CheckBox isShow = new CheckBox(mContext);
            isShow.setClickable(false);
            isShow.setTag("isShow");
            linearLayout.addView(isShow);
            isShow.setVisibility(View.GONE);

            v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            // bind listener
            if (mOnDeleteClickListener != null) {
                v.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnDeleteClickListener.onDeleteClickListener(v, position);
                        Toast.makeText(mContext, "Delete push", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        return v;
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
            if (item.ifShow) {
                isShow.setChecked(true);
                isShow.setVisibility(View.VISIBLE);
            } else {
                isShow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        final ParcelableCalendarType item = (ParcelableCalendarType) getItem(position);
        if (item.calendarId.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
