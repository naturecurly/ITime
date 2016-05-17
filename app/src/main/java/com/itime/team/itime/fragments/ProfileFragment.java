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


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.InputDialogActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.database.ITimeDataStore;
import com.itime.team.itime.model.ParcelableUser;
import com.itime.team.itime.task.UserTask;
import com.itime.team.itime.utils.FileUtil;
import com.itime.team.itime.utils.MySingleton;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
    private View mProfileImage;
    private View mEmail;
    private View mPhoneNumber;

    private ProgressDialog mProgress;


    //Views
    private TextView mUserNameTextView;
    private TextView mUserIdTextView;
    private ImageView mUserProfileImageView;
    private TextView mUserEmailTextView;
    private TextView mUserPhoneNumberTv;

    // Data
    private ParcelableUser mUser;

    // User ID of this user
    private String mUserId;

    private static final String PROFILE_FRAGMENT_TAG = ProfileFragment.class.getSimpleName();
    private static final int REQUEST_SET_USER_NAME = 1;
    private static final int REQUEST_SET_EMAIL = 2;
    private static final int REQUEST_SET_PHONE_NUMBER = 3;
    private static final int REQUEST_IMAGE_SELECT = 4;

    public static final int RESULT_UPDATE_PROFILE = 1;
    public static final String RESULT_UPDATE_PROFILE_DATA = "result_update_profile_data";


    private static final int PROFILE_LOADER = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mProfileView = inflater.inflate(R.layout.fragment_profile, null);
        TextView title = (TextView) getActivity().findViewById(R.id.setting_toolbar_title);
        title.setText("Profile");
        init();
        setHasOptionsMenu(true);

        mProgress = new ProgressDialog(getActivity());
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);

        return mProfileView;
    }

    private void init() {
        mName = mProfileView.findViewById(R.id.setting_profile_name);
        mEmail = mProfileView.findViewById(R.id.setting_profile_email);
        mPhoneNumber = mProfileView.findViewById(R.id.setting_profile_phone_number);
        mProfileImage = mProfileView.findViewById(R.id.setting_profile_picture);
        mQRCode = mProfileView.findViewById(R.id.setting_profile_qrcode);

        View[] views = new View[]{mName, mEmail, mPhoneNumber, mProfileImage, mQRCode};
        bindOnClickListener(views);

        mUserNameTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_name_text);
        mUserIdTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_id_text);
        mUserProfileImageView = (ImageView) mProfileView.findViewById(R.id.setting_profile_picture_img);
        mUserEmailTextView = (TextView) mProfileView.findViewById(R.id.setting_profile_email_text);
        mUserPhoneNumberTv = (TextView) mProfileView.findViewById(R.id.setting_profile_phone_number_text);

        // TODO: 14/03/16 Not a good way to store static data in a model object
        mUserId = com.itime.team.itime.bean.User.ID;
        mUser = new ParcelableUser();
    }

    private void bindOnClickListener(View[] views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.setting_profile_picture: {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
                break;
            }
            case R.id.setting_profile_name: {
                final Intent intent = new Intent(getActivity(), InputDialogActivity.class);
                intent.putExtra(InputDialogActivity.INPUT_DIALOG_TITLE, "Edit User Name");
                startActivityForResult(intent, REQUEST_SET_USER_NAME);
                break;
            }

            case R.id.setting_profile_qrcode:
                final QRCodeFragment qrCodeFragment = new QRCodeFragment();
                final Bundle args = new Bundle();
                args.putString(QRCodeFragment.QRCODE_STRING, mUserId);
                qrCodeFragment.setArguments(args);
                qrCodeFragment.show(getActivity().getSupportFragmentManager(), "qrcode_fragment");
                //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                //ft.add(qrCodeFragment, "qrcode_fragment");
                //ft.commit();
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
        loadProfileImage();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_SELECT: {
                if (data != null) {
                    Log.i(LOG_TAG, data.getData().toString());
                    String filePath = FileUtil.getFileAbsolutePath(getActivity(), data.getData());
                    if (filePath != null) {
                        Log.i(LOG_TAG, filePath);
                    }
                    //getActivity().openFileInput()

                    new MakeRequestTask(data.getData(), filePath, User.ID).execute();
                }
                break;
            }
            case REQUEST_SET_USER_NAME: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUser.userName = data.getStringExtra(InputDialogActivity.RETURN_TEXT);
                    UserTask.getInstance(getActivity()).updateUserInfo(mUserId, mUser, null);
                    mUserNameTextView.setText(data.getStringExtra(InputDialogActivity.RETURN_TEXT));
                }
                break;
            }
            case REQUEST_SET_EMAIL: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUser.email = data.getStringExtra(InputDialogActivity.RETURN_TEXT);
                    UserTask.getInstance(getActivity()).updateUserInfo(mUserId, mUser, null);
                    mUserEmailTextView.setText(data.getStringExtra(InputDialogActivity.RETURN_TEXT));
                }
                break;
            }
            case REQUEST_SET_PHONE_NUMBER: {
                if (resultCode == InputDialogActivity.RESULT_SET_TEXT) {
                    mUser.phoneNumber = data.getStringExtra(InputDialogActivity.RETURN_TEXT);
                    UserTask.getInstance(getActivity()).updateUserInfo(mUserId, mUser, null);
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
            final String userDefaultAlert = data.getString(cursorIndices.defaultAlert);
            final String userProfilePicture = data.getString(cursorIndices.profilePicture);

            mUserIdTextView.setText(userId);
            mUserNameTextView.setText(userName);
            mUserEmailTextView.setText(userEmail);
            mUserPhoneNumberTv.setText(userPhoneNum);

            mUser = new ParcelableUser(data, new ParcelableUser.CursorIndices(data));
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    static class CursorIndices {
        final int _id, userName, userId, email, phone, defaultAlert, profilePicture;

        CursorIndices(final Cursor mCursor) {
            _id = mCursor.getColumnIndex(ITimeDataStore.User._ID);
            userName = mCursor.getColumnIndex(ITimeDataStore.User.USER_NAME);
            userId = mCursor.getColumnIndex(ITimeDataStore.User.USER_ID);
            email = mCursor.getColumnIndex(ITimeDataStore.User.EMAIL);
            phone = mCursor.getColumnIndex(ITimeDataStore.User.PHONE_NUMBER);
            defaultAlert = mCursor.getColumnIndex(ITimeDataStore.User.DEFAULT_ALERT);
            profilePicture = mCursor.getColumnIndex(ITimeDataStore.User.USER_PROFILE_PICTURE);
        }
    }

    private void loadProfileImage() {
        String url = URLs.PROFILE_PICTURE +
                User.ID + "/profile_picture.png";
        ImageRequest request = new ImageRequest(url,
                new com.android.volley.Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mUserProfileImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new com.android.volley.Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mUserProfileImageView.setImageResource(R.drawable.default_profile_image);
                    }
                });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }



    private class MakeRequestTask extends AsyncTask<Void, String, String> {
        private Uri mFile;
        private String mFilePath;
        private String mFileName;
        private Exception mLastError = null;

        public MakeRequestTask(Uri file, String filePath, String fileName) {
            mFile = file;
            mFilePath = filePath;
            mFileName = fileName;
        }

        private void uploadProfilePicture(String file, String fileName) throws IOException {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

            final OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    //.addFormDataPart("name", "image")
                    .addFormDataPart("image", fileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(file)))
                    .build();
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + User.token)
                    .url(URLs.USER_PROFILE_PIC_UPLOAD)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Log.i(LOG_TAG, response.body().string());
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                uploadProfilePicture(mFilePath, mFileName);
                return "success";
            } catch (IOException e) {
                mLastError = e;
                cancel(true);
                return "error";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.hide();

            Toast.makeText(getContext(), getString(R.string.upload_profile_photo_success), Toast.LENGTH_SHORT).show();
            mUserProfileImageView.setImageURI(mFile);
            Intent intent = new Intent();
            intent.putExtra(RESULT_UPDATE_PROFILE_DATA, true);
            getActivity().setResult(RESULT_UPDATE_PROFILE, intent);

        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                System.out.println(mLastError.getMessage());
                Toast.makeText(getContext(), getString(R.string.upload_profile_photo_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
