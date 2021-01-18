package com.app.kgs_user.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.kgs_user.R;
import com.app.kgs_user.activity.MainActivity;

public class MyNotificationManager {  // For Oreo Version

    private Context mCtx;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void showNotificationMessage(final String title, final String message) {

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mCtx.getPackageName() + "/" + R.raw.notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Notification Channel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(Config.CHANNEL_ID, Config.CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //notificationChannel.setSound(android.net.Uri.parse("android.resource://org.nativescript.notification/raw/" + soundFileName), null);
            notificationChannel.setSound(alarmSound, null);
            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mCtx, Config.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setSound(alarmSound)
                        .setContentText(message);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent intent = new Intent(mCtx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(alarmSound);
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(mCtx.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
    }

}