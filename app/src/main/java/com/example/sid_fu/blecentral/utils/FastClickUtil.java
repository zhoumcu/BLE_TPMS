package com.example.sid_fu.blecentral.utils;

/**
 * 快速点击辅助类
 * Created by CHEN on 2015/6/3.
 */
public class FastClickUtil {

    private static long lastClickTime;

    /**
     * 判断快速点击
     *
     * @return true无效操作
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 判断快速点击
     *
     * @param limit 有效点击时间，单位ms
     * @return true无效操作
     */
    public static boolean isFastClick(long limit) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < limit) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
