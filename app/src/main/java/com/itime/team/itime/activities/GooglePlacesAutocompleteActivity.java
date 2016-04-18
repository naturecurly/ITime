package com.itime.team.itime.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.URLConnectionUtil;
import com.itime.team.itime.views.adapters.AutoCompleteAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Weiwei Cai on 16/3/6.
 * The activity UI contains a ListView which get Locations from Google Map, the locations are based
 * on users' query.
 */
public class GooglePlacesAutocompleteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static String API_KEY;
    private SearchView mSearch;

    private Activity myActivity;

    private ListView mListView;
    private ArrayList<String> mAddresses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_auto_complete);

        Toolbar toolbar = (Toolbar) findViewById(R.id.auto_complete_bar);
        setSupportActionBar(toolbar);
        setTitle(R.string.auto_titile);
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
        API_KEY = getResources().getString(R.string.google_web_id);
        mListView = (ListView) findViewById(R.id.auto_complete_listview);
        mSearch = (SearchView) findViewById(R.id.auto_complete_search);
        mSearch.setOnQueryTextListener(this);
        mAddresses = new ArrayList<>();
        myActivity = this;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                TextView textView = (TextView) view.findViewById(R.id.auto_complete_listview_item);
                intent.putExtra("address", textView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    // the parameter is the query, this function will get location names from Google Map based on
    // the parameter. Here just support Aus location, if want to support more, then set the parameter
    // &components=country:XXX
    private void postData(String input){
        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?key=" + API_KEY);
        sb.append("&components=country:aus");
        sb.append("&input=" + URLConnectionUtil.encode(input));
        String url = sb.toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray predsJsonArray = response.getJSONArray("predictions");
                            for(int i = 0; i < predsJsonArray.length(); i ++){
                                mAddresses.add(predsJsonArray.getJSONObject(i).getString("description"));
                            }
                            AutoCompleteAdapter adapter = new AutoCompleteAdapter(myActivity,mAddresses);
                            mListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("rerror",error.toString());

                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void setListView(String query){
        mAddresses.clear();
        postData(query);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent();
        intent.putExtra("address", mSearch.getQuery().toString());
        setResult(RESULT_OK, intent);
        finish();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setListView(newText);
        return false;
    }
}



