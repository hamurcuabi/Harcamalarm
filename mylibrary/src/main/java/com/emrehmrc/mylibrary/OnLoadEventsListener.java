package com.snollidea.peppycalendarview;

import com.snollidea.peppycalendarview.event.CalendarEvent;

import java.util.List;

public interface OnLoadEventsListener {
    /**
     * Called for loading events, when new month is initialized
     * @param year Year of downloadable month from Java Calendar
     * @param month Month number of downloadable month from Java Calendar
     * @return List of target month events
     */
    List<? extends CalendarEvent> onLoadEvents(int year, int month);
}