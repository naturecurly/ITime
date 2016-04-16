package com.itime.team.itime.utils;

import com.itime.team.itime.bean.Events;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by leveyleonhardt on 3/19/16.
 */
public class EventUtil {

    public static boolean isTodayPressed = false;


    public static JSONArray initialEvents() {
        JSONArray array = new JSONArray();
        JSONArray response = Events.response;
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                if (!decideWhetherMultiDays(object)) {
                    array.put(object);
                } else {
                    String startString = object.getString("event_starts_datetime_new");
                    String endString = object.getString("event_ends_datetime_new");
                    Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
                    Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
                    int day = calDurationDays(startCal,endCal);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public static List<JSONObject> getEventFromDate(int day, int month, int year) {
        List<JSONObject> list = new ArrayList<>();
        JSONArray response = Events.response;
//        Date date = DateUtil.getLocalDateObject(dateString);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime_new"));
                Date enddate = DateUtil.getLocalDateObject(object.getString("event_ends_datetime_new"));
                Calendar calendar = DateUtil.getCalendarFromInteger(day, month, year);
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(date);
                Calendar endCal = DateUtil.getLocalDateObjectToCalendar(enddate);
                int duration = calDurationDays(cal, endCal);
                String type = object.getString("event_repeats_type_new");
                Boolean isLong = object.getBoolean("is_long_repeat");
                if (type.equals("One-time event")) {
                    if ((cal.get(Calendar.DAY_OF_MONTH) == day && (cal.get(Calendar.MONTH) + 1) == month && cal.get(Calendar.YEAR) == year)) {
                        list.add(object);
                    } else if ((endCal.get(Calendar.DAY_OF_MONTH) == day && (endCal.get(Calendar.MONTH) + 1) == month && endCal.get(Calendar.YEAR) == year)) {
                        list.add(object);
                    } else if (cal.compareTo(calendar) < 0 && endCal.compareTo(calendar) > 0) {
                        list.add(object);
                    }
                } else {
                    if (type.equals("Daily") && isLong) {
                        if (calendar.compareTo(cal) > 0 || (cal.get(Calendar.DAY_OF_MONTH) == day && (cal.get(Calendar.MONTH) + 1) == month && cal.get(Calendar.YEAR) == year)) {
                            list.add(object);
                        }
                    } else if (type.equals("Weekly") && isLong) {

                    } else if (type.equals("Bi-Weekly") && isLong) {

                    } else if (type.equals("Monthly") && isLong) {

                    } else if (type.equals("Yearly") && isLong) {

                    }
                }
//                if (object.getString("event_starts_datetime").equals(dateString)) {
//                    list.add(object);
//                }
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


    public static List<JSONObject> getEventsByMonth(int year, int month) {
        JSONArray response = Events.response;
        List<JSONObject> objects = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Calendar cal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime")));
                if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month - 1) {
                    objects.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    public static List<JSONObject> getRepeatEventsFromEvents(JSONArray events) {
        JSONArray response = Events.response;
        List<JSONObject> objects = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                if (!object.getString("event_repeats_type_new").equals("One-time event")) {
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
        new_cal.set(Calendar.HOUR_OF_DAY, 0);
        new_cal.set(Calendar.MINUTE, 0);
        Calendar new_eventCal = (Calendar) eventCal.clone();
        new_eventCal.set(Calendar.HOUR_OF_DAY, 0);
        new_eventCal.set(Calendar.MINUTE, 0);
        int day = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(new_cal.getTimeInMillis() - new_eventCal.getTimeInMillis()));
        return day;
    }

    public static boolean hasRepeatEvent(Calendar calendar) throws JSONException {
        List<JSONObject> list = Events.repeatEvent;
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            String type = object.getString("event_repeats_type_new");
            Boolean isLong = object.getBoolean("is_long_repeat");
            Calendar eventCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime_new")));
//            Calendar new_cal = (Calendar) calendar.clone();
//            new_cal.set(Calendar.HOUR_OF_DAY, 0);
//            new_cal.set(Calendar.MINUTE, 0);
//            Calendar new_eventCal = (Calendar) eventCal.clone();
//            new_eventCal.set(Calendar.HOUR_OF_DAY, 0);
//            new_eventCal.set(Calendar.MINUTE, 0);
//            int day = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(new_cal.getTimeInMillis() - new_eventCal.getTimeInMillis()));
//            int day = calendar.get(Calendar.DAY_OF_YEAR) - eventCal.get(Calendar.DAY_OF_YEAR);
            int day = calDurationDays(calendar, eventCal);
            if (type.equals("Daily") && isLong) {
                if (calendar.compareTo(eventCal) > 0) {
                    return true;
                }
            } else if (type.equals("Weekly") && isLong) {
                if (day > 0 && day % 7 == 0) {
                    return true;
                }
            } else if (type.equals("Bi-Weekly") && isLong) {
                if (day > 0 && day % 14 == 0) {
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


    public static void getIgnoredEventsFromResponse(JSONArray events) {
        for (int i = 0; i < events.length(); i++) {
            try {
                Events.ignoredEvent.add(events.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean decideWhetherMultiDays(JSONObject object) {
        try {
            String startString = object.getString("event_starts_datetime_new");
            String endString = object.getString("event_ends_datetime_new");
            Calendar startCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(startString));
            Calendar endCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(endString));
            if (calDurationDays(startCal, endCal) >= 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


}
