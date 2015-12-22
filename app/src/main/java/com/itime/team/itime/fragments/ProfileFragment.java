package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.itime.team.itime.R;
import com.itime.team.itime.views.widget.ImageButton_Text_Image;

/**
 * Created by mac on 15/12/20.
 */
public class ProfileFragment extends Fragment {
    private View profile;
    private LinearLayout mProfilePhotoLayout;
    private ImageButton_Text_Image mPhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profile = inflater.inflate(R.layout.fragment_profile, null);
        initData();
        return profile;
    }

    public void initData(){
        mProfilePhotoLayout = (LinearLayout) profile.findViewById(R.id.profile_photo);
        mPhoto = new ImageButton_Text_Image(getActivity(),R.drawable.facebook,R.string.profile_photo);
        mProfilePhotoLayout.addView(mPhoto);
    }
}
