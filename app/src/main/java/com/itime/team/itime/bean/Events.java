package com.itime.team.itime.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by leveyleonhardt on 3/18/16.
 */
public class Events {
    public static JSONArray response;

    public static List<JSONObject> repeatEvent;

    public static List<JSONObject> ignoredEvent;

    public static Set<String> daysHaveEvents;

    public static Map<String, List<JSONObject>> eventsByMonth;

    public static Map<String, List<JSONObject>> eventsMonth = new HashMap<>();

    public static Map<String, Set<List<JSONObject>>> eventsMonthMap = new HashMap<>();
}
