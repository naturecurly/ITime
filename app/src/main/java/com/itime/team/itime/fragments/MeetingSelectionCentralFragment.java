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
    private AbsoluteLayout mMainLayout;
    private LinearLayout mColorsContainer;
    private LinearLayout[] mColumn;
    private View[] mLines;
    private ArrayList<ImageView>[] mColors;
    private ArrayList<LinearLayout>[] mEachCOntainer;
    private static MeetingSelectionScrollView mScrollView;
    private MeetingSelectionTopFragment topFragment;
    //store the number of people who are available in the specific period of time (24 * 60)
    private JSONArray mAvailability;
    private boolean[][] mIsAvailable;
    private Preference[] mPreference;
    private int checkDatePointer;

    private ArrayList<String> mFriendIDS;
    private JsonManager mJsonManager;

    private int DAYS;
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

    private int MARGINOFCHILDTABLEROW = 2;
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
        if(DAYS < 6 && DAYS > 0){
            WIDTHOFCOLORSQUARE = WIDTHOFCENTERLAYOUT / DAYS;
        }
        double parts = 60.0 / DURATION;
        TOTALHEIGHT = (int) ((2 * PADDDINGOFSQAURE +
                (parts < 1 ? (int) (HIGHTOFCOLORSQUARE / parts) : (int)(DURATION / 60.0 * HIGHTOFCOLORSQUARE))) *
                        24 * parts);
        WIDTHFOREACH = WIDTHOFCOLORSQUARE + PADDDINGOFSQAURE + WEIGHTOFLINE;
        checkDatePointer = 0;
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
        mIsAvailable = new boolean[24][DAYS];
        mColors = new ArrayList[DAYS];



        double parts = 60.0 / DURATION;
        int size = (int) (HIGHTOFCOLORSQUARE/parts);
        mLinepara = new LinearLayout.LayoutParams(WEIGHTOFLINE, TOTALHEIGHT);
        //Top and bottom margins are 0.
        mLinepara.setMargins(PADDDINGOFSQAURE, 0, PADDDINGOFSQAURE, 0);
        mImagepara = new LinearLayout.LayoutParams(WIDTHOFCOLORSQUARE,
                parts < 1 ? size : (int)(DURATION / 60.0 * HIGHTOFCOLORSQUARE));

        mInitDays = DAYS > 12 ? 12 : DAYS;
        for(int i = 0; i < mInitDays; i ++){
            mColumn[i] = new LinearLayout(getActivity());
            mColumn[i].setOrientation(LinearLayout.VERTICAL);
            mColumn[i].setMinimumWidth(WIDTHFOREACH);
            mColors[i] = new ArrayList<ImageView>();
            mLines[i] = new View(getActivity());
            mLines[i].setLayoutParams(mLinepara);
            mLines[i].setBackgroundColor(Color.parseColor(COLOROFLINE));
            int part = DURATION > 60 ? 1 : 60 / DURATION;
            int index = 0;
            int unMatchedNumber = 0;
            for(int j = 0; j <= 24 * part; j ++){
                LinearLayout container = new LinearLayout(getActivity());
                ImageView imageView = new ImageView(getActivity());
                ImageView defaultView = new ImageView(getActivity());
                if(checkAvailability(j / part, DURATION > 60 ? 0 : j % part * DURATION, DURATION, i)){
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
                        container.addView(mColors[i].get(index ++));
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
        }
    }

    private void addView(){
        mColumn[mInitDays] = new LinearLayout(getActivity());
        mColumn[mInitDays].setOrientation(LinearLayout.VERTICAL);
        mColors[mInitDays] = new ArrayList<ImageView>();
        mLines[mInitDays] = new View(getActivity());
        mLines[mInitDays].setLayoutParams(mLinepara);
        mLines[mInitDays].setBackgroundColor(Color.parseColor(COLOROFLINE));
        int part = DURATION > 60 ? 1 : 60 / DURATION;
        int index = 0;
        int unMatchedNumber = 0;
        if(mInitDays < DAYS){
            for(int j = 0; j < 24 * part; j ++){
                LinearLayout container = new LinearLayout(getActivity());
                ImageView imageView = new ImageView(getActivity());
                if(checkAvailability(j / part, DURATION > 60 ? 0 : j % part * DURATION, DURATION, mInitDays)){
                    j += (DURATION/60 == 0 ? 1: DURATION/60) - 1;
                    if(j + 1 < 24 * part) {

                        imageView.setBackgroundColor(Color.parseColor(COLOROFTABLE));
                        imageView.setLayoutParams(mImagepara);
                    }
                }else{
                    unMatchedNumber ++;
                }
                container.setPadding(0,PADDDINGOFSQAURE,0,PADDDINGOFSQAURE);
                mColors[mInitDays].add(imageView);
                container.addView(mColors[mInitDays].get(index ++));
                mColumn[mInitDays].addView(container);
            }
            mColorsContainer.addView(mColumn[mInitDays]);
            mColorsContainer.addView(mLines[mInitDays]);
        }

        mInitDays ++;
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
            for (int i = checkDatePointer; i < mAvailability.length(); i ++){
                //mAvailability.get(i)
                JSONObject object = (JSONObject) mAvailability.get(checkDatePointer);
                Date targetStartTime = DateUtil.getLocalTime(object.get("starts_time").toString());
                Date targetEndTime = DateUtil.getLocalTime(object.get("ends_time").toString());
                if(targetStartTime.getTime() <= currentStartTime.getTime() &&
                        targetEndTime.getTime() >= currentEndTime.getTime()){
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
            if(mInitDays * WIDTHFOREACH <= x + (12 * WIDTHFOREACH) && mInitDays < DAYS){
                addView();
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
