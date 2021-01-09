package com.example.reminders;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.github.mikephil.charting.charts.Chart;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class BaseActivity extends AppCompatActivity{

    public final String TAG = "BaseActivity";
    public final String ACTION = "NIGHT_SWITCH";
    protected BroadcastReceiver receiver;
    protected IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //调用设置黑夜模式的function
        setNightMode();

        TypedValue typedValue = new TypedValue();

        this.getTheme().resolveAttribute(R.attr.tvBackground, typedValue, true);

        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, typedValue.resourceId));

        filter = new IntentFilter();
        filter.addAction(ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                needRefresh();
            }
        };

        registerReceiver(receiver, filter);


    }

    //判断是否为黑夜模式
    public boolean isNightMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return sharedPreferences.getBoolean("nightMode", false);
    }

    //设置黑夜模式
    public void setNightMode(){
        if(isNightMode()) this.setTheme(R.style.NightTheme);
        else setTheme(R.style.DayTheme);

    }

    protected abstract void needRefresh();

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public long calStrToSec(String date) throws ParseException {//decode calender date to second
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long secTime = format.parse(date).getTime();
        return secTime;
    }

}

