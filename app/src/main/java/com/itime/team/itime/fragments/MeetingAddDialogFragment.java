package com.itime.team.itime.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.itime.team.itime.activities.R;
import com.itime.team.itime.bean.URLs;
import com.itime.team.itime.bean.User;
import com.itime.team.itime.interfaces.DataRequest;
import com.itime.team.itime.utils.JsonManager;
import com.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mac on 15/12/15.
 */
public class MeetingAddDialogFragment extends DialogFragment implements DataRequest{
    private ListView listView;
    private ArrayList<HashMap<String, Object>> listItem;
    private View addDialog;
    private JsonManager mJsonManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addDialog = inflater.inflate(R.layout.fragment_meeting_adddialog,container);
        listView = (ListView) addDialog.findViewById(R.id.meeting_add_listview);
        listItem = new ArrayList<HashMap<String, Object>>();
        mJsonManager = new JsonManager();
        getDialog().setTitle("Search");
        return addDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.search);
        map.put("ItemText", "Search iTIME friends");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.contacts);
        map.put("ItemText", "Add Contacts");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.qrcode);
        map.put("ItemText", "Scan QR code");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.facebook);
        map.put("ItemText", "Add Facebook Friends");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemImage", R.drawable.wechat);
        map.put("ItemText", "Add WeChat Friends");
        listItem.add(map);

        SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItem,
                R.layout.fragment_meeting_adddoalog_listview,
                new String[] {"ItemImage","ItemText"},
                new int[] {R.id.meeting_add_image,R.id.meeting_add_text}
        );
        listView.setAdapter(listItemAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TestClick", String.valueOf(position));
                if(position == 0){

                }else if (position == 1){

                }else if (position == 2){
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 0);
                }else if (position == 3){

                }else if(position == 4){

                } else{

                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            String result = data.getExtras().getString("result");
            String url = URLs.ADD_FRIEND_REQUEST;
            JSONObject object = new JSONObject();
            try {
                object.put("user_id", User.ID);
                object.put("friend_id",result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestJSONObject(mJsonManager,object,url,"adding_friend_request");
            dismiss();
        }

    }

    @Override
    public void handleJSON(JsonManager manager) {
    }

    @Override
    public void requestJSONObject(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonObject(url, jsonObject, getActivity(), tag);
    }

    @Override
    public void requestJSONArray(JsonManager manager,JSONObject jsonObject, String url, String tag) {
        manager.postForJsonArray(url, jsonObject, getActivity(),tag);
    }
}
