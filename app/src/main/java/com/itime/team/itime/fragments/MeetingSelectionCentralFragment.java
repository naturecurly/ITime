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

import com.itime.team.itime.activities.R;
import com.itime.team.itime.listener.ScrollViewListener;
import com.itime.team.itime.views.MeetingSelectionScrollView;

/**
 * Created by mac on 15/12/27.
 */
public class MeetingSelectionCentralFragment extends Fragment implements ScrollViewListener {
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
    private int[][] availability;

    private int DAYS;
    private int HEIGHTOFTOPLINE = 5;
    private int MARGINOFTOPLINE = 2; //5

    private int WEIGHTOFLINE = 1;
    private int MARGINOFLINE = 2;   //2

    private int SIZEOFCOLORSQUARE = 120;
    private int MARGINEOFSQAURE = 10; //10

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
        initTable();
        return mParent;
    }
    private void initParameters(Bundle savedInstanceState){
        savedInstanceState = getArguments();
        DAYS  = savedInstanceState.getInt("totaldays");
        DURATION = savedInstanceState.getInt("duration");
    }
    public void init(){
        mLeftView = (LinearLayout) mParent.findViewById(R.id.meeting_selection_left);
        mTable = (TableLayout) mParent.findViewById(R.id.meeting_selection_center_table);
        mScrollView = (MeetingSelectionScrollView) mParent.findViewById(R.id.meeting_selection_center_scroll);
        mScrollView.setOnScrollViewListener(this);
        availability = new int[24][60];
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
                    //If the background color is invisible, which means the period of the time is unavailable.
                    mColors[i][j][k].setBackgroundColor(Color.parseColor(COLOROFTABLE));
                    mChildTableRow[i][j].addView(mColors[i][j][k]);
                }
                mTableRow[i].addView(mChildTableRow[i][j]);
                mTableRow[i].addView(mLines[i][j]);
            }


//            for(int j = 0; j < DAYS; j ++){
//                mLines[i][j] = new View(getActivity());
//                mLines[i][j].setLayoutParams(linepara);
//                mLines[i][j].setBackgroundColor(Color.parseColor(COLOROFLINE));
//                mColors[i][j] = new ImageView(getActivity());
//                mColors[i][j].setLayoutParams(tablepara);
//                //If the background color is invisible, which means the period of the time is unavailable.
//                mColors[i][j].setBackgroundColor(Color.parseColor(COLOROFTABLE));
//                mTableRow[i].addView(mColors[i][j]);
//                mTableRow[i].addView(mLines[i][j]);
//            }
            mTable.addView(mTableRow[i]);
        }
    }
    // 在上面 一边循环一边check，如果上面的颜色是白色那么 setPadding = X, 如果下面是白色设置 setPadding = x （或者超出了duration 也是这么设置）
    //设置一个flag, 两个白色之间的组建 监听事件是一样的
    private void checkAvailability(){

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
}
