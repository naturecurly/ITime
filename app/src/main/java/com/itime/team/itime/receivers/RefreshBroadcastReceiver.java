package com.itime.team.itime.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by leveyleonhardt on 5/6/16.
 */
public class RefreshBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "fresh", Toast.LENGTH_SHORT).show();
    }
}
