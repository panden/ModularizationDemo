package com.lib.log;

import android.app.Application;
import android.content.Context;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.lib.modular.base.app.AbsModularApplication;
import com.lib.utils.PathUtils;

import java.io.File;

public class LogApplication extends AbsModularApplication {

    private static final String DEFAULT_TAG = "XLog";

    public LogApplication(Application application) {
        super(application);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initXlog(getApplication());
    }

    //初始化xlog库
    private void initXlog(Context context) {
        LogConfiguration configuration = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
                .tag(DEFAULT_TAG)
                .build();

        Printer androidPrinter = new AndroidPrinter();
        //日志文件存放目录：/storage/emulated/0/Android/data/package/files/Documents/XLog/
        File xlogPrintDir = new File(PathUtils.getExternalAppDocumentsPath(context), "XLog");
        Printer filePrinter = new FilePrinter.Builder(xlogPrintDir.getAbsolutePath())//制定日志存储的目录
                .logFlattener(new TimeFlatter())//设置日志输出的时间格式
                .build();
        XLog.init(configuration, androidPrinter, filePrinter);
    }
}
