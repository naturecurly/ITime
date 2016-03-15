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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.itime.team.itime.R;
import com.zxing.encoding.EncodingHandler;

/**
 * Created by Xuhui Chen (yorkfine) on 15/03/16.
 */
public class QRCodeFragment extends DialogFragment {

    private String mUserId;

    public static final String QRCODE_STRING = "qrcode_string";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_qrcode, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.setting_profile_qrcode_img);
        Bitmap qrCodeBitmap = null;
        final Bundle arguments = getArguments();
        if (!arguments.isEmpty()) {
            mUserId = arguments.getString(QRCODE_STRING);
        }
        try {
            qrCodeBitmap = EncodingHandler.createQRCode(mUserId, 350);
            imageView.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            Toast.makeText(getActivity(), "QRCode text can not be empty", Toast.LENGTH_SHORT).show();
        }

        return v;
    }
}
