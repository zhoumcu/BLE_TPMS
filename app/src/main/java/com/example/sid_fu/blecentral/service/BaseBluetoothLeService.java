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

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.sid_fu.blecentral.model.MyBluetoothDevice;
import com.example.sid_fu.blecentral.model.SampleGattAttributes;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.widget.SystemDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BaseBluetoothLeService extends Service {
    private final static String TAG = BaseBluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager = null;
    protected BluetoothAdapter mBluetoothAdapter = null;
    private ArrayList<BluetoothGatt> connectionQueue = new ArrayList<BluetoothGatt>();

    private MyBluetoothDevice bluetoothDevice;
    static BluetoothGattCharacteristic gattCharacteristic_char6 = null;
    private ArrayList<MyBluetoothDevice> connectDevice = new ArrayList<>();
    private BluetoothGatt bluetoothGatt;

    public void findService(BluetoothGatt gatt) {
        BluetoothGattCharacteristic gattCharacteristic = gatt.getService(SampleGattAttributes.UUID_SERVICE).getCharacteristic(SampleGattAttributes.UUID_CHAR);
        // 把char1 保存起来以方便后面读写数据时使用
        for (MyBluetoothDevice bluetoothDevice : connectDevice) {
            if(bluetoothDevice.getDevice().getAddress().equals(gatt.getDevice().getAddress())) {
                bluetoothDevice.setGattCharacteristic_char6(gattCharacteristic);
            }
        }
        gattCharacteristic_char6 = gattCharacteristic;
        if(setCharacteristicNotification(gatt,gattCharacteristic, true))
            broadcastUpdate(SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED, gatt);
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private  BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            Logger.i(TAG, "oldStatus=" + status + " NewStates=" + newState);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    intentAction = SampleGattAttributes.ACTION_GATT_CONNECTED;
                    broadcastUpdate(intentAction,gatt);
                    Logger.i(TAG, "Connected to GATT server.");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = SampleGattAttributes.ACTION_GATT_DISCONNECTED;
                    listClose(gatt);
                    Logger.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction, gatt);
                }
        	}
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Logger.e(TAG, "onServicesDiscovered received: " + status);
            	findService(gatt);
            } else {
            	if(gatt.getDevice().getUuids() == null)
                    Logger.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Logger.e(TAG, "onread " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(SampleGattAttributes.ACTION_DATA_AVAILABLE, characteristic,gatt);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(SampleGattAttributes.ACTION_DATA_AVAILABLE, characteristic,gatt);
        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            // TODO Auto-generated method stub
            Logger.e("--onReadRemoteRssi--: " + status + ",   rssi:" + rssi
                    + "----------------------------------");
             super.onReadRemoteRssi(gatt, rssi, status);
             Intent mIntent = new Intent(SampleGattAttributes.ACTION_NAME_RSSI);
             mIntent.putExtra("DEVICE_ADDRESS", gatt.getDevice());
             mIntent.putExtra("RSSI", rssi);
             //发送广播
             sendBroadcast(mIntent);
        }
    };

    private void broadcastUpdate(final String action, final BluetoothGatt gatt) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt.getDevice());
        sendBroadcast(intent);
    }

    private void broadcastUpdate( String action, BluetoothGattCharacteristic characteristic,BluetoothGatt gatt) {
        Intent intent = new Intent(action);
        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(SampleGattAttributes.EXTRA_DATA,data);
            intent.putExtra("DEVICE_ADDRESS", gatt.getDevice());
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BaseBluetoothLeService getService() {
            return BaseBluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Logger.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        if(mBluetoothAdapter == null)
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Logger.i("BluetoothAdapter not initialized or unspecified address.");
//            Logger.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.i("Device not found.  Unable to connect.");
            return false;
        }
        bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Logger.i("Trying to create a new connection.");
        return true;
    }
    public boolean connect(String address, MyBluetoothDevice blueDevice) {
        if (mBluetoothAdapter == null || address == null) {
            Logger.i( "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        this.bluetoothDevice = blueDevice;
        Logger.i("the object is:"+bluetoothDevice.getDevice().getAddress());
        if(checkGatt(blueDevice)&&blueDevice!=null){
            connectDevice.add(blueDevice);
            Logger.i("connect device num:"+connectDevice.size());
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Logger.i( "Device not found.  Unable to connect.");
            return false;
        }
        BluetoothGatt bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Logger.i("Trying to create a new connection and connection sum:"+connectionQueue.size());
        if(checkGatt(bluetoothGatt)&&bluetoothGatt!=null){
            blueDevice.setBluetoothGatt(bluetoothGatt);
            connectionQueue.add(bluetoothGatt);
            Logger.i("no bluetoothGatt."+connectionQueue.size());
        }
        return true;
    }
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param device The device address of the destination device.
     *
     * @param size
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(BluetoothDevice device, int size) {
        if (mBluetoothAdapter == null || device == null) {
            Logger.i(TAG, "BluetoothAdapter not initialized or unspecified address.");
            Logger.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        BluetoothGatt bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        if(checkGatt(bluetoothGatt)&&bluetoothGatt!=null){
            connectionQueue.add(bluetoothGatt);
            Logger.i("no bluetoothGatt."+connectionQueue.size());
        }
        Logger.i("Trying to create a new connection.");
        return true;
    }

    private boolean checkGattForDevice(BluetoothDevice device) {
        for (int j=0;j<connectionQueue.size();j++) {
            if(connectionQueue.get(j).getDevice().getAddress().equals(device.getAddress())) {
                Logger.i("gatt存在，直接连"+device.getAddress());
                connectionQueue.get(j).connect();
                return true;
            }
        }
        return false;
    }
    private boolean checkGatt(BluetoothGatt bluetoothGatt) {
        if (!connectionQueue.isEmpty()&&bluetoothGatt!=null) {
            for(BluetoothGatt btg:connectionQueue){
                if(btg.getDevice().getAddress().equals(bluetoothGatt.getDevice().getAddress())){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkGatt(MyBluetoothDevice bluetoothDevice) {
        if (!connectDevice.isEmpty()&&bluetoothDevice!=null) {
            for(MyBluetoothDevice btg:connectDevice){
                if(btg.getDevice().getAddress().equals(bluetoothDevice.getDevice().getAddress())){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null ) {
            Logger.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if(!Constants.SINGLE_BLE) {
            for (final BluetoothGatt bluetoothGatt : connectionQueue) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            bluetoothGatt.disconnect();
                            Thread.sleep(250);
                            bluetoothGatt.close();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }else {
            if(bluetoothGatt ==null) return;
                new Thread(new Runnable() {
                public void run() {
                    try {
                        bluetoothGatt.disconnect();
                        Thread.sleep(250);
                        bluetoothGatt.close();
                        Logger.e("bluetoothGatt close");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!Constants.SINGLE_BLE)
            connectionQueue.clear();
        Logger.e("service close!");
    }

    public void disconnect(final BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt == null) {
            Logger.e(TAG, "bluetoothGatt not initialized");
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(250);
                    bluetoothGatt.disconnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        if (connectionQueue.isEmpty()&&!Constants.SINGLE_BLE) {
            return;
        }
        if(!Constants.SINGLE_BLE) {
            for ( BluetoothGatt bluetoothGatt : connectionQueue) {
                bluetoothGatt.close();
            }
            connectionQueue.clear();
            connectDevice.clear();
        }else {
            if(bluetoothGatt!=null)
                bluetoothGatt.close();
        }
    }
    private synchronized void close(BluetoothGatt gatt) {
        if (gatt != null) {
            gatt.disconnect();
            gatt.close();
        }
    }
    public void listClose(BluetoothGatt gatt) {
        if (!connectionQueue.isEmpty()) {
            if (gatt != null) {
                for(final BluetoothGatt bluetoothGatt:connectionQueue){
                    if(bluetoothGatt.equals(gatt)){
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    bluetoothGatt.disconnect();
                                    Thread.sleep(250);
                                    //bluetoothGatt.close();
                                    connectionQueue.remove(bluetoothGatt);
                                    connectDevice.clear();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }else{
                for ( BluetoothGatt bluetoothGatt : connectionQueue) {
                    bluetoothGatt.disconnect();
                }
                connectionQueue.clear();
                connectDevice.clear();
            }
        }else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        bluetoothGatt.disconnect();
                        Thread.sleep(250);
                        connectionQueue.remove(bluetoothGatt);
                        connectDevice.clear();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public void removeConnectionQueue(BluetoothGatt gatt) {
        connectionQueue.remove(gatt);
    }
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || connectionQueue.isEmpty() ) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        for(BluetoothGatt bluetoothGatt:connectionQueue){
            Log.e(TAG,"connectionQueue");
            bluetoothGatt.readCharacteristic(characteristic);
        }
    }
    public void writeCharacteristic(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        bluetoothGatt.writeCharacteristic(characteristic);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *  @param characteristic
     *            Characteristic to act on.
     * @param enabled
     */
    public boolean setCharacteristicNotification(
            BluetoothGatt gatt,BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || (connectionQueue.isEmpty()&& !Constants.SINGLE_BLE)) {
            Logger.e( "BluetoothAdapter not initialized");
            return false;
        }
        Logger.e( "Enable Notification");
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
        if(descriptor!=null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
        return true;
    }
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || (connectionQueue.isEmpty()&& !Constants.SINGLE_BLE)) {
            Logger.e( "BluetoothAdapter not initialized");
            return;
        }
        if(!Constants.SINGLE_BLE) {
            for(BluetoothGatt bluetoothGatt:connectionQueue) {
                Logger.e( "Enable Notification");
                bluetoothGatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
        }else {
            Logger.e( "Enable Notification");
            bluetoothGatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        BluetoothGattCharacteristic Characteristic_cur = null;

        for (BluetoothGattService gattService : gattServices) {
            // -----Service的字段信息----//
            int type = gattService.getType();
            // -----Characteristics的字段信息----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                int permission = gattCharacteristic.getPermissions();
                int property = gattCharacteristic.getProperties();
                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    /*Log.e(TAG, "---->char value:" + new String(data));*/
                }

                if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.UUID_CHAR5)) {
                    bluetoothDevice.gattCharacteristic_char5 = gattCharacteristic;
                }

                if (gattCharacteristic.getUuid().toString().equals(SampleGattAttributes.UUID_CHAR6)) {
                    // 把char1 保存起来以方便后面读写数据时使用
                    bluetoothDevice.gattCharacteristic_char6 = gattCharacteristic;
                    gattCharacteristic_char6 = gattCharacteristic;
                    setCharacteristicNotification(gattCharacteristic, true);
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(SampleGattAttributes.UUID_HERATRATE)) {
                    // 把heartrate 保存起来以方便后面读写数据时使用
                    bluetoothDevice.gattCharacteristic_heartrate = gattCharacteristic;
                    // 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    setCharacteristicNotification(gattCharacteristic, true);
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(SampleGattAttributes.UUID_KEY_DATA)) {
                    // 把heartrate 保存起来以方便后面读写数据时使用
                    bluetoothDevice.gattCharacteristic_keydata = gattCharacteristic;
                    // 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    setCharacteristicNotification(gattCharacteristic, true);
                }

                if (gattCharacteristic.getUuid().toString()
                        .equals(SampleGattAttributes.UUID_TEMPERATURE)) {
                    // 把heartrate 保存起来�?以方便后面读写数据时使用
                    bluetoothDevice.gattCharacteristic_temperature = gattCharacteristic;
                    // 接受Characteristic被写的�?�?收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
                    setCharacteristicNotification(gattCharacteristic, true);
                }


                if (gattCharacteristic.getUuid().toString()
                        .equals(SampleGattAttributes.UUID_0XFFA6)) {
                    // 把heartrate 保存起来以方便后面读写数据时使用
                    bluetoothDevice.gattCharacteristic_0xffa6 = gattCharacteristic;
                }

                // -----Descriptors的字段信�?----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
                        .getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    int descPermission = gattDescriptor.getPermissions();
                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
/*
                        Log.e(TAG, "-------->desc value:" + new String(desData));
*/
                    }
                }

            }
        }//
    }

    public ArrayList<BluetoothGatt> getBluethGatt()
    {
        return this.connectionQueue;
    }
    public void removeBluethGatt(BluetoothGatt gatt) {
        gatt.disconnect();
        connectionQueue.remove(gatt);
    }

    public void showDialog() {
        /*
        new AlertDialog.Builder(this)
                .setTitle("系统提示")
                .setMessage("检测到应用长时间未工作，确定退出吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity_3.mContext.finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity_3.mShakeListener.start();
                }
            })
        .show();

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("系统提示");
        builder .setMessage("检测到应用长时间未工作，确定退出吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity_3.mContext.finish();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MainActivity_3.mShakeListener.start();
                }
         });
        AlertDialog ad = builder.create();
        //ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性
        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ad.setCanceledOnTouchOutside(false);                                   //点击外面区域不会让dialog消失
        ad.show();
        */
        Intent it =new Intent(this,SystemDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }
    public void writeChar6(String string) {
        if (gattCharacteristic_char6 != null&&bluetoothGatt!=null) {
            boolean bRet = gattCharacteristic_char6.setValue(string.getBytes());
            writeCharacteristic(bluetoothGatt, gattCharacteristic_char6);
        }else {
            Logger.e("getGattCharacteristic_char6 is null");
        }
    }
}
