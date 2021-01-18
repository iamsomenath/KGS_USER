package com.app.kgs_user.firebase;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.app.kgs_user.R;
import com.app.kgs_user.activity.MainActivity;

public class NotificationHandler {

    private Context mContext;

    public NotificationHandler(Context mContext) {
        this.mContext = mContext;
    }

    //This method would display the notification
    public void showNotificationMessage(final String title, final String message) {

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, Config.CHANNEL_ID)
                        .setSmallIcon(R.drawable.login)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.login))
                        .setContentTitle(title)
                        .setSound(alarmSound)
                        .setContentText(message);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        //builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.login_round));
        builder.setSmallIcon(R.drawable.login);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(alarmSound);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
    }
}