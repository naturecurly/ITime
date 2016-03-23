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

package com.itime.team.itime.api.model.utils;

import com.bluelinelabs.logansquare.typeconverters.DateTypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Xuhui Chen (yorkfine) on 22/03/16.
 */
public class ITimeDateTypeConverter extends DateTypeConverter {
    private DateFormat mDateFormat;

    public ITimeDateTypeConverter() {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        mDateFormat.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public DateFormat getDateFormat() {
        return mDateFormat;
    }
}
