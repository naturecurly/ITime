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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itime.team.itime.R;


/**
 * Created by Xuhui Chen (yorkfine) on 19/01/16.
 */
public class AlertTimePreferenceFragment extends Fragment {
    private View alertTimeView;
    private ListView mAlertTimeList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        alertTimeView = inflater.inflate(R.layout.fragment_preference_alert_time, null);

        mAlertTimeList = (ListView) alertTimeView.findViewById(R.id.setting_dft_alert_time_list);
        String data[] = getResources().getStringArray(R.array.entry_default_alert_time);
        AlertTimeListAdapter alertTimeListAdapter = new AlertTimeListAdapter(getActivity(), R.layout.view_setting_list, data);
        mAlertTimeList.setAdapter(alertTimeListAdapter);

        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Default Alert Time");
        setHasOptionsMenu(true);

        return alertTimeView;
    }

    private class AlertTimeListAdapter extends ArrayAdapter<String> {

        Context context;
        int layoutResourceId;
        String data[] = null;

        public AlertTimeListAdapter(Context context, int resource, String[] data) {
            super(context, resource, data);
            this.context = context;
            this.layoutResourceId = resource;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // View Holder pattern save overhead of inflating view each time
            ViewHolder holder = null;
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.view_setting_list, null, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.getLeftText().setText(data[position]);
            //holder.getRightText().setText(data[position]);
            return convertView;
            //return super.getView(position, convertView, parent);
        }
    }

    private class ViewHolder {
        private View row;
        private TextView leftText = null;
        private View rightView = null;

        public ViewHolder(View row) {
            this.row = row;
        }

        public TextView getLeftText() {
            if (this.leftText == null) {
                this.leftText = (TextView) row.findViewById(R.id.setting_list_left_text);
            }
            return this.leftText;
        }

        public View getRightView() {
            if (this.rightView == null) {
                this.rightView =  row.findViewById(R.id.setting_list_right_text);
            }
            return this.rightView;
        }
    }
}
