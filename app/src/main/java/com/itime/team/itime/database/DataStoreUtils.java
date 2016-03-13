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

import android.content.UriMatcher;
import android.net.Uri;

import com.itime.team.itime.database.ITimeDataStore.*;

/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public class DataStoreUtils {
    static final UriMatcher CONTENT_PROVIDER_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int TABLE_ID_USER = 1;
    public static final int TABLE_ID_USER_WITH_USERID = 2;

    static {
        CONTENT_PROVIDER_URI_MATCHER.addURI(ITimeDataStore.AUTHORITY, User.CONTENT_PATH, TABLE_ID_USER);
        CONTENT_PROVIDER_URI_MATCHER.addURI(ITimeDataStore.AUTHORITY, User.CONTENT_PATH + "/*", TABLE_ID_USER_WITH_USERID);
    }

    public static int getTableId(final Uri uri) {
        if (uri == null) return -1;
        return CONTENT_PROVIDER_URI_MATCHER.match(uri);
    }

    public static String getTableNameById(final int id) {
        switch (id) {
            case TABLE_ID_USER:
                return User.TABLE_NAME;
            case TABLE_ID_USER_WITH_USERID:
                return User.TABLE_NAME;
            default:
                return null;
        }
    }

        // We had better put some specific database query methods here.
}
