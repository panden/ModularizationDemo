package com.lib.modular.base.app;

import android.app.Application;
import android.content.res.Configuration;


public interface IModularApplication {

    Application getApplication();

    void onCreate();

    void onTerminate();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory();
}
