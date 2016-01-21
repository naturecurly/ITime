package com.itime.team.itime.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.itime.team.itime.activities.R;
import com.itime.team.itime.bean.Preference;
import com.itime.team.itime.bean.URLs;
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
 * The function of this fragment is to present the friends' preferences of meeting time.
 * Each Green View is an ImageView which is contained by a LinearLayout (container) to control the
 * padding of the view. The invisible part of the fragment (we call this part as default), is
 * still a ImageView, but the continuous part is a single ImageView, rather than the combination of
 * many ImageViews.
 */

public class MeetingSelectionCentralFragment extends Fragment implements ScrollViewListener, DataRequest {
    private Intent intent;
    private View mParent;
    private View mTopView;
    private LinearLayout mLeftView;
    private AbsoluteLayout mMainLayout;
    private LinearLayout mColorsContainer;
    private LinearLayout[] mColumn;
    private View[] mLines;
    private ArrayList<ImageView>[] mColors;
    private static MeetingSelectionScrollView mScrollView;
    private MeetingSelectionTopFragment topFragment;
    //store the number of people who are available in the specific period of time (24 * 60)
    private JSONArray mAvailability;
    private Preference[] mPreference;
    //This variable is virtual pointer which points to a sorted array (which is a jsonArray and sorted
    // by time).When satisfying a condition, the pointer moves forward.
    private int checkDatePointer;
    private boolean mIsAvailable;
    private Date mEachEndDate;

    private ArrayList<String> mFriendIDS;
    private JsonManager mJsonManager;

    //The total days of meeting period.
    private int DAYS;
    //Formats for post to server side
    private String mStartDateForPost;
    private String mEndDateForPost;
    private int mStartYear;
    private int mStartMonth;
    private int mStartDay;
    private int mInitDays;

    private LinearLayout.LayoutParams mImagepara = null;
    private LinearLayout.LayoutParams mDefaultImagePara = null;
    private LinearLayout.LayoutParams mLinepara = null;


    private int WEIGHTOFLINE = 1;

    private int WIDTHOFCOLORSQUARE = 120;
    private int HIGHTOFCOLORSQUARE = 120;
    private int PADDDINGOFSQAURE = 10;
    private int TOTALHEIGHT;
    private int WIDTHFOREACH;

    public static int WIDTHOFCENTERLAYOUT = 800;

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
//        getPreference();
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
        //When the total days are less than 6, then we averagely separate the width to make screen full
        if(DAYS < 6 && DAYS > 0){
            WIDTHOFCOLORSQUARE = WIDTHOFCENTERLAYOUT / DAYS;
        }
        //The values of duration might be one of 10,15,30,60,120,360
        double parts = 60.0 / DURATION;
        TOTALHEIGHT = (int) ((2 * PADDDINGOFSQAURE +
                (parts < 1 ? (int) (HIGHTOFCOLORSQUARE / parts) : (int)(DURATION / 60.0 * HIGHTOFCOLORSQUARE))) *
                        24 * parts);
        WIDTHFOREACH = WIDTHOFCOLORSQUARE +  PADDDINGOFSQAURE + WEIGHTOFLINE;
        checkDatePointer = 0;
        mIsAvailable = false;
    }

    public void init(){
        mJsonManager = new JsonManager();
        mLeftView = (LinearLayout) mParent.findViewById(R.id.meeting_selection_left);
        mMainLayout = (AbsoluteLayout) mParent.findViewById(R.id.meeting_selection_center_absolute);
        mColorsContainer = (LinearLayout) mParent.findViewById(R.id.meeting_selection_center_date);
        mScrollView = (MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_center_scroll);
        mScrollView.setOnScrollViewListener(this);
    }

    public void initLeftView(){
        TextView[] textView = new TextView[24];
        //Format the left side view
        for(int i = 0; i < 24; i ++) {
            textView[i] = new TextView(getActivity());
            textView[i].setText(i < 10 ? "0" + i + ":00" : i + ":00");
            textView[i].setHeight(TOTALHEIGHT / 24);
            mLeftView.addView(textView[i]);
        }
    }

    public void initTable(){
        mColumn = new LinearLayout[DAYS];
        mLines = new View[DAYS];
        mColors = new ArrayList[DAYS];

        double parts = 60.0 / DURATION;
        int size = (int) (HIGHTOFCOLORSQUARE/parts);
        mLinepara = new LinearLayout.LayoutParams(WEIGHTOFLINE, TOTALHEIGHT);
        //Top and bottom margins are 0.
        mLinepara.setMargins(PADDDINGOFSQAURE, 0, PADDDINGOFSQAURE, 0);
        mImagepara = new LinearLayout.LayoutParams(WIDTHOFCOLORSQUARE,
                parts < 1 ? size : (int)(DURATION / 60.0 * HIGHTOFCOLORSQUARE));
        //12 means that we initialize 12 days' data at beginning
        mInitDays = DAYS > 12 ? 12 : DAYS;
        for(int i = 0; i < mInitDays; i ++){
            addView(i, true);
        }
    }
    /*
        Since the operations of initializing the 12 views and add views are the same, the two functions
        are programmed together.
        If isInit is true means that initializing 12 views, otherwise, do add view operation.
     */
    private void addView(int i, boolean isInit){

        double parts = 60.0 / DURATION;
        int size = (int) (HIGHTOFCOLORSQUARE/parts);
        if(isInit == false){
            i = mInitDays;
        }
        mColumn[i] = new LinearLayout(getActivity());
        mColumn[i].setOrientation(LinearLayout.VERTICAL);
        mColumn[i].setMinimumWidth(WIDTHFOREACH - WEIGHTOFLINE - PADDDINGOFSQAURE);
        mColors[i] = new ArrayList<ImageView>();
        mLines[i] = new View(getActivity());
        mLines[i].setLayoutParams(mLinepara);
        mLines[i].setBackgroundColor(Color.parseColor(COLOROFLINE));
        int part = DURATION > 60 ? 1 : 60 / DURATION;
        int index = 0;

        //Calculate the number of periods of time which cannot match the target data, it contributes
        //to calculate the height of default view. Once one item matches, then this variable should be
        //cleared.
        int unMatchedNumber = 0;
        for(int j = 0; j <= 24 * part; j ++){
            LinearLayout container = new LinearLayout(getActivity());
            ImageView imageView = new ImageView(getActivity());
            ImageView defaultView = new ImageView(getActivity());
            //Change the current values j to current time, we should the formula below.
            final int hour = j / part;
            final int min = DURATION > 60 ? 0 : j % part * DURATION;
            mIsAvailable = mEachEndDate == null ? false : priorityCheckAvailability(hour, min, i);
            if(mIsAvailable || checkAvailability(hour, min, DURATION, i)){
                //Since after each loop, the value j is increased by 1. In order to use j correctly,
                //just minus 1 temporarily. The meaning of this statement is that: Since some ImageView
                //is bigger that 1 hour's length, it has to be done that increase j to match the current
                //time.
                j += (DURATION/60 == 0 ? 1: DURATION/60) - 1;
                if(j + 1 <= 24 * part) {
                    mDefaultImagePara = new LinearLayout.LayoutParams(WIDTHOFCOLORSQUARE,
                            DURATION >= 60 ? TOTALHEIGHT / 24 * unMatchedNumber :
                                    unMatchedNumber * (2 * PADDDINGOFSQAURE + size));
                    defaultView.setLayoutParams(mDefaultImagePara);
                    imageView.setBackgroundColor(Color.parseColor(COLOROFTABLE));
                    imageView.setLayoutParams(mImagepara);
                    mColors[i].add(imageView);

                    container.setPadding(0, PADDDINGOFSQAURE, 0, PADDDINGOFSQAURE);
                    container.addView(mColors[i].get(index++));
                    mColors[i].get(index - 1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), hour + " :" + min, Toast.LENGTH_SHORT).show();
                        }
                    });
                    mColumn[i].addView(defaultView);
                    mColumn[i].addView(container);
                    unMatchedNumber = 0;
                }
            }else{
                unMatchedNumber ++;
            }
        }
        mColorsContainer.addView(mColumn[i]);
        mColorsContainer.addView(mLines[i]);
        if(isInit == false) {
            mInitDays++;
        }
    }

    //Check two dates which one is latter based on Year,month, day, hour and minute
    //The logic and structure seems to be complicated, but in this way, which can improve the efficiency of the program.
    private boolean priorityCheckAvailability(int startHour, int startMin, int currentDay){
        int[] date = {mStartYear,mStartMonth,mStartDay};
        for(int i = 0; i < currentDay; i ++){
            date = DateUtil.addDaysBasedOnCalendar(date[0], date[1], date[2], 1);
        }
        if(date[0] < mEachEndDate.getYear() + 1900){
            return true;
        }else if(date[0] > mEachEndDate.getYear() + 1900){
            return false;
        }else{
            if(date[1] < mEachEndDate.getMonth() + 1){
                return true;
            }else if(date[1] > mEachEndDate.getMonth() + 1){
                return false;
            }else{
                if(date[2] < mEachEndDate.getDate()){
                    return true;
                }else if(date[2] > mEachEndDate.getDate()){
                    return false;
                }else{
                    if(startHour < mEachEndDate.getHours()){
                        return true;
                    }else if(startHour > mEachEndDate.getHours()){
                        return false;
                    }else{
                        if(startMin <= mEachEndDate.getMinutes()){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }
        }
    }

    private boolean checkAvailability(int startHour, int startMin, long duration, int currentDay){
        int[] date = {mStartYear,mStartMonth,mStartDay};
        for(int i = 0; i < currentDay; i ++){
            date = DateUtil.addDaysBasedOnCalendar(date[0], date[1], date[2], 1);
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String startTimeFormat = date[0] + "-" + date[1] + "-" + date[2] + " " + startHour + ":" +
                    startMin + ":00";
            Date currentStartTime = formatter.parse(startTimeFormat);
            Date currentEndTime = new Date(currentStartTime.getTime() + duration * 60 * 1000);
            //Here is the implementation of the pointer.
            for (int i = checkDatePointer; i < mAvailability.length(); i ++){
                //mAvailability.get(i)
                JSONObject object = (JSONObject) mAvailability.get(checkDatePointer);
                Date targetStartTime = DateUtil.getLocalTime(object.get("starts_time").toString());
                Date targetEndTime = DateUtil.getLocalTime(object.get("ends_time").toString());
                if(targetStartTime.getTime() <= currentStartTime.getTime() &&
                        targetEndTime.getTime() >= currentEndTime.getTime()){
                    mEachEndDate = targetEndTime;
                    mIsAvailable = true;
//                    Log.i("FOrmat", mEachEndDate.toString());
                    Log.i("mEachEndDate",mEachEndDate.getYear() + " " + mEachEndDate.getMonth() + " " +
                    mEachEndDate.getDate() + " - " + mEachEndDate.getHours() + " : " + mEachEndDate.getMinutes());
                    return true;
                }else{
                    if(currentStartTime.getTime() > targetEndTime.getTime()){
                        checkDatePointer ++;
                    }else{
                        return false;
                    }
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
            String url = URLs.LOAD_FRDS_PERFER_PREFERENCES;
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
            String url = URLs.MATCH_TIME_WITH_FRIENDS;
            String duration = "";
            JSONObject post = new JSONObject();
            post.put("user_id",User.ID);
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
            if(mInitDays * WIDTHFOREACH <= x + (12 * WIDTHFOREACH) && mInitDays < DAYS){
                addView(0,false);
            }
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
                                Log.i("json",jsonArray.toString());
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
