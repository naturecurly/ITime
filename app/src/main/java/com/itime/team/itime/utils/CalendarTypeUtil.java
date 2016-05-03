package com.itime.team.itime.utils;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.model.ParcelableCalendarType;

/**
 * Created by leveyleonhardt on 5/2/16.
 */
public class CalendarTypeUtil {
    public static ParcelableCalendarType findCalendarById(String id) {
        for (ParcelableCalendarType calendarType : Events.calendarTypeList) {
            if (id.equals(calendarType.calendarId)) {
                return calendarType;
            }
        }
        return null;
    }
}
