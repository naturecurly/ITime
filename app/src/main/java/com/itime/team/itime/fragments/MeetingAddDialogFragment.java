package com.itime.team.itime.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.CaptureActivityPortraitOrientation;
import com.itime.team.itime.activities.SearchFriendActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.JsonObjectAuthRequest;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 15/12/15.
 * This Dialog Fragment provides many ways to add friends, including searching detail, contact number
 * , contact email, facebook, wechat.
 */
public class MeetingAddDialogFragment extends DialogFragment implements DataRequest{
    private ListView listView;
    private ArrayList<HashMap<String, Object>> listItem;
    private View addDialog;
    private JsonManager mJsonManager;
    private ArrayList<HashMap<String, Object>> mUserInfo;
    public static final String PACKAGE_NAME = "com.facebook.orca";

    private static String APPID;
    private IWXAPI api;

    public String invitationContent;

    MeetingAddDialogFragment meetingAddDialogFragment;


    public MeetingAddDialogFragment(ArrayList<HashMap<String, Object>> mUserInfo){
        this.mUserInfo = mUserInfo;
    }

    private void init(){
        String[] info = User.ID.split("@");
        String name = info[0];
        String address = info.length > 1 ? info[1] : "";
        String request = URLs.HEAD + "redirect_friend_request?id=" + name + "&address=" + address;
        invitationContent = new StringBuilder()
                .append("<p style='font-weight:bold;'>Hello, this is ")
                .append(User.ID)
                .append(", please click the link</p>")
                .append("<a>").append(request).append("</a>")
                .append("<p> to be my iTime firend. If you do not install the iTime yet, please click following " +
                        "link to find the App ")
                .append("Install iTime</p>")
                .toString();
        Log.i("info",invitationContent);
        meetingAddDialogFragment = this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        APPID = getString(R.string.wechat_app_id);
        regToWX();


        addDialog = inflater.inflate(R.layout.fragment_meeting_adddialog,container);
        listView = (ListView) addDialog.findViewById(R.id.meeting_add_listview);
        listItem = new ArrayList<HashMap<String, Object>>();
        mJsonManager = new JsonManager();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return addDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    private void initData(){
        init();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.search);
        map.put("ItemText", getString(R.string.meeting_dialog_search));
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.contacts);
        map.put("ItemText", getString(R.string.meeting_dialog_sms));
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.mail_i);
        map.put("ItemText", getString(R.string.meeting_dialog_email));
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.qrcode);
        map.put("ItemText", getString(R.string.meeting_dialog_qrcode));
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.facebook);
        map.put("ItemText", getString(R.string.meeting_dialog_facebook));
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.wechat);
        map.put("ItemText", getString(R.string.meeting_dialog_wechat));
        listItem.add(map);

        SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem,
                R.layout.fragment_meeting_adddoalog_listview,
                new String[] {"ItemImage","ItemText"},
                new int[] {R.id.meeting_add_image,R.id.meeting_add_text}
        );
        listView.setAdapter(listItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ // Search Friends
                    Intent intent = new Intent(getActivity(), SearchFriendActivity.class);
                    intent.putStringArrayListExtra("friendIDs",getFriendIDs());
                    startActivity(intent);
                }else if (position == 1){   // Add friend by SMS
//                    Intent intent = new Intent(getActivity(), PhoneContactActivity.class);
//                    startActivity(intent);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("sms_body", Html.fromHtml(invitationContent).toString());
                    startActivity(intent);
                }else if(position == 2){    // Add friend by Email
                    String mySbuject = "Add Friend";
                    String myCc = "cc";
                    Intent myIntent = new Intent(android.content.Intent.ACTION_SEND, Uri.fromParts("mailto", "", null));
                    myIntent.setType("text/html");
                    myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
                    myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
                    myIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(invitationContent));

                    startActivity(Intent.createChooser(myIntent, "mail"));
                }else if (position == 3){   // Add friend by QR code
                    IntentIntegrator.forSupportFragment(meetingAddDialogFragment)
                            .setCaptureActivity(CaptureActivityPortraitOrientation.class)
                            .setPrompt(getString(R.string.meeting_search_scan_qrcode))
                            .initiateScan();
                }else if (position == 4){   // Add friend by FaceBook
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(invitationContent).toString());
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.facebook.orca");
                    final Context c = getContext();
                    try {
                        startActivity(sendIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(getString(R.string.add_friend_facebook_install));
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setTitle(getString(R.string.warning));
                        builder.setNegativeButton("No", null);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openMessengerInPlayStore(c);
                            }
                        });
                        builder.show();
                    }
                }else if(position == 5){    // Add friend by WeChat
                    WXTextObject textObject = new WXTextObject(Html.fromHtml(invitationContent).toString());

                    WXMediaMessage msg = new WXMediaMessage();
                    msg.mediaObject = textObject;
                    msg.description = Html.fromHtml(invitationContent).toString();
                    msg.messageExt = "content";

                    SendMessageToWX.Req req = new SendMessageToWX.Req();
                    req.transaction = String.valueOf(System.currentTimeMillis());
                    req.message = msg;


                    api.sendReq(req);

                } else{

                }
                if( position !=3 ) MeetingAddDialogFragment.this.dismiss();
            }
        });
    }


    public void openMessengerInPlayStore(Context context) {
        try {
            startViewUri(context, "market://details?id=" + PACKAGE_NAME);
        } catch (ActivityNotFoundException anfe) {
            startViewUri(context, "http://play.google.com/store/apps/details?id=" + PACKAGE_NAME);
        }
    }
    private void startViewUri(Context context, String uri) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    // The same friends will not be added twice.
    private void allowSendReuqest(final String friendId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to add '" + friendId + "' as your " +
                "new iTIME friend?");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Add New Friend");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = URLs.ADD_FRIEND_REQUEST;
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", User.ID);
                    object.put("friend_id", friendId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectAuthRequest request = new JsonObjectAuthRequest(Request.Method.POST, url, object.toString(),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );
                MySingleton.getInstance(getContext()).addToRequestQueue(request);
            }
        });
        builder.show();
    }

    private void notAllowedSendRequest(final String friendId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("'" + friendId + "' " +
                "is already your friend");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("failed");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void notAllowAddYourself(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.add_friend_myself));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("failed");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from bar code capture activity
        final IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String friendId = intentResult.getContents();
            if (friendId.equals(User.ID)) {
                notAllowAddYourself();
            } else if (isCurrentFriend(friendId)) {
                notAllowedSendRequest(friendId);
            } else {
                allowSendReuqest(friendId);
            }
            // return so that it won't interfere other result processing below
            MeetingAddDialogFragment.this.dismiss();
            return;
        }

        if (resultCode == getActivity().RESULT_OK) {
            String result = data.getExtras().getString("result");
            String url = URLs.ADD_FRIEND_REQUEST;
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", User.ID);
                object.put("friend_id", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestJSONObject(mJsonManager, object, url, "adding_friend_request");
            //dismiss();
            MeetingAddDialogFragment.this.dismiss();
        }

    }

    private boolean isCurrentFriend(String id){
        for(HashMap<String, Object> info : mUserInfo){
            String friendId = (String) info.get("ItemID");
            if (friendId.equals(id)) {
                return true;
            }

        }
        return false;
    }

    private ArrayList<String> getFriendIDs(){
        ArrayList<String> ids = new ArrayList<>();
        for(HashMap<String, Object> info : mUserInfo){
            String id = (String) info.get("ItemID");
            ids.add(id);
        }
        return ids;
    }

    private void regToWX(){
        api = WXAPIFactory.createWXAPI(getContext(), APPID, true);
        api.registerApp(APPID);
    }

    @Override
    public void handleJSON(JsonManager manager) {

    }

    @Override
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url, jsonObject, getActivity(), tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, getActivity(),tag);
    }
}
