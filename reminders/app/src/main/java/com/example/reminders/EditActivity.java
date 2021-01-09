package com.example.reminders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.reminders.database.NoteDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditActivity extends BaseActivity{
    private NoteDatabase dbHelper;
    private Context context = this;

    private EditText et;

    private String old_content = "";
    private String old_time = "";
    private int old_Tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    private boolean tagChange = false;
    //富文本编辑器
    private ImageView formatbold;   //加粗
    private ImageView formatitalic;     //斜体
    private ImageView formatcolortext;      //字体颜色
    private ImageView formatalignleft;      //左对齐
    private ImageView formataligncenter;    //中间对齐
    private ImageView formatalignright;     //右对齐
    private ImageView insertphoto;      //插入图片

    private boolean isBold = false;
    private boolean isitalic = false;
    private boolean iscolortext = false;
    private boolean isalignleft = false;
    private boolean isaligncenter = false;
    private boolean isalignright = false;
    private boolean isinsertphoto = false;
    private int start;//edittext开始时的位置
    private int count;//edittext添加的数量

    final String TAG="oop";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        //自定义导航栏
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //富文本编辑器

        //下拉选择框
        Spinner mySpinner = (Spinner)findViewById(R.id.spinner);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tagList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tag = (int)id + 1;
                tagChange = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(isNightMode()) myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(openMode == 4){
                    if(et.getText().toString().length() == 0){
                        intent.putExtra("mode", -1); //nothing new happens.
                    }
                    else{
                        intent.putExtra("mode", 0); // new one note;
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", dateToStr());
                        intent.putExtra("tag", tag);
                    }
                }
                else {
                    if (et.getText().toString().equals(old_content) && !tagChange)
                        intent.putExtra("mode", -1); // edit nothing
                    else {
                        intent.putExtra("mode", 1); //edit the content
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", dateToStr());
                        intent.putExtra("id", id);
                        intent.putExtra("tag", tag);
                    }
                }
                setResult(RESULT_OK, intent);
                finish();//返回
                overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
            }
        });

        et = (EditText)findViewById(R.id.et);

        Intent getIntent = getIntent();
        //EditText实时监控

        openMode = getIntent.getIntExtra("mode", 0);
        if (openMode == 3) {//打开已存在的note
            id = getIntent.getLongExtra("id", 0);
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_Tag = getIntent.getIntExtra("tag", 1);
            et.setText(old_content);
            et.setSelection(old_content.length());
            mySpinner.setSelection(old_Tag - 1);
        }
//******************************富文本编辑器控件 点击事件************************************************************

//*************************************************END*************************************************************
    }


    @Override
    protected void needRefresh() {
        setNightMode();
        startActivity(new Intent(this, EditActivity.class));
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( keyCode== KeyEvent.KEYCODE_HOME){
            return true;
        } else if( keyCode== KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

        Intent intent = new Intent();
        intent.setClass(EditActivity.this,MainActivity.class);
        startActivity(intent);
          overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                Intent intent = new Intent();
                if(openMode == 4){
                    if(et.getText().toString().length() == 0){
                        intent.putExtra("mode", -1); //nothing new happens.
                    }
                    else{
                        intent.putExtra("mode", 0); // new one note;
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", dateToStr());
                        intent.putExtra("tag", tag);
                    }
                }
                else {
                    if (et.getText().toString().equals(old_content) && !tagChange)
                        intent.putExtra("mode", -1); // edit nothing
                    else {
                        intent.putExtra("mode", 1); //edit the content
                        intent.putExtra("content", et.getText().toString());
                        intent.putExtra("time", dateToStr());
                        intent.putExtra("id", id);
                        intent.putExtra("tag", tag);
                    }
                }
                setResult(RESULT_OK, intent);
                finish();//返回
                overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
                break;
        }
        return super.onOptionsItemSelected(item);

    }


    //输出时间
    public String dateToStr(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }


}
