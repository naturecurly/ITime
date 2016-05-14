package com.itime.team.itime.views.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.fragments.MeetingFragment;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 16/1/12.
 * This adapter displays all friends of a user.
 */
public class DynamicListViewAdapter extends BaseAdapter implements DataRequest{
    private ArrayList<HashMap<String, Object>> list;
    private Activity context;
    private Boolean[] checkbox;
    private LinearLayout mLinearLayout;
    private ImageView[] invitedFriends;
    private Resources resources;
    private Drawable mBackgroud;
    private Animation mScale;
    private Animation mScaleRemove;
    private JsonManager mJsonManager;
    private MeetingFragment mMeetingFragment;

    private Map<String,Boolean> checkBoxKeeper;
    public DynamicListViewAdapter(Activity context, ArrayList<HashMap<String, Object>> listItem,
                                  LinearLayout linearLayout, Resources resources, MeetingFragment meetingFragment, HashMap<String,Boolean> checkBoxKeeper){
        list = listItem;
        this.context = context;
        mLinearLayout = linearLayout;
        invitedFriends = new ImageView[listItem.size()];
        this.resources = resources;
        this.checkbox = checkbox;
        mScale = AnimationUtils.loadAnimation(context, R.anim.meeting_scale);
        mScaleRemove = AnimationUtils.loadAnimation(context, R.anim.meeting_scale_remove);
        mJsonManager = new JsonManager();
        mMeetingFragment = meetingFragment;
        this.checkBoxKeeper = checkBoxKeeper;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final JsonManager jsonManager = new JsonManager();
        LayoutInflater inflater = context.getLayoutInflater();
        final View itemView = inflater.inflate(R.layout.fragment_meeting_listview, null);
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.meeting_profile);
        final CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.meeting_invite);
        TextView ID = (TextView) itemView.findViewById(R.id.meeting_id);
        TextView name = (TextView) itemView.findViewById(R.id.meeting_name);
        mBackgroud = itemView.getBackground();

        // If a user has set his profile picture, then show it. Otherwise, displaying the default
        // picture.
        if(list.get(position).get("url") != null) {
            jsonManager.postForImage(list.get(position).get("url").toString(), imageView, context);
        }
        final String textID = (String) list.get(position).get("ItemID");
        ID.setText(textID);
        final String textName = (String) list.get(position).get("ItemName");
        name.setText(textName);

        // Reset the friend searching view, if the checkbox is selected, then add its image to the
        // ScrollView.
        if(list.get(position).get("CheckBox") != null && (Boolean)list.get(position).get("CheckBox") == true){
//        if((Boolean)checkBoxKeeper.get(list.get(position).get("ItemID"))){
            checkBox.setChecked(true);
            mLinearLayout.removeAllViews();
            int index = 0;
            for(HashMap<String, Object> map : list) {
                if(map.get("CheckBox") != null && (Boolean)map.get("CheckBox") == true)
                    addFriendToScrollView(index, jsonManager, checkBox);
                index ++;
            }
        }

        // Change the status when the checkbox is changed.
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    list.get(position).put("CheckBox", true);
                    addFriendToScrollView(position, jsonManager, checkBox);
                    checkBoxKeeper.put((String) list.get(position).get("ItemID"),true);
                } else {
                    list.get(position).put("CheckBox", false);
                    deleteFriendFromScrollView(invitedFriends[position]);
                    checkBoxKeeper.put((String) list.get(position).get("ItemID"), false);
                }
            }
        });

        // Clicking the view also selects the friends rather than can only click the checkbox.
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    checkBoxKeeper.put((String) list.get(position).get("ItemID"), false);
                } else {
                    checkBox.setChecked(true);
                    checkBoxKeeper.put((String) list.get(position).get("ItemID"), true);
                }
            }
        });

        //Long Click the item can delete a friend.
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete " + textName);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Warning");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFriend(textID);
                    }
                });
                builder.show();
                return false;
            }
        });
        return itemView;
    }

    private void deleteFriend(String friendID){
        try {
            JSONObject json = new JSONObject();
            json.put("user_id", User.ID);
            json.put("friend_id",friendID);
            final String url = URLs.DELETE_FRIEND;
            Map<String, String> params = new HashMap();
            params.put("json", json.toString());

            JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.get("result").toString().equals("success")){
                            mMeetingFragment.initListView();
                            checkBoxKeeper.clear();
                            mLinearLayout.removeAllViews();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFriendToScrollView(final int position, JsonManager jsonManager, final CheckBox checkBox){
        final ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams mDefaultImagePara = new LinearLayout.LayoutParams((int) resources.getDimension(R.dimen.meeting_search_picture),
                (int) resources.getDimension(R.dimen.meeting_search_picture));
        imageView.setLayoutParams(mDefaultImagePara);
        if(list.get(position).get("url") != null && !list.get(position).get("url").equals("")) {
            jsonManager.postForImage(list.get(position).get("url").toString(), imageView, context);
        }else{
            imageView.setImageResource(R.drawable.default_profile_image);
        }
        imageView.startAnimation(mScale);
        mLinearLayout.addView(imageView);

        // Clicking the image means delete it from the view, and also reset the checkbox.
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearLayout.removeView(imageView);
                checkBox.setChecked(false);
                checkBoxKeeper.put((String) list.get(position).get("ItemID"), false);
            }
        });
        invitedFriends[position] = imageView;
    }

    private void deleteFriendFromScrollView(ImageView imageView){
        mLinearLayout.removeView(imageView);
    }

    @Override
    public void handleJSON(JsonManager manager) {
        MySingleton.getInstance(context).getRequestQueue().addRequestFinishedListener(
                new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        HashMap map;
                        while ((map = mJsonManager.getJsonQueue().poll()) != null) {
                            if ((jsonObject = (JSONObject) map.get("delete_friend")) != null) {
                                try {
                                    if(jsonObject.get("result").toString().equals("success")){
                                        mMeetingFragment.initListView();
                                        checkBoxKeeper.clear();
                                        mLinearLayout.removeAllViews();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
        );
    }

    @Override
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url, jsonObject, context, tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, context, tag);
    }

}
