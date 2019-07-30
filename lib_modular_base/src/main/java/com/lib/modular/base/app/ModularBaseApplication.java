package com.lib.modular.base.app;

import android.app.Application;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

public class ModularBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ModularApplicationManager.getInstance().dispatchApplicationOnCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ModularApplicationManager.getInstance().dispatchApplicationOnTermanite(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ModularApplicationManager.getInstance().dispatchApplicationOnConfigurationChanged(this, newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ModularApplicationManager.getInstance().dispatchApplicationOnLowMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        ModularApplicationManager.getInstance().dispatchApplicationOnTrimMemory(this);
    }
}
