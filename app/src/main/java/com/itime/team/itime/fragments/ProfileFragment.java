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


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.activities.InputDialogActivity;
import com.itime.team.itime.database.ITimeDataStore;


/**
 * Created by Xuhui Chen (yorkfine) on 19/01/16.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ProfileFragment.class.getSimpleName();

    private View mProfileView;
    private View mName;
    private View mID;
    private View mQRCode;
    private View mEmail;
    private View mPhoneNumber;

    //Views
    private TextView mUserNameTextView;
    private TextView mUserIdTextView;
    private ImageView mUserProfileImageView;
    private TextView mUserEmailTextView;
    private TextView mUserPhoneNumberTv;

    // User ID of this user
    private String mUserId;

    private static final String PROFILE_FRAGMENT_TAG = ProfileFragment.class.getSimpleName();
    private static final int REQUEST_SET_USER_NAME = 1;
    private static final int REQUEST_SET_EMAIL = 2;
    private static final int REQUEST_SET_PHONE_NUMBER = 3;

    private static final int PROFILE_LOADER = 0;


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

        mUserNameTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_name_text);
        mUserIdTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_id_text);
        mUserProfileImageView = (ImageView) mProfileView.findViewById(R.id.setting_profile_picture);
        mUserEmailTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_email_text);
        mUserPhoneNumberTv = (TextView) mProfileView.findViewById(R.id.setting_profile_phone_number_text);

        // TODO: 14/03/16 Not a good way to store static data in a model object
        mUserId = com.itime.team.itime.bean.User.ID;
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
            case R.id.setting_profile_name: {
                final Intent intent = new Intent(getActivity(), InputDialogActivity.class);
                intent.putExtra(InputDialogActivity.INPUT_DIALOG_TITLE, "Edit User Name");
                startActivityForResult(intent, REQUEST_SET_USER_NAME);
                break;
            }

            case R.id.setting_profile_qrcode:
                break;

            case R.id.setting_profile_email: {
                final Intent intent = new Intent(getActivity(), InputDialogActivity.class);
                intent.putExtra(InputDialogActivity.INPUT_DIALOG_TITLE, "Edit Your Email");
                startActivityForResult(intent, REQUEST_SET_EMAIL);
                break;
            }
            case R.id.setting_profile_phone_number: {
                final Intent intent = new Intent(getActivity(), InputDialogActivity.class);
                intent.putExtra(InputDialogActivity.INPUT_DIALOG_TITLE, "Edit Your Phone Number");
                startActivityForResult(intent, REQUEST_SET_PHONE_NUMBER);
                break;
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(PROFILE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SET_USER_NAME: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUserNameTextView.setText(data.getStringExtra(InputDialogActivity.RETURN_TEXT));
                }
                break;
            }
            case REQUEST_SET_EMAIL: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUserEmailTextView.setText(data.getStringExtra(InputDialogActivity.RETURN_TEXT));
                }
                break;
            }
            case REQUEST_SET_PHONE_NUMBER: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUserPhoneNumberTv.setText(data.getStringExtra(InputDialogActivity.RETURN_TEXT));
                }
                break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri userByIdUri = ITimeDataStore.User.CONTENT_URI.buildUpon().appendPath(mUserId).build();
        return new CursorLoader(getActivity(),
                userByIdUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Log.i(LOG_TAG, "user information found in database");
            CursorIndices cursorIndices = new CursorIndices(data);
            final String userId = data.getString(cursorIndices.userId);
            final String userName = data.getString(cursorIndices.userName);
            final String userEmail = data.getString(cursorIndices.email);
            final String userPhoneNum = data.getString(cursorIndices.phone);

            mUserIdTextView.setText(userId);
            mUserNameTextView.setText(userName);
            mUserEmailTextView.setText(userEmail);
            mUserPhoneNumberTv.setText(userPhoneNum);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class CursorIndices {
        final int _id, userName, userId, email, phone;

        CursorIndices(final Cursor mCursor) {
            _id = mCursor.getColumnIndex(ITimeDataStore.User._ID);
            userName = mCursor.getColumnIndex(ITimeDataStore.User.USER_NAME);
            userId = mCursor.getColumnIndex(ITimeDataStore.User.USER_ID);
            email = mCursor.getColumnIndex(ITimeDataStore.User.EMAIL);
            phone = mCursor.getColumnIndex(ITimeDataStore.User.PHONE_NUMBER);
        }
    }
}
