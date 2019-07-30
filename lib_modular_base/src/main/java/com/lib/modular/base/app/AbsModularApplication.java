package com.lib.modular.base.app;

import android.app.Application;
import android.content.res.Configuration;

public class AbsModularApplication implements IModularApplication {

    private Application mApplication;

    public AbsModularApplication(Application application){
        mApplication = application;
    }


    @Override
    public Application getApplication() {
        return mApplication;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory() {

    }
}
