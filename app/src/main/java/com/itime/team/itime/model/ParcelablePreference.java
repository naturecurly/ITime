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
import com.itime.team.itime.api.model.Preference;
import com.itime.team.itime.api.model.utils.ITimeDateTypeConverter;

import java.util.Date;

/**
 * Created by Xuhui Chen (yorkfine) on 28/03/16.
 * Parcelable Model aims to change the api model to parcelable and flattens the field into
 * primitive type
 */

@ParcelablePlease(allFields = false)
@JsonObject
public class ParcelablePreference implements Parcelable {

    @JsonField(name = "preference_id")
    public String id;

    @JsonField(name = "user_id")
    public String userId;

    @JsonField(name = "starts_date", typeConverter = ITimeDateTypeConverter.class)
    public Date startsDate;

    @JsonField(name = "starts_time", typeConverter = ITimeDateTypeConverter.class)
    public Date startsTime;

    @JsonField(name = "ends_time", typeConverter = ITimeDateTypeConverter.class)
    public Date endsTime;

    @JsonField(name = "is_long_repeat")
    public boolean isLongRepeat;

    @JsonField(name = "repeat_type")
    public String repeatType;

    @JsonField(name = "preference_type")
    public String preferenceType;

    @JsonField(name = "if_deleted")
    public boolean ifDeleted;

    @JsonField(name = "repeat_to_date", typeConverter = ITimeDateTypeConverter.class)
    public Date repeatToDate;

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
        this.ifDeleted = preference.ifDeleted;
        this.repeatToDate = preference.repeatToDate;
        this.lastUpdate = preference.lastUpdate;
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

    }
}
