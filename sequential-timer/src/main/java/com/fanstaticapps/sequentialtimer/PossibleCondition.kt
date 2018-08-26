package com.fanstaticapps.sequentialtimer

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

enum class PossibleCondition(@StringRes val labelResId: Int, @StringRes val summaryResId: Int, @LayoutRes val layoutResId: Int) {
    EXACT_INTERVAL(R.string.condition_exact_interval, R.string.condition_summary_exact_interval, R.layout.view_exact_interval),
    RANDOM_INTERVAL(R.string.condition_random_interval, R.string.condition_summary_random_interval, R.layout.view_random_interval),
    NUMBER_OF_TIMES(R.string.condition_number_of_times, R.string.condition_summary_number_of_times, R.layout.view_minimum_count)
}