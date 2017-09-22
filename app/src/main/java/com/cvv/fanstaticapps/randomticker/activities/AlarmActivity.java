package com.cvv.fanstaticapps.randomticker.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;
import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.kobakei.grenade.annotation.Extra;
import io.github.kobakei.grenade.annotation.Navigator;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static com.cvv.fanstaticapps.randomticker.helper.TimerHelper.ONE_SECOND_IN_MILLIS;

@Navigator
public class AlarmActivity extends BaseActivity {

    private static final String TAG = AlarmActivity.class.getSimpleName();

    @Extra
    boolean cancelNotification;
    @Extra
    boolean timeElapsed;


    @BindView(R.id.alarm_bell_icon)
    RichPathView alarmBell;

    @BindView(R.id.pulsator)
    PulsatorLayout pulsatorLayout;
    @BindView(R.id.remaining_time)
    TextView remainingTime;
    @BindView(R.id.remaining_time_label)
    TextView label;

    @Inject
    TimerHelper timerHelper;

    private Ringtone playingAlarmSound;
    private RichPathAnimator bellAnimator;
    private long intervalFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        AlarmActivityNavigator.inject(this, getIntent());

        if (cancelNotification) {
            timerHelper.cancelNotification(this);
        }
        intervalFinished = preferences.getIntervalFinished();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (timeElapsed) {
            timerFinished();
        } else {
            startCountDownTimer();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (playingAlarmSound != null) {
            playingAlarmSound.stop();
        }
    }

    private void startCountDownTimer() {
        new CountDownTimer(intervalFinished - System.currentTimeMillis(), ONE_SECOND_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime.setText(timerHelper.getFormattedElapsedMilliseconds(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                timerFinished();
                preferences.setCurrentlyTickerRunning(false);
            }
        }.start();
    }

    private void timerFinished() {
        playRingtone();
        startAnimation();
        label.setVisibility(View.GONE);
        remainingTime.setVisibility(View.GONE);
        pulsatorLayout.start();
    }

    @OnClick(R.id.dismiss_button)
    void onDismissClicked() {
        if (playingAlarmSound != null) {
            playingAlarmSound.stop();
        }
        if (bellAnimator != null) {
            bellAnimator.cancel();
        }
        cancelNotificationAndGoBack();
    }

    private void cancelNotificationAndGoBack() {
        preferences.setCurrentlyTickerRunning(false);
        timerHelper.cancelNotification(this);
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
        if (playingAlarmSound == null) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                playingAlarmSound = RingtoneManager.getRingtone(getApplicationContext(), notification);
                playingAlarmSound.play();
            } catch (Exception e) {
                Log.e(TAG, "Error while trying to play alarm sound", e);
            }
        } else {
            toast("Ringing ma bell");
        }
    }

}
