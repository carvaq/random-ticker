package com.cvv.fanstaticapps.randomticker.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.activities.AlarmActivityNavigator;

import javax.inject.Inject;

/**
 * Created by Carla
 * Date: 20/09/2017
 * Project: RandomTicker
 */

public class NotificationHelper {

    @Inject
    public NotificationHelper() {

    }

    private static final int NOTIFICATION_ID = 2312;

    public void createNotification(Context context, long interval) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationCompat.CATEGORY_ALARM);

        Intent alarmIntent = new AlarmActivityNavigator(false, interval, false).build(context);
        Intent cancelIntent = new AlarmActivityNavigator(true, interval,false).build(context);
        PendingIntent alarmPendingIntent =
                PendingIntent.getActivity(context, 0, alarmIntent, 0);
        PendingIntent cancelPendingIntent
                = PendingIntent.getActivity(context, 0, cancelIntent, 0);

        NotificationCompat.Action cancelAction =
                new NotificationCompat.Action(R.drawable.ic_action_stop_timer,
                        context.getString(android.R.string.cancel), cancelPendingIntent);
        Notification notification = builder
                .addAction(cancelAction)
                .setContentTitle(context.getString(R.string.app_name))
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(alarmPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setUsesChronometer(true)
                .setTicker(context.getString(R.string.info_alarm_set, interval))
                .setSmallIcon(R.drawable.ic_stat_timer)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void cancelNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
