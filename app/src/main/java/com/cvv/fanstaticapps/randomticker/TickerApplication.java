package com.cvv.fanstaticapps.randomticker;

import android.app.Application;

import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.configuration.Configuration;
import toothpick.registries.FactoryRegistryLocator;
import toothpick.registries.MemberInjectorRegistryLocator;
import toothpick.smoothie.module.SmoothieApplicationModule;

/**
 * Created by Carla
 * Date: 20/09/2017
 * Project: RandomTicker
 */

public class TickerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Toothpick.setConfiguration(Configuration.forProduction().disableReflection());
        MemberInjectorRegistryLocator.setRootRegistry(new com.cvv.fanstaticapps.randomticker.MemberInjectorRegistry());
        FactoryRegistryLocator.setRootRegistry(new com.cvv.fanstaticapps.randomticker.FactoryRegistry());

        Scope appScope = Toothpick.openScope(this);
        appScope.installModules(new SmoothieApplicationModule(this));
    }
}
