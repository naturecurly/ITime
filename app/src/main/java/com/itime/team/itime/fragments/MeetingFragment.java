package com.itime.team.itime.fragments;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.itime.team.itime.R;
import com.itime.team.itime.views.CustomExpandableListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 15/12/11.
 */
public class MeetingFragment extends Fragment {
    private List<TextView> parent;
    private Map<Integer, View> map;
    private LayoutInflater inflater;
    private View mmeeting;
    private CustomExpandableListView mExpandableList;
    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private DatePicker datePicker1;
    private DatePicker datePicker2;
    private TextView textView1;
    private TextView textView2;
    private View timeSelection;
    private View timeSelection2;
    private Calendar calendar;

    private String startDate = "";
    private String startTime = "";
    private String endDate = "";
    private String endTime = "";

    private ListView listView;
    private ArrayList<HashMap<String, Object>> listItem;
    private Button duration;

    //Topmenu
    private Button add;
    private Button invite;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        mmeeting = inflater.inflate(R.layout.fragment_meeting,null);
        timeSelection = inflater.inflate(R.layout.fragment_meeting_timeselection, null);
        timeSelection2 = inflater.inflate(R.layout.fragment_meeting_timeselection, null);
        initData();
        mExpandableList = (CustomExpandableListView) mmeeting.findViewById(R.id.meeting_expandablelist);
        mExpandableList.setAdapter(new MyAdapter());

        listView = (ListView) mmeeting.findViewById(R.id.meeting_listview);
        duration = (Button) mmeeting.findViewById(R.id.meeting_duration);

        listItem = new ArrayList<HashMap<String, Object>>();
        initListView();
        return mmeeting;
    }

    private void initData(){
        textView1 = new TextView(getActivity());
        textView2 = new TextView(getActivity());
        textView1.setText("Starts");
        textView2.setText("Ends");

        parent = new ArrayList<TextView>();
        parent.add(textView1);
        parent.add(textView2);

        timePicker1 = (TimePicker) timeSelection.findViewById(R.id.meeting_list_timepicker);
        timePicker2 = (TimePicker) timeSelection2.findViewById(R.id.meeting_list_timepicker);
        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                startTime = timePicker1.getCurrentHour() + " : " + timePicker1.getCurrentMinute();
                textView1.setText(timeFormat("Starts",startDate,startTime));
            }
        });
        timePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                endTime = timePicker2.getCurrentHour() + " : " + timePicker2.getCurrentMinute();
                textView2.setText(timeFormat("Ends",endDate,endTime));
            }
        });

        datePicker1 = (DatePicker) timeSelection.findViewById(R.id.meeting_list_datepicker);
        datePicker2 = (DatePicker) timeSelection2.findViewById(R.id.meeting_list_datepicker);

        calendar = Calendar.getInstance();
        datePicker1.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),dateSetListener);
        datePicker2.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), dateSetListener2);

        map = new HashMap<Integer, View>();
        map.put(0, timeSelection);
        map.put(1, timeSelection2);

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

    //A listener for datepicker1
    private DatePicker.OnDateChangedListener dateSetListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String[] month = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            startDate = dayOfMonth + " " + month[monthOfYear-1] + " " + year;
            textView1.setText(timeFormat("Starts",startDate,startTime));
        }
    };

    //A listener for datepicker2
    private DatePicker.OnDateChangedListener dateSetListener2 = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String[] month = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            endDate = dayOfMonth + " " + month[monthOfYear-1] + " " + year;
            textView2.setText(timeFormat("Ends",endDate,endTime));
        }
    };

    //A format for representing date
    private String timeFormat(String title, String date, String time){
        String s = title + "      " + date + "   " + time;
        return s;
    }
    class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return parent.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parent.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return (map.get(groupPosition));
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            return (TextView)MeetingFragment.this.parent.get(groupPosition);
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = map.get(groupPosition);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
