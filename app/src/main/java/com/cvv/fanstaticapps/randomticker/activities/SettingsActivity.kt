package com.cvv.fanstaticapps.randomticker.activities

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.preference.*
import android.support.v4.app.NavUtils
import android.view.MenuItem
import com.cvv.fanstaticapps.randomticker.R
import kotlinx.android.synthetic.main.app_bar.*

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 *
 * See [
 * Android Design: Settings](http://developer.android.com/design/patterns/settings.html) for design guidelines and the [Settings
 * API Guide](http://developer.android.com/guide/topics/ui/settings.html) for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content, NotificationPreferenceFragment())
                    .commit()
        }
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this)
            }
            return true
        }
        return super.onMenuItemSelected(featureId, item)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName || NotificationPreferenceFragment::class.java.name == fragmentName
    }

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
                startActivity(Intent(activity, LicenseActivity::class.java))
                true
            }
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
                        val name = ringtone.getTitle(preference.context)
                        preference.summary = name
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
}
