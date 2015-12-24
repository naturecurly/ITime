package com.itime.team.itime.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itime.team.itime.views.widget.ImageButton_Text_Image;

/**
 * Created by mac on 15/12/22.
 */
public class ProfileActivity extends Activity {
    private LinearLayout mProfilePhotoLayout;
    private ImageButton_Text_Image mPhoto;
    private Button mBack;
    private View profile;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.i("Profile","oooooooooooooo");
        setContentView(R.layout.fragment_profile);
//        initData();
    }

    public void initData(){
        profile = this.getLayoutInflater().inflate(R.layout.fragment_profile,null);
        mProfilePhotoLayout = (LinearLayout) findViewById(R.id.profile_photo);
        mPhoto = new ImageButton_Text_Image(this,R.drawable.facebook,R.string.profile_photo);
        mProfilePhotoLayout.addView(mPhoto);

        mBack = (Button) findViewById(R.id.profile_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
