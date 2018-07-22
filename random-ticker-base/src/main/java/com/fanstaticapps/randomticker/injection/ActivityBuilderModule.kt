package com.fanstaticapps.randomticker.injection

import com.fanstaticapps.common.activities.CancelActivity
import com.fanstaticapps.common.activities.SettingsActivity
import com.fanstaticapps.randomticker.activities.KlaxonActivity
import com.fanstaticapps.randomticker.activities.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by carvaq
 * Date: 5/26/18
 * Project: random-ticker
 */

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindKlaxonActivity(): KlaxonActivity

    @ContributesAndroidInjector
    abstract fun bindCancelActivity(): CancelActivity


    @ContributesAndroidInjector
    abstract fun bindSettingsActivity(): SettingsActivity

}