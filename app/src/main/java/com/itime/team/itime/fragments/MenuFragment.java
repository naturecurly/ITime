package com.itime.team.itime.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.itime.team.itime.R;

/**
 * Created by mac on 15/12/11.
 */
public class MenuFragment extends Fragment {
    private ImageView mCalendar;
    private ImageView mMeeting;
    private ImageView mInbox;
    private ImageView mSettings;
    private View mMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMenu = inflater.inflate(R.layout.fragment_bottom_bar, null);
        MenuListener menuListener = new MenuListener();

        mCalendar = (ImageView) mMenu.findViewById(R.id.calendar_button);
        mCalendar.setOnClickListener(menuListener);

        mMeeting = (ImageView) mMenu.findViewById(R.id.meeting_button);
        mMeeting.setOnClickListener(menuListener);

        mInbox = (ImageView) mMenu.findViewById(R.id.mail_button);
        mInbox.setOnClickListener(menuListener);

        mSettings = (ImageView) mMenu.findViewById(R.id.setting_button);
        mSettings.setOnClickListener(menuListener);

        return mMenu;
    }

    class MenuListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.calendar_button:
                    Fragment calendarFragment = new CalendarFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.app_body, calendarFragment)
                            .commit();

                    break;
                case R.id.meeting_button:
                    Fragment meetingFragment = new MeetingFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.app_body, meetingFragment)
                            .commit();
                    break;
                case R.id.mail_button:
                    break;
                case R.id.setting_button:
                    break;
                default:

            }
        }
    }
}
