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

package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.InputDialogFragment;
import com.itime.team.itime.fragments.InputDialogFragment.InputDialogListener;

/**
 * Created by Xuhui Chen (yorkfine) on 15/03/16.
 */
public class InputDialogActivity extends FragmentActivity implements InputDialogListener {

    public static final int RESULT_SET_TEXT = 1;
    public static final String RETURN_TEXT = "return_text";
    public static final String INPUT_DIALOG_TITLE = "input_dialog_title";


    @Override
    public void onDialogPositiveClick(String text) {
        final Intent intent = new Intent();
        intent.putExtra(RETURN_TEXT, text);
        setResult(RESULT_SET_TEXT, intent);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            final InputDialogFragment f = new InputDialogFragment();
            final Bundle args = new Bundle();
            args.putString(INPUT_DIALOG_TITLE, intent.getStringExtra(INPUT_DIALOG_TITLE));
            f.setArguments(args);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.setting_content, f)
//                    .commit();
            f.show(getSupportFragmentManager(), "input_dialog");
        }
    }
}
