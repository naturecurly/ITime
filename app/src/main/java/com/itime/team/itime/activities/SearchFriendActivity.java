package com.itime.team.itime.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.adapters.SearchFriendListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac on 16/2/1.
 */
public class SearchFriendActivity extends AppCompatActivity implements DataRequest,SearchView.OnQueryTextListener, AdapterView.OnItemClickListener{
    private SearchView mSearch;
    private ListView mListView;
    private JsonManager mJsonManager;
    private ArrayList<HashMap<String, Object>> mListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_friend_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.meeting_search_friend_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init(){
        mSearch = (SearchView) findViewById(R.id.meeting_search_search);
        mListView = (ListView) findViewById(R.id.meeting_search_friendbyid);
        mJsonManager = new JsonManager();
        mSearch.setOnQueryTextListener(this);
        mListItem = new ArrayList<>();
        mListView.setOnItemClickListener(this);
    }

    private void searchFriend(String name){
        JSONObject jsonObject = new JSONObject();
        try {
            String url = URLs.SEARCH_FRIENDS;
            jsonObject.put("user_id",User.ID);
            jsonObject.put("name",name);
            requestJSONArray(mJsonManager, jsonObject, url, "search_friend");
            handleJSON(mJsonManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void adaptListView(JSONArray jsonArray){
        try {
            for(int i = 0; i < jsonArray.length(); i ++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", jsonObject.get("user_id"));
                map.put("ItemName", jsonObject.get("user_name"));
                String url = null;
                if(jsonObject.get("user_profile_picture") != null &&
                        !jsonObject.get("user_profile_picture").equals("")) {
                    url = URLs.PROFILE_PICTURE +
                            jsonObject.get("user_id") + "/profile_picture.png";
                }
                map.put("url", url);
                mListItem.add(map);
                SearchFriendListViewAdapter listItemAdapter = new SearchFriendListViewAdapter(this,mListItem);
                mListView.setAdapter(listItemAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleJSON(JsonManager manager) {
        MySingleton.getInstance(this).getRequestQueue().addRequestFinishedListener(
                new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        HashMap map;
                        while ((map = mJsonManager.getJsonQueue().poll()) != null) {
                            if ((jsonArray = (JSONArray) map.get("search_friend")) != null) {
                                adaptListView(jsonArray);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void requestJSONObject(JsonManager manager, JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url, jsonObject, this, tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager, JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, this, tag);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mListItem.clear();
        searchFriend(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
       // searchFriend(newText);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to add '" + mListItem.get(position).get("ItemID") + "' as your " +
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
                    object.put("friend_id",mListItem.get(position).get("ItemID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestJSONObject(mJsonManager,object,url,"adding_friend_request");
            }
        });
        builder.show();

    }
}
