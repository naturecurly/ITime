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

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Xuhui Chen (yorkfine) on 21/04/16.
 */
public class AlertUtil {

    public static void showMessageDialog(Context context, String messageTitle, String messageBody) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(messageTitle)
                .setMessage(messageBody)
                .show();
    }
}
