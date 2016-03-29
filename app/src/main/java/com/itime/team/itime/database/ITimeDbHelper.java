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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.itime.team.itime.database.ITimeDataStore.User;

import static com.itime.team.itime.database.ITimeDataStore.*;

/**
 * Created by Xuhui Chen (yorkfine) on 12/03/16.
 */
public class ITimeDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = ITimeDbHelper.class.getSimpleName();
    // If you change the database schema, you must increase the database version
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "itime.db";

    public ITimeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "onCreate is called, create tables");
        db.beginTransaction();
        // execSQL
        db.execSQL(createTable(User.TABLE_NAME, User.COLUMNS, User.TYPES));
        db.execSQL(createTable(Preference.TABLE_NAME, Preference.COLUMNS, Preference.TYPES));
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(LOG_TAG, "upgrade database");
        // drop table
        db.execSQL("DROP TABLE IF EXISTS " + User.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Preference.TABLE_NAME);
        onCreate(db);
    }

    // create each table
    // TODO: 13/03/16 should be private after test 
    String createTable(final String tableName, final String[] columns, final String[] types, final String... constraints) {
        if (tableName == null) {
            throw new NullPointerException("Name must not be null");
        }
        if (columns == null) {
            throw new NullPointerException("Columns must not be null");
        }
        if (types == null || columns.length != types.length) {
            throw new IllegalArgumentException("length of columns and types not match");
        }
        String newColumns[] = new String[columns.length];
        for (int i = 0; i < newColumns.length; i++) {
            newColumns[i] = String.format("%s %s", columns[i], types[i]);
        }
        final StringBuilder sb = new StringBuilder("CREATE ");
        sb.append("TABLE ");
        sb.append(tableName);
        sb.append(' ');
        if (newColumns != null && newColumns.length > 0) {
            sb.append('(');
            sb.append(joinArray(newColumns, ',', true));
            if (constraints != null && constraints.length > 0) {
                sb.append(", ");
                sb.append(joinArray(constraints, ',', true));
                sb.append(' ');
            }
            sb.append(')');
        }
        return sb.toString();
    }

    private static String joinArray(final String[] columns, final char token, final boolean includeSpace) {
        final StringBuilder sb = new StringBuilder();
        final int length = columns.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(includeSpace ? token + " " : token);
            }
            sb.append(columns[i]);
        }
        return sb.toString();
    }

}
