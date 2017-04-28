package com.example.sid_fu.blecentral.ui.frame;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.utils.Logger;

/**
 * Created by Administrator on 2016/7/28.
 */
public abstract class BaseBleConnetFragment extends Fragment{

    private FragmentActivity context;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    public  BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;
    private ManageDevice manageDevice;

    protected abstract void initData();

    protected abstract void initRunnable();

    protected abstract void initConfig();

    public boolean isRecevice = true;
    public boolean isWrite;
    public boolean timeout;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initConfig();
        iniBle();
        initRunnable();
        initUI();
        initData();
        //注册广播
        getActivity().registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }
    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    //ToastUtil.show(getActivity(), "home", 1).show();
//                    mActivity.isQuiting = true;
                    Logger.e("表示按了home键,程序到了后台");
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
    private void initUI() {

    }
    protected void setManageDevice(ManageDevice device) {
        this.manageDevice = device;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mHomeKeyEventReceiver);
        }catch (IllegalArgumentException e) {
            Logger.e("mHomeKeyEventReceiver:"+e.toString());
        }
        if(mBluetoothAdapter!=null)
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Logger.e("onDestroy");
    }

    private void iniBle() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            context.finish();
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            context.finish();
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                if (mBluetoothAdapter.isEnabled()) {
//                    scanLeDevice(true);
                    mScanning = true;
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            context.finish();
            return;
        }
        mScanning = true;
        super.onActivityResult(requestCode, resultCode, data);
    }
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.e("发现新设备"+device.getAddress());
                    if(!isNull(device,manageDevice.getLeftBDevice())||!isNull(device,manageDevice.getLeftFDevice())||!isNull(device,manageDevice.getRightBDevice())
                            ||!isNull(device,manageDevice.getRightFDevice())) {
                        broadcastUpdate(BluetoothLeService.ACTION_RETURN_OK,device,rssi,scanRecord);
                    }
                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
                    /*if (mScanning == true) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }*/
                }
            });
        }
    };
    public abstract void broadcastUpdate(final String action, BluetoothDevice gatt,int rssi,byte[] scanResult);

    private boolean isNull(BluetoothDevice device,String str) {
        if(str==null) return false;
        if(device.getAddress().contains(str)) {
            return true;
        }
        return false;
    }
    public void startScan() {
        isWrite = false;
        isRecevice = false;
        timeout = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    public void stopScan() {
        App.getInstance().speak("正在关闭蓝牙设备");
        isWrite = false;
        timeout = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
