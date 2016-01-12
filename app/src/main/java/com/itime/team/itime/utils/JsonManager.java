package com.itime.team.itime.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.itime.team.itime.activities.R;

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
    private Queue<HashMap> jsonQueue;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    public JsonManager(){
        jsonQueue = new ConcurrentLinkedQueue<HashMap>();
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public Queue<HashMap> getJsonQueue(){
        return jsonQueue;
    }
    public void postForJsonObject(String url, JSONObject parameter, final Activity activity, final String tag){
        String param = turnJSONintoString(url, parameter);
        MyJsonObjectRequest jsonObjectRequest = new MyJsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HashMap<String, JSONObject> map = new HashMap<>();
                        map.put(tag, response);
                        jsonQueue.add(map);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error_Volley_getJsonObject",tag + " : " + error.toString());
                    }
                });
        jsonObjectRequest.setParameters(param);
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

//    public void postForJsonObject(String url, JSONArray parameter,final Activity activity){}

    public void postForJsonArray(String url, JSONObject parameter, final Activity activity, final String tag){
        String param = turnJSONintoString(url,parameter);
        MyJsonArrayRequest jsObjRequest = new MyJsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        HashMap<String, JSONArray> map = new HashMap<>();
                        map.put(tag, response);
                        jsonQueue.add(map);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error_Volley_getJsonArray",tag + " : " + error.toString());
                    }
                });
        jsObjRequest.setParameters(param);
        MySingleton.getInstance(activity).addToRequestQueue(jsObjRequest);
    }

//    public void postForJsonArray(String url, JSONArray parameter,final Activity activity){}

    public void postForImage(String url, final ImageView imageView, final Activity activity){
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.drawable.default_profile_image);
                    }
                });
        MySingleton.getInstance(activity).addToRequestQueue(request);
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
