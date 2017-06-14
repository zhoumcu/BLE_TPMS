package com.example.sid_fu.blecentral.ui.activity;

import android.widget.ImageView;

import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.model.BleData;

/**
 * author：Administrator on 2017/6/14 16:28
 * company: xxxx
 * email：1032324589@qq.com
 */

public interface IBleDispath {
    /**
     * 白天模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    void getDataTimeOutForDay(RecordData recordData, int state);
    /**
     * 夜间模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    void getDataTimeOutForNight(RecordData recordData, int state);
    /**
     * 白天模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    void dicoverBlueDeviceForDay(String strAddress, String noticeStr, float date);
    /**
     * 夜间模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    void dicoverBlueDeviceForNight(String strAddress, String noticeStr, float date);
    /**
     * 白天模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    void bleIsExceptionForDay(String strAddress,String noticeStr);
    /**
     * 夜间模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    void bleIsExceptionForNight(String strAddress,String noticeStr);
    /**
     *  数据显示
     * @param device
     * @param bleData
     */
    void showDataForUI(String device , BleData bleData);
    /**
     * 电池变化情况指示
     * @param img
     * @param voltage
     */
    void handleVoltageShow(ImageView img, float voltage);

    /**
     * 修改显示字体大小
     */
    void setUnitTextSize();
}
