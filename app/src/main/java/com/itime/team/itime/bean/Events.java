package com.itime.team.itime.bean;

import com.itime.team.itime.model.ParcelableCalendarType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by leveyleonhardt on 3/18/16.
 */
public class Events {
    public static JSONArray response;

    public static List<JSONObject> repeatEvent = new ArrayList<>();

    public static List<JSONObject> ignoredEvent = new ArrayList<>();

    public static Set<String> daysHaveEvents = new HashSet<>();

    public static Map<String, List<JSONObject>> eventsByMonth = new HashMap<>();

//    public static Map<String, List<JSONObject>> eventsMonth = new HashMap<>();

    public static Map<String, Set<List<JSONObject>>> eventsMonthMap = new HashMap<>();

    public static Set<String> loadingMonth = new HashSet<>();

    public static String daySelected = "";

    public static List<ParcelableCalendarType> calendarTypeList = new ArrayList<>();

    public static Set<String> notShownId = new HashSet<>();
}
