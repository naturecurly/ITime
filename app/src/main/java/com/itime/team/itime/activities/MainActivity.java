package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.fragments.CalendarFragment;
import com.itime.team.itime.fragments.MeetingFragment;
import com.itime.team.itime.fragments.SettingsFragment;
//import com.itime.team.itime.fragments.MenuFragment;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {


    private ViewPager mPager;
    private PagerAdapter mAdapter;
    private FragmentTabHost tabHost;
    private Class fragmentArray[] = {CalendarFragment.class, MeetingFragment.class, null, SettingsFragment.class};
    private int mImageViewArray[] = {R.drawable.ic_date_range_s, R.drawable.ic_group_s, R.drawable.ic_email_black_s,
            R.drawable.ic_setting_s};
    private String mTextViewArray[] = {"Calendar", "Meeting", "Email", "Settings"};
    private LayoutInflater layoutInflater;

    private int mIndex;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        layoutInflater = LayoutInflater.from(this);
        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtab_content);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            tabHost.addTab(tabSpec, fragmentArray[i], null);

        }



    }


    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.tab_image_view);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.tab_text_view);
        textView.setText(mTextViewArray[index]);


        return view;
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
    protected void onPause() {
        super.onPause();
        //Log.i("Pause","oooqqqqqqqq");
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
}
