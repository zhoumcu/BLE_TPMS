package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.ScanDeviceRunnable;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6.
 */
public class InsteadDeviceFragment extends Fragment {
    private MainFrameActivity mActivity;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private ProgressBar pb_left_from;
    private ProgressBar pb_right_from;
    private ProgressBar pb_left_back;
    private ProgressBar pb_right_back;
    private final int maxLenght = -60;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
    private ScanDeviceRunnable leftFRunable;
    private ScanDeviceRunnable rightFRunable;
    private ScanDeviceRunnable leftBRunable;
    private ScanDeviceRunnable rightBRunable;
    private ImageView imgTopleft;
    private ImageView imgTopright;
    private ImageView imgBottomleft;
    private ImageView imgBottomright;
    private Button topleft_ok;
    private Button topleft_next;
    private Button topright_ok;
    private Button topright_next;
    private Button bottomleft_ok;
    private Button bottomleft_next;
    private Button bottomright_ok;
    private Button bottomright_next;
    public static int leftF = 1;
    public static int rightF = 2;
    public static int leftB = 3;
    public static int rightB =4;
    public static int none =5;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_change, container, false);
//        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }
    private void initUI()
    {

        imgTopleft = (ImageView) getView().findViewById(R.id.img_topleft);
        imgTopright = (ImageView) getView().findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) getView().findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) getView().findViewById(R.id.img_bottomright);

        pb_left_from = (ProgressBar) getView().findViewById(R.id.pb_left_from);
        pb_right_from = (ProgressBar) getView().findViewById(R.id.pb_right_from);
        pb_left_back = (ProgressBar) getView().findViewById(R.id.pb_left_back);
        pb_right_back = (ProgressBar) getView().findViewById(R.id.pb_right_back);

        tv_note_left_from = (TextView) getView().findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) getView().findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) getView().findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) getView().findViewById(R.id.tv_note_right_back);

        topleft_ok = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_ok);
        topleft_next = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_next);

        topright_ok = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_ok);
        topright_next = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_next);

        bottomleft_ok = (Button) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_ok);
        bottomleft_next = (Button) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_next);

        bottomright_ok = (Button) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.btn_ok);
        bottomright_next = (Button)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.btn_next);

//        leftFRunable = new ScanDeviceRunnable(handler,leftF);
//        rightFRunable = new ScanDeviceRunnable(handler,rightF);
//        leftBRunable = new ScanDeviceRunnable(handler,leftB);
//        rightBRunable = new ScanDeviceRunnable(handler,rightB);

        topleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = leftF;
                topleft_ok.setVisibility(View.GONE);
                pb_left_from.setVisibility(View.VISIBLE);
                if(topleft_ok.getText().equals(getResources().getString(R.string.change)))
                {
//                    state = leftF;
                    showDialog(mActivity.leftFDevice,topleft_ok,pb_left_from);
                }else  if(topleft_ok.getText().equals(getResources().getString(R.string.retry)))
                {
                    topleft_ok.setVisibility(View.GONE);
                    pb_left_from.setVisibility(View.VISIBLE);
                    startScanLe();
                }
            }
        });
        topright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = rightF;
//                topright_ok.setVisibility(View.GONE);
//                pb_right_from.setVisibility(View.VISIBLE);
                if(topright_ok.getText().equals(getResources().getString(R.string.change)))
                {
//                    state = leftF;
                    showDialog(mActivity.rightFDevice,topright_ok,pb_right_from);
                }else  if(topright_ok.getText().equals(getResources().getString(R.string.retry)))
                {
                    topright_ok.setVisibility(View.GONE);
                    pb_right_from.setVisibility(View.VISIBLE);
                    startScanLe();
                }
            }
        });
        bottomleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = leftB;
//                bottomleft_next.setVisibility(View.GONE);
//                pb_left_back.setVisibility(View.VISIBLE);
                if(bottomleft_ok.getText().equals(getResources().getString(R.string.change)))
                {
//                    state = leftF;
                    showDialog(mActivity.leftBDevice,bottomleft_ok,pb_left_back);
                }else if(bottomleft_ok.getText().equals(getResources().getString(R.string.retry)))
                {
                    bottomleft_ok.setVisibility(View.GONE);
                    pb_left_back.setVisibility(View.VISIBLE);
                    startScanLe();
                }
            }
        });
        bottomright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                bottomright_next.setVisibility(View.GONE);
//                pb_right_back.setVisibility(View.VISIBLE);
                state = rightB;
                if(bottomright_ok.getText().equals(getResources().getString(R.string.change)))
                {
                    showDialog(mActivity.rightBDevice,bottomright_ok,pb_right_back);
                }else if(bottomright_ok.getText().equals(getResources().getString(R.string.retry)))
                {
                    bottomright_ok.setVisibility(View.GONE);
                    pb_right_back.setVisibility(View.VISIBLE);
                    startScanLe();
                }
            }
        });
    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(state==none) return;
            Logger.e("重试");
            switch (msg.what)
            {
                case 1:
                    handlerReScan(topleft_ok,pb_left_from);
                    break;
                case 2:
                    handlerReScan(topright_ok,pb_right_from);
                    break;
                case 3:
                    handlerReScan(bottomleft_ok,pb_left_back);
                    break;
                case 4:
                    handlerReScan(bottomright_ok,pb_right_back);
                    break;
            }
        }
    };
    private void handlerReScan(Button currentBtn,ProgressBar currentPb)
    {
//        if(currentBtn.getText().equals(getResources().getString(R.string.change)))
//        {
        currentBtn.setText("重试");
        currentPb.setVisibility(View.GONE);
        currentBtn.setVisibility(View.VISIBLE);
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
    }
    private void showDialog(final MyBluetoothDevice device, final Button btn, final ProgressBar pb)
    {
        //弹出选择框
        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题
                .setMessage("如果确定，之前的蓝牙设备将被取消，是否确定？")//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub
//                         finish();
                        dialog.dismiss();
                        btn.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                        startScanLe();
                    }
                }).setNegativeButton("不用了",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
//                    Log.i("alertdialog"," 请保存数据！");
//                    finish();
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }
    //    private void iniBle() {
//        // Use this check to determine whether BLE is supported on the device.  Then you can
//        // selectively disable BLE-related features.
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//    }
    private void startScanLe()
    {
//        Logger.e("重新扫描");
//        new Thread(new Runnable() {
//            public void run() {
//                if (mBluetoothAdapter.isEnabled()) {
//                    scanLeDevice(true);
//                    mScanning = true;
//                } else {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                }
//            }
//        }).start();
    }
    //    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            // Stops scanning after a pre-defined scan period.
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        Thread.sleep(SCAN_PERIOD);
//                        if (mScanning) {
//                            Logger.e(TAG,"断开扫描");
//                            mScanning = false;
//                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                            //invalidateOptionsMenu();
//                            handler.sendEmptyMessage(state);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//            //WinToast.makeText(mContext,"start scan");
//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mScanning = false;
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//
//
//        //invalidateOptionsMenu();
//    }
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
//    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, final Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_CHANGE_RESULT.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
//                int rssi = intent.getParcelableExtra("RSSI");
//                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
//                bleIsFind(device,rssi,scanRecord,state);
//                Logger.e("发现新设备"+device.getAddress());
//            }
//        }
//    };
    private int state = leftF;
    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //ParsedAd ad = DataUtils.parseData(scanRecord);
//                    bleIsFind(device,rssi,scanRecord,state);
//                    Logger.e(TAG,"发现新设备"+device.getAddress());
//                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
//                    /*if (mScanning == true) {
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                        mBluetoothAdapter.startLeScan(mLeScanCallback);
//                    }
//                    */
//                }
//            });
//        }
//    };
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // User chose not to enable Bluetooth.
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
//        scanLeDevice(true);
//        mScanning = true;
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data,int state)
    {
        Logger.e("信号强度"+rssi+data.toString());
        for (BluetoothDevice ble : mDeviceList)
        {
            Logger.e("列表中存在的设备："+ble.getAddress());
        }
        if(!ManageDevice.isConfigEquals(mDeviceList,device)&&rssi>maxLenght) {

            switch (state)
            {
                case 1:
                    if(rssi>maxLenght) {
                        mDeviceList.add(device);
                        scanForResult(device,leftFRunable,topleft_ok,pb_left_from,tv_note_left_from);
                        //保存蓝牙mac地址
                        SharedPreferences.getInstance().putString(Constants.LEFT_F_DEVICE,device.getAddress());
                    }
                    break;
                case 2:
                    if(rssi>maxLenght) {
                        mDeviceList.add(device);
                        scanForResult(device,rightFRunable,topright_ok,pb_right_from,tv_note_right_from);
                        //保存蓝牙mac地址
                        SharedPreferences.getInstance().putString(Constants.RIGHT_F_DEVICE,device.getAddress());
                    }
                    break;
                case 3:
                    if(rssi>maxLenght) {
                        mDeviceList.add(device);
                        scanForResult(device,leftBRunable,bottomleft_ok,pb_left_back,tv_note_left_back);
                        //保存蓝牙mac地址
                        SharedPreferences.getInstance().putString(Constants.LEFT_B_DEVICE,device.getAddress());
                    }
                    break;
                case 4:
                    if(rssi>maxLenght) {
                        mDeviceList.add(device);
                        scanForResult(device,rightBRunable,bottomright_ok,pb_right_back,tv_note_right_back);
                        //保存蓝牙mac地址
                        SharedPreferences.getInstance().putString(Constants.RIGHT_B_DEVICE,device.getAddress());
                    }
                    break;
                default:
                    break;
            }

        }
    }
    private void scanForResult(BluetoothDevice device, ScanDeviceRunnable runnable, Button currentBtn, ProgressBar currentPb, TextView currentTv)
    {
        currentBtn.setText("更换成功");
        currentBtn.setVisibility(View.VISIBLE);
        currentPb.setVisibility(View.GONE);
        //保存数据到本地
        currentTv.setText("左前轮：\n"+device.getAddress());
        state = none;
//        handler.removeCallbacks(runnable);
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
}
