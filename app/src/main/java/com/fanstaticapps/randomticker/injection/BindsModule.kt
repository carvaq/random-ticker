package com.fanstaticapps.randomticker.injection

import com.fanstaticapps.randomticker.helper.IntentHelper
import com.fanstaticapps.randomticker.helper.RandomTickerIntentHelper
import dagger.Binds
import dagger.Module

@Module
abstract class BindsModule {
    @Binds
    abstract fun provideUserPreferences(intentHelper: RandomTickerIntentHelper): IntentHelper
}