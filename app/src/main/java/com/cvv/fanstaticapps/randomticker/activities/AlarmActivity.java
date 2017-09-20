package com.cvv.fanstaticapps.randomticker.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.NotificationHelper;
import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;
import com.shitij.goyal.slidebutton.SwipeButton;

import javax.inject.Inject;

import butterknife.BindView;
import io.github.kobakei.grenade.annotation.Extra;
import io.github.kobakei.grenade.annotation.Navigator;

@Navigator
public class AlarmActivity extends BaseActivity {

    private static final String REMAINING_SECONDS = "%ss";

    @Extra
    boolean cancelNotification;
    @Extra
    long intervalFinished;
    @Extra
    boolean timeElapsed;


    @BindView(R.id.alarm_bell_icon)
    RichPathView alarmBell;
    @BindView(R.id.dismiss_button)
    SwipeButton swipeButton;
    @BindView(R.id.remaining_time)
    TextView remainingTime;
    @BindView(R.id.remaining_time_label)
    TextView label;

    @Inject
    NotificationHelper notificationHelper;

    private Ringtone playingAlarmSound;
    private RichPathAnimator bellAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        AlarmActivityNavigator.inject(this, getIntent());

        if (cancelNotification) {
            notificationHelper.cancelNotification(this);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (timeElapsed) {
            timerFinished();
        } else {
            startCountDownTimer();
            prepareSwipeButton();
        }
    }

    private void startCountDownTimer() {
        new CountDownTimer(intervalFinished - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setText(String.format(REMAINING_SECONDS, (int) (millisUntilFinished / 1000)));
            }

            @Override
            public void onFinish() {
                timerFinished();
            }
        }.start();
    }

    private void timerFinished() {
        playRingtone();
        startAnimation();
        label.setVisibility(View.GONE);
        remainingTime.setVisibility(View.GONE);
        swipeButton.setVisibility(View.GONE);
    }

    private void prepareSwipeButton() {
        swipeButton.addOnSwipeCallback(new SwipeButton.Swipe() {
            @Override
            public void onButtonPress() {

            }

            @Override
            public void onSwipeCancel() {

            }

            @Override
            public void onSwipeConfirm() {
                if (playingAlarmSound != null) {
                    playingAlarmSound.stop();
                }
                if (bellAnimator != null) {
                    bellAnimator.cancel();
                }
                cancelNotificationAndGoBack();
            }
        });
    }

    private void cancelNotificationAndGoBack() {
        notificationHelper.cancelNotification(this);
        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startIntent);
        overridePendingTransition(0, 0);
        finish();
    }


    private void startAnimation() {
        RichPath top = alarmBell.findRichPathByName("top");
        RichPath bottom = alarmBell.findRichPathByName("bottom");

        bellAnimator = RichPathAnimator.animate(top)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 20, -20, 10, -10, 5, -5, 2, -2, 0)
                .duration(4000)
                .andAnimate(bottom)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 10, -10, 5, -5, 2, -2, 0)
                .startDelay(50)
                .duration(4000)
                .repeatMode(RichPathAnimator.RESTART)
                .repeatCount(RichPathAnimator.INFINITE)
                .start();
    }


    private void playRingtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            playingAlarmSound = RingtoneManager.getRingtone(getApplicationContext(), notification);
            playingAlarmSound.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
