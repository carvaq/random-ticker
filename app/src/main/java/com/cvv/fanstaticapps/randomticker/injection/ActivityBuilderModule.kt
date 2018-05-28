package com.cvv.fanstaticapps.randomticker.injection

import com.cvv.fanstaticapps.randomticker.activities.CancelActivity
import com.cvv.fanstaticapps.randomticker.activities.KlaxonActivity
import com.cvv.fanstaticapps.randomticker.activities.LicenseActivity
import com.cvv.fanstaticapps.randomticker.activities.MainActivity
import dagger.Module


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
    abstract fun bindLicenseActivity(): LicenseActivity
}