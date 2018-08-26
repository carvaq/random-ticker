package com.fanstaticapps.randomticker.injection

import com.fanstaticapps.common.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.RandomTickerIntentHelper
import dagger.Binds
import dagger.Module

@Module
abstract class BindsModule {
    @Binds
    abstract fun provideUserPreferences(intentHelper: RandomTickerIntentHelper): IntentHelper
}