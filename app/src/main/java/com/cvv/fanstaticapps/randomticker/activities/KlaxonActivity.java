package com.cvv.fanstaticapps.randomticker.activities;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

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
public class KlaxonActivity extends BaseActivity {

    private static final String TAG = KlaxonActivity.class.getSimpleName();

    @Extra
    boolean timeElapsed;

    @BindView(R.id.alarm_bell_icon)
    RichPathView alarmBell;

    @BindView(R.id.pulsator)
    PulsatorLayout pulsatorLayout;

    @Inject
    TimerHelper timerHelper;

    private Ringtone playingAlarmSound;
    private RichPathAnimator bellAnimator;
    private long intervalFinished;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_klaxon);
        KlaxonActivityNavigator.inject(this, getIntent());

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
        cancelEverything();
    }

    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(intervalFinished - System.currentTimeMillis(), ONE_SECOND_IN_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                //nothing to do
            }

            @Override
            public void onFinish() {
                timerFinished();
                preferences.setCurrentlyTickerRunning(false);
            }
        };
    }

    private void timerFinished() {
        playRingtone();
        startAnimation();
        pulsatorLayout.start();
    }

    @OnClick(R.id.dismiss_button)
    void onDismissClicked() {
        cancelEverything();
        timerHelper.cancelNotificationAndGoBack(this, preferences);
    }

    private void cancelEverything() {
        if (playingAlarmSound != null) {
            playingAlarmSound.stop();
        }
        if (bellAnimator != null) {
            bellAnimator.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
