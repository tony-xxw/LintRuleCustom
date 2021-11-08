package com.wynne.lint;

import android.util.Log;


/**
 * 日志工具
 * Created by LinHongHong on 2018/8/28 14:08
 * E-Mail Address：371655539@qq.com
 */
public final class LogUtils {
    private static boolean sShowLog = false;
    private static final int LOG_TYPE_D = 0;
    private static final int LOG_TYPE_I = 1;
    private static final int LOG_TYPE_E = 2;
    private static final int LOG_TYPE_W = 3;
    private static final int LOG_TYPE_V = 4;
    private static final int LOG_TYPE_F = 5;

    private static final int SEGMENT_SIZE = 3 * 1024;

    /**
     * debug日志
     * @param tag
     * @param objs
     */
    public static void d(String tag, Object... objs){
        log(LOG_TYPE_D, tag, objs);
    }

    /**
     * info
     * @param tag
     * @param objs
     */
    public static void i(String tag, Object... objs){
        log(LOG_TYPE_I, tag, objs);
    }

    /**
     * error
     * @param tag
     * @param objs
     */
    public static void e(String tag, Object... objs){
        log(LOG_TYPE_E, tag, objs);
    }

    /**
     * wraning
     * @param tag
     * @param objs
     */
    public static void w(String tag, Object... objs){
        log(LOG_TYPE_W, tag, objs);
    }

    /**
     * verbose
     * @param tag
     * @param objs
     */
    public static void v(String tag, Object... objs){
        log(LOG_TYPE_V, tag, objs);
    }

    /**
     * 抛出异常
     * @param e
     */
    public static void throwsException(Throwable e){
        if(sShowLog){
            e.printStackTrace();
        }
    }

    /**
     * 是否显示日志
     * @param showLog
     */
    public static void setShowLog(boolean showLog){
        sShowLog = showLog;
    }

    public static void f(String tag, Object... objs) {
        log(LOG_TYPE_F, tag, objs);
    }

    /**
     * 日志总入口
     * @param logType
     * @param tag
     * @param objs
     */
    private static void log(int logType, String tag, Object... objs){
        if(!sShowLog){
            return;
        }
        String msg =  formatObjectsByLn(objs);
        switch (logType){
            case LOG_TYPE_D:
                Log.d(tag, msg);
                break;
            case LOG_TYPE_E:
                Log.e(tag, msg);
                break;
            case LOG_TYPE_I:
                Log.i(tag, msg);
                break;
            case LOG_TYPE_V:
                Log.v(tag, msg);
                break;
            case LOG_TYPE_W:
                Log.w(tag, msg);
                break;
            case LOG_TYPE_F:
                //来自视频组的特别需求，打印超长的log

                long length = msg.length();
                if (length <= SEGMENT_SIZE) {// 长度小于等于限制直接打印
                    Log.w(tag, msg);
                } else {
                    while (msg.length() > SEGMENT_SIZE) {// 循环分段打印日志
                        String logContent = msg.substring(0, SEGMENT_SIZE);
                        msg = msg.replace(logContent, "");
                        Log.e(tag, logContent);
                    }
                    Log.w(tag, msg);// 打印剩余日志
                }
                break;
        }
    }

    public  static String formatObjectsByLn(Object... objs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objs) {
            stringBuilder.append(object.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
