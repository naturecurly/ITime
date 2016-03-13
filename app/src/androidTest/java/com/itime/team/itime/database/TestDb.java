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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public class TestDb extends AndroidTestCase{
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(ITimeDbHelper.DATABASE_NAME);
    }

    /**
     * delete the database before each test
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(ITimeDataStore.User.TABLE_NAME);

        mContext.deleteDatabase(ITimeDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ITimeDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // test whether table is created
        Cursor c = db.rawQuery("select name from sqlite_master where type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Not all the tables are created", tableNameHashSet.isEmpty());

        // test columns
        c = db.rawQuery("pragma table_info(" + ITimeDataStore.User.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        final HashSet<String> columnsHashSet = new HashSet<>();
        for (String col : ITimeDataStore.User.COLUMNS) {
            columnsHashSet.add(col);
        }

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnsHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required user table columns",
                columnsHashSet.isEmpty());

        db.close();
    }


}
