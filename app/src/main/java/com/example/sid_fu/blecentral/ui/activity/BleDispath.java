package com.example.sid_fu.blecentral.ui.activity;

import android.widget.ImageView;

import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.model.BleData;

/**
 * author：Administrator on 2017/6/14 16:31
 * company: xxxx
 * email：1032324589@qq.com
 */

public class BleDispath implements IBleDispath{

    @Override
    public void getDataTimeOutForDay(RecordData recordData, int state) {

    }

    @Override
    public void getDataTimeOutForNight(RecordData recordData, int state) {

    }

    @Override
    public void dicoverBlueDeviceForDay(String strAddress, String noticeStr, float date) {

    }

    @Override
    public void dicoverBlueDeviceForNight(String strAddress, String noticeStr, float date) {

    }

    @Override
    public void bleIsExceptionForDay(String strAddress, String noticeStr) {

    }

    @Override
    public void bleIsExceptionForNight(String strAddress, String noticeStr) {

    }

    @Override
    public void showDataForUI(String device, BleData bleData) {

    }

    @Override
    public void handleVoltageShow(ImageView img, float voltage) {

    }

    @Override
    public void setUnitTextSize() {

    }
}
