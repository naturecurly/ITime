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
import com.itime.team.itime.model.utils.MessageType;
import com.itime.team.itime.model.utils.MessageTypeConverter;
import com.itime.team.itime.utils.DateUtil;

import java.util.Date;

import static com.itime.team.itime.model.utils.MessageType.*;

/**
 * Created by Xuhui Chen (yorkfine) on 28/03/16.
 * Parcelable Model aims to change the api model to parcelable and flattens the field into
 * primitive type
 */

@ParcelablePlease(allFields = false)
@JsonObject
public class ParcelableMessage implements Parcelable {

    @ParcelableThisPlease
    @JsonField(name = "message_id")
    public String messageId;

    @ParcelableThisPlease
    @JsonField(name = "user_id")
    public String userId;

    @ParcelableThisPlease
    @JsonField(name = "if_read")
    public boolean ifRead;

    @ParcelableThisPlease
    @JsonField(name = "if_useful")
    public boolean ifUseful;

    @ParcelableThisPlease
    @JsonField(name = "message_type", typeConverter = MessageTypeConverter.class)
    public MessageType messageType;

    @ParcelableThisPlease
    @JsonField(name = "message_title")
    public String messageTitle;

    @ParcelableThisPlease
    @JsonField(name = "message_body")
    public String messageBody;

    @ParcelableThisPlease
    @JsonField(name = "message_subtitle")
    public String messageSubtitle;

    @ParcelableThisPlease
    @JsonField(name = "relevant_id")
    public String relevantId;

    @ParcelableThisPlease
    @JsonField(name = "created_time", typeConverter = ITimeDateTypeConverter.class)
    public Date createdTime;


    @ParcelableThisPlease
    @JsonField(name = "meeting_id")
    public String meetingId;

    @ParcelableThisPlease
    @JsonField(name = "meeting_valid_token")
    public String meetingValidToken;

    public ParcelableMessage() {

    }


    public static final Creator<ParcelableMessage> CREATOR = new Creator<ParcelableMessage>() {
        public ParcelableMessage createFromParcel(Parcel source) {
            ParcelableMessage target = new ParcelableMessage();
            ParcelableMessageParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public ParcelableMessage[] newArray(int size) {
            return new ParcelableMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelableMessageParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static boolean isLongTermUsefulMessage(ParcelableMessage message) {

        switch (message.messageType) {

            case NEW_MEETING_INVITATION:
            case MEETING_UPDATED:
            case SOMEONE_CANCEL_THE_MEETING:
            case MEETING_UPDATED_CONFIRM:
            case MEETING_UPDATED_RESET:
            case CONFIRMED_NEW_INVITATION:
            case ALL_PEOPLE_ACCEPTED_THE_MEETING:
            case YOU_SENT_A_NEW_INVITATION:
            case MEETING_STATUS_CHANGE:
            case SOMEONE_QUIT_MEETING:
                return true;
            default:
                return false;
        }
    }
}
