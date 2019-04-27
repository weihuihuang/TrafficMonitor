package com.splendid.trafficmonitor.common;

import android.app.Application;
import android.content.Context;

import com.splendid.floatingwindowlibrary.manager.TrafficMonitoringManager;

public class MyApplication extends Application {

    private final static int NET_POLLING_PRIOD_MILLIS = 1000;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //小米手机需要给app授予显示悬浮窗的权限才会显示悬浮窗口
        TrafficMonitoringManager.startPollingTask(getContext(), NET_POLLING_PRIOD_MILLIS);
    }

    public static Context getContext() {
        return context;
    }
}
