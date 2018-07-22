package com.fanstaticapps.common.activities

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.*
import com.crashlytics.android.Crashlytics
import com.fanstaticapps.common.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import timber.log.Timber

/**
 * This fragment shows notification preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
class NotificationPreferenceFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_ringtone)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_notification)))
        findPreference(getString(R.string.pref_license)).setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val bindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is RingtonePreference) {
                bindRingtonePreference(stringValue, preference)
            } else if (preference is CheckBoxPreference) {
                preference.isChecked = stringValue.toBoolean()
            }
            true
        }

        private fun bindRingtonePreference(stringValue: String, preference: Preference) {
            if (stringValue.isEmpty()) {
                preference.setSummary(R.string.pref_ringtone_silent)
            } else {
                val ringtone = RingtoneManager.getRingtone(
                        preference.context, Uri.parse(stringValue))
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
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see .bindPreferenceSummaryToValueListener
     */
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