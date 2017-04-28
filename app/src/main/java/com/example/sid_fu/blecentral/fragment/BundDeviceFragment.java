package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.BluetoothLeStartService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.NotifyDialog;
import com.example.sid_fu.blecentral.ParsedAd;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.ConfigDevice;
import com.example.sid_fu.blecentral.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.ui.frame.BaseBleConnetFragment;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.widget.LoadingDialog;

/**
 * Created by Administrator on 2016/6/6.
 */
public class BundDeviceFragment extends BaseBleConnetFragment {

    private final static String TAG = BundDeviceFragment.class.getSimpleName();

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

    private MainFrameForStartServiceActivity mActivity;
    public static int leftF = 1;
    public static int rightF = 2;
    public static int leftB = 3;
    public static int rightB =4;
    public static int none =5;
    private int state;
    private DeviceDao deviceDaoUtils;
    private Device deviceDao;
    private LoadingDialog loadDialog;
    private BluetoothLeService mBluetoothLeService;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameForStartServiceActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_bund, container, false);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        super.setManageDevice(mActivity.manageDevice);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initIsCofigBle();
        loadDialog = new LoadingDialog(getActivity());
    }
    @Override
    protected void initData() {
        deviceDaoUtils = App.getDeviceDao();
        deviceDao = new Device();
    }

    @Override
    protected void initRunnable() {

    }

    @Override
    protected void initConfig() {
        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        Logger.d("Try to bindService=" + getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE));
    }

    private void initIsCofigBle() {
        Logger.e(mActivity.deviceDetails.toString()+":"+mActivity.manageDevice.getLeftFDevice()+":"+mActivity.manageDevice.getRightFDevice());
        if(mActivity.manageDevice.getLeftFDevice()==null||mActivity.manageDevice.getLeftFDevice().equals("null")) {
            topleft_next.setText("点击绑定");
            topleft_next.setBackgroundResource(R.mipmap.b_btn);
        }else {
            topleft_next.setText("点击解绑");
            topleft_next.setBackgroundResource(R.mipmap.unbund);
        }
        if(mActivity.manageDevice.getRightFDevice()==null||mActivity.manageDevice.getRightFDevice().equals("null")) {
            topright_next.setText("点击绑定");
            topright_next.setBackgroundResource(R.mipmap.b_btn);
        }else {
            topright_next.setText("点击解绑");
            topright_next.setBackgroundResource(R.mipmap.unbund);
        }
        if(mActivity.manageDevice.getLeftBDevice()==null||mActivity.manageDevice.getLeftBDevice().equals("null")) {
            bottomleft_next.setText("点击绑定");
            bottomleft_next.setBackgroundResource(R.mipmap.b_btn);
        }else {
            bottomleft_next.setText("点击解绑");
            bottomleft_next.setBackgroundResource(R.mipmap.unbund);
        }
        if(mActivity.manageDevice.getRightBDevice()==null||mActivity.manageDevice.getRightBDevice().equals("null")) {
            bottomright_next.setText("点击绑定");
            bottomright_next.setBackgroundResource(R.mipmap.b_btn);
        }else {
            bottomright_next.setText("点击解绑");
            bottomright_next.setBackgroundResource(R.mipmap.unbund);
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
                sendButtonEvent(topleft_next,leftF);
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(topright_next,rightF);
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(bottomleft_next,leftB);
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButtonEvent(bottomright_next,rightB);
            }
        });
    }
    private void sendButtonEvent(TextView tv,int event){
        if(tv.getText().equals(getResources().getString(R.string.unbund))) {
            showNotifyDialog(event);
        }else if(tv.getText().equals(getResources().getString(R.string.unbund_success))) {
            bundDevice(event);
        }
    }
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                Intent it =new Intent(getActivity(),NotifyDialog.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(ConfigDevice.PAIRED_OK,"重试");
                it.putExtra(ConfigDevice.NONE_NEXT,true);
                startActivity(it);
                stopScan();
                timeout = true;
            }
        }
    };
    private void broadcastUpdate(final String action, BluetoothDevice gatt) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        getActivity().sendBroadcast(intent);
    }
    private void unBundDevice(TextView tvNext,MyBluetoothDevice device,int state) {
        tvNext.setText("点击绑定");
        tvNext.setBackgroundResource(R.mipmap.b_btn);
        Logger.e("正在解绑。。。"+state);
        mActivity.mDeviceList.remove(device);
        //保存蓝牙mac地址
        deviceDaoUtils.update(state,mActivity.deviceId,null);
        if(device!=null)
            broadcastUpdate(BluetoothLeService.ACTION_DISCONNECT_SCAN,device.getDevice());
    }
    private void bundDevice(int states) {
        isRecevice = false;
        state = states;
        startScan();
        showDialog("正在识别信号强度。。。",false);
        //ToastUtil.show("开始扫描...");
    }

    /** Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
     * or notification operations.
     **/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                isTimeOut();
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                if(!isWrite)
                    mBluetoothLeService.connect(device.getAddress());
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                if(isTimeOut()) return;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothLeService.writeChar6("AT+USED=1");
                    }
                },2000);
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
                            if(data.length==2&&!isWrite) {
                                onSuccess(device);
                                isWrite = true;
                                Logger.e("解绑成功");
                            }
                        }
                    }
                });
            } else if (BluetoothLeService.ACTION_RETURN_OK.equals(action)/*|| BluetoothLeStartService.SCAN_FOR_RESULT.equals(action)*/) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                int rssi = intent.getIntExtra("RSSI",0);
                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
                Logger.e("绑定==收到广播数据"+rssi);
                for (MyBluetoothDevice ble : mActivity.mDeviceList) {
                    Logger.e("列表中存在的设备："+ble.getDevice().getAddress());
                }
                if(!isRecevice&&!ManageDevice.isBundConfigEquals(mActivity.mDeviceList,device)&&rssi>maxLenght) {
                    isRecevice = true;
                    bleIsFind(device,rssi,scanRecord);
                }
            }else if (NotifyDialog.ACTION_BTN_STATE.equals(action)) {
                startScan();
                mBluetoothLeService.disconnect();
                showDialog("正在识别信号强度。。。",false);
            }else if (NotifyDialog.ACTION_BTN_NEXT.equals(action)) {
                stopScan();
                mBluetoothLeService.disconnect();
                Logger.e("完成"+state);
            }
        }
    };

    private void onSuccess(BluetoothDevice device) {
        switch (state) {
            case 1:
                setUsedToTrue(device,topleft_next,tv_note_left_from,"左前轮");
                mActivity.manageDevice.setLeftFDevice(device.getAddress());
                break;
            case 2:
                setUsedToTrue(device,topright_next,tv_note_right_from,"右前轮");
                mActivity.manageDevice.setRightFDevice(device.getAddress());
                break;
            case 3:
                setUsedToTrue(device,bottomleft_next,tv_note_left_back,"左后轮");
                mActivity.manageDevice.setLeftBDevice(device.getAddress());
                break;
            case 4:
                setUsedToTrue(device,bottomright_next,tv_note_right_back,"右后轮");
                mActivity.manageDevice.setRightBDevice(device.getAddress());
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
        intentFilter.addAction(BluetoothLeStartService.SCAN_FOR_RESULT);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data) {
        ParsedAd ad = DataUtils.parseData(data);
        if(ad == null||ad.datas == null) return;
        if(ad.datas.length == 0) return;
        if(ad.datas.length>=4) {
            int state = DigitalTrans.byteToBin0x0F(ad.datas[0]);
            ManageDevice.status[] statusData = ManageDevice.status.values();
            Logger.e(statusData[state]+"");
            //状态检测
            String buff = statusData[state]+"";
            if(buff.contains("快漏气")||buff.contains("加气")) {
                onSuccess(device);
            } else{
                isRecevice = false;
            }
        }else if(ad.datas.length>=9) {
            int state = DigitalTrans.byteToBin0x0F(ad.datas[0]);
            ManageDevice.status[] statusData = ManageDevice.status.values();
            Logger.e(statusData[state]+"");
            //状态检测
            String buff = statusData[state]+"";
            if(buff.contains("快漏气")||buff.contains("加气")) {
                onSuccess(device);
            } else{
                isRecevice = false;
            }
        }else {
            scanForResult(device);
        }
    }
    private void scanForResult(BluetoothDevice device) {
        stopScan();
        Logger.e("开始连接："+device.getAddress());
        showDialog(device.getAddress()+"正在连接。。。",false);
        //开始连接蓝牙
        mBluetoothLeService.connect(device.getAddress());
    }
    private void setUsedToTrue(BluetoothDevice device,Button currentBtn ,TextView currentTv,String str) {
        //如果出现超时，任何数据都放弃
        if(isTimeOut()) return;
        loadDialog.stopCount();
        Intent it =new Intent(getActivity(),NotifyDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(ConfigDevice.PAIRED_OK,"完成");
        it.putExtra(ConfigDevice.NONE_NEXT,true);
        startActivity(it);
        currentBtn.setText(getResources().getString(R.string.unbund));
        currentBtn.setBackgroundResource(R.mipmap.unbund);
        currentBtn.setVisibility(View.VISIBLE);
        //保存数据到本地
        deviceDaoUtils.update(state,mActivity.deviceId,device.getAddress());
        currentTv.setText(str+"：\n"+device.getAddress());
        mBluetoothLeService.disconnect();
        state = none;
    }
    private void showDialog(String str,boolean isConnect) {
        if(!loadDialog.isShowing()) {
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
    }
    private void showDialog(String str) {
        if(!loadDialog.isShowing()) {
            loadDialog.setText(str);
            loadDialog.show();
        }else{
            loadDialog.stopCount();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mGattUpdateReceiver);
            getActivity().unbindService(mServiceConnection);
        }catch (IllegalArgumentException e) {
            Logger.e("mHomeKeyEventReceiver:"+e.toString());
        }
    }

    @Override
    public void broadcastUpdate(String action, BluetoothDevice gatt, int rssi, byte[] scanResult) {
        for (MyBluetoothDevice ble : mActivity.mDeviceList) {
            Logger.e("列表中存在的设备："+ble.getDevice().getAddress());
        }
        if(!isRecevice&&!isBundBle(gatt)&&rssi>maxLenght) {
            isRecevice = true;
            bleIsFind(gatt,rssi,scanResult);
        }
    }
    private boolean isBundBle(BluetoothDevice device) {
        if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice()))
            return true;
        if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice()))
            return true;
        if(device.getAddress().equals(mActivity.manageDevice.getLeftBDevice()))
            return true;
        if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice()))
            return true;
        return false;
    }
    /**
     *
     */
    private void showNotifyDialog(final int state) {
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
                                mActivity.manageDevice.setLeftFDevice(null);
                                break;
                            case 2:
                                unBundDevice(topright_next,mActivity.rightFDevice,rightF);
                                mActivity.manageDevice.setRightFDevice(null);
                                break;
                            case 3:
                                unBundDevice(bottomleft_next,mActivity.leftBDevice,leftB);
                                mActivity.manageDevice.setLeftBDevice(null);
                                break;
                            case 4:
                                unBundDevice(bottomright_next,mActivity.rightBDevice,rightB);
                                mActivity.manageDevice.setRightBDevice(null);
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
        mBluetoothLeService.disconnect();
    }
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            Log.e(TAG, "mBluetoothLeService is okay");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean isTimeOut(){
        if(timeout) {
            timeout = false;
            isWrite = true;
            mBluetoothLeService.disconnect();
            return true;
        }
        return false;
    }
}
