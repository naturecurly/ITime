package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mac on 16/2/26.
 */
public class CheckLoginActivity extends AppCompatActivity {
    private Animation mIn;
    private LinearLayout mMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklogin);
        mIn = AnimationUtils.loadAnimation(this, R.anim.alpha_light);
        mMain = (LinearLayout) findViewById(R.id.checklogin_main);
        mMain.startAnimation(mIn);
        final Intent localIntent=new Intent(this,LoginActivity.class);
        Timer timer=new Timer();
        TimerTask tast=new TimerTask()
        {
            @Override
            public void run(){
                startActivity(localIntent);
                finish();
            }
        };
        timer.schedule(tast, 1000);
    }
}
