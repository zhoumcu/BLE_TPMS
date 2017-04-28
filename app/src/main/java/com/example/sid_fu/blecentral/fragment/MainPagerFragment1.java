package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.ParsedAd;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.LoadingDialog;
import com.example.sid_fu.blecentral.widget.WiperSwitch;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/6/6.
 */
public class MainPagerFragment1 extends Fragment implements View.OnClickListener{
    private TextView editConfig;
    private WiperSwitch switch1;
    private ImageView imgTopleft;
    private TextView topleftAdjust;
    private ImageView imgTopright;
    private TextView toprightAdjust;
    private ImageView imgBottomleft;
    private TextView bottomleftAdjust;
    private ImageView imgBottomright;
    private TextView bottomrightAdjust;
    private TextView topleft_preesure;
    private TextView topleft_temp;
    private TextView topright_preesure;
    private TextView topright_temp;
    private TextView bottomleft_preesure;
    private TextView bottomleft_temp;
    private TextView bottomright_preesure;
    private TextView bottomright_temp;
    private TextView topleft_note;
    private TextView topright_note;
    private TextView bottomleft_note;
    private TextView bottomright_note;
    private TextView topleft_voltage;
    private TextView topright_voltage;
    private TextView bottomleft_voltage;
    private TextView bottomright_voltage;
    private TextView topleft_releat;
    private TextView topright_releat;
    private TextView bottomleft_releat;
    private TextView bottomright_releat;

    private int index = 0;
    private int indexRe = 0;
    private boolean isFirst = false;
    private ProgressDialog progressDialog;
    private int currentsecond;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int timeCount = 30;
    private int connectedNum;
    private static final int CONNECTTIME = 8;//单位s
    private static final int RECONNECTTIME = 8;//单位s
    private static final int CONNECT = 1001;
    private int connectCount = 8 ;//单位s
    private int recConnectCount ;//单位s
    private String preStr;
    private String curStr;
    private LoadingDialog loadDialog;
    private int indexConnect;
    private String engFh = "";
    private DecimalFormat df1;
    private DecimalFormat df;
    private int count;
    private boolean isDisconnect = false;
    private Handler mHandler;
    private SimpleDateFormat d;
    private String nowtime;
    private MainFrameActivity mActivity;
    public  MyBluetoothDevice leftBDevice =null;
    public  MyBluetoothDevice leftFDevice =null ;
    public  MyBluetoothDevice rightFDevice =null;
    public  MyBluetoothDevice rightBDevice =null;
    public  List<MyBluetoothDevice> mDeviceList = new ArrayList<>();
    public  ManageDevice manageDevice;
    private TextView topleft_phone_rssi;
    private TextView topright_phone_rssi;
    private TextView bottomleft_phone_rssi;
    private TextView bottomright_phone_rssi;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameActivity) activity;
//        leftBDevice = mActivity.leftBDevice;
//        leftFDevice =mActivity.leftFDevice ;
//        rightFDevice =mActivity.rightFDevice;
//        rightBDevice =mActivity.rightBDevice;
//        mDeviceList = mActivity.mDeviceList;
//        manageDevice = mActivity.manageDevice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_pressure, container, false);
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }
    private void initUI() {
        editConfig = (TextView) getView().findViewById(R.id.edit_config);
        switch1 = (WiperSwitch) getView().findViewById(R.id.switch1);
        imgTopleft = (ImageView) getView().findViewById(R.id.img_topleft);
        imgTopright = (ImageView) getView().findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) getView().findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) getView().findViewById(R.id.img_bottomright);
        topleftAdjust = (TextView) getView().findViewById(R.id.topleft_adjust);
        toprightAdjust = (TextView) getView().findViewById(R.id.topright_adjust);
        bottomleftAdjust = (TextView) getView().findViewById(R.id.bottomleft_adjust);
        bottomrightAdjust = (TextView) getView().findViewById(R.id.bottomright_adjust);

        topleft_preesure = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
        topleft_note = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
        topleft_voltage = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_voltage);
        topleft_releat = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);
        topleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.phone_rssi);

        topright_preesure = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
        topright_temp = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
        topright_note = (TextView)getView(). findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
        topright_voltage = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_voltage);
        topright_releat = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);
        topright_phone_rssi = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.phone_rssi);

        bottomleft_preesure = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
        bottomleft_temp = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
        bottomleft_note = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
        bottomleft_voltage = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_voltage);
        bottomleft_releat = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);
        bottomleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.phone_rssi);

        bottomright_preesure = (TextView)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
        bottomright_temp = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
        bottomright_note = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
        bottomright_voltage = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_voltage);
        bottomright_releat = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);
        bottomright_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.phone_rssi);

        bottomleftAdjust.setOnClickListener(this);
        bottomrightAdjust.setOnClickListener(this);
        topleftAdjust.setOnClickListener(this);
        toprightAdjust.setOnClickListener(this);
        editConfig.setOnClickListener(this);

        loadDialog = new LoadingDialog(getActivity());
        loadDialog.setText("设备扫描中...");
        loadDialog.show();

        //set timer to scan ble that is on changing
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1000,1000);

        df1=new DecimalFormat("#.##");
        df=new DecimalFormat("#.#");

        d= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
        nowtime=d.format(new Date());//按以上格式 将当前时间转换成字符串
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0)
                {
                    String testtime=d.format(new Date());//按以上格式 将当前时间转换成字符串
                    try {
                        long result=(d.parse(testtime).getTime()-d.parse(nowtime).getTime())/1000;
                        ToastUtil.show("扫描四个耗时：" + result+"秒");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }else if(msg.what==1)
                {
                    showDialog("正在拼命扫描中。。。",mActivity.mDeviceList.size());
                }
            }
        };
    }

    @Override
    public void onClick(View v) {

    }
    private  class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            timeCount--;
//            multConnect();
            connect();
        }

    }

    /**
     * 旧方法
     */
    private void connect()
    {
        //30s扫描超时自动连接
        if(mActivity.mDeviceList.size()!=4&&timeCount<=0)
        {
            if(mActivity.mDeviceList.size()==0)
            {
                mActivity.startScan();
            }
            loadDialog.dismiss();
            timeCount = 30;
            App.getInstance().speak("蓝牙扫描超时，正在尝试新的扫描");
            Logger.e("未扫描到4个设备，且连接超时，断开扫描再重新扫描！");
        }else
        if(mActivity.mDeviceList.size()==4)
        {
            loadDialog.dismiss();
//            App.getInstance().speak("扫描完毕");
            /*
            //.sendEmptyMessage(CONNECT);
//            if(mScanning&&timeCount<30)
//            {
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                mScanning = false;
//                Logger.e(TAG,"全部扫描到,且关闭扫描"+mDeviceList.size());
//            }else
//            {
                Logger.e("扫描超时，自动连接"+mActivity.mDeviceList.size());
//            }
            if(index<mActivity.mDeviceList.size())
            {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                mActivity.mDeviceList.get(index).connectBle(mActivity.mDeviceList.get(index));
//                    }
//                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                index = 5;
                Logger.e("重新连接"+mActivity.mDeviceList.size());
                if(indexRe<mActivity.mDeviceList.size())
                {
                    if(!mActivity.mDeviceList.get(indexRe).isSuccessComm()&&!mActivity.mDeviceList.get(indexRe).isRequestConnect())
                    {
                        Logger.e("i:"+indexRe);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
                        mActivity.mDeviceList.get(indexRe).reConnectBle(mActivity.mDeviceList.get(indexRe));
//                            }
//                        });
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else
                {
                    indexRe = -1;
                }
                indexRe++;

            }
            index++;
            */
        }else {

            mHandler.sendEmptyMessage(1);
            Logger.e("扫描中。。。。。。"+mActivity.mDeviceList.size());
        }
    }

    private synchronized void multConnect()
    {
        if(mActivity.mDeviceList.size()==4||timeCount>30)
        {
            loadDialog.dismiss();
            if(index<mActivity.mDeviceList.size())
            {
                if(!mActivity.mDeviceList.get(index).isRequestConnect()&&!mActivity.mDeviceList.get(index).isSuccessComm())
                {
                    mDeviceList.get(index).reConnectBle(mActivity.mDeviceList.get(index));
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!mDeviceList.get(index).isSuccessComm())
//                            {
//                                index++;
//                            }
//                        }
//                    },10000);
                }
                if(mActivity.mDeviceList.get(index).isSuccessComm()||!mActivity.mDeviceList.get(index).isRequestConnect()/*||mDeviceList.get(index).isTimeout()*/)
                    index++;
            }else
            {
                index = 0;
            }
            /*else
            {
                index = 5;
                if(indexRe<mDeviceList.size())
                {
                    if(!mDeviceList.get(indexRe).isRequestConnect()&&!mDeviceList.get(indexRe).isSuccessComm())
                    {
                        mDeviceList.get(indexRe).reConnectBle();
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(!mDeviceList.get(indexRe).isSuccessComm())
//                                {
//                                    indexRe++;
//                                }
//                            }
//                        },10000);
                    }
                    if(mDeviceList.get(indexRe).isSuccessComm()||!mDeviceList.get(index).isRequestConnect())
                        indexRe++;
                }else
                {
                    indexRe = -1;
                }
            }*/
        }
    }
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
                Logger.e("connected:"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                onFailed(device);
                bleIsDisConnected(device.getAddress());
                //invalidateOptionsMenu();
                Logger.e("Disconneted GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //invalidateOptionsMenu();
                bleIsConnected(device.getAddress());
                onSuccess(device);
                Logger.e("Discover GATT Services"+device.getAddress());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功
//                        onSuccess(device);
                        if (data != null) {
                            //Logger.e(data);
                            //bleIsConnected(device.getAddress());
                            bleStringToDouble(device,false,data);
                        }
                    }
                });
            } else if (BluetoothLeService.ACTION_CHANGE_RESULT.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                int rssi = intent.getIntExtra("RSSI",0);
                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
                Logger.e("收到广播数据");
                ParsedAd ad = DataUtils.parseData(scanRecord);
                bleIsFind(device);
                bleStringToDouble(device,true,ad.datas);
                showRssi(device, rssi);
            } else if (BluetoothLeService.ACTION_NAME_RSSI.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        int rssi = intent.getIntExtra("RSSI", 0);
                        if (Constants.IS_TEST)
                            showRssi(device, rssi);
                    }
                });
            }else if (BluetoothLeService.ACTION_DISCONNECT_SCAN.equals(action)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        Logger.e("解绑设备："+device.getAddress());
                       bleIsDisConnected(device.getAddress());
                    }
                });
            }

        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_CHANGE_RESULT);
        intentFilter.addAction(BluetoothLeService.ACTION_NAME_RSSI);
        intentFilter.addAction(BluetoothLeService.ACTION_DISCONNECT_SCAN);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }
    private void onSuccess(BluetoothDevice device)
    {
        if(mActivity.leftFDevice!=null&&mActivity.leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,mActivity.leftFDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_LF,false))
            {
                mActivity.leftFDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LF,true);
            }
            if(Constants.IS_TEST)
            mActivity.leftFDevice.writeChar6(Constants.RSSI_ON);
        }else
        if(mActivity.rightFDevice!=null&&mActivity.rightFDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,mActivity.rightFDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_RF,false))
            {
                mActivity.rightFDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RF,true);
            }
            if(Constants.IS_TEST)
            mActivity.rightFDevice.writeChar6(Constants.RSSI_ON);
        }else
        if(mActivity.leftBDevice!=null&&mActivity.leftBDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,mActivity.leftBDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_LB,false))
            {
                mActivity.leftBDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_LB,true);
            }
            if(Constants.IS_TEST)
            mActivity.leftBDevice.writeChar6(Constants.RSSI_ON);
        }else
        if(mActivity.rightBDevice!=null&&mActivity.rightBDevice.getDevice().equals(device))
        {
            ManageDevice.setSuccess(device,mActivity.rightBDevice);
            if(!SharedPreferences.getInstance().getBoolean(Constants.FIRST_USED_RB,false))
            {
                mActivity.rightBDevice.writeChar6("AT+USED=1");
                SharedPreferences.getInstance().putBoolean(Constants.FIRST_USED_RB,true);
            }
            if(Constants.IS_TEST)
            mActivity.rightBDevice.writeChar6(Constants.RSSI_ON);
        }
    }
    private  void onFailed(BluetoothDevice device) {
        //记录因其他原因造成蓝牙断开，实现重连机制
        if(mActivity.leftFDevice!=null&&mActivity.leftFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,mActivity.leftFDevice);
        }else
        if(mActivity.rightFDevice!=null&&mActivity.rightFDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,mActivity.rightFDevice);
        }else
        if(mActivity.leftBDevice!=null&&mActivity.leftBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,mActivity.leftBDevice);
        }else
        if(mActivity.rightBDevice!=null&&mActivity.rightBDevice.getDevice().equals(device))
        {
            ManageDevice.setFailed(device,mActivity.rightBDevice);
        }
        if(isDisconnect)
        {
            Logger.e("主动退出应用");
            for(MyBluetoothDevice ble : mActivity.mDeviceList)
            {
                if(ble.getDevice().equals(device))
                {
                    if(!ble.isSuccessComm())
                        count++;
                }
            }
            if(count==mActivity.mDeviceList.size())
            //if(!rightFDevice.isSuccessComm()&&!rightBDevice.isSuccessComm()&&!leftBDevice.isSuccessComm()&&!leftFDevice.isSuccessComm())
            {
                Logger.e("已经关闭所有蓝牙设备了！！！！！！！！");
                if(progressDialog!=null)
                    progressDialog.dismiss();
                getActivity().finish();
            }
        }
    }
    private void bleIsFind(BluetoothDevice device)
    {
        String strAddress = device.getAddress();
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if(mActivity.mDeviceList.size()==4)
        {
            mHandler.sendEmptyMessage(0);
        }
    }
    private void bleIsConnected(String strAddress)
    {
//        Logger.e("ble is connect"+strAddress);
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setBackgroundColor(getResources().getColor(R.color.white));
            //mActivity.leftBDevice.setRequestConnect(true);
//            leftBDevice.setGattCharacteristic_char6(gattCharacteristic);

        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setBackgroundColor(getResources().getColor(R.color.white));
            //mActivity.rightBDevice.setRequestConnect(true);
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setBackgroundColor(getResources().getColor(R.color.white));
            //mActivity.leftFDevice.setRequestConnect(true);
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setBackgroundColor(getResources().getColor(R.color.white));
            //mActivity.rightFDevice.setRequestConnect(true);
        }
    }
    public void bleIsDisConnected(String strAddress)
    {
//        Logger.e("ble is disconnect"+strAddress);
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            if(mActivity.leftBDevice!=null&&!mActivity.leftBDevice.isTimeOutDisconnect())
            {
                Logger.e("解绑成功");
                mActivity.manageDevice.setLeftBDevice("");
                imgBottomleft.setBackground(null);
                bottomleft_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            if(mActivity.rightBDevice!=null&&!mActivity.rightBDevice.isTimeOutDisconnect()) {
                mActivity.manageDevice.setRightBDevice("");
                imgBottomright.setBackground(null);
                bottomright_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            if(mActivity.leftFDevice!=null&&!mActivity.leftFDevice.isTimeOutDisconnect()) {
                mActivity.manageDevice.setLeftFDevice("");
                imgTopleft.setBackground(null);
                topleft_note.setVisibility(View.GONE);
            }
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            if(mActivity.rightFDevice!=null&&!mActivity.rightFDevice.isTimeOutDisconnect()) {
                mActivity.manageDevice.setRightFDevice("");
                imgTopright.setBackground(null);
                topright_note.setVisibility(View.GONE);
            }
        }
    }
    private void broadcastUpdate(final String action, BluetoothDevice gatt) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        getActivity().sendBroadcast(intent);
    }
    private boolean isLeftF,isRightF,isRightB,isLeftB;
    private void showRssi(BluetoothDevice device,int rssi)
    {
        if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_phone_rssi.setVisibility(View.VISIBLE);
            topleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftF)
            {
                isLeftF = false;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isLeftF = true;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }

        }else if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_phone_rssi.setVisibility(View.VISIBLE);
            topright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightF)
            {
                isRightF = false;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isRightF = true;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getLeftBDevice()))
        {
            bottomleft_phone_rssi.setVisibility(View.VISIBLE);
            bottomleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftB)
            {
                isLeftB = false;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isLeftB = true;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_phone_rssi.setVisibility(View.VISIBLE);
            bottomright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightB)
            {
                isRightB = false;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else
            {
                isRightB = true;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }
    private void bleStringToDouble(BluetoothDevice device, boolean isNotify,byte[] data)
    {
        float voltage = 0.00f;
        float press = 0;
        int temp =0;
        int state = 0;
        int rssi = 0;
        BleData bleData = new BleData();
        Logger.e(DigitalTrans.byte2hex(data));
        if(data==null) return;
        if(isNotify&&data.length==4)
        {
            press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            voltage = ((float)(DigitalTrans.byteToAlgorism(data[3])-31)*20/21+160)/100;
            temp = DigitalTrans.byteToAlgorism(data[2])-50;
//            state = DigitalTrans.byteToBin(data[0]);
            state = DigitalTrans.byteToBin0x0F(data[0]);
        }else
        {
            if(data.length==5&Constants.IS_TEST)
            {
                voltage = ((float)(DigitalTrans.byteToAlgorism(data[0])-31)*20/21+160)/100;
                temp = DigitalTrans.byteToAlgorism(data[1])-50;
                press = ((float)DigitalTrans.byteToAlgorism(data[2])*160)/51/100;
                state = DigitalTrans.byteToBin0x0F(data[3]);
                rssi = 256-DigitalTrans.byteToAlgorism(data[4]);
                Logger.e("信号强度：-"+rssi);
            }else if(data.length==4)
            {
                voltage = ((float)(DigitalTrans.byteToAlgorism(data[0])-31)*20/21+160)/100;
                temp = DigitalTrans.byteToAlgorism(data[1])-50;
                press = ((float)DigitalTrans.byteToAlgorism(data[2])*160)/51/100;
                state = DigitalTrans.byteToBin0x0F(data[3]);
            }else if(data.length==2)
            {
                broadcastUpdate(BluetoothLeService.ACTION_SEND_OK,device);
                return;
            }
        }
        Logger.e("状态："+state+"\n");
        Logger.e("压力值："+press+"\n");
        Logger.e("温度："+temp+"\n");
        Logger.e("电压"+voltage+"");

        bleData.setTemp (temp);
        bleData.setPress(press);
        bleData.setStatus(state);

        if(device.getAddress().equals(mActivity.manageDevice.getLeftBDevice()))
        {
            bottomleft_preesure.setText(df.format(press));
            bottomleft_temp.setText(String.valueOf(temp));
            bottomleft_voltage.setText(df1.format(voltage));
            bottomleft_releat.setText("-"+rssi);
            handleException(bleData, "左后轮\n"+ mActivity.manageDevice.getLeftBDevice()+"\n", bottomleft_note, imgBottomleft);

        }else if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_preesure.setText(df.format(press));
            bottomright_temp.setText(String.valueOf(temp));
            bottomright_voltage.setText(df1.format(voltage));
            bottomright_releat.setText("-"+rssi);
            handleException(bleData, "右后轮\n"+ mActivity.manageDevice.getRightBDevice()+"\n", bottomright_note, imgBottomright);
        }else if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_preesure.setText(df.format(press));
            topleft_temp.setText(String.valueOf(temp));
            topleft_voltage.setText(df1.format(voltage));
            topleft_releat.setText("-"+rssi);
            handleException(bleData, "左前轮\n"+ mActivity.manageDevice.getLeftFDevice()+"\n", topleft_note, imgTopleft);
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_preesure.setText(df.format(press));
            topright_temp.setText(String.valueOf(temp));
            topright_voltage.setText(df1.format(voltage));
            topright_releat.setText("-"+rssi);
            handleException(bleData, "右前轮\n"+ mActivity.manageDevice.getRightFDevice()+"\n", topright_note, imgTopright);
        }
    }
    private void handleException(BleData date, String str, TextView v, ImageView img) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(str + ":");
        buffer.append(date.getPress() > 3.2 ? "高压" + "\n" : "");
        buffer.append(date.getPress() < 1.8 ? "低压" + "\n" : "");
        buffer.append(date.getTemp() > 35 ? "高温" + "\n" : "");
        buffer.append(date.getTemp() < 20 ? "低温" + "\n" : "");
        ManageDevice.status[] statusData = ManageDevice.status.values();
        buffer.append(statusData[date.getStatus()] + "\n");
//        buffer.append(date.getSensorFailure() == 1 ? "传感器失效" + "\n" : "");
//        buffer.append(date.getSensorLow() == 1 ? "传感器低电" + "\n" : "");
//        buffer.append(date.getLeakage() == 1 ? "慢漏气" + "\n" : "");
//        buffer.append(date.getLeakageQucik() == 1 ? "快漏气" + "\n" : "");

        if (!TextUtils.isEmpty(buffer) && buffer != null && !buffer.toString().equals(str + ":")) {
//            img.setBackgroundColor(getResources().getColor(R.color.white));
//            img.setAnimation(alphaAnimation1);
//            alphaAnimation1.start();
            v.setVisibility(View.VISIBLE);
            v.setText(buffer.toString());
//            ToastUtil.show(buffer.toString() + "异常警报");
//            showDialog(buffer.toString());
        } else {
//            if (alphaAnimation1.isFillEnabled())
//                alphaAnimation1.cancel();
//            img.setBackground(null);
            v.setVisibility(View.GONE);
        }

    }
    private void showDialog(String str,int number)
    {
        if(!loadDialog.isShowing())
        {
            loadDialog.setText(str);
            loadDialog.show();
            loadDialog.setCountNum(number);
        }else{
            loadDialog.setCountNum(number);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        myTimerTask.cancel();
        myTimerTask = null;
        timer = null;
        getActivity().unregisterReceiver(mGattUpdateReceiver);
//        if(!isDisconnect)
//        {
//            if(myTimerTask!=null)
//                myTimerTask.cancel();
//            timer = null;
//            if (mActivity.mBluetoothLeService != null) {
//                mActivity.mBluetoothLeService.disconnect();
//                //mActivity.mBluetoothLeService.close();
//                //mActivity.mBluetoothLeService = null;
//            }
//        }
    }
}
