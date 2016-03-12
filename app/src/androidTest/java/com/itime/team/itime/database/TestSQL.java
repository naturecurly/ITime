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

import android.test.AndroidTestCase;
import android.util.Log;


/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public class TestSQL extends AndroidTestCase {
    public static final String LOG_TAG = TestSQL.class.getSimpleName();

    public void testCreateTable() {
        String table = "account";
        String [] columns = new String[]{"username", "password", "email"};
        String [] types = new String[]{"TEXT NOT NULL", "TEXT NOT NULL", "TEXT NOT NULL"};
        ITimeDbHelper dbHelper = new ITimeDbHelper(mContext);
        String sql = dbHelper.createTable(table, columns, types);
        System.out.println(sql);


        try {
            String [] columns1 = null;
            dbHelper.createTable(table, columns1, types);
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, e.toString());
        }

        try {
            String [] columns2 = new String[]{"username", "password"};
            dbHelper.createTable(table, columns2, types);
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }

    }
}
