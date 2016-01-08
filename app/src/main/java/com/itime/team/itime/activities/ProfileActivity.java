package com.itime.team.itime.activities;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.itime.team.itime.utils.MySingleton;
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
        initData();
    }

    public void initData() {
        final ImageView mImageView;
        String url = "http://developer.android.com/images/training/system-ui.png";
        mImageView = (ImageView) findViewById(R.id.test_imageview);
// Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mImageView.setImageResource(R.drawable.default_profile_image);
                    }
                });
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
