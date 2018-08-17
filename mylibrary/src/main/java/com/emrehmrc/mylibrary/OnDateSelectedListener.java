package com.snollidea.peppycalendarview;

import android.support.annotation.Nullable;

import com.snollidea.peppycalendarview.event.CalendarEvent;

import java.util.Calendar;
import java.util.List;

public interface OnDateSelectedListener {

    /**
     * Called after click on day of the month
     * @param dayCalendar Calendar of selected day
     * @param events Events of selected day
     */
    void onDateSelected(Calendar dayCalendar, @Nullable List<CalendarEvent> events);
}