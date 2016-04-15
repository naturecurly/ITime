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

import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.model.ParcelableMessage;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Xuhui Chen (yorkfine) on 23/03/16.
 */
public class TestUtils extends AndroidTestCase {
    public void testURLConnectionUtil() {
        String date = "2016-01-27 17:14:00 +1100";
        assertEquals("URLEncode error", "2016-01-27+17%3A14%3A00+%2B1100", URLConnectionUtil.encode(date));
    }

    public void testJsonArrayParse() {
        JSONObject jsonObject = new JSONObject();
        try {
            LoganSquare.parse(jsonObject.toString(), ParcelableMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
