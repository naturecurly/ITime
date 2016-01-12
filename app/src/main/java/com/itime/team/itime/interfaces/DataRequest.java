package com.itime.team.itime.interfaces;

import com.itime.team.itime.utils.JsonManager;

import org.json.JSONObject;

/**
 * Created by mac on 16/1/12.
 */
public interface DataRequest {
    public void handleJSON(JsonManager manager);
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag);
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag);
}
