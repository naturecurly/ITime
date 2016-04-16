package com.itime.team.itime.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 16/4/12.
 */
public class MeetingDetailReasonDialogFragment extends DialogFragment implements View.OnClickListener{
    private View mView;
    private EditText mNote;
    private Button mSend,mCancel;
    private String meetingID;

    public MeetingDetailReasonDialogFragment(String meetingID){
        this.meetingID = meetingID;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.meeting_detail_reason_dialog, null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
        return mView;
    }

    private void init(){
        mNote = (EditText) mView.findViewById(R.id.meeting_detail_reason_note);
        mSend = (Button) mView.findViewById(R.id.meeting_detail_reason_send);
        mCancel = (Button) mView.findViewById(R.id.meeting_detail_reason_cancel);
        mSend.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mSend.getId()){
            send();
        }else if(v.getId() == mCancel.getId()){
            dismiss();
        }
    }

    private void send() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("meeting_id", meetingID);
            jsonObject.put("user_id", User.ID);
            jsonObject.put("meeting_status","Reject");
            jsonObject.put("reason",mNote.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = URLs.MEETING_STATUS_CHANGE_WITH_REASON;
        Map<String, String> params = new HashMap();
        params.put("json", jsonObject.toString());

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String result = response.getString("result");
                    if (!result.equals("success")){
                        Toast.makeText(getContext(), getString(R.string.time_out), Toast.LENGTH_LONG);
                    }else{
                        MeetingDetailReasonDialogFragment.this.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}
