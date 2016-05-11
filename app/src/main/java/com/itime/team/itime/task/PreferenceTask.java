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
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bluelinelabs.logansquare.LoganSquare;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.model.ParcelablePreference;
import com.itime.team.itime.utils.ContentValuesCreator;
import com.itime.team.itime.utils.JsonArrayAuthRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Xuhui Chen (yorkfine) on 28/03/16.
 *
 * Tasks related to handle preference
 *
 * Actually, it should have been a module of application, meaning that it is under the application
 * context. So that it could contains a field of application context, which could be used by Volley
 * Singleton. But it needs a strong mind and technique to manager a subclass of application and
 * some application modules, making these modules could be initialised before they could be used and
 * shared by all the application parts. Currently ,I just pass the context from the Activity. In the
 * future, refactor should be taken.
 *
 */
public class PreferenceTask {

    private final static String LOG_TAG = PreferenceTask.class.getSimpleName();

    private final Context mContext;
    private static PreferenceTask mInstance;

    /**
     * // TODO: 28/03/16 implement as singleton temporary, change to app module and use dependency inject
     *
     * @param context
     */
    private PreferenceTask(Context context) {
        mContext = context;
    }

    public static synchronized PreferenceTask getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PreferenceTask(context);
        }
        return mInstance;
    }

    public interface Callback {
        public void callback(ParcelablePreference[] preference);
        public void callbackError(VolleyError error);
    }

    public void syncPreference(String userId, @Nullable ParcelablePreference[] preferences,
                               final Callback callback) {
        final String url = URLs.SYNC_PREFERENCES;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            JSONArray jsonArray = new JSONArray();
            if (preferences != null) {
                for (ParcelablePreference p : preferences) {
                    String jsonString = LoganSquare.serialize(p);
                    jsonArray.put(new JSONObject(jsonString));
                }
            }
            jsonObject.put("local_preferences", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        JsonArrayAuthRequest request = new JsonArrayAuthRequest(
                Request.Method.POST, url, jsonObject.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(LOG_TAG, response.toString());

                        // TODO: 29/03/16 do in worker thread
                        final ParcelablePreference[] preferences = new ParcelablePreference[response.length()];
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                preferences[i] = LoganSquare.parse(response.getJSONObject(i).toString(), ParcelablePreference.class);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        savePreferenceToDatabase(preferences);
                        ////

                        if (callback != null) {
                            callback.callback(preferences);
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

    /**
     * Update a new or existed preference to server.
     * @param userId
     * @param preference
     */
    public void updatePreference(String userId, @Nullable ParcelablePreference preference,
                                 Callback callback) {
        if (userId == null || preference == null) {
            return;
        }
        final ParcelablePreference [] preferences = new ParcelablePreference[]{preference};
        syncPreference(userId, preferences, callback);
    }

    /**
     * save synchronized preference into database
     * @param preferences
     */
    public void savePreferenceToDatabase(final ParcelablePreference[] preferences) {
        // 1. delete all preferences
        mContext.getContentResolver().delete(ITimeDataStore.Preference.CONTENT_URI, null, null);
        // 2. insert all new preferences
        final ContentValues[] valueArray = new ContentValues[preferences.length];
        int i = 0;
        for (ParcelablePreference p : preferences) {
            valueArray[i++] = ContentValuesCreator.createPreference(p);
        }
        mContext.getContentResolver().bulkInsert(ITimeDataStore.Preference.CONTENT_URI, valueArray);

    }
}
