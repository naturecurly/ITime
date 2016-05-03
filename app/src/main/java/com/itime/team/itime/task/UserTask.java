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

package com.itime.team.itime.task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.model.ParcelableUser;
import com.itime.team.itime.utils.ContentValuesCreator;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Xuhui Chen (yorkfine) on 29/03/16.
 */
public class UserTask {
    private final static String LOG_TAG = UserTask.class.getSimpleName();

    private final Context mContext;
    private static UserTask mInstance;

    private UserTask(Context context) {
        mContext = context;
    }

    public static synchronized UserTask getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserTask(context);
        }
        return mInstance;
    }

    public interface Callback {
        public void callback(ParcelableUser user);

        public void callbackError(VolleyError error);
    }

    public interface CallBackCalType {
        void callback(List<ParcelableCalendarType> calendarType);
        void callbackError(VolleyError error);
    }

    public interface CallBackResult<T> {
        void callback(T data);
    }

    public void loadUserInfo(final String userId, final Callback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.LOAD_USER_INFO;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, response.toString());

                        ParcelableUser user = new ParcelableUser();
                        try {
                            user = LoganSquare.parse(response.toString(), ParcelableUser.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        updateUserDatabase(userId, user);
                        if (callback != null) {
                            callback.callback(user);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                }
        );
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void updateUserInfo(final String userId, final ParcelableUser user, final Callback callback) {
        // Since parcelableUser have more fields than upload json, it is better to pass value manually
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("user_name", user.userName);
            jsonObject.put("phone_number", user.phoneNumber);
            jsonObject.put("email", user.email);
            jsonObject.put("user_profile_picture", user.userProfilePicture);
            jsonObject.put("default_alert", user.defaultAlert);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.i(LOG_TAG, jsonObject.toString());
        final String url = URLs.UPDATE_USER_INFO;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getString("result").equals("success")) {
                                updateUserDatabase(userId, user);
                                if (callback != null) {
                                    callback.callback(user);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.callbackError(error);
                        }
                    }
                }
        );
        MySingleton.getInstance(mContext).addToRequestQueue(request);

    }


    private void updateUserDatabase(String userId, ParcelableUser user) {
        // 1. check existence
        Uri userInfoByIdUri = ITimeDataStore.User.CONTENT_URI.buildUpon().appendPath(userId).build();
        Cursor c = mContext.getContentResolver().query(
                userInfoByIdUri,
                new String[]{ITimeDataStore.User._ID},
                null,
                null,
                null);

        // 2. insert or update
        final ContentValues values = ContentValuesCreator.createUser(user);
        if (c.moveToFirst()) {
            String selection = ITimeDataStore.User.USER_ID + " = ? ";
            String[] selectionArgs = new String[]{userId};
            mContext.getContentResolver().update(userInfoByIdUri, values, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(userInfoByIdUri, values);
        }
    }

    public void loadCalendarType(String userId, final CallBackCalType callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("local_preferences", new JSONArray()); // ?
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.LOAD_USER_CALENDAR_TYPES;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(LOG_TAG, response.toString());
                        if (callback != null) {
                            List<ParcelableCalendarType> calTypeList;
                            try {
                                calTypeList = LoganSquare.parseList(response.toString(), ParcelableCalendarType.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            callback.callback(calTypeList);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.toString());
                        if (callback != null) {
                            callback.callbackError(error);
                        }
                    }
                }
        );
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void updateCalendarType(ParcelableCalendarType calendarType, final CallBackResult<String> callback) {
        try {
            String calType = LoganSquare.serialize(calendarType);
            final String url = URLs.INSERT_OR_UPDATE_USER_CALENDAR_TYPE;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, calType,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.i(LOG_TAG, response.toString());
                            String result = "failed";
                            try {
                                result = response.getString("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (callback != null) {
                                callback.callback(result);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            MySingleton.getInstance(mContext).addToRequestQueue(request);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void logout(String userId, final CallBackResult<String> callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        final String url = URLs.LOG_OUT;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, response.toString());
                        String result = "failed";
                        try {
                            result = response.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (callback != null) {
                            callback.callback(result);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


        public void syncGcmRegistrationToken(String userId, String registrationToken, final CallBackResult<String> callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("gcm_registration_token", registrationToken);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.SYNC_GCM_REGISTRATION_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, response.toString());
                        String result = "failed";
                        try {
                            result = response.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (callback != null) {
                            callback.callback(result);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
    }
}
