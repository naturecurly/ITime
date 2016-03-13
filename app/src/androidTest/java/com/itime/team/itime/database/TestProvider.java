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

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Xuhui Chen (yorkfine) on 13/03/16.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ITimeDataStore.User.CONTENT_URI,
                null,
                null
        );

        Cursor c = mContext.getContentResolver().query(
                ITimeDataStore.User.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from User table during delete", 0, c.getCount());
        c.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
            This test checks to make sure that the content provider is registered correctly.
         */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // ITimeDataProivder class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ITimeDataProivder.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: ITimeDataProivder registered with authority: " + providerInfo.authority +
                            " instead of authority: " + ITimeDataStore.AUTHORITY,
                    providerInfo.authority, ITimeDataStore.AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: ITimeDataProivder not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
        This test uses the database directly to insert and then users the ContentProvider to read
        out the data.
     */
    public void testQuery() {
        // insert test data into the database
        ITimeDbHelper dbHelper = new ITimeDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues userValues = TestUtilities.createUserValues();
        long rowId = db.insert(ITimeDataStore.User.TABLE_NAME, null, userValues);
        assertTrue("Error: Unable to insert user values into the database", rowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor userCursor = mContext.getContentResolver().query(
                ITimeDataStore.User.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testUserQuery", userCursor, userValues);

    }
}
