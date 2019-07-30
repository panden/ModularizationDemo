package com.lib.modular.base.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModularApplicationManager {

    private static ModularApplicationManager INSTANCE;

    private List<String> mIModularApplicationClass = new ArrayList<>();
    private Map<String, AbsModularApplication> mIModularApplicationMap = new HashMap<>();
    private boolean hadInitModularApplication;

    private ModularApplicationManager() {
    }

    public static ModularApplicationManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ModularApplicationManager.class) {
                INSTANCE = new ModularApplicationManager();
            }
        }
        return INSTANCE;
    }

    /**
     * 注册模块间的application class
     */
    public ModularApplicationManager registerModularApplicationClass(String applicationClass) {
        if (TextUtils.isEmpty(applicationClass)) return this;
        if (mIModularApplicationClass.contains(applicationClass)) return this;
        mIModularApplicationClass.add(applicationClass);
        return this;
    }

    //反射创建AbsModularApplication
    private AbsModularApplication createApplicationClass(String applicationClass, Application application) {
        try {
            Class applicationClasses = Class.forName(applicationClass);
            Constructor<AbsModularApplication> constructor = applicationClasses.getConstructor(Application.class);
            constructor.setAccessible(true);
            AbsModularApplication absModularApplication = constructor.newInstance(application);
            return absModularApplication;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 注销模块间的application class
     */
    public ModularApplicationManager unregisterModularApplicationClass(String applicationClass) {
        if (TextUtils.isEmpty(applicationClass)) return this;
        if (!mIModularApplicationClass.contains(applicationClass)) return this;
        mIModularApplicationClass.remove(applicationClass);
        return this;
    }

    /**
     * 初始化模块间的application
     */
    public void initModularApplication(Application application) {
        if (hadInitModularApplication) return;
        hadInitModularApplication = true;
        for (String applicationClass : mIModularApplicationClass) {
            AbsModularApplication absModularApplication = createApplicationClass(applicationClass, application);
            if (absModularApplication != null) {
                mIModularApplicationMap.put(applicationClass, absModularApplication);
            }
        }
        mIModularApplicationClass.clear();
        mIModularApplicationClass = null;
    }

    public void dispatchApplicationOnCreate(Application application) {
        if (!checkSelfProcress(application)) return;
        Collection<AbsModularApplication> collection = mIModularApplicationMap.values();
        for (AbsModularApplication absModularApplication : collection) {
            if (absModularApplication != null) {
                try {
                    absModularApplication.onCreate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispatchApplicationOnTermanite(Application application) {
        if (!checkSelfProcress(application)) return;
        Collection<AbsModularApplication> collection = mIModularApplicationMap.values();
        for (AbsModularApplication absModularApplication : collection) {
            if (absModularApplication != null) {
                try {
                    absModularApplication.onTerminate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispatchApplicationOnConfigurationChanged(Application application, Configuration newConfig) {
        if (!checkSelfProcress(application)) return;
        Collection<AbsModularApplication> collection = mIModularApplicationMap.values();
        for (AbsModularApplication absModularApplication : collection) {
            if (absModularApplication != null) {
                try {
                    absModularApplication.onConfigurationChanged(newConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispatchApplicationOnLowMemory(Application application) {
        if (!checkSelfProcress(application)) return;
        Collection<AbsModularApplication> collection = mIModularApplicationMap.values();
        for (AbsModularApplication absModularApplication : collection) {
            if (absModularApplication != null) {
                try {
                    absModularApplication.onLowMemory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispatchApplicationOnTrimMemory(Application application) {
        if (!checkSelfProcress(application)) return;
        Collection<AbsModularApplication> collection = mIModularApplicationMap.values();
        for (AbsModularApplication absModularApplication : collection) {
            if (absModularApplication != null) {
                try {
                    absModularApplication.onTrimMemory();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //检查是否是当前自己的进程在运行，避免多进程导致会被初始化两次
    private static boolean checkSelfProcress(Context context) {
        if (context == null) return false;
        return TextUtils.equals(context.getPackageName(), getCurrentProgressName(context));
    }

    //获取当前运行的进程名
    private static String getCurrentProgressName(Context context) {
        if (context == null) return null;
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                return appProcessInfo.processName;
            }
        }
        return null;
    }
}
