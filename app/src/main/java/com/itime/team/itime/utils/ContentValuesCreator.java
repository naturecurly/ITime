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

import android.content.ContentValues;

import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.model.ParcelablePreference;

/**
 * Created by Xuhui Chen (yorkfine) on 29/03/16.
 */
public final class ContentValuesCreator {

    public static ContentValues createUser() {
        final ContentValues values = new ContentValues();
        return values;
    }

    public static ContentValues createPreference(ParcelablePreference preference) {
        if (preference == null) return null;
        final ContentValues values = new ContentValues();
        values.put(ITimeDataStore.Preference.PREFERENCE_ID, preference.id);
        values.put(ITimeDataStore.Preference.USER_ID, preference.userId);
        values.put(ITimeDataStore.Preference.STARTS_DATE, DateUtil.formatLocalDateObject(preference.startsDate));
        values.put(ITimeDataStore.Preference.STARTS_TIME, DateUtil.formatLocalDateObject(preference.startsTime));
        values.put(ITimeDataStore.Preference.ENDS_TIME, DateUtil.formatLocalDateObject(preference.endsTime));
        values.put(ITimeDataStore.Preference.IS_LONG_REPEAT, preference.isLongRepeat);
        values.put(ITimeDataStore.Preference.REPEAT_TYPE, preference.repeatType);
        values.put(ITimeDataStore.Preference.PREFERENCE_TYPE, preference.preferenceType);
        values.put(ITimeDataStore.Preference.IF_DELETED, preference.ifDeleted);
        values.put(ITimeDataStore.Preference.REPEAT_TO_DATE, DateUtil.formatLocalDateObject(preference.repeatToDate));
        values.put(ITimeDataStore.Preference.PREFERENCE_LAST_UPDATE_DATETIME, DateUtil.formatLocalDateObject(preference.lastUpdate));
        return values;
    }
}
