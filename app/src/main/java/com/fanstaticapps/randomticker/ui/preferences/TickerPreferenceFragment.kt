package com.fanstaticapps.randomticker.ui.preferences

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
import android.provider.Settings.EXTRA_APP_PACKAGE
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.TickerPreferences
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import com.fanstaticapps.randomticker.extensions.setDarkTheme
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker
import javax.inject.Inject

@AndroidEntryPoint
class TickerPreferenceFragment : PreferenceFragmentCompat(), MusicPickerListener {

    @Inject
    lateinit var tickerPreferences: TickerPreferences

    @Inject
    lateinit var notificationManager: TickerNotificationManager

    override fun onMusicPick(uri: Uri, title: String) {
        activity?.let {
            tickerPreferences.alarmRingtone = uri.toString()
            findPreference<Preference>(getString(R.string.pref_ringtone_key))?.let { updateRingtonePreferenceSummary(it) }
        }
    }

    override fun onPickCanceled() {
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        bindShowRunningNotificationPreference()

        findPreference<Preference>(getString(R.string.pref_license_key))?.setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }

        bindNotificationChannelPreference()

        bindDarkThemePreference()
    }

    private fun bindNotificationChannelPreference() {
        val notificationChannelPreference = findPreference<Preference>(getString(R.string.pref_open_notification_channel))
        val vibrationPreference = findPreference<CheckBoxPreference>(getString(R.string.pref_vibration_key))
        val ringtonePreference = findPreference<Preference>(getString(R.string.pref_ringtone_key))

        if (isAtLeastAndroid26()) {
            notificationManager.createNotificationChannelIfNecessary(requireContext())
            notificationChannelPreference?.setOnPreferenceClickListener {
                val intent = Intent(ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(EXTRA_APP_PACKAGE, context?.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, TickerNotificationManager.FOREGROUND_CHANNEL_ID)
                startActivity(intent)
                true
            }
            notificationChannelPreference?.isVisible = true
            vibrationPreference?.isVisible = false
            ringtonePreference?.isVisible = false
        } else {
            notificationChannelPreference?.isVisible = false
            bindRingtonePreference(ringtonePreference)
            bindVibrationPreference(vibrationPreference)
        }
    }

    private fun bindVibrationPreference(vibrationPreference: CheckBoxPreference?) {
        vibrationPreference ?: return
        vibrationPreference.isChecked = getBooleanPreference(vibrationPreference)
        vibrationPreference.setOnPreferenceChangeListener { preference, newValue ->
            (preference as CheckBoxPreference).isChecked = newValue.toString().toBoolean()
            true
        }


    }

    private fun bindDarkThemePreference() {
        val darkThemePreference = findPreference<CheckBoxPreference>(getString(R.string.pref_dark_theme_key))
                ?: return
        darkThemePreference.isChecked = getBooleanPreference(darkThemePreference)
        darkThemePreference.setOnPreferenceChangeListener { preference, newValue ->
            (preference as CheckBoxPreference).isChecked = newValue.toString().toBoolean()
            setDarkTheme(tickerPreferences)
            activity?.recreate()
            true
        }
    }


    private fun bindRingtonePreference(preference: Preference?) {
        preference ?: return
        preference.setOnPreferenceClickListener {
            val defaultUri = Uri.parse(TickerPreferences(it.context).alarmRingtone)
            UltimateMusicPicker()
                    .defaultUri(defaultUri)
                    .ringtone()
                    .notification()
                    .alarm()
                    .music()
                    .goWithDialog(childFragmentManager)

            true
        }
        updateRingtonePreferenceSummary(preference)
    }

    private fun updateRingtonePreferenceSummary(preference: Preference) {
        val uriPath = tickerPreferences.alarmRingtone
        if (uriPath.isEmpty()) {
            preference.setSummary(R.string.pref_ringtone_silent)
        } else {
            when (val ringtone = RingtoneManager.getRingtone(preference.context, Uri.parse(uriPath))) {
                null -> preference.summary = null
                else -> {
                    try {
                        val name = ringtone.getTitle(preference.context)
                        preference.summary = name
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Timber.e(e, "Could not load title for ringtone")
                        preference.summary = null
                    }
                }
            }
        }
    }

    private fun bindShowRunningNotificationPreference() {
        val checkBoxPreference = findPreference<CheckBoxPreference>(getString(R.string.pref_show_notification_key))
        checkBoxPreference ?: return
        // Set the listener to watch for value changes.
        checkBoxPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, value ->
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