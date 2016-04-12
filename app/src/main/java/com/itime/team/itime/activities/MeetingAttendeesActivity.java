package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.adapters.AttendeesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/4/12.
 */
public class MeetingAttendeesActivity extends AppCompatActivity{
    private ListView mAttendees;
    private AttendeesAdapter mAdapter;
    private ArrayList<HashMap<String,String>> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_detail_attendees);
        init();
        loadAttendees();
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.meeting_attendees_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.meeting_attendees_title);
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

        mAttendees = (ListView) findViewById(R.id.meeting_detail_attendee_listview);
        mItems = new ArrayList<>();
    }

    private void setAdapter(JSONArray array){
        try {
            for(int i = 0; i < array.length(); i ++){
                JSONObject json =  array.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("name", json.getString("user_name"));
                map.put("id", json.getString("user_id"));
                map.put("isFriend", json.getString("is_friend"));
                map.put("status", json.getString("status"));
                mItems.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAdapter = new AttendeesAdapter(this,mItems);
        mAttendees.setAdapter(mAdapter);
    }

    private void loadAttendees() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", "23C5D415-91E1-4E50-BDF9-26AECFE28506");
            jsonObject.put("user_id", "1@2.com");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.LOAD_MEETING_ATTENDANTS;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                setAdapter(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
