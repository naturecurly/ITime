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

package com.itime.team.itime.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.CheckLoginActivity;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.model.ParcelableUser;
import com.itime.team.itime.task.UserTask;
import com.itime.team.itime.utils.ITimeGcmPreferences;
import com.itime.team.itime.utils.NotificationID;
import com.itime.team.itime.utils.UserUtil;

public class ITimeGcmListenerService extends GcmListenerService {

    private static final String TAG = "ITimeGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String alert = data.getString("alert");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Alert: " + alert);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */
        checkLogout(data);

        /**
         * Note for myself: If I want to use the received message to update UI, There are a few ways:
         *
         * 1. Use LocalBroadcastManager to send broadcast. Register the broadcast in related activities'
         * onResume and not forget to unregister them in onPause. So if a bunch of activities want to
         * receive this broadcast, use a base activity and make all those activities extend it.
         * Expose the onReceive method to subclass to be overrode.
         *
         * 2. Define a receiver in the manifest and set a subclass of BroadcastReceiver. Define any
         * actions in onReceive. However, this way could not get the context of specific activities
         * which need to perform the actual UI changes. But this way could startActivity like that
         * in a service. For an example, see usage in {@link #checkLogout}.
         */
        broadcastNotification(data);

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(data);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * If received message is OTHER_DEVICE_LOGIN, start CheckLoginActivity
     * @param data GCM message received.
     */
    private void checkLogout(Bundle data) {
        if (data.getString("itime_message_type", "").equals("OTHER_DEVICE_LOGIN")) {
            updateUserTable(this);
            UserTask userTask = UserTask.getInstance(getApplicationContext());
            Uri userByIdUri = ITimeDataStore.User.CONTENT_URI.buildUpon().appendPath(User.ID).build();
            Cursor c = getApplicationContext().getContentResolver().query(userByIdUri, null, null, null, null);
            if (c.moveToFirst()) {
                ParcelableUser user = new ParcelableUser(c, new ParcelableUser.CursorIndices(c));
                user.userProfilePicture = UserUtil.getLastUserCalendarId(getApplicationContext());
                userTask.updateUserInfo(user.userId, user, null);
            }
            Intent logoutIntent = new Intent(this, CheckLoginActivity.class);
            logoutIntent.putExtra("username", User.ID);
            Log.i(getClass().getSimpleName(), User.ID);

            // This service will initialize User class with User ID as empty. If app is started, User
            // ID will be set a value. It could be used as an indicator for whether app is running.
            if (!User.ID.isEmpty()) {
                // Important!!!
                // startActivity from a service must add FLAG_ACTIVITY_NEW_TASK
                // FLAG_ACTIVITY_CLEAR_TASK will clear the previous task
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                Log.i(getClass().getSimpleName(), "checklogout");
            }
        }
    }

    /*
        if a user logout, the person's account will not be remembered.
     */
    private void updateUserTable(Context context) {
        UserTableHelper dbHelper = new UserTableHelper(context, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("remember", false);
        db.update("itime_user", values, "id=?", new String[]{"1"});
        dbHelper.close();
        db.close();
    }

    private void broadcastNotification(Bundle data) {
        if (!data.getString("itime_message_type", "").equals("OTHER_DEVICE_LOGIN")) {
            Intent broadcastIntent = new Intent(ITimeGcmPreferences.HANDLE_MESSAGE);
            broadcastIntent.putExtra(ITimeGcmPreferences.HANDLE_MESSAGE_DATA, data);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    private void sendNotification(Bundle data) {
        ParcelableMessage message = new ParcelableMessage(data);
        if (message.messageType == null) {
            return;
        }

        Intent intent = new Intent(this, CheckLoginActivity.class);
        // Set ACTION_MAIN and CATEGORY_LAUNCHER is the key!
        // Even though CheckLoginActivity is finish and no longer exist, the app will return to the
        // foreground and CheckLoginActivity would not be created again. Demonstrated in the logcat
        // where on CheckLoginActivity onCreate, onResume.
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Do add this flags otherwise it will clear all the activity in the stack
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String messageTitle = data.getString("alert", "ITime Message");
        String messageBody = data.getString("message_body", "ITime Message");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_active_black)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());
    }
}
