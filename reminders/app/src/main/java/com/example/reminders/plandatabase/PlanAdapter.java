package com.example.reminders.plandatabase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminders.EditActivity;
import com.example.reminders.MainActivity;
import com.example.reminders.R;
import com.example.reminders.database.Note;
import com.example.reminders.loading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;

import static android.app.Notification.BADGE_ICON_SMALL;
import static android.content.ContentValues.TAG;

public class PlanAdapter extends BaseAdapter implements Filterable {
    private Context mContext;

    final String TAG="oopoo";
    private int checkNum; // 记录选中的条目数量
    private List<Plan> backList;//用来备份原始数据
    private List<Plan> planList;//这个数据是会改变的，所以要有个变量来备份一下原始数据
    private MyFilter mFilter;
    private MyFilter2 myFilter2;
    private AlarmManager alarmManager;

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

    int a,b,c;


    //滑动删除
    public static ListItemDelete itemDelete = null;

    public PlanAdapter(Context mContext, List<Plan> planList) {
        this.mContext = mContext;
        this.planList = planList;
        backList = planList;
    }

    @Override
    public int getCount() {
        return planList.size();
    }

    @Override
    public Object getItem(int position) {
        return planList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mContext.setTheme((sharedPreferences.getBoolean("nightMode", false)? R.style.NightTheme: R.style.DayTheme));
        View v = View.inflate(mContext, R.layout.plan_layout, null);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
        TextView tv_content = (TextView)v.findViewById(R.id.tv_content);
        TextView tv_time = (TextView)v.findViewById(R.id.tv_time);
        ImageView btnDelete_plan=(ImageView)v.findViewById(R.id.btnDelete_plan);
        //ImageView btnNao_plan=(ImageView)v.findViewById(R.id.btnNao_plan);
        ImageView btnDelete=(ImageView)v.findViewById(R.id.btnDelete);
        //ImageView btnNao=(ImageView)v.findViewById(R.id.btnNao);

        CheckBox accomplish=(CheckBox)v.findViewById(R.id.accomplish);

        final Plan plan = planList.get(position);
        //******************************************滑动删除********************************************
        btnDelete_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planList.remove(position);
                notifyDataSetChanged();
                CRUD op = new CRUD(mContext);
                op.open();
                op.removePlan(plan);
                a=op.get_num();//未完成计划个数
                b=op.get_num2();//已完成计划个数
                op.close();
                showInfo("点击删除了");
                itemDelete.reSet();
                if(a==0&&b==0){ a=1; }
                num=String.valueOf(a); //int转换成String
                fnum=String.valueOf(b); //int转换成String

                p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
                createGroup();
                notification();
            }
        });
        //********************************************点击编辑***********************************************
        //********************************************Checkbox**********************************************
        accomplish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked=true){
                planList.remove(position);
                notifyDataSetChanged();
                CRUD op = new CRUD(mContext);
                op.open();
                op.removePlan(plan);
                op.addPlan_finish(plan);
                    a=op.get_num();//未完成计划个数
                    b=op.get_num2();//已完成计划个数
                op.close();
                showInfo("计划已完成");
                    if(a==0&&b==0){ a=1;c=0; }
                    num=String.valueOf(a); //int转换成String
                    fnum=String.valueOf(b); //int转换成String

                    p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
                    if(c==0){num=String.valueOf(c);}
                    createGroup();
                    notification();
                }
            }
        });
        //***********************************************************************************************
        //Set text for TextView
        tv_title.setText(planList.get(position).getTitle());
        tv_content.setText(planList.get(position).getContent());
        tv_time.setText(planList.get(position).getTime());
        //Save plan id to tag
        v.setTag(planList.get(position).getId());

        return v;
    }

    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new PlanAdapter.MyFilter();
        }
        return mFilter;
    }


    public Filter getFilter2(){
        if (myFilter2 ==null){
            myFilter2 = new PlanAdapter.MyFilter2();
        }
        return myFilter2;

    }


    class MyFilter extends Filter {
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<Plan> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Plan plan : backList) {
                    if (plan.getTitle().contains(charSequence) || plan.getContent().contains(charSequence)) {
                        list.add(plan);
                    }

                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }





        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            planList = (List<Plan>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }


    private Toast mToast;
//提示框
    public void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public static void ItemDeleteReset() {
        if (itemDelete != null) {
            itemDelete.reSet();
        }
    }

//日历功能
    class MyFilter2 extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence charSequence){
            FilterResults result = new FilterResults();
            List<Plan> list;
            StringBuffer buf = new StringBuffer();
            buf.append(charSequence);
            String year = buf.substring(0, 4);        //年
            String month = buf.substring(4, 6);        //月
            String dayOfMonth = buf.substring(6, 8);   //日

            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (Plan plan : backList) {
                    if (plan.getTime().substring(0, 4).equals(year) && plan.getTime().substring(5, 7).equals(month) && plan.getTime().substring(8, 10).equals(dayOfMonth)) {
                        list.add(plan);
                    } else {
                        Log.d(TAG, "错误");
                    }
                }
            }

            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }



        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            planList = (List<Plan>)filterResults.values;
            if (filterResults.count>0){
                notifyDataSetChanged();//通知数据发生了改变
            }else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }

    }

    public void notification() {
        createNotificationChannel(chatChannelId, chatChannelName, chatChannelImportance, chatChannelDesc, groupId2);

        NotificationCompat.Builder builder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(mContext, chatChannelId);
            builder.setSmallIcon(R.mipmap.ic_launcher2)
                    .setContentTitle("您今日计划已完成"+p_num+"%")
                    .setContentText("您还有"+num+"个计划未完成，请尽快完成！")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setNumber(1)
                    .setOngoing(true)// 将Ongoing设为true 那么notification将不能滑动删除
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(mContext, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
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
//        notificationChannel.setSound();

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
