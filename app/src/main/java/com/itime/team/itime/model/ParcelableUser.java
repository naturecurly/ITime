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
import com.itime.team.itime.api.model.utils.ITimeDateTypeConverter;
import com.itime.team.itime.database.ITimeDataStore;

import java.util.Date;

/**
 * Created by Xuhui Chen (yorkfine) on 29/03/16.
 */
@ParcelablePlease(allFields = false)
@JsonObject
public class ParcelableUser implements Parcelable {
    @ParcelableThisPlease
    @JsonField(name = "user_id")
    public String userId;

    @ParcelableThisPlease
    @JsonField(name = "password")
    public String password;

    @ParcelableThisPlease
    @JsonField(name = "user_name")
    public String userName;

    @ParcelableThisPlease
    @JsonField(name = "dev_token")
    public String devToken;

    @ParcelableThisPlease
    @JsonField(name = "dev_id")
    public String devId;

    @ParcelableThisPlease
    @JsonField(name = "phone_number")
    public String phoneNumber;

    @ParcelableThisPlease
    @JsonField(name = "email")
    public String email;

    @ParcelableThisPlease
    @JsonField(name = "qr_code")
    public String qrCode;

    @ParcelableThisPlease
    @JsonField(name = "user_profile_picture")
    public String userProfilePicture;

    @ParcelableThisPlease
    @JsonField(name = "last_event_sync_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastEventSyncDate;

    @ParcelableThisPlease
    @JsonField(name = "last_preference_sync_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastPreferenceSyncDate;

    @ParcelableThisPlease
    @JsonField(name = "last_login_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastLoginDate;

    @ParcelableThisPlease
    @JsonField(name = "connect_token")
    public String connectToken;

    @ParcelableThisPlease
    @JsonField(name = "default_alert")
    public String defaultAlert;

    public ParcelableUser() {

    }

    public ParcelableUser(final Cursor cursor, CursorIndices cursorIndices) {
        this.userId = cursor.getString(cursorIndices.userId);
        this.userName = cursor.getString(cursorIndices.userName);
        this.password = cursor.getString(cursorIndices.password);
        this.email = cursor.getString(cursorIndices.email);
        this.phoneNumber = cursor.getString(cursorIndices.phone);
        this.defaultAlert = cursor.getString(cursorIndices.defaultAlert);
        this.userProfilePicture = cursor.getString(cursorIndices.profilePicture);

    }

    public static class CursorIndices {
        final int _id, userName, userId, password, email, phone, defaultAlert, profilePicture;

        public CursorIndices(final Cursor mCursor) {
            _id = mCursor.getColumnIndex(ITimeDataStore.User._ID);
            userName = mCursor.getColumnIndex(ITimeDataStore.User.USER_NAME);
            userId = mCursor.getColumnIndex(ITimeDataStore.User.USER_ID);
            password = mCursor.getColumnIndex(ITimeDataStore.User.PASSWORD);
            email = mCursor.getColumnIndex(ITimeDataStore.User.EMAIL);
            phone = mCursor.getColumnIndex(ITimeDataStore.User.PHONE_NUMBER);
            defaultAlert = mCursor.getColumnIndex(ITimeDataStore.User.DEFAULT_ALERT);
            profilePicture = mCursor.getColumnIndex(ITimeDataStore.User.USER_PROFILE_PICTURE);
        }
    }


    protected ParcelableUser(Parcel in) {
        userId = in.readString();
        password = in.readString();
        userName = in.readString();
        devToken = in.readString();
        devId = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        qrCode = in.readString();
        userProfilePicture = in.readString();
        connectToken = in.readString();
        defaultAlert = in.readString();
    }


    public static final Creator<ParcelableUser> CREATOR = new Creator<ParcelableUser>() {
        public ParcelableUser createFromParcel(Parcel source) {
            ParcelableUser target = new ParcelableUser();
            ParcelableUserParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelableUser[] newArray(int size) {
            return new ParcelableUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableUserParcelablePlease.writeToParcel(this, dest, flags);
    }
}
