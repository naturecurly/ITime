package com.itime.team.itime.utils;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by mac on 16/3/3.
 */
public class JPushApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
