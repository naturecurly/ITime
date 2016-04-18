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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.adapters.SearchFriendListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 16/2/1.
 * Users can search other users' information and add them as friends. The same friends will not be
 * added twice.
 */
public class SearchFriendActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener{
    private SearchView mSearch;
    private ListView mListView;
    private ArrayList<HashMap<String, Object>> mListItem;
    private ArrayList<String> mFriendIDs;

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
        mSearch.setOnQueryTextListener(this);
        mListItem = new ArrayList<>();
        mListView.setOnItemClickListener(this);
        mFriendIDs = getIntent().getStringArrayListExtra("friendIDs");
    }

    private void searchFriend(String name){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", User.ID);
            jsonObject.put("name", name);

            final String url = URLs.SEARCH_FRIENDS;
            Map<String, String> params = new HashMap();
            params.put("json", jsonObject.toString());
            JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    adaptListView(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(request);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isCurrentFriend((String) mListItem.get(position).get("ItemID"))){
            notAllowedSendRequest(position);
        }else {
            allowSendReuqest(position);
        }
    }

    // The same friends will not be added twice.
    private void allowSendReuqest(final int position){
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
                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", User.ID);
                    object.put("friend_id", mListItem.get(position).get("ItemID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String url = URLs.ADD_FRIEND_REQUEST;
                Map<String, String> params = new HashMap();
                params.put("json", object.toString());

                JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                Toast.makeText(getApplicationContext(),getResources().
                        getString(R.string.search_friend_added_friend_info),Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void notAllowedSendRequest(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("'" + mListItem.get(position).get("ItemID") + "' " +
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

    private boolean isCurrentFriend(String id){
        for(String listID : mFriendIDs){
            if(listID.equals(id)){
                return true;
            }
        }
        return false;
    }
}
