package com.itime.team.itime.task;

import android.os.AsyncTask;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.utils.EventUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by leveyleonhardt on 4/20/16.
 */
public class ReadMonthEventTask extends AsyncTask<Integer, Void, Void> {
    @Override
    protected Void doInBackground(Integer... params) {
        int month = params[0];
        int year = params[1];
        try {
            if (Events.eventsMonthMap.size() == 0 || !Events.eventsMonthMap.containsKey(month + "-" + year)) {
                List<JSONObject> list = EventUtil.getEventsByMonth(year, month);

                Set<List<JSONObject>> set = new HashSet<>();

//                Events.eventsMonth.put(month + "-" + year, list);
                Events.eventsMonthMap.put(month + "-" + year, set);
                Events.eventsMonthMap.get(month + "-" + year).add(list);            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
