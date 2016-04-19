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

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Xuhui Chen (yorkfine) on 15/04/16.
 */
public class InboxTask {
    private final static String LOG_TAG = InboxTask.class.getSimpleName();

    private final Context mContext;
    private static InboxTask mInstance;

    /**
     * // TODO: 15/04/16 implement as singleton temporary, change to app module and use dependency inject
     *
     * @param context
     */
    private InboxTask(Context context) {
        mContext = context;
    }

    public static synchronized InboxTask getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new InboxTask(context);
        }
        return mInstance;
    }

    // TODO: 15/04/16 Combine two callback function into one with success and error status
    public interface Callback {
        public void callback(List<ParcelableMessage> messages);
        public void callbackError(VolleyError error);
    }

    public interface ResultCallBack<T> {
        public void callback(T result);
    }

    public void loadMessage(String userId, final Callback callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.LOAD_INBOX;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(LOG_TAG, response.toString());

                        // TODO: 29/03/16 do in worker thread
                        List<ParcelableMessage> messages;
                        try {
                             messages = LoganSquare.parseList(response.toString(), ParcelableMessage.class);


                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        ////

                        if (callback != null) {
                            callback.callback(messages);
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
                });
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public void getUnreadMessageCount(String userId, final ResultCallBack<Integer> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.UNREAD_MESSAGE_COUNT;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int unreadMessageCount = -1; // invalid count
                        try {
                            unreadMessageCount = response.getInt("unread_message_count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (callback != null) {
                            callback.callback(new Integer(unreadMessageCount));
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

    public void markMessageToRead(String messageId, final ResultCallBack<String> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message_id", messageId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.MARK_MESSAGE_TO_READ;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

    public void markMessageToUseless(String messageId, final ResultCallBack<String> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message_id", messageId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.MARK_MESSAGE_TO_USELESS;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

    public void markAllMessagesToRead(String userId, final ResultCallBack<String> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.MARK_ALL_MESSAGES_TO_READ;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

    public void deleteMessage(String messageId, final ResultCallBack<String> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message_id", messageId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.DELETE_ALL_MESSAGES;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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

    public void deleteAllMessages(String userId, final ResultCallBack<String> callback) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String url = URLs.DELETE_MESSAGE;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
}
