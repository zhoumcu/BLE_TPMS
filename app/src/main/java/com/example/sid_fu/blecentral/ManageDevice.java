package com.example.sid_fu.blecentral;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/4/16.
 */
public class ManageDevice implements Serializable{
    private  BluetoothLeService mBluetoothLeService;
    private  Context context;

    public int disLeftFCount = 0;
    public int disRightFCount = 0;
    public int disLeftBCount = 0;
    public int disRightBCount = 0;

    public boolean leftF_notify;
    public boolean rightF_notify ;
    public boolean leftB_notify ;
    public boolean rightB_notify ;

    public  String leftFDevice ;
    public  String rightFDevice ;
    public  String leftBDevice ;
    public  String rightBDevice ;

    public String leftF_preContent;
    public String rightF_preContent ;
    public String leftB_preContent ;
    public String rightB_preContent ;

    public String getLeftBDevice() {
        return leftBDevice;
    }

    public void setLeftBDevice(String leftBDevice) {
        this.leftBDevice = leftBDevice;
    }

    public String getRightBDevice() {
        return rightBDevice;
    }

    public void setRightBDevice(String rightBDevice) {
        this.rightBDevice = rightBDevice;
    }

    public String getRightFDevice() {
        return rightFDevice;
    }

    public void setRightFDevice(String rightFDevice) {
        this.rightFDevice = rightFDevice;
    }

    public String getLeftFDevice() {
        return leftFDevice;
    }

    public void setLeftFDevice(String leftFDevice) {
        this.leftFDevice = leftFDevice;
    }


//    String MON = "初始状态";
//    String TUE = "正常";
//    String WED = "快漏气";
//    String THU = "慢漏气";
//    String FRI = "加气";
//    String SAT = "保留";
//    String SUN = "异常，传感器未能获取胎压信息";
    public static enum  status{
        正常(0),加气(1), 慢漏气(2),保留3(3),快漏气(4),保留5(5),保留6(6),保留7(7),保留(8);
        private final int mValue;
        status(int value)
        {
            mValue = value;
        }
    }

    public ManageDevice(){

    }
    public ManageDevice(Context context,BluetoothLeService mBluetoothLeService){
        this.context = context;
        this.mBluetoothLeService = mBluetoothLeService;
    }
    public static boolean isEquals(List<MyBluetoothDevice> mList,BluetoothDevice add) {
        for (MyBluetoothDevice ble : mList) {
            if(ble.getDevice().equals(add)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isBundConfigEquals(List<MyBluetoothDevice> mList,BluetoothDevice add) {
        for (MyBluetoothDevice ble : mList) {
            if(ble.getDevice().getAddress().equals(add.getAddress())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isConfigEquals(List<BluetoothDevice> mList,BluetoothDevice add) {
        for (BluetoothDevice ble : mList) {
            if(ble.getAddress().equals(add.getAddress())) {
                return true;
            }
        }
        return false;
    }
    public static void setSuccess(BluetoothDevice device,MyBluetoothDevice status) {
        if(device.equals(status.getDevice())) {
            status.setSuccessComm(true);
        }
    }
    public static void setFailed(BluetoothDevice device,MyBluetoothDevice status) {
        if(device.equals(status.getDevice())) {
            status.setRequestConnect(false);
            status.setSuccessComm(false);
            status.setBluetoothGatt(null);
        }
    }
}
