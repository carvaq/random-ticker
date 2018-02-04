package com.cvv.fanstaticapps.randomticker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cvv.fanstaticapps.randomticker.helper.PrefUserSettings;

import butterknife.ButterKnife;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.smoothie.module.SmoothieActivityModule;

/**
 * Created by Carla
 * Date: 20/09/2017
 * Project: RandomTicker
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Scope scope;
    protected PrefUserSettings preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scope = Toothpick.openScopes(getApplication(), this);
        scope.installModules(new SmoothieActivityModule(this));
        super.onCreate(savedInstanceState);
        Toothpick.inject(this, scope);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences = new PrefUserSettings(prefs);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        Toothpick.closeScope(scope);
        super.onDestroy();
    }

    protected void toast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
