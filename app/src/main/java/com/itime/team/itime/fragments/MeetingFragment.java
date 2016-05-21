package com.itime.team.itime.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.activities.DateSelectionActivity;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.listener.ScrollMeetingViewListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonArrayFormRequest;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.MeetingScrollView;
import com.itime.team.itime.views.adapters.DynamicListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAI on 15/12/11.
 * Besides loading and presenting the User's friends, this fragment also handle friends' preference
 * information so that reduce the computing work load of MeetingSelection Fragment.
 */
public class MeetingFragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener,
        ScrollMeetingViewListener{
//    private List<TextView> parent;
    private Map<Integer, View> map;
    private View mmeeting;
    private TimePickerDialog timePicker1;
    private TimePickerDialog timePicker2;
    private DatePickerDialog datePicker1;
    private DatePickerDialog datePicker2;
    private Button mStartTime;
    private Button mEndTime;
    private Button mStartDate;
    private Button mEndDate;
    private Calendar mCalendar;
    private SearchView mSearch;
    private LinearLayout mInvitedFriend;
    private Menu mMenu;

    private ListView listView;
    //This variable stores users' friends, and it will be not changed after initialization
    private ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> listItemForPresent;
    private Button duration;

    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private int mStartHour;
    private int mStartMin;
    private int mEndYear;
    private int mEndMonth;
    private int mEndDay;
    private int mEndHour;
    private int mEndMin;

    private HashMap<String,Boolean> checkBoxKeeper;

    //private JsonManager mJsonManager;
    private MeetingScrollView mScrollView;

    //If the value is true, it satisfies the condition of inviting people
    private boolean mIsFeasible;

    public static int mDuration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        checkBoxKeeper = new HashMap<>();

        mmeeting = inflater.inflate(R.layout.fragment_meeting,null);
        initData();
        listView = (ListView) mmeeting.findViewById(R.id.meeting_listview);
        duration = (Button) mmeeting.findViewById(R.id.meeting_duration);
        duration.setText("1Hour");
        duration.setOnClickListener(this);

        mSearch = (SearchView) mmeeting.findViewById(R.id.meeting_search);
        mSearch.setOnQueryTextListener(this);

        listItem = new ArrayList<HashMap<String, Object>>();
        listItemForPresent = new ArrayList<HashMap<String, Object>>();
        initListView();

        mInvitedFriend = (LinearLayout) mmeeting.findViewById(R.id.meeting_invited_friend);
        mDuration = 60;

        return mmeeting;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        mMenu.clear();
        getActivity().getMenuInflater().inflate(R.menu.meeting_main, mMenu);

    }

    private void reSetMenuOnClickListener(ImageButton mAddFriend){
        mAddFriend.setVisibility(View.VISIBLE);
        mAddFriend.setImageResource(R.drawable.ic_add_white);
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingAddDialogFragment searchDialog = new MeetingAddDialogFragment(listItem);
                searchDialog.show(getFragmentManager(), "searchDialog");
            }
        });
        mAddFriend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }

    private void hideYear(Button year){
        year.setVisibility(View.GONE);
    }

    public void handleConflict(ImageButton addFriend, Button year){
        reSetMenuOnClickListener(addFriend);
        hideYear(year);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.meeting_menu_invite){
            inviteFriend();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData(){
        mScrollView = (MeetingScrollView) mmeeting.findViewById(R.id.meeting_view);
        mScrollView.setOnScrollViewListener(this);

        mCalendar = Calendar.getInstance();
        mStartTime = (Button) mmeeting.findViewById(R.id.meeting_start_time);
        mStartTime.setOnClickListener(this);
        mStartHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mStartMin =  mCalendar.get(Calendar.MINUTE);
        mStartTime.setText(timeFormat(mStartHour, mStartMin));

        mEndTime = (Button) mmeeting.findViewById(R.id.meeting_end_time);
        mEndTime.setOnClickListener(this);
        mEndHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mEndMin = mCalendar.get(Calendar.MINUTE);
        mEndTime.setText(timeFormat(mEndHour, mEndMin));

        mStartDate = (Button) mmeeting.findViewById(R.id.meeting_start_date);
        mStartDate.setOnClickListener(this);
        mStartYear = mCalendar.get(Calendar.YEAR);
        mStartMonth = mCalendar.get(Calendar.MONTH);
        mStartDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mStartDate.setText(dateFormat(mStartDay, mStartMonth, mStartYear));

        setEndTime();

        mIsFeasible = true;
    }

    private void setEndTime(){
        //Default date : 7 days later
        int[] laterDay = DateUtil.addDaysBasedOnCalendar(mStartYear, mStartMonth, mStartDay, 7);

        mEndDate = (Button) mmeeting.findViewById(R.id.meeting_end_date);
        mEndDate.setOnClickListener(this);
        mEndYear = laterDay[0];
        mEndMonth = laterDay[1];
        mEndDay = laterDay[2];
        mEndDate.setText(dateFormat(mEndDay, mEndMonth, mEndYear));
    }

    public void initListView() {
        listItem.clear();
        listItemForPresent.clear();
        fetchEvents();
    }

    // There are two listItem to store friends' information, the listItem stores the information from
    // the server, and the listItemForPresent just get information from the listItem and then represent
    // it. The reason creating two listItem is that user is allowed to search his friends, and the
    // listItemForPresent shows these friends who are searched.
    private void doInitListView(JSONArray mUserInfo){
        try {
            listItem.clear();
            listItemForPresent.clear();
            for(int i = 0; i < mUserInfo.length(); i ++){
                JSONObject jsonObject = (JSONObject) mUserInfo.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", jsonObject.get("user_id"));
                map.put("ItemName", jsonObject.get("user_name"));
                map.put("CheckBox", false);
                String url = null;
                if(jsonObject.get("user_profile_picture") != null &&
                        !jsonObject.get("user_profile_picture").equals("")) {
                    url = URLs.PROFILE_PICTURE +
                            jsonObject.get("user_id") + "/profile_picture.png";
                }
                map.put("url",url);
                listItem.add(map);
                //The deep copy of listItem
                listItemForPresent.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DynamicListViewAdapter listItemAdapter = new DynamicListViewAdapter(getActivity(),
                listItemForPresent,mInvitedFriend,getResources(),this, checkBoxKeeper);
        listView.setAdapter(listItemAdapter);
        //reset height
        setListViewHeightBasedOnChildren(listView);
    }

    private void searchListView(String query){
        listItemForPresent.clear();
        for(HashMap<String, Object> map : listItem){
            if(map.get("ItemID").toString().contains(query)){
                map.put("CheckBox",checkBoxKeeper.get(map.get("ItemID").toString()));
                listItemForPresent.add(map);
            }else if(query == null || query.equals("")){
                listItemForPresent.add(map);
            }
        }
        DynamicListViewAdapter listItemAdapter = new DynamicListViewAdapter(getActivity(),
                listItemForPresent,mInvitedFriend, getResources(),this,checkBoxKeeper);
        listView.setAdapter(listItemAdapter);

        //reset height
        setListViewHeightBasedOnChildren(listView);
    }


    // Since the content of listview cannot be completely represented, it is necessary to use this
    //method to calculated the height of the listview
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private String timeFormat(int hour, int min){
        String hourReturn = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minReturn = min < 10 ? "0" + min : String.valueOf(min);

        return hourReturn + " : " + minReturn;
    }
    private String dateFormat(int day, int month, int year){
        String dayReturn = day < 10 ? "0" + day : String.valueOf(day);
        return  DateUtil.weekName[DateUtil.getDateOfWeek_M(year,month, day) - 1] +
                ", " + dayReturn + " " + DateUtil.month[month] + " " + year;
    }

    private void checkTime(){
        if(DateUtil.isFeasible(mStartYear,mStartMonth,mStartDay,mStartHour,mStartMin,mEndYear,mEndMonth,
                mEndDay,mEndHour,mEndMin)) {
            mIsFeasible = true;
            mEndDate.setTextColor(getResources().getColor(R.color.bottom_bar));
            mEndTime.setTextColor(getResources().getColor(R.color.bottom_bar));
        }else{
            mIsFeasible = false;
            mEndDate.setTextColor(getResources().getColor(R.color.colorAccent));
            mEndTime.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void onClick(View v) {
        checkTime();
        if(v.getId() == R.id.meeting_start_time){
            timePicker1 = new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTime.setText(timeFormat(hourOfDay, minute));
                    mStartHour = hourOfDay;
                    mStartMin = minute;
                    setEndTime();
                    checkTime();
                }
            },mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),false);
            timePicker1.show();
        }else if (v.getId() == R.id.meeting_start_date){
            datePicker1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mStartDate.setText(dateFormat(dayOfMonth,monthOfYear,year));
                    mStartYear = year;
                    mStartMonth = monthOfYear;
                    mStartDay = dayOfMonth;
                    setEndTime();
                    checkTime();
                }
            },mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker1.show();
        }else if (v.getId() == R.id.meeting_end_time){
            timePicker2 = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mEndTime.setText(timeFormat(hourOfDay, minute));
                    mEndHour = hourOfDay;
                    mEndMin = minute;
                    checkTime();
                }
            },mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),false);
            timePicker2.show();
        }else if(v.getId() == R.id.meeting_end_date){
            datePicker2 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mEndDate.setText(dateFormat(dayOfMonth, monthOfYear,year));
                    mEndYear = year;
                    mEndMonth = monthOfYear;
                    mEndDay = dayOfMonth;
                    checkTime();
                }
            },mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker2.show();
        }else if(v.getId() == R.id.meeting_duration){
            MeetingDurationDialogFragment durationDialog = new MeetingDurationDialogFragment(duration);
            durationDialog.show(getFragmentManager(), "durationDialog");
        }else if(v.getId() == R.id.meeting_listview){
        }
    }

    private void inviteFriend(){
        if(mIsFeasible) {
            boolean isChecked = false;
            ArrayList<String> IDs = new ArrayList<>();
            for (HashMap<String, Object> map : listItemForPresent){
                if(map.get("CheckBox") != null && (Boolean)map.get("CheckBox")){
                    isChecked = true;
                    IDs.add((String) map.get("ItemID"));
                }
            }
            if(isChecked) {
                Intent intent = new Intent(getActivity(), DateSelectionActivity.class);
                intent.putExtra("startyear", mStartYear);
                intent.putExtra("startmonth", mStartMonth + 1);
                intent.putExtra("startday", mStartDay);
                intent.putExtra("starthour", mStartHour);
                intent.putExtra("startmin", mStartMin);
                intent.putExtra("endyear", mEndYear);
                intent.putExtra("endmonth", mEndMonth + 1);
                intent.putExtra("endday", mEndDay);
                intent.putExtra("endhour", mEndHour);
                intent.putExtra("endmin", mEndMin);
                intent.putExtra("duration", mDuration);
                intent.putStringArrayListExtra("friendIDs", IDs);
                getActivity().startActivity(intent);
                checkTime();
            }else{
                Toast.makeText(getContext(),"Please select a friend at least", Toast.LENGTH_LONG)
                        .show();
            }
        }else{
            Toast.makeText(getActivity(), "the Start Time should be earlier than End Time",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchListView(newText);
        return true;
    }

    public void fetchEvents() {
        if(User.hasNewFriend == true) {
            if(checkBoxKeeper != null) {
                checkBoxKeeper.clear();
            }
            if(mInvitedFriend != null) {
                mInvitedFriend.removeAllViews();
            }
            User.hasNewFriend = false;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", User.ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.LOAD_FRIEND;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonArrayFormRequest request = new JsonArrayFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                doInitListView(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }


    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        index ++;
        if(index == 1){
            mScrollView.scrollTo(0,oldy);
        }
    }
    private int index = 0;
    public void setPosition(){
        index = 0;
    }

}
