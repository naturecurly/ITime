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

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by Xuhui Chen (yorkfine) on 24/04/16.
 */
@ParcelablePlease(allFields = false)
@JsonObject
public class ParcelableCalendarType implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "calendar_id")
    public String calendarId;

    @ParcelableThisPlease
    @JsonField(name = "user_id")
    public String userId;

    @ParcelableThisPlease
    @JsonField(name = "calendar_name")
    public String calendarName;

    @ParcelableThisPlease
    @JsonField(name = "calendar_owner_id")
    public String calendarOwnerId;

    @ParcelableThisPlease
    @JsonField(name = "calendar_owner_name")
    public String calendarOwnerName;

    @ParcelableThisPlease
    @JsonField(name = "if_show")
    public boolean ifShow;

    public ParcelableCalendarType() {

    }

    public ParcelableCalendarType(String id) {
        calendarId = "";
        userId = id;
        calendarName = "";
        calendarOwnerId = userId + "_iTIME";
        calendarOwnerName = "iTIME";
        ifShow = true;
    }



    public static final Creator<ParcelableCalendarType> CREATOR = new Creator<ParcelableCalendarType>() {
        public ParcelableCalendarType createFromParcel(Parcel source) {
            ParcelableCalendarType target = new ParcelableCalendarType();
            ParcelableCalendarTypeParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelableCalendarType[] newArray(int size) {
            return new ParcelableCalendarType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableCalendarTypeParcelablePlease.writeToParcel(this, dest, flags);
    }



}
