package com.lib.utils;

import android.app.Application;
import android.content.Context;

public class ContextUtils {

    /**
     * 获取Application.Context 避免应用Activity的Context而导致内存泄漏的情况发生
     */
    public static Context getApplicationContex(Context context) {
        if (context == null) return null;
        if (context instanceof Application) {
            return context;
        } else {
            return context.getApplicationContext();
        }
    }
}
