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

package com.itime.team.itime.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public interface ITimeDataStore {
    String AUTHORITY = "itime";

    String TYPE_PRIMARY_KEY = "INTEGER PRIMARY KEY AUTOINCREMENT";
    String TYPE_INT = "INTEGER";
    String TYPE_INT_UNIQUE = "INTEGER UNIQUE";
    String TYPE_BOOLEAN = "INTEGER(1)";
    String TYPE_BOOLEAN_DEFAULT_TRUE = "INTEGER(1) DEFAULT 1";
    String TYPE_BOOLEAN_DEFAULT_FALSE = "INTEGER(1) DEFAULT 0";
    String TYPE_TEXT = "TEXT";
    String TYPE_DOUBLE_NOT_NULL = "DOUBLE NOT NULL";
    String TYPE_TEXT_NOT_NULL = "TEXT NOT NULL";
    String TYPE_TEXT_NOT_NULL_UNIQUE = "TEXT NOT NULL UNIQUE";

    Uri BASE_CONTENT_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY).build();

    interface User extends BaseColumns {
        String TABLE_NAME = "user";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String USER_ID = "user_id";
        String USER_NAME = "user_name";
        String PASSWORD = "password"; // maybe useless

        String USER_PROFILE_PICTURE = "user_profile_picture";
        String EMAIL = "email";
        String PHONE_NUMBER = "phone_number";
        String DEFAULT_ALERT = "default_alert";

        String [] COLUMNS = {_ID, USER_ID, USER_NAME, PASSWORD, USER_PROFILE_PICTURE, EMAIL,
                PHONE_NUMBER, DEFAULT_ALERT};
        String [] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL,
                TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL, TYPE_TEXT, TYPE_TEXT, TYPE_TEXT_NOT_NULL};
    }

    interface Preference extends BaseColumns {
        String TABLE_NAME = "preference";
        String CONTENT_PATH = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);

        String PREFERENCE_ID = "preference_id";
        String USER_ID = "user_id";
        String STARTS_DATE = "starts_date";
        String STARTS_TIME = "starts_time";
        String ENDS_TIME = "ends_time";
        String IS_LONG_REPEAT = "is_long_repeat";
        String REPEAT_TYPE = "repeat_type";
        String PREFERENCE_TYPE = "preference_type";
        String IF_DELETED = "if_deleted";
        String REPEAT_TO_DATE = "repeat_to_date";
        String PREFERENCE_LAST_UPDATE_DATETIME = "preference_last_update_datetime";

        String [] COLUMNS = {_ID, PREFERENCE_ID, USER_ID, STARTS_DATE, STARTS_TIME, ENDS_TIME,
                IS_LONG_REPEAT, REPEAT_TYPE, PREFERENCE_TYPE, IF_DELETED, REPEAT_TO_DATE,
                PREFERENCE_LAST_UPDATE_DATETIME};

        String [] TYPES = {TYPE_PRIMARY_KEY, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL,
                TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL,
                TYPE_BOOLEAN_DEFAULT_FALSE, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL,
                TYPE_BOOLEAN_DEFAULT_FALSE, TYPE_TEXT_NOT_NULL, TYPE_TEXT_NOT_NULL,};



    }

}
