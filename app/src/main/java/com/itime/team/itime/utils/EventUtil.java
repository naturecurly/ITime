package com.itime.team.itime.utils;

import android.util.Log;

import com.google.api.services.calendar.model.Event;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.fragments.EventDetailFragment;
import com.itime.team.itime.model.ParcelableCalendarType;
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

    public static final String ONE_TIME = "One-time event";
    public static final String DAILY = "Daily";
    public static final String WEEKLY = "Weekly";
    public static final String BI_WEEKLY = "Bi-Weekly";
    public static final String MONTHLY = "Monthly";
    public static final String YEARLY = "Yearly";
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
//                Log.d("isValid", object.toString());
//                Log.d("notShownId", Events.notShownId.toString());

                if (isValidEvent(object) && !Events.notShownId.contains(object.getString("calendar_id"))) {
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
//                        Log.d("this", startCal.get(Calendar.DAY_OF_MONTH) + " " + endCal.get(Calendar.DAY_OF_MONTH));
                        int day = calDuration(startCal, endCal);
                        if (day > 0) {
//                            Log.d("how many days", day + "");
                            for (int y = 0; y <= day; y++) {
                                if (y == 0) {

                                    JSONObject objectStart = new JSONObject(object.toString());

                                    Calendar temp = (Calendar) startCal.clone();
                                    temp.set(Calendar.HOUR_OF_DAY, 23);
                                    temp.set(Calendar.MINUTE, 59);
                                    objectStart.put("event_starts_datetime", startString);
                                    objectStart.put("event_ends_datetime", DateUtil.getDateStringFromCalendarGMT(temp));
//                                    Log.d("how many days", objectStart.toString());
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
//                                    Log.d("how many days", objectEnd.toString());
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
//                                    Log.d("how many days", y + "");

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
        Events.repeatEvent = sortEvents(repeatEvents);
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
                if (DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(repeatTo)).compareTo(DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(start))) < 0) {
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
//        JSONArray response = Events.response;
        String dateString = day + "-" + (month - 1) + "-" + year;
        String key = month + "-" + year;
        List<JSONObject> response = Events.eventsByMonth.get(key);
        List<JSONObject> repeat = Events.repeatEvent;
//        Log.d("responseSize", response.size() + "");
        boolean hasIgn = false;
        if (Events.ignoredEventMap.containsKey(dateString)) {
            hasIgn = true;
        }
        if (response != null) {
            for (int i = 0; i < response.size(); i++) {
                try {

                    JSONObject object = response.get(i);
                    Log.d("yyyyyy", object.toString());
                    String event_id = object.getString("event_id");
                    Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                    Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
//                    Date repeatToDate = DateUtil.getLocalDateObject(object.getString("event_repeat_to_date"));
                    Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
                    Calendar new_cal = Calendar.getInstance();
                    new_cal.set(year, month - 1, day);
                    Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                    Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
//                    Calendar repeatToCal = DateUtil.getLocalDateObjectToCalendar(repeatToDate);
//                Log.d("tttttt", cal.get(Calendar.DAY_OF_MONTH) + "");

                    String type = object.getString("event_repeats_type");


                    if (type.equals("One-time event")) {
                        if ((cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
                            list.add(object);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        for (int i = 0; i < repeat.size(); i++) {
            try {
                JSONObject object = repeat.get(i);
                boolean skip = false;
                String type = object.getString("event_repeats_type");
                String event_id = object.getString("event_id");
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                Date repeatToDate = DateUtil.getLocalDateObject(object.getString("event_repeat_to_date"));
                Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
                Calendar new_cal = Calendar.getInstance();
                new_cal.set(year, month - 1, day);
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
//                Calendar repeatToCal = null;
//                try {
//                    repeatToCal = DateUtil.getLocalDateObjectToCalendar(repeatToDate);
//                } catch (Exception e) {
////                    Log.d("repeatlog",e.printStackTrace())
//                    e.printStackTrace();
//                    repeatToCal = (Calendar) endCal.clone();
//                }
                int duration = calDuration(cal, calendar);
                Boolean isLong = object.getBoolean("is_long_repeat");
                if (hasIgn) {
                    for (JSONObject ignoreObject : Events.ignoredEventMap.get(dateString)) {
                        if (ignoreObject.getString("event_id").equals(event_id)) {

                            Calendar ignoredStart = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(ignoreObject.getString("event_starts_datetime")));
                            if (ignoredStart.get(Calendar.HOUR_OF_DAY) == DateUtil.getLocalDateObjectToCalendar(date).get(Calendar.HOUR_OF_DAY) && ignoredStart.get(Calendar.MINUTE) == DateUtil.getLocalDateObjectToCalendar(date).get(Calendar.MINUTE))
                                skip = true;
                        }

                    }
                }
                if (!skip) {
                    Calendar repeatToCal = DateUtil.getLocalDateObjectToCalendar(repeatToDate);

                    if (type.equals("Daily") && duration >= 0) {
                        if (isLong) {
                            //                            if (calendar.compareTo(cal) > 0 || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {

                            cal.add(Calendar.DAY_OF_MONTH, duration);
                            endCal.add(Calendar.DAY_OF_MONTH, duration);
                            list.add(changeObjectDate(cal, endCal, object));
                            //                            list.add(object);
                            //                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (duration <= days) {
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        }
                    } else if (type.equals("Weekly") && duration >= 0) {
                        if (isLong) {
                            if (duration % 7 == 0) {
                                //                            list.add(object);
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (duration <= days && duration % 7 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        }
                    } else if (type.equals("Bi-Weekly") && duration >= 0) {
                        if (isLong) {
                            if (duration % 14 == 0) {
                                //                            list.add(object);
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (duration <= days && duration % 14 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        }

                    } else if (type.equals("Monthly") && duration >= 0) {
                        if (isLong) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                                //                            list.add(object);
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (duration <= days && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        }
                    } else if (type.equals("Yearly") && duration >= 0) {
                        if (isLong) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                                //                            list.add(object);
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (duration <= days && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, duration);
                                endCal.add(Calendar.DAY_OF_MONTH, duration);
                                list.add(changeObjectDate(cal, endCal, object));
                            }
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
            boolean hasIgn = false;
            String dateString = j + "-" + (month - 1) + "-" + year;
            if (Events.ignoredEventMap.containsKey(dateString)) {
                hasIgn = true;
            }
            for (int i = 0; i < rEvents.size(); i++) {
                boolean skip = false;
                JSONObject object = rEvents.get(i);
                String event_id = object.getString("event_id");
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
                String type = object.getString("event_repeats_type");
                Boolean isLong = object.getBoolean("is_long_repeat");
                Calendar repeatToCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_repeat_to_date")));
                int day = calDuration(cal, calendar);
                if (hasIgn) {
                    for (JSONObject ignoreObject : Events.ignoredEventMap.get(dateString)) {
                        if (ignoreObject.getString("event_id").equals(event_id)) {
                            Calendar ignoredStart = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(ignoreObject.getString("event_starts_datetime")));
                            if (ignoredStart.get(Calendar.HOUR_OF_DAY) == DateUtil.getLocalDateObjectToCalendar(date).get(Calendar.HOUR_OF_DAY) && ignoredStart.get(Calendar.MINUTE) == DateUtil.getLocalDateObjectToCalendar(date).get(Calendar.MINUTE))
                                skip = true;
                        }

                    }
                }
                if (skip == false) {
                    if (type.equals("Daily") && day >= 0) {
                        if (isLong) {
                            cal.add(Calendar.DAY_OF_MONTH, day);
                            endCal.add(Calendar.DAY_OF_MONTH, day);
                            allevents.add(changeObjectDate(cal, endCal, object));
                            continue;
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (day <= days) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        }
                    } else if (type.equals("Weekly") && day >= 0) {
                        if (isLong) {
                            if (day % 7 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (day <= days && day % 7 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        }
                    } else if (type.equals("Bi-Weekly") && day >= 0) {
                        if (isLong) {
                            if (day % 14 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (day <= days && day % 14 == 0) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        }
                    } else if (type.equals("Monthly") && day >= 0) {
                        if (isLong) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (day <= days && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        }
                    } else if (type.equals("Yearly") && day >= 0) {
                        if (isLong) {
                            if (calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        } else {
                            int days = calDuration(cal, repeatToCal);
                            if (day <= days && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
                                cal.add(Calendar.DAY_OF_MONTH, day);
                                endCal.add(Calendar.DAY_OF_MONTH, day);
                                allevents.add(changeObjectDate(cal, endCal, object));
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return allevents;

    }


    public static boolean hasRepeatEvent(Calendar calendar) throws JSONException {
        List<JSONObject> list = Events.repeatEvent;
//        list = sortEvents(list);
        boolean hasIgn = false;
        String dateString = calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
        if (Events.ignoredEventMap.containsKey(dateString)) {
            hasIgn = true;
        }
        for (int i = 0; i < list.size(); i++) {
            boolean skip = false;

            JSONObject object = list.get(i);
            String event_id = object.getString("event_id");
            String type = object.getString("event_repeats_type");
            Boolean isLong = object.getBoolean("is_long_repeat");
            Calendar eventCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime")));
            int day = calDuration(eventCal, calendar);
            if (day < 0) {
                break;
            }
            if (hasIgn) {
                for (JSONObject ignoreObject : Events.ignoredEventMap.get(dateString)) {
                    if (ignoreObject.getString("event_id").equals(event_id)) {
                        Calendar ignoredStart = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(ignoreObject.getString("event_starts_datetime")));
                        if (ignoredStart.get(Calendar.HOUR_OF_DAY) == eventCal.get(Calendar.HOUR_OF_DAY) && ignoredStart.get(Calendar.MINUTE) == eventCal.get(Calendar.MINUTE))
                            skip = true;
                    }

                }
            }
            if (skip == false) {
                Calendar repeatToCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_repeat_to_date")));

                if (type.equals("Daily") && day >= 0) {
                    if (isLong) {
                        return true;
                    } else {
                        int days = calDuration(eventCal, repeatToCal);
                        if (day <= days) {
                            return true;
                        }
                    }
                } else if (type.equals("Weekly") && day >= 0) {
                    if (isLong) {
                        if (day % 7 == 0) {
                            return true;
                        }
                    } else {
                        int days = calDuration(eventCal, repeatToCal);
                        if (day <= days && day % 7 == 0) {
                            return true;
                        }
                    }
                } else if (type.equals("Bi-Weekly") && day >= 0) {
                    if (isLong) {
                        if (day % 14 == 0) {
                            return true;
                        }
                    } else {
                        int days = calDuration(eventCal, repeatToCal);
                        if (day <= days && day % 14 == 0) {
                            return true;
                        }
                    }
                } else if (type.equals("Monthly") && day >= 0) {
                    if (isLong) {

                        if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)) {
                            return true;
                        }

                    } else {
                        int days = calDuration(eventCal, repeatToCal);
                        if (day <= days && calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)) {
                            return true;
                        }
                    }
                } else if (type.equals("Yearly") && day >= 0) {
                    if (isLong) {
                        if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH)) {
                            return true;
                        }
                    } else {
                        int days = calDuration(eventCal, repeatToCal);
                        if (day <= days && calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static Map<String, List<JSONObject>> processIgnoredEvents(JSONArray events) {
        Map<String, List<JSONObject>> ignored = new HashMap<>();
        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject object = events.getJSONObject(i);
                String id = object.getString("event_id");
                String dateString = object.getString("event_starts_datetime");
                Calendar calendar = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateString));
                if (findEventById(id) == null) {
                    continue;
                }
                int day = calDuration(findEventById(id));


                for (int d = 0; d <= day; d++) {
                    JSONObject clone_object = new JSONObject(object.toString());
                    String key = calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);

                    if (d > 0 && d <= day) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        clone_object.put("event_starts_datetime", DateUtil.getDateStringFromCalendarGMT(calendar));
                    }

                    if (ignored.containsKey(key)) {
                        ignored.get(key).add(clone_object);
                    } else {
                        List<JSONObject> list = new ArrayList<>();
                        list.add(clone_object);
                        ignored.put(key, list);
                    }
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, List<JSONObject>> entry : ignored.entrySet()) {
            for (JSONObject object : entry.getValue()) {
                Log.d("testMul", object.toString());

            }
        }
        return ignored;
    }

    public static int calDuration(Calendar start, Calendar end) {
        Calendar newStart = Calendar.getInstance();
        newStart.setTimeZone(TimeZone.getTimeZone("UTC"));
        newStart.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 0, 0);
        Calendar newEnd = Calendar.getInstance();
        newEnd.setTimeZone(TimeZone.getTimeZone("UTC"));
        newEnd.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 0, 0);
        int day = (int) TimeUnit.MILLISECONDS.toDays(newEnd.getTimeInMillis() - newStart.getTimeInMillis());
        return day;
    }

    public static int calDuration(JSONObject object) {
        String startString = "";
        String endString = "";
        try {
            startString = object.getString("event_starts_datetime");
            endString = object.getString("event_ends_datetime");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
        Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
        return calDuration(startCal, endCal);
    }

    public static Boolean decideWhetherMultiDays(JSONObject object) {
        try {
            String startString = object.getString("event_starts_datetime");
            String endString = object.getString("event_ends_datetime");
            Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
            Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
            if (calDuration(startCal, endCal) >= 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static boolean hasEvents(int day, int month, int year) {
//        JSONArray response = Events.response;
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                JSONObject object = response.getJSONObject(i);
//                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
//                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
//                Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
//                Calendar new_cal = Calendar.getInstance();
//                new_cal.set(year, month - 1, day);
//                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
//                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
//                Log.d("tttttt", cal.get(Calendar.DAY_OF_MONTH) + "");
//                int duration = calDuration(calendar, cal);
//                String type = object.getString("event_repeats_type");
//                Boolean isLong = object.getBoolean("is_long_repeat");
//                if (type.equals("One-time event")) {
//                    if ((cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                        return true;
//                    }
//                } else {
//                    if (type.equals("Daily") && isLong) {
//                        if (calendar.compareTo(cal) > 0 || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            return true;
//                        }
//                    } else if (type.equals("Weekly") && isLong) {
//                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 7 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            return true;
//                        }
//
//                    } else if (type.equals("Bi-Weekly") && isLong) {
//                        if ((calendar.compareTo(cal) > 0 && duration > 0 && duration % 14 == 0) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            return true;
//                        }
//
//                    } else if (type.equals("Monthly") && isLong) {
//                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            return true;
//                        }
//                    } else if (type.equals("Yearly") && isLong) {
//                        if ((calendar.compareTo(cal) > 0 && calendar.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) || (cal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && (cal.get(Calendar.MONTH)) == calendar.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))) {
//                            return true;
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return false;
//    }


    public static boolean isRepeat(JSONObject object) {
        try {
            String type = object.getString("event_repeats_type");
            boolean isLong = object.getBoolean("is_long_repeat");
            if (!type.equals("One-time event")) {
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


//    public static List<ParcelableCalendarType> getCalendarTypeFromResponse(JSONArray response) {
//        List<ParcelableCalendarType> list = new ArrayList<>();
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                list.add(response.getJSONObject(i));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return list;
//    }

    public static Set<String> getNotShownCalendarId() {
        Set<String> notShownCalendarId = new HashSet<>();
        for (ParcelableCalendarType object : Events.calendarTypeList) {
            if (object.ifShow == false) {
                notShownCalendarId.add(object.calendarId);
            }
        }
        return notShownCalendarId;
    }

    public static Map<String, JSONObject> processRawEvents(JSONArray response) {
        Map<String, JSONObject> rawEvents = new HashMap<>();

        for (int i = 0; i < response.length(); i++) {
            try {
                rawEvents.put(response.getJSONObject(i).getString("event_id"), response.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rawEvents;
    }

    public static JSONObject findEventById(String id) {
        return Events.rawEvents.get(id);
    }


//    public static boolean isIgnored(Calendar calendar, String id) {
//        List<JSONObject> ignoredEvents = Events.ignoredEvent;
//        for (int i = 0; i < ignoredEvents.size(); i++) {
//            ignoredEvents.get(i)
//        }
//    }

    public static Set<String> findEventsByCalendarType(Set<String> calendarTypeIds) {
        Set<String> events = new HashSet<>();
        Log.i("EventUtils", "" + Events.rawEvents.size());
        for (JSONObject o : Events.rawEvents.values()) {
            try {
                String calType = o.getString("calendar_id");
                if (calendarTypeIds.contains(calType)) {
                    events.add(o.getString("event_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    public static List<JSONObject> searchByName(String query) {
        List<JSONObject> result = new ArrayList<>();
        for (JSONObject rawEvent : Events.rawEvents.values()) {
            try {
                Log.d("search", rawEvent.getString("event_name"));
                if (isValidEvent(rawEvent)) {
                    if (rawEvent.getString("event_name").toLowerCase().indexOf(query.toLowerCase()) >= 0) {
                        result.add(rawEvent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

//    public static Set<String> newHasRepeateEventOneMonth(int month, int year) {
//        List<JSONObject> repeats = Events.repeatEvent;
//        Set<String> dateHasRepeatEvent = new HashSet<>();
//        Calendar cal = Calendar.getInstance();
//        cal.set(year, month - 1, 1);
//        for (int i = 0; i < repeats.size(); i++) {
//            JSONObject repeat = repeats.get(i);
//            try {
//                String dateStart = repeat.getString("event_starts_datetime");
//                Calendar calStart = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(dateStart));
//                if (calDuration(cal, calStart) <= DateUtil.getMonthDays(year, month)) {
//
//                } else {
//                    break;
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}
