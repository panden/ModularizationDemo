package com.lib.crash;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * created by siwei on 2018/7/26
 * 本地异常日志记录
 */
public class LocalCrash implements Thread.UncaughtExceptionHandler {

    private static LocalCrash sLocakCrash;
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private Builder mBuilder;
    private ILog mILog;

    private LocalCrash(Builder builder) {
        this.mBuilder = builder;
        mILog = mBuilder.getILog();
        mILog.setTag("LocalCrash");
    }

    public static LocalCrash instance(Builder builder) {
        if (sLocakCrash == null) {
            synchronized (LocalCrash.class) {
                sLocakCrash = new LocalCrash(builder);
            }
        }
        return sLocakCrash;
    }

    /**
     * 注册异常监听
     */
    public void register(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        mDefaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        crashRecovery(e);
        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        }
        killProcess();
    }

    /**
     * 关闭进程
     */
    private void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static class Builder {

        private Map<String, String> appendParams = new HashMap<>();//追加的参数
        private String outDirName = "LocalCrash";//输出目录
        private String appendFileName;//追加的文件名
        private Context mContext;
        private ILog mILog = ILog.DefaultLog;

        public Builder(Context context) {
            if (context == null) throw new NullPointerException("context not be null");
            if (!(context instanceof Application)) {
                context = context.getApplicationContext();
            }
            mContext = context;
        }

        /**
         * 当记录异常时,需要携带在日志文件中的参数,方便根据参数去寻找问题
         */
        public Builder putParams(String key, String value) {
            this.appendParams.put(key, value);
            return this;
        }

        /**
         * 移除需要携带的参数
         */
        public Builder removeParams(String key) {
            this.appendParams.remove(key);
            return this;
        }

        /**
         * 设置日志输出目录名
         */
        public Builder setOutDirName(String outDir) {
            this.outDirName = outDir;
            return this;
        }

        /**
         * 设置日志文件名需要添加的名称
         */
        public Builder setAppendFileName(String appendFileName) {
            this.appendFileName = appendFileName;
            return this;
        }

        protected Map<String, String> getAppendParams() {
            return appendParams;
        }

        protected String getOutDirName() {
            return outDirName;
        }

        protected String getAppendFileName() {
            return appendFileName;
        }

        protected Context getContext() {
            return mContext;
        }

        protected ILog getILog() {
            return mILog;
        }

        /**
         * 设置日志输出
         */
        public Builder setILog(ILog ILog) {
            if (ILog != null) mILog = ILog;
            return this;
        }
    }

    /**
     * 记录异常
     */
    private void crashRecovery(Throwable throwable) {
        File outDir = getCrashLoggerDir(mBuilder.getContext(), mBuilder.getOutDirName());
        if (!outDir.exists()) outDir.mkdirs();
        String fileName = "creash";
        if (!TextUtils.isEmpty(mBuilder.getAppendFileName())) {
            fileName += "_" + mBuilder.getAppendFileName() + "_";
        }
        String formatter = "yyyy_MM_dd_HH_mm_ss_SS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatter);
        fileName += "_" + simpleDateFormat.format(new Date()) + ".crash";
        String filePath = new File(outDir, fileName).getPath();
        saveCrash(throwable, filePath);
    }

    //存储异常
    private void saveCrash(Throwable throwable, String filePath) {
        mILog.e("save crash:%s", throwable.getMessage());
        if (throwable == null) return;
        StringBuffer buffer = new StringBuffer();
        PrintWriter printWriter = null;
        try {
            Context context = mBuilder.getContext();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            buffer.append(String.format("App Version Code:%s\n", pi.versionCode));
            buffer.append(String.format("App Version Name:%s\n", pi.versionName));
            buffer.append(String.format("Android:%s\n", Build.VERSION.SDK_INT));
            buffer.append(String.format("Crash Time:%s\n", new Date().toLocaleString()));
            if (mBuilder.getAppendParams() != null) {
                buffer.append("\nPARAMS====================\n");
                Iterator<Map.Entry<String, String>> iterator = mBuilder.getAppendParams().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    buffer.append(String.format("%s : %s\n", entry.getKey(), entry.getValue()));
                }
                buffer.append("PARAMS====================\n");
            }
            buffer.append("\n");
            printWriter = new PrintWriter(new File(filePath));
            printWriter.write(buffer.toString());
            throwable.printStackTrace(printWriter);
            printWriter.flush();
            addRecoveryCrashTime(mBuilder.getContext());
            mILog.e("crash recovery file:%s success", filePath);
        } catch (Exception e) {
            mILog.e("crash recovery file:%s failure e:%s", e.getMessage(), filePath);
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    //获取异常存储的目录
    private File getCrashLoggerDir(Context context, String fileName) {
        if (context == null) return null;
        if (fileName == null) fileName = "";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (!file.exists()) file.mkdirs();
        return file;
    }


    public interface ILog {

        ILog setTag(String tag);

        void e(String msg, Object... values);

        ILog DefaultLog = new ILog() {

            private String mTag;

            @Override
            public ILog setTag(String tag) {
                mTag = tag;
                return this;
            }

            @Override
            public void e(String msg, Object... values) {
                Log.e(mTag, String.format(msg, values));
            }
        };
    }

    private static final String SP_NAME = "crash_covery_sp";
    private static final String SP_KEY_RECOVERY_CRASH_TIME = "sp_key_recovery_crash_time";

    /**添加一个新的crash记录*/
    private static void addRecoveryCrashTime(Context context){
        if(context == null)return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        int crashTime = sharedPreferences.getInt(SP_KEY_RECOVERY_CRASH_TIME, 0);
        sharedPreferences.edit().putInt(SP_KEY_RECOVERY_CRASH_TIME, crashTime + 1);
    }

    /**获取记录到的异常的次数*/
    public static int getRecoveryCrashTime(Context context){
        if(context == null)return -1;
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        int crashTime = sharedPreferences.getInt(SP_KEY_RECOVERY_CRASH_TIME, 0);
        return crashTime;
    }
}
