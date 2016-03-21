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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_profile_qrcode);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.setting_profile_qrcode_img);

        final Bundle arguments = getArguments();
        if (!arguments.isEmpty()) {
            mUserId = arguments.getString(QRCODE_STRING);
        }

        Bitmap qrCodeBitmap;

        // method1: change a fix dip to pixel
        //final int dim = DensityUtil.dip2px(getContext(), 600);

        // method2: get the display size and scale into 7/8
        // This assumes the view is full screen, which is a good assumption
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int width = displaySize.x;
        int height = displaySize.y;
        int smallerDimension = width < height ? width : height;
        final int dim = smallerDimension * 7 / 8;

        try {
            qrCodeBitmap = EncodingHandler.createQRCode(mUserId, dim);
            imageView.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            Toast.makeText(getActivity(), "QRCode text can not be empty", Toast.LENGTH_SHORT).show();
        }

        return dialog;
    }
}
