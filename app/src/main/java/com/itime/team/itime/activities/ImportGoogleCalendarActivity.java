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

package com.itime.team.itime.activities;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bluelinelabs.logansquare.LoganSquare;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableCalendarType;
import com.itime.team.itime.task.UserTask;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonArrayAuthRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Xuhui Chen (yorkfine) on 3/05/16.
 */
public class ImportGoogleCalendarActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks{
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private Button mCallApiButton;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    // Can only use lower 8 bits for requestCode
    // http://stackoverflow.com/questions/33331073/android-what-to-choose-for-requestcode-values
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 100;

    private static final String BUTTON_TEXT = "Import Google Calendar";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout activityLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        activityLayout.setLayoutParams(lp);
        activityLayout.setOrientation(LinearLayout.VERTICAL);
        activityLayout.setPadding(16, 16, 16, 16);

        ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mCallApiButton = new Button(this);
        mCallApiButton.setText(BUTTON_TEXT);
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);
            }
        });
        activityLayout.addView(mCallApiButton);

        mOutputText = new TextView(this);
        mOutputText.setLayoutParams(tlp);
        mOutputText.setPadding(16, 16, 16, 16);
        mOutputText.setVerticalScrollBarEnabled(true);
        mOutputText.setMovementMethod(new ScrollingMovementMethod());
        mOutputText.setText(
                "Click the \'" + BUTTON_TEXT +"\' button to import.");
        activityLayout.addView(mOutputText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API ...");

        setContentView(activityLayout);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }



    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
//            String accountName = getPreferences(Context.MODE_PRIVATE)
//                    .getString(PREF_ACCOUNT_NAME, null);
//            if (accountName != null) {
//                mCredential.setSelectedAccountName(accountName);
//                getResultsFromApi();
//            } else {
//                // Start a dialog from which the user can choose an account
//                startActivityForResult(
//                        mCredential.newChooseAccountIntent(),
//                        REQUEST_ACCOUNT_PICKER);
//            }
            startActivityForResult(
                    mCredential.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER);
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
//                        SharedPreferences settings =
//                                getPreferences(Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = settings.edit();
//                        editor.putString(PREF_ACCOUNT_NAME, accountName);
//                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ImportGoogleCalendarActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient mClient = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();

            mClient = new OkHttpClient();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                //return getDataFromApi();
                List<String> calendars =  getCalendarLists();
                List<String> result = importEvents(calendars);
                /*
                http://stackoverflow.com/questions/18030486/google-oauth2-application-remove-self-from-user-authenticated-applications
                https://accounts.google.com/o/oauth2/revoke?token={token}
                If the revocation succeeds, the response's status code is 200. If an error occurs, the response's status code is 400 and the response also contains an error code.
                 */
                String token = mCredential.getToken();
                Log.i("MakeRequestTask", "AuthToken " + token);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://accounts.google.com/o/oauth2/revoke?token=" + token)
                        .build();

                okhttp3.Response response = mClient.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.i("MakeRequestTask", response.body().string());
                Log.i("MakeRequestTask", "revoke auth token success");
                return result;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                            //.setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            return eventStrings;
        }

        /**
         * get all calendars and return their ids
         * @return a list of calendar ids
         * @throws IOException
         */
        private List<String> getCalendarLists() throws IOException {
            List<String> calendarIds = new ArrayList();
            // Iterate through entries in calendar list
            String pageToken = null;
            do {
                CalendarList calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
                List<CalendarListEntry> items = calendarList.getItems();

                for (CalendarListEntry calendarListEntry : items) {
                    calendarIds.add(calendarListEntry.getId());
                    // update calendar type
                    ParcelableCalendarType calendarType = new ParcelableCalendarType(User.ID);
                    final String calendarId = User.ID + "_" + calendarListEntry.getId();
                    calendarType.calendarId = calendarId;
                    calendarType.calendarName = calendarListEntry.getSummary();
                    calendarType.calendarOwnerId = User.ID + "_Google";
                    calendarType.calendarOwnerName = "Google";

                    String calType = LoganSquare.serialize(calendarType);
                    final String url = URLs.INSERT_OR_UPDATE_USER_CALENDAR_TYPE;
                    String postBody = calType;

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + User.token)
                            .post(RequestBody.create(JSON, postBody))
                            .build();

                    okhttp3.Response response = mClient.newCall(request).execute();
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    Log.i("MakeRequestTask", response.body().string());
                    Log.i("MakeRequestTask", "update calendar type success");

                }
                pageToken = calendarList.getNextPageToken();
            } while (pageToken != null);
            return calendarIds;
        }

        /**
         * get all calendars events
         * @param calendars  a list of calendar ids
         * @return all events of all calendars
         */
        private List<String> importEvents(List<String> calendars) throws IOException{
            List<String> result = new ArrayList<String>();
            for (String calendar : calendars) {
                Events events = mService.events().list(calendar)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .setTimeZone("UTC")
                        .execute();

                final String calendarId = User.ID + "_" + calendar;
                transferOnlineCalendarDataToLocal(events.getItems(), calendarId);
                result.add(String.format("Import calendar %s success", calendar));
            }
            return result;
        }

        /**
         *
         * @param eventList
         */
        private void transferOnlineCalendarDataToLocal(List<Event> eventList, String calendarId) throws IOException {
            Map<String, JSONObject> events = com.itime.team.itime.bean.Events.rawEvents;
            JSONArray localEvents = new JSONArray();
            for (Event event : eventList) {
                if (!events.containsKey(event.getId())) {
                    // sync event
                    JSONObject object = toEventJSONObject(event, calendarId);
                    localEvents.put(object);
                }
            }
            syncEvents(localEvents);
            Log.i("MakeRequestTask", "syncEvents");
        }

        private JSONObject toEventJSONObject(Event event, String calendarId) {
            JSONObject object = new JSONObject();
            try {
                object.put("event_id", event.getId());
                object.put("user_id", User.ID);
                object.put("host_id", "");
                object.put("meeting_id", "");

                object.put("event_name", event.getSummary());
                object.put("event_comment", "");
                String start_datetime = "";
                if (event.getStart().getDateTime() != null) {
                    start_datetime = DateUtil.parseDateTimeString(event.getStart().getDateTime().toString());
                } else if (event.getStart().getDate() != null) {
                    start_datetime = DateUtil.parseDateString(event.getStart().getDate().toString());
                }
                object.put("event_starts_datetime", start_datetime);

                String end_datetime = "";
                if (event.getEnd().getDateTime() != null) {
                    end_datetime = DateUtil.parseDateTimeString(event.getEnd().getDateTime().toString());
                } else if (event.getEnd().getDate() != null) {
                    end_datetime = DateUtil.parseDateString(event.getEnd().getDate().toString());

                }
                object.put("event_ends_datetime", end_datetime);

                final String location = event.getLocation() == null ? "" : event.getLocation();
                object.put("event_venue_show", location);
                object.put("event_venue_location", location);
                object.put("event_venue_show_new", location);
                object.put("event_venue_location_new", location);

                object.put("event_repeats_type", EventUtil.ONE_TIME);

                object.put("event_latitude", "-37.799137");
                object.put("event_longitude", "144.96078");

                object.put("event_last_sug_dep_time", start_datetime);
                object.put("event_last_time_on_way_in_second", "0");
                object.put("event_last_distance_in_meter", "0");

                object.put("event_name_new", "");
                object.put("event_comment_new", "");

                object.put("event_starts_datetime_new", start_datetime);
                object.put("event_ends_datetime_new", end_datetime);


                object.put("event_repeats_type_new", EventUtil.ONE_TIME);

                object.put("event_latitude_new", "");
                object.put("event_longitude_new", "");

                object.put("event_last_sug_dep_time_new", start_datetime);
                object.put("event_last_time_on_way_in_second_new", "0");
                object.put("event_last_distance_in_meter_new", "0");

                object.put("is_meeting", 0);
                object.put("is_host", 0);

                object.put("meeting_status", "");
                object.put("meeting_valid_token", UUID.randomUUID().toString());

                object.put("event_repeat_to_date", end_datetime);


                object.put("is_long_repeat", 1);

                object.put("event_alert", User.defaultAlert);
                object.put("calendar_id", calendarId);

                object.put("event_last_update_datetime", "");
                object.put("if_deleted", 0);

                //punctual
                object.put("event_is_punctual", 1);
                object.put("event_is_punctual_new", 1);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.d("newEvent", object.toString());
            return object;
        }

        private void syncEvents(JSONArray events) throws IOException {
            final String url = URLs.SYNC;
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("user_id", User.ID);
                jsonObject.put("local_events", events);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String postBody = jsonObject.toString();

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + User.token)
                    .post(RequestBody.create(JSON, postBody))
                    .build();

            okhttp3.Response response = mClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Log.i("MakeRequestTask", response.body().string());
            Log.i("MakeRequestTask", "sync google events success");
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ImportGoogleCalendarActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    /*
    http://stackoverflow.com/questions/20555351/google-oauth2-re-authorization-is-missing-permissions-on-the-consent-page
    You should use the refresh token that you have stored in the future.  ...

    public static void RevokeAcess(String accessOrRefreshToken) throws ClientProtocolException, IOException
    {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/revoke?token="+accessOrRefreshToken);
        client.execute(post);
    }
    */
}
