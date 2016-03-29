package com.itime.team.itime.views;

/**
 * Created by leveyleonhardt on 12/16/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.itime.team.itime.R;
import com.itime.team.itime.listener.OnDateSelectedListener;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.DensityUtil;

import java.util.Calendar;

public class CalendarView extends View {

    private static final String TAG = "CalendarView";

    public static final int MONTH_STYLE = 0;
    public static final int WEEK_STYLE = 1;

    private static final int TOTAL_COL = 7;
    private static final int TOTAL_ROW = 6;

    private int isDateSelected = 0;
    private Paint mCirclePaint;
    private Paint mSelectedCirclePaint;

    private Paint mTextPaint;
    private Paint mGridPaint;
    private Paint mLinePaint;
    private Paint mMonthTextPaint;
    private Paint mYearViewMonthTextPaint;
    private Paint mPointPaint;
    private int mViewWidth;
    private int mViewHight;
    private float mCellSpace;
    private Row rows[] = new Row[TOTAL_ROW];
    private Context context;
    private Bitmap bm;
    private int mShowYear;
    private int mShowMonth;
    private int mShowDay;
    protected int defaultStyle = WEEK_STYLE;
    private static final int WEEK = 7;

    float downX = 0;
    float downY = 0;
    float upX = 0;
    float upY = 0;
    int dateX = 0;
    int dateY = 0;
    private OnDateSelectedListener listener;
    private boolean isInitialed = false;
    private boolean[] ifEvents = new boolean[7];
    private boolean isTodayClicked = false;
    private boolean isTodayHasEvents = false;

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomedCalendarView, 0, 0);
        try {

            this.defaultStyle = array.getInteger(R.styleable.CustomedCalendarView_calendar_type, 1);
        } finally {
            array.recycle();
        }
        init(context);

    }

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, int style) {
        super(context);
        this.defaultStyle = style;
        init(context);
    }

    public CalendarView(Context context, int year, int month, int day) {
        super(context);
        this.mShowYear = year;
        this.mShowMonth = month;
        this.mShowDay = day;
        isInitialed = true;
        init(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (defaultStyle == MONTH_STYLE) {
            canvas.drawText(DateUtil.month[mShowMonth - 1], mCellSpace / 4, mCellSpace, mYearViewMonthTextPaint);
        }
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null)
                rows[i].drawCells(canvas, i);
        }
        if (isDateSelected == 1) {
            if (!isTodayClicked) {
                dateX = DateUtil.analysePosition(upX, mCellSpace);
                dateY = DateUtil.analysePosition(upY, mCellSpace);
            }
            canvas.drawCircle(dateX * mCellSpace + mCellSpace / 2, dateY * mCellSpace + mCellSpace / 2, mCellSpace / 3, mSelectedCirclePaint);
            //mShowDay = 2;
            isDateSelected = 0;
            isTodayClicked = false;
        }
    }

    private void init(Context context) {
        //bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_calendar_circle);
        this.context = context;

        mMonthTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mMonthTextPaint.setColor(Color.rgb(255, 165, 0));
        //mMonthTextPaint.setTextSize(dip2px(context, 10));

        mYearViewMonthTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYearViewMonthTextPaint.setColor(Color.RED);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
//        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mCirclePaint.setStyle(Paint.Style.FILL);
        // mCirclePaint.setColor(Color.parseColor("#FF559CFF"));

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setStrokeWidth(DensityUtil.dip2px(context, 3));

        mSelectedCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedCirclePaint.setStyle(Paint.Style.STROKE);
        mSelectedCirclePaint.setColor(Color.parseColor("#FF559CFF"));
        mSelectedCirclePaint.setStrokeWidth(DensityUtil.dip2px(context, 3));
//        mSelectedCirclePaint.setAlpha(80);

        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mLinePaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        mLinePaint.setColor(Color.GRAY);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setColor(Color.RED);
//        mPointPaint.setStrokeWidth(DensityUtil.dip2px(context, 3));
        if (isInitialed == false)
            initDate();

    }

    private void initDate() {
        if (defaultStyle == MONTH_STYLE) {
            mShowYear = DateUtil.getYear();
            mShowMonth = DateUtil.getMonth();
            mShowDay = 1;
        } else {
            int time[] = DateUtil.getPerviousWeekSunday();
            mShowYear = time[0];
            mShowMonth = time[1];
            mShowDay = time[2];
        }
        fillDate();
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mViewWidth = w;
//        mViewHight = h;
//       // mCellSpace = Math.min((float) mViewHight / TOTAL_ROW, (float) mViewWidth / TOTAL_COL);
//        //mCellSpace = mViewWidth / TOTAL_COL;
//        //mTextPaint.setTextSize(mCellSpace / 3);
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();

                return true;

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                //Log.i("TTTT", x + "+" + y);
                if ((Math.abs(upX - downX) < mCellSpace) && (Math.abs(upY - downY) < mCellSpace)) {
                    Log.i("TTTT", upX + "+" + upY);
//                        if (defaultStyle == WEEK_STYLE) {
                    isDateSelected = 1;
                    listener.dateSelected(upX, upY);
                    invalidate();
//                        }
                }

                return true;
        }
        return super.onTouchEvent(event);


    }

    public int analysePosition(float x) {
        int dateX = (int) Math.floor(x / mCellSpace);
        return dateX;
    }


    public class Row {
        public Cell[] cells = new Cell[TOTAL_COL];

        public void drawCells(Canvas canvas, int j) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null)
                    cells[i].drawSelf(canvas, i, j);
            }

        }

        public Cell[] getCells() {
            return cells;
        }
    }

    public class Cell {
        public String text;
        public State state;
        public int month;
        public int year;
        public boolean hasEvents;

        public Cell(String text, State state, int month, int year, boolean hasEvents) {
            super();
            this.text = text;
            this.state = state;
            this.month = month;
            this.year = year;
            this.hasEvents = hasEvents;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void drawSelf(Canvas canvas, int i, int j) {
            switch (state) {
                case CURRENT_MONTH_DAY:
                    //mTextPaint.setColor(Color.parseColor("#80000000"));
                    break;
                case NEXT_MONTH_DAY:
                case PAST_MONTH_DAY:
                    //mTextPaint.setColor(Color.parseColor("#40000000"));
                    break;
                case TODAY:
                    //mTextPaint.setColor(Color.parseColor("#80000000"));
//                    canvas.drawCircle((float) (mCellSpace * (i + 0.45)),
//                            (float) ((j + 0.8) * mCellSpace), mCellSpace / 2,
//                            mCirclePaint);
                    float center = mCellSpace / 2;
                    float ringWidth = dip2px(context, 5);
                    //mCirclePaint.setStrokeWidth(2);
                    //canvas.drawCircle(mCellSpace * i + center, j * mCellSpace + center, mCellSpace / 2, mCirclePaint);
//                    Matrix matrix = new Matrix();
//                    int pic_width = bm.getWidth();
//                    int pic_height = bm.getHeight();
//                    matrix.postScale(mCellSpace / pic_width, mCellSpace / pic_height);
//                    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, pic_width, pic_height, matrix, true);
//                    canvas.drawBitmap(newbm, i * mCellSpace, j * mCellSpace + (defaultStyle == MONTH_STYLE ? mCellSpace : 0), mCirclePaint);
                    canvas.drawCircle(i * mCellSpace + mCellSpace / 2, j * mCellSpace + mCellSpace / 2, mCellSpace / 3, mCirclePaint);

                    break;
            }

            //canvas.drawRect(i * mCellSpace, j * mCellSpace, (i + 1) * mCellSpace, (j + 1) * mCellSpace, mGridPaint);
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
//            float fontHeight = fm.bottom - fm.top;
//            float textY = mCellSpace - (mCellSpace - fontHeight) / 2 - fm.bottom;
//            float fontWidth = mTextPaint.measureText("11");
//            float textX = (mCellSpace - fontWidth) / 2;
//            Log.d("measure", textX + " " + textY);
//            canvas.drawText(text, i*mCellSpace+ textX, (j + 1) * textY, mTextPaint);
            canvas.drawText(text, i * mCellSpace + mCellSpace / 2,
                    (j + 1) * mCellSpace - mCellSpace / 2 - (fm.ascent - fm.descent) / 2 - dip2px(context, 2) + (defaultStyle == MONTH_STYLE ? mCellSpace : 0), mTextPaint);
//            if (defaultStyle == WEEK_STYLE)
//                canvas.drawLine(i * mCellSpace, j * mCellSpace, (i + 1) * mCellSpace, j * mCellSpace, mLinePaint);
            if (text.equals(1 + "")) {
                canvas.drawText(DateUtil.month[month - 1], i * mCellSpace + mCellSpace / 2, (j + 1) * mCellSpace - mCellSpace * 3 / 4 + (defaultStyle == MONTH_STYLE ? mCellSpace : 0), mMonthTextPaint);

            }
            if (hasEvents) {
                //canvas.drawPoint(i * mCellSpace + mCellSpace / 2, mCellSpace / 2, mPointPaint);
                canvas.drawCircle(i * mCellSpace + mCellSpace / 2, j * mCellSpace + mCellSpace * 3 / 4, DensityUtil.dip2px(context, 2), mPointPaint);
            }
        }

        public String getText() {
            return text;
        }

        public State getState() {
            return state;
        }
    }

    public enum State {
        CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, TODAY;
    }

    private void fillDate() {
        if (defaultStyle == MONTH_STYLE) {
            //fillMonthDate();
        } else {
            fillWeekDate();
        }
        //fillWeekDate();
    }

    private void fillWeekDate() {

        int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
//        rows[1] = new Row();
//        rows[0] = new Row();
        for (int week = 0; week < 1; week++) {
            rows[week] = new Row();
//            if (mShowDay + WEEK - 1 > currentMonthDays) {
//                mShowMonth += 1;
//            }
            for (int i = 0; i < TOTAL_COL; i++) {
                mShowDay += 1;
                if (mShowDay > currentMonthDays) {
                    mShowDay = 1;
                    mShowMonth += 1;
                    if (mShowMonth > 12) {
                        mShowMonth = 1;
                        mShowYear += 1;
                    }
                }
                if (mShowDay == DateUtil.getCurrentMonthDays() &&
                        mShowYear == DateUtil.getYear()
                        && mShowMonth == DateUtil.getMonth()) {
                    rows[week].cells[i] = new Cell(mShowDay + "", State.TODAY, mShowMonth, mShowYear, ifEvents[i]);
                    dateX = i;
                    dateY = 0;
                    isTodayHasEvents = ifEvents[i];
                    continue;
                }
                rows[week].cells[i] = new Cell(mShowDay + "", State.CURRENT_MONTH_DAY, mShowMonth, mShowYear, ifEvents[i]);
            }
        }
    }

//    private void fillMonthDate() {
//        //Log.d("test", mShowDay + " " + mShowMonth + " " + mShowYear);
////        int monthDay = DateUtil.getCurrentMonthDays();
////        Log.d("test_monthDay", monthDay + "");
//        // int lastMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth - 1);
//        // Log.d("test_lastmonthDay", lastMonthDays + "");
//        int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
//        Log.d("test_currentmonthDay", currentMonthDays + "");
//        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowYear, mShowMonth);
//        Log.d("test_firstDayWeek", firstDayWeek + "");
//        boolean isCurrentMonth = false;
//        if (mShowYear == DateUtil.getYear() && mShowMonth == DateUtil.getMonth()) {
//            isCurrentMonth = true;
//        }
//        int time = 0;
//        for (int j = 0; j < TOTAL_ROW; j++) {
//            rows[j] = new Row();
//            for (int i = 0; i < TOTAL_COL; i++) {
//                int postion = i + j * TOTAL_COL + 1;
//                if (postion >= firstDayWeek
//                        && postion < firstDayWeek + currentMonthDays) {
//                    time++;
////                    if (isCurrentMonth && time == monthDay) {
////                        rows[j].cells[i] = new Cell(time + "", State.TODAY, mShowMonth, mShowYear);
////                        continue;
////                    }
//                    rows[j].cells[i] = new Cell(time + "",
//                            State.CURRENT_MONTH_DAY, mShowMonth, mShowYear);
//                    continue;
//                } else {
//                    rows[j].cells[i] = new Cell("", State.CURRENT_MONTH_DAY, mShowMonth, mShowYear);
//                }
////                } else if (postion < firstDayWeek) {
////                    rows[j].cells[i] = new Cell((lastMonthDays - (firstDayWeek
////                            - 2) + postion - 1)
////                            + "", State.PAST_MONTH_DAY, mShowMonth - 1, mShowYear);
////                    continue;
////                } else if (postion >= firstDayWeek + currentMonthDays) {
////                    rows[j].cells[i] = new Cell((postion - firstDayWeek
////                            - currentMonthDays + 1)
////                            + "", State.NEXT_MONTH_DAY, mShowMonth + 1, mShowYear);
////                }
//            }
//        }
//    }


    public void update(int year, int month, int day, boolean[] ifEvents) {
        this.mShowMonth = month;
        this.mShowYear = year;
        this.mShowDay = day;
        this.ifEvents = ifEvents;
        fillDate();
        invalidate();
    }

    public void update(Calendar calendar) {
        calendar.add(Calendar.DATE, -calendar.get(Calendar.DAY_OF_WEEK));
        this.mShowMonth = calendar.get(Calendar.MONTH) + 1;
        this.mShowYear = calendar.get(Calendar.YEAR);
        this.mShowDay = calendar.get(Calendar.DATE);
        fillDate();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        int d = Math.min(width, height);
        mCellSpace = (float) width / TOTAL_COL;
        //Log.i("TTTT", mCellSpace + " " + width + " " + height);
        mTextPaint.setTextSize(mCellSpace / 3);
        mMonthTextPaint.setTextSize(mCellSpace / 6);
        mYearViewMonthTextPaint.setTextSize(mCellSpace * 2 / 3);
        setMeasuredDimension(width, (int) mCellSpace * (defaultStyle == MONTH_STYLE ? 7 : 1));
    }

    protected int measure(int measureSpec) {
        int size = MeasureSpec.getSize(measureSpec);
        return size;
    }

    public static float dip2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp * scale + 0.5f);
    }

    public static CalendarView[] createCalendarViewsForPager(Context context, int count, int style) {
        CalendarView[] views = new CalendarView[count];
        for (int i = 0; i < count; i++) {
            views[i] = new CalendarView(context, style);
        }
        return views;
    }

    public static CalendarView[] createCalendarViewsForPager(Context context, int count) {
        CalendarView[] views = new CalendarView[count];
        for (int i = 0; i < count; i++) {
            views[i] = new CalendarView(context, CalendarView.MONTH_STYLE);
        }
        return views;
    }

    public void removeSelectedDate() {
        isDateSelected = 0;
        invalidate();
    }

    public int getmShowYear() {
        return mShowYear;
    }

    public int getmShowMonth() {
        return mShowMonth;
    }

    public int getmShowDay() {
        return mShowDay;
    }

    public Row[] getRows() {
        return rows;
    }

    public float getmCellSpace() {
        return mCellSpace;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public boolean whetherHasFirstDay() {
        Cell[] cell = rows[0].getCells();
        for (int i = 0; i < 7; i++) {
            if (cell[i].text.equals("1")) {
                return true;
            }
        }
        return false;
    }

    public void setTodaySelected() {
        isTodayClicked = true;
        isDateSelected = 1;
        invalidate();
    }

    public boolean isTodayHasEvents() {
        return isTodayHasEvents;
    }

    //    public void setCalendarType(int type) {
//        if (type == MONTH_STYLE) {
//            this.defaultStyle = MONTH_STYLE;
//            invalidate();
//            requestLayout();
//        } else {
//            this.defaultStyle = WEEK_STYLE;
//            invalidate();
//            requestLayout();
//        }
//    }
}
