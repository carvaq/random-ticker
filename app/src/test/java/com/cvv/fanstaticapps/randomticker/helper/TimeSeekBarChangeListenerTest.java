package com.cvv.fanstaticapps.randomticker.helper;

import android.widget.SeekBar;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Carla
 * Date: 05/02/2018
 * Project: RandomTicker
 */

@RunWith(RobolectricTestRunner.class)
public class TimeSeekBarChangeListenerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SeekBar seekBarMinutes;
    @Mock
    private SeekBar seekBarSeconds;
    @Mock
    private TextView displayTextView;

    private TimeSeekBarChangeListener timeSeekBarChangeListener;

    @Before
    public void setUp() throws Exception {
        timeSeekBarChangeListener = new TimeSeekBarChangeListener(displayTextView, seekBarMinutes, seekBarSeconds);
    }

    @Test
    public void testCorrectFormat() throws Exception {
        setSeekBarValues(12, 2);
        verify(displayTextView).setText("12m 02s");

        setSeekBarValues(2, 2);
        verify(displayTextView).setText("02m 02s");

        setSeekBarValues(12, 12);
        verify(displayTextView).setText("12m 12s");
    }

    private void setSeekBarValues(int minutes, int seconds) {
        when(seekBarMinutes.getProgress()).thenReturn(minutes);
        when(seekBarSeconds.getProgress()).thenReturn(seconds);
        timeSeekBarChangeListener.onProgressChanged(null, 0, true);
    }
}