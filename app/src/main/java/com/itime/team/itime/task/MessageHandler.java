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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.MeetingDetaiHostlActivity;
import com.itime.team.itime.activities.MeetingDetailActivity;
import com.itime.team.itime.bean.MeetingInfo;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.model.utils.MessageType;
import com.itime.team.itime.utils.AlertUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Xuhui Chen (yorkfine) on 21/04/16.
 *
 * Do not store the pass in context as a member of this class!
 */
public class MessageHandler {

    public static void handleMessage(Context context, ParcelableMessage message) {
        if (!message.userId.equals(User.ID)) {
            return;
        }

        MessageType messageType = message.messageType;
        String messageTitle = message.messageTitle;
        String messageBody = message.messageBody;

        String messageId = message.messageId;

        switch (messageType) {
            case MEETING_STATUS_CHANGE:
            case YOU_SENT_A_NEW_INVITATION:
            case SOMEONE_QUIT_MEETING:
                hostReceiveMeetingMessage(context, message);
                break;

            case NEW_MEETING_INVITATION:
            case MEETING_UPDATED:
            case MEETING_UPDATED_CONFIRM:
            case MEETING_UPDATED_RESET:
                memberReceiveMeetingMessage(context, message);
                break;

            case NEW_FRIEND_REQUEST:
                receiveFriendRequestMessage(context, message);
                break;

            case MAKE_FRIEND_SUCCESS_INFO:
                AlertUtil.showMessageDialog(context, messageTitle, messageBody);
                User.hasNewFriend = true;
                // load new friends
                break;
            case MAKE_FRIEND_FAIL_INFO:
                AlertUtil.showMessageDialog(context, messageTitle, messageBody);
                break;

            case REJECT_FRIEND_REQUEST:
                AlertUtil.showMessageDialog(context, messageTitle, messageBody);
                break;

            case CONFIRMED_NEW_INVITATION:
                confirmedNewInvitationMessage(context, message);
                break;

            case SOMEONE_CANCEL_THE_MEETING:
                // delete event via meeting id
                break;

            case OTHER_DEVICE_LOGIN:
                break;

            case ALL_PEOPLE_ACCEPTED_THE_MEETING:
                allPeopleAcceptedMeetingMessage(context, message);
                break;

            default:
                break;
        }
    }

    public static void hostReceiveMeetingMessage(final Context context, final ParcelableMessage message) {
        final JSONObject event =  EventUtil.findEventById(message.meetingId);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.show_detail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String calendarID = null;
                        String alert = null;
                        // pass meeting information to meeting detail
                        Intent intent = new Intent(context, MeetingDetaiHostlActivity.class);
                        intent.putExtra(MeetingDetailActivity.ARG_MEETING_ID, message.meetingId);
                        intent.putExtra("event_id", message.meetingId);
                        if (event != null) {
                            try {
                                calendarID = event.getString("calendar_id");
                                alert = event.getString("event_alert");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        intent.putExtra("calendar_id",calendarID);
                        intent.putExtra("event_alert",alert);
                        context.startActivity(intent);
                    }
                });

        // perform some actions before showing the dialog

        builder.show();
    }

    public static void memberReceiveMeetingMessage(final Context context, final ParcelableMessage message) {
        final JSONObject event =  EventUtil.findEventById(message.meetingId);

        if(!message.ifRead){
            createEvent(context, message.meetingId);
            User.hasNewMeeting = true;

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.show_detail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String calendarID = null;
                        String alert = null;
                        // pass meeting information to meeting detail
                        Intent intent = new Intent(context, MeetingDetailActivity.class);
                        intent.putExtra(MeetingDetailActivity.ARG_MEETING_ID, message.meetingId);
                        intent.putExtra("event_id", message.meetingId);

                        if (event != null) {
                            try {
                                calendarID = event.getString("calendar_id");
                                alert = event.getString("event_alert");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            calendarID = UserUtil.getLastUserCalendarId(context);
                            alert = UserUtil.getDefaultAlert(context);
                        }
                        intent.putExtra("calendar_id",calendarID);
                        intent.putExtra("event_alert",alert);
                        context.startActivity(intent);
                    }
                })
                .setNeutralButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement your accept logic
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("meeting_id", message.meetingId);
                            jsonObject.put("user_id", User.ID);
                            jsonObject.put("meeting_status", "Accept");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String url = URLs.MEETING_STATUS_CHANGE;
                        Map<String, String> params = new HashMap();
                        params.put("json", jsonObject.toString());

                        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (!response.getString("result").equals("success")){
                                        Toast.makeText(context, context.getString(R.string.time_out), Toast.LENGTH_LONG);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        MySingleton.getInstance(context).addToRequestQueue(request);

                    }
                });

        // perform some actions before showing the dialog

        builder.show();
    }

    private static void createEvent(final Context context, final String meetingID){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", meetingID);
            jsonObject.put("user_id", User.ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.LOAD_MEETING_INFO;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleMeetingInfo(context,meetingID,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }

    private static void handleMeetingInfo(Context context, String meetingID,JSONObject json){

        MeetingInfo mMeetingInfo = new MeetingInfo();
        try {
            mMeetingInfo.setLatitude("");
            mMeetingInfo.setNewLocation(json.getString("event_venue_location_new"));
            mMeetingInfo.setNewName(json.getString("event_name_new"));
            mMeetingInfo.setStatus(json.getString("meeting_status"));
            mMeetingInfo.setNewRepeat(json.getString("event_repeats_type_new"));
            mMeetingInfo.setLongitude("");
            mMeetingInfo.setLocation(json.getString("event_venue_location"));
            mMeetingInfo.setNewPunctual(json.getBoolean("event_is_punctual_new"));
            mMeetingInfo.setComment(json.getString("event_comment"));
            mMeetingInfo.setPunctual(json.getBoolean("event_is_punctual"));
            mMeetingInfo.setEnd(json.getString("event_ends_datetime"));
            mMeetingInfo.setId(json.getString("meeting_id"));
            mMeetingInfo.setVenue(json.getString("event_venue_show"));
            mMeetingInfo.setHostID(json.getString("host_id"));
            mMeetingInfo.setRepeat(json.getString("event_repeats_type"));
            mMeetingInfo.setNewStart(json.getString("event_starts_datetime_new"));
            mMeetingInfo.setNewVenue(json.getString("event_venue_show_new"));
            mMeetingInfo.setStart(json.getString("event_starts_datetime"));
            mMeetingInfo.setNewLongitude("");
            mMeetingInfo.setName(json.getString("event_name"));
            mMeetingInfo.setNewLatitude("");
            mMeetingInfo.setToken(json.getString("meeting_valid_token"));
            mMeetingInfo.setNewEnd(json.getString("event_ends_datetime_new"));
            mMeetingInfo.setNewComment(json.getString("event_comment_new"));

            postEvent(context,meetingID,mMeetingInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static void postEvent(Context context, String meetingID,MeetingInfo meetingInfo) {
        String startDateForPost = DateUtil.getDateStringFromCalendar(meetingInfo.getNewStart());
        String endDateForPost = DateUtil.getDateStringFromCalendar(meetingInfo.getNewEnd());
        String comment = meetingInfo.getNewComment();
        String name = meetingInfo.getNewName();
        String punctual = "false";
        String repeative = meetingInfo.getRepeat();


        String address = meetingInfo.getNewVenue();
        String location = meetingInfo.getLocation();

//        String location = mAddress.equals("") ? getString(R.string.post_null) : mAddress;
//        String showLocation = address[0].equals("") ? getString(R.string.post_null) : address[0];

        JSONObject object = new JSONObject();
        try {
            object.put("event_id", meetingID);
            object.put("user_id", User.ID);
            object.put("host_id", meetingInfo.getHostID());
            object.put("meeting_id", meetingID);

            object.put("event_name", name.equals("") ? context.getString(R.string.new_meeting) : name);
            object.put("event_comment", comment);
            object.put("event_starts_datetime", startDateForPost);
            object.put("event_ends_datetime", endDateForPost);

            object.put("event_venue_show", address);
            object.put("event_venue_location", location);

            object.put("event_repeats_type", repeative);

            object.put("event_latitude", 0);
            object.put("event_longitude", 0);

            object.put("event_last_sug_dep_time", startDateForPost);
            object.put("event_last_time_on_way_in_second", "0");
            object.put("event_last_distance_in_meter", "0");

            object.put("event_name_new", name);
            object.put("event_comment_new", comment);

            object.put("event_starts_datetime_new", startDateForPost);
            object.put("event_ends_datetime_new", endDateForPost);

            object.put("event_venue_show_new", address);
            object.put("event_venue_location_new", location);

            object.put("event_repeats_type_new", repeative);
            //punctual
            object.put("event_latitude_new", 0);
            object.put("event_longitude_new", 0);

            object.put("event_last_sug_dep_time_new", startDateForPost);
            object.put("event_last_time_on_way_in_second_new", "0");
            object.put("event_last_distance_in_meter_new", "0");

            object.put("is_meeting", 1);
            object.put("is_host", 1);

            object.put("meeting_status", "");
            object.put("meeting_valid_token", UUID.randomUUID().toString());

            object.put("event_repeat_to_date", endDateForPost);


            if (!repeative.equals("One-time event")) {
                object.put("is_long_repeat", 1);
            } else {
                object.put("is_long_repeat", 0);
            }
            object.put("event_alert", UserUtil.getDefaultAlert(context));
            object.put("calendar_id", UserUtil.getLastUserCalendarId(context));

            object.put("event_last_update_datetime", DateUtil.getDateStringFromCalendarGMT(Calendar.getInstance()));
            object.put("if_deleted", 0);

            object.put("event_is_punctual", punctual);
            object.put("event_is_punctual_new", punctual);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String url = URLs.SYNC;
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(object);
        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("local_events", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);

    }


    private static void receiveFriendRequestMessage(final Context context, final ParcelableMessage message) {
        final String friendId = message.relevantId;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement your accept friend logic
                        User.hasNewFriend = true;
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_id", User.ID);
                            jsonObject.put("friend_id", friendId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String url = URLs.SEND_AGREE_FRIEND_REQUEST;
                        Map<String, String> params = new HashMap();
                        params.put("json", jsonObject.toString());

                        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (!response.getString("result").equals("success")){
                                        Toast.makeText(context, context.getString(R.string.time_out), Toast.LENGTH_LONG);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        MySingleton.getInstance(context).addToRequestQueue(request);
                    }
                })
                .setNeutralButton(R.string.reject, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement your reject friend logic
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_id", User.ID);
                            jsonObject.put("friend_id", friendId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String url = URLs.SEND_REJECT_FRIEND_REQUEST;
                        Map<String, String> params = new HashMap();
                        params.put("json", jsonObject.toString());

                        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (!response.getString("result").equals("success")){
                                        Toast.makeText(context, context.getString(R.string.time_out), Toast.LENGTH_LONG);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        MySingleton.getInstance(context).addToRequestQueue(request);
                    }
                });

        // perform some actions before showing the dialog

        builder.show();
    }

    private static void confirmedNewInvitationMessage(final Context context, final ParcelableMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.show_detail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // pass meeting information to meeting detail
                        Intent intent = new Intent(context, MeetingDetailActivity.class);
                        intent.putExtra(MeetingDetailActivity.ARG_MEETING_ID, message.meetingId);
                        context.startActivity(intent);
                    }
                });

        // No need to perform any action before showing dialog

        builder.show();
    }

    /**
     * It seems to be same as hostReceiveMeetingMessage
     * @param context
     * @param message
     */
    public static void allPeopleAcceptedMeetingMessage(final Context context, final ParcelableMessage message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.show_detail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // pass meeting information to meeting detail
                        Intent intent = new Intent(context, MeetingDetailActivity.class);
                        intent.putExtra(MeetingDetailActivity.ARG_MEETING_ID, message.meetingId);
                        context.startActivity(intent);
                    }
                });

        // perform some actions before showing the dialog

        builder.show();
    }



}
