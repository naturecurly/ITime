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

package com.itime.team.itime.model.utils;

/**
 * Created by Xuhui Chen (yorkfine) on 19/04/16.
 */
public enum MessageType {
    NEW_MEETING_INVITATION,
    MEETING_UPDATED,
    SOMEONE_CANCEL_THE_MEETING,
    MEETING_UPDATED_CONFIRM,
    MEETING_UPDATED_RESET,
    CONFIRMED_NEW_INVITATION,

    //host received
    SOMEONE_QUIT_MEETING,
    MEETING_STATUS_CHANGE,
    YOU_SENT_A_NEW_INVITATION,

    // both
    NEW_FRIEND_REQUEST,
    MAKE_FRIEND_SUCCESS_INFO,

    ALL_PEOPLE_ACCEPTED_THE_MEETING,

    // friend
    MAKE_FRIEND_FAIL_INFO,
    REJECT_FRIEND_REQUEST,

    // others
    OTHER_DEVICE_LOGIN
}
