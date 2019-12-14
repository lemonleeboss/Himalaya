package com.example.a76780.himalaya.DataBaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper extends SQLiteOpenHelper {
    public static final String DB_NAME="mydb";
    public static final int DB_VERSION=1;
    public static final String TB_NAME="users";
    public static final String COL_USERNAME="username";
    public static final String COL_PWD="pwd";
    public static final String SQL="CREATE TABLE "+TB_NAME+"("+
            COL_USERNAME+" text,"+
            COL_PWD+" text)";
    public Databasehelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
