package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.ParsedAd;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6.
 */
public class BundDeviceFragment01 extends Fragment {
    private ProgressBar pb_left_from;
    private ProgressBar pb_right_from;
    private ProgressBar pb_left_back;
    private ProgressBar pb_right_back;
    private final int maxLenght = -60;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
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
    private MainFrameActivity mActivity;
    public static int leftF = 1;
    public static int rightF = 2;
    public static int leftB = 3;
    public static int rightB =4;
    public static int none =5;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private int state;
    private DeviceDao deviceDaoUtils;
    private Device deviceDao;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_bund, container, false);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initIsCofigBle();
    }

    private void initData() {
        deviceDaoUtils = new DeviceDao(getActivity());
        deviceDao = new Device();
    }

    private void initIsCofigBle() {
        Logger.e(mActivity.deviceDetails.toString());
        Logger.e(mActivity.manageDevice.getLeftFDevice());
        Logger.e(mActivity.manageDevice.getRightFDevice());
        if(mActivity.manageDevice.getLeftFDevice()==null||mActivity.manageDevice.getLeftFDevice().equals("null"))
            topleft_next.setText("点击绑定");
        if(mActivity.manageDevice.getRightFDevice()==null)
            topright_next.setText("点击绑定");
        if(mActivity.manageDevice.getLeftBDevice()==null)
            bottomleft_next.setText("点击绑定");
        if(mActivity.manageDevice.getRightBDevice()==null)
            bottomright_next.setText("点击绑定");
    }
    private void initUI() {
        imgTopleft = (ImageView)getView(). findViewById(R.id.img_topleft);
        imgTopright = (ImageView) getView().findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) getView().findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView)getView(). findViewById(R.id.img_bottomright);

        pb_left_from = (ProgressBar) getView().findViewById(R.id.pb_left_from);
        pb_right_from = (ProgressBar)getView(). findViewById(R.id.pb_right_from);
        pb_left_back = (ProgressBar) getView().findViewById(R.id.pb_left_back);
        pb_right_back = (ProgressBar) getView().findViewById(R.id.pb_right_back);

        tv_note_left_from = (TextView)getView(). findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) getView().findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) getView().findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) getView().findViewById(R.id.tv_note_right_back);

        topleft_ok = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_ok);
        topleft_next = (Button) getView().findViewById(R.id.ll_topleft).findViewById(R.id.btn_next);

        topright_ok = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_ok);
        topright_next = (Button) getView().findViewById(R.id.ll_topright).findViewById(R.id.btn_next);

        bottomleft_ok = (Button)getView(). findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_ok);
        bottomleft_next = (Button)getView(). findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_next);

        bottomright_ok = (Button)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.btn_ok);
        bottomright_next = (Button)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.btn_next);

        topleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topleft_next.getText().equals(getResources().getString(R.string.unbund)))
                {
//                    topleft_next.setVisibility(View.GONE);
//                    pb_left_from.setVisibility(View.VISIBLE);
                    mActivity.leftFDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(mActivity.leftFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(topleft_next.getText().equals(getResources().getString(R.string.unbund_success))){
//                    mActivity.leftFDevice.writeChar6("AT+USED=1");
//                    ToastUtil.show(mActivity.leftFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                    state = leftF;
                    bundDevice(pb_left_from,topleft_next);
                }
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topright_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    topright_next.setVisibility(View.GONE);
//                    pb_right_from.setVisibility(View.VISIBLE);
                    mActivity.rightFDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(mActivity.rightFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(topright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
//                    mActivity.rightFDevice.writeChar6("AT+USED=1");
//                    ToastUtil.show(mActivity.rightFDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                    state = rightF;
                    bundDevice(pb_right_from,topright_next);
                }
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    bottomleft_next.setVisibility(View.GONE);
//                    pb_left_back.setVisibility(View.VISIBLE);
                    mActivity.leftBDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(mActivity.leftBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
//                    mActivity.leftBDevice.writeChar6("AT+USED=1");
//                    ToastUtil.show(mActivity.leftBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=1");
                    state = leftB;
                    bundDevice(pb_left_back,bottomleft_next);
                }
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomright_next.getText().equals(getResources().getString(R.string.unbund))) {
//                    bottomright_next.setVisibility(View.GONE);
//                    pb_right_back.setVisibility(View.VISIBLE);
                    mActivity.rightBDevice.writeChar6("AT+USED=0");
                    ToastUtil.show(mActivity.rightBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                }else if(bottomright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
//                    mActivity.startScan();
//                    ToastUtil.show(mActivity.rightBDevice.getDevice().getAddress()+"发送命令："+"AT+USED=0");
                    state = rightB;
                    bundDevice(pb_right_back,bottomright_next);
                }
            }
        });
//        initImg();
//        initData(mActivity.leftBDevice,bottomleft_next);
//        initData(mActivity.rightBDevice,bottomright_next);
//        initData(mActivity.leftFDevice,topleft_next);
//        initData(mActivity.rightFDevice,topright_next);
    }

    private void bundDevice(ProgressBar pb,Button btn)
    {
        isRecevice = false;
        mActivity.startScan();
//        pb.setVisibility(View.VISIBLE);
//        btn.setVisibility(View.GONE);
        ToastUtil.show("开始扫描...");
    }

    private void initData(MyBluetoothDevice device, Button btn)
    {
        if(device==null)
        {
            btn.setText(getResources().getString(R.string.erronofind));
        }else if(!device.isSuccessComm())
        {
            btn.setText(getResources().getString(R.string.erroinfo));
        }
    }
    private void success(BluetoothDevice device)
    {

        if(mActivity.leftFDevice!=null&&device.getAddress().equals(mActivity.leftFDevice.getDevice().getAddress()))
        {
            topleft_next.setText("点击绑定");
            mActivity.leftFDevice.setTimeOutDisconnect(false);
            if(mActivity.leftFDevice.getBluetoothGatt()!=null)
            mActivity.leftFDevice.getBluetoothGatt().disconnect();
//            mActivity.leftFDevice.getBluetoothGatt().close();
            mActivity.mDeviceList.remove( mActivity.leftFDevice);
            //保存蓝牙mac地址
//            deviceDao.setLeft_FD(null);
            deviceDaoUtils.update(1,mActivity.deviceId,null);
//            SharedPreferences.getInstance().putString(Constants.LEFT_F_DEVICE,null);
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LF,false);
            mActivity.manageDevice.setLeftFDevice("");
        }else if(mActivity.rightFDevice!=null&&device.getAddress().equals(mActivity.rightFDevice.getDevice().getAddress()))
        {
            topright_next.setText("点击绑定");
            mActivity.rightFDevice.setTimeOutDisconnect(false);
            if(mActivity.rightFDevice.getBluetoothGatt()!=null)
            mActivity.rightFDevice.getBluetoothGatt().disconnect();
//            mActivity.rightFDevice.getBluetoothGatt().close();
            mActivity.mDeviceList.remove( mActivity.rightFDevice);
            //保存蓝牙mac地址
//            deviceDao.setRight_FD(null);
            deviceDaoUtils.update(2,mActivity.deviceId,null);
//            SharedPreferences.getInstance().putString(Constants.RIGHT_F_DEVICE,null);
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RF,false);
            mActivity.manageDevice.setRightFDevice("");
        }else if(mActivity.leftBDevice!=null&&device.getAddress().equals(mActivity.leftBDevice.getDevice().getAddress()))
        {
            bottomleft_next.setText("点击绑定");
            mActivity.leftBDevice.setTimeOutDisconnect(false);
            if(mActivity.leftBDevice.getBluetoothGatt()!=null)
                mActivity.leftBDevice.getBluetoothGatt().disconnect();
//            mActivity.leftBDevice.getBluetoothGatt().close();
            mActivity.mDeviceList.remove( mActivity.leftBDevice);
            //保存蓝牙mac地址
//            deviceDao.setLeft_BD(null);
            deviceDaoUtils.update(3,mActivity.deviceId,null);
//            SharedPreferences.getInstance().putString(Constants.LEFT_B_DEVICE,null);
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LB,false);
            mActivity.manageDevice.setLeftBDevice("");
        }else if(mActivity.rightBDevice!=null&&device.getAddress().equals(mActivity.rightBDevice.getDevice().getAddress()))
        {
            bottomright_next.setText("点击绑定");
            mActivity.rightBDevice.setTimeOutDisconnect(false);
            if(mActivity.rightBDevice.getBluetoothGatt()!=null)
                mActivity.rightBDevice.getBluetoothGatt().disconnect();
//            mActivity.rightBDevice.getBluetoothGatt().close();
            mActivity.mDeviceList.remove( mActivity.rightBDevice);
            //保存蓝牙mac地址
//            deviceDao.setRight_BD(null);
            deviceDaoUtils.update(4,mActivity.deviceId,null);
//            SharedPreferences.getInstance().putString(Constants.RIGHT_B_DEVICE,null);
            mActivity.manageDevice.setRightBDevice("");
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RB,false);
        }
//        deviceDaoUtils.update(deviceDao);
        ToastUtil.show(device.getAddress()+"解绑成功！");
    }

    private boolean isRecevice;
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_RETURN_OK.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                int rssi = intent.getIntExtra("RSSI",0);
                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
                Logger.e("绑定==收到广播数据");
//                if(!isRecevice)
//                {
//                    isRecevice = true;
                    bleIsFind(device,rssi,scanRecord);
//                }
            }else  if (BluetoothLeService.ACTION_SEND_OK.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                success(device);
                Logger.e("解绑成功");
            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_RETURN_OK);
        intentFilter.addAction(BluetoothLeService.ACTION_SEND_OK);
        return intentFilter;
    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data)
    {
        try {
            ParsedAd ad = DataUtils.parseData(data);
            if(ad.datas==null) return;
            Logger.e(DigitalTrans.byte2hex(ad.datas));
            float press = ((float) DigitalTrans.byteToAlgorism(ad.datas[1]) * 160) / 51 / 100;
            int temp = DigitalTrans.byteToAlgorism(ad.datas[2]) - 50;
            int stateUse = DigitalTrans.byteToBin(ad.datas[0]);
            Logger.e("使用情况："+stateUse);
//            if(stateUse!=0) return;
            Logger.e("信号强度"+rssi+data.toString());
        }catch (NullPointerException e)
        {
            Logger.e("erro"+e.toString());
        }
        for (BluetoothDevice ble : mDeviceList)
        {
            Logger.e("列表中存在的设备："+ble.getAddress());
        }
        if(!ManageDevice.isConfigEquals(mDeviceList,device)&&rssi>maxLenght) {

            switch (state) {
                case 1:
                    if (rssi > maxLenght) {
                        mDeviceList.add(device);
                        topleft_next.setText(getResources().getString(R.string.unbund));
//                        scanForResult(device,leftFRunable,topleft_ok,pb_left_from,tv_note_left_from);
                        //保存蓝牙mac地址
//                        deviceDao.setLeft_FD(null);
                        deviceDaoUtils.update(1,mActivity.deviceId,device.getAddress());
//                        SharedPreferences.getInstance().putString(Constants.LEFT_F_DEVICE, device.getAddress());
                        mActivity.manageDevice.setLeftFDevice(device.getAddress());
                    }
                    break;
                case 2:
                    if (rssi > maxLenght) {
                        mDeviceList.add(device);
                        topright_next.setText(getResources().getString(R.string.unbund));
                        deviceDaoUtils.update(2,mActivity.deviceId,device.getAddress());
//                        scanForResult(device,rightFRunable,topright_ok,pb_right_from,tv_note_right_from);
//                        deviceDao.setRight_FD(null);
                        SharedPreferences.getInstance().putString(Constants.RIGHT_F_DEVICE, device.getAddress());
//                        mActivity.manageDevice.setRightFDevice(device.getAddress());
                    }
                    break;
                case 3:
                    if (rssi > maxLenght) {
                        mDeviceList.add(device);
                        bottomleft_next.setText(getResources().getString(R.string.unbund));
//                        scanForResult(device,leftBRunable,bottomleft_ok,pb_left_back,tv_note_left_back);
                        //保存蓝牙mac地址
//                        deviceDao.setLeft_BD(null);
                        deviceDaoUtils.update(3,mActivity.deviceId,device.getAddress());
//                        SharedPreferences.getInstance().putString(Constants.LEFT_B_DEVICE, device.getAddress());
//                        mActivity.manageDevice.setLeftBDevice(device.getAddress());
                    }
                    break;
                case 4:
                    if (rssi > maxLenght) {
                        mDeviceList.add(device);
                        bottomright_next.setText(getResources().getString(R.string.unbund));
//                        scanForResult(device,rightBRunable,bottomright_ok,pb_right_back,tv_note_right_back);
                        //保存蓝牙mac地址
//                        deviceDao.setRight_BD(null);
                        deviceDaoUtils.update(4,mActivity.deviceId,device.getAddress());
//                        SharedPreferences.getInstance().putString(Constants.RIGHT_B_DEVICE, device.getAddress());
//                        mActivity.manageDevice.setRightBDevice(device.getAddress());
                    }
                    break;
                default:
                    break;

            }
            mActivity.stopScan();
//            deviceDaoUtils.update(deviceDao);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }
}
