package com.itime.team.itime.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.UserTableHelper;
import com.itime.team.itime.utils.DateUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 16/2/26.
 */
public class CheckLoginActivity extends AppCompatActivity {
    private Animation mIn;
    private LinearLayout mMain;
    private String mUsernameStr, mPasswordStr, mLastLoginTime;
    private boolean mCanLogin;
    private boolean isFirstLogin;
    private boolean mIsRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklogin);
        mIn = AnimationUtils.loadAnimation(this, R.anim.alpha_light);
        mMain = (LinearLayout) findViewById(R.id.checklogin_main);
        mMain.startAnimation(mIn);
        final Intent LoginIntent = new Intent(this,LoginActivity.class);
        final Intent MainIntent = new Intent(this, MainActivity.class);

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
                    User.ID = mUsernameStr;
                    User.isRemembered = true;
                    startActivity(MainIntent);
                }else{
                    LoginIntent.putExtra("username",mUsernameStr);
                    LoginIntent.putExtra("password",mPasswordStr);
                    LoginIntent.putExtra("lastlogintime",mLastLoginTime);
                    LoginIntent.putExtra("remember",mIsRemember);

                    startActivity(LoginIntent);
                }
                finish();
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
            return true;
        }else{
            return false;
        }
    }

}
