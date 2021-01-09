package com.example.reminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.reminders.plandatabase.AlarmReceiver;
import com.example.reminders.plandatabase.CRUD;
import com.example.reminders.plandatabase.FPlanAdapter;
import com.example.reminders.plandatabase.Plan;
import com.example.reminders.plandatabase.PlanAdapter;
import com.example.reminders.plandatabase.PlanDatabase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.view.View.GONE;

public class chart extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener{


    private PieChart chart;         //表
    private SeekBar seekBarX, seekBarY;     //控件
    private TextView tvX, tvY;          //控件显示数字

    private RadioButton today_button;
    private RadioButton history_button;
    private RelativeLayout today_plan;
    private RelativeLayout history_plan;
    private SharedPreferences sharedPreferences;

    private PlanDatabase planDbHelper;
    private FPlanAdapter planAdapter;
    private Context context = this;
    private List<Plan> planList = new ArrayList<Plan>();
    private ListView history_lv;
    private String fnum="1";
    private String num="1";
    private String p_num="25";
    int a,b;
    int c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);                //全屏显示 去掉导航栏
        setContentView(R.layout.activity_chart);
        initView();
        refreshLvVisibility();
        showplandatebase();
        setTitle("PieChartActivity");
        showlistView();
        //控件显示数字
      // tvX = findViewById(R.id.tvXMax);
      // tvY = findViewById(R.id.tvYMax);
        //控件
      // seekBarX = findViewById(R.id.seekBar1);
      // seekBarY = findViewById(R.id.seekBar2);

      // seekBarX.setOnSeekBarChangeListener(this);
       //seekBarY.setOnSeekBarChangeListener(this);

        chart = findViewById(R.id.chart1);   //图表与图表控件绑定
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

         chart.setDragDecelerationFrictionCoef(0.95f);

        //chart.setCenterTextTypeface(tfLight);
        //设置中间文件
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);

        chart.animateXY(1400, 1400);
        // enable rotation of the chart by touch
        //触摸旋转
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        //变化监听
       // chart.setOnChartValueSelectedListener(this);

       //seekBarX.setProgress(4);
        //seekBarY.setProgress(10);

        //chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
       // chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        setData(2,2);

    }

    private void showlistView() {
        history_lv=findViewById(R.id.history_lv);
        planAdapter = new FPlanAdapter(getApplicationContext(), planList);
        com.example.reminders.plandatabase.CRUD op1 = new com.example.reminders.plandatabase.CRUD(context);
        op1.open();
        planList.addAll(op1.getAllPlans_finish());
        op1.close();
        planAdapter.notifyDataSetChanged();
        history_lv.setAdapter(planAdapter);
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

    //turn long into 1, 0, -1
    public int npLong(Long l) {
        if (l > 0) return 1;
        else if (l < 0) return -1;
        else return 0;
    }
    public long calStrToSec(String date) throws ParseException {//decode calender date to second
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long secTime = format.parse(date).getTime();
        return secTime;
    }


    private void showplandatebase() {
        TextView num_finish=findViewById(R.id.num_finish);      //显示今日已完成数量
        TextView num_nofinish=findViewById(R.id.num_nofinish);      //显示今日未完成数量
        TextView finish_percent=findViewById(R.id.finish_percent);      //显示今日完成计划百分比

        //int num=5;
        com.example.reminders.plandatabase.CRUD op=new com.example.reminders.plandatabase.CRUD(context);
        op.open();
        a=op.get_num();//未完成计划个数
        b=op.get_num2();//已完成计划个数
        op.close();

        if(a==0&&b==0){ a=1;
            c=2;
        }else{
            c=0;
        }
        num=String.valueOf(a); //1
        fnum=String.valueOf(b); //3
        p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));

        if(c==2){
            num_nofinish.setText("0个");
        }else{
            num_nofinish.setText(num+"个");
        }
        num_finish.setText(fnum+"个");

        finish_percent.setText(p_num+"%");
    }

    private void initView() {

        initPrefs();
        //Layout布局
        today_plan = findViewById(R.id.today_plan);
        history_plan = findViewById(R.id.history_plan);

        //导航栏控件
        today_button =findViewById(R.id.today_button);
        history_button=findViewById(R.id.history_button);

        boolean temp=sharedPreferences.getBoolean("today_plan",false);
        boolean temp2=sharedPreferences.getBoolean("history_plan",false);

        //*********************************************************导航栏**************************************************************

        today_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("today_plan",isChecked);
                editor.commit();
                refreshLvVisibility();
            }
        });

        //  history_button.setChecked(temp2);
        history_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("history_plan",isChecked);
                editor.commit();
                refreshLvVisibility();

            }
        });

        //**********************************************************END***************************************************************
    }

    private void refreshLvVisibility() {
        boolean temp=sharedPreferences.getBoolean("today_plan",false);
        boolean temp2=sharedPreferences.getBoolean("history_plan",false);

        if(temp){
            today_plan.setVisibility(View.VISIBLE);
            history_plan.setVisibility(GONE);
        }else if(temp2){
            today_plan.setVisibility(GONE);
            history_plan.setVisibility(View.VISIBLE);
        }

    }

    private void initPrefs() {
        //initialize all useful SharedPreferences for the first time the app runs
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!sharedPreferences.contains("today_plan")){
            editor.putBoolean("today_plan",true);
            editor.commit();
        }
        if(!sharedPreferences.contains("history_plan")){
            editor.putBoolean("history_plan",false);
            editor.commit();
        }

    }

    //设置数据
    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
/**
        for (int i = 0; i < count ; i++) {
 entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
 //parties[i % parties.length],
 getResources().getDrawable(R.drawable.ic_assessment_black_24dp)));
        }
**/
       // com.example.reminders.plandatabase.CRUD op1 = new com.example.reminders.plandatabase.CRUD(context);
        //op1.open();
        //planList.addAll(op1.getAllPlans_finish());
        //op1.close();

           entries.add(new PieEntry(Integer.parseInt(p_num),"已完成"));
            entries.add(new PieEntry(100-Integer.parseInt(p_num),"未完成"));


        PieDataSet dataSet = new PieDataSet(entries, "您的今日计划");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        //数据和颜色
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
       data.setValueTypeface(Typeface.DEFAULT_BOLD);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        //刷新
        chart.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //当我滑动seekBarx，和seekbary的时候此方法被调用
        tvX.setText(String.valueOf(seekBarX.getProgress())); //输出seekbarx的值
        tvY.setText(String.valueOf(seekBarY.getProgress()));//输出seekbarY的值

        //当进程改变时输入新的数据
       // setData(seekBarX.getProgress(), seekBarY.getProgress());
    }


    //中间字体
    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("您的今日计划");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
       // s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
      //  s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
      //  s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
      //  s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
      //  s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}

