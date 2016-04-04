package com.itime.team.itime.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.Device;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.DeviceTableHelper;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.task.PreferenceTask;
import com.itime.team.itime.task.UserTask;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.widget.ClearEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mac on 16/2/24.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener{
    private Toast mToast;
    private ClearEditText mUsername, mPassword;
    private Button mLogin, mRegister;
    private Switch mRemember;
    private String mUsernameStr, mPasswordStr, mLastLoginTime;
    private boolean mIsRemember;

    private CallbackManager callbackManager;
    private Intent mMainIntent;

    private String mInviatedFriendID;

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

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        Intent intent = getIntent();
        mUsernameStr = intent.getStringExtra("username");
        mPasswordStr = intent.getStringExtra("password");
        mLastLoginTime = intent.getStringExtra("lastlogintime");
        mIsRemember = intent.getBooleanExtra("remember", false);
        mInviatedFriendID = intent.getStringExtra("invitation");

        mMainIntent = new Intent(this, MainActivity.class);
        mMainIntent.putExtra("invitation", mInviatedFriendID);


        setTexts();

        linkFaceBook();

    }

    private void setTexts(){
        mUsername.setText(mUsernameStr);
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
                new String[]{"id", "device_id"}, "id=?", new String[]{"1"}, null, null, null, null);
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
        dbHelper.close();
        db.close();
        return id;
    }

    private void register(){
        if(isTextNull()){
            return;
        }
        mUsernameStr = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        if(isUsernameLegal(mUsernameStr) && isPasswordLeagal(password)) {
            final JSONObject json = new JSONObject();
            try {
                json.put("user_id", mUsernameStr);
                json.put("password", password);
                json.put("connect_token", "");
                json.put("dev_id", Device.DeviceID);
                json.put("dev_token", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String url = URLs.REGISTER;
            Map<String, String> params = new HashMap();
            params.put("json", json.toString());

            JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    doRegister(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handleTimeout();
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(request);

        }
    }

    private void doRegister(JSONObject json){
        try {
            if(json.get("result").toString().equals("success")){
                if(isFirstLogin()){
                    insertUserTable();
                }else {
                    updateUserTable();
                }
                User.ID = mUsernameStr;
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

    /*
        refresh item user table
     */
    private void updateUserTable(){
        UserTableHelper dbHelper = new UserTableHelper(LoginActivity.this, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", mUsername.getText().toString());
        values.put("password", mPassword.getText().toString());
        values.put("last_login_time", DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        values.put("remember", mRemember.isChecked());
        db.update("itime_user", values, "id=?", new String[]{"1"});
        dbHelper.close();
        db.close();
    }

    private void updateUserTable(String username,String password){
        UserTableHelper dbHelper = new UserTableHelper(LoginActivity.this, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", username);
        values.put("password", password);
        values.put("last_login_time", DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        values.put("remember", mRemember.isChecked());
        db.update("itime_user", values, "id=?", new String[]{"1"});
        dbHelper.close();
        db.close();
    }

    private void insertUserTable(){
        UserTableHelper dbHelper = new UserTableHelper(LoginActivity.this, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",1);
        values.put("user_id", mUsername.getText().toString());
        values.put("password", mPassword.getText().toString());
        values.put("last_login_time", DateUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
        values.put("remember", mRemember.isChecked());
        db.insert("itime_user", null, values);
        dbHelper.close();
        db.close();
    }

    private boolean isFirstLogin(){
        UserTableHelper dbHelper = new UserTableHelper(LoginActivity.this, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("itime_user",
                new String[]{"id","user_id"}, "id=?", new String[]{"1"}, null, null, null, null);
        if(cursor.getCount() > 0){
            dbHelper.close();
            db.close();
            return false;
        }else {
            dbHelper.close();
            db.close();
            return true;
        }
    }



    private void login(){
        if(isTextNull()){
            return;
        }
        mUsernameStr = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        final JSONObject json = new JSONObject();
        try {
            json.put("user_id",mUsernameStr);
            json.put("password",password);
            json.put("connect_token","");
            json.put("dev_id", Device.DeviceID);
            json.put("dev_token", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.SIGN_IN;
        Map<String, String> params = new HashMap();
        params.put("json", json.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                doLogin(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleTimeout();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void doLogin(JSONObject json){
        try {
            if(json.get("result").toString().equals("success")){
                if(isFirstLogin()){
                    insertUserTable();
                }else {
                    updateUserTable();
                }
                //Intent intent = new Intent(this, MainActivity.class);

                User.ID = mUsernameStr;

                PreferenceTask preferenceTask = PreferenceTask.getInstance(getApplicationContext());
                preferenceTask.syncPreference(User.ID, null, null);
                UserTask userTask = UserTask.getInstance(getApplicationContext());
                userTask.loadUserInfo(User.ID, null);

                startActivity(mMainIntent);
                finish();
            }else if(json.get("result").toString().equals("fail")){
                showToast(getResources().getString(R.string.login_warning_login_fail));
                mUsername.setShakeAnimation();
                mPassword.setShakeAnimation();
            } else{
                Log.i("login_error",json.toString());
            }
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

    private boolean isUsernameLegal(String username){
        if(username.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")){
            return true;
        }else{
            mUsername.setShakeAnimation();
            Toast.makeText(getApplicationContext(),getString(R.string.login_warning_username_illegal),Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isPasswordLeagal(String password){
        if(password.length() < 6){
            Toast.makeText(getApplicationContext(),getString(R.string.login_warning_password_illegal),Toast.LENGTH_LONG).show();
            mPassword.setShakeAnimation();
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_login){
            login();
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

    private void handleTimeout(){
        Toast.makeText(getApplicationContext(),getString(R.string.check_login_timeout)
                ,Toast.LENGTH_LONG).show();
    }

    private void linkFaceBook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {

                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            redirect();
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    redirect();
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });
    }

    private void redirect(){
        mUsernameStr = Profile.getCurrentProfile().getId();
        User.ID = mUsernameStr;
        updateUserTable(mUsernameStr, "");
        startActivity(mMainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

}
