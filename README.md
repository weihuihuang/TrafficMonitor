# TrafficMonitor
简单的项目网络监测工具,记录自悬浮窗口打开之后，app的流量总消耗、网络状况、每秒消耗流量  
（1）用法：在需要开启的地方调用TrafficMonitoringManager.startPollingTask(getContext(), NET_POLLING_PRIOD_MILLIS);
     NET_POLLING_PRIOD_MILLIS是监测轮询周期   
（2）gradle添加依赖implementation 'com.github.weihuihuang:TrafficMonitor:19.2.0'使用  
（3）小米手机测试时请给app授予悬浮窗弹出权限
![image](https://github.com/weihuihuang/TrafficMonitor/blob/master/TrafficMonitor/1.jpg)
![image](https://github.com/weihuihuang/TrafficMonitor/blob/master/TrafficMonitor/2.jpg)
