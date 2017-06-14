    /*
    * Copyright (C) 2013 The Android Open Source Project
    *
    * Licensed under the Apache License, Version 2.0 (the "License");
    * you may not use this file except in compliance with the License.
    * You may obtain a copy of the License at
    *
    *      http://www.apache.org/licenses/LICENSE-2.0
    *
    * Unless required by applicable law or agreed to in writing, software
    * distributed under the License is distributed on an "AS IS" BASIS,
    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    * See the License for the specific language governing permissions and
    * limitations under the License.
    */

    package com.example.sid_fu.blecentral.service;

    import android.app.Notification;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.content.IntentFilter;
    import android.net.Uri;
    import android.os.Bundle;
    import android.os.Handler;
    import android.provider.MediaStore;

    import com.example.sid_fu.blecentral.App;
    import com.example.sid_fu.blecentral.R;
    import com.example.sid_fu.blecentral.model.SampleGattAttributes;
    import com.example.sid_fu.blecentral.widget.SystemDialog;
    import com.example.sid_fu.blecentral.db.entity.Device;
    import com.example.sid_fu.blecentral.helper.DataHelper;
    import com.example.sid_fu.blecentral.model.BleData;
    import com.example.sid_fu.blecentral.utils.Constants;
    import com.example.sid_fu.blecentral.utils.Logger;
    import com.example.sid_fu.blecentral.utils.SharedPreferences;
    import com.example.sid_fu.blecentral.widget.NotificationReceiver;

    import java.util.Timer;
    import java.util.TimerTask;

    /**
    * Service for managing connection and data communication with a GATT server hosted on a
    * given Bluetooth LE device.
    */
    public class BluetoothLeStartService extends BaseBluetoothLeService {
    private final static String TAG = BluetoothLeStartService.class.getSimpleName();
    private Notification messageNotification;
    private NotificationManager messageNotificatioManager;
    private PendingIntent messagePendingIntent;
    // 通知栏消息
    private int messageNotificationID = 1000;
    private Device deviceDetails;
    private Timer timer;
    private boolean isSend;
    private Handler mHandler = new Handler();
    private int deviceId;
    private boolean mScanning;

        @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isSend = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        },1000,20000);
        Logger.e("onceate service");
        initBroacast();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    private void initBroacast(){
        // 初始化
        messageNotification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_logo)
                .setTicker("小安科技:" + "您有新短消息，请注意查收！")
                .setContentTitle("小安胎压监测系统")
                .setContentText("轮胎异常,请及时处理！")
                .setLights(0xff0000ff, 300, 0)
                //获取默认铃声
                .setDefaults(Notification.DEFAULT_SOUND)
                //获取自定义铃声
                //.setSound(Uri.parse("file:///sdcard/xx/xx.mp3"))
                //获取Android多媒体库内的铃声
                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "5"))
                .setVibrate(new long[] {0,300,500,700})
                .setContentIntent(messagePendingIntent).setNumber(1).build();
        messageNotification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //设置点击通知栏的动作为启动另外一个广播
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED); // 关键的一步，设置启动模式
        messagePendingIntent = PendingIntent.
                getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleGattAttributes.ACTION_STOP_SCAN);
        intentFilter.addAction(SampleGattAttributes.ACTION_START_SCAN);
        intentFilter.addAction(SampleGattAttributes.ACTION_REFREASH_DEVICE);
        return intentFilter;
    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (SampleGattAttributes.ACTION_STOP_SCAN.equals(action)) {
               mBluetoothAdapter.stopLeScan(mLeScanCallback);
                Logger.i("stop service BluetoothAdapter");
            }else  if (SampleGattAttributes.ACTION_START_SCAN.equals(action)) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Logger.i("start service BluetoothAdapter");
            }else  if (SampleGattAttributes.ACTION_REFREASH_DEVICE.equals(action)) {
                refreash(intent.getExtras().getInt("id"));
                Logger.i("start service BluetoothAdapter");
            }
        }
    };

    private void refreash(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceDetails = null;
                //刷新数据库
                Logger.e("onStartCommand service:"+id);
                deviceDetails = App.getDeviceDao().get(id);
                if(deviceDetails==null) return;
                SharedPreferences.getInstance().putString(Constants.LoW_PRESS, String.valueOf(deviceDetails.getBackMinValues()));
                SharedPreferences.getInstance().putString(Constants.HIGH_PRESS, String.valueOf(deviceDetails.getBackmMaxValues()));
                SharedPreferences.getInstance().putString(Constants.HIGH_TEMP, String.valueOf(deviceDetails.getFromMinValues()));
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deviceId = SharedPreferences.getInstance().getInt(Constants.LAST_DEVICE_ID, 0);
        refreash(deviceId);
        initialize();
        iniBle();
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        Intent intent = new Intent("com.dbjtech.waiqin.destroy");
        sendBroadcast(intent);
        Logger.e("service close!");
        unregisterReceiver(mGattUpdateReceiver);
    }

    public void showDialog() {
        Intent it =new Intent(this,SystemDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

    private void iniBle() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanLeDevice(true);
                }
            },1000);
        }else {
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            App.getInstance().speak("正在打开蓝牙设备");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //scanBleForResult(device);
            // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
            /*if (mScanning == true) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Logger.i("后台接收广播数据："+device.getAddress()+"isAppOnForeground:"+SharedPreferences.getInstance().getBoolean("isAppOnForeground",false));
                    //如何应用退至后台，发送异常通知
                    if(SharedPreferences.getInstance().getBoolean("isAppOnForeground",false)) {
                        if(deviceDetails==null) return;
                        if(isNull(deviceDetails.getLeft_BD(),device.getAddress())||isNull(deviceDetails.getLeft_FD(),device.getAddress())||
                                isNull(deviceDetails.getRight_BD(),device.getAddress())||isNull(deviceDetails.getRight_FD(),device.getAddress())){
                            bleStringToDouble(device,true,scanRecord);
                            Logger.i("后台接收===================================="+device.getAddress());
                        }
                    }else{
                        if(deviceDetails==null) return;
                        if(deviceDetails.getDefult()==null) return;
                        if(deviceDetails.getDefult().equals("true")){
                            Logger.i("发送数据到前台显示===================================="+device.getAddress());
                            broadcastUpdate(SampleGattAttributes.SCAN_FOR_RESULT,device,rssi,scanRecord);
                        }
                    }
                }
            }).start();
        }
    };
    private boolean isNull(String device,String str) {
        if(device==null) return false;
        if(device.equals(str)) {
            return true;
        }
        return false;
    }
    private void broadcastUpdate(String action,  BluetoothDevice device, int rssi, byte[] scanRecord) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", device);
        intent.putExtra("SCAN_RSSI", rssi);
        intent.putExtra("SCAN_RECORD", scanRecord);
        sendBroadcast(intent);
    }
    private void bleStringToDouble(BluetoothDevice device, boolean isNotify,byte[] data) {
        BleData bleData = DataHelper.getScanData(data);
        if(device.getAddress().equals(deviceDetails.getLeft_BD())) {
            handleException(bleData, deviceDetails.getLeft_BD());
        }else if(device.getAddress().equals(deviceDetails.getRight_BD())) {
            handleException(bleData,deviceDetails.getRight_BD());
        }else if(device.getAddress().equals(deviceDetails.getLeft_FD())) {
            handleException(bleData, deviceDetails.getLeft_FD());
        }else if(device.getAddress().equals(deviceDetails.getRight_FD())) {
            handleException(bleData, deviceDetails.getRight_FD());
        }
    }
    private void handleException(BleData date, String str) {
        if (date.isError()) {
            //高压
            Logger.i("server 异常发送通知！");
            if(!isSend) {
                isSend = true;
                messageNotificatioManager.notify(messageNotificationID, messageNotification);
                Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("id", deviceId);
                broadcastIntent.putExtras(mBundle);
                sendBroadcast(broadcastIntent);
            }
        }
    }
}
