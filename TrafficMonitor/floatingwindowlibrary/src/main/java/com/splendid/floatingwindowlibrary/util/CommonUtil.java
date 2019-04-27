package com.splendid.floatingwindowlibrary.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtil {

    public static int getUid(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getNetStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null && manager.getActiveNetworkInfo() != null) {
            NetworkInfo activeNet = manager.getActiveNetworkInfo();
            if (activeNet != null && activeNet.isAvailable() && activeNet.isConnected()) {
                int netType = activeNet.getType();
                switch (netType) {
                    case 0:
                        return "3G/4G";
                    case 1:
                        return "WIFI";
                    default:
                        return "网络错误";
                }
            }
        }
        return "网络错误";
    }
}
