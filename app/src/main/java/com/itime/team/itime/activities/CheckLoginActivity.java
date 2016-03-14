package com.itime.team.itime.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.Device;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.DeviceTableHelper;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 16/2/26.
 */
public class CheckLoginActivity extends AppCompatActivity implements DataRequest{
    private Animation mIn;
    private LinearLayout mMain;
    private String mUsernameStr, mPasswordStr, mLastLoginTime;
    private boolean mCanLogin;
    private boolean isFirstLogin;
    private boolean mIsRemember;
    private JsonManager mJsonManager;
    private Intent MainIntent;
    private Intent LoginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_checklogin);
        mIn = AnimationUtils.loadAnimation(this, R.anim.alpha_light);
        mMain = (LinearLayout) findViewById(R.id.checklogin_main);
        mMain.startAnimation(mIn);
        LoginIntent = new Intent(this,LoginActivity.class);
        MainIntent = new Intent(this, MainActivity.class);
        mJsonManager = new JsonManager();

        mUsernameStr = "";
        mPasswordStr = "";
        mLastLoginTime = "";

        getUserInfo();


        Timer timer=new Timer();
        TimerTask tast=new TimerTask()
        {
            @Override
            public void run(){
                if(canLogin()){
//                    User.ID = mUsernameStr;
//                    startActivity(MainIntent);
                }else{
                    LoginIntent.putExtra("username",mUsernameStr);
                    LoginIntent.putExtra("password",mPasswordStr);
                    LoginIntent.putExtra("lastlogintime",mLastLoginTime);
                    LoginIntent.putExtra("remember",mIsRemember);

                    startActivity(LoginIntent);
                    finish();
                }
            }
        };
        timer.schedule(tast, 1000);
    }

    private void getUserInfo(){
        UserTableHelper dbHelper = new UserTableHelper(CheckLoginActivity.this, "userbase1");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("itime_user",
                new String[]{"id","user_id","password","last_login_time","remember"}, "id=?",
                new String[]{"1"}, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            mUsernameStr = cursor.getString(cursor.getColumnIndex("user_id"));
            mPasswordStr = cursor.getString(cursor.getColumnIndex("password"));
            mLastLoginTime = cursor.getString(cursor.getColumnIndex("last_login_time"));
            mIsRemember = cursor.getString(cursor.getColumnIndex("remember")).equals("1") ? true : false;
        }else{
            isFirstLogin = true;
        }
    }

    //decide whether can login directly
    private boolean canLogin(){
        //This valid time is set to be 3 days
        if(DateUtil.diffDate(mLastLoginTime) >  3 * 24 * 60 * 60 * 1000){
            return false;
        }

        if(mIsRemember){
            if(getDeviceID().equals(""))
                return false;
            login();
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void login(){
        JSONObject json = new JSONObject();
        try {
            json.put("user_id",mUsernameStr);
            json.put("password",mPasswordStr);
            json.put("connect_token","");
            json.put("dev_id", Device.DeviceID);
            json.put("dev_token", "");
            requestJSONObject(mJsonManager, json, URLs.SIGN_IN,
                    "login");
            handleJSON(mJsonManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDeviceID(){
        String id = "";
        DeviceTableHelper dbHelper = new DeviceTableHelper(CheckLoginActivity.this, "deviceidbase");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("device_id",
                new String[]{"id", "device_id"}, "id=?", new String[]{"1"}, null, null, null, null);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            id = cursor.getString(cursor.getColumnIndex("device_id"));
        }
        Device.DeviceID = id;
        dbHelper.close();
        db.close();
        return id;
    }

    private void handleTimeout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_login_timeout));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.warning);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void handleJSON(final JsonManager manager) {
        MySingleton.getInstance(this).getRequestQueue().addRequestFinishedListener(
                new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        HashMap map;
                        VolleyError error;
                        try {
                            while ((map = mJsonManager.getJsonQueue().poll()) != null) {
                                if ((jsonObject = (JSONObject) map.get("login")) != null) {
                                        String result = (String) jsonObject.get("result");
                                    if(result.equals("success")) {
                                        User.ID = mUsernameStr;
                                        //User.isRemembered = true;
                                        startActivity(MainIntent);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(),
                                                getString(R.string.login_warning_login_fail),Toast.LENGTH_SHORT);
                                    }
                                }
                            }
                            while ((map = mJsonManager.getErrorQueue().poll()) != null) {
                                if((error = (VolleyError) map.get("login")) != null){
                                    handleTimeout();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
