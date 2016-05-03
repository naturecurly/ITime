package com.itime.team.itime.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.Device;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.DeviceTableHelper;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.ITimeGcmPreferences;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Weiwei Cai on 16/2/26.
 * This activity handles user Login function.
 *
 */
public class CheckLoginActivity extends AppCompatActivity{
    private Animation mIn;
    private LinearLayout mMain;
    private String mUsernameStr, mPasswordStr, mLastLoginTime;
    private boolean mCanLogin;
    private boolean isFirstLogin;
    private boolean mIsRemember;
    private Intent MainIntent;
    private Intent LoginIntent;
    private String mInviatedFriendID;

    private boolean hasAddFriendRequest;

    @Override
    protected void onDestroy() {
        Log.i(getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate");

        // This is a tool that can detect crashes of the App and send the details to developers.
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_checklogin);
        mIn = AnimationUtils.loadAnimation(this, R.anim.alpha_light);
        mMain = (LinearLayout) findViewById(R.id.checklogin_main);
        mMain.startAnimation(mIn);
        LoginIntent = new Intent(this,LoginActivity.class);
        MainIntent = new Intent(this, MainActivity.class);

        mUsernameStr = "";
        mPasswordStr = "";
        mLastLoginTime = "";
        hasAddFriendRequest = false;
        mInviatedFriendID = "";

        getInvitation();

        getUserInfo();

        // Welcome UI, after a second, user can enter into main UI.
        Timer timer=new Timer();
        TimerTask tast=new TimerTask()
        {
            @Override
            public void run(){
                if(canLogin()){
//                    User.ID = mUsernameStr;
//                    startActivity(MainIntent);
                    mCanLogin = true;
                }else{
                    // If cannot login directly, then enter into the UI which provides inputing
                    // password and username function.
                    LoginIntent.putExtra("username",mUsernameStr);
                    LoginIntent.putExtra("password",mPasswordStr);
                    LoginIntent.putExtra("lastlogintime",mLastLoginTime);
                    LoginIntent.putExtra("remember",mIsRemember);
                    LoginIntent.putExtra("invitation",mInviatedFriendID);
                    startActivity(LoginIntent);
                    finish();
                }
            }
        };
        timer.schedule(tast, 1000);
    }

    // Get Users' information from database.
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

    // Upload username, password and other relative information to the server.
    private void login(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPreferences.getString(ITimeGcmPreferences.REGISTRATION_TOKEN, "");
        JSONObject json = new JSONObject();
        try {
            json.put("user_id",mUsernameStr);
            json.put("password",mPasswordStr);
            json.put("connect_token","");
            json.put("dev_id", Device.DeviceID);
            json.put("dev_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.SIGN_IN;
        Map<String, String> params = new HashMap();
        params.put("json", json.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String result = null;
                try {
                    result = (String) response.get("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(result.equals("success")) {
                    try {
                        User.token = response.getString("connect_token");
                        Log.i("CheckLoginActivity", "connect_token: " + User.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    User.ID = mUsernameStr;
                    if (hasAddFriendRequest) {
                        MainIntent.putExtra("invitation",mInviatedFriendID);
                    }
                    startActivity(MainIntent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.login_warning_login_fail),Toast.LENGTH_SHORT);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleTimeout();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    // GetDeviceID from database, if it is the first time to login, then the ID should be "".
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

    // a Dialog to tell user that the internet is bad.
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


    private void getInvitation(){
        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();

        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = i_getvalue.getData();
            if(uri != null){
                hasAddFriendRequest = true;
                mInviatedFriendID = uri.getQueryParameter("id");
                String address = uri.getQueryParameter("address");
                final String id = mInviatedFriendID + "@" + address;
                mInviatedFriendID = id;
                if(uri != null){
                }
            }
        }
    }

    // When user is invited to make friends by clicking a link, then this method will handle this
    // situation.

}
