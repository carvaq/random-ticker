package com.cvv.fanstaticapps.randomticker.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;

import com.cvv.fanstaticapps.randomticker.R;
import com.cvv.fanstaticapps.randomticker.helper.TimerHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.kobakei.grenade.annotation.Extra;
import io.github.kobakei.grenade.annotation.Navigator;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static com.cvv.fanstaticapps.randomticker.helper.TimerHelper.ONE_SECOND_IN_MILLIS;

@Navigator
public class KlaxonActivity extends BaseActivity {

    private static final int ANIMATION_DURATION = 750;
    private static final String TAG = KlaxonActivity.class.getSimpleName();

    @Extra
    boolean timeElapsed;

    @BindView(R.id.waiting_icon)
    View waitingIcon;
    @BindView(R.id.timer_hand)
    View timerHand;
    @BindView(R.id.root)
    View root;
    @BindView(R.id.dismiss_button)
    View dismissButton;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsatorLayout;

    @Inject
    TimerHelper timerHelper;

    private Ringtone playingAlarmSound;
    private Animation waitingIconAnimation;
    //timestamp when the timer should ring
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KlaxonActivityNavigator.inject(this, intent);
        if (timeElapsed && countDownTimer != null) {
            countDownTimer.cancel();
            timerFinished();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                countDownTimer = null;
            }
        };
        countDownTimer.start();
        startBellAnimation();
    }

    private void timerFinished() {
        timerHelper.cancelNotification(this, preferences);
        playRingtone();
        if (!pulsatorLayout.isStarted()) {
            hideBellAndMoveCancelButton();
        }
    }

    private void hideBellAndMoveCancelButton() {
        waitingIconAnimation.cancel();
        waitingIcon.animate()
                .alpha(0)
                .scaleX(0)
                .scaleY(0)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        waitingIcon.setVisibility(View.GONE);
                    }
                }).start();

        pulsatorLayout.animate()
                .scaleX(1.5f)
                .setStartDelay(100)
                .scaleY(1.5f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        pulsatorLayout.start();
                    }
                })
                .setDuration(ANIMATION_DURATION);
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
        if (waitingIconAnimation != null) {
            waitingIconAnimation.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void startBellAnimation() {
        waitingIconAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.55f);
        waitingIconAnimation.setRepeatCount(Animation.INFINITE);
        waitingIconAnimation.setInterpolator(new CycleInterpolator(1));
        waitingIconAnimation.setDuration(4000);
        timerHand.startAnimation(waitingIconAnimation);
    }


    private void playRingtone() {
        if (playingAlarmSound == null) {
            try {
                String preferenceRingtone = PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getString(getString(R.string.pref_ringtone),
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
                Uri uri = Uri.parse(preferenceRingtone);
                playingAlarmSound = RingtoneManager.getRingtone(getApplicationContext(), uri);
                playingAlarmSound.play();
            } catch (Exception e) {
                Log.e(TAG, "Error while trying to play alarm sound", e);
            }
        } else {
            toast(R.string.bell_ringing);
        }
    }

}
