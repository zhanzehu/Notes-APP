package com.example.reminders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import static android.app.Notification.BADGE_ICON_LARGE;
import static android.app.Notification.BADGE_ICON_SMALL;

public class loading extends AppCompatActivity {
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
    private Context context=this;

    int a,b,c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //取消标题栏
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().hide();
        }
        //取消状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView imageview =(ImageView) findViewById(R.id.loading);

        imageview.setImageResource(R.drawable.loading);
        // 从浅到深,从百分之10到百分之百
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(3000);//设置动画时间
        imageview.setAnimation(aa);//给image设置动画
        aa.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                Intent intent =new Intent();
                intent.setClass(loading.this, MainActivity.class);//logo展示完毕跳转至另一个Activity
                startActivity(intent);
                finish();
            }
        });

        com.example.reminders.plandatabase.CRUD op=new com.example.reminders.plandatabase.CRUD(context);
        op.open();
        a=op.get_num();//未完成计划个数
        b=op.get_num2();//已完成计划个数
        op.close();

        if(a==0&&b==0){ a=1;c=0; }
        num=String.valueOf(a); //int转换成String
        fnum=String.valueOf(b); //int转换成String
        p_num=String.valueOf((Integer.parseInt(fnum)*100)/(Integer.parseInt(fnum)+Integer.parseInt(num)));
        if(c==0){num=String.valueOf(c);}
         createGroup();
          notification();
    }

    public void notification() {
        createNotificationChannel(chatChannelId, chatChannelName, chatChannelImportance, chatChannelDesc, groupId2);

        Notification.Builder builder = null;

        /** if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Android8的notification创建代码
        }else{
                Android7的notification创建代码
        }**/

       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, chatChannelId);
            builder.setSmallIcon(R.mipmap.ic_launcher2)
                    .setContentTitle("您今日计划已完成"+p_num+"%")
                    .setContentText("您还有"+num+"个计划未完成，请尽快完成！")
                    .setBadgeIconType(BADGE_ICON_SMALL)
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
