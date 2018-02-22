package com.cvv.fanstaticapps.randomticker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;

import com.cvv.fanstaticapps.randomticker.activities.BaseActivity;

import butterknife.BindView;

public class LicenseActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl("file:///android_asset/licenses.html");
    }
}
