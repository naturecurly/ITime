package com.itime.team.itime.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.itime.team.itime.activities.DateSelectionActivity;
import com.itime.team.itime.activities.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.views.adapters.DynamicListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 15/12/11.
 */
public class MeetingFragment extends Fragment implements View.OnClickListener,SearchView.OnQueryTextListener,
        AdapterView.OnItemLongClickListener, DataRequest{
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

    private ListView listView;
    //This variable stores users' friends, and it will be not changed after initialization
    private ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> listItemForPresent;
    private Button duration;

    //Topmenu
    private Button add;
    private Button invite;

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

    private JsonManager mJsonManager;

    //If the value is true, it satisfies the condition of inviting people
    private boolean mIsFeasible;

    public static int mDuration = 60;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        return mmeeting;
    }

    private void initData(){
        mCalendar = Calendar.getInstance();
        mStartTime = (Button) mmeeting.findViewById(R.id.meeting_start_time);
        mStartTime.setOnClickListener(this);
        mStartHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mStartMin =  mCalendar.get(Calendar.MINUTE);
        mStartTime.setText(timeFormat(mStartHour, mStartMin));

        mEndTime = (Button) mmeeting.findViewById(R.id.meeting_end_time);
        mEndTime.setOnClickListener(this);
        mEndHour = mCalendar.get(Calendar.HOUR_OF_DAY) + 1;
        mEndMin = mCalendar.get(Calendar.MINUTE);
        mEndTime.setText(timeFormat(mEndHour, mEndMin));

        mStartDate = (Button) mmeeting.findViewById(R.id.meeting_start_date);
        mStartDate.setOnClickListener(this);
        mStartYear = mCalendar.get(Calendar.YEAR);
        mStartMonth = mCalendar.get(Calendar.MONTH);
        mStartDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mStartDate.setText(dateFormat(mStartYear, mStartMonth, mStartDay));

        mEndDate = (Button) mmeeting.findViewById(R.id.meeting_end_date);
        mEndDate.setOnClickListener(this);
        mEndYear = mCalendar.get(Calendar.YEAR);
        mEndMonth = mCalendar.get(Calendar.MONTH);
        mEndDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mEndDate.setText(dateFormat(mEndYear,mEndMonth,mEndDay));

        add = (Button) mmeeting.findViewById(R.id.meeting_add);
        invite = (Button) mmeeting.findViewById(R.id.meeting_invitebutton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingAddDialogFragment searchDialog = new MeetingAddDialogFragment();
                searchDialog.show(getFragmentManager(), "searchDialog");
            }
        });

        invite.setOnClickListener(this);
        mIsFeasible = true;
        mJsonManager = new JsonManager();
//        test();
    }

    private void initListView(){
        JSONObject userID = new JSONObject();
        try {
            userID.put("user_id", User.ID);
            requestJSONArray(mJsonManager, userID, URLs.LOAD_FRIEND,
                    "load_friends");
            handleJSON(mJsonManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doInitListView(JSONArray mUserInfo){
        try {
            for(int i = 0; i < mUserInfo.length(); i ++){
                JSONObject jsonObject = (JSONObject) mUserInfo.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemID", jsonObject.get("user_id"));
                map.put("ItemName", jsonObject.get("user_name"));
                map.put("CheckBox", false);
                String url = null;
                if(jsonObject.get("user_profile_picture") != null &&
                        !jsonObject.get("user_profile_picture").equals("")) {
                    url = "http://www.kooyear.com/iTIME_Server/static/user_profiles/" +
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
        DynamicListViewAdapter listItemAdapter = new DynamicListViewAdapter(getActivity(),listItemForPresent);
        listView.setAdapter(listItemAdapter);
        //reset height
        setListViewHeightBasedOnChildren(listView);

        //Make a long click to delete a friend
        listView.setOnItemLongClickListener(this);
//        listView.setOnClickListener(this);
    }

    private void searchListView(String query){
        listItemForPresent.clear();
        for(HashMap<String, Object> map : listItem){
            if(map.get("ItemID").toString().contains(query)){
                listItemForPresent.add(map);
            }else if(query == null || query.equals("")){
                listItemForPresent.add(map);
            }
        }
        DynamicListViewAdapter listItemAdapter = new DynamicListViewAdapter(getActivity(),listItemForPresent);
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
        return hour + " : " + min;
    }
    private String dateFormat(int day, int month, int year){
        return day + " " + DateUtil.month[month] + " " + year;
    }

    private void checkTime(){
        if(DateUtil.isFeasible(mStartYear,mStartMonth,mStartDay,mStartHour,mStartMin,mEndYear,mEndMonth,
                mEndDay,mEndHour,mEndMin)) {
            mIsFeasible = true;
            mEndDate.setTextColor(getResources().getColor(R.color.bottom_bar));
            mEndTime.setTextColor(getResources().getColor(R.color.bottom_bar));
        }else{
            mIsFeasible = false;
            mEndDate.setTextColor(getResources().getColor(R.color.colorPrimary));
            mEndTime.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
    @Override
    public void onClick(View v) {
        checkTime();
        if(v.getId() == R.id.meeting_start_time){
            timePicker1 = new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTime.setText(hourOfDay + " : " + minute);
                    mStartHour = hourOfDay;
                    mStartMin = minute;
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
        }else if(v.getId() == R.id.meeting_invitebutton){
            if(mIsFeasible) {
                boolean isChecked = false;
                ArrayList<String> IDs = new ArrayList<>();
                for (HashMap<String, Object> map : listItemForPresent ){
                    if((Boolean)map.get("CheckBox")){
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
        }else if(v.getId() == R.id.meeting_duration){
            MeetingDurationDialogFragment durationDialog = new MeetingDurationDialogFragment(duration);
            durationDialog.show(getFragmentManager(), "durationDialog");
        }else if(v.getId() == R.id.meeting_listview){

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


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete " + listItemForPresent.get(position).get("ItemID"));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Warning");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
        return false;
    }

    @Override
    public void handleJSON(JsonManager manager) {
        MySingleton.getInstance(getContext()).getRequestQueue().addRequestFinishedListener(
                new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        HashMap map;
                        while((map = mJsonManager.getJsonQueue().poll()) != null) {
                            if((jsonArray = (JSONArray) map.get("load_friends")) != null){
                                doInitListView(jsonArray);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url,jsonObject,getActivity(),tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, getActivity(),tag);
    }

}
