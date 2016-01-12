package com.itime.team.itime.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itime.team.itime.views.widget.ImageButton_Text_Image;

/**
 * Created by mac on 15/12/22.
 */
public class ProfileActivity extends AppCompatActivity {
    private LinearLayout mProfilePhotoLayout;
    private ImageButton_Text_Image mPhoto;
    private Button mBack;
    private View profile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        final TextView t = (TextView) findViewById(R.id.oo);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.setText("ok");
            }
        });
    }
}
