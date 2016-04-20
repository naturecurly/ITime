package com.itime.team.itime.task;

import android.os.AsyncTask;

import com.itime.team.itime.bean.Events;
import com.itime.team.itime.utils.EventUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by leveyleonhardt on 4/20/16.
 */
public class ReadMonthEventTask extends AsyncTask<Integer, Void, Void> {
    @Override
    protected Void doInBackground(Integer... params) {
        int month = params[0];
        int year = params[1];
        try {
            if (Events.eventsMonth.size() == 0 || !Events.eventsMonth.containsKey(month + "-" + year)) {
                List<JSONObject> list = EventUtil.getEventsByMonth(year, month);
                Events.eventsMonth.put(month + "-" + year, list);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
