package com.snollidea.peppycalendarview.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

public class CommonUtils {

    public static String[] getWeekDaysAbbreviation(int firstDayOfWeek) {
        if (firstDayOfWeek < 1 || firstDayOfWeek > 7) {
            throw new IllegalArgumentException("Day must be from Java Calendar class");
        }

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        String[] shortWeekdays = dateFormatSymbols.getShortWeekdays();

        String[] weekDaysFromSunday = new String[]{shortWeekdays[1], shortWeekdays[2],
                shortWeekdays[3], shortWeekdays[4], shortWeekdays[5], shortWeekdays[6],
                shortWeekdays[7]};

        String[] weekDaysNames = new String[7];

        for (int day = firstDayOfWeek - 1, i = 0; i < 7; i++, day++) {
            day = day >= 7 ? 0 : day;
            weekDaysNames[i] = weekDaysFromSunday[day].toUpperCase();
        }

        return weekDaysNames;
    }
}