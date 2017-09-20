package com.cvv.fanstaticapps.randomticker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.DecelerateInterpolator;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;

import javax.inject.Inject;

import butterknife.BindView;
import io.github.kobakei.grenade.annotation.Extra;
import io.github.kobakei.grenade.annotation.Navigator;

@Navigator
public class AlarmActivity extends BaseActivity {

    @Extra
    boolean cancelNotification;
    @Extra
    long interval;

    @BindView(R.id.alarm_bell_icon)
    RichPathView alarmBell;

    @Inject
    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        AlarmActivityNavigator.inject(this, getIntent());

        if (cancelNotification) {
            notificationHelper.cancelNotification(this);
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        RichPath top = alarmBell.findRichPathByName("top");
        RichPath bottom = alarmBell.findRichPathByName("bottom");

        RichPathAnimator.animate(top)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 20, -20, 10, -10, 5, -5, 2, -2, 0)
                .duration(4000)
                .andAnimate(bottom)
                .interpolator(new DecelerateInterpolator())
                .rotation(0, 10, -10, 5, -5, 2, -2, 0)
                .startDelay(50)
                .duration(4000)
                .start();
    }
}
