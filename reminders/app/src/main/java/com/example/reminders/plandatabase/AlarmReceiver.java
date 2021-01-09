package com.example.reminders.plandatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.reminders.MainActivity;
import com.example.reminders.R;

import androidx.core.app.NotificationCompat;

import static android.content.Context.VIBRATOR_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    private String channelId = "生活便签";
    private String name = "ChannelName";
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getExtras().getString("title");
        String content = intent.getExtras().getString("content");
        int id = intent.getExtras().getInt("id");
        Intent intent1 = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent1, 0);
        intent1.putExtra("mode", 1);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.enableVibration(true);
            manager.createNotificationChannel(mChannel);
        }

        //手机震动 适应Android10版本
        Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,1000,1000};
        AudioAttributes audioAttributes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) //key
                    .build();
            mVibrator.vibrate(pattern, 1, audioAttributes);
        }else {
            mVibrator.vibrate(pattern, 1);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title).setContentText(content).setSmallIcon(R.drawable.red_alarm_24dp)
                .setContentIntent(pendingIntent).setAutoCancel(true).setFullScreenIntent(pendingIntent, true);

        Notification notification = builder.build();
        manager.notify(1, notification);
    }
}
