package com.itime.team.itime.utils;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by mac on 16/2/5.
 */
public class DebugMemoryLease extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
