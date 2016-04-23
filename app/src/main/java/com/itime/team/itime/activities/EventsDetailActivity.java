package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.EventDetailFragment;

/**
 * Created by leveyleonhardt on 4/23/16.
 */
public class EventsDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.event_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.events_detail_content);
        if (fragment == null) {
            fragment = new EventDetailFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.events_detail_content, fragment).commit();
        }
    }
}
