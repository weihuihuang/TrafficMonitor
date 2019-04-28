package com.splendid.floatingwindowlibrary.model;

import android.content.Context;

import com.splendid.floatingwindowlibrary.util.CommonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

/**
 * Created by WeiHuiHuang on 2019/4/26.  针对本应用流量统计相关数值信息
 */
public class NetFloatingMessage {

    private long receiveTraffic;   //接收流量

    private long sendTraffic;      //发送流量

    private long totalTrafficFirstInit; //第一次计算得到的流量总数

    private long totalTrafficIncreased; //开启悬浮窗后流量总增量

    private long changePerSecond;       //每秒消耗增量

    private int uid = -1;

    private boolean firstInitFlag;

    private final static int M_UNIT_BYTE = 1024 * 1024;

    private final static int KB_UNIT_BYTE = 1024;

    private final static int INVALID_UID = -1;

    private Context context;

    private int period;

    public NetFloatingMessage(Context context, int period) {
        this.context = context;
        this.period = period;
    }

    public boolean isFirstInitFlag() {
        return firstInitFlag;
    }

    public void setFirstInitFlag(boolean firstInitFlag) {
        this.firstInitFlag = firstInitFlag;
    }

    public void calculaTraffic() {
        if (uid == INVALID_UID) {
            uid = CommonUtil.getUid(context);
        }
        RandomAccessFile rafReceive = null, rafSend = null;
        String pathReceive = "/proc/uid_stat/" + uid + "/tcp_rcv";
        String pathSend = "/proc/uid_stat/" + uid + "/tcp_snd";
        try {

            rafReceive = new RandomAccessFile(pathReceive, "r");
            receiveTraffic = Long.parseLong(rafReceive.readLine());

            rafSend = new RandomAccessFile(pathSend, "r");
            sendTraffic = Long.parseLong(rafSend.readLine());

        } catch (FileNotFoundException ignore) {

        } catch (IOException ignore) {

        } finally {
            try {
                if (rafReceive != null)
                    rafReceive.close();
                if (rafSend != null)
                    rafSend.close();
            } catch (IOException ignore) {
            }
        }
        long totalTraffic = receiveTraffic + sendTraffic;
        if (totalTrafficFirstInit == 0) {
            totalTrafficFirstInit = totalTraffic;
        }
        long totalTrafficIncreasedBefore = totalTrafficIncreased;
        totalTrafficIncreased = totalTraffic - totalTrafficFirstInit;
        changePerSecond = (totalTrafficIncreased - totalTrafficIncreasedBefore) * 1000 / period;
    }

    public String getNetSpeed() {
        if (changePerSecond == 0) {
            return "0kb/s";
        }
        //int kb = (int) Math.floor(bytes / 1024 + 0.5);
        double kb = changePerSecond > M_UNIT_BYTE ? (double) changePerSecond / M_UNIT_BYTE : (double) changePerSecond / KB_UNIT_BYTE;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + getUnit(changePerSecond) + "/s";
    }

    public String getTotalTrafficIncreased() {
        if (totalTrafficIncreased == 0) {
            return "0kb";
        }
        double kb = totalTrafficIncreased > M_UNIT_BYTE ? (double) totalTrafficIncreased / M_UNIT_BYTE : (double) totalTrafficIncreased / KB_UNIT_BYTE;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + getUnit(totalTrafficIncreased);
    }

    private String getUnit(long value) {
        return value > M_UNIT_BYTE ? "M" : "kb";
    }

}
