package com.itime.team.itime.utils;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.model.ParcelableCalendarType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public static final Comparator<ParcelableCalendarType> CALENDAR_TYPE_COMPARATOR = new Comparator<ParcelableCalendarType>() {
        @Override
        public int compare(ParcelableCalendarType lhs, ParcelableCalendarType rhs) {
            String lOwner = lhs.calendarOwnerName;
            String rOwner = rhs.calendarOwnerName;
            String lName = lhs.calendarName;
            String rName = rhs.calendarName;
            if (lOwner.equals("iTIME") && rOwner.equals("iTIME")) {
                return lName.compareTo(rName);
            } else if (lOwner.equals("iTIME")) {
                return -1;
            } else if (rOwner.equals("iTIME")) {
                return 1;
            } else {
                return (lOwner + lName).compareTo(rOwner + rName);
            }
        }
    };

    public static void sortCalendarType() {
        List<ParcelableCalendarType> types = Events.calendarTypeList;

        Collections.sort(types, CALENDAR_TYPE_COMPARATOR);

    }
}
