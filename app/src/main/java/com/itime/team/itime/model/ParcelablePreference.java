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

package com.itime.team.itime.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;
import com.itime.team.itime.api.model.Preference;
import com.itime.team.itime.api.model.utils.ITimeDateTypeConverter;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.utils.DateUtil;

import java.util.Date;

/**
 * Created by Xuhui Chen (yorkfine) on 28/03/16.
 * Parcelable Model aims to change the api model to parcelable and flattens the field into
 * primitive type
 */

@ParcelablePlease(allFields = false)
@JsonObject
public class ParcelablePreference implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "preference_id")
    public String id;

    @ParcelableThisPlease
    @JsonField(name = "user_id")
    public String userId;

    @ParcelableThisPlease
    @JsonField(name = "starts_date", typeConverter = ITimeDateTypeConverter.class)
    public Date startsDate;

    @ParcelableThisPlease
    @JsonField(name = "starts_time", typeConverter = ITimeDateTypeConverter.class)
    public Date startsTime;

    @ParcelableThisPlease
    @JsonField(name = "ends_time", typeConverter = ITimeDateTypeConverter.class)
    public Date endsTime;

    @ParcelableThisPlease
    @JsonField(name = "is_long_repeat")
    public boolean isLongRepeat;

    @ParcelableThisPlease
    @JsonField(name = "repeat_type")
    public String repeatType;

    @ParcelableThisPlease
    @JsonField(name = "preference_type")
    public String preferenceType;

    @ParcelableThisPlease
    @JsonField(name = "if_deleted")
    public boolean ifDeleted;

    @ParcelableThisPlease
    @JsonField(name = "repeat_to_date", typeConverter = ITimeDateTypeConverter.class)
    public Date repeatToDate;

    @ParcelableThisPlease
    @JsonField(name = "preference_last_update_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastUpdate;

    public ParcelablePreference() {

    }

    public ParcelablePreference(final Preference preference) {
        this.id = preference.id;
        this.userId = preference.userId;
        this.startsDate = preference.startsDate;
        this.endsTime = preference.endsTime;
        this.startsTime = preference.startsTime;
        this.isLongRepeat = preference.isLongRepeat;
        this.repeatType = preference.repeatType;
        this.preferenceType = preference.preferenceType;
        this.ifDeleted = preference.ifDeleted;
        this.repeatToDate = preference.repeatToDate;
        this.lastUpdate = preference.lastUpdate;
    }

    public ParcelablePreference(final Cursor cursor, CursorIndices indices) {
        this.id = cursor.getString(indices.preferencId);
        this.userId = cursor.getString(indices.userId);
        this.startsDate = DateUtil.getLocalDateObject(cursor.getString(indices.startsDate));
        this.startsTime = DateUtil.getLocalDateObject(cursor.getString(indices.startsTime));
        this.endsTime = DateUtil.getLocalDateObject((cursor.getString(indices.endsTime)));
        this.isLongRepeat = cursor.getInt(indices.ifDeleted) == 1 ? true : false;
        this.repeatType = cursor.getString(indices.repeatType);
        this.preferenceType = cursor.getString(indices.preferenceType);
        this.ifDeleted = cursor.getInt(indices.ifDeleted) == 1 ? true : false;
        this.repeatToDate = DateUtil.getLocalDateObject(cursor.getString(indices.repeatToDate));
        this.lastUpdate = DateUtil.getLocalDateObject(cursor.getString(indices.lastUpdate));
    }

    public static class CursorIndices {
        final int _id, preferencId, userId, startsDate, startsTime, endsTime, isLongRepeat,
                repeatType, preferenceType, ifDeleted, repeatToDate, lastUpdate;

        public CursorIndices(final Cursor mCursor) {
            _id = mCursor.getColumnIndex(ITimeDataStore.Preference._ID);
            preferencId = mCursor.getColumnIndex(ITimeDataStore.Preference.PREFERENCE_ID);
            userId = mCursor.getColumnIndex(ITimeDataStore.Preference.USER_ID);
            startsDate = mCursor.getColumnIndex(ITimeDataStore.Preference.STARTS_DATE);
            endsTime = mCursor.getColumnIndex(ITimeDataStore.Preference.ENDS_TIME);
            startsTime = mCursor.getColumnIndex(ITimeDataStore.Preference.STARTS_TIME);
            isLongRepeat = mCursor.getColumnIndex(ITimeDataStore.Preference.IS_LONG_REPEAT);
            repeatType = mCursor.getColumnIndex(ITimeDataStore.Preference.REPEAT_TYPE);
            preferenceType = mCursor.getColumnIndex(ITimeDataStore.Preference.PREFERENCE_TYPE);
            ifDeleted = mCursor.getColumnIndex(ITimeDataStore.Preference.IF_DELETED);
            repeatToDate = mCursor.getColumnIndex(ITimeDataStore.Preference.REPEAT_TO_DATE);
            lastUpdate = mCursor.getColumnIndex(ITimeDataStore.Preference.PREFERENCE_LAST_UPDATE_DATETIME);
        }
    }

    public static final Creator<ParcelablePreference> CREATOR = new Creator<ParcelablePreference>() {
        public ParcelablePreference createFromParcel(Parcel source) {
            ParcelablePreference target = new ParcelablePreference();
            ParcelablePreferenceParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelablePreference[] newArray(int size) {
            return new ParcelablePreference[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelablePreferenceParcelablePlease.writeToParcel(this, dest, flags);
    }
}
