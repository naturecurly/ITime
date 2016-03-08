/*
 * Copyright (C) 2016 Yorkfine Chan <yorkfinechan@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itime.team.itime.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.R;


/**
 * Created by Xuhui Chen (yorkfine) on 19/01/16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, InputDialogFragment.InputDialogListener {
    private View mProfileView;
    private View mName;
    private View mID;
    private View mQRCode;
    private View mEmail;
    private View mPhoneNumber;

    private static final String PROFILE_FRAGMENT_TAG = ProfileFragment.class.getSimpleName();
    private static final String SETTINGS_PROFILE_NAME_TAG = "inputName";
    private static final String SETTINGS_PROFILE_EMAIL_TAG = "inputEmail";
    private static final String SETTINGS_PROFILE_PHONE_NUMBER_TAG = "inputPhoneNumber";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mProfileView = inflater.inflate(R.layout.fragment_profile, null);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Profile");
        init();
        setHasOptionsMenu(true);

        return mProfileView;
    }

    private void init() {
        mName = mProfileView.findViewById(R.id.setting_profile_name);
        mEmail = mProfileView.findViewById(R.id.setting_profile_email);
        mPhoneNumber = mProfileView.findViewById(R.id.setting_profile_phone_number);

        View [] views = new View[]{mName, mEmail, mPhoneNumber};
        bindOnClickListener(views);
    }

    private void bindOnClickListener (View [] views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.setting_profile_name:
                DialogFragment nameInputDialog = new InputDialogFragment();
                bundle.putString(InputDialogFragment.INPUT_DIALOG_TITLE, "Edit User Name");

                nameInputDialog.setArguments(bundle);
                nameInputDialog.show(getFragmentManager(), SETTINGS_PROFILE_NAME_TAG);
                break;

            case R.id.setting_profile_qrcode:
                break;

            case R.id.setting_profile_email:
                DialogFragment emailInputDialog = new InputDialogFragment();
                bundle.putString(InputDialogFragment.INPUT_DIALOG_TITLE, "Edit Your Email");
                emailInputDialog.setArguments(bundle);
                emailInputDialog.show(getFragmentManager(), SETTINGS_PROFILE_EMAIL_TAG);
                break;
            case R.id.setting_profile_phone_number:
                DialogFragment phoneNumInputDialog = new InputDialogFragment();
                bundle.putString(InputDialogFragment.INPUT_DIALOG_TITLE, "Edit Your Phone Number");
                phoneNumInputDialog.setArguments(bundle);
                phoneNumInputDialog.show(getFragmentManager(), SETTINGS_PROFILE_PHONE_NUMBER_TAG);
                break;
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String tag = dialog.getTag();
        switch (tag) {
            case SETTINGS_PROFILE_NAME_TAG:
                break;
            case SETTINGS_PROFILE_EMAIL_TAG:
                break;
            case SETTINGS_PROFILE_PHONE_NUMBER_TAG:
                break;
            default:
                Log.e(PROFILE_FRAGMENT_TAG, "UnKnow Click Result from " + tag);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
