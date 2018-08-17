package com.snollidea.peppycalendarview.event;


public abstract class CalendarEvent {

    private int mIndicatorColor;
    private long mStartTimeInMillis;

    public CalendarEvent(long startTimeInMillis, int indicatorColor) {
        mStartTimeInMillis = startTimeInMillis;
        mIndicatorColor = indicatorColor;
    }

    public long getTimeInMillis() {
        return mStartTimeInMillis;
    }

    public int getColor() {
        return mIndicatorColor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CalendarEvent)) {
            return false;
        }
        CalendarEvent eventObj = (CalendarEvent) obj;
        return eventObj.getTimeInMillis() == mStartTimeInMillis && eventObj.getColor() == mIndicatorColor;
    }

    @Override
    public int hashCode() {
        return 37 * mIndicatorColor + (int) (mStartTimeInMillis ^ (mStartTimeInMillis >>> 32));
    }
}