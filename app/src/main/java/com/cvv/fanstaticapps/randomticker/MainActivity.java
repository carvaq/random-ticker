package com.cvv.fanstaticapps.randomticker;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.min_min)
    EditText minMin;
    @BindView(R.id.min_sec)
    EditText minSec;
    @BindView(R.id.max_min)
    EditText maxMin;
    @BindView(R.id.max_sec)
    EditText maxSec;

    private Random randomGenerator = new Random(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.start)
    void onStartClicked() {
        int min = getTotalValueInMillis(minMin, minSec);
        int max = getTotalValueInMillis(maxMin, maxSec);
        if (max > min) {
            long randomNum = randomGenerator.nextInt((max - min) + 1) + min;
            //TODO confirmation screen
            //TODO show notification
            //TODO New activity for timer
        } else {
            Toast.makeText(this, R.string.error_min_is_bigger_than_max, Toast.LENGTH_SHORT).show();
        }
    }

    private void playRingtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getTotalValueInMillis(EditText minute, EditText second) {
        return (60 * getIntValue(minute) + getIntValue(second)) * 1000;
    }

    private int getIntValue(EditText editText) {
        CharSequence value = editText.getText();
        if (TextUtils.isEmpty(value)) {
            return 0;
        } else {
            return Integer.valueOf(value.toString());
        }
    }
}
