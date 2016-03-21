package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bugtags.library.Bugtags;
import com.itime.team.itime.R;
import com.itime.team.itime.fragments.CalendarFragment;
import com.itime.team.itime.fragments.MeetingFragment;
import com.itime.team.itime.fragments.SettingsFragment;
import com.itime.team.itime.fragments.YearViewFragment;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    private LayoutInflater layoutInflater;
    private RadioGroup mRadioGroup;
    private Menu mMenu;
    private CalendarFragment calendarFragment;
    private MeetingFragment meetingFragment;
    private SettingsFragment settingsFragment;
    private YearViewFragment yearViewFragment;
    private RadioButton calendarButton;
    private FragmentManager fragmentManager;
    private TextView title;

    private ImageButton mEventList;
    private Button mToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bugtags.start("26329ac444b1350d86677cfe9eec71d6", getApplication(), Bugtags.BTGInvocationEventBubble);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fragmentManager = getSupportFragmentManager();
        getSupportFragmentManager();
        layoutInflater = LayoutInflater.from(this);
        title = (TextView) findViewById(R.id.toolbar_title);

        mEventList = (ImageButton) findViewById(R.id.event_list);
        mToday = (Button) findViewById(R.id.button_today);

        setFragments();

        mRadioGroup = (RadioGroup) findViewById(R.id.tab_menu);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button_calendar:
                        showFragment(calendarFragment);
                        break;
                    case R.id.button_meeting:
                        showFragment(meetingFragment);
                        break;
                    case R.id.button_setting:
                        showFragment(settingsFragment);
                        break;
                }
            }
        });
    }

    private void setFragments() {
        calendarFragment = new CalendarFragment();

        meetingFragment = new MeetingFragment();
        settingsFragment = new SettingsFragment();
        yearViewFragment = new YearViewFragment();
        calendarFragment.setCurrentFragment(calendarFragment, yearViewFragment);
        yearViewFragment.setCalendarFragment(calendarFragment,yearViewFragment);
        getSupportFragmentManager().beginTransaction().add(R.id.realtab_content, calendarFragment).commit();
    }

    private void showFragment(Fragment me) {
        if (!me.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.realtab_content, me).commit();
        }

        if (me == calendarFragment) {
            fragmentManager.beginTransaction().hide(meetingFragment).commit();
            fragmentManager.beginTransaction().hide(settingsFragment).commit();
            fragmentManager.beginTransaction().hide(yearViewFragment).commit();
            fragmentManager.beginTransaction().show(calendarFragment).commit();
        } else if (me == meetingFragment) {
            fragmentManager.beginTransaction().hide(calendarFragment).commit();
            fragmentManager.beginTransaction().hide(settingsFragment).commit();
            fragmentManager.beginTransaction().hide(yearViewFragment).commit();
            fragmentManager.beginTransaction().show(meetingFragment).commit();
        } else if (me == settingsFragment) {
            fragmentManager.beginTransaction().hide(calendarFragment).commit();
            fragmentManager.beginTransaction().hide(meetingFragment).commit();
            fragmentManager.beginTransaction().hide(yearViewFragment).commit();
            fragmentManager.beginTransaction().show(settingsFragment).commit();
        }

        if (me == meetingFragment) {
            title.setText(getResources().getString(R.string.meeting_title));
            meetingFragment.setPosition();
            meetingFragment.handleConflict(mEventList, mToday);
        } else if (me == calendarFragment) {
            title.setText(getResources().getString(R.string.calendar_title));
            calendarFragment.reSetMenuOnClickListener(mEventList);
        } else if (me == settingsFragment) {
            title.setText(getResources().getString(R.string.setting_title));
            settingsFragment.handleConfilct(mEventList, mToday);
        }

        calendarButton = (RadioButton) findViewById(R.id.button_calendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (fragmentManager.findFragmentById(R.id.realtab_content) != calendarFragment && fragmentManager.findFragmentById(R.id.realtab_content) != meetingFragment && fragmentManager.findFragmentById(R.id.realtab_content) != settingsFragment)
//                    fragmentManager.beginTransaction().replace(R.id.realtab_content, calendarFragment).commit();
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(R.id.fragment_container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }
}
