package com.example.sid_fu.blecentral;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sid_fu.blecentral.utils.Logger;

/**
 * Created by Administrator on 2016/8/10.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.dbjtech.waiqin.destroy")) {
            //TODO
            //在这里写重新启动service的相关操作
            //startUploadService(context);
            Intent musicServiceIntent = new Intent(context, BluetoothLeStartService.class);
            context.startService(musicServiceIntent);
            Logger.e("不死线程");
        }
    }
}
