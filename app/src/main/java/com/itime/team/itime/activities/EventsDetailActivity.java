package com.itime.team.itime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

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
        TextView title = (TextView) findViewById(R.id.event_title_detail);
        title.setText("Detail");
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.events_detail_content);
        if (fragment == null) {
            fragment = new EventDetailFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.events_detail_content, fragment).commit();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.event_detail, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().getFragments().get(0);
            if (fragment.isEdited()) {
                setResult(RESULT_OK);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventDetailFragment fragment = (EventDetailFragment) getSupportFragmentManager().getFragments().get(0);
        if (fragment.isEdited()) {
            setResult(RESULT_OK);
            finish();
        }
    }

    //    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.event_detail_edit) {
//            Intent intent = new Intent(this, EventsDetailEditActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
