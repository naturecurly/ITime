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

import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

public class MessageTypeConverter extends StringBasedTypeConverter<MessageType> {
    @Override
    public MessageType getFromString(String s) {
        if (s.equalsIgnoreCase("YOU SENT A NEW INVITATION")) {
            s = "YOU_SENT_A_NEW_INVITATION";
        }
        return MessageType.valueOf(s);
    }

    public String convertToString(MessageType object) {
        return object.toString();
    }
}