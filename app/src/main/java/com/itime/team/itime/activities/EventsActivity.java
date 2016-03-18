package com.itime.team.itime.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by leveyleonhardt on 3/18/16.
 */
public class EventsActivity extends AppCompatActivity {
    private RecyclerView eventRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Log.d("eventtest", "success");
        eventRecyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView.setAdapter(new eventAdapter());
    }

    public class eventAdapter extends RecyclerView.Adapter<eventViewHolder> {

        @Override
        public eventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater().from(getApplication());
            View v = inflater.inflate(R.layout.item_events_list, parent, false);
            return new eventViewHolder(v);
        }

        @Override
        public void onBindViewHolder(eventViewHolder holder, int position) {
            try {
                JSONObject object = Events.response.getJSONObject(position);
                Log.d("eventtest", object.toString());
                holder.eventTitle.setText(object.getString("event_name"));
                holder.eventPosition.setText(object.getString("event_venue_location"));
                Date date = DateUtil.getLocalDateObject(object.getString("event_starts_datetime"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                holder.eventDate.setText(DateUtil.weekName[cal.get(Calendar.DAY_OF_WEEK) - 1] + " " + cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                holder.eventStartTime.setText(cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
                date = DateUtil.getLocalDateObject(object.getString("event_ends_datetime"));
                cal.setTime(date);
                holder.eventEndTime.setText(cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return Events.response.length();
        }
    }

    public class eventViewHolder extends RecyclerView.ViewHolder {
        private TextView eventDate;
        private TextView eventStartTime;
        private TextView eventEndTime;
        private TextView eventTitle;
        private TextView eventPosition;

        public eventViewHolder(View itemView) {
            super(itemView);
            eventDate = (TextView) itemView.findViewById(R.id.event_date_text_view);
            eventStartTime = (TextView) itemView.findViewById(R.id.start_time_text_view);
            eventEndTime = (TextView) itemView.findViewById(R.id.end_time_text_view);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title_text_view);
            eventPosition = (TextView) itemView.findViewById(R.id.location_text_view);
        }
    }
}
