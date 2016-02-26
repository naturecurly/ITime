package com.itime.team.itime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mac on 16/2/25.
 */
public class DeviceTableHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public DeviceTableHelper(Context context, String name){
        this(context, name, null,VERSION);
    }


    public DeviceTableHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table device_id(id int,device_id varchar(256))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
