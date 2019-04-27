package com.splendid.floatingwindowlibrary.manager;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.splendid.floatingwindowlibrary.model.NetFloatingMessage;
import com.splendid.floatingwindowlibrary.view.NetFloatingWindow;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by WeiHuiHuang on 2019/4/25. 计算流量信息的工具类
 */
public class TrafficMonitoringManager {

    private static NetFloatingWindow netFloatingWindow;

    private static TimerTask task;

    private final static int UPDATE_NET_MESSAGE = 1;

    private static Context context;

    private static int period;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == UPDATE_NET_MESSAGE){
                windowMessageUpdate();
            }
        }
    };


    //开始定时任务
    public static void startPollingTask(Context mContext,int periodtimeMillis) {
        System.currentTimeMillis();
        context = mContext;
        period= periodtimeMillis;
        initFloatingView();
        Timer timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATE_NET_MESSAGE;
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task,0,period);
    }

    /**
     * 初始化悬浮窗口
     */
    public static void initFloatingView() {
        netFloatingWindow = new NetFloatingWindow(new NetFloatingMessage(context,period),context,period);
        netFloatingWindow.showMax();
    }


    public static void windowMessageUpdate() {
        if (netFloatingWindow == null) {
            startPollingTask(context,period);
        }
        netFloatingWindow.updateWindowDisplayMessage();
    }

    public static void stopPollingTask() {
        task.cancel();
        mHandler.removeCallbacksAndMessages(task);
    }
}
