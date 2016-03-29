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

package com.itime.team.itime.api.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.itime.team.itime.api.model.utils.ITimeDateTypeConverter;

import java.util.Date;

/**
 * Created by Xuhui Chen (yorkfine) on 29/03/16.
 */
@JsonObject
public class User {
    @JsonField(name = "user_id")
    public String userId;

    @JsonField(name = "password")
    public String password;

    @JsonField(name = "user_name")
    public String userName;

    @JsonField(name = "dev_token")
    public String devToken;

    @JsonField(name = "dev_id")
    public String devId;

    @JsonField(name = "phone_number")
    public String phoneNumber;

    @JsonField(name = "email")
    public String email;

    @JsonField(name = "qr_code")
    public String qrCode;

    @JsonField(name = "user_profile_picture")
    public String userProfilePicture;

    @JsonField(name = "last_event_sync_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastEventSyncDate;

    @JsonField(name = "last_preference_sync_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastPreferenceSyncDate;

    @JsonField(name = "last_login_datetime", typeConverter = ITimeDateTypeConverter.class)
    public Date lastLoginDate;

    @JsonField(name = "connect_token")
    public String connectToken;

    @JsonField(name = "default_alert")
    public String defaultAlert;
}
