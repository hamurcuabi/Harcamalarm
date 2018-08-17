package com.snollidea.peppycalendarview.utils;

import java.util.Calendar;

public class CalendarUtils {

    private CalendarUtils() {}

    public static int getFirstWeekDayOfMonth(Calendar calendar) {
        Calendar utilCalendar = (Calendar) calendar.clone();
        utilCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = utilCalendar.get(Calendar.DAY_OF_WEEK) - utilCalendar.getFirstDayOfWeek();
        return dayOfWeek < 0 ? 7 + dayOfWeek : dayOfWeek;
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH);
    }

    public static int getDayOfMonth(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getNumberOfDaysInMonth(Calendar calendar) {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getTodayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar;
    }

    public static boolean isSameMonth(Calendar calendar, Calendar calendar2) {
        return getYear(calendar) == getYear(calendar2) && getMonth(calendar) == getMonth(calendar2);
    }

    public static void setNextMonth(Calendar calendar) {
        calendar.add(Calendar.MONTH, 1);
    }

    public static void setPreviousMonth(Calendar calendar) {
        calendar.add(Calendar.MONTH, -1);
    }

}
