package com.itime.team.itime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Weiwei Cai on 16/2/25.
 */
public class UserTableHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public UserTableHelper(Context context, String name){
        this(context, name, null,VERSION);
    }


    public UserTableHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table itime_user(id int,user_id int,password varchar(128),last_login_time date,remember boolean)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
