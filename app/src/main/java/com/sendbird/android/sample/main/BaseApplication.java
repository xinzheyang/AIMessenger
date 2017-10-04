package com.sendbird.android.sample.main;


import android.app.Application;

import com.sendbird.android.SendBird;

public class BaseApplication extends Application {

    private static final String APP_ID = "26A73EDE-7E2F-4DC3-A512-9C93409FCC01"; // US-1 Demo
    public static final String VERSION = "3.0.36";

    @Override
    public void onCreate() {
        super.onCreate();
        SendBird.init(APP_ID, getApplicationContext());
    }
}
