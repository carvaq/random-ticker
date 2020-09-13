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
import com.crashlytics.android.Crashlytics
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.UserPreferences
import com.fanstaticapps.randomticker.extensions.getNotificationManager
import com.fanstaticapps.randomticker.extensions.isAtLeastAndroid26
import com.fanstaticapps.randomticker.extensions.setDarkTheme
import com.fanstaticapps.randomticker.helper.TickerNotificationManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import timber.log.Timber
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker


class NotificationPreferenceFragment : PreferenceFragmentCompat(), MusicPickerListener {


    private lateinit var userPreferences: UserPreferences

    override fun onMusicPick(uri: Uri, title: String) {
        activity?.let {
            findPreference<Preference>(getString(R.string.pref_ringtone_key))
            userPreferences.alarmRingtone = uri.toString()
        }
    }

    override fun onPickCanceled() {
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        userPreferences = UserPreferences(activity!!)
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        bindRingtonePreference(findPreference(getString(R.string.pref_ringtone_key)))

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_notification_key)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vibration_key)))
        findPreference<Preference>(getString(R.string.pref_license_key))?.setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }

        val notificationChannelPreference = findPreference<Preference>(getString(R.string.pref_open_notification_channel))
        if (isAtLeastAndroid26()) {
            val notificationChannel = context?.getNotificationManager()?.getNotificationChannel(TickerNotificationManager.FOREGROUND_CHANNEL_ID)
            notificationChannelPreference?.setOnPreferenceClickListener {
                val intent = Intent(ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(EXTRA_APP_PACKAGE, context?.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, TickerNotificationManager.FOREGROUND_CHANNEL_ID)
                startActivity(intent)
                true
            }
            notificationChannelPreference?.isVisible = notificationChannel != null
        } else {
            notificationChannelPreference?.isVisible = false
        }


        bindDarkThemePreference()

    }

    private fun bindDarkThemePreference() {
        val darkThemePreference = findPreference<CheckBoxPreference>(getString(R.string.pref_dark_theme_key))
        darkThemePreference ?: return
        darkThemePreference.isChecked = getPreferenceValue(darkThemePreference).toString().toBoolean()
        darkThemePreference.setOnPreferenceChangeListener { preference, newValue ->
            (preference as CheckBoxPreference).isChecked = newValue.toString().toBoolean()
            setDarkTheme(userPreferences)
            activity?.recreate()
            true
        }
    }

    private val checkboxPreferenceListener = Preference.OnPreferenceChangeListener { preference, value ->
        if (preference is CheckBoxPreference) {
            preference.isChecked = value.toString().toBoolean()
            true
        } else {
            false
        }
    }

    private fun bindRingtonePreference(preference: Preference?) {
        preference ?: return
        preference.setOnPreferenceClickListener {
            val defaultUri = Uri.parse(UserPreferences(it.context).alarmRingtone)
            UltimateMusicPicker()
                    .defaultUri(defaultUri)
                    .ringtone()
                    .notification()
                    .alarm()
                    .music()
                    // Show a picker dialog
                    .goWithDialog(childFragmentManager)

            true
        }
        val uriPath = userPreferences.alarmRingtone
        if (uriPath.isEmpty()) {
            preference.setSummary(R.string.pref_ringtone_silent)
        } else {
            val ringtone = RingtoneManager.getRingtone(preference.context, Uri.parse(uriPath))
            when (ringtone) {
                null -> preference.summary = null
                else -> {
                    try {
                        val name = ringtone.getTitle(preference.context)
                        preference.summary = name
                    } catch (e: Exception) {
                        Crashlytics.logException(e)
                        Timber.e(e, "Could not load title for ringtone")
                        preference.summary = null
                    }
                }
            }
        }
    }

    private fun bindPreferenceSummaryToValue(preference: Preference?) {
        preference ?: return
        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = checkboxPreferenceListener

        checkboxPreferenceListener.onPreferenceChange(preference, getPreferenceValue(preference))
    }

    private fun getPreferenceValue(preference: Preference): Any {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(preference.context)
        return when (preference) {
            is CheckBoxPreference -> preferenceManager.getBoolean(preference.key, false)
            else -> preferenceManager.getString(preference.key, "").orEmpty()
        }
    }
}