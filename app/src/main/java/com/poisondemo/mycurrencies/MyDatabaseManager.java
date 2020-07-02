package com.poisondemo.mycurrencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MyDatabaseManager {
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;
    public MyDatabaseManager(Context context){
        helper = new MyDatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    public void addData(BeanRecord data){

        addContent(data);
    }

    private void addContent(BeanRecord data){
        ContentValues values = new ContentValues();
        values.put("homCode",data.getHomCode());
        values.put("homAmount",data.getHomAmount());
        values.put("forCode",data.getForCode());
        values.put("forAmount",data.getForAmount());
        values.put("time",data.getTime());
        db.insert("record",null,values);
    }
    public ArrayList<BeanRecord> queryAllContent(){
        ArrayList<BeanRecord> datas = new ArrayList<>();
        Cursor c = db.query("record",null,null,null,null,null,null);
        while(c.moveToNext()){

            BeanRecord data = null;

            String forCode = c.getString(c.getColumnIndex("forCode"));
            String forAmount = c.getString(c.getColumnIndex("forAmount"));
            String homCode = c.getString(c.getColumnIndex("homCode"));
            String homAmount = c.getString(c.getColumnIndex("homAmount"));
            String time = c.getString(c.getColumnIndex("time"));
            data = new BeanRecord(forCode,forAmount,homCode,homAmount,time);
            datas.add(data);
        }

        c.close();
        return datas;
    }
}
