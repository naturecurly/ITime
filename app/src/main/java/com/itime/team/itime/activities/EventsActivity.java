package com.itime.team.itime.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.itime.team.itime.R;
import com.itime.team.itime.bean.Events;
import com.itime.team.itime.utils.CalendarTypeUtil;
import com.itime.team.itime.utils.DateUtil;
import com.itime.team.itime.utils.EventUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by leveyleonhardt on 3/18/16.
 */
public class EventsActivity extends AppCompatActivity {
    private static final int EVENT_SEARCH_REQUEST = 106;
    private RecyclerView eventRecyclerView;
    private SearchView searchView;
    private List<JSONObject> result = new ArrayList<>();
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Log.d("eventtest", "success");
        searchView = (SearchView) findViewById(R.id.events_search);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                result = EventUtil.searchByName(query);
                Log.d("search", query);
                eventRecyclerView.getAdapter().notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
                final JSONObject object = result.get(position);
                Log.d("eventtest", object.toString());
                holder.eventTitle.setText(object.getString("event_name"));
                holder.eventPosition.setText(object.getString("event_venue_location"));
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(this,EventsDetailActivity.class);
                        jumpToDetail(object);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return result.size();
        }
    }

    public class eventViewHolder extends RecyclerView.ViewHolder {
        private TextView eventTitle;
        private TextView eventPosition;
        private View view;

        public eventViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            eventTitle = (TextView) itemView.findViewById(R.id.event_name);
            eventPosition = (TextView) itemView.findViewById(R.id.event_location);
        }
    }

    public void jumpToDetail(JSONObject object) {
        Intent intent = new Intent(this, EventsDetailActivity.class);
        Bundle bundle = new Bundle();
        try {
            bundle.putString("event_name", object.getString("event_name"));
            bundle.putString("venue", object.getString("event_venue_location"));
            bundle.putString("start_time", object.getString("event_starts_datetime"));
            bundle.putString("end_time", object.getString("event_ends_datetime"));
            bundle.putString("repeat_type", object.getString("event_repeats_type"));
            bundle.putString("alert", object.getString("event_alert"));
            bundle.putString("calendar_type", CalendarTypeUtil.findCalendarById(object.getString("calendar_id")).calendarName);
            bundle.putParcelable("calendar_type_pacelable", CalendarTypeUtil.findCalendarById(object.getString("calendar_id")));
            bundle.putString("event_id", object.getString("event_id"));
            bundle.putString("json", object.toString());
            intent.putExtras(bundle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivityForResult(intent, EVENT_SEARCH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EVENT_SEARCH_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                update = true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
        Log.d("back", update + "");
        if (update == true) {
            setResult(Activity.RESULT_OK);
        }
    }
}
