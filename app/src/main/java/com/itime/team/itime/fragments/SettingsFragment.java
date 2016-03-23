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

package com.itime.team.itime.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.login.LoginManager;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.LoginActivity;
import com.itime.team.itime.activities.SettingsActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.database.ITimeDataStore.User;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xuhui Chen (yorkfine) on 10/01/16.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    private static final int SETTINGS_LOADER = 0;


    private static final int SETTINGS_PROFILE_ID = R.id.setting_profile;
    private static final int SETTINGS_MEETING_ID = R.id.setting_meeting;
    private static final int SETTINGS_IMPORT_ID = R.id.setting_import;
    private static final int SETTINGS_ALERT_TIME_ID = R.id.setting_dft_alert_time;
    private static final int SETTINGS_CALENDAR_TYPE_ID = R.id.setting_calendar_type;
    private static final int SETTINGS_CLEAR_CALENDAR_ID = R.id.setting_clear_calendar;
    private static final int SETTING_LOGOUT_ID = R.id.settings_btn_logout;

    private static final String SETTINGS = "Settings";
    private static final int PROFILE_SETTINGS = 1;
    private static final int MEETING_SETTINGS = 2;
    private static final int IMPORT_SETTINGS = 3;
    private static final int ALERT_TIME_SETTINGS = 4;
    private static final int CALENDAR_TYPE_SETTINGS = 7;
    private static final int CLEAR_CALENDAR_SETTINGS = 8;

    //Views
    private TextView mUserNameTextView;
    private TextView mUserIdTextView;
    private ImageView mUserProfileImageView;
    private TextView mUserDefaultAlertTimeTv;


    // User ID of this user
    private String mUserId;

    View mAlertTimeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Setting");

        View v1 = view.findViewById(SETTINGS_PROFILE_ID);
        v1.setOnClickListener(this);
        View v2 = view.findViewById(SETTINGS_MEETING_ID);
        v2.setOnClickListener(this);
        View v3 = view.findViewById(SETTINGS_IMPORT_ID);
        v3.setOnClickListener(this);
        mAlertTimeView = view.findViewById(SETTINGS_ALERT_TIME_ID);
        mAlertTimeView.setOnClickListener(this);
        View v4 = view.findViewById(SETTINGS_CALENDAR_TYPE_ID);
        v4.setOnClickListener(this);
        View v5 = view.findViewById(SETTINGS_CLEAR_CALENDAR_ID);
        v5.setOnClickListener(this);
        View v6 = view.findViewById(SETTING_LOGOUT_ID);
        v6.setOnClickListener(this);

        // Hold the views
        mUserIdTextView = (TextView) view.findViewById(R.id.setting_profile_id);
        mUserNameTextView = (TextView) view.findViewById(R.id.setting_profile_name);
        mUserProfileImageView = (ImageView) view.findViewById(R.id.setting_profile_image);
        mUserDefaultAlertTimeTv = (TextView) view.findViewById(R.id.setting_dft_alert_time_text);

        // TODO: 14/03/16 Not a good way to store static data in a model object
        mUserId = com.itime.team.itime.bean.User.ID;

         return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SETTINGS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);

        switch (v.getId()) {
            case SETTINGS_PROFILE_ID:
                intent.putExtra(SETTINGS, PROFILE_SETTINGS);
                startActivity(intent);
                break;

            case SETTINGS_MEETING_ID:
                intent.putExtra(SETTINGS, MEETING_SETTINGS);
                startActivity(intent);
                break;


            case SETTINGS_IMPORT_ID:
                intent.putExtra(SETTINGS, IMPORT_SETTINGS);
                startActivity(intent);
                break;

            case SETTINGS_ALERT_TIME_ID:
                intent.putExtra(SETTINGS, ALERT_TIME_SETTINGS);
                startActivityForResult(intent, ALERT_TIME_SETTINGS);
                break;

            case SETTINGS_CALENDAR_TYPE_ID:
                intent.putExtra(SETTINGS, CALENDAR_TYPE_SETTINGS);
                startActivity(intent);
                break;

            case SETTINGS_CLEAR_CALENDAR_ID:
                break;

            case SETTING_LOGOUT_ID:
                getActivity().finish();
                Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                intent2.putExtra("username", com.itime.team.itime.bean.User.ID);
                updateUserTable();
                startActivity(intent2);
                LoginManager.getInstance().logOut();
                break;

            default:
                break;
        }
    }

    public void handleConfilct(ImageButton eventList, Button today){
        eventList.setVisibility(View.GONE);
        today.setVisibility(View.GONE);
    }

    /*
        if a user logout, the person's account will not be remembered.
     */
    private void updateUserTable(){
        UserTableHelper dbHelper = new UserTableHelper(getContext(), "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("remember", false);
        db.update("itime_user", values, "id=?", new String[]{"1"});
        dbHelper.close();
        db.close();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALERT_TIME_SETTINGS) {
            if (resultCode == AlertTimePreferenceFragment.RESULT_SET_DEFAULT_ALERT) {
                String text = data.getStringExtra(AlertTimePreferenceFragment.RETURN_TEXT);
                TextView textView = (TextView) mAlertTimeView.findViewById(R.id.setting_dft_alert_time_text);
                textView.setText(text);
            }
        }
    }

    // Fetch Data. Move to appropriate position

    public void fetchUserProfile(String userId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        final String url = URLs.LOAD_USER_INFO;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, response.toString());
                        // TODO: 14/03/16 move to worker thread
                        insertUserInfo(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                }
        );
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    public void insertUserInfo(JSONObject userInfo) {
        try {
            String userId = userInfo.getString("user_id");
            String userName = userInfo.getString("user_name");
            String password = userInfo.getString("password");
            String phoneNumber = userInfo.getString("phone_number");
            String userProfilePic = userInfo.getString("user_profile_picture");
            String email = userInfo.getString("email");
            String defaultAlert = userInfo.getString("default_alert");

            Uri userInfoByIdUri = User.CONTENT_URI.buildUpon().appendPath(userId).build();
            ContentValues contentValues = new ContentValues();
            contentValues.put(User.USER_ID, userId);
            contentValues.put(User.USER_NAME, userName);
            contentValues.put(User.PASSWORD, password);
            contentValues.put(User.EMAIL, email);
            contentValues.put(User.PHONE_NUMBER, phoneNumber);
            contentValues.put(User.USER_PROFILE_PICTURE, userProfilePic);
            contentValues.put(User.DEFAULT_ALERT, defaultAlert);
            getContext().getContentResolver().insert(userInfoByIdUri, contentValues);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri userByIdUri = ITimeDataStore.User.CONTENT_URI.buildUpon().appendPath(mUserId).build();
        return new CursorLoader(getActivity(),
                userByIdUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Only one record return
        if (data.moveToFirst()) {
            Log.i(LOG_TAG, "user information found in database");
            CursorIndices cursorIndices = new CursorIndices(data);
            final String userId = data.getString(cursorIndices.userId);
            final String userName = data.getString(cursorIndices.userName);
            final String defaultAlert = data.getColumnName(cursorIndices.defaultAlert);

            mUserIdTextView.setText(userId);
            mUserNameTextView.setText(userName);
            mUserDefaultAlertTimeTv.setText(defaultAlert);
        } else {
            // TODO: 14/03/16 should not fetch data here
            fetchUserProfile(mUserId);
            Log.e(LOG_TAG, "no user information found in database");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class CursorIndices {
        final int _id, userName, userId, defaultAlert;

        CursorIndices(final Cursor mCursor) {
            _id = mCursor.getColumnIndex(ITimeDataStore.User._ID);
            userName = mCursor.getColumnIndex(User.USER_NAME);
            userId = mCursor.getColumnIndex(User.USER_ID);
            defaultAlert = mCursor.getColumnIndex(User.DEFAULT_ALERT);
        }
    }
}
