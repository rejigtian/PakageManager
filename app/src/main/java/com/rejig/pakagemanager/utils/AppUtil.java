package com.rejig.pakagemanager.utils;

import android.app.Application;

/**
 * 管理app的工具类
 * @author rejig
 * date 2021-08-10
 */
public class AppUtil {
    private Application application;
    private static final AppUtil instance = new AppUtil();

    public static AppUtil getInstance() {
        return instance;
    }

    public void init(Application application){
        this.application = application;
    }

    public static Application getApplication(){
        return getInstance().application;
    }


}
