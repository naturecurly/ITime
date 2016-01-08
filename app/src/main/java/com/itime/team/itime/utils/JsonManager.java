package com.itime.team.itime.utils;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mac on 16/1/7.
 */
public class JsonManager {
    private Queue jsonQueue;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    public JsonManager(){
        jsonQueue = new ConcurrentLinkedQueue();
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Queue getJsonQueue(){
        return jsonQueue;
    }
    public void postForJsonObject(String url, JSONObject parameter, final Activity activity){
        String param = turnJSONintoString(url, parameter);
        MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        jsonQueue.add(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error_Volley_getJsonObject",error.toString());
                    }
                });
        jsonObjectRequest.setParameters(param);
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

    public void postForJsonObject(String url, JSONArray parameter,final Activity activity){

    }

    public void postForJsonArray(String url, JSONObject parameter, final Activity activity){
        String param = turnJSONintoString(url,parameter);
        MyJsonArrayRequest jsObjRequest = new MyJsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        jsonQueue.add(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error_Volley_getJsonArray",error.toString());
                    }
                });
        jsObjRequest.setParameters(param);
        MySingleton.getInstance(activity).addToRequestQueue(jsObjRequest);
    }

    public void postForJsonArray(String url, JSONArray parameter,final Activity activity){
    }

    private String turnJSONintoString(String url, JSONObject parameter){
        Map<String,String> params = new HashMap<>();
        params.put("json",parameter.toString());
        return appendParameter(url,params);
    }
    private String turnJSONArrayintoString(String url, JSONArray parameter){
        return null;
    }

    private String appendParameter(String url,Map<String,String> params){
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for(Map.Entry<String,String> entry:params.entrySet()){
            builder.appendQueryParameter(entry.getKey(),entry.getValue());
        }
        return builder.build().getQuery();
    }
}
