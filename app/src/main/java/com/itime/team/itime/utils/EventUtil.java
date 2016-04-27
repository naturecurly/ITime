package com.itime.team.itime.utils;

import android.util.Log;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.task.ReadMonthEventTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by leveyleonhardt on 3/19/16.
 */
public class EventUtil {

    public static boolean isTodayPressed = false;


    public static JSONArray initialEvents(JSONArray response) {
        Set<String> stringSet = new HashSet<>();
        JSONArray array = new JSONArray();
        List<JSONObject> repeatEvents = new ArrayList<>();
        Map<String, List<JSONObject>> eventsByMonth = new HashMap<>();
//        JSONArray response = Events.response;
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Log.d("isValid", object.toString());
                if (isValidEvent(object)) {
                    if (!decideWhetherMultiDays(object)) {
                        array.put(object);
                        if (isRepeat(object)) {
                            repeatEvents.add(object);
                        }
                        String dateString = object.getString("event_starts_datetime");
                        Calendar dateCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                        stringSet.add(dateCal.get(Calendar.DAY_OF_MONTH) + "-" + (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR));
                        String key = (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR);

                        if (eventsByMonth.size() == 0 || !eventsByMonth.containsKey(key)) {
                            List<JSONObject> monthList = new ArrayList<>();
                            monthList.add(object);
                            eventsByMonth.put(key, monthList);
                        } else {
                            eventsByMonth.get(key).add(object);

                        }
                    } else {
                        String startString = object.getString("event_starts_datetime");
                        String endString = object.getString("event_ends_datetime");
                        Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
                        Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
                        Log.d("this", startCal.get(Calendar.DAY_OF_MONTH) + " " + endCal.get(Calendar.DAY_OF_MONTH));
                        int day = calDuration(endCal, startCal);
                        if (day > 0) {
                            Log.d("how many days", day + "");
                            for (int y = 0; y <= day; y++) {
                                if (y == 0) {

                                    JSONObject objectStart = new JSONObject(object.toString());

                                    Calendar temp = (Calendar) startCal.clone();
                                    temp.set(Calendar.HOUR_OF_DAY, 23);
                                    temp.set(Calendar.MINUTE, 59);
                                    objectStart.put("event_starts_datetime", startString);
                                    objectStart.put("event_ends_datetime", DateUtil.getDateStringFromCalendarGMT(temp));
                                    Log.d("how many days", objectStart.toString());
                                    if (isValidEvent(objectStart)) {
                                        array.put(objectStart);
                                        if (isRepeat(objectStart)) {
                                            repeatEvents.add(objectStart);
                                        }
                                        String dateString = objectStart.getString("event_starts_datetime");
                                        Calendar dateCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                                        stringSet.add(dateCal.get(Calendar.DAY_OF_MONTH) + "-" + (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR));

                                        String key = (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR);
                                        if (eventsByMonth == null || !eventsByMonth.containsKey(key)) {
                                            List<JSONObject> monthList = new ArrayList<>();
                                            monthList.add(objectStart);
                                            eventsByMonth.put(key, monthList);
                                        } else {
                                            eventsByMonth.get(key).add(objectStart);

                                        }
                                    }
                                } else if (y == day) {

                                    JSONObject objectEnd = new JSONObject(object.toString());
                                    Calendar temp1 = (Calendar) endCal.clone();
                                    temp1.set(Calendar.HOUR_OF_DAY, 0);
                                    temp1.set(Calendar.MINUTE, 0);
                                    objectEnd.put("event_starts_datetime", DateUtil.getDateStringFromCalendarGMT(temp1));
                                    objectEnd.put("event_ends_datetime", endString);
                                    Log.d("how many days", objectEnd.toString());
                                    if (isValidEvent(objectEnd)) {
                                        array.put(objectEnd);
                                        if (isRepeat(objectEnd)) {
                                            repeatEvents.add(objectEnd);
                                        }
                                        String dateString = objectEnd.getString("event_starts_datetime");
                                        Calendar dateCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                                        stringSet.add(dateCal.get(Calendar.DAY_OF_MONTH) + "-" + (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR));
                                        String key = (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR);

                                        if (eventsByMonth == null || !eventsByMonth.containsKey(key)) {
                                            List<JSONObject> monthList = new ArrayList<>();
                                            monthList.add(objectEnd);
                                            eventsByMonth.put(key, monthList);
                                        } else {
                                            eventsByMonth.get(key).add(objectEnd);

                                        }
                                    }
                                } else {
                                    Log.d("how many days", y + "");

                                    JSONObject objectDuration = new JSONObject(object.toString());
                                    Calendar temp = (Calendar) startCal.clone();
                                    temp.add(Calendar.DAY_OF_MONTH, y);
                                    temp.set(Calendar.HOUR_OF_DAY, 0);
                                    temp.set(Calendar.MINUTE, 0);

                                    objectDuration.put("event_starts_datetime", DateUtil.getDateStringFromCalendarGMT(temp));
                                    temp.set(Calendar.HOUR_OF_DAY, 23);
                                    temp.set(Calendar.MINUTE, 59);
                                    objectDuration.put("event_ends_datetime", DateUtil.getDateStringFromCalendarGMT(temp));
                                    if (isValidEvent(objectDuration)) {
                                        array.put(objectDuration);
                                        if (isRepeat(objectDuration)) {
                                            repeatEvents.add(objectDuration);
                                        }
                                        String dateString = objectDuration.getString("event_starts_datetime");
                                        Calendar dateCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                                        stringSet.add(dateCal.get(Calendar.DAY_OF_MONTH) + "-" + (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR));
                                        String key = (dateCal.get(Calendar.MONTH) + 1) + "-" + dateCal.get(Calendar.YEAR);

                                        if (eventsByMonth == null || !eventsByMonth.containsKey(key)) {
                                            List<JSONObject> monthList = new ArrayList<>();
                                            monthList.add(objectDuration);
                                            eventsByMonth.put(key, monthList);
                                        } else {
                                            eventsByMonth.get(key).add(objectDuration);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("length", array.length() + "");
        Events.daysHaveEvents = stringSet;
        Events.repeatEvent = repeatEvents;
        Events.eventsByMonth = eventsByMonth;
        return array;
    }

    public static boolean isValidEvent(JSONObject object) {
        try {
            String repeatTo = object.getString("event_repeat_to_date");
            String start = object.getString("event_starts_datetime");
            String end = object.getString("event_ends_datetime");
            String isRepeat = object.getString("event_repeats_type");
            boolean isLong = object.getBoolean("is_long_repeat");
            if (start.equals(end)) {
                return false;
            }
            if (!isRepeat.equals("One-time event") && !isLong) {
                if (DateUtil.getLocalDateObject(repeatTo).compareTo(DateUtil.getLocalDateObject(start)) < 0) {
                    return false;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static List<JSONObject> getEventFromDate(int day, int month, int year) {
        List<JSONObject> list = new ArrayList<>();
        JSONArray response = Events.response;
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
                Calendar new_cal = Calendar.getInstance();
                new_cal.set(year, month - 1, day);
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
                Log.d("tttttt", cal.get(Calendar.DAY_OF_MONTH) + "");
                int duration = calDuration(calendar, cal);
                String type = object.getString("event_repeats_type");
                Boolean isLong = object.getBoolean("is_long_repeat");
                if (type.equals("One-time event")) {
                    if ((cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                        list.add(object);
                    }
                } else {
                    if (type.equals("Daily") && isLong) {
                        if (calendar.compareTo(cal) > 0 || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
//                            list.add(object);
                        }
                    } else if (type.equals("Weekly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 7 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            list.add(object);
                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
                        }

                    } else if (type.equals("Bi-Weekly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 14 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            list.add(object);
                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
                        }

                    } else if (type.equals("Monthly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            list.add(object);
                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
                        }
                    } else if (type.equals("Yearly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            list.add(object);
                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    public static List<JSONObject> sortEvents(List<JSONObject> list) {
        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                try {
                    Date lDate = DateUtil.getLocalDateObject(lhs.getString("event_starts_datetime"));
                    Date rDate = DateUtil.getLocalDateObject(rhs.getString("event_starts_datetime"));
                    Calendar lCal = Calendar.getInstance();
                    Calendar rCal = Calendar.getInstance();
                    lCal.setTime(lDate);
                    rCal.setTime(rDate);
                    if (lCal.compareTo(rCal) < 0) {
                        return -1;
                    }
                    if (lCal.compareTo(rCal) > 0) {
                        return 1;
                    } else {
                        Date lEndDate = DateUtil.getLocalDateObject(lhs.getString("event_ends_datetime"));
                        Date rEndDate = DateUtil.getLocalDateObject(rhs.getString("event_ends_datetime"));
                        Calendar lEndCal = Calendar.getInstance();
                        Calendar rEndCal = Calendar.getInstance();
                        lEndCal.setTime(lEndDate);
                        rEndCal.setTime(rEndDate);
                        if (lEndCal.compareTo(rEndCal) < 0) {
                            return -1;
                        }
                        if (lEndCal.compareTo(rEndCal) > 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;

            }
        });
        return list;


    }


    public static List<JSONObject> getEventsByMonth(int year, int month) throws JSONException {
        List<JSONObject> allevents = new ArrayList<>();
//        if (Events.eventsMonth)
//        List<JSONObject> events = new ArrayList<>();
//        if (Events.eventsByMonth != null) {
        List<JSONObject> events = Events.eventsByMonth.get(month + "-" + year);
//        }
        if (events != null) {
            for (JSONObject t : events) {
                if (t.getString("event_repeats_type").equals("One-time event")) {
                    allevents.add(t);
                }
            }
        }
        List<JSONObject> rEvents = Events.repeatEvent;
        for (int j = 1; j <= DateUtil.getMonthDays(year, month); j++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, j);
            for (int i = 0; i < rEvents.size(); i++) {
                JSONObject object = rEvents.get(i);
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
                String type = object.getString("event_repeats_type");
                Boolean isLong = object.getBoolean("is_long_repeat");
                int day = calDuration(calendar, cal);
                if (type.equals("Daily") && isLong) {
                    if (compareTwoCalendar(calendar, cal)) {
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        endCal.add(Calendar.DAY_OF_MONTH, day);
                        allevents.add(changeObjectDate(cal, endCal, object));
                        continue;
                    }
                } else if (type.equals("Weekly") && isLong) {
                    if (compareTwoCalendar(calendar, cal) && day > 0 && day % 7 == 0) {
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        endCal.add(Calendar.DAY_OF_MONTH, day);
                        allevents.add(changeObjectDate(cal, endCal, object));
                        continue;
                    }
                } else if (type.equals("Bi-Weekly") && isLong) {
                    if (compareTwoCalendar(calendar, cal) && day > 0 && day % 14 == 0) {
                        cal.add(Calendar.DAY_OF_MONTH, day);
                        endCal.add(Calendar.DAY_OF_MONTH, day);
                        allevents.add(changeObjectDate(cal, endCal, object));
                        continue;
                    }
                } else if (type.equals("Monthly") && isLong) {
                    if (compareTwoCalendar(calendar, cal)) {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            endCal.add(Calendar.DAY_OF_MONTH, day);
                            allevents.add(changeObjectDate(cal, endCal, object));
                            continue;
                        }
                    }
                } else if (type.equals("Yearly") && isLong) {
                    if (compareTwoCalendar(calendar, cal)) {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            endCal.add(Calendar.DAY_OF_MONTH, day);
                            allevents.add(changeObjectDate(cal, endCal, object));
                            continue;
                        }
                    }
                }
            }
        }
        return allevents;
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                JSONObject object = response.getJSONObject(i);
//                Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime")));
//                if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month - 1) {
//                    objects.add(object);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return objects;
    }

    public static List<JSONObject> getRepeatEventsFromEvents(JSONArray events) {
        JSONArray response = Events.response;
        List<JSONObject> objects = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                if (!object.getString("event_repeats_type").equals("One-time event") && object.getBoolean("is_long_repeat")) {
                    objects.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return objects;
    }


    public static int calDurationDays(Calendar calendar, Calendar eventCal) {
        Calendar new_cal = (Calendar) calendar.clone();
        new_cal.set(Calendar.HOUR_OF_DAY, eventCal.get(Calendar.HOUR_OF_DAY));
        new_cal.set(Calendar.MINUTE, eventCal.get(Calendar.MINUTE));
//        Calendar new_eventCal = (Calendar) eventCal.clone();
//        new_eventCal.set(Calendar.HOUR_OF_DAY, 0);
//        new_eventCal.set(Calendar.MINUTE, 0);
        int day = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(new_cal.getTimeInMillis() - eventCal.getTimeInMillis()));
        return day;
    }

    public static boolean hasRepeatEvent(Calendar calendar) throws JSONException {
        List<JSONObject> list = Events.repeatEvent;
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            String type = object.getString("event_repeats_type");
            Boolean isLong = object.getBoolean("is_long_repeat");
            Calendar eventCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime")));
            int day = calDuration(calendar, eventCal);
            if (type.equals("Daily") && isLong) {
                if (calendar.compareTo(eventCal) > 0) {
                    return true;
                }
            } else if (type.equals("Weekly") && isLong) {
                if (calendar.compareTo(eventCal) > 0 && day > 0 && day % 7 == 0) {
                    return true;
                }
            } else if (type.equals("Bi-Weekly") && isLong) {
                if (calendar.compareTo(eventCal) > 0 && day > 0 && day % 14 == 0) {
                    return true;
                }
            } else if (type.equals("Monthly") && isLong) {
                if (calendar.compareTo(eventCal) > 0) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)) {
                        return true;
                    }
                }
            } else if (type.equals("Yearly") && isLong) {
                if (calendar.compareTo(eventCal) > 0) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static List<JSONObject> getIgnoredEventsFromResponse(JSONArray events) {
        List<JSONObject> ignord = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {

            try {
                ignord.add(events.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ignord;
    }

    public static int calDuration(Calendar start, Calendar end) {
        Calendar newStart = Calendar.getInstance();
        newStart.setTimeZone(TimeZone.getTimeZone("GMT"));
        newStart.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 0, 0);
        Calendar newEnd = Calendar.getInstance();
        newEnd.setTimeZone(TimeZone.getTimeZone("GMT"));
        newEnd.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 0, 0);
        int day = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(newEnd.getTimeInMillis() - newStart.getTimeInMillis()));
        return day;
    }


    public static Boolean decideWhetherMultiDays(JSONObject object) {
        try {
            String startString = object.getString("event_starts_datetime");
            String endString = object.getString("event_ends_datetime");
            Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
            Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
            if (calDuration(endCal, startCal) >= 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasEvents(int day, int month, int year) {
        JSONArray response = Events.response;
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
                Calendar new_cal = Calendar.getInstance();
                new_cal.set(year, month - 1, day);
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
                Log.d("tttttt", cal.get(Calendar.DAY_OF_MONTH) + "");
                int duration = calDuration(calendar, cal);
                String type = object.getString("event_repeats_type");
                Boolean isLong = object.getBoolean("is_long_repeat");
                if (type.equals("One-time event")) {
                    if ((cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                        return true;
                    }
                } else {
                    if (type.equals("Daily") && isLong) {
                        if (calendar.compareTo(cal) > 0 || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            return true;
                        }
                    } else if (type.equals("Weekly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 7 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            return true;
                        }

                    } else if (type.equals("Bi-Weekly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 14 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            return true;
                        }

                    } else if (type.equals("Monthly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            return true;
                        }
                    } else if (type.equals("Yearly") && isLong) {
                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            return true;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    public static boolean isRepeat(JSONObject object) {
        try {
            String type = object.getString("event_repeats_type");
            boolean isLong = object.getBoolean("is_long_repeat");
            if (!type.equals("One-time event") && isLong) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static JSONObject changeObjectDate(Calendar start, Calendar end, JSONObject object) {
        try {
            JSONObject new_object = new JSONObject(object.toString());
            new_object.put("event_starts_datetime", DateUtil.getDateStringFromCalendarGMT(start));
            new_object.put("event_ends_datetime", DateUtil.getDateStringFromCalendarGMT(end));
            return new_object;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compareTwoCalendar(Calendar c1, Calendar c2) {
        Calendar cp1 = (Calendar) c1.clone();
        Calendar cp2 = (Calendar) c2.clone();
        cp1.set(Calendar.HOUR_OF_DAY, 0);
        cp2.set(Calendar.HOUR_OF_DAY, 0);
        cp1.set(Calendar.MINUTE, 0);
        cp2.set(Calendar.MINUTE, 0);
        return cp1.compareTo(cp2) > 0;
    }

    public static void excuteAsyncTask(int month, int year) {
        Calendar post_calendar = Calendar.getInstance();
        post_calendar.set(year, month - 1, 1);
        Calendar pre_calendar = (Calendar) post_calendar.clone();
        if (!Events.loadingMonth.contains(month + "-" + year)) {
            Events.loadingMonth.add(month + "-" + year);
            new ReadMonthEventTask().execute(month, year);
        }
        post_calendar.add(Calendar.MONTH, 1);
        if (!Events.loadingMonth.contains((post_calendar.get(Calendar.MONTH) + 1) + "-" + post_calendar.get(Calendar.YEAR))) {
            Events.loadingMonth.add((post_calendar.get(Calendar.MONTH) + 1) + "-" + post_calendar.get(Calendar.YEAR));
            new ReadMonthEventTask().execute(post_calendar.get(Calendar.MONTH) + 1, post_calendar.get(Calendar.YEAR));
        }

        pre_calendar.add(Calendar.MONTH, -1);
        if (!Events.loadingMonth.contains((pre_calendar.get(Calendar.MONTH) + 1) + "-" + pre_calendar.get(Calendar.YEAR))) {
            Events.loadingMonth.add((pre_calendar.get(Calendar.MONTH) + 1) + "-" + pre_calendar.get(Calendar.YEAR));
            new ReadMonthEventTask().execute(pre_calendar.get(Calendar.MONTH) + 1, pre_calendar.get(Calendar.YEAR));

        }

        post_calendar.add(Calendar.MONTH, 1);
        if (!Events.loadingMonth.contains((post_calendar.get(Calendar.MONTH) + 1) + "-" + post_calendar.get(Calendar.YEAR))) {
            Events.loadingMonth.add((post_calendar.get(Calendar.MONTH) + 1) + "-" + post_calendar.get(Calendar.YEAR));
            new ReadMonthEventTask().execute(post_calendar.get(Calendar.MONTH) + 1, post_calendar.get(Calendar.YEAR));
        }

        pre_calendar.add(Calendar.MONTH, -1);
        if (!Events.loadingMonth.contains((pre_calendar.get(Calendar.MONTH) + 1) + "-" + pre_calendar.get(Calendar.YEAR))) {
            Events.loadingMonth.add((pre_calendar.get(Calendar.MONTH) + 1) + "-" + pre_calendar.get(Calendar.YEAR));
            new ReadMonthEventTask().execute(pre_calendar.get(Calendar.MONTH) + 1, pre_calendar.get(Calendar.YEAR));

        }

    }


    public static List<JSONObject> getCalendarTypeFromResponse(JSONArray response) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                list.add(response.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static Set<String> getNotShownCalendarId() {
        Set<String> notShownCalendarId = new HashSet<>();
        for (JSONObject object : Events.calendarTypeList) {
            try {
                if (object.getBoolean("if_show") == false) {
                    notShownCalendarId.add(object.getString("calendar_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return notShownCalendarId;
    }
}
