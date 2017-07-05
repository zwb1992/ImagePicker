package com.zwb.imagepicker;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by zwb
 * Description
 * Date 2017/7/5.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
