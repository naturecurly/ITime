package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        if(savedInstanceState == null){
            Bundle arguments = new Bundle();

            NewEventFragment fragment = new NewEventFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.new_event_container, fragment)
                    .commit();
        }
    }
}
