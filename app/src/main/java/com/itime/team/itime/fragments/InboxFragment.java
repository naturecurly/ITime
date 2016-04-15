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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.task.InboxTask;
import com.itime.team.itime.utils.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class InboxFragment extends Fragment {

    private static final String LOG_TAG = InboxFragment.class.getSimpleName();

    private static final int UNREAD = 0;
    private static final int ALL = 1;

    /* Status: UNREAD or ALL */
    private int mStatus = UNREAD;

    private ListView messageListView;
    private MessageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_inbox, null);
        setHasOptionsMenu(true);
        //setTitle();

        messageListView = (ListView) view.findViewById(R.id.inbox_message_list);
        mAdapter = new MessageAdapter(getActivity(), new ArrayList<ParcelableMessage>());
        messageListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.inbox, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.inbox_action) {
            mStatus = (mStatus+1)%2;
            setTitle();
            setItemTitle(item);
            // TODO: 22/03/16 Reload the list view

        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemTitle(MenuItem item) {
        switch (mStatus) {
            case UNREAD:
                item.setTitle("All");
                break;

            case ALL:
                item.setTitle("UnRead");
                break;
        }
    }

    public void setTitle() {
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        switch (mStatus) {
            case UNREAD:
                title.setText("UnRead");
                break;

            case ALL:
                title.setText("All");
                break;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMessages();
    }

    public void setMessages() {
        InboxTask inboxTask = InboxTask.getInstance(getActivity());
        InboxTask.Callback callback = new InboxTask.Callback() {
            @Override
            public void callback(List<ParcelableMessage> messages) {
                mAdapter.loadMessages(messages);
            }

            @Override
            public void callbackError(VolleyError error) {

            }
        };
        inboxTask.loadMessage(User.ID, callback);
    }

    public class MessageAdapter extends BaseAdapter {

        private List<ParcelableMessage> messageData = null;
        private Context mContext;

        /* show unread or all messages */
        private boolean showAll = false;

        public MessageAdapter(Context context, List<ParcelableMessage> messages) {
            mContext = context;
            messageData = messages;
        }

        public void loadMessages(List<ParcelableMessage> messageData) {
            this.messageData = messageData;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return messageData.size();
        }

        @Override
        public Object getItem(int position) {
            return messageData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.view_message_list_cell, null);
                holder = new ViewHolder();
                holder.mMessageTitle = (TextView) convertView.findViewById(R.id.message_title);
                holder.mMessageTime = (TextView) convertView.findViewById(R.id.message_time);
                holder.mMessageBody = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ParcelableMessage message = (ParcelableMessage) getItem(position);
            holder.mMessageTitle.setText(message.messageTitle);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTime = formatter.format(message.createdTime);
            holder.mMessageTime.setText(createTime);
            holder.mMessageBody.setText(message.messageSubtitle);
            return convertView;
        }

        private final class ViewHolder {
            private TextView mMessageTitle = null;
            private TextView mMessageTime = null;
            private TextView mMessageBody = null;
        }
    }
}
