package com.itime.team.itime.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.utils.DensityUtil;

/**
 * Created by leveyleonhardt on 4/21/16.
 */
public class CustomizedTextView extends TextView {


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 5));

        paint.setColor(ContextCompat.getColor(getContext(), R.color.event_color_01_line));

        canvas.drawLine(0, 0, 0, this.getHeight(), paint);
    }

    public CustomizedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomizedTextView(Context context) {
        super(context);
    }
}
