package bc.yxdc.com.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import bc.yxdc.com.global.IssApplication;

public class MyChatService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = (NotificationManager) IssApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        NotificationChannel channel = new NotificationChannel("1", "channel_name", NotificationManager.IMPORTANCE_HIGH);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = new Notification.Builder(this, "1").build();
        startForeground(1, notification);
    }
}
