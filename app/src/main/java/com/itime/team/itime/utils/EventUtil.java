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

/**
 * Created by leveyleonhardt on 3/19/16.
 */
public class EventUtil {

    public static boolean isTodayPressed = false;

    public static List<JSONObject> getEventFromDate(String dateString) {
        List<JSONObject> list = new ArrayList<>();
        JSONArray response = Events.response;
//        Date date = DateUtil.getLocalDateObject(dateString);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject object = response.getJSONObject(i);
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                if ((cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)).equals(dateString)) {
                    list.add(object);
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
                if (!object.getString("event_repeats_type").equals("One-time event")) {
                    objects.add(object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return objects;
    }

    public static boolean hasRepeatEvent(Calendar calendar) throws JSONException {
        List<JSONObject> list = Events.repeatEvent;
        for (int i = 0; i < list.size(); i++) {
            JSONObject object = list.get(i);
            String type = object.getString("event_repeats_type");
            Calendar eventCal = DateUtil.getLocalDateObjectToCalendar(DateUtil.getLocalDateObject(object.getString("event_starts_datetime_new")));
            int day = calendar.get(Calendar.DAY_OF_YEAR) - eventCal.get(Calendar.DAY_OF_YEAR);
            if (type.equals("Daily")) {
                if (calendar.compareTo(eventCal) > 0) {
                    return true;
                }
            } else if (type.equals("Weekly")) {
                if (day > 0 && day % 7 == 0) {
                    return true;
                }
            } else if (type.equals("Bi-Weekly")) {
                if (day > 0 && day % 14 == 0) {
                    return true;
                }
            } else if (type.equals("Monthly")) {
                if (calendar.compareTo(eventCal) > 0) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH)) {
                        return true;
                    }
                }
            } else if (type.equals("Yearly")) {
                if (calendar.compareTo(eventCal) > 0) {
                    if (calendar.get(Calendar.DAY_OF_MONTH) == eventCal.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.MONTH) == eventCal.get(Calendar.MONTH)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
