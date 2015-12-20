package com.itime.team.itime.views.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by mac on 15/12/20.
 */
public class MyImageButton extends LinearLayout {
    private ImageView mImage;
    private TextView mText;

    public MyImageButton(Context context, AttributeSet attrs) {
        super(context);

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        setMinimumWidth(wm.getDefaultDisplay().getWidth());

        mImage = new ImageView(context, attrs);

        mImage.setPadding(0, 0, 0, 0);

        mText = new TextView(context, attrs);

        mText.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

        mText.setPadding(50, 50, 50, 50);

        setClickable(true);

        setFocusable(true);

        setBackgroundResource(android.R.drawable.list_selector_background);

        setOrientation(LinearLayout.HORIZONTAL);

        addView(mImage);

        addView(mText);
    }

}
