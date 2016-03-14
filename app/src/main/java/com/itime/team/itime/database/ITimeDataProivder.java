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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public class ITimeDataProivder extends ContentProvider {

    private ITimeDbHelper mOpenHelper;
    private ContentResolver mContentResolver;


    @Override
    public boolean onCreate() {
        mOpenHelper = new ITimeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int tableId = DataStoreUtils.getTableId(uri);
        final String table = DataStoreUtils.getTableNameById(tableId);
        switch (tableId) {
            case DataStoreUtils.TABLE_ID_USER_WITH_USERID: {
                // TODO: 13/03/16 query with user_id
                String userId = uri.getLastPathSegment();
                Cursor c = mOpenHelper.getReadableDatabase().query(
                        table,
                        projection,
                        ITimeDataStore.User.USER_ID + " = ?",
                        new String[]{userId},
                        null,
                        null,
                        null);
                setNotificationUri(c, uri);
                return c;
            }
        }
        if (table == null) return null;
        final Cursor c = mOpenHelper.getReadableDatabase().query(table, projection, selection,
                selectionArgs, null, null, sortOrder);
        setNotificationUri(c, uri);
        return c;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int tableId = DataStoreUtils.getTableId(uri);
        final String table = DataStoreUtils.getTableNameById(tableId);
        final long rowId;
        switch (tableId) {
            //
        }
        if (table == null) return null;
        rowId = mOpenHelper.getWritableDatabase().insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(rowId));
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int tableId = DataStoreUtils.getTableId(uri);
        final String table = DataStoreUtils.getTableNameById(tableId);
        switch (tableId) {
            //
        }
        if (table == null) return 0;
        final int result = mOpenHelper.getWritableDatabase().delete(table, selection, selectionArgs);
        if (result > 0) {
            getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void setNotificationUri(final Cursor c, final Uri uri) {
        final ContentResolver cr = getContentResolver();
        if (cr == null || c == null || uri == null) return;
        c.setNotificationUri(cr, uri);
    }

    private ContentResolver getContentResolver() {
        if (mContentResolver != null) return mContentResolver;
        final Context context = getContext();
        assert context != null;
        return mContentResolver = context.getContentResolver();
    }
}
