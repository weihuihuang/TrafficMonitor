package com.splendid.floatingwindowlibrary.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.splendid.floatingwindowlibrary.R;
import com.splendid.floatingwindowlibrary.manager.TrafficMonitoringManager;
import com.splendid.floatingwindowlibrary.model.NetFloatingMessage;
import com.splendid.floatingwindowlibrary.util.CommonUtil;

/**
 * Created by WeiHuiHuang on 2019/4/25. 网络监测的悬浮窗口
 */
public class NetFloatingWindow {

    private WindowManager.LayoutParams mLayoutParams;

    private WindowManager mWindowManager;

    //显示内容
    private View mContentView;

    //展开内容
    private View floatingMaxView;

    //关闭内容
    private View floatingMinView;

    private final int WINDOW_STATUS_MAX = 1;

    private final int WINDOW_STATUS_MIN = 2;

    private int curWindowStatus = -1;

    private TextView mNetStatusTv;

    private TextView mTrafficIncreasedTv;

    private TextView mNetSpeedTv;

    private NetFloatingMessage message;

    private Context context;

    private int period;

    public NetFloatingWindow(NetFloatingMessage message, Context context, int period) {
        this.message = message;
        this.context = context;
        this.period = period;
    }

    private Context getContext() {
        if (context == null) {
            throw new IllegalArgumentException("检测状态异常");
        }
        return context;
    }


    /**
     * 显示悬浮窗
     */
    public void showMax() {
        curWindowStatus = WINDOW_STATUS_MAX;
        setFloatingWindowOpen();
        updateWindowDisplayMessage();
    }

    /**
     * 最小化悬浮窗
     */
    public void showMin() {
        curWindowStatus = WINDOW_STATUS_MIN;
        setFloatingWindowMin();
    }

    /**
     * 关闭悬浮窗
     */
    public void dismiss() {
        if (mContentView != null) {
            getWindowManager().removeView(mContentView);
        }
        TrafficMonitoringManager.stopPollingTask();
        release();
    }

    private void release() {
        mLayoutParams = null;
        mWindowManager = null;
        mContentView = null;
        floatingMaxView = null;
        floatingMinView = null;
        mNetStatusTv = null;
        mTrafficIncreasedTv = null;
        mNetSpeedTv = null;
        context = null;
    }


    /**
     * 悬浮窗口打开
     */
    public void setFloatingWindowOpen() {
        floatingMaxView = LayoutInflater.from(getContext()).inflate(R.layout.network_floating_window_max, null);
        setContentView();
    }


    /**
     * 悬浮窗口最小化
     */
    public void setFloatingWindowMin() {
        floatingMinView = LayoutInflater.from(getContext()).inflate(R.layout.network_floating_window_min, null);
        setContentView();
    }

    private void setContentView() {
        if (mContentView != null) {
            getWindowManager().removeView(mContentView);
        }
        createContentView();
    }

    /**
     * 配置布局View
     */
    private void createContentView() {
        mContentView = windowMax() ? floatingMaxView : floatingMinView;
        mContentView.setOnTouchListener(new WindowTouchListener());
        initWindowMaxChildView();
        addChildContentClickEvent();
        getWindowManager().addView(mContentView, getLayoutParams());
    }


    public WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        if (mLayoutParams == null) {
            mLayoutParams = new WindowManager.LayoutParams();
            initLayoutParams();
        }
        return mLayoutParams;
    }

    /**
     * 初始化布局参数
     */
    private void initLayoutParams() {
        getLayoutParams().flags = getLayoutParams().flags
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        getLayoutParams().dimAmount = 0.2f;
        getLayoutParams().type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        getLayoutParams().height = WindowManager.LayoutParams.WRAP_CONTENT;
        getLayoutParams().width = WindowManager.LayoutParams.WRAP_CONTENT;
        getLayoutParams().gravity = Gravity.LEFT | Gravity.TOP;
        getLayoutParams().format = PixelFormat.RGBA_8888;
        getLayoutParams().alpha = 1.0f;  // 设置窗口的透明度
        getLayoutParams().x = 100;
        getLayoutParams().y = 100;
    }


    /**
     * 更新移动后的窗口的位置
     */
    private void updateLocation(MotionEvent event) {
        getLayoutParams().x = (int) event.getRawX();
        getLayoutParams().y = (int) event.getRawY();
        getWindowManager().updateViewLayout(mContentView, getLayoutParams());
    }

    private boolean windowMin() {
        return curWindowStatus == WINDOW_STATUS_MIN;
    }

    private boolean windowMax() {
        return curWindowStatus == WINDOW_STATUS_MAX;
    }

    class WindowTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    updateLocation(event);
                    break;
//                case MotionEvent.ACTION_UP:
//                    if (windowMin()) {
//                        showMax();
//                    }
//                    break;
//                case MotionEvent.ACTION_OUTSIDE:
//                    if (windowMax()) {
//                        showMin();
//                    }
//                    break;
            }
            return false;
        }
    }

    private void initWindowMaxChildView() {
        mNetStatusTv = mContentView.findViewById(R.id.net_status);
        mTrafficIncreasedTv = mContentView.findViewById(R.id.traffic_increased);
        mNetSpeedTv = mContentView.findViewById(R.id.net_speed);
    }

    private void addChildContentClickEvent() {
        if (windowMax()) {
            View minBtn = mContentView.findViewById(R.id.btn_min);
            View closeBtn = mContentView.findViewById(R.id.btn_close);
            minBtn.setOnClickListener(clickListener);
            closeBtn.setOnClickListener(clickListener);
        }
        if (windowMin()) {
            View floatingMinContainer = mContentView.findViewById(R.id.floating_min_container);
            floatingMinContainer.setOnClickListener(clickListener);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickView(v);
        }
    };

    public void onClickView(View v) {
        int viewId = v.getId();
        if (viewId == R.id.floating_min_container) {
            showMax();
        } else if (viewId == R.id.btn_min) {
            showMin();
        } else if (viewId == R.id.btn_close) {
            dismiss();
        }
    }

    public void updateWindowDisplayMessage() {
        if (windowMin()) {
            return;
        }
        if (message == null) {
            message = new NetFloatingMessage(getContext(),period);
        }
        if (message.isFirstInitFlag()) {
            message.setFirstInitFlag(false);
            return;
        }
        message.calculaTraffic();
        mNetStatusTv.setText(CommonUtil.getNetStatus(context));
        mTrafficIncreasedTv.setText(message.getTotalTrafficIncreased());
        mNetSpeedTv.setText(message.getNetSpeed());
    }

}
