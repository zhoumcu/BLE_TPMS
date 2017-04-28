package com.example.sid_fu.blecentral.model;

import com.example.sid_fu.blecentral.utils.Logger;

/**
 * author：Administrator on 2016/10/25 09:01
 * company: xxxx
 * email：1032324589@qq.com
 */

public class ConfigValues {

    public static float getLowPressKpaValue(float f){
        return f*102f;
    }
    public static float getHighPressKpaValue(float f){
        return f*102f;
    }
    public static int getHighTempFValue(float f){
        return (int)(f*1.80f)+32;
    }
    public static float getLowPressPsiValue(float f){
        return f*14.5f;
    }
    public static float getHighPressPsiValue(float f){
        return f*14.5f;
    }

    public static int getLowPressProgress(float f){
        if(f==1.7f) return 0;
        return (int) ((f-1.6)*10);
    }
    public static int getHighPressProgress(float f){
        Logger.e(String.valueOf(f));
        if(f==2.7f) return 0;
        if(f==4.0f) return 13;
        return (int)((f-2.7)*10);
    }
    public static int getLowTempProgress(int f){
        return f-50;
    }
}
