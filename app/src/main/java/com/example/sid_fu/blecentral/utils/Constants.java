package com.example.sid_fu.blecentral.utils;

/**
 * Created by Bob on 2015/4/17.
 */
public class Constants {

    public static final String LEFT_F_DEVICE = "leftF";
    public static final String RIGHT_F_DEVICE = "rightF";
    public static final String LEFT_B_DEVICE = "leftB";
    public static final String RIGHT_B_DEVICE = "rightB";
    public static final String FIRST_CONFIG = "first_config";
    public static final String FIRST_USED_LF = "first_used_lf";
    public static final String FIRST_USED_RF = "first_used_rf";
    public static final String FIRST_USED_LB = "first_used_lb";
    public static final String FIRST_USED_RB = "first_used_rb";

    public static final String USED ="AT+USED=1";
    public static final String UN_USED ="AT+USED=0";

    public static final String RSSI_ON ="AT+RSSI=ON";
    public static final String RSSI_OFF ="AT+RSSI=OFF";

    public static final boolean IS_TEST = false;

    public static final int LEFT_F = 1;
    public static final int RIGHT_F = 2;
    public static final int LEFT_B = 3;
    public static final int RIGHT_B = 4;

    public static final String TEMP_DW = "temp_danwei";
    public static final String PRESSUER_DW = "pressuer_danwei";
    public static final boolean SINGLE_BLE = true;
    public static final String DAY_NIGHT = "dayornight";

    public static final float vol = 3.5f;
    public static final String LANDORPORT = "land_or_port";
    public static final String DEFIED = "竖屏";
    public static final String LAST_DEVICE_ID = "LAST_DEVICE_ID";

    public static final int MYSQL_DEVICE_ID = 10;
    public static final String IS_CONFIG = "is_config_device";
    public static final String MY_CAR_DEVICE = "my_car_device";
    public static final String PRESSUER_DW_NUM = "PRESSUER_DW_NUM";
    public static final String TEMP_DW_NUM = "TEMP_DW_NUM";
    public static final boolean NO_SHOW_VOL = true;
    public static final String HIGH_PRESS = "high_press";
    public static final String HIGH_TEMP = "high_temp";
    public static final String LoW_PRESS = "low_press";
    public static final String IS_LOGIN = "is_login";

    public static float getLowPressValue(){
        return Float.valueOf(SharedPreferences.getInstance().getString(Constants.LoW_PRESS,"1.8"));
    }
    public static float getHighPressValue(){
        return Float.valueOf(SharedPreferences.getInstance().getString(Constants.HIGH_PRESS,"3.2"));
    }
    public static int getHighTempValue(){
        return Integer.valueOf(SharedPreferences.getInstance().getString(Constants.HIGH_TEMP,"65"));
    }

    public static float getLowPressKpaValue(){
        return getLowPressValue()*102f;
    }
    public static float getHighPressKpaValue(){
        return getHighPressValue()*102f;
    }
    public static int getHighTempFValue(){
        return (int)(getHighTempValue()*1.80f)+32;
    }
    public static float getLowPressPsiValue(){
        return getLowPressValue()*14.5f;
    }
    public static float getHighPressPsiValue(){
        return getHighPressValue()*14.5f;
    }

    public static int getLowPressProgress(){
        if(getLowPressValue()==2.0){
            return 4;
        }
        return (int)((getLowPressValue()*10)%10)-6;
    }
    public static int getHighPressProgress(){
        return (int)((getHighPressValue()*10)%10);
    }
    public static int getLowTempProgress(){
        if(getHighTempValue()==70){
            return 10;
        }
        return getHighTempValue()%10;
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
