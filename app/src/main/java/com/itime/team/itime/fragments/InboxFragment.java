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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.MeetingDetailActivity;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.task.InboxTask;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    /* Timer and Task for repeated load inbox messages */
    public static final int HAS_NEW_MESSAGES = 1;
    private Timer mTimer = new Timer();
    private static class ReloadMessageHandler extends Handler {
        // a weak reference to this activity
        private final WeakReference<InboxFragment> mInboxFragment;

        public ReloadMessageHandler(InboxFragment fragment) {
            mInboxFragment = new WeakReference<InboxFragment>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAS_NEW_MESSAGES:
                    // load messages on the main thread, so the onResponse will be handled in main
                    // thread
                    mInboxFragment.get().setMessages();
                    break;
            }
        }
    }
    private Handler mHandler = new ReloadMessageHandler(this);

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            checkNewMessages();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_inbox, null);
        setHasOptionsMenu(true);
        //setTitle();

        messageListView = (ListView) view.findViewById(R.id.inbox_message_list);
        mAdapter = new MessageAdapter(getActivity(), new ArrayList<ParcelableMessage>(), mStatus == ALL);
        messageListView.setAdapter(mAdapter);
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showItemClickDialog(view, position);
            }
        });
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
            mAdapter.setShowAll(mStatus == ALL);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showItemClickDialog(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ParcelableMessage message = (ParcelableMessage) mAdapter.getItem(position);

        // common dialog attributes
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null);

        String messageType = message.messageType;
        switch (messageType) {
            case "NEW_MEETING_INVITATION":
                builder.setPositiveButton(R.string.show_detail,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // pass meeting information to meeting detail
                                Intent intent = new Intent(getActivity(), MeetingDetailActivity.class);
                                intent.putExtra(MeetingDetailActivity.ARG_MEETING_ID, message.meetingId);
                                getActivity().startActivity(intent);
                            }
                        });
                break;
            case "SOMEONE_CANCEL_THE_MEETING":
                break;
            case "ALL_PEOPLE_ACCEPTED_THE_MEETING":
                break;
        }

        builder.show();
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

        // schedule timer, start after 100ms and repeat every 5s
        mTimer.scheduleAtFixedRate(mTimerTask, 100, 5000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "detach and cancel timer");
        mTimer.cancel();
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

    /**
     * this method is called in the timer task thread, so do not modify data through adapter here
     * because the UI thread also access to it. Adapter may be not thread-safe.
     */
    public void checkNewMessages() {
        InboxTask inboxTask = InboxTask.getInstance(getActivity());
        InboxTask.ResultCallBack<Integer> callback = new InboxTask.ResultCallBack<Integer>() {
            @Override
            public void callback(Integer result) {
                Log.i(LOG_TAG, "unread message count: " + result);

                // it is fine to just get value
                // if count is not consistent, load all the message
                if (mAdapter.getUnreadMessageCount() != result.intValue()) {
                    // do not call setMessage to load messages directly
                    Message message = new Message();
                    message.what = HAS_NEW_MESSAGES;
                    mHandler.sendMessage(message);
                }
            }
        };
        inboxTask.getUnreadMessageCount(User.ID, callback);
    }

    public class MessageAdapter extends BaseAdapter {

        private List<ParcelableMessage> messageData = null;
        private List<ParcelableMessage> unReadMessageData = null;
        private Context mContext;

        /* show unread or all messages */
        private boolean showAll = false;

        /* unread message count */
        private int unreadMessageCount;

        public MessageAdapter(Context context, List<ParcelableMessage> messages, boolean showAll) {
            mContext = context;
            messageData = messages;
            this.showAll = showAll;
            setUnReadMessageData(messageData);
        }

        private void setUnReadMessageData(List<ParcelableMessage> messageData) {
            unReadMessageData = new ArrayList<>();
            unreadMessageCount = 0;
            for (ParcelableMessage m : messageData) {
                if (!m.ifRead) {
                    unReadMessageData.add(m);
                    unreadMessageCount++;
                }
            }
        }

        public int getUnreadMessageCount() {
            return unreadMessageCount;
        }

        public void loadMessages(List<ParcelableMessage> messageData) {
            this.messageData = messageData;
            setUnReadMessageData(messageData);
            notifyDataSetChanged();
        }

        public boolean isShowAll() {
            return showAll;
        }

        public void setShowAll(boolean showAll) {
            this.showAll = showAll;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return showAll ? messageData.size() : unReadMessageData.size();
        }

        @Override
        public Object getItem(int position) {
            return showAll ? messageData.get(position) : unReadMessageData.get(position);
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
            if (message.ifRead) {
                convertView.setBackgroundColor(Color.GRAY);
            }

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
