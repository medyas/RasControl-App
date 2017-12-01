package com.sabri.com.mini_project;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by Mohamed Yassine on 9/29/2017.
 */

public class Notification extends Service {

    Activity rootact;

    @Override
    public void onCreate() {
        Log.d("Reminder", "Service Started");

    }

    @Override
    public void onDestroy() {
        Log.d("Reminder", "Service Stoped");

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setAct(Activity act) {
        rootact = act;
    }

    public void not(String msg, int i) {
        NotificationManager manager = (NotificationManager) rootact.getSystemService(NOTIFICATION_SERVICE);

        int icon = R.drawable.raspberry;
        CharSequence tickerText = "New Notification";
        CharSequence contentTitle = rootact.getText(R.string.app_name);
        CharSequence contentText = msg;

        Intent notificationIntent = new Intent(rootact, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(rootact, 0, notificationIntent, flag);

        android.app.Notification notification = new android.app.Notification.Builder(rootact.getApplicationContext())
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        switch (i) {
            case 1:
                notification.sound = Uri.parse("android.resource://" + rootact.getPackageName() + "/" + R.raw.connected);
                break;
            case 2:
                notification.sound = Uri.parse("android.resource://" + rootact.getPackageName() + "/" + R.raw.disconnected);
                break;
            case 3:
                notification.sound = Uri.parse("android.resource://" + rootact.getPackageName() + "/" + R.raw.object_detected);
                break;
            default:
                notification.sound = Uri.parse("android.resource://" + rootact.getPackageName() + "/" + R.raw.defaultring);
                break;
        }

        //notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;

        manager.notify(1, notification);
    }
}
