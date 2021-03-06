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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.daimajia.swipe.util.Attributes;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.MainActivity;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.task.InboxTask;
import com.itime.team.itime.task.MessageHandler;
import com.itime.team.itime.utils.AlertUtil;
import com.itime.team.itime.utils.ITimeGcmPreferences;
import com.itime.team.itime.views.adapters.MessageAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class InboxFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = InboxFragment.class.getSimpleName();

    private static final int UNREAD = 0;
    private static final int ALL = 1;

    private FragmentActivity mActivity;

    /* message create time comparator used for sorting message time in reverse order */
    public static final Comparator<ParcelableMessage> MESSAGE_TIME_ORDER =
            new Comparator<ParcelableMessage>() {
                public int compare(ParcelableMessage m1, ParcelableMessage m2) {
                    return m2.createdTime.compareTo(m1.createdTime);
                }
            };


    /* Status: UNREAD or ALL */
    private int mStatus = UNREAD;

    private RecyclerView messageRecyclerView;
    private MessageAdapter mAdapter;

    /* receiver */
    private BroadcastReceiver mITimeNotificationBroadcastReceiver;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mITimeNotificationBroadcastReceiver = new ITimeNotificationBroadcastReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_inbox, null);
        setHasOptionsMenu(true);
        //setTitle();

        messageRecyclerView = (RecyclerView) view.findViewById(R.id.inbox_message_recycler_list);
        // Layout Managers:
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Item Decorator:
        //messageRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        //messageRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        mAdapter = new MessageAdapter(getActivity(), new ArrayList<ParcelableMessage>(), mStatus == ALL);
        mAdapter.setMode(Attributes.Mode.Single);
        messageRecyclerView.setAdapter(mAdapter);

        // Item Click Listener
        mAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showItemClickDialog(view, position);
            }

            @Override
            public void onItemDeleteClick(View view, int position) {
                deleteMessage(view, position);
            }
        });

        view.findViewById(R.id.inbox_mark_all_read).setOnClickListener(this);
        view.findViewById(R.id.inbox_delete_all).setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        final int id = v.getId();
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
        InboxTask.ResultCallBack reloadMessageCallback = new InboxTask.ResultCallBack<String>() {
            @Override
            public void callback(String result) {
                if (result.equals("success")) {
                    setMessages();
                }
            }
        };
        switch (id) {
            case R.id.inbox_mark_all_read:
                inboxTask.markAllMessagesToRead(User.ID, reloadMessageCallback);
                break;
            case R.id.inbox_delete_all:
                inboxTask.deleteAllMessages(User.ID, reloadMessageCallback);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
        final ParcelableMessage message = (ParcelableMessage) view.getTag();
        if (message.ifUseful || ParcelableMessage.isLongTermUsefulMessage(message)) {
            MessageHandler.handleMessage(getContext(), message);
        } else {
            AlertUtil.showMessageDialog(getContext(), message.messageTitle, message.messageBody);
        }
        if (!message.ifRead) {
            InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
            InboxTask.ResultCallBack callback = new InboxTask.ResultCallBack<String>() {
                @Override
                public void callback(String result) {
                    if (result.equals("success")) {
                        mAdapter.unRead(message);
                        // Assume this fragment attach to MainActivity, actually it does
                        ((MainActivity) getActivity()).setBadgeCount(getActivity(), mAdapter.getUnreadMessageCount() + "");
                    }
                }
            };
            inboxTask.markMessageToRead(message.messageId, callback);
        }
    }

    private void deleteMessage(final View view, final int position) {
        final ParcelableMessage message = (ParcelableMessage) view.getTag();
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
        InboxTask.ResultCallBack callback = new InboxTask.ResultCallBack<String>() {
            @Override
            public void callback(String result) {
                if (result.equals("success")) {
                    mAdapter.deleteMessage(view, position);
                    // Assume this fragment attach to MainActivity, actually it does
                    ((MainActivity) getActivity()).setBadgeCount(getActivity(), mAdapter.getUnreadMessageCount() + "");
                }
            }
        };
        inboxTask.deleteMessage(message.messageId, callback);

    }


    private void setItemTitle(MenuItem item) {
        switch (mStatus) {
            case UNREAD:
                item.setTitle(getString(R.string.all_message));
                break;

            case ALL:
                item.setTitle(getString(R.string.unread));
                break;
        }
    }

    public void setTitle() {
        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        switch (mStatus) {
            case UNREAD:
                title.setText(getString(R.string.unread));
                break;

            case ALL:
                title.setText(getString(R.string.all_message));
                break;
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkeckNewMessagesOnMainThreadOnCreate();

        // schedule timer, start after 100ms and repeat every 5s
        //mTimer.scheduleAtFixedRate(mTimerTask, 100, 5000);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "detach and cancel timer");
        mTimer.cancel();
    }

    public void setMessages() {
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
        InboxTask.Callback callback = new InboxTask.Callback() {
            @Override
            public void callback(List<ParcelableMessage> messages) {
                Collections.sort(messages, MESSAGE_TIME_ORDER);
                mAdapter.loadMessages(messages);
                Log.i(LOG_TAG, "load message");
                // Assume this fragment attach to MainActivity, actually it does
                ((MainActivity) getActivity()).setBadgeCount(getActivity(), mAdapter.getUnreadMessageCount() + "");
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
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
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

    /**
     * this method is called on the main thread (UI thread)
     */
    public void checkeckNewMessagesOnMainThreadOnCreate() {
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
        InboxTask.ResultCallBack<Integer> callback = new InboxTask.ResultCallBack<Integer>() {
            @Override
            public void callback(Integer result) {
                Log.i(LOG_TAG, "unread message count: " + result);

                // if count is zero, load messages, otherwise messages will be load onResume
                if (mAdapter.getUnreadMessageCount() == 0) {
                    setMessages();
                }
            }
        };
        inboxTask.getUnreadMessageCount(User.ID, callback);
    }

    /**
     * this method is called on the main thread (UI thread)
     */
    public void checkeckNewMessagesOnMainThread() {
        InboxTask inboxTask = InboxTask.getInstance(getActivity().getApplicationContext());
        InboxTask.ResultCallBack<Integer> callback = new InboxTask.ResultCallBack<Integer>() {
            @Override
            public void callback(Integer result) {
                Log.i(LOG_TAG, "unread message count: " + result);

                // if count is not consistent, load all the message
                if (mAdapter.getUnreadMessageCount() != result.intValue()) {
                    // could call setMessage to load messages directly
                    setMessages();
                }
            }
        };
        inboxTask.getUnreadMessageCount(User.ID, callback);
    }

    @Override
    public void onStart() {
        Log.i(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
        // check whether there are new messages whenever this activity/fragment resume
        checkeckNewMessagesOnMainThread();
        // register a receiver to receive broadcast from GCMListenerService
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mITimeNotificationBroadcastReceiver,
                new IntentFilter(ITimeGcmPreferences.HANDLE_MESSAGE));
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause");
        InboxTask.cancelLoadMessageTask(getContext());
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mITimeNotificationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        Log.i(LOG_TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    private class ITimeNotificationBroadcastReceiver extends BroadcastReceiver {

        private final String TAG = ITimeNotificationBroadcastReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i(TAG, "onReceive: " + action);
            switch (action) {
                case ITimeGcmPreferences.HANDLE_MESSAGE:
                    // just load all message, may be use the data from intent
                    setMessages();
                    break;
            }
        }
    }

}
