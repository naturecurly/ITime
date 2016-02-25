package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itime.team.itime.views.widget.ClearEditText;

/**
 * Created by mac on 16/2/24.
 */
public class LoginActivity extends AppCompatActivity {
    private Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ClearEditText username = (ClearEditText) findViewById(R.id.username);
        final ClearEditText password = (ClearEditText) findViewById(R.id.password);

        ((Button) findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username.getText())) {
                    username.setShakeAnimation();
                    showToast("a");
                    return;
                }

                if (TextUtils.isEmpty(password.getText())) {
                    password.setShakeAnimation();
                    showToast("b");
                    return;
                }
            }
        });
    }

    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

}
