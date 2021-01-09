package com.example.reminders.plandatabase;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminders.BaseActivity;
import com.example.reminders.MainActivity;
import com.example.reminders.R;
import com.example.reminders.loading;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import static android.app.Notification.BADGE_ICON_SMALL;

public class EditAlarmActivity extends BaseActivity implements View.OnClickListener {

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private EditText et_title;
    private EditText et;
    private Button set_date;
    private Button set_time;


    private TextView date;
    private TextView time;
    private Plan plan;
    private int[] dateArray = new int[3];
    private int[] timeArray = new int[2];

    private int openMode = 0;
    private String old_title = "";
    private String old_content = "";
    private String old_time = "";
    private long id = 0;
    private boolean timeChange = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_alarm_layout);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        final Intent intent = getIntent();
        openMode = intent.getExtras().getInt("mode", 0);
        if (openMode == 1) {
            id = intent.getLongExtra("id", 0);
            old_title = intent.getStringExtra("title");
            old_content = intent.getStringExtra("content");
            old_time = intent.getStringExtra("time");
            et_title.setText(old_title);
            et_title.setSelection(old_title.length());
            et.setText(old_content);
            et.setSelection(old_content.length());

            String[] wholeTime = old_time.split(" ");
            String[] temp = wholeTime[0].split("-");
            String[] temp1 = wholeTime[1].split(":");
            setDateTV(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
            setTimeTV(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1]));
        }

        if (isNightMode())
            myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canBeSet()) {
                    Toast.makeText(EditAlarmActivity.this, "不合逻辑的提醒时间", Toast.LENGTH_SHORT).show();
                } else if (et.getText().toString().length() + et_title.getText().toString().length() == 0 && openMode == 2) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("mode", -1);//nothing new happens.
                    setResult(RESULT_OK, intent1);
                    finish();//返回
                    overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
                } else if (et_title.getText().toString().length() == 0) {
                    Toast.makeText(EditAlarmActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    isTimeChange();
                    Intent intent = new Intent();
                    if (openMode == 2) {
                        intent.putExtra("mode", 10); // new one plan;
                        intent.putExtra("title", et_title.getText().toString());
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", date.getText().toString() + " " + time.getText().toString());
                        Log.d(TAG, date.getText().toString() + time.getText().toString());
                    } else {
                        if (et.getText().toString().equals(old_content) && et_title.getText().toString().equals(old_title) && !timeChange) {
                            intent.putExtra("mode", -1); // edit nothing
                        } else {
                            intent.putExtra("mode", 11); //edit the content
                            intent.putExtra("title", et_title.getText().toString());
                            intent.putExtra("content", et.getText().toString());
                            intent.putExtra("time", date.getText().toString() + " " + time.getText().toString());
                            intent.putExtra("id", id);
                        }
                    }
                    setResult(RESULT_OK, intent);
                    finish();//返回
                    overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
                }
            }
        });

    }

    private void setTimeTV(int h, int m) {
        //update tv and timeArra
        String temp = "";
        if (h < 10) temp += "0";
        temp += (h + ":");
        if (m < 10) temp += "0";
        temp += m;
        time.setText(temp);
        timeArray[0] = h;
        timeArray[1] = m;
    }

    private void setDateTV(int y, int m, int d) {
        //update tv and dateArray
        String temp = y + "-";
        if (m < 10) temp += "0";
        temp += (m + "-");
        if (d < 10) temp += "0";
        temp += d;
        date.setText(temp);
        dateArray[0] = y;
        dateArray[1] = m;
        dateArray[2] = d;
    }

    private void init() {
        plan = new Plan();
        dateArray[0] = plan.getYear();
        dateArray[1] = plan.getMonth() + 1;
        dateArray[2] = plan.getDay();
        timeArray[0] = plan.getHour();
        timeArray[1] = plan.getMinute();

        et_title = findViewById(R.id.et_title);
        et = findViewById(R.id.et);
        set_date = findViewById(R.id.set_date);
        set_time = findViewById(R.id.set_time);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);

        //initialize two textviews
        setDateTV(dateArray[0], dateArray[1], dateArray[2]);
        setTimeTV((timeArray[1] > 54 ? timeArray[0] + 1 : timeArray[0]), (timeArray[1] + 5) % 60);
        Log.d(TAG, "init: " + dateArray[1]);

        set_date.setOnClickListener(this);
        set_time.setOnClickListener(this);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                setDateTV(year, month + 1, dayOfMonth);

            }
        };
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTimeTV(hourOfDay, minute);
            }
        };
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent();
            intent.setClass(EditAlarmActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);

            //  overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);

        }
        return super.onKeyDown(keyCode, event);
    }

    private void isTimeChange() {
        String newTime = date.getText().toString() + " " + time.getText().toString();
        if (!newTime.equals(old_time)) timeChange = true;
    }

    private boolean canBeSet() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateArray[0], dateArray[1] - 1, dateArray[2], timeArray[0], timeArray[1]);
        Calendar cur = Calendar.getInstance();
        Log.d(TAG, "canBeSet: " + cur.getTime().toString() + calendar.getTime().toString());
        if (cur.before(calendar)) return true;
        else {
            Toast.makeText(this, "不合逻辑的提醒时间", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void needRefresh() {
        setNightMode();
        startActivity(new Intent(this, EditAlarmActivity.class));
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_date: //choose day
                DatePickerDialog dialog = new DatePickerDialog(EditAlarmActivity.this,
                        (isNightMode() ? R.style.NightDialogTheme : R.style.DayDialogTheme), dateSetListener,
                        dateArray[0], dateArray[1] - 1, dateArray[2]);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable((isNightMode()?Color.BLACK : Color.WHITE)));
                dialog.show();
                break;
            case R.id.set_time://choose hour and minute
                TimePickerDialog dialog1 = new TimePickerDialog(EditAlarmActivity.this,
                        (isNightMode() ? R.style.NightDialogTheme : R.style.DayDialogTheme), timeSetListener,
                        timeArray[0], timeArray[1], true);
                //dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                if (!canBeSet()) {
                    Toast.makeText(EditAlarmActivity.this, "不合逻辑的提醒时间", Toast.LENGTH_SHORT).show();
                } else if (et.getText().toString().length() + et_title.getText().toString().length() == 0 && openMode == 2) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("mode", -1);//nothing new happens.
                    setResult(RESULT_OK, intent1);
                    finish();//返回
                    overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
                } else if (et_title.getText().toString().length() == 0) {
                    Toast.makeText(EditAlarmActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    isTimeChange();
                    Intent intent = new Intent();
                    if (openMode == 2) {
                        intent.putExtra("mode", 10); // new one plan;
                        intent.putExtra("title", et_title.getText().toString());
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", date.getText().toString() + " " + time.getText().toString());
                        intent.putExtra("check", 1);
                        Log.d(TAG, date.getText().toString() + time.getText().toString());
                    } else {
                        if (et.getText().toString().equals(old_content) && et_title.getText().toString().equals(old_title) && !timeChange) {
                            intent.putExtra("mode", -1); // edit nothing
                        } else {
                            intent.putExtra("mode", 11); //edit the content
                            intent.putExtra("title", et_title.getText().toString());
                            intent.putExtra("content", et.getText().toString());
                            intent.putExtra("time", date.getText().toString() + " " + time.getText().toString());
                            intent.putExtra("id", id);
                        }
                    }

                        setResult(RESULT_OK, intent);
                        finish();//返回

                    //notification();
                        //      startActivity(new Intent(EditAlarmActivity.this,MainActivity.class));
                    }
                    break;
                }

                return super.onOptionsItemSelected(item);
        }





}
