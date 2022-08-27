package com.fanstaticapps.randomticker.ui.preferences

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
import android.provider.Settings.EXTRA_APP_PACKAGE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.extensions.getVibrator
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TickerPreferenceFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Inject
    lateinit var notificationManager: TickerNotificationManager


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)

        bindShowRunningNotificationPreference()

        findPreference<Preference>(getString(R.string.pref_license_key))?.setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }

        bindNotificationChannelPreference()
    }

    private fun bindNotificationChannelPreference() {
        val notificationChannelPreference =
            findPreference<Preference>(getString(R.string.pref_open_notification_channel))
        val vibrationPreference =
            findPreference<CheckBoxPreference>(getString(R.string.pref_vibration_key))
        val ringtonePreference = findPreference<Preference>(getString(R.string.pref_ringtone_key))

        if (isAtLeastAndroid26()) {
            notificationManager.createNotificationChannelIfNecessary(requireContext())
            notificationChannelPreference?.setOnPreferenceClickListener {
                Intent(ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(EXTRA_APP_PACKAGE, context?.packageName)
                    putExtra(
                        Settings.EXTRA_CHANNEL_ID,
                        TickerNotificationManager.FOREGROUND_CHANNEL_ID
                    )
                }.let { startActivity(it) }
                true
            }
        } else {
            notificationChannelPreference?.isVisible = false
        }
        vibrationPreference?.isVisible = requireContext().getVibrator()?.hasVibrator() == true

        ringtonePreference?.bindRingtonePreference()
        vibrationPreference?.bindVibrationPreference()
    }

    private fun CheckBoxPreference.bindVibrationPreference() {
        isChecked = getBooleanPreference(this)
        setOnPreferenceChangeListener { preference, newValue ->
            (preference as CheckBoxPreference).isChecked = newValue.toString().toBoolean()
            true
        }
    }

    private fun Preference.bindRingtonePreference() {
        updateRingtonePreferenceSummary(this)

        val callback = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                tickerPreferences.alarmRingtone = activityResult.data?.getUri().toString()
            }
            updateRingtonePreferenceSummary(this)
        }
        setOnPreferenceClickListener {
            Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                    Settings.System.DEFAULT_NOTIFICATION_URI
                )
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    Settings.System.DEFAULT_NOTIFICATION_URI
                )
                tickerPreferences.alarmRingtone.takeIf { it.isNotBlank() }?.let {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, it.toUri())
                }
            }.let { callback.launch(it) }
            true
        }
    }

    @Suppress("DEPRECATION")
    private fun Intent.getUri() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(
                RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                Uri::class.java
            )
        } else {
            getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        }

    private fun updateRingtonePreferenceSummary(preference: Preference) {
        val uriPath = tickerPreferences.alarmRingtone
        if (uriPath.isEmpty()) {
            preference.setSummary(R.string.pref_ringtone_silent)
        } else {
            when (val ringtone =
                RingtoneManager.getRingtone(preference.context, Uri.parse(uriPath))) {
                null -> preference.summary = null
                else -> {
                    try {
                        val name = ringtone.getTitle(preference.context)
                        preference.summary = name
                    } catch (e: Exception) {
                        Timber.e(e, "Could not load title for ringtone")
                        preference.summary = null
                    }
                }
            }
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

    private fun getBooleanPreference(preference: Preference): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(preference.context)
        return when (preference) {
            is CheckBoxPreference -> preferenceManager.getBoolean(preference.key, false)
            else -> throw RuntimeException("This should not be called if not CheckBoxPrenfece")
        }
    }
}