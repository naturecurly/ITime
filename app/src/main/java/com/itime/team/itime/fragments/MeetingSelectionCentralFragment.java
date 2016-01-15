package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.itime.team.itime.activities.R;
import com.itime.team.itime.bean.Preference;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.listener.ScrollViewListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.JsonManager;
import com.itime.team.itime.utils.MySingleton;
import com.itime.team.itime.utils.URLConnectionUtil;
import com.itime.team.itime.views.MeetingSelectionScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by mac on 15/12/27.
 */
public class MeetingSelectionCentralFragment extends Fragment implements ScrollViewListener, DataRequest {
    private Intent intent;
    private View mParent;
    private View mTopView;
    private LinearLayout mLeftView;
    private TableLayout mTable;
    private TableRow[] mTableRow;
    //private TableRow[] mChildTableRow;
    private LinearLayout[][] mChildTableRow;
    private View[] mTopLine;
    private View[][] mLines;
    private ImageView[][][] mColors;
    private static MeetingSelectionScrollView mScrollView;
    private MeetingSelectionTopFragment topFragment;
    //store the number of people who are available in the specific period of time (24 * 60)
    private JSONArray mAvailability;
    private boolean[][] mIsAvailable;
    private Preference[] mPreference;

    private ArrayList<String> mFriendIDS;
    private JsonManager mJsonManager;

    private int DAYS;
    private String mStartDateForPost;
    private String mEndDateForPost;
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;

    private int HEIGHTOFTOPLINE = 5;
    private int MARGINOFTOPLINE = 2; //5

    private int WEIGHTOFLINE = 1;
    private int MARGINOFLINE = 2;   //2

    private int SIZEOFCOLORSQUARE = 120;
    private int MARGINEOFSQAURE = 10; //10

    private int MARGINOFCHILDTABLEROW = 2;
    private int PADDINGOFCOLORSQUARE = 10;
    private int DURATION;

    private String COLOROFTOPLINE = "#87CEFA";
    private String COLOROFLINE = "#87CEFA";
    private String COLOROFTABLE = "#7CFC00";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initParameters(savedInstanceState);
        mParent = inflater.inflate(R.layout.meeting_selection_center_fragment, null);
        mTopView = getActivity().getLayoutInflater().inflate(R.layout.meeting_selection_top_fragment, null);
        init();
        initLeftView();
//        initTable();
        getPreference();
        getAvailability();
        return mParent;
    }

    private void initParameters(Bundle savedInstanceState){
        savedInstanceState = getArguments();
        DAYS  = savedInstanceState.getInt("totaldays");
        DURATION = savedInstanceState.getInt("duration");
        mFriendIDS = savedInstanceState.getStringArrayList("friendIDs");
        mStartDateForPost = savedInstanceState.getString("startdate");
        mEndDateForPost = savedInstanceState.getString("enddate");
        mStartYear = savedInstanceState.getInt("startyear");
        mStartMonth = savedInstanceState.getInt("startmonth");
        mStartDay = savedInstanceState.getInt("startday");
    }

    public void init(){
        mJsonManager = new JsonManager();
        mLeftView = (LinearLayout) mParent.findViewById(R.id.meeting_selection_left);
        mTable = (TableLayout) mParent.findViewById(R.id.meeting_selection_center_table);
        mScrollView = (MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_center_scroll);
        mScrollView.setOnScrollViewListener(this);
    }

    public void initLeftView(){
        TextView[] textView = new TextView[24];
        int height = 0;
        if(DURATION <= 60){
            height = SIZEOFCOLORSQUARE + (60 / DURATION - 1) * MARGINEOFSQAURE * 2 + 2 * MARGINEOFSQAURE
                        + 2 * MARGINOFTOPLINE + HEIGHTOFTOPLINE;
        }else{
            height = SIZEOFCOLORSQUARE + HEIGHTOFTOPLINE;
        }
        //Format the left side view
        for(int i = 0; i < 24; i ++) {
            textView[i] = new TextView(getActivity());
            textView[i].setText(i < 10 ? "0" + i + ":00" : i + ":00");
            textView[i].setHeight(height);
            mLeftView.addView(textView[i]);
        }
    }

    public void initTable(){
        mTableRow = new TableRow[24];
        mTopLine = new View[24];
        mLines = new View[24][DAYS];
        mChildTableRow = new LinearLayout[24][DAYS];
        mIsAvailable = new boolean[24][DAYS];
        //mColors = new ImageView[24][DAYS];
        TableLayout.LayoutParams toplinepara = null;
        TableRow.LayoutParams linepara = null;
        TableRow.LayoutParams tablepara = null;
//        TableRow.LayoutParams childTablePara = null;

        int parts = 1;
        if(DURATION <= 60){
            parts = 60 / DURATION;
            toplinepara = new TableLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,HEIGHTOFTOPLINE);
            toplinepara.setMargins(MARGINOFTOPLINE,MARGINOFTOPLINE,MARGINOFTOPLINE,MARGINOFTOPLINE);
            linepara = new TableRow.LayoutParams(WEIGHTOFLINE,
                    TableRow.LayoutParams.MATCH_PARENT);
            //Top and bottom margins are 0.
            linepara.setMargins(MARGINEOFSQAURE,0,MARGINOFLINE,0);
            //The layout parameters of ImageViews
            tablepara = new TableRow.LayoutParams(SIZEOFCOLORSQUARE,
                    SIZEOFCOLORSQUARE/parts);
            tablepara.setMargins(MARGINEOFSQAURE,MARGINEOFSQAURE,MARGINEOFSQAURE,MARGINEOFSQAURE);

            //e.g. meeting duration is 20 mins, then the table row is divided into 60 / 20 = 3
            //parts. Each part will contain certain ImageView

            mColors = new ImageView[24][DAYS][parts];
            //The layout parameters of each "big" table row
//            childTablePara = new TableRow.LayoutParams(SIZEOFCOLORSQUARE
//                    ,SIZEOFCOLORSQUARE);
//            childTablePara.setMargins(MARGINOFCHILDTABLEROW, MARGINOFCHILDTABLEROW,
//                    MARGINOFCHILDTABLEROW, MARGINOFCHILDTABLEROW);


        }else{
            mColors = new ImageView[24][DAYS][parts];
            toplinepara = new TableLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,HEIGHTOFTOPLINE);
            toplinepara.setMargins(0,0,MARGINOFLINE,0);
            linepara = new TableRow.LayoutParams(WEIGHTOFLINE,
                    TableRow.LayoutParams.MATCH_PARENT);
            linepara.setMargins(MARGINOFLINE,0,MARGINOFLINE,0);
            //The layout parameters of ImageViews
            tablepara = new TableRow.LayoutParams(SIZEOFCOLORSQUARE,
                    SIZEOFCOLORSQUARE/parts);
            tablepara.setMargins(MARGINEOFSQAURE,0,MARGINEOFSQAURE,0);
//            childTablePara = new TableRow.LayoutParams(SIZEOFCOLORSQUARE
//                    ,SIZEOFCOLORSQUARE);
//            childTablePara.setMargins(0, 0, 0, 0);
        }

        //24 means 24hours(rows), the vaule of DAYS depends on the difference between start days
        //and end days.
        for(int i = 0; i < 24; i ++){
            mTableRow[i] = new TableRow(getActivity());
            mTopLine[i] = new View(getActivity());
            mTopLine[i].setLayoutParams(toplinepara);
            mTopLine[i].setBackgroundColor(Color.parseColor(COLOROFTOPLINE));
            //add top line
            mTable.addView(mTopLine[i]);

            for(int j = 0; j < DAYS; j ++){
                mLines[i][j] = new View(getActivity());
                mLines[i][j].setLayoutParams(linepara);
                mLines[i][j].setBackgroundColor(Color.parseColor(COLOROFLINE));
                mChildTableRow[i][j] = new LinearLayout(getActivity());
                mChildTableRow[i][j].setOrientation(LinearLayout.VERTICAL);
                //mChildTableRow[i][j].setLayoutParams(childTablePara);
                for(int k = 0; k < parts; k ++){
                    mColors[i][j][k] = new ImageView(getActivity());
                    mColors[i][j][k].setLayoutParams(tablepara);
                    if(checkAvailability(i, k * 60 / parts, i, (k + 1) * 60 /parts, j)) {
                        //If the background color is invisible, which means the period of the time is unavailable.
                        mColors[i][j][k].setBackgroundColor(Color.parseColor(COLOROFTABLE));
                        if(DURATION > 60){
                            mIsAvailable[i][j] = true;
                        }else{
                            mColors[i][j][k].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                        }
                    }else{
                        mColors[i][j][k].setBackgroundColor(Color.parseColor("#CCCCCC"));
                        mIsAvailable[i][j] = false;
                    }

                    mChildTableRow[i][j].addView(mColors[i][j][k]);

                }
                mTableRow[i].addView(mChildTableRow[i][j]);
                mTableRow[i].addView(mLines[i][j]);
            }
            mTable.addView(mTableRow[i]);
        }
        if(DURATION > 60){
            setListener();
        }
    }

    private void setListener(){
        int chunk = DURATION / 60;
        for(int i = 0; i < DAYS; i ++){
            boolean isSetPadding = false;
            int count = 0;
            int start,end;
            for(int j = 0; j < 24; j ++){
                if(mIsAvailable[j][i]){
                    start = j;
                    end = (j + DURATION / 60) >= 24 ? 24 : (j + DURATION / 60);
                    count ++;
                    if(count >= chunk){
                        isSetPadding = true;
                        count = 0;
                    }
                    if(isSetPadding) {
                        mChildTableRow[j][i].setPadding(0, PADDINGOFCOLORSQUARE, 0, 0);
                        isSetPadding = false;
                    }

                }else {
                    if(isSetPadding == false && j - 1 >= 0){
                        mChildTableRow[j - 1][i].setPadding(0,0,0,PADDINGOFCOLORSQUARE);
                    }
                    isSetPadding = true;
                    count = 0;
                }
            }
        }
    }
    // 在上面 一边循环一边check，如果上面的颜色是白色那么 setPadding = X, 如果下面是白色设置 setPadding = x （或者超出了duration 也是这么设置）
    //设置一个flag, 两个白色之间的组建 监听事件是一样的
    private boolean checkAvailability(int startHour, int startMin, int endHour, int endMin, int currentDay){
        int[] date = {mStartYear,mStartMonth,mStartDay};
        for(int i = 0; i < currentDay; i ++){
            date = DateUtil.addDaysBasedOnCalendar(date[0], date[1], date[2], 1);
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String startTimeFormat = date[0] + "-" + date[1] + "-" + date[2] + " " + startHour + ":" +
                    startMin + ":00";
            String endTimeFormat = date[0] + "-" + date[1] + "-" + date[2] + " " + endHour + ":" +
                    endMin + ":00";
            Date currentStartTime = formatter.parse(startTimeFormat);
            Date currentEndTime = formatter.parse(endTimeFormat);

            for(int i = 0; i < mAvailability.length(); i ++){
                //mAvailability.get(i)
                JSONObject object = (JSONObject) mAvailability.get(i);
                Date targetStartTime = DateUtil.getLocalTime(object.get("starts_time").toString());
                Date targetEndTime = DateUtil.getLocalTime(object.get("ends_time").toString());
                if(targetStartTime.getTime() <= currentStartTime.getTime() &&
                        targetEndTime.getTime() >= currentEndTime.getTime()){
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void getPreference(){
        try {
            JSONArray friendID = new JSONArray();
            for(String ids : mFriendIDS){
                friendID.put(ids);
            }
            String url = "http://www.kooyear.com/iTIME_Server/load_frds_prefer_preferences";
            JSONObject post = new JSONObject();
            post.put("friend_id", friendID);
            requestJSONArray(mJsonManager, post, url, "load_frds_prefer_preferences");
            handleJSON(mJsonManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doGetPreference(JSONArray jsonArray){
        mPreference = new Preference[jsonArray.length()];
        try {
            for(int i = 0; i < jsonArray.length(); i ++){
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                mPreference[i] = new Preference();
                mPreference[i].setStarts_time(DateUtil.getLocalTime(jsonObject.get("starts_time").toString()));
                mPreference[i].setEnds_time(DateUtil.getLocalTime(jsonObject.get("ends_time").toString()));
                mPreference[i].setRepeat_type(jsonObject.get("repeat_type").toString());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getAvailability(){
        try {
            JSONArray friendID = new JSONArray();
            for(String ids : mFriendIDS){
                friendID.put(ids);
            }
            String url = "http://www.kooyear.com/iTIME_Server/match_time_with_friends";
            String duration = "";
            JSONObject post = new JSONObject();
            post.put("user_id",new User().getID());
            switch (DURATION){
                case 10:
                    duration = "10mins";break;
                case 15:
                    duration = "15mins";break;
                case 30:
                    duration = "30mins";break;
                case 60:
                    duration = "1hrs";break;
                case 120:
                    duration = "2hrs";break;
                case 360:
                    duration = "6hrs";break;
            }
            post.put("duration", duration);
            post.put("starts", URLConnectionUtil.encode(mStartDateForPost));
            post.put("events",new JSONArray());
            post.put("ends",URLConnectionUtil.encode(mEndDateForPost));
            post.put("friend_id",friendID);
            requestJSONArray(mJsonManager, post, url, "match_time_with_friends");
            handleJSON(mJsonManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getColor(){
        String color = null;

        return color;

    }

    public static void setPosition(int x, int y){
        mScrollView.scrollTo(x,y);
    }

    @Override
    public void onScrollChanged(MeetingSelectionScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (scrollView == mScrollView) {
            topFragment.setPosition(x,y);
        }
    }

    @Override
    public void handleJSON(JsonManager manager) {
        MySingleton.getInstance(getActivity()).getRequestQueue().addRequestFinishedListener(
                new RequestQueue.RequestFinishedListener<String>() {
                    @Override
                    public void onRequestFinished(Request<String> request) {
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        HashMap map;
                        while ((map = mJsonManager.getJsonQueue().poll()) != null) {
                            if ((jsonArray = (JSONArray) map.get("load_frds_prefer_preferences")) != null) {
                                doGetPreference(jsonArray);
                            }
                            if ((jsonArray = (JSONArray) map.get("match_time_with_friends")) != null) {
                                mAvailability = jsonArray;
                                initTable();
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url, jsonObject, getActivity(), tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, getActivity(),tag);
    }
}
