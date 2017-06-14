package com.example.sid_fu.blecentral.helper;

import com.example.sid_fu.blecentral.model.ManageDevice;
import com.example.sid_fu.blecentral.model.BleData;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;

import java.text.DecimalFormat;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：Administrator on 2016/9/28 08:54
 * company: xxxx
 * email：1032324589@qq.com
 */

public class DataHelper {

    public DataHelper() {

    }

    public static BleData getScanData(byte[] scanRecord){
        Logger.e(":"+ DataUtils.bytesToHexString(scanRecord));
        byte[] data = DataUtils.parseData(scanRecord).datas;
        return getData(data);
    }

    public static BleData getData(byte[] data){
        float voltage = 0.00f,press = 0, temp =0;
        int temp1 = 0,state = 0,rssi = 0;
        String pressStr = "";
        DecimalFormat df = new DecimalFormat("#0.0#");
        BleData bleData = new BleData();
        if(data==null) return bleData;
        if(data.length==0) return bleData;
        if(data.length==4) {
            voltage = ((float)(DigitalTrans.byteToAlgorism(data[3])-31)*20/21+160)/100;
            press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            temp = (float)(DigitalTrans.byteToAlgorism(data[2])-50);
            state = DigitalTrans.byteToBin0x0F(data[0]);
            press = Math.round(press*10)*0.1f;
            if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
                pressStr = df.format(press);
            }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
                press = Math.round(press*102*10)*0.1f;
                pressStr = df.format(press);
            }else{
                press = Math.round(press*14.5f*10)*0.1f;
                pressStr = df.format(press);
            }

            if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
                temp1 = (int)temp;
            }else {
                temp1 = (int)(temp*1.8f)+32;
            }
        }else if(data.length==9) {
            voltage = ((float)(DigitalTrans.byteToAlgorism(data[3])-31)*20/21+160)/100;
            press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            temp = (float)(DigitalTrans.byteToAlgorism(data[2])-50);
            state = DigitalTrans.byteToBin0x0F(data[0]);
            press = Math.round(press*10)*0.1f;
            if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
                pressStr = df.format(press);
            }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
                press = Math.round(press*102*10)*0.1f;
                pressStr = df.format(press);
            }else{
                press = Math.round(press*14.5f*10)*0.1f;
                pressStr = df.format(press);
            }
            if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
                temp1 = (int)temp;
            }else {
                temp1 = (int)(temp*1.8f)+32;
            }
        }
        Logger.e("状态："+state+"\n"+"压力值："+press+"\n"+"温度："+temp+"\n"+"电压"+voltage+"");
        //计算异常情况
        bleData.setTemp (temp1);
        bleData.setPress(press);
        bleData.setStringPress(pressStr);
        bleData.setStatus(state);
        bleData.setVoltage(voltage);
        bleData.setData(DigitalTrans.byte2hex(data));
        return handleException(bleData);
    }

    private static BleData handleException(BleData date) {
        StringBuffer buffer = new StringBuffer();
        float maxPress, minPress, maxTemp;
        if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Bar")) {
            maxPress = Constants.getHighPressValue();
            minPress = Constants.getLowPressValue();
        }else if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
            maxPress = Constants.getHighPressKpaValue();
            minPress = Constants.getLowPressKpaValue();
        }else{
            maxPress = Constants.getHighPressPsiValue();
            minPress = Constants.getLowPressPsiValue();
        }
        if(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃").equals("℃")) {
            maxTemp = Constants.getHighTempValue();
        }else {
            maxTemp = Constants.getHighTempFValue();
        }
        Logger.e("maxPress" + maxPress + "minPress" + minPress + "maxTemp" + maxTemp);
        buffer.append(date.getPress() > maxPress ? "高压" + " " : "");
        buffer.append(date.getPress() < minPress ? "低压" + " " : "");
        buffer.append(date.getTemp() > maxTemp ? "高温" + " " : "");
//        buffer.append(date.getTemp() < 20 ? "低温" + " " : "");
        ManageDevice.status[] statusData = ManageDevice.status.values();
        //状态检测
        if (date.getStatus() == 8 || date.getStatus() == 0) {

        } else {
            buffer.append(statusData[date.getStatus()] + " ");
        }
        if (buffer.toString().contains("快漏气")||date.getPress() > maxPress || date.getPress() < minPress || date.getTemp() > maxTemp ? true: false) {
            //高压
            date.setIsError(true);
        }else {
            //正常
            date.setIsError(false);
        }
        date.setErrorState(buffer.toString());
        return date;
    }
    public static Observable<BleData> getBleData(final byte[] data) {
        return Observable.create(new Observable.OnSubscribe<BleData>() {
            @Override
            public void call(Subscriber<? super BleData> subscriber) {
                subscriber.onNext(getScanData(data));
            }
        });
    }
    private <T> void toSubscribe(Observable<T> o,Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }
}
