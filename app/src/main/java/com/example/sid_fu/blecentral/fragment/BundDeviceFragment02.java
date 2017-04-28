package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.NotifyDialog;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.ConfigDevice;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/6.
 */
public class BundDeviceFragment02 extends Fragment {

    private final int maxLenght = -60;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
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
    private LoadingDialog loadDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_bund, container, false);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        initData();
        App.getInstance().speak("您正在使用解绑或绑定功能");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initIsCofigBle();
        loadDialog = new LoadingDialog(getActivity());
    }

    private void initData() {
        deviceDaoUtils = App.getDeviceDao();
        deviceDao = new Device();
    }

    private void initIsCofigBle() {
        Logger.e(mActivity.deviceDetails.toString());
        Logger.e(mActivity.manageDevice.getLeftFDevice());
        Logger.e(mActivity.manageDevice.getRightFDevice());
        if(mActivity.manageDevice.getLeftFDevice()==null||mActivity.manageDevice.getLeftFDevice().equals("null"))
        {
            topleft_next.setText("点击绑定");
        }else
        {
            topleft_next.setText("点击解绑");
        }

        if(mActivity.manageDevice.getRightFDevice()==null)
        {
            topright_next.setText("点击绑定");
        }else
        {
            topright_next.setText("点击解绑");
        }
        if(mActivity.manageDevice.getLeftBDevice()==null)
        {
            bottomleft_next.setText("点击绑定");
        }else
        {
            bottomleft_next.setText("点击解绑");
        }
        if(mActivity.manageDevice.getRightBDevice()==null)
        {
            bottomright_next.setText("点击绑定");
        }else
        {
            bottomright_next.setText("点击解绑");
        }

    }
    private void initUI() {

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
                    showNotifyDialog(leftF);

                }else if(topleft_next.getText().equals(getResources().getString(R.string.unbund_success))){
                    bundDevice(leftF);
                }
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topright_next.getText().equals(getResources().getString(R.string.unbund))) {
                    showNotifyDialog(rightF);
                }else if(topright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    bundDevice(rightF);
                }
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund))) {
                    showNotifyDialog(leftB);
                }else if(bottomleft_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    bundDevice(leftB);
                }
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomright_next.getText().equals(getResources().getString(R.string.unbund))) {
                    showNotifyDialog(rightB);
                }else if(bottomright_next.getText().equals(getResources().getString(R.string.unbund_success)))
                {
                    bundDevice(rightB);
                }
            }
        });
    }

    private boolean timeout;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0)
            {
                Intent it =new Intent(getActivity(),NotifyDialog.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(ConfigDevice.PAIRED_OK,"重试");
                it.putExtra(ConfigDevice.NONE_NEXT,true);
                startActivity(it);
                mActivity.stopScan();
                timeout = true;
            }
        }
    };
    private void broadcastUpdate(final String action, BluetoothDevice gatt) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        getActivity().sendBroadcast(intent);
    }
    private void unBundDevice(TextView tvNext,MyBluetoothDevice device,int state)
    {
        tvNext.setText("点击绑定");
//        showDialog("正在解绑。。。");
        Logger.e("正在解绑。。。"+state);
//        mHandler.sendEmptyMessage(0);
        mActivity.mDeviceList.remove(device);
        //保存蓝牙mac地址
        deviceDaoUtils.update(state,mActivity.deviceId,null);
        //SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RB,false);
        if(device!=null)
            broadcastUpdate(BluetoothLeService.ACTION_DISCONNECT_SCAN,device.getDevice());
//        showDialog("解绑成功。。。");
        switch (state) {
            case 1:
                mActivity.manageDevice.setLeftFDevice(null);
                break;
            case 2:
                mActivity.manageDevice.setRightFDevice(null);
                break;
            case 3:
                mActivity.manageDevice.setLeftBDevice(null);
                break;
            case 4:
                mActivity.manageDevice.setRightBDevice(null);
                break;
        }
    }
    private void bundDevice(int states)
    {
        isRecevice = false;
        state = states;
        mActivity.startScan();
        showDialog("正在识别信号强度。。。",false);
        //ToastUtil.show("开始扫描...");
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
        mDeviceList.add(device);
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

    private boolean isRecevice = true;
    private boolean isWrite;
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
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                if(timeout) {
                    timeout = false;
                    isWrite = true;
                    mActivity.mBluetoothLeService.disconnect();
                    return;
                }
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                if(!isWrite)
                    mActivity.mBluetoothLeService.connect(device.getAddress());
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                if(timeout) {
                    timeout = false;
                    isWrite = true;
                    mActivity.mBluetoothLeService.disconnect();
                    return;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.mBluetoothLeService.writeChar6("AT+USED=1");
                    }
                },2000);
                //mActivity.mBluetoothLeService.writeChar6("AT+USED=1");
                //mActivity.mBluetoothLeService.writeChar6("AT+USED=1");
                if(!isWrite)
                    showDialog("正在配置传感器。。。",false);
                Logger.e("Discover GATT Services"+device.getAddress()+"send:AT+USED=1");
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功 OK
                        if (data != null) {
                            if(data.length==2&&!isWrite)
                            {
                                onSuccess(device);
                                isWrite = true;
                                Logger.e("解绑成功");
                            }
                        }
                    }
                });
            } else if (BluetoothLeService.ACTION_RETURN_OK.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                int rssi = intent.getIntExtra("RSSI",0);
                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
                Logger.e("绑定==收到广播数据"+rssi);
                for (MyBluetoothDevice ble : mActivity.mDeviceList)
                {
                    Logger.e("列表中存在的设备："+ble.getDevice().getAddress());
                }
                if(!isRecevice&&!ManageDevice.isBundConfigEquals(mActivity.mDeviceList,device)&&rssi>maxLenght) {
                    isRecevice = true;
                    bleIsFind(device,rssi,scanRecord);
                }
            }else if (NotifyDialog.ACTION_BTN_STATE.equals(action)) {
//                int state = intent.getExtras().getInt(NotifyDialog.BTN_STATE);
//                Logger.e("重试"+state);
                isWrite = false;
                isRecevice = false;
                timeout = false;
                mActivity.startScan();
                showDialog("正在识别信号强度。。。",false);
            }else if (NotifyDialog.ACTION_BTN_NEXT.equals(action)) {
                Logger.e("完成"+state);
                isWrite = false;
                timeout = false;
                mActivity.mBluetoothLeService.disconnect();
            }
        }
    };

    private void onSuccess(BluetoothDevice device)
    {
        switch (state) {
            case 1:
                setUsedToTrue(device,topleft_next,tv_note_left_from,"左前轮");
                break;
            case 2:
                setUsedToTrue(device,topright_next,tv_note_right_from,"右前轮");
                break;
            case 3:
                setUsedToTrue(device,bottomleft_next,tv_note_left_back,"左后轮");
                break;
            case 4:
                setUsedToTrue(device,bottomright_next,tv_note_right_back,"右后轮");
                break;
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_RETURN_OK);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_STATE);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_NEXT);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data)
    {

        scanForResult(device);
    }
    private void scanForResult(BluetoothDevice device)
    {
        mActivity.stopScan();
        Logger.e("开始连接："+device.getAddress());
        showDialog(device.getAddress()+"正在连接。。。",false);
        //开始连接蓝牙
        mActivity.mBluetoothLeService.connect(device.getAddress());
    }
    private void setUsedToTrue(BluetoothDevice device,Button currentBtn ,TextView currentTv,String str)
    {
        //如果出现超时，任何数据都放弃
        if(timeout) {
            timeout = false;
            isWrite = true;
            mActivity.mBluetoothLeService.disconnect();
            return;
        }
        loadDialog.stopCount();
        Intent it =new Intent(getActivity(),NotifyDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(ConfigDevice.PAIRED_OK,"完成");
        it.putExtra(ConfigDevice.NONE_NEXT,true);
        startActivity(it);
        //Intent broackIntent = new Intent(NotifyDialog.ACTION_CHANGE_STATE);
        //broackIntent.putExtra(NotifyDialog.BTN_STATE,"完成");
        //getActivity().sendBroadcast(broackIntent);

        //showDialog("正在配置传感器。。。",false);
        currentBtn.setText(getResources().getString(R.string.unbund));
        //currentBtn.setText("确定");
        currentBtn.setVisibility(View.VISIBLE);
        //保存数据到本地
        deviceDaoUtils.update(state,mActivity.deviceId,device.getAddress());
        currentTv.setText(str+"：\n"+device.getAddress());
//        mDeviceList.add(device);
        //showDialog("正在复位传感器。。。",false);
        switch (state) {
            case 1:
                mActivity.manageDevice.setLeftFDevice(device.getAddress());
                break;
            case 2:
                mActivity.manageDevice.setRightFDevice(device.getAddress());
                break;
            case 3:
                mActivity.manageDevice.setLeftBDevice(device.getAddress());
                break;
            case 4:
                mActivity.manageDevice.setRightBDevice(device.getAddress());
                break;
        }
        mActivity.mBluetoothLeService.disconnect();
        state = none;
    }
    private void showDialog(String str,boolean isConnect)
    {
        if(!loadDialog.isShowing())
        {
            loadDialog.setText(str);
            loadDialog.show();
            loadDialog.setCountNum(30);
            loadDialog.startCount(new LoadingDialog.OnListenerCallBack() {
                @Override
                public void onListenerCount() {
                    mHandler.sendEmptyMessage(0);
                }
            });
        }else{
            loadDialog.reStartCount(str,30);
        }
//        App.getInstance().speak(str);
//        mActivity.startScan();
    }
    private void showDialog(String str)
    {
        if(!loadDialog.isShowing())
        {
            loadDialog.setText(str);
            loadDialog.show();
        }else{
            loadDialog.stopCount();
        }
//        App.getInstance().speak(str);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }
    /**
     *
     */
    private void showNotifyDialog(final int state)
    {
//        App.getInstance().speak(preStr+"与"+curStr+"进行对调，请选择确定或者取消");
        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题
                .setMessage("请确定传感器已经损坏或者无法正常工作情况下，才能使用解绑功能，否则解绑成功之后，无法进行绑定，" +
                        "如果确定请点击确定按钮进行解绑，否则请选择取消")//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub
//                         finish();
                        dialog.dismiss();
                        switch (state) {
                            case 1:
                                unBundDevice(topleft_next,mActivity.leftFDevice,leftF);
                                break;
                            case 2:
                                unBundDevice(topright_next,mActivity.rightFDevice,rightF);

                                break;
                            case 3:
                                unBundDevice(bottomleft_next,mActivity.leftBDevice,leftB);
                                break;
                            case 4:
                                unBundDevice(bottomright_next,mActivity.rightBDevice,rightB);
                                break;
                        }
                    }
                }).setNegativeButton("取消",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.mBluetoothLeService.disconnect();
    }
}
