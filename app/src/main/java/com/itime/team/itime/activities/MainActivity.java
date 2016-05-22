package com.itime.team.itime.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bugtags.library.Bugtags;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.fragments.CalendarFragment;
import com.itime.team.itime.fragments.InboxFragment;
import com.itime.team.itime.fragments.MeetingFragment;
import com.itime.team.itime.fragments.SettingsFragment;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.widget.BadgeDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private RadioGroup mRadioGroup;
    private Menu mMenu;
    private CalendarFragment calendarFragment;
    private MeetingFragment meetingFragment;
    private InboxFragment inboxFragment;
    private SettingsFragment settingsFragment;
    //    private YearViewFragment yearViewFragment;
    private RadioButton calendarButton;
    private FragmentManager fragmentManager;
    private TextView title;

    private ImageButton mEventList;
    private Button mToday;

    private boolean mIsFriend;

    private LayerDrawable mBadgeIcon;


    // GCM Notification Receiver
    private BroadcastReceiver mNotificationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bugtags.start("26329ac444b1350d86677cfe9eec71d6", getApplication(), Bugtags.BTGInvocationEventBubble);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RadioButton br = (RadioButton) findViewById(R.id.button_inbox);
        mBadgeIcon = (LayerDrawable) (br.getCompoundDrawables()[1]); // left, top, right, bottom
        setBadgeCount(this, "0");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        fragmentManager = getSupportFragmentManager();
//        getSupportFragmentManager();
        layoutInflater = LayoutInflater.from(this);
        title = (TextView) findViewById(R.id.toolbar_title);

        mEventList = (ImageButton) findViewById(R.id.event_list);
        mToday = (Button) findViewById(R.id.button_today);

//        isFriend();
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
                    case R.id.button_inbox:
                        showFragment(inboxFragment);
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
        inboxFragment = new InboxFragment();
        fragmentManager.beginTransaction()
                .add(R.id.realtab_content, inboxFragment).hide(inboxFragment)
                .add(R.id.realtab_content, calendarFragment)
                .commit();

    }

    private void showFragment(Fragment me) {
        if (!me.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.realtab_content, me).commit();
        }

        if (me == calendarFragment) {
            fragmentManager.beginTransaction().hide(meetingFragment).commit();
            fragmentManager.beginTransaction().hide(settingsFragment).commit();
            fragmentManager.beginTransaction().hide(inboxFragment).commit();
            fragmentManager.beginTransaction().show(calendarFragment).commit();
            if(User.hasNewMeeting){
                calendarFragment.refresh();
                User.hasNewMeeting = false;
            }
        } else if (me == meetingFragment) {
            fragmentManager.beginTransaction().hide(calendarFragment).commit();
            fragmentManager.beginTransaction().hide(settingsFragment).commit();
            fragmentManager.beginTransaction().hide(inboxFragment).commit();
            fragmentManager.beginTransaction().show(meetingFragment).commit();

            if (User.hasNewFriend)
                meetingFragment.fetchEvents();
        } else if (me == inboxFragment) {
            fragmentManager.beginTransaction().hide(calendarFragment).commit();
            fragmentManager.beginTransaction().hide(settingsFragment).commit();
            fragmentManager.beginTransaction().hide(meetingFragment).commit();
            fragmentManager.beginTransaction().show(inboxFragment).commit();
        } else if (me == settingsFragment) {
            fragmentManager.beginTransaction().hide(calendarFragment).commit();
            fragmentManager.beginTransaction().hide(meetingFragment).commit();
            fragmentManager.beginTransaction().hide(inboxFragment).commit();
            fragmentManager.beginTransaction().show(settingsFragment).commit();
        }

        if (me == meetingFragment) {
            title.setText(getResources().getString(R.string.meeting_title));
            meetingFragment.setPosition();
            meetingFragment.handleConflict(mEventList, mToday);
        } else if (me == calendarFragment) {
            title.setText(calendarFragment.getTitle_string());
            calendarFragment.reSetMenuOnClickListener(mEventList);
        } else if (me == settingsFragment) {
            title.setText(getResources().getString(R.string.setting_title));
            settingsFragment.handleConfilct(mEventList, mToday);
        } else if (me == inboxFragment) {
            //inboxFragment.setTitle();
            mEventList.setVisibility(View.GONE);
            mToday.setVisibility(View.GONE);
            title.setText(getString(R.string.unread));

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
        isFriend();
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause");
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    private void addFriend(String name){
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", User.ID);
            object.put("friend_id", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.ADD_FRIEND_REQUEST;
        Map<String, String> params = new HashMap();
        params.put("json", object.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), getString(R.string.search_friend_added_friend_info), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }


    private void isFriend(){
        String id = getIntent().getStringExtra("invitation");
        if (!User.addFriendResume.equals("")){
            id = User.addFriendResume;
            User.addFriendResume = "";
        }
        if (id == null || id.equals("")){
            return;
        }
        if (id.equals(User.ID)) {
            Toast.makeText(this, getString(R.string.add_friend_myself), Toast.LENGTH_LONG).show();
            return;
        }

        mIsFriend = false;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", User.ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.LOAD_FRIEND;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        final String finalId = id;
        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject json = response.getJSONObject(i);
                        if (json.get("user_id").toString().equals(finalId)){
                            mIsFriend = true;
                            break;
                        }
                    }
                    if(mIsFriend == false) {
                        showDia(finalId);
                    } else {
                        String warning = String.format(getString(R.string.repeat_add_friend), finalId);
                        Toast.makeText(getApplication(), warning, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(request);
        User.addFriendResume = "";
    }

    private void showDia(final String finalId){
        String warning = String.format(getString(R.string.add_friend_confirm), finalId);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(warning);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getString(R.string.new_meeting_confirm_title));
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addFriend(finalId);
            }
        });
        builder.show();
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge; // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        }
        else {
            badge = new BadgeDrawable(context);
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public void setBadgeCount(Context context, String count) {
        setBadgeCount(context, mBadgeIcon, count);
    }

}
