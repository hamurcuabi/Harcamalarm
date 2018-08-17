package com.snollidea.peppycalendarview;

import android.util.SparseArray;

import com.snollidea.peppycalendarview.event.CalendarEvent;
import com.snollidea.peppycalendarview.utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.snollidea.peppycalendarview.MonthIndex.FOCUSED_MONTH;
import static com.snollidea.peppycalendarview.MonthIndex.NEXT_MONTH;
import static com.snollidea.peppycalendarview.MonthIndex.PREVIOUS_MONTH;

class MonthPager {

    private boolean mReachedMax;
    private boolean mReachedMin;

    /**
     * Start weekday of the week from Java Calendar
     */
    private int mFirstDayOfWeek;

    /**
     * Selected day number of focused month
     */
    private int mSelectedDay;

    private Calendar minDate;
    private Calendar maxDate;

    /**
     * Used for comparing months with current month and getting current day
     */
    private Calendar mCurrentMonthCalendar;

    /**
     * Months of focused, previous and next position
     */
    private CalendarMonth mPreviousMonth;
    private CalendarMonth mFocusedMonth;
    private CalendarMonth mNextMonth;

    private OnLoadEventsListener mOnLoadEventsListener;

    MonthPager(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;

        mCurrentMonthCalendar = CalendarUtils.getTodayCalendar();
        mCurrentMonthCalendar.setFirstDayOfWeek(mFirstDayOfWeek);

        // Start from current month
        Calendar focusedMonthCalendar = (Calendar) mCurrentMonthCalendar.clone();

        // Select current day selected by default
        mSelectedDay = CalendarUtils.getDayOfMonth(focusedMonthCalendar);

        setCalendarMonths(focusedMonthCalendar);
    }

    /**
     *
     * @param focusedMonthCalendar Base month, for building 2 next-door months (previous and next)
     */
    private void setCalendarMonths(Calendar focusedMonthCalendar) {
        if (minDate != null && mCurrentMonthCalendar.getTimeInMillis() < minDate.getTimeInMillis()) {
            setCalendarMonths(minDate);
            invalidateRange();
            return;
        } else if (maxDate != null && mCurrentMonthCalendar.getTimeInMillis() > maxDate.getTimeInMillis()) {
            setCalendarMonths(maxDate);
            invalidateRange();
            return;
        }

        Calendar nextMonthCalendar = (Calendar) focusedMonthCalendar.clone();
        CalendarUtils.setNextMonth(nextMonthCalendar);

        Calendar previousMonthCalendar = (Calendar) focusedMonthCalendar.clone();
        CalendarUtils.setPreviousMonth(previousMonthCalendar);

        mPreviousMonth = buildCalendarMonth(previousMonthCalendar);
        mFocusedMonth = buildCalendarMonth(focusedMonthCalendar);
        mNextMonth = buildCalendarMonth(nextMonthCalendar);
    }

    private CalendarMonth buildCalendarMonth(Calendar calendar) {
        CalendarMonth month = new CalendarMonth.Builder()
                .setYear(CalendarUtils.getYear(calendar))
                .setMonth(CalendarUtils.getMonth(calendar))
                .setFirstWeekDay(CalendarUtils.getFirstWeekDayOfMonth(calendar))
                .setAmountOfDays(CalendarUtils.getNumberOfDaysInMonth(calendar))
                .setCalendar(calendar)
                .build();

        loadEventsForMonth(month);
        return month;
    }

    private void loadEventsForMonth(CalendarMonth calendarMonth) {
        if (mOnLoadEventsListener == null) {
            return;
        }

        List<? extends CalendarEvent> monthEvents = mOnLoadEventsListener
                .onLoadEvents(calendarMonth.getYear(), calendarMonth.getMonth());

        if (monthEvents == null) {
            return;
        }

        // Events sorted by : (key) day of month - (value) events of day
        SparseArray<List<CalendarEvent>> eventsByDay = new SparseArray<>();

        Calendar eventCalendar = Calendar.getInstance();
        for (CalendarEvent calendarEvent : monthEvents) {
            eventCalendar.setTimeInMillis(calendarEvent.getTimeInMillis());

            if (CalendarUtils.isSameMonth(eventCalendar, calendarMonth.getCalendar())) {
                // Key
                int dayOfMonth = CalendarUtils.getDayOfMonth(eventCalendar);

                // Value
                List<CalendarEvent> eventsOfDay = eventsByDay.get(dayOfMonth);

                if (eventsOfDay == null) {
                    eventsOfDay = new ArrayList<>();
                    eventsOfDay.add(calendarEvent);
                    eventsByDay.put(dayOfMonth, eventsOfDay);
                } else {
                    eventsOfDay.add(calendarEvent);
                }
            }
        }

        calendarMonth.setEvents(eventsByDay);
    }

    private void invalidateRange() {
        if (maxDate != null &&
                CalendarUtils.isSameMonth(getCalendarMonth(FOCUSED_MONTH).getCalendar(), maxDate)) {
            mReachedMax = true;
        }

        if (minDate != null &&
                CalendarUtils.isSameMonth(getCalendarMonth(FOCUSED_MONTH).getCalendar(), minDate)) {
            mReachedMin = true;
        }
    }

    // Public

    void goForward() {
        // Select first day of month after change of month
        selectDay(1);

        mPreviousMonth = mFocusedMonth;
        mFocusedMonth = mNextMonth;

        // Building next month from focused month calendar, after adding a month to clone
        Calendar calendar = (Calendar) mFocusedMonth.getCalendar().clone();
        CalendarUtils.setNextMonth(calendar);
        mNextMonth = buildCalendarMonth(calendar);

        if (maxDate != null && CalendarUtils.isSameMonth(getCalendarMonth(FOCUSED_MONTH).getCalendar(), maxDate)) {
            mReachedMax = true;
        }

        if (mReachedMin) {
            mReachedMin = false;
        }
    }

    void goBack() {
        // Select first day of month after change of month
        selectDay(1);

        mNextMonth = mFocusedMonth;
        mFocusedMonth = mPreviousMonth;

        // Building next month from focused month calendar, after subtracting a month from clone
        Calendar calendar = (Calendar) mFocusedMonth.getCalendar().clone();
        CalendarUtils.setPreviousMonth(calendar);
        mPreviousMonth = buildCalendarMonth(calendar);

        if (minDate != null && CalendarUtils.isSameMonth(getCalendarMonth(FOCUSED_MONTH).getCalendar(), minDate)) {
            mReachedMin = true;
        }

        if (mReachedMax) {
            mReachedMax = false;
        }
    }

    CalendarMonth getCalendarMonth(@MonthIndex int monthIndex) {
        switch (monthIndex) {
            case PREVIOUS_MONTH:
                return mPreviousMonth;
            case FOCUSED_MONTH:
                return mFocusedMonth;
            case NEXT_MONTH:
                return mNextMonth;
        }

        return null;
    }

    boolean isReachedMax() {
        return mReachedMax;
    }

    boolean isReachedMin() {
        return mReachedMin;
    }

    int getSelectedDay() {
        return mSelectedDay;
    }

    int getCurrentDay() {
        return CalendarUtils.getDayOfMonth(mCurrentMonthCalendar);
    }

    boolean isOnCurrentMonth(@MonthIndex int monthIndex) {
        return getCalendarMonth(monthIndex).getYear() == CalendarUtils.getYear(mCurrentMonthCalendar) &&
                getCalendarMonth(monthIndex).getMonth() == CalendarUtils.getMonth(mCurrentMonthCalendar);
    }

    void selectDay(int day) {
        mSelectedDay = day;
    }

    void setFirstDayOfWeek(int firstDayOfWeek) {
        mFirstDayOfWeek = firstDayOfWeek;

        // Invalidate
        Calendar focusedMonthCalendar = getCalendarMonth(FOCUSED_MONTH).getCalendar();
        focusedMonthCalendar.setFirstDayOfWeek(mFirstDayOfWeek);
        setCalendarMonths(focusedMonthCalendar);
    }

    void setOnLoadEventsListener(OnLoadEventsListener listener) {
        mOnLoadEventsListener = listener;

        // Invalidate
        loadEventsForMonth(getCalendarMonth(PREVIOUS_MONTH));
        loadEventsForMonth(getCalendarMonth(FOCUSED_MONTH));
        loadEventsForMonth(getCalendarMonth(NEXT_MONTH));
    }

    void setMinimumDate(long timeInMillis) {
        minDate = Calendar.getInstance();
        minDate.setFirstDayOfWeek(mFirstDayOfWeek);
        minDate.setTimeInMillis(timeInMillis);

        setCalendarMonths(mCurrentMonthCalendar);
    }

    void setMaximumDate(long timeInMillis) {
        maxDate = Calendar.getInstance();
        minDate.setFirstDayOfWeek(mFirstDayOfWeek);
        maxDate.setTimeInMillis(timeInMillis);

        setCalendarMonths(mCurrentMonthCalendar);
    }

    void updateEvents() {
        loadEventsForMonth(getCalendarMonth(PREVIOUS_MONTH));
        loadEventsForMonth(getCalendarMonth(FOCUSED_MONTH));
        loadEventsForMonth(getCalendarMonth(NEXT_MONTH));
    }
}
