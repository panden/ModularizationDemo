package com.lib.log;

import android.text.TextUtils;

import com.elvishew.xlog.XLog;

public class Logger {

    private String mTag;
    private String mCustomTag;
    private boolean logEnable = true;

    private Logger(String tag) {
        this.mTag = tag;
    }

    private Logger(String tag, boolean logEnable) {
        this.mTag = tag;
        this.logEnable = logEnable;
    }

    public Logger tag(String customTag) {
        this.mCustomTag = customTag;
        return this;
    }

    public void i(String log, Object args) {
        if (logEnable) {
            XLog.tag(getTag()).i(log, args);
        }
    }

    public void v(String log, Object args) {
        if (logEnable) {
            XLog.tag(getTag()).v(log, args);
        }
    }

    public void d(String log, Object args) {
        if (logEnable) {
            XLog.tag(getTag()).d(log, args);
        }
    }

    public void w(String log, Object args) {
        if (logEnable) {
            XLog.tag(getTag()).w(log, args);
        }
    }

    public void e(String log, Object args) {
        if (logEnable) {
            XLog.tag(getTag()).e(log, args);
        }
    }

    private String getTag() {
        String showTag = "";
        showTag = TextUtils.isEmpty(mCustomTag) ? mTag : mCustomTag;
        mCustomTag = null;
        return showTag;
    }

    public static Logger createLogger(String tag){
        return new Logger(tag);
    }

    public static Logger createLogger(String tag, boolean logEnable){
        return new Logger(tag, logEnable);
    }
}
