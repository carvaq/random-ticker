package com.fanstaticapps.randomticker.injection

import com.fanstaticapps.randomticker.ui.CancelActivity
import com.fanstaticapps.randomticker.ui.klaxon.KlaxonActivity
import com.fanstaticapps.randomticker.ui.main.MainActivity
import com.fanstaticapps.randomticker.ui.preferences.SettingsActivity
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