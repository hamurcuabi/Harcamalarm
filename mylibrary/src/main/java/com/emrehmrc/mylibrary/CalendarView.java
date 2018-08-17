package com.snollidea.peppycalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.OverScroller;

import com.snollidea.peppycalendarview.event.CalendarEvent;
import com.snollidea.peppycalendarview.utils.CommonUtils;

import java.util.Calendar;
import java.util.List;

import static com.snollidea.peppycalendarview.MonthIndex.FOCUSED_MONTH;
import static com.snollidea.peppycalendarview.MonthIndex.NEXT_MONTH;
import static com.snollidea.peppycalendarview.MonthIndex.PREVIOUS_MONTH;

public class CalendarView extends View {

    /**
     * Velocity threshold for smooth scroll to another month.
     */
    private static final int VELOCITY_THRESHOLD = 2000;

    /**
     * Ratio between different values
     */
    private static final float RATIO_ROW_HEIGHT_WIDTH = 0.098f;
    private static final float RATIO_WIDTH_PADDING_X = 12.0f;
    private static final float RATIO_WIDTH_PADDING_Y = 15.0f;
    private static final float RATIO_WIDTH_TEXT_HEIGHT = 36.0f;
    private static final float RATIO_WIDTH_CIRCLE_RADIUS = 27.0f;
    private static final float RATIO_DURATION_DISTANCE = 0.75f;

    private static final int DEFAULT_DAYS_IN_WEEK = 7;

    /**
     * Duration of resize animation in ms
     */
    private static final int RESIZE_ANIMATION_DURATION = 200;

    /**
     * Used for shifting drawing items
     */
    private int mOffset;

    /**
     * mPaddingX used for left and right padding
     * mPaddingY used for top and bottom padding
     */
    private float mPaddingX;
    private float mPaddingY;

    /**
     * Space between objects in calendar for X and Y axis
     */
    private float mBetweenX;
    private float mBetweenY;

    /**
     * Used for dynamic resize from different parts of code
     */
    private int mViewHeight;
    private int mRowsCount;

    /**
     * Used for measuring text with Paint.getTextBounds method
     */
    private Rect mTextRect = new Rect();

    /**
     * Names of days of the week
     */
    private String[] mWeekDayNames;

    /**
     * Is view in resize animation
     */
    private boolean mIsResize;

    /**
     * First day of the week is; e.g., SUNDAY in the U.S., MONDAY in France.
     */
    private int mFirstDayOfWeek;

    // XML Attributes
    private int mBackgroundColor;
    private int mTextColor;
    private int mTextInsideCircleColor;
    private int mWeekDaysNamesColor;
    private int mCurrentDayCircleColor;
    private int mSelectedDayCircleColor;

    // Listeners
    private OnDateSelectedListener mOnDateSelectedListener;
    private OnMonthChangedListener mOnMonthChangedListener;

    // Interactive
    private GestureDetectorCompat mDetector;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;

    // Drawing
    private Paint mTextInsideCirclePaint;
    private Paint mTextPaint;
    private float mTextHeight;
    private Paint mSelectedDayCirclePaint;
    private Paint mCurrentDayCirclePaint;
    private float mCircleRadius;
    private Paint mEventCirclePaint;
    private Paint mBackgroundPaint;
    private Paint mWeekDaysNamesTextPaint;
    private float mEventCircleRadius;
    private float mPlaceForPointsWidth; // Place where events points are located

    private MonthPager mMonthPager;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.CalendarView, 0, 0);

            try {
                mBackgroundColor = typedArray.getColor(R.styleable.CalendarView_backgroundColor,
                        0xffffffff);
                mTextColor = typedArray.getColor(R.styleable.CalendarView_textColor, Color.BLACK);
                mTextInsideCircleColor = typedArray.getColor(R.styleable.CalendarView_textInsideCircleColor,
                        Color.WHITE);
                mWeekDaysNamesColor = typedArray.getColor(R.styleable.CalendarView_weekDaysNamesColor,
                        Color.GRAY);
                mCurrentDayCircleColor = typedArray.getColor(R.styleable.CalendarView_currentDayCircleColor,
                        Color.BLACK);
                mSelectedDayCircleColor = typedArray.getColor(R.styleable.CalendarView_selectedCircleColor,
                        Color.LTGRAY);
                mFirstDayOfWeek = typedArray.getInt(R.styleable.CalendarView_firstDayOfWeek, Calendar.MONDAY);
            } finally {
                typedArray.recycle();
            }
        }

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Background drawing
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);

        // Focused month drawing
        drawMonth(canvas, FOCUSED_MONTH);

        // If mOffset <= 0 previous month out of sight
        if (mOffset > 0) {
            drawMonth(canvas, PREVIOUS_MONTH);
        }

        // If mOffset >= 0 next month out of sight
        if (mOffset < 0) {
            drawMonth(canvas, NEXT_MONTH);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);
        int height;

        if (mIsResize) {
            // mViewHeight is changing by resizeView() method
            height = mViewHeight;
        } else {
            height = (int) (width * RATIO_ROW_HEIGHT_WIDTH *
                    getMonthRowsCount(mMonthPager.getCalendarMonth(FOCUSED_MONTH)));

            mPaddingX = width / RATIO_WIDTH_PADDING_X;
            mPaddingY = width / RATIO_WIDTH_PADDING_Y;
            mBetweenX = (width - mPaddingX * 2) / (DEFAULT_DAYS_IN_WEEK - 1);
            mBetweenY = (height / mRowsCount * 6 - mPaddingY * 2) / 5;

            mTextHeight = width / RATIO_WIDTH_TEXT_HEIGHT;
            mCircleRadius = width / RATIO_WIDTH_CIRCLE_RADIUS;
            mEventCircleRadius = mCircleRadius / 7;
            mPlaceForPointsWidth = mBetweenX / 2;
            mTextPaint.setTextSize(mTextHeight);
            mTextInsideCirclePaint.setTextSize(mTextHeight);
            mWeekDaysNamesTextPaint.setTextSize(mTextHeight);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);

                mVelocityTracker.computeCurrentVelocity(1000);
                handleGesture(mVelocityTracker.getXVelocity());
                mVelocityTracker.recycle();
                mVelocityTracker.clear();
                mVelocityTracker = null;
                break;
        }

        return this.mDetector.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    private void init() {
        mMonthPager = new MonthPager(mFirstDayOfWeek);
        mScroller = new OverScroller(getContext());
        mDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (!mScroller.isFinished()) {
                    return true;
                }

                CalendarMonth calendarMonth = mMonthPager.getCalendarMonth(FOCUSED_MONTH);

                float x = motionEvent.getX(), y = motionEvent.getY();
                int day = getDayNumberOfCrd(x, y, calendarMonth.getFirstWeekDay());

                if (day < 1 || day > calendarMonth.getAmountOfDays()) {
                    return true;
                }

                mMonthPager.selectDay(day);
                invalidate();
                dispatchOnDateSelected(calendarMonth.getCalendar(), calendarMonth.getEventOfDay(day));
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float dx, float dy) {
                getParent().requestDisallowInterceptTouchEvent(true);

                int width = getWidth();

                // Return if MonthPager reached max or min date
                if ((dx > 0 && mMonthPager.isReachedMax()) ||
                        (dx < 0 && mMonthPager.isReachedMin())) {
                    return true;
                }

                mOffset -= dx;

                // Set max offset value, if offset has reached one of the edges
                if (mOffset > width) {
                    mOffset = width;
                } else if (mOffset < -width) {
                    mOffset = -width;
                }

                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mRowsCount = getMonthRowsCount(mMonthPager.getCalendarMonth(FOCUSED_MONTH));
        mWeekDayNames = CommonUtils.getWeekDaysAbbreviation(mFirstDayOfWeek);

        // Text of days numbers
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);

        // Text of selected and current day number
        mTextInsideCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextInsideCirclePaint.setColor(mTextInsideCircleColor);

        mSelectedDayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectedDayCirclePaint.setStyle(Paint.Style.FILL);
        mSelectedDayCirclePaint.setColor(mSelectedDayCircleColor);

        mCurrentDayCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrentDayCirclePaint.setStyle(Paint.Style.FILL);
        mCurrentDayCirclePaint.setColor(mCurrentDayCircleColor);

        mEventCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEventCirclePaint.setStyle(Paint.Style.FILL);

        // Week Name Text
        mWeekDaysNamesTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWeekDaysNamesTextPaint.setColor(mWeekDaysNamesColor);
        mWeekDaysNamesTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Background
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);
    }

    /**
     *
     * @param x The x position of touch event.
     * @param y The y position of touch event.
     * @param firstDayOfWeek First day of the week.
     * @return Selected day of focused month.
     */
    private int getDayNumberOfCrd(float x, float y, int firstDayOfWeek) {
        // Measure text
        mWeekDaysNamesTextPaint.getTextBounds(mWeekDayNames[0], 0, mWeekDayNames[0].length(), mTextRect);
        float weekDaysNamesHeight = mTextRect.top + mBetweenY;

        float widthPerDay = (getWidth() - mPaddingX * 2 + mBetweenX) / DEFAULT_DAYS_IN_WEEK;
        float heightPerDay = (getHeight() - mPaddingY * 2 - weekDaysNamesHeight + mBetweenY) / (mRowsCount - 1);

        x = x - mPaddingX + mBetweenX;
        y = y - mPaddingY + mBetweenY - weekDaysNamesHeight;

        int row = Math.round(x / widthPerDay);
        int column = Math.round(y / heightPerDay);

        return (column - 1) * DEFAULT_DAYS_IN_WEEK + row - firstDayOfWeek;
    }

    /**
     *
     * @param index The index on calendar grid
     * @param text Day number text, for calculation text center, if it is needed. Set null for calculating circle position.
     * @param monthIndex Month index, for calculating offset
     * @return Float array for x[0] and y[1] position
     */
    private float[] calculateCrdForIndex(int index, @Nullable String text, @MonthIndex int monthIndex) {
        int rowIndex = (index - 1) % DEFAULT_DAYS_IN_WEEK;
        int column = (index - 1) / DEFAULT_DAYS_IN_WEEK;

        float x = mPaddingX + (mBetweenX * rowIndex) + (getWidth() * monthIndex);
        float y = mPaddingY + (mBetweenY * column);

        x += mOffset;

        // Calculation of the text center
        if (text != null) {
            // Measure text size
            mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);

            x -= mTextRect.centerX();
            y -= mTextRect.centerY();
        }

        return new float[]{x, y};
    }

    private void drawMonth(Canvas canvas, @MonthIndex int monthIndex) {
        CalendarMonth calendarMonth = mMonthPager.getCalendarMonth(monthIndex);

        // Selected day circle drawing
        if (monthIndex == FOCUSED_MONTH) {
            float[] crdCircle = calculateCrdForIndex(calendarMonth.getDayIndex(mMonthPager.getSelectedDay()),
                    null, monthIndex);
            canvas.drawCircle(crdCircle[0], crdCircle[1], mCircleRadius, mSelectedDayCirclePaint);
        }

        // Current day circle drawing
        if (mMonthPager.isOnCurrentMonth(monthIndex)) {
            float[] crdCircle = calculateCrdForIndex(calendarMonth.getDayIndex(mMonthPager.getCurrentDay()),
                    null, monthIndex);
            canvas.drawCircle(crdCircle[0], crdCircle[1], mCircleRadius, mCurrentDayCirclePaint);
        }

        // Week days names drawing
        for (int i = 1; i <= DEFAULT_DAYS_IN_WEEK; i++) {
            float[] crd = calculateCrdForIndex(i, mWeekDayNames[i - 1], monthIndex);
            canvas.drawText(mWeekDayNames[i - 1], crd[0], crd[1], mWeekDaysNamesTextPaint);
        }

        // Numbers of days drawing
        for (int day = 1; day <= calendarMonth.getAmountOfDays(); day++) {
            int index = calendarMonth.getDayIndex(day);
            float[] crd = calculateCrdForIndex(index, Integer.toString(day), monthIndex);

            boolean isCurrentDay = mMonthPager.isOnCurrentMonth(monthIndex) && day == mMonthPager.getCurrentDay();
            boolean isSelectedDay = monthIndex == FOCUSED_MONTH && day == mMonthPager.getSelectedDay();

            canvas.drawText(Integer.toString(day), crd[0], crd[1],
                    isCurrentDay || isSelectedDay ? mTextInsideCirclePaint : mTextPaint);

            // Events drawing
            List<CalendarEvent> events = calendarMonth.getEventOfDay(day);
            if (events != null && !isCurrentDay && !isSelectedDay) {
                drawEventsOfDay(canvas, events, crd, day);
            }
        }
    }

    private void drawEventsOfDay(Canvas canvas, List<CalendarEvent> events, float[] crd, int day) {
        // Measure text of events day number
        String dayText = Integer.toString(day);
        mTextPaint.getTextBounds(dayText, 0, dayText.length(), mTextRect);

        float offsetForCenter = mPlaceForPointsWidth / 2 - mTextRect.centerX();

        // Space between events points for X axis
        float betweenPoints = mPlaceForPointsWidth / (events.size() + 1);

        for (int i = 0; i < events.size(); i++) {
            mEventCirclePaint.setColor(events.get(i).getColor());
            canvas.drawCircle(crd[0] - offsetForCenter + betweenPoints * (i + 1),
                    crd[1] + mCircleRadius / 2, mEventCircleRadius, mEventCirclePaint);
        }
    }

    private void handleGesture(float velocity) {
        if (velocity == 0 && mOffset == 0) {
            return;
        }

        if ((velocity > VELOCITY_THRESHOLD || mOffset > getWidth() / 2) &&
                (!mMonthPager.isReachedMin())) {
            if (!canGoBack()) {
                handleGesture(0);
                return;
            }

            mMonthPager.goBack();

            int distance = getWidth() - mOffset;

            // Invalidate offset, because of changed focused month
            mOffset = mOffset - getWidth();

            mScroller.startScroll(mOffset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE));

            dispatchOnMonthChanged(mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(mMonthPager.getCalendarMonth(FOCUSED_MONTH)));

            ViewCompat.postInvalidateOnAnimation(this);
        } else if ((velocity < -VELOCITY_THRESHOLD || mOffset < -getWidth() / 2) &&
                (!mMonthPager.isReachedMax())) {
            if (!canGoForward()) {
                handleGesture(0);
                return;
            }

            mMonthPager.goForward();

            int distance = -getWidth() - mOffset;

            // Invalidate offset, because of changed focused month
            mOffset = mOffset + getWidth();

            mScroller.startScroll(mOffset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE));

            dispatchOnMonthChanged(mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
            resizeView(getMonthRowsCount(mMonthPager.getCalendarMonth(FOCUSED_MONTH)));

            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // Smooth scroll to focused month
            int distance = -mOffset;
            mScroller.startScroll(mOffset, 0, distance, 0,
                    (int) (Math.abs(distance) * RATIO_DURATION_DISTANCE * 2)); // More slowly scroll (x2 duration)
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean canGoForward() {
        return mOffset <= 0;
    }

    private boolean canGoBack() {
        return mOffset >= 0;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrX();
            invalidate();
            if (mOffset == mScroller.getFinalX()) {
                mScroller.forceFinished(true);

                // First day of month selected, after changing month
                CalendarMonth calendarMonth = mMonthPager.getCalendarMonth(FOCUSED_MONTH);
                dispatchOnDateSelected(calendarMonth.getCalendar(),
                        calendarMonth.getEventOfDay(mMonthPager.getSelectedDay()));
            }
        }
    }

    /**
     *
     * @param calendarMonth Target month.
     * @return Number of required rows, for drawing target month.
     */
    private int getMonthRowsCount(CalendarMonth calendarMonth) {
        float rowsCount = (float) (calendarMonth.getAmountOfDays() + calendarMonth.getFirstWeekDay()) / DEFAULT_DAYS_IN_WEEK;
        return (int) Math.ceil(rowsCount) + 1; // + 1 for week days names row
    }

    /**
     * Change view height, for next month
     * @param targetRowsCount Number of next month rows
     */
    private void resizeView(int targetRowsCount) {
        // If current rows count are equals to target rows count resize not required
        if (mRowsCount == targetRowsCount) {
            return;
        }

        class ResizeAnimation extends Animation {
            private int mTargetHeight;
            private View mView;
            private int mStartHeight;

            private ResizeAnimation(View view, int targetHeight, int startHeight) {
                mView = view;
                mTargetHeight = targetHeight;
                mStartHeight = startHeight;
            }

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = (int) (mStartHeight + (mTargetHeight - mStartHeight) * interpolatedTime);
                mViewHeight = newHeight;
                mView.getLayoutParams().height = newHeight;
                mView.requestLayout();

                if (interpolatedTime == 1.0f) {
                    // Animation is over
                    mIsResize = false;
                }
            }

            @Override
            public void initialize(int width, int height, int parentWidth, int parentHeight) {
                super.initialize(width, height, parentWidth, parentHeight);
            }
        }
        ResizeAnimation resizeAnimation = new ResizeAnimation(this,
                getHeight() * targetRowsCount / mRowsCount, getHeight());
        resizeAnimation.setDuration(RESIZE_ANIMATION_DURATION);
        startAnimation(resizeAnimation);

        mIsResize = true;
        mRowsCount = targetRowsCount;
    }

    // Perform listeners

    private void dispatchOnDateSelected(Calendar calendar, List<CalendarEvent> eventsOfDay) {
        if (mOnDateSelectedListener != null) {
            mOnDateSelectedListener.onDateSelected(calendar, eventsOfDay);
        }
    }

    private void dispatchOnMonthChanged(Calendar calendar) {
        if (mOnMonthChangedListener != null) {
            mOnMonthChangedListener.onMonthChanged(calendar);
        }
    }

    // Public methods

    public void updateEvents() {
        mMonthPager.updateEvents();
    }

    public void setFirstDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day must be from Java Calendar class");
        }
        mFirstDayOfWeek = dayOfWeek;
        mWeekDayNames = CommonUtils.getWeekDaysAbbreviation(mFirstDayOfWeek);
        mMonthPager.setFirstDayOfWeek(dayOfWeek);
        invalidate();
    }

    public void setMinimumDate(long timeInMillis) {
        mMonthPager.setMinimumDate(timeInMillis);
        dispatchOnMonthChanged(mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
        invalidate();
    }

    public void setMaximumDate(long timeInMillis) {
        mMonthPager.setMaximumDate(timeInMillis);
        dispatchOnMonthChanged(mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
        invalidate();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;

        CalendarMonth calendarMonth = mMonthPager.getCalendarMonth(FOCUSED_MONTH);
        dispatchOnDateSelected(calendarMonth.getCalendar(),
                calendarMonth.getEventOfDay(mMonthPager.getSelectedDay()));
    }

    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        mOnMonthChangedListener = onMonthChangedListener;
        dispatchOnMonthChanged(mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar());
    }

    public void setOnLoadEventsListener(OnLoadEventsListener onLoadEventsListener) {
        mMonthPager.setOnLoadEventsListener(onLoadEventsListener);

        CalendarMonth calendarMonth = mMonthPager.getCalendarMonth(FOCUSED_MONTH);
        dispatchOnDateSelected(calendarMonth.getCalendar(),
                calendarMonth.getEventOfDay(mMonthPager.getSelectedDay()));
    }

    public void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
        mBackgroundPaint.setColor(mBackgroundColor);
        invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextInsideCircleColor(@ColorInt int color) {
        mTextInsideCircleColor = color;
        mTextInsideCirclePaint.setColor(mTextInsideCircleColor);
        invalidate();
    }

    public int getTextInsideCircleColor() {
        return mTextInsideCircleColor;
    }

    public void setWeekDaysNamesColor(@ColorInt int color) {
        mWeekDaysNamesColor = color;
        mWeekDaysNamesTextPaint.setColor(mWeekDaysNamesColor);
        invalidate();
    }

    public int getWeekDaysNamesColor() {
        return mWeekDaysNamesColor;
    }

    public void setCurrentDayCircleColor(@ColorInt int color) {
        mCurrentDayCircleColor = color;
        mCurrentDayCirclePaint.setColor(mCurrentDayCircleColor);
        invalidate();
    }

    public int getCurrentDayCircleColor() {
        return mCurrentDayCircleColor;
    }

    public void setSelectedDayCircleColor(@ColorInt int color) {
        mSelectedDayCircleColor = color;
        mSelectedDayCirclePaint.setColor(mSelectedDayCircleColor);
        invalidate();
    }

    public int getSelectedDayCircleColor() {
        return mSelectedDayCircleColor;
    }

    public Calendar getFocusedMonthCalendar() {
        return mMonthPager.getCalendarMonth(FOCUSED_MONTH).getCalendar();
    }
}
