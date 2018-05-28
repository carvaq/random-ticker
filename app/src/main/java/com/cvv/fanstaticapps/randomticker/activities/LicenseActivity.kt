package com.cvv.fanstaticapps.randomticker.activities

import android.os.Bundle
import android.view.View
import com.cvv.fanstaticapps.randomticker.R
import kotlinx.android.synthetic.main.activity_license.*
import kotlinx.android.synthetic.main.app_bar.*

class LicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        webView.loadUrl("file:///android_asset/licenses.html")
    }
}
