package com.itime.team.itime.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itime.team.itime.R;

/**
 * Created by mac on 16/3/13.
 */
public class ReceiveMsgActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Intent intent = getIntent();
        Uri data = intent.getData();

        // Figure out what to do based on the intent type
        if (intent.getType().indexOf("image/") != -1) {
            // Handle intents with image data ...
        } else if (intent.getType().equals("text/plain")) {
            // Handle intents with text ...
            Log.i("hello","hello");
        }
    }
}
