package com.example.reminders.plandatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CRUD {
    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;
    final String TAG="CRUD";

    private static final String[] columns = {
            PlanDatabase.ID,
            PlanDatabase.TITLE,
            PlanDatabase.CONTENT,
            PlanDatabase.TIME,
    };

    private static final String[] columns2 = {
            PlanDatabase.ID2,
            PlanDatabase.TITLE2,
            PlanDatabase.CONTENT2,
            PlanDatabase.TIME2,
    };

    public CRUD(Context context){
        dbHandler = new PlanDatabase(context);
    }

    public void open(){
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        dbHandler.close();
    }

    public Plan addPlan(Plan plan){
        //add a plan object to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanDatabase.TITLE, plan.getTitle());
        contentValues.put(PlanDatabase.CONTENT, plan.getContent());
        contentValues.put(PlanDatabase.TIME, plan.getTime());
        long insertId = db.insert(PlanDatabase.TABLE_NAME, null, contentValues);
        plan.setId(insertId);
        return plan;
    }

    public Plan getPlan(long id){
        //get a plan from database using cursor index
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME,columns,PlanDatabase.ID + "=?",
                new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Plan e = new Plan(cursor.getString(1),cursor.getString(2), cursor.getString(3));
        return e;
    }

    public List<Plan> getAllPlans(){
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME,columns,null,null,null, null, null);

        List<Plan> plans = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Plan plan = new Plan();
                plan.setId(cursor.getLong(cursor.getColumnIndex(PlanDatabase.ID)));
                plan.setTitle(cursor.getString(cursor.getColumnIndex(PlanDatabase.TITLE)));
                plan.setContent(cursor.getString(cursor.getColumnIndex(PlanDatabase.CONTENT)));
                plan.setTime(cursor.getString(cursor.getColumnIndex(PlanDatabase.TIME)));
                plans.add(plan);
            }
        }
        return plans;
    }

    public int updatePlan(Plan plan) {
        //update the info of an existing plan
        ContentValues values = new ContentValues();
        values.put(PlanDatabase.TITLE, plan.getTitle());
        values.put(PlanDatabase.CONTENT, plan.getContent());
        values.put(PlanDatabase.TIME, plan.getTime());
        // updating row
        return db.update(PlanDatabase.TABLE_NAME, values,
                PlanDatabase.ID + "=?",new String[] { String.valueOf(plan.getId())});
    }

    public void removePlan(Plan plan) {
        //remove a plan according to ID value
        db.delete(PlanDatabase.TABLE_NAME, PlanDatabase.ID + "=" + plan.getId(), null);
    }



    //*********************************************完成——表的CRUD***********************************************************
    public Plan addPlan_finish(Plan plan){
        //add a plan object to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlanDatabase.TITLE2, plan.getTitle());
        contentValues.put(PlanDatabase.CONTENT2, plan.getContent());
        contentValues.put(PlanDatabase.TIME2, plan.getTime());
        long insertId = db.insert(PlanDatabase.TABLE_NAME2, null, contentValues);
        plan.setId(insertId);
        return plan;
    }

    public Plan getPlan_finish(long id){
        //get a plan from database using cursor index
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME2,columns2,PlanDatabase.ID2 + "=?",
                new String[]{String.valueOf(id)},null,null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Plan e = new Plan(cursor.getString(1),cursor.getString(2), cursor.getString(3));
        return e;
    }

    public List<Plan> getAllPlans_finish(){
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME2,columns2,null,null,null, null, null);

        List<Plan> plans = new ArrayList<>();
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                Plan plan = new Plan();
                plan.setId(cursor.getLong(cursor.getColumnIndex(PlanDatabase.ID2)));
                plan.setTitle(cursor.getString(cursor.getColumnIndex(PlanDatabase.TITLE2)));
                plan.setContent(cursor.getString(cursor.getColumnIndex(PlanDatabase.CONTENT2)));
                plan.setTime(cursor.getString(cursor.getColumnIndex(PlanDatabase.TIME2)));
                plans.add(plan);
            }
        }
        return plans;
    }

    public int updatePlan_finish(Plan plan) {
        //update the info of an existing plan
        ContentValues values = new ContentValues();
        values.put(PlanDatabase.TITLE2, plan.getTitle());
        values.put(PlanDatabase.CONTENT2, plan.getContent());
        values.put(PlanDatabase.TIME2, plan.getTime());
        // updating row
        return db.update(PlanDatabase.TABLE_NAME2, values,
                PlanDatabase.ID2 + "=?",new String[] { String.valueOf(plan.getId())});
    }

    public void removePlan_finish(Plan plan) {
        //remove a plan according to ID value
        db.delete(PlanDatabase.TABLE_NAME2, PlanDatabase.ID2 + "=" + plan.getId(), null);
    }

    //获取表数据的数量
    public int get_num2(){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String secTime = simpleDateFormat.format(date);

        Cursor cursor = db.query(PlanDatabase.TABLE_NAME2,columns2,"ftime like?",new String[] {"%"+secTime+"%"},null, null, null);
       // Log.d(TAG,"ggg"+simpleDateFormat);
        if(cursor.getCount()==0){
            return 0;
        }else{
            return cursor.getCount();
        }
        //return 0;
    }

    //获取表数据的数量
    public int get_num(){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String secTime = simpleDateFormat.format(date);
        Cursor cursor = db.query(PlanDatabase.TABLE_NAME,columns,"time like?",new String[] {"%"+secTime+"%"},null, null, null);

       if(cursor.getCount()==0){
           return 0;
       }else{
           return cursor.getCount();
       }
    }

}
