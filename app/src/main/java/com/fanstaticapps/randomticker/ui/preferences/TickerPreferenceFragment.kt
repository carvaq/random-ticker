package com.fanstaticapps.randomticker.ui.preferences

import android.content.Intent
import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.helper.NotificationCoordinator
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TickerPreferenceFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Inject
    lateinit var notificationManager: NotificationCoordinator


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)

        bindShowRunningNotificationPreference()

        findPreference<Preference>(getString(R.string.pref_license_key))?.setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }
    }

    private fun bindShowRunningNotificationPreference() {
        val checkBoxPreference =
            findPreference<CheckBoxPreference>(getString(R.string.pref_show_notification_key))
        checkBoxPreference ?: return
        // Set the listener to watch for value changes.
        checkBoxPreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, value ->
                if (preference is CheckBoxPreference) {
                    preference.isChecked = value.toString().toBoolean()
                    true
                } else {
                    false
                }
            }
    }
}