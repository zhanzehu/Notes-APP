package com.example.reminders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminders.database.CRUD;
import com.example.reminders.database.Note;
import com.example.reminders.database.NoteAdapter;
import com.example.reminders.database.NoteDatabase;
import com.example.reminders.database.ScrollListviewDelete;
import com.example.reminders.plandatabase.AlarmReceiver;
import com.example.reminders.plandatabase.EditAlarmActivity;
import com.example.reminders.plandatabase.Plan;
import com.example.reminders.plandatabase.PlanAdapter;
import com.example.reminders.plandatabase.PlanDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Notification.BADGE_ICON_SMALL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static android.view.View.GONE;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    final String TAG="oops";

    private FloatingActionButton fab;
    private FloatingActionButton fab_alarm;
    private ScrollListviewDelete lv;
    private com.example.reminders.plandatabase.ScrollListviewDelete lv_plan;
    private LinearLayout lv_layout;
    private LinearLayout lv_plan_layout;
    private LinearLayout calendar;

    private Context context = this;
    private NoteAdapter adapter;
    private PlanAdapter planAdapter;
    private List<Note> noteList = new ArrayList<Note>();
    private List<Plan> planList = new ArrayList<Plan>();
    private TextView mEmptyView;
    private int checkNum; // 记录选中的条目数量
    private TextView tv_show;// 用于显示选中的条目数量
    private int id;//消息编号，用来存储故障标号，从上一个界面传来
    private String defaul="";//默认的全部不勾选
    private String selectall="";//全部勾选
    private ArrayList<String> list;


    private Toolbar myToolbar;

    private PopupWindow popupWindow; // 左侧弹出菜单
    private PopupWindow popupCover; // 菜单蒙版
    private LayoutInflater layoutInflater;
    private RelativeLayout main;
    private ViewGroup customView;
    private ViewGroup coverView;
    private WindowManager wm;
    private DisplayMetrics metrics;
    private TagAdapter tagAdapter;

    private ListView lv_tag;
    private TextView add_tag;

    private SharedPreferences sharedPreferences;
    private RadioButton note_tab;
    private RadioButton plan_tab;

    private AlarmManager alarmManager;
    //日历点击事件
    private CalendarView calendarView;

    private NotificationManager mNotificationManager;

    private String groupId = "groupId";
    private CharSequence groupName = "Group1";

    private String groupId2 = "groupId2";
    private CharSequence groupName2 = "Group2";
    private String chatChannelId2 = "chatChannelId2";
    private String adChannelId2 = "adChannelId2";

    private String chatChannelId = "chatChannelId";
    private String chatChannelName = "聊天通知";
    private String chatChannelDesc = "这是一个聊天通知，建议您置于开启状态，这样才不会漏掉女朋友的消息哦";
    private int chatChannelImportance = NotificationManager.IMPORTANCE_MAX;

    private String adChannelId = "adChannelId";
    private String adChannelName = "广告通知";
    private String adChannelDesc = "这是一个广告通知，可以关闭的，但是如果您希望我们做出更好的软件服务于你，请打开广告支持一下吧";
    private int adChannelImportance = NotificationManager.IMPORTANCE_LOW;
    private String fnum;
    private String num;
    private String p_num;

    int a,b,c1,c2,c3;


    String[] list_String = {"before one month", "before three months", "before six months", "before one year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // list = new ArrayList<String>();

       // createGroup();
     //   notification();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        initView();

        boolean temp=sharedPreferences.getBoolean("note_tab",false);
        boolean temp2=sharedPreferences.getBoolean("plan_tab",false);

        if (temp){
            if (super.isNightMode())
                myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_assessment_white_24dp));
            else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_assessment_black_24dp)); // 图表


        }else if(temp2){
            if (super.isNightMode())
                myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_menu_white_24dp));
            else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_menu_black_24dp)); // 三道杠
        }

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean temp=sharedPreferences.getBoolean("note_tab",false);
                boolean temp2=sharedPreferences.getBoolean("plan_tab",false);
                if(temp) {//计划页面
                    startActivity(new Intent(MainActivity.this,chart.class));
                }else if(temp2){//编辑页面
                    showPopUpWindow();
                }
            }
        });

    }

    private void showPopUpWindow() {
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        //菜单蒙版
        popupCover = new PopupWindow(coverView, width, height, false);
        //左侧弹出菜单
        popupWindow = new PopupWindow(customView, (int) (width * 0.7), (height), true);

        if (isNightMode()) popupWindow.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupCover.setAnimationStyle(R.style.AnimationCover);


        //display the popup window
        findViewById(R.id.main_layout).post(new Runnable() {//等待main_layout加载完，再show popupwindow
            @Override
            public void run() {
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);

                //setting_text = customView.findViewById(R.id.setting_settings_text);
               // setting_image = customView.findViewById(R.id.setting_settings_image);
                lv_tag = customView.findViewById(R.id.lv_tag);
                add_tag = customView.findViewById(R.id.add_tag);

                add_tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sharedPreferences.getString("tagListString","").split("_").length < 8) {
                            final EditText et = new EditText(context);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("请定义新的标签")
                                    .setView(et)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags

                                            String name = et.getText().toString();
                                            if (!tagList.contains(name)) {
                                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                                String oldTagListString = sharedPreferences.getString("tagListString", null);
                                                String newTagListString = oldTagListString + "_" + name;
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("tagListString", newTagListString);
                                                editor.commit();
                                                refreshTagList();
                                            }
                                            else Toast.makeText(context, "重复标记!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                        }
                        else{
                            Toast.makeText(context, "自定义的标签够多了！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
                tagAdapter = new TagAdapter(context, tagList, numOfTagNotes(tagList));
                lv_tag.setAdapter(tagAdapter);

                lv_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
                        int tag = position+1;
                        List<Note> temp = new ArrayList<>();
                        for (int i = 0; i < noteList.size(); i++) {
                            if (noteList.get(i).getTag() == tag) {
                                Note note = noteList.get(i);
                                temp.add(note);
                            }
                        }

                        NoteAdapter tempAdapter = new NoteAdapter(context, temp);
                        lv.setAdapter(tempAdapter);
                        myToolbar.setTitle(tagList.get(position));
                        popupWindow.dismiss();
                        Log.d(TAG, position + "");
                    }
                });

                lv_tag.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (position > 4) {
                            resetTagsX(parent);
                            float length = getResources().getDimensionPixelSize(R.dimen.distance);
                            TextView blank = view.findViewById(R.id.blank_tag);
                            blank.animate().translationX(length).setDuration(300).start();
                            TextView text = view.findViewById(R.id.text_tag);
                            text.animate().translationX(length).setDuration(300).start();
                            ImageView del = view.findViewById(R.id.delete_tag);
                            del.setVisibility(View.VISIBLE);
                            del.animate().translationX(length).setDuration(300).start();

                            del.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("此标签下的所有笔记将会归类于 \"未定义\" !")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int tag = position + 1;
                                                    for (int i = 0; i < noteList.size(); i++) {
                                                        //被删除tag的对应notes tag = 1
                                                        Note temp = noteList.get(i);
                                                        if (temp.getTag() == tag) {
                                                            temp.setTag(1);
                                                            CRUD op = new CRUD(context);
                                                            op.open();
                                                            op.updateNote(temp);
                                                            op.close();
                                                        }
                                                    }
                                                    List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
                                                    if(tag + 1 < tagList.size()) {
                                                        for (int j = tag + 1; j < tagList.size() + 1; j++) {
                                                            //大于被删除的tag的所有tag减一
                                                            for (int i = 0; i < noteList.size(); i++) {
                                                                Note temp = noteList.get(i);
                                                                if (temp.getTag() == j) {
                                                                    temp.setTag(j - 1);
                                                                    CRUD op = new CRUD(context);
                                                                    op.open();
                                                                    op.updateNote(temp);
                                                                    op.close();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    //edit the preference
                                                    List<String> newTagList = new ArrayList<>();
                                                    newTagList.addAll(tagList);
                                                    newTagList.remove(position);
                                                    String newTagListString = TextUtils.join("_", newTagList);
                                                    Log.d(TAG, "onClick: " + newTagListString);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("tagListString", newTagListString);
                                                    editor.commit();

                                                    refreshTagList();
                                                }
                                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                                }
                            });

                            return true;
                        }
                        return false;
                    }
                });

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });
            }
        });

    }

    private void refreshTagList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
        tagAdapter = new TagAdapter(context, tagList, numOfTagNotes(tagList));
        lv_tag.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();
    }

    private void resetTagsX(AdapterView<?> parent) {
        for (int i = 5; i < parent.getCount(); i++) {
            View view = parent.getChildAt(i);
            if (view.findViewById(R.id.delete_tag).getVisibility() == View.VISIBLE) {
                float length = 0;
                TextView blank = view.findViewById(R.id.blank_tag);
                blank.animate().translationX(length).setDuration(300).start();
                TextView text = view.findViewById(R.id.text_tag);
                text.animate().translationX(length).setDuration(300).start();
                ImageView del = view.findViewById(R.id.delete_tag);
                del.setVisibility(GONE);
                del.animate().translationX(length).setDuration(300).start();
            }
        }
    }

    @Override
    protected void needRefresh() {
        setNightMode();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("opMode", 10);
        startActivity(intent);
        overridePendingTransition(R.anim.night_switch, R.anim.night_switch_over);
        if (popupWindow.isShowing()) popupWindow.dismiss();
        finish();
    }

    public void initView() {

        initPrefs();

        fab = findViewById(R.id.fab);
        fab_alarm = findViewById(R.id.fab_alarm);
        lv = findViewById(R.id.lv);//笔记listview
        lv_plan = findViewById(R.id.lv_plan);//计划listview
        lv_layout = findViewById(R.id.lv_layout);
        lv_plan_layout = findViewById(R.id.lv_plan_layout);
        //lv_item_layout = findViewById(R.id.lv_item_layout);
        //日历页面
        calendar = findViewById(R.id.calendar);
        //日历控件
        calendarView = findViewById(R.id.calendarView);
        //content_switch = findViewById(R.id.content_switch);
        myToolbar = findViewById(R.id.my_toolbar);
        //下部导航栏按钮
        note_tab=findViewById(R.id.note_tab);
        plan_tab=findViewById(R.id.plan_tab);

        //滑动删除
        ImageView btnDelete=findViewById(R.id.btnDelete);
        //ImageView btnNao=findViewById(R.id.btnNao);
        ImageView btnDelete_plan=findViewById(R.id.btnDelete_plan);
        //ImageView btnNao_plan=findViewById(R.id.btnNao_plan);
        //Checkbox
        CheckBox accomplish=findViewById(R.id.accomplish);


        refreshLvVisibility();

        mEmptyView = findViewById(R.id.emptyView); // search page

        adapter = new NoteAdapter(getApplicationContext(), noteList);
        planAdapter = new PlanAdapter(getApplicationContext(), planList);

        refreshListView();
        lv.setAdapter(adapter);
        //lv.setEmptyView(mEmptyView); // connect empty textview with listview
        lv_plan.setAdapter(planAdapter);

       // boolean temp = sharedPreferences.getBoolean("content_switch", false);
        boolean temp=sharedPreferences.getBoolean("note_tab",false);
        boolean temp2=sharedPreferences.getBoolean("plan_tab",false);
         /**
        content_switch.setChecked(temp);//判断是看note还是plan
        content_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("content_switch" ,isChecked);
                editor.commit();
                refreshLvVisibility();
            }
        });
**/
         //******************************************lv*********************************************
         lv.setOnItemClickListener(new ScrollListviewDelete.ItemClickListener() {
             @Override
             public void onItemClick(int position) {
                 Intent intent = new Intent(MainActivity.this, EditActivity.class);
                 intent.putExtra("content", noteList.get(position).getContent());
                 intent.putExtra("id", noteList.get(position).getId());
                 intent.putExtra("time", noteList.get(position).getTime());
                 intent.putExtra("mode", 3);     // MODE of 'click to edit'
                 intent.putExtra("tag", noteList.get(position).getTag());
                 startActivityForResult(intent, 1);      //collect data from edit
                 overridePendingTransition(R.anim.in_righttoleft, R.anim.out_righttoleft);
             }
         });

         lv_plan.setOnItemClickListener(new com.example.reminders.plandatabase.ScrollListviewDelete.ItemClickListener() {
             @Override
             public void onItemClick(int position) {
                 Intent intent1 = new Intent(MainActivity.this, EditAlarmActivity.class);
                 intent1.putExtra("title", planList.get(position).getTitle());
                 intent1.putExtra("content", planList.get(position).getContent());
                 intent1.putExtra("time", planList.get(position).getTime());
                 intent1.putExtra("mode", 1);
                 intent1.putExtra("id", planList.get(position).getId());
                 startActivityForResult(intent1, 1);
             }
         });

         //************************************点击日历天数输出计划********************************

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                StringBuffer sBuffer= new StringBuffer();
                sBuffer.append(year);
                String mon=String.format("%02d",month+1); //转换精度 int转String 不足两位数自动补0
                sBuffer.append(mon);
                String day=String.format("%02d",dayOfMonth);//转换精度 int转String 不足两位数自动补0
                sBuffer.append(day);
                planAdapter.getFilter2().filter(sBuffer);
                myToolbar.setTitle("计划");
                Log.d(TAG, "onSelectedDayChange: "+sBuffer);

                //Toast.makeText(MainActivity.this, "您的生日是"+year+"年"+month+"月"+dayOfMonth+"日", Toast.LENGTH_LONG).show();
            }
        });

        //***************************************************************************************
        //底部导航栏测试
        note_tab.setChecked(temp);
        note_tab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("note_tab",isChecked);
                editor.commit();
                refreshLvVisibility();
            }
        });

        plan_tab.setChecked(temp2);
        plan_tab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("plan_tab",isChecked);
                editor.commit();
                refreshLvVisibility();
            }
        });
        //***************************************************************************************
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("mode", 4);     // MODE of 'new note'
                startActivityForResult(intent, 1);      //collect data from edit
                overridePendingTransition(R.anim.in_righttoleft, R.anim.out_righttoleft);

            }
        });

        fab_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditAlarmActivity.class);
                intent.putExtra("mode", 2); // MODE of 'new plan'
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.in_righttoleft, R.anim.no);
            }
        });

       // lv.setOnItemClickListener(this);
        //lv_plan.setOnItemClickListener(this);

        //lv.setOnItemLongClickListener(this);
        //lv_plan.setOnItemLongClickListener(this);


        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar
        initPopupView();


    }


    @SuppressLint("RestrictedApi")
    private void refreshLvVisibility() {
        //决定应该是notes还是plans
        boolean temp=sharedPreferences.getBoolean("note_tab",false);
        boolean temp2=sharedPreferences.getBoolean("plan_tab",false);

        if(temp){
            lv_layout.setVisibility(GONE);
            fab.setVisibility(GONE);
            lv_plan_layout.setVisibility(View.VISIBLE);
            fab_alarm.setVisibility(View.VISIBLE);
            calendar.setVisibility(View.VISIBLE);
            if (isNightMode())
                myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_assessment_white_24dp));
            else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_assessment_black_24dp)); // 图表

        }
        else if(temp2){
            lv_layout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            lv_plan_layout.setVisibility(GONE);
            fab_alarm.setVisibility(GONE);
            calendar.setVisibility(GONE);

            if (super.isNightMode())
                myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_menu_white_24dp));
            else myToolbar.setNavigationIcon(getDrawable(R.drawable.ic_menu_black_24dp)); // 三道杠

        }

        if(temp) myToolbar.setTitle("计划");
        else if(temp2) myToolbar.setTitle("笔记");
    }

    public void initPopupView() {
        //instantiate the popup.xml layout file
        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = (ViewGroup) layoutInflater.inflate(R.layout.setting_layout, null);
        coverView = (ViewGroup) layoutInflater.inflate(R.layout.setting_cover, null);

        main = findViewById(R.id.main_layout);
        //instantiate popup window
        wm = getWindowManager();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

    }

    private void initPrefs() {
        //initialize all useful SharedPreferences for the first time the app runs

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("nightMode")) {
            editor.putBoolean("nightMode", false);
            editor.commit();
        }
        if (!sharedPreferences.contains("reverseSort")) {
            editor.putBoolean("reverseSort", true);
            editor.commit();
        }
        if (!sharedPreferences.contains("tagListString")) {
            String s = "未定义_工作_学习_生活_数据";
            editor.putString("tagListString", s);
            editor.commit();
        }
        if(!sharedPreferences.contains("note_tab")) {
            editor.putBoolean("note_tab", false);
            editor.commit();
        }
        if(!sharedPreferences.contains("plan_tab")) {
            editor.putBoolean("plan_tab", true);
            editor.commit();
        }
        if(!sharedPreferences.contains("noteTitle")){
            editor.putBoolean("noteTitle", true);
            editor.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //search setting
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setQueryHint("搜索");

        //搜索图标按钮的点击事件
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(note_tab.isChecked()) {
                    calendar.setVisibility(GONE);
                }
            }
        });
        //搜索框内容变化监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(note_tab.isChecked()) {
                    planAdapter.getFilter().filter(newText);
                }else{ adapter.getFilter().filter(newText);}
                return false;
            }
        });
//搜索框展开时点击叉叉按钮关闭搜索框的点击事件
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(note_tab.isChecked()) {
                    calendar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

//public boolean refresh_is_true=false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:

                boolean temp=sharedPreferences.getBoolean("note_tab",false);
                boolean temp2=sharedPreferences.getBoolean("plan_tab",false);
                if(temp){
                  myToolbar.setTitle("所有计划");
                //    lv_plan.setAdapter(adapter);
                    startActivity(new Intent(MainActivity.this,MainActivity.class));

                }else if(temp2){

                    myToolbar.setTitle("所有笔记");
                    lv.setAdapter(adapter);
                }
                break;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                overridePendingTransition(R.anim.in_lefttoright, R.anim.out_lefttoright);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //刷新listview 显示所有笔记
    public void refreshListView() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //initialize CRUD
        CRUD op = new CRUD(context);
        op.open();
        // set adapter
        if (noteList.size() > 0) noteList.clear();
        noteList.addAll(op.getAllNotes());
        if (sharedPreferences.getBoolean("reverseSort", false)) sortNotes(noteList, 2);
        else sortNotes(noteList, 1);
        op.close();
        adapter.notifyDataSetChanged();

        com.example.reminders.plandatabase.CRUD op1 = new com.example.reminders.plandatabase.CRUD(context);
        op1.open();
        if(planList.size() > 0) {
            cancelAlarms(planList);//删除所有闹钟
            planList.clear();
        }
        planList.addAll(op1.getAllPlans());
        startAlarms(planList);//添加所有新闹钟
        if (sharedPreferences.getBoolean("reverseSort", false)) sortPlans(planList, 2);
        else sortPlans(planList, 1);
        op1.close();
        planAdapter.notifyDataSetChanged();

        //achievement.listen();

    }

    //click item in listView 点击listview进入编辑页面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_item_layout:
                Note curNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3);     // MODE of 'click to edit'
                intent.putExtra("tag", curNote.getTag());
                startActivityForResult(intent, 1);      //collect data from edit
                overridePendingTransition(R.anim.in_righttoleft, R.anim.out_righttoleft);
                break;
            case R.id.lv_plan:
                Plan curPlan = (Plan) parent.getItemAtPosition(position);
                Intent intent1 = new Intent(MainActivity.this, EditAlarmActivity.class);
                intent1.putExtra("title", curPlan.getTitle());
                intent1.putExtra("content", curPlan.getContent());
                intent1.putExtra("time", curPlan.getTime());
                intent1.putExtra("mode", 1);
                intent1.putExtra("id", curPlan.getId());
                startActivityForResult(intent1, 1);
                break;
        }
    }

    // react to startActivityForResult and collect data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        int returnMode;
        long note_Id;
        returnMode = data.getExtras().getInt("mode", -1);
        note_Id = data.getExtras().getLong("id", 0);
        if (returnMode == 1) {  //update current note

            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            newNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.updateNote(newNote);
            // achievement.editNote(op.getNote(note_Id).getContent(), content);
            op.close();

        } else if (returnMode == 2) {  //delete current note
            Note curNote = new Note();
            curNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.removeNote(curNote);
            op.close();
            // achievement.deleteNote();
        } else if (returnMode == 0) {  // create new note
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);
            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(context);
            op.open();
            op.addNote(newNote);
            op.close();
            //achievement.addNote(content);
        } else if (returnMode == 11) {//edit plan
            String title = data.getExtras().getString("title", null);
            String content = data.getExtras().getString("content", null);
            String time = data.getExtras().getString("time", null);
            Log.d(TAG, time);
            Plan plan = new Plan(title, content, time);
            plan.setId(note_Id);
            com.example.reminders.plandatabase.CRUD op = new com.example.reminders.plandatabase.CRUD(context);
            op.open();
            op.updatePlan(plan);
            a=op.get_num();//未完成计划个数
            b=op.get_num2();//已完成计划个数
            op.close();
            if(a==0&&b==0){ a=1;c1=0; }
            num=String.valueOf(a); //int转换成String
            fnum=String.valueOf(b); //int转换成String

            p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
            if(c1==0){num=String.valueOf(c1);}
            createGroup();
            notification();
            /**  com.example.reminders.plandatabase.CRUD op=new com.example.reminders.plandatabase.CRUD(context);
             op.open();
             a=op.get_num();//未完成计划个数
             b=op.get_num2();//已完成计划个数
             op.close();

             if(a==0&&b==0){ a=1; }
             num=String.valueOf(a); //int转换成String
             fnum=String.valueOf(b); //int转换成String

             p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));**/

        } else if (returnMode == 12) {//delete existing plan
            Plan plan = new Plan();
            plan.setId(note_Id);
            com.example.reminders.plandatabase.CRUD op = new com.example.reminders.plandatabase.CRUD(context);
            op.open();
            op.removePlan(plan);
            a=op.get_num();//未完成计划个数
            b=op.get_num2();//已完成计划个数
            op.close();
            if(a==0&&b==0){ a=1; c2=0;}
            num=String.valueOf(a); //int转换成String
            fnum=String.valueOf(b); //int转换成String
            p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
            if(c2==0){num=String.valueOf(c2);}
            createGroup();
            notification();
        } else if (returnMode == 10) {//create new plan
            String title = data.getExtras().getString("title", null);
            String content = data.getExtras().getString("content", null);
            String time = data.getExtras().getString("time", null);
            Plan newPlan = new Plan(title, content, time);
            com.example.reminders.plandatabase.CRUD op = new com.example.reminders.plandatabase.CRUD(context);
            op.open();
            op.addPlan(newPlan);
            a=op.get_num();//未完成计划个数
            b=op.get_num2();//已完成计划个数
            Log.d(TAG, "onActivityResult: " + time);
            op.close();
            if(a==0&&b==0){ a=1;c3=0; }
            num=String.valueOf(a); //int转换成String
            fnum=String.valueOf(b); //int转换成String

            p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
            if(c3==0){num=String.valueOf(c3);}
            createGroup();
            notification();
        } else {
        }
        refreshListView();
    }



    //按模式时间排序笔记
    public void sortNotes(List<Note> noteList, final int mode) {
        Collections.sort(noteList, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                try {
                    if (mode == 1) {
                        Log.d(TAG, "sortnotes 1");
                        return npLong(dateStrToSec(o2.getTime()) - dateStrToSec(o1.getTime()));
                    }
                    else if (mode == 2) {//reverseSort
                        Log.d(TAG, "sortnotes 2");
                        return npLong(dateStrToSec(o1.getTime()) - dateStrToSec(o2.getTime()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
    }

    //按模式时间排序计划
    public void sortPlans(List<Plan> planList, final int mode){
        Collections.sort(planList, new Comparator<Plan>() {
            @Override
            public int compare(Plan o1, Plan o2) {
                try {
                    if (mode == 1)
                        return npLong(calStrToSec(o1.getTime()) - calStrToSec(o2.getTime()));
                    else if (mode == 2) //reverseSort
                        return npLong(calStrToSec(o2.getTime()) - calStrToSec(o1.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });
    }

    //格式转换 string -> milliseconds
    public long dateStrToSec(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long secTime = format.parse(date).getTime();
        return secTime;
    }

    //统计不同标签的笔记数
    public List<Integer> numOfTagNotes(List<String> noteStringList){
        Integer[] numbers = new Integer[noteStringList.size()];
        for(int i = 0; i < numbers.length; i++) numbers[i] = 0;
        for(int i = 0; i < noteList.size(); i++){
            numbers[noteList.get(i).getTag() - 1] ++;
        }
        return Arrays.asList(numbers);
    }

    //turn long into 1, 0, -1
    public int npLong(Long l) {
        if (l > 0) return 1;
        else if (l < 0) return -1;
        else return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //设置提醒
    private void startAlarm(Plan p) {
        Calendar c = p.getPlanTime();
        if(!c.before(Calendar.getInstance())) {
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            intent.putExtra("title", p.getTitle());
            intent.putExtra("content", p.getContent());
            intent.putExtra("id", (int)p.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) p.getId(), intent, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    //设置很多提醒
    private void startAlarms(List<Plan> plans){
        for(int i = 0; i < plans.size(); i++) startAlarm(plans.get(i));
    }

    //取消提醒
    private void cancelAlarm(Plan p) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int)p.getId(), intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    //取消很多提醒
    private void cancelAlarms(List<Plan> plans){
        for(int i = 0; i < plans.size(); i++) cancelAlarm(plans.get(i));
    }


    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if(intent!=null && intent.getIntExtra("mode", 0) == 1){
            note_tab.setChecked(true);
            refreshLvVisibility();
        }
    }

    public void notification() {
        createNotificationChannel(chatChannelId, chatChannelName, chatChannelImportance, chatChannelDesc, groupId2);

        NotificationCompat.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, chatChannelId);
            builder.setSmallIcon(R.mipmap.ic_launcher2)
                    .setContentTitle("您今日计划已完成"+p_num+"%")
                    .setContentText("您还有"+num+"个计划未完成，请尽快完成！")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(1)
                    .setOngoing(true)// 将Ongoing设为true 那么notification将不能滑动删除
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            mNotificationManager.notify(2, builder.build());
        }

    }

    public void createNotificationChannel(String id, String name, int importance, String desc, String groupId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager.getNotificationChannel(id) != null) {
                return ;
            }

            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.setShowBadge(true);
            notificationChannel.setBypassDnd(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
            notificationChannel.setDescription(desc);
            notificationChannel.setGroup(groupId);

            mNotificationManager.createNotificationChannel(notificationChannel);

        }

    }
    public void createGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));
            mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId2, groupName2));
            createNotificationChannel(chatChannelId2, chatChannelName, chatChannelImportance, chatChannelDesc, groupId);
            createNotificationChannel(adChannelId2, adChannelName, adChannelImportance, adChannelDesc, groupId);
        }
    }
}
