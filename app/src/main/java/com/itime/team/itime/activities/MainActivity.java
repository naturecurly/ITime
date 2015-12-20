package com.itime.team.itime.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
//import com.itime.team.itime.fragments.MenuFragment;

public class MainActivity extends AppCompatActivity {


    private ViewPager mPager;
    private PagerAdapter mAdapter;
    private FragmentTabHost tabHost;
    private Class fragmentArray[] = {CalendarFragment.class, MeetingFragment.class};
    private int mImageViewArray[] = {R.drawable.ic_date_range_black_48px, R.drawable.ic_group_work_black_48px};
    private String mTextViewArray[] = {"Calendar", "Meeting"};
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layoutInflater = LayoutInflater.from(this);
        tabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtab_content);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            tabHost.addTab(tabSpec,fragmentArray[i],null);
        }
//        FragmentManager fm = getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.app_body, new CalendarFragment()).commit();
//        fm.beginTransaction().add(R.id.app_foot, new MenuFragment()).commit();

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
