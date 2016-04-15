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

import android.test.AndroidTestCase;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.model.ParcelableMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuhui Chen (yorkfine) on 23/03/16.
 */
public class TestUtils extends AndroidTestCase {
    public void testURLConnectionUtil() {
        String date = "2016-01-27 17:14:00 +1100";
        assertEquals("URLEncode error", "2016-01-27+17%3A14%3A00+%2B1100", URLConnectionUtil.encode(date));
    }

    public void testJsonArrayParse() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message_id", "09322c6b-d396-4930-973d-75f22c156191");
            jsonObject.put("message_body", "from somebody");
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            List<ParcelableMessage> messageList = LoganSquare.parseList(jsonArray.toString(), ParcelableMessage.class);
            assertEquals("Should be on message", 1, messageList.size());
            assertNotNull(messageList.get(0).messageBody);
            assertNull(messageList.get(0).messageSubtitle);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testJsonObjectSerialize() {
        ParcelableMessage message = new ParcelableMessage();
        message.messageId = "1";
        message.messageBody = "from xxx";
        try {
            String str = LoganSquare.serialize(message);
            // from the result: we know that null String will not be serialized. Primitive type with
            // default value will be serialized to default values, here is the boolean value (false).
            // date object will be serialized because its typeConverter write null when field name
            // is not but null but object value is null
            String expected = "{\"created_time\":null,\"if_read\":false,\"if_useful\":false,\"message_body\":\"from xxx\",\"message_id\":\"1\"}";
            assertEquals(expected, str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testJsonArraySerialize() {
        List<ParcelableMessage> messages = new ArrayList();
        ParcelableMessage message = new ParcelableMessage();
        message.messageId = "1";
        message.messageBody = "from xxx";
        messages.add(message);
        try {
            // serialize a list
            String str = LoganSquare.serialize(messages);
            // json string is a json array
            String expected = "[{\"created_time\":null,\"if_read\":false,\"if_useful\":false,\"message_body\":\"from xxx\",\"message_id\":\"1\"}]";
            assertEquals(expected, str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
