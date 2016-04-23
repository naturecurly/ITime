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
import com.itime.team.itime.activities.MeetingDetailActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.model.ParcelableMessage;
import com.itime.team.itime.model.utils.MessageType;
import com.itime.team.itime.utils.AlertUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    public static void memberReceiveMeetingMessage(final Context context, final ParcelableMessage message) {
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

    private static void receiveFriendRequestMessage(Context context, ParcelableMessage message) {
        String friendId = message.relevantId;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message.messageTitle)
                .setMessage(message.messageBody)
                .setNegativeButton(R.string.later, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement your accept friend logic
                    }
                })
                .setNeutralButton(R.string.reject, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement your reject friend logic
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
