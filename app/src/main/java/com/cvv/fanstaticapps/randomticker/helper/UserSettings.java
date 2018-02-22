package com.cvv.fanstaticapps.randomticker.helper;

import org.xelevra.prefdata.annotations.PrefData;

@PrefData
abstract class UserSettings {
    int minMin;
    int minSec;
    int maxMin = 5;
    int maxSec;
    long interval;
    long intervalFinished;
    boolean currentlyTickerRunning;
}