package com.cvv.fanstaticapps.randomticker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

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

        }else{
            Toast.makeText(this, R.string.error_min_is_bigger_than_max, Toast.LENGTH_SHORT).show();
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
