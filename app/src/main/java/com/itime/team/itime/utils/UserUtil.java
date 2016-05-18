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

package com.itime.team.itime.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.model.ParcelableUser;
import com.itime.team.itime.task.UserTask;

/**
 * Created by Xuhui Chen (yorkfine) on 18/05/16.
 */
public class UserUtil {
    public static String getLastUserCalendarId(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String id = sharedPreferences.getString(ITimePreferences.LAST_CALENDAR_TYPE, User.ID+"_iTIME_default");
        if (id.isEmpty()) {
            id = User.ID+"_iTIME_default";
        }
        return id;
    }

    /**
     * call this method every time you choose a new calendar
     * @param context
     * @param calendarId
     */
    public static void setLastUserCalendarId(Context context, String calendarId) {
        UserTask userTask = UserTask.getInstance(context);
        Uri userByIdUri = ITimeDataStore.User.CONTENT_URI.buildUpon().appendPath(User.ID).build();
        Cursor c = context.getContentResolver().query(userByIdUri, null, null, null, null);
        if (c.moveToFirst()) {
            ParcelableUser user = new ParcelableUser(c, new ParcelableUser.CursorIndices(c));
            user.userProfilePicture = calendarId;
            userTask.updateUserInfo(user.userId, user, null);
            Log.i("setLastUserCalendarId", calendarId);
        }
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(ITimePreferences.LAST_CALENDAR_TYPE, calendarId).apply();
    }
}
