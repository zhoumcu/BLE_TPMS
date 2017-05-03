package com.example.sid_fu.blecentral.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;


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

    //遍历设置字体
    public static void changeViewSize(ViewGroup viewGroup,int screenWidth,int screenHeight) {//传入Activity顶层Layout,屏幕宽,屏幕高
        int adjustFontSize = adjustFontSize(screenWidth,screenHeight);
        for(int i = 0; i<viewGroup.getChildCount(); i++ ){
            View v = viewGroup.getChildAt(i);
            if(v instanceof ViewGroup){
                changeViewSize((ViewGroup)v,screenWidth,screenHeight);
            }else if(v instanceof Button){//按钮加大这个一定要放在TextView上面，因为Button也继承了TextView
                ( (Button)v ).setTextSize(adjustFontSize+2);
            }else if(v instanceof TextView){
                if(v.getId()== R.id.tv_title){//顶部标题
                    ( (TextView)v ).setTextSize(adjustFontSize+4);
                }else{
                    ( (TextView)v ).setTextSize(adjustFontSize);
                }
            }
        }
    }

    //获取字体大小
    public static int adjustFontSize(int screenWidth, int screenHeight) {
        screenWidth=screenWidth>screenHeight?screenWidth:screenHeight;
        /**
         * 1. 在视图的 onsizechanged里获取视图宽度，一般情况下默认宽度是320，所以计算一个缩放比率
         rate = (float) w/320   w是实际宽度
         2.然后在设置字体尺寸时 paint.setTextSize((int)(8*rate));   8是在分辨率宽为320 下需要设置的字体大小
         实际字体大小 = 默认字体大小 x  rate
         */
        int rate = (int)(5*(float) screenWidth/320); //我自己测试这个倍数比较适合，当然你可以测试后再修改
        return rate<15?15:rate; //字体太小也不好看的
    }
}
