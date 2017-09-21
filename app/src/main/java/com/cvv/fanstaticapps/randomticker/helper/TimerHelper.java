package com.cvv.fanstaticapps.randomticker.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateUtils;

import com.cvv.fanstaticapps.randomticker.OnAlarmReceive;
import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.activities.AlarmActivityNavigator;

import javax.inject.Inject;

/**
 * Created by Carla
 * Date: 20/09/2017
 * Project: RandomTicker
 */

public class TimerHelper {

    private static final Handler HANDLER = new Handler();
    public static final long ONE_SECOND_IN_MILLIS = 1000;

    @Inject
    TimerHelper() {
    }

    private static final int NOTIFICATION_ID = 2312;

    public void createNotificationAndAlarm(final Context context, final long interval, final long intervalFinished) {
        showNotification(context, interval, intervalFinished);

        HANDLER.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intervalFinished <= System.currentTimeMillis()) return;
                showNotification(context, interval, intervalFinished);
                HANDLER.postDelayed(this, ONE_SECOND_IN_MILLIS);
            }
        }, ONE_SECOND_IN_MILLIS);

        setAlarm(context, intervalFinished);
    }

    private void showNotification(Context context, long interval, long intervalFinished) {
        Notification notification = buildNotification(context, interval, intervalFinished);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    private Notification buildNotification(final Context context, long interval, final long intervalFinished) {
        Intent alarmIntent = getAlarmIntent(context, false);
        Intent cancelIntent = getAlarmIntent(context, true);

        PendingIntent alarmPendingIntent =
                PendingIntent.getActivity(context, 0, alarmIntent, 0);
        PendingIntent cancelPendingIntent
                = PendingIntent.getActivity(context, 0, cancelIntent, 0);
        NotificationCompat.Action cancelAction =
                new NotificationCompat.Action(R.drawable.ic_action_stop_timer,
                        context.getString(android.R.string.cancel), cancelPendingIntent);
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NotificationCompat.CATEGORY_ALARM);
        String formattedInterval = getFormattedElapsedMilliseconds(interval);
        builder
                .addAction(cancelAction)
                .setContentTitle(context.getString(R.string.notification_title, formattedInterval))
                .setContentText(getFormattedElapsedMilliseconds(intervalFinished - System.currentTimeMillis()))
                .setAutoCancel(false)
                .setShowWhen(true)
                .setContentIntent(alarmPendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setTicker(context.getString(R.string.info_alarm_set, interval))
                .setSmallIcon(R.drawable.ic_stat_timer);

        return builder.build();
    }

    public String getFormattedElapsedMilliseconds(long elapsedMilliseconds) {
        return DateUtils.formatElapsedTime(elapsedMilliseconds / ONE_SECOND_IN_MILLIS);
    }

    private void setAlarm(Context context, long intervalFinished) {
        PendingIntent pendingIntent = getAlarmPendingIntent(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, intervalFinished, pendingIntent);
    }

    private PendingIntent getAlarmPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, OnAlarmReceive.class);
        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getAlarmIntent(Context context, boolean cancelNotification) {
        return new AlarmActivityNavigator(cancelNotification,  false).build(context);
    }

    public void cancelNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        alarmManager.cancel(getAlarmPendingIntent(context));
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
