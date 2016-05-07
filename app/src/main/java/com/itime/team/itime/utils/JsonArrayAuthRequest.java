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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xuhui Chen (yorkfine) on 3/05/16.
 */
public class JsonArrayAuthRequest extends JsonArrayRequest {

    private String mToken;
    private Map<String, String> mHeaders;

    public JsonArrayAuthRequest(int method, String url, String requestBody, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mToken != null) {
            mHeaders = new HashMap<>();
            mHeaders.put("Authorization", "Bearer " + mToken);
            return mHeaders;
        } else {
            return super.getHeaders();
        }
    }

    public void setmToken(String mToken) {
        this.mToken = mToken;
    }
}
