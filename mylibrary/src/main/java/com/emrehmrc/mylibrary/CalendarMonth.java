package com.snollidea.peppycalendarview;

import android.util.SparseArray;

import com.snollidea.peppycalendarview.event.CalendarEvent;

import java.util.Calendar;
import java.util.List;

public class CalendarMonth {
    private int mYear;
    private int mMonth;
    private int mAmountOfDays;
    private int mFirstWeekDay;

    private SparseArray<List<CalendarEvent>> mEvents;

    private static final int DEFAULT_DAYS_IN_WEEK = 7;

    private Calendar mCalendar;

    private CalendarMonth(Builder builder) {
        mYear = builder.mYear;
        mMonth = builder.mMonth;
        mAmountOfDays = builder.mAmountOfDays;
        mFirstWeekDay = builder.mFirstWeekDay;
        mCalendar = builder.mCalendar;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public int getAmountOfDays() {
        return mAmountOfDays;
    }

    public int getFirstWeekDay() {
        return mFirstWeekDay;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public List<CalendarEvent> getEventOfDay(int dayOfMonth) {
        if (mEvents == null) {
            return null;
        }
        return mEvents.get(dayOfMonth);
    }

    public void setEvents(SparseArray<List<CalendarEvent>> events) {
        mEvents = events;
    }

    /**
     *
     * @param dayOfMonth Number of day of month.
     * @return The index of day on calendar grid.
     */
    public int getDayIndex(int dayOfMonth) {
        return DEFAULT_DAYS_IN_WEEK + mFirstWeekDay + dayOfMonth;
    }

    boolean compareByDate(CalendarMonth calendarMonth) {
        return mYear == calendarMonth.getYear() && mMonth == calendarMonth.getMonth();
    }

    static class Builder {
        private int mYear;
        private int mMonth;
        private int mAmountOfDays;
        private int mFirstWeekDay;
        private Calendar mCalendar;

        public Builder setYear(int mYear) {
            this.mYear = mYear;
            return this;
        }

        public Builder setMonth(int mMonth) {
            this.mMonth = mMonth;
            return this;
        }

        public Builder setAmountOfDays(int mAmountOfDays) {
            this.mAmountOfDays = mAmountOfDays;
            return this;
        }

        public Builder setFirstWeekDay(int mFirstWeekDay) {
            this.mFirstWeekDay = mFirstWeekDay;
            return this;
        }

        public Builder setCalendar(Calendar calendar) {
            this.mCalendar = calendar;
            return this;
        }

        public CalendarMonth build() {
            return new CalendarMonth(this);
        }
    }
}