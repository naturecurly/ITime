package com.itime.team.itime.views.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mac on 15/12/20.
 */
public class ImageButton_Text_Image extends LinearLayout {
    private ImageView mImage;
    private TextView mText;

    public ImageButton_Text_Image(Context context, int ImageID, String textID) {
        super(context);
        init(context, ImageID);
        setText(textID);
        addView(mImage);
        addView(mText);
    }

    public ImageButton_Text_Image(Context context, int ImageID, int textID) {
        super(context);
        init(context, ImageID);
        setText(textID);
        addView(mText);
        addView(mImage);
    }

    private void init(Context context, int ImageID){
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        setMinimumWidth(wm.getDefaultDisplay().getWidth());

        mImage = new ImageView(context);

        //mImage.setPadding(0, 0, 0, 0);

        mImage.setFocusable(false);

        mImage.setDuplicateParentStateEnabled(true);

        mText = new TextView(context);

        mText.setGravity(Gravity.CENTER);

        mText.setPadding(0, 20, 0, 0);

        mText.setFocusable(false);

        mText.setDuplicateParentStateEnabled(true);


        setClickable(true);

        setFocusable(true);

        setBackgroundResource(android.R.drawable.list_selector_background);

        setOrientation(LinearLayout.HORIZONTAL);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        setImageResource(ImageID);


    }

    public void setImageResource(int resId) {
        mImage.setImageResource(resId);
    }

    public void setText(int resId) {
        mText.setText(resId);
    }

    public void setText(CharSequence buttonText) {
        mText.setText(buttonText);
    }

    public void setTextColor(int color) {
        mText.setTextColor(color);
    }

    public void setTextPadding(int start, int top, int end, int bottom){
        mText.setPadding(start, top, end, bottom);
    }
    public void setImagePadding(int start, int top, int end, int bottom){
        mImage.setPadding(start,top,end,bottom);
    }
}
