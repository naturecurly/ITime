package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.YearViewFragment;

/**
 * Created by leveyleonhardt on 4/11/16.
 */
public class YearViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_year_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.year_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.year_content);
        if (fragment == null) {
            fragment = new YearViewFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.year_content, fragment).commit();
        }
    }


}
