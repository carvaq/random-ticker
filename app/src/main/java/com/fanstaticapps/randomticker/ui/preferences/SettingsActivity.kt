package com.fanstaticapps.randomticker.ui.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.NavUtils
import com.fanstaticapps.randomticker.R
import com.fanstaticapps.randomticker.ui.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}