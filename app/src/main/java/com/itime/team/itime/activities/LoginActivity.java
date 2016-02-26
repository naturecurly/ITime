package com.itime.team.itime.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.itime.team.itime.bean.Device;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.database.DeviceTableHelper;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.widget.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mac on 16/2/24.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, DataRequest{
    private Toast mToast;
    private ClearEditText mUsername, mPassword;
    private Button mLogin, mRegister;
    private Switch mRemember;
    private String mUsernameStr, mPasswordStr;
    private boolean mIsRemember;
    private boolean mCanLogin;

    private JsonManager mJsonManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsername = (ClearEditText) findViewById(R.id.login_username);
        mPassword = (ClearEditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login_login);
        mRemember = (Switch) findViewById(R.id.login_remember);
        mRegister = (Button) findViewById(R.id.login_register);

        init();
    }

    private void init(){
        getDeviceID();
        mJsonManager = new JsonManager();

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    /*
        If a device already has a device id in the database, then we just get the ID from the database,
        otherwise, creating a new device id via uuid and storing it.
     */
    private String getDeviceID(){
        String id;
        DeviceTableHelper dbHelper = new DeviceTableHelper(LoginActivity.this, "deviceidbase");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("device_id",
                new String[]{"id","device_id"}, "id=?", new String[]{"1"}, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            id = cursor.getString(cursor.getColumnIndex("device_id"));
        }else{
            id = "ANDROID_" + UUID.randomUUID().toString();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("device_id",id);
            db.insert("device_id", null, values);
        }
        Device.DeviceID = id;
        Log.i("deviceID", id);
        return id;
    }

    private void register(){
        if(isTextNull()){
            return;
        }
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("user_id",username);
            json.put("password",password);
            json.put("connect_token","");
            json.put("dev_id", Device.DeviceID);
            json.put("dev_token","");
            requestJSONObject(mJsonManager, json, URLs.REGISTER,
                    "register");
            handleJSON(mJsonManager);
            Log.i("json",json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doRegister(JSONObject json){
        try {
            if(json.get("result").toString().equals("success")){
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            }else if(json.get("result").toString().equals("user_id_existed")){
                showToast(getResources().getString(R.string.login_warning_exist));
                mUsername.setShakeAnimation();
            }else {
                Log.i("sign up log",json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo(){
        DeviceTableHelper dbHelper = new DeviceTableHelper(LoginActivity.this, "userbase");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("itime_user",
                new String[]{"id","user_id","password","last_login_time","remember"}, "id=?",
                new String[]{"1"}, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            mUsernameStr = cursor.getString(cursor.getColumnIndex("user_id"));
            mPasswordStr = cursor.getString(cursor.getColumnIndex("password"));
            String lastTime = cursor.getString(cursor.getColumnIndex("last_login_time"));
            mIsRemember = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("remember")));
            Log.i("adsasd",mUsernameStr+" " + mPasswordStr +" " + lastTime + " " + mIsRemember);
        }else{

        }
    }

    private boolean isValidTime(String date){

        return false;
    }

    private void postInfo(){
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("user_id",username);
            json.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isTextNull(){
        if (TextUtils.isEmpty(mUsername.getText())) {
            mUsername.setShakeAnimation();
            showToast(getResources().getString(R.string.login_warning_username));
        }else if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setShakeAnimation();
            showToast(getResources().getString(R.string.login_warning_password));
        }else{
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_login){
            if(!isTextNull()){
                postInfo();
            }
        }else if(v.getId() == R.id.login_register){
            register();
        }
    }

    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
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
                            if ((jsonObject = (JSONObject) map.get("register")) != null) {
                                doRegister(jsonObject);
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
}
