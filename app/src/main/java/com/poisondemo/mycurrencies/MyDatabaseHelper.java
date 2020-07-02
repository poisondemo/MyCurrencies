package com.poisondemo.mycurrencies;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;


public class MyDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "currenciesdb.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //建表
        db.execSQL("CREATE TABLE IF NOT EXISTS record" +
                   "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                   "forCode TEXT, " +
                 "forAmount TEXT," +
                "homCode TEXT," +
                "homAmount TEXT," +
                "time TEXT)");
    }

    //数据库更新
    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        db.execSQL("drop table if exists record");
    }

}
