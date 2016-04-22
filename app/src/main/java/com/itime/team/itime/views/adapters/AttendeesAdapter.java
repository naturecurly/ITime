package com.itime.team.itime.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.itime.team.itime.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.utils.JsonObjectFormRequest;
import com.itime.team.itime.utils.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Weiwei Cai on 16/4/12.
 * This is listView adapter, which shows all attendees of a meeting and their status (Accept, Reject
 * , No Response).
 */
public class AttendeesAdapter extends BaseAdapter {
    private ArrayList<HashMap<String,String>> mItems;
    private Context mContext;
    private LayoutInflater inflater;

    public AttendeesAdapter(Context mContext, ArrayList<HashMap<String,String>> mItems){
        this.mContext = mContext;
        this.mItems = mItems;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String,String> map = mItems.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.meeting_detail_listview, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.meeting_attendee_image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.meeting_attendee_name);
            viewHolder.id = (TextView) convertView.findViewById(R.id.meeting_attendee_id);
            viewHolder.status = (TextView) convertView.findViewById(R.id.meeting_attendee_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.image.setImageResource(R.drawable.default_profile_image);
        viewHolder.name.setText(map.get("name"));
        viewHolder.id.setText(map.get("id"));
        viewHolder.status.setText(map.get("status"));
        boolean isGrey = !Boolean.valueOf(map.get("isFriend")) && !User.ID.equals(viewHolder.name.getText().toString());
        if(isGrey){
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey));
            final ViewHolder finalViewHolder = viewHolder;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Do you want to add '" + finalViewHolder.name.getText() + "' as your " +
                            "new iTIME friend?");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Add New Friend");
                    final ViewHolder finalViewHolder1 = finalViewHolder;
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addFriend(finalViewHolder1.id.getText().toString());
                        }
                    });
                    builder.show();
                }
            });

        } else {

        }
        return convertView;
    }

    private void addFriend(String name){
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", User.ID);
            object.put("friend_id", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.ADD_FRIEND_REQUEST;
        Map<String, String> params = new HashMap();
        params.put("json", object.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(mContext, mContext.
                        getString(R.string.search_friend_added_friend_info), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private class ViewHolder{
        TextView name;
        TextView id;
        TextView status;
        ImageView image;
    }
}
