package com.itime.team.itime.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import com.itime.team.itime.activities.R;
import com.itime.team.itime.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 15/12/11.
 */
public class MeetingFragment extends Fragment implements View.OnClickListener{
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

    private ListView listView;
    private ArrayList<HashMap<String, Object>> listItem;
    private Button duration;

    //Topmenu
    private Button add;
    private Button invite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mmeeting = inflater.inflate(R.layout.fragment_meeting,null);
        initData();

        listView = (ListView) mmeeting.findViewById(R.id.meeting_listview);
        duration = (Button) mmeeting.findViewById(R.id.meeting_duration);

        listItem = new ArrayList<HashMap<String, Object>>();
        initListView();
        return mmeeting;
    }

    private void initData(){
        mCalendar = Calendar.getInstance();
        mStartTime = (Button) mmeeting.findViewById(R.id.meeting_start_time);
        mStartTime.setOnClickListener(this);
        mEndTime = (Button) mmeeting.findViewById(R.id.meeting_end_time);
        mEndTime.setOnClickListener(this);
        mStartDate = (Button) mmeeting.findViewById(R.id.meeting_start_date);
        mStartDate.setOnClickListener(this);
        mEndDate = (Button) mmeeting.findViewById(R.id.meeting_end_date);
        mEndDate.setOnClickListener(this);

        add = (Button) mmeeting.findViewById(R.id.meeting_add);
        invite = (Button) mmeeting.findViewById(R.id.meeting_invitebutton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingAddDialogFragment searchDialog = new MeetingAddDialogFragment();
                searchDialog.show(getFragmentManager(), "searchDialog");
            }
        });
    }

    private void initListView(){
        for(int i = 0; i < 20; i ++){
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.default_profile_image);
            map.put("ItemID", "Cai " + i);
            map.put("ItemName", "Cai "+ i);
            map.put("ItemInvite", mmeeting.findViewById(R.id.meeting_invite));
            listItem.add(map);
        }
        SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem,
                R.layout.fragment_meeting_listview,
                new String[] {"ItemImage","ItemID", "ItemName", "ItemInvite"},
                new int[] {R.id.meeting_profile,R.id.meeting_id,R.id.meeting_name,R.id.meeting_invite}
        );
        listView.setAdapter(listItemAdapter);
        //reset height
        setListViewHeightBasedOnChildren(listView);

        //Make a long click to delete a friend
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to delete " + listItem.get(position).get("ItemID"));
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
        });

        //Duration Button
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingDurationDialogFragment durationDialog = new MeetingDurationDialogFragment();
                durationDialog.show(getFragmentManager(), "durationDialog");
            }
        });
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.meeting_start_time){
            timePicker1 = new TimePickerDialog(getActivity(),new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTime.setText(hourOfDay + " : " + minute);
                    //If the end time is not set, the default value is one hour latter.
                    if(mEndTime.getText() == null || mEndTime.getText().equals("")){
                        mEndTime.setText((hourOfDay + 1) + " : " + minute);
                    }
                }
            },mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),false);
            timePicker1.show();
        }else if (v.getId() == R.id.meeting_start_date){
            datePicker1 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mStartDate.setText(dayOfMonth + "  " + DateUtil.month[monthOfYear - 1] +  "  " + year);
                    if(mEndDate.getText() == null || mEndDate.getText().equals("")){
                        mEndDate.setText(dayOfMonth + "  " + DateUtil.month[monthOfYear - 1] +  "  " + year);
                    }
                }
            },mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker1.show();
        }else if (v.getId() == R.id.meeting_end_time){
            timePicker2 = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mEndTime.setText(hourOfDay + " : " + minute);
                }
            },mCalendar.get(Calendar.HOUR_OF_DAY),mCalendar.get(Calendar.MINUTE),false);
            timePicker2.show();
        }else if(v.getId() == R.id.meeting_end_date){
            datePicker2 = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mEndDate.setText(dayOfMonth + "  " + DateUtil.month[monthOfYear - 1] +  "  " + year);
                }
            },mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            datePicker2.show();
        }
    }
}
