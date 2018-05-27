package com.cvv.fanstaticapps.randomticker.activities

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.View
import com.cvv.fanstaticapps.randomticker.R
import kotlinx.android.synthetic.main.activity_license.*

class LicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
    }

    override fun onPostCreate(@Nullable savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        webView.loadUrl("file:///android_asset/licenses.html")
    }
}
