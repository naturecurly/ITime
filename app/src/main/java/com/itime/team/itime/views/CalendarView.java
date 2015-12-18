package com.itime.team.itime.views;

/**
 * Created by leveyleonhardt on 12/16/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.itime.team.itime.R;
import com.itime.team.itime.utils.DateUtil;

public class CalendarView extends View {

    private static final String TAG = "CalendarView";

    public static final int MONTH_STYLE = 0;
    public static final int WEEK_STYLE = 1;

    private static final int TOTAL_COL = 7;
    private static final int TOTAL_ROW = 6;

    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mGridPaint;
    private Paint mLinePaint;
    private int mViewWidth;
    private int mViewHight;
    private float mCellSpace;
    private Row rows[] = new Row[TOTAL_ROW];
    private Context context;
    private Bitmap bm;
    private int mShowYear;
    private int mShowMonth;
    private int mShowDay;
    protected int defaultStyle = MONTH_STYLE;
    private static final int WEEK = 7;


    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        fillDate();
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null)
                rows[i].drawCells(canvas, i);
        }
    }

    private void init(Context context) {
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_calendar_circle);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mCirclePaint.setColor(Color.parseColor("#F24949"));
        mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.STROKE);
        mLinePaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        mLinePaint.setColor(Color.GRAY);
        this.context = context;

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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHight = h;
        mCellSpace = Math.min((float) mViewHight / TOTAL_ROW, (float) mViewWidth / TOTAL_COL);
        mTextPaint.setTextSize(mCellSpace / 3);
    }


    class Row {
        public Cell[] cells = new Cell[TOTAL_COL];

        public void drawCells(Canvas canvas, int j) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null)
                    cells[i].drawSelf(canvas, i, j);
            }

        }
    }

    class Cell {
        public String text;
        public State state;

        public Cell(String text, State state) {
            super();
            this.text = text;
            this.state = state;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void drawSelf(Canvas canvas, int i, int j) {
            switch (state) {
                case CURRENT_MONTH_DAY:
                    mTextPaint.setColor(Color.parseColor("#80000000"));
                    break;
                case NEXT_MONTH_DAY:
                case PAST_MONTH_DAY:
                    mTextPaint.setColor(Color.parseColor("#40000000"));
                    break;
                case TODAY:
                    mTextPaint.setColor(Color.parseColor("#80000000"));
//                    canvas.drawCircle((float) (mCellSpace * (i + 0.45)),
//                            (float) ((j + 0.8) * mCellSpace), mCellSpace / 2,
//                            mCirclePaint);
                    float center = mCellSpace / 2;
                    float ringWidth = dip2px(context, 5);
                    //mCirclePaint.setStrokeWidth(2);
                    //canvas.drawCircle(mCellSpace * i + center, j * mCellSpace + center, mCellSpace / 2, mCirclePaint);
                    Matrix matrix = new Matrix();
                    int pic_width = bm.getWidth();
                    int pic_height = bm.getHeight();
                    matrix.postScale(mCellSpace / pic_width, mCellSpace / pic_height);
                    Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, pic_width, pic_height, matrix, true);
                    canvas.drawBitmap(newbm, i * mCellSpace, j * mCellSpace, mCirclePaint);
                    break;
            }
            //canvas.drawRect(i * mCellSpace, j * mCellSpace, (i + 1) * mCellSpace, (j + 1) * mCellSpace, mGridPaint);
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();

            canvas.drawText(text, i * mCellSpace + mCellSpace / 2,
                    (j + 1) * mCellSpace - mCellSpace / 2 - (fm.ascent - fm.descent) / 2 - dip2px(context, 2), mTextPaint);
            //canvas.drawLine(i * mCellSpace, j * mCellSpace, (i + 1) * mCellSpace, j * mCellSpace,mLinePaint );
        }
    }

    enum State {
        CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, TODAY;
    }

    private void fillDate() {
        if (defaultStyle == MONTH_STYLE) {
            fillMonthDate();
        } else {
            fillWeekDate();
        }
    }

    private void fillWeekDate() {

        int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
        rows[0] = new Row();
        if (mShowDay + WEEK - 1 > currentMonthDays) {
            mShowMonth += 1;
        }
        for (int i = 0; i < TOTAL_COL; i++) {
            mShowDay += 1;
            if (mShowDay > currentMonthDays) {
                mShowDay = 1;
            }
            if (mShowDay == DateUtil.getCurrentMonthDays() &&
                    mShowYear == DateUtil.getYear()
                    && mShowMonth == DateUtil.getMonth()) {
                rows[0].cells[i] = new Cell(mShowDay + "", State.TODAY);
                continue;
            }
            rows[0].cells[i] = new Cell(mShowDay + "", State.CURRENT_MONTH_DAY);
        }
    }

    private void fillMonthDate() {
        int monthDay = DateUtil.getCurrentMonthDays();
        int lastMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth - 1);
        int currentMonthDays = DateUtil.getMonthDays(mShowYear, mShowMonth);
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowYear, mShowMonth);
        boolean isCurrentMonth = false;
        if (mShowYear == DateUtil.getYear() && mShowMonth == DateUtil.getMonth()) {
            isCurrentMonth = true;
        }
        int time = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row();
            for (int i = 0; i < TOTAL_COL; i++) {
                int postion = i + j * TOTAL_COL;
                if (postion >= firstDayWeek
                        && postion < firstDayWeek + currentMonthDays) {
                    time++;
                    if (isCurrentMonth && time == monthDay) {
                        rows[j].cells[i] = new Cell(time + "", State.TODAY);
                        continue;
                    }
                    rows[j].cells[i] = new Cell(time + "",
                            State.CURRENT_MONTH_DAY);
                    continue;
                } else if (postion < firstDayWeek) {
                    rows[j].cells[i] = new Cell((lastMonthDays - (firstDayWeek
                            - postion - 1))
                            + "", State.PAST_MONTH_DAY);
                    continue;
                } else if (postion >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((postion - firstDayWeek
                            - currentMonthDays + 1)
                            + "", State.NEXT_MONTH_DAY);
                }
            }
        }
    }

    public void update(int year, int month, int day) {
        this.mShowMonth = month;
        this.mShowYear = year;
        this.mShowDay = day;

        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        int d = Math.min(width, height);
        mCellSpace = (float) MeasureSpec.getSize(widthMeasureSpec) / TOTAL_COL;
        Log.i("TTTT", mCellSpace + " " + width + " " + height);
        setMeasuredDimension(d, d);
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
}