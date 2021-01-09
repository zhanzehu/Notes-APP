package com.example.reminders.plandatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlanDatabase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "plans";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ID = "_id";
    public static final String TIME = "time";
    public static final String MODE = "mode";

    public static final String TABLE_NAME2 = "fplans";
    public static final String TITLE2 = "ftitle";
    public static final String CONTENT2 = "fcontent";
    public static final String ID2 = "f_id";
    public static final String TIME2 = "ftime";

    public PlanDatabase(Context context){
        super(context, "plans", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT NOT NULL,"
                + CONTENT + " TEXT,"
                + TIME + " TEXT NOT NULL )"


        );

        db.execSQL("CREATE TABLE "+ TABLE_NAME2
                + "("
                + ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE2 + " TEXT NOT NULL,"
                + CONTENT2 + " TEXT,"
                + TIME2 + " TEXT NOT NULL )"


        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
