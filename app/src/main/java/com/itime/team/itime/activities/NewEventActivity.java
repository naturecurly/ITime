package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.NewEventFragment;

/**
 * Created by mac on 16/3/15.
 */
public class NewEventActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event_activity);

        init();

        if(savedInstanceState == null){
            Bundle arguments = new Bundle();

            NewEventFragment fragment = new NewEventFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.new_event_container, fragment)
                    .commit();
        }
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_meeting_toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.new_event_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
