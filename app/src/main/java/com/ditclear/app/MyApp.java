package com.ditclear.app;

import android.app.Application;
import android.content.Context;

/**
 * 页面描述：
 * <p>
 * Created by ditclear on 2017/3/5.
 */

public class MyApp extends Application {

    private static Application instance;

    public static Context instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }
}
