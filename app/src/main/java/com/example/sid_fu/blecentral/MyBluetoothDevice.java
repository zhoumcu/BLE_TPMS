package com.example.sid_fu.blecentral;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.example.sid_fu.blecentral.utils.Logger;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/4/18.
 */
public class MyBluetoothDevice implements Serializable{

    private static final long serialVersionUID = -6298516694275121291L;

    public static BluetoothGattCharacteristic gattCharacteristic_char1 = null;
    public static BluetoothGattCharacteristic gattCharacteristic_char5 = null;

    public MyBluetoothDevice(BluetoothDevice device) {
        this.device = device;
    }

    public  BluetoothGattCharacteristic getGattCharacteristic_char6() {
        return gattCharacteristic_char6;
    }

    public  void setGattCharacteristic_char6(BluetoothGattCharacteristic gattCharacteristic_char6) {
        this.gattCharacteristic_char6 = gattCharacteristic_char6;
        Logger.e("设置gattCharacteristic_char6："+getDevice().getAddress()+gattCharacteristic_char6.toString()+gattCharacteristic_char6.getUuid().toString());
    }

    public  BluetoothGattCharacteristic gattCharacteristic_char6 = null;
    public static BluetoothGattCharacteristic gattCharacteristic_heartrate = null;
    public static BluetoothGattCharacteristic gattCharacteristic_keydata = null;
    public static BluetoothGattCharacteristic gattCharacteristic_temperature = null;
    public static BluetoothGattCharacteristic gattCharacteristic_0xffa6 = null;

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    private BluetoothAdapter mBluetoothAdapter;

    private  BluetoothLeService mBluetoothLeService;
    /**
     * 请求连接
     */
    private boolean requestConnect;

    public int getRequesetConnectTimeout() {
        return requesetConnectTimeout;
    }

    public void setRequesetConnectTimeout(int requesetConnectTimeout) {
        this.requesetConnectTimeout = requesetConnectTimeout;
    }
    /**
     * 请求连接超时
     */
    public int sendGetTime = 3;
    /**
     * 请求连接超时
     */
    private int requesetConnectTimeout = 2;
    /**
     * 连接成功
     */
    private boolean connected;
    /**
     * 发现的设备
     */
    private BluetoothDevice device;
    /**
     * 正在连接中
     */
    private boolean connecting;

    /**
     * 成功通讯
     */
    private boolean successComm;

    public boolean isTimeOutDisconnect() {
        return timeOutDisconnect;
    }

    public void setTimeOutDisconnect(boolean timeOutDisconnect) {
        this.timeOutDisconnect = timeOutDisconnect;
    }
    private boolean timeOutDisconnect;
    /**
     * 异常状态：
     *  0   正常
     *  1   断开
     *  2   发现服务但通讯不正常
     *  3   连接超时
     */
    public int errorStatus;
    public final int OK_STATUS = 200;
    public final int DISCONNECT_STATUS = 201;
    public final int FOUNDGATT_NOCOMM_STATUS = 202;
    public final int CONNECT_TIMEOUT_STATUS = 203;
    /**
     * 连接超时时间间隔
     */
    public final int CONNECT_TIMEOUT = 10000;
    /**
     * 记录连接次数
     */
    public int connectTime;

    /**
     * 是否扫描到该设备
     */
    public boolean bleScaned;

    public boolean isBleScaned() {
        return bleScaned;
    }

    public void setBleScaned(boolean bleScaned) {
        this.bleScaned = bleScaned;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public boolean timeout;
    /**
     * 发送数据与否
     */
    public boolean isSend;
    /**
     *  连接上的设备服务
     */
    public BluetoothGatt bluetoothGatt;
    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }


    public static MyBluetoothDevice getInstance(BluetoothDevice device,
                                                BluetoothLeService mBluetoothLeService)
    {
        return new MyBluetoothDevice(device,mBluetoothLeService);
    }
    public static MyBluetoothDevice getInstance(BluetoothDevice device)
    {
        return new MyBluetoothDevice(device);
    }
    public MyBluetoothDevice(BluetoothDevice device, BluetoothLeService mBluetoothLeService) {
        this.device = device;
        this.mBluetoothLeService = mBluetoothLeService;
    }
    public boolean isSuccessComm() {
        return successComm;
    }

    public void setSuccessComm(boolean successComm) {
        this.successComm = successComm;
    }
    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }


    public boolean isRequestConnect() {
        return requestConnect;
    }

    public void setRequestConnect(boolean requestConnect) {
        this.requestConnect = requestConnect;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    /**
     * 连接蓝牙设备
     */
    public void connectBle(MyBluetoothDevice bluetoothDevice) {

        while (true) {

            if (mBluetoothLeService != null) {
                Logger.e("try to connect...:" +device.getAddress());
                this.setRequestConnect(true);
                mBluetoothLeService.connect(device.getAddress(),bluetoothDevice);
                //开启一个定时器5s，判断申请连接是否超时
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(!isSuccessComm())
                        {
                            setRequestConnect(false);
                            setTimeout(true);
                            Logger.e("初次连接超时，取消请求标志"+device.getAddress());
                        }
                    }
                },30000);
                break;
            } else {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void redictConnect(MyBluetoothDevice bluetoothDevice)
    {

    }
    /**
     * 失败或者断开，重新蓝牙设备
     */
    public void reConnectBle(MyBluetoothDevice bluetoothDevice) {
        //已经连接
        Logger.e("已经连接:"+getDevice().getAddress()+":"+isSuccessComm());
        if(isSuccessComm()) return;
        //已经申请连接
        Logger.e("申请连接:"+getDevice().getAddress()+":"+isRequestConnect());
        if(isRequestConnect()) return;
        //设置已经发起申请连接
        this.setTimeout(false);
        Logger.e("发起连接："+device.getAddress());

        //判断该设备是否已经存在gatt服务
        if(getBluetoothGatt()!=null&&getBluetoothGatt().getDevice().getAddress().equals(device.getAddress()))
        {

            //判断请求次数，如果服务存在次数超时，仍然未能连接，极有可能是设备未断开原因导致，因此先断开设备，再连接
            requesetConnectTimeout--;
            if(requesetConnectTimeout==0)
            {
                Logger.e("连接超时，先断开服务，再重新连接"+device.getAddress());
                requesetConnectTimeout=2;
                if(isSuccessComm()) return;
                this.getBluetoothGatt().disconnect();
                mBluetoothLeService.removeConnectionQueue(getBluetoothGatt());
                this.setTimeOutDisconnect(true);
                this.getBluetoothGatt().close();
                this.setBluetoothGatt(null);
                this.connectBle(bluetoothDevice);
                return;
            }
            //已经存在服务
            //开启一个定时器5s，判断申请连接是否超时
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!isSuccessComm())
                    {
                        setRequestConnect(false);
                        setTimeout(true);
                        Logger.e("连接超时，取消请求标志"+device.getAddress());
                    }
                }
            },10000);
            Logger.e("已经存在服务,直接连接"+device.getAddress());
            this.getBluetoothGatt().connect();
            //设置已经发起申请连接
            this.setRequestConnect(true);

        }else
        {
            if(!this.isRequestConnect())
            {
                Logger.e("服务不存在,重新连接"+device.getAddress());
                this.connectBle(bluetoothDevice);
            }
        }
    }
    public void disConnect()
    {
        bluetoothGatt.disconnect();

    }
    public void reScanBle() {
        //已经扫描
        if(isBleScaned()) return;

    }
    public void writeChar1() {
        byte[] writeValue = new byte[1];
//        Log.i(TAG, "gattCharacteristic_char1 = " + gattCharacteristic_char1);
        if (gattCharacteristic_char1 != null) {
//            writeValue[0] = writeValue_char1++;
//            Log.i(TAG, "gattCharacteristic_char1.setValue writeValue[0] ="
//                    + writeValue[0]);
//            boolean bRet = gattCharacteristic_char1.setValue(writeValue);
            mBluetoothLeService.writeCharacteristic(getBluetoothGatt(),gattCharacteristic_char1);
        }
    }

    public void writeChar6(String string) {
        // byte[] writeValue = new byte[1];
//        Log.i(TAG, "gattCharacteristic_char6 = " + gattCharacteristic_char6);
        if (getGattCharacteristic_char6() != null&&getBluetoothGatt()!=null) {
            // writeValue[0] = writeValue_char1++;
            // Log.i(TAG, "gattCharacteristic_char6.setValue writeValue[0] =" +
            // writeValue[0]);
            // byte[] writebyte = new byte[4];
            Logger.e(getDevice().getAddress()+"发送数据： "+string);
            boolean bRet = getGattCharacteristic_char6().setValue(string.getBytes());
            mBluetoothLeService.writeCharacteristic(this.getBluetoothGatt(), getGattCharacteristic_char6());
        }else {
            Logger.e("getGattCharacteristic_char6 is null");
        }
    }

    public void read_char1() {
        byte[] writeValue = new byte[1];
//        Log.i(TAG, "readCharacteristic = ");
        if (gattCharacteristic_char1 != null) {
            mBluetoothLeService.readCharacteristic(gattCharacteristic_char1);
        }
    }

    public void read_uuid_0xffa6() {
//        Log.i(TAG, "readCharacteristic = ");
        if (gattCharacteristic_0xffa6 != null) {
            mBluetoothLeService.readCharacteristic(gattCharacteristic_0xffa6);
        }
    }
}
