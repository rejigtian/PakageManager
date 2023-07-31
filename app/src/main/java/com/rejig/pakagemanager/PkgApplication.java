package com.rejig.pakagemanager;

import android.app.Application;

import com.rejig.pakagemanager.utils.AppUtil;

public class PkgApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUtil.getInstance().init(this);
    }
}
