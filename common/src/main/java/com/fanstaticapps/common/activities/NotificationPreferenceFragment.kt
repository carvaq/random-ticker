package com.fanstaticapps.common.activities

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.crashlytics.android.Crashlytics
import com.fanstaticapps.common.R
import com.fanstaticapps.common.UserPreferences
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import timber.log.Timber
import xyz.aprildown.ultimatemusicpicker.MusicPickerListener
import xyz.aprildown.ultimatemusicpicker.UltimateMusicPicker


class NotificationPreferenceFragment : PreferenceFragmentCompat(), MusicPickerListener {


    private lateinit var userPreferences: UserPreferences

    override fun onMusicPick(uri: Uri, title: String) {
        activity?.let {
            findPreference(getString(R.string.pref_ringtone))
            userPreferences.alarmRingtone = uri.toString()
        }
    }

    override fun onPickCanceled() {
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        userPreferences = UserPreferences(activity!!)
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        bindRingtonePreference(findPreference(getString(R.string.pref_ringtone)))

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_notification)))
        findPreference(getString(R.string.pref_license)).setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }
    }

    private val bindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
        val stringValue = value.toString()
        if (preference is CheckBoxPreference) {
            preference.isChecked = stringValue.toBoolean()
        }
        true
    }

    private fun bindRingtonePreference(preference: Preference) {
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

    private fun bindPreferenceSummaryToValue(preference: Preference) {
        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = bindPreferenceSummaryToValueListener

        bindPreferenceSummaryToValueListener.onPreferenceChange(preference, getPreferenceValue(preference))
    }

    private fun getPreferenceValue(preference: Preference): Any {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(preference.context)
        return when (preference) {
            is CheckBoxPreference -> preferenceManager.getBoolean(preference.key, false)
            else -> preferenceManager.getString(preference.key, "")
        }
    }
}