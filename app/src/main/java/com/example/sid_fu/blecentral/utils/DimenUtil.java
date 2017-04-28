package com.example.sid_fu.blecentral.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.sid_fu.blecentral.App;


/**
 * pd、px、sp互转
 *
 * @author JiangPing
 */
public class DimenUtil {

    private static DisplayMetrics mDisplayMetrics;

    /**
     * 获取屏幕的宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = App.getInstance().getResources().getDisplayMetrics();
        }
        return mDisplayMetrics.widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    public static int getScreenHeight() {
        if (mDisplayMetrics == null) {
            mDisplayMetrics = App.getInstance().getResources().getDisplayMetrics();
        }
        return mDisplayMetrics.heightPixels;
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        try {
            final float scale = App.getInstance().getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        try {
            final float scale = App.getInstance().getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        final float fontScale = App.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = App.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据图片原始宽度和调整后的宽度得到调整后的高度
     *
     * @param originalWidth  原始宽度
     * @param originalHeight 原始高度
     * @param adjustWidth    调整后的宽度
     * @return 调整后的高度
     */
    public static int getAdjustHeight(int originalWidth, int originalHeight,
                                      int adjustWidth) {
        float temp = (float) originalWidth / (float) adjustWidth;
        int adjustHeight = (int) ((float) originalHeight / temp);
        return adjustHeight;
    }

    /**
     * 按比例获取长度
     * <p/>
     * refSize /result == refRatio/resultRadio
     *
     * @param refSize     参考长度
     * @param refRatio    参考比例
     * @param resultRadio 结果值比例
     * @return
     */
    public static int getSizeByScale(int refSize, int refRatio, int resultRadio) {
        return (int) ((float) refSize * resultRadio / refRatio);
    }

    public static float getDimension(int resId) {
        return App.getInstance().getResources().getDimension(resId);
    }

    public static int adjustFontSize(Context context){
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        if (screenWidth <= 240) {        // 240X320 屏幕
            return 10;
        }else if (screenWidth <= 320){   // 320X480 屏幕
            return 14;
        }else if (screenWidth <= 480){   // 480X800 或 480X854 屏幕
            return 24;
        }else if (screenWidth <= 540){   // 540X960 屏幕
            return 26;
        }else if(screenWidth <= 800){    // 800X1280 屏幕
            return 30;
        }else{                          // 大于 800X1280
            return 35;
        }
    }
}
