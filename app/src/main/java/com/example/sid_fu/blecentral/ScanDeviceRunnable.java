package com.example.sid_fu.blecentral;

import android.os.Handler;
import android.os.Message;

/**
 * Created by sid-fu on 2016/5/18.
 */
public class ScanDeviceRunnable implements Runnable {

    private final int type;
    private final Handler handler;

    public ScanDeviceRunnable(Handler handler, int type) {
        this.type = type;
        this.handler = handler;
    }
    @Override
    public void run() {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = type;
        handler.sendMessage(msg);
    }
}
