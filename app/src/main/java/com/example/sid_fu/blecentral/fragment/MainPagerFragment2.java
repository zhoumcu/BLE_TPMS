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
import com.example.sid_fu.blecentral.ScanDeviceRunnable;
import com.example.sid_fu.blecentral.activity.MainFrameActivity;
import com.example.sid_fu.blecentral.db.DbHelper;
import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.SoundManager;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.LoadingDialog;

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
public class MainPagerFragment2 extends Fragment implements View.OnClickListener{
    private static final long DISTIME = 180000;
    //    private TextView editConfig;
//    private WiperSwitch switch1;
    private ImageView imgTopleft;
//    private TextView topleftAdjust;
    private ImageView imgTopright;
//    private TextView toprightAdjust;
    private ImageView imgBottomleft;
//    private TextView bottomleftAdjust;
    private ImageView imgBottomright;
//    private TextView bottomrightAdjust;
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
    private ImageView topleft_voltage;
    private ImageView topright_voltage;
    private ImageView bottomleft_voltage;
    private ImageView bottomright_voltage;
    private TextView topleft_releat;
    private TextView topright_releat;
    private TextView bottomleft_releat;
    private TextView bottomright_releat;
    private ProgressDialog progressDialog;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int timeCount = 30;
    private static final int CONNECTTIME = 8;//单位s
    private static final int RECONNECTTIME = 8;//单位s
    private static final int CONNECT = 1001;
    private int connectCount = 8 ;//单位s
    private int recConnectCount ;//单位s
    private LoadingDialog loadDialog;
    private DecimalFormat df1;
    private DecimalFormat df;
    private SimpleDateFormat d;
    private String nowtime;
    private MainFrameActivity mActivity;
    public  MyBluetoothDevice leftBDevice =null;
    public  MyBluetoothDevice leftFDevice =null ;
    public  MyBluetoothDevice rightFDevice =null;
    public  MyBluetoothDevice rightBDevice =null;
    private TextView topleft_phone_rssi;
    private TextView topright_phone_rssi;
    private TextView bottomleft_phone_rssi;
    private TextView bottomright_phone_rssi;
    private ScanDeviceRunnable leftFRunnable;
    private ScanDeviceRunnable rightFRunnable;
    private ScanDeviceRunnable leftBRunnable;
    private ScanDeviceRunnable rightBRunnable;
    private List<RecordData> recordDatas = new ArrayList<>();
    private TextView pressBottomleft;
    private TextView pressTopright;
    private TextView pressTopleft;
    private TextView pressBottomright;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mActivity = (MainFrameActivity) activity;
        }catch (IllegalStateException e)
        {
            Logger.e(e.toString());
        }
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
        initRunnable();
        initUI();
//        initSound();
//        recordDao =  new RecordDao(getActivity());
//        Logger.e(recordDao.get(0).toString());
        initData();
    }

    /**
     * 获取数据库上一次关闭前保存的胎压数据
     */
    private void initData()
    {
        recordDatas =  DbHelper.getInstance(getActivity()).getCarDataList(mActivity.deviceId);
        for (RecordData data :recordDatas)
        {
            bleStringToDouble(data);
        }
    }

    /**
     * 初始化 用于获取数据超时或者断开连接进行判断
     */
    private void initRunnable() {
        leftFRunnable = new ScanDeviceRunnable(mHandler,1001);
        rightFRunnable = new ScanDeviceRunnable(mHandler,1002);
        leftBRunnable = new ScanDeviceRunnable(mHandler,1003);
        rightBRunnable = new ScanDeviceRunnable(mHandler,1004);
    }
    private Handler  mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 0:
                    String testtime=d.format(new Date());//按以上格式 将当前时间转换成字符串
                    try {
                        long result=(d.parse(testtime).getTime()-d.parse(nowtime).getTime())/1000;
                        ToastUtil.show("扫描四个耗时：" + result+"秒");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case  1:
//                    showDialog("正在拼命扫描中。。。",mActivity.mDeviceList.size());
                    break;
                case 2:
                    disFind((Integer)msg.obj);
                    break;
            }
        }
    };
    private void initUI() {
        topleft_preesure = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
        topleft_note = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
        topleft_voltage = (ImageView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.img_battle);
        topleft_releat = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);
        topleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.phone_rssi);
        imgTopleft =  (ImageView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.img_topleft);
        pressTopleft = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.press);
        TextView tempunitTopleft = (TextView) getView().findViewById(R.id.ll_topleft).findViewById(R.id.tempunit);

        topright_preesure = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
        topright_temp = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
        topright_note = (TextView)getView(). findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
        topright_voltage = (ImageView) getView().findViewById(R.id.ll_topright).findViewById(R.id.img_battle);
        topright_releat = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);
        topright_phone_rssi = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.phone_rssi);
        imgTopright =  (ImageView) getView().findViewById(R.id.ll_topright).findViewById(R.id.img_topleft);
        pressTopright = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.press);
        TextView tempunitTopright = (TextView) getView().findViewById(R.id.ll_topright).findViewById(R.id.tempunit);

        bottomleft_preesure = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
        bottomleft_temp = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
        bottomleft_note = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
        bottomleft_voltage = (ImageView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.img_battle);
        bottomleft_releat = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);
        bottomleft_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.phone_rssi);
        imgBottomleft =  (ImageView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.img_topleft);
        pressBottomleft = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.press);
        TextView tempunitBottomleft = (TextView) getView().findViewById(R.id.ll_bottomleft).findViewById(R.id.tempunit);

        bottomright_preesure = (TextView)getView(). findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
        bottomright_temp = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
        bottomright_note = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
        bottomright_voltage = (ImageView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.img_battle);
        bottomright_releat = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);
        bottomright_phone_rssi = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.phone_rssi);
        imgBottomright =  (ImageView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.img_topleft);
        pressBottomright = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.press);
        TextView tempunitBottomright = (TextView) getView().findViewById(R.id.ll_bottomright).findViewById(R.id.tempunit);

        pressTopleft.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressTopright.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressBottomleft.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        pressBottomright.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        tempunitTopleft.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitTopright.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitBottomleft.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tempunitBottomright.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));

//        loadDialog = new LoadingDialog(getActivity());
//        loadDialog.setText("设备扫描中...");
//        loadDialog.show();

        //set timer to scan ble that is on changing
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1000,1000);

        df1=new DecimalFormat("######0.00");
        df=new DecimalFormat("######0.0");

        d= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间
        nowtime=d.format(new Date());//按以上格式 将当前时间转换成字符串

        //1分钟报警重复提醒
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
                mActivity.manageDevice.leftB_notify = false;
                mActivity.manageDevice.leftF_notify = false;
                mActivity.manageDevice.rightB_notify = false;
                mActivity.manageDevice.rightF_notify = false;
                mHandler.postDelayed(this, 60000);
                Logger.e("重复报警");
            }
        };
        mHandler.postDelayed(runnable, 60000);
    }

    @Override
    public void onClick(View v) {

    }
    private  class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            timeCount--;
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
//            loadDialog.dismiss();
            timeCount = 30;
            App.getInstance().speak("蓝牙扫描超时，请确保已经添加了4个传感器，正在尝试新的扫描");
            Logger.e("未扫描到4个设备，且连接超时，断开扫描再重新扫描！");
        }else if(mActivity.mDeviceList.size()==4)
        {
//            loadDialog.dismiss();
//          App.getInstance().speak("扫描完毕");
        }else {

//            mHandler.sendEmptyMessage(1);
            Logger.e("扫描中。。。。。。"+mActivity.mDeviceList.size());
        }
    }

    /**
     *  Handles various events fired by the Service.
     * ACTION_GATT_CONNECTED: connected to a GATT server.
     * ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     * ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     * ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_CHANGE_RESULT.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                int rssi = intent.getIntExtra("RSSI",0);
                byte[] scanRecord = intent.getByteArrayExtra("SCAN_RECORD");
                ParsedAd ad = DataUtils.parseData(scanRecord);
                bleIsFind(device.getAddress(),"");
                bleStringToDouble(device,true,ad.datas);
                showRssi(device, rssi);
                Logger.e("收到广播数据");
            }else if(BluetoothLeService.ACTION_DISCONNECT_SCAN.equals(action))
            {
                //解绑断开
                Logger.e("解绑断开");
            }
        }
    };

    /**
     * 注册广播
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_CHANGE_RESULT);
        intentFilter.addAction(BluetoothLeService.ACTION_DISCONNECT_SCAN);
        return intentFilter;
    }

    /**
     * 扫描发现设备 UI界面状态改变
     * @param state
     */
    private void disFind(int state)
    {
        try{
            RecordData recordData = new RecordData();
            recordData.setData(null);
            recordData.setDeviceId(mActivity.deviceId);
            if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false))
            {
                getDataTimeOutForDay(recordData,state);
            }else
            {
                getDataTimeOutForNight(recordData,state);
            }
        }catch (IllegalStateException e)
        {
            Logger.e("TIME_OUT",e.toString());
        }
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
//                broadcastUpdate(BluetoothLeService.ACTION_SEND_OK,device);
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
//            bottomleft_voltage.setText(df1.format(voltage));
//            bottomleft_releat.setText("-"+rssi);
            handleVoltageShow(bottomleft_voltage,voltage);
            handleException(bleData, mActivity.manageDevice.getLeftBDevice());
            //开启定时器用于监听数据
            if(mActivity.manageDevice.disLeftBCount==1)
            {
                mHandler.removeCallbacks(leftBRunnable);
                mActivity.manageDevice.disLeftBCount = 0;
            }
            mActivity.manageDevice.disLeftBCount++;
            mHandler.postDelayed(leftBRunnable, DISTIME);// 打开定时器，执行操作
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_preesure.setText(df.format(press));
            bottomright_temp.setText(String.valueOf(temp));
//            bottomright_voltage.setText(df1.format(voltage));
//            bottomright_releat.setText("-"+rssi);
            handleVoltageShow(bottomright_voltage,voltage);
            handleException(bleData, mActivity.manageDevice.getRightBDevice());
            if(mActivity.manageDevice.disRightBCount==1)
            {
                mHandler.removeCallbacks(rightBRunnable);
                mActivity.manageDevice.disRightBCount = 0;
            }
            mActivity.manageDevice.disRightBCount++;
            mHandler.postDelayed(rightBRunnable, DISTIME);// 打开定时器，执行操作

        }else if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_preesure.setText(df.format(press));
            topleft_temp.setText(String.valueOf(temp));
//            topleft_voltage.setText(df1.format(voltage));
//            topleft_releat.setText("-"+rssi);
            handleException(bleData, mActivity.manageDevice.getLeftFDevice());
            handleVoltageShow(topleft_voltage,voltage);
            if(mActivity.manageDevice.disLeftFCount==1)
            {
                mHandler.removeCallbacks(leftFRunnable);
                mActivity.manageDevice.disLeftFCount = 0;
            }
            mActivity.manageDevice.disLeftFCount++;
            mHandler.postDelayed(leftFRunnable, DISTIME);// 打开定时器，执行操作

        }else if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_preesure.setText(df.format(press));
            topright_temp.setText(String.valueOf(temp));
//            topright_voltage.setText(df1.format(voltage));
//            topright_releat.setText("-"+rssi);
            handleException(bleData, mActivity.manageDevice.getRightFDevice());
            handleVoltageShow(topright_voltage,voltage);
            if(mActivity.manageDevice.disRightFCount==1)
            {
                mHandler.removeCallbacks(rightFRunnable);
                mActivity.manageDevice.disRightFCount = 0;
            }
            mActivity.manageDevice.disRightFCount++;
            mHandler.postDelayed(rightFRunnable, DISTIME);// 打开定时器，执行操作
        }
        RecordData recordData = new RecordData();
        recordData.setName(device.getAddress());
        recordData.setData(DigitalTrans.byte2hex(data));
        recordData.setDeviceId(mActivity.deviceId);
        DbHelper.getInstance(getActivity()).update(mActivity.deviceId,device.getAddress(),recordData);
    }
    private void bleStringToDouble(RecordData recordData)
    {
        float voltage = 0.00f;
        float press = 0;
        int temp =0;
        int state = 0;
        int rssi = 0;
        byte[] data;
        BleData bleData = new BleData();
        data = DigitalTrans.hex2byte(recordData.getData());
        if(data==null) return;
        if(data.length==4)
        {
            press = ((float)DigitalTrans.byteToAlgorism(data[1])*160)/51/100;
            voltage = ((float)(DigitalTrans.byteToAlgorism(data[3])-31)*20/21+160)/100;
            temp = DigitalTrans.byteToAlgorism(data[2])-50;
//            state = DigitalTrans.byteToBin(data[0]);
            state = DigitalTrans.byteToBin0x0F(data[0]);
        }
        Logger.e("状态："+state+"\n"+"压力值："+press+"\n"+"温度："+temp+"\n"+"电压"+voltage+"");
        bleData.setTemp (temp);
        bleData.setPress(press);
        bleData.setStatus(state);
        if(recordData.getName().equals(mActivity.manageDevice.getLeftBDevice()))
        {
            bottomleft_preesure.setText(df.format(press));
            bottomleft_temp.setText(String.valueOf(temp));
//            bottomleft_voltage.setText(df1.format(voltage));
//            bottomleft_releat.setText("-"+rssi);
            handleVoltageShow(bottomleft_voltage,voltage);
            handleException(bleData, mActivity.manageDevice.getLeftBDevice());
            //开启定时器用于监听数据
//            if(mActivity.manageDevice.disLeftBCount==1)
//            {
//                mHandler.removeCallbacks(leftBRunnable);
//                mActivity.manageDevice.disLeftBCount = 0;
//            }
//            mActivity.manageDevice.disLeftBCount++;
//            mHandler.postDelayed(leftBRunnable, DISTIME);// 打开定时器，执行操作
        }else if(recordData.getName().equals(mActivity.manageDevice.getRightBDevice()))
        {
            bottomright_preesure.setText(df.format(press));
            bottomright_temp.setText(String.valueOf(temp));
//            bottomright_voltage.setText(df1.format(voltage));
//            bottomright_releat.setText("-"+rssi);
            handleVoltageShow(bottomright_voltage,voltage);
            handleException(bleData, mActivity.manageDevice.getRightBDevice());
//            if(mActivity.manageDevice.disRightBCount==1)
//            {
//                mHandler.removeCallbacks(rightBRunnable);
//                mActivity.manageDevice.disRightBCount = 0;
//            }
//            mActivity.manageDevice.disRightBCount++;
//            mHandler.postDelayed(rightBRunnable, DISTIME);// 打开定时器，执行操作

        }else if(recordData.getName().equals(mActivity.manageDevice.getLeftFDevice()))
        {
            topleft_preesure.setText(df.format(press));
            topleft_temp.setText(String.valueOf(temp));
//            topleft_voltage.setText(df1.format(voltage));
//            topleft_releat.setText("-"+rssi);
            handleException(bleData, mActivity.manageDevice.getLeftFDevice());
            handleVoltageShow(topleft_voltage,voltage);
//            if(mActivity.manageDevice.disLeftFCount==1)
//            {
//                mHandler.removeCallbacks(leftFRunnable);
//                mActivity.manageDevice.disLeftFCount = 0;
//            }
//            mActivity.manageDevice.disLeftFCount++;
//            mHandler.postDelayed(leftFRunnable, DISTIME);// 打开定时器，执行操作

        }else if(recordData.getName().equals(mActivity.manageDevice.getRightFDevice()))
        {
            topright_preesure.setText(df.format(press));
            topright_temp.setText(String.valueOf(temp));
//            topright_voltage.setText(df1.format(voltage));
//            topright_releat.setText("-"+rssi);
            handleException(bleData, mActivity.manageDevice.getRightFDevice());
            handleVoltageShow(topright_voltage,voltage);
//            if(mActivity.manageDevice.disRightFCount==1)
//            {
//                mHandler.removeCallbacks(rightFRunnable);
//                mActivity.manageDevice.disRightFCount = 0;
//            }
//            mActivity.manageDevice.disRightFCount++;
//            mHandler.postDelayed(rightFRunnable, DISTIME);// 打开定时器，执行操作
        }
    }
    private void handleException(BleData date, String str) {
        StringBuffer buffer = new StringBuffer();
        int state = 0;
        buffer.append(date.getPress() > 3.2 ? "高压" + " " : "");
        buffer.append(date.getPress() < 1.8 ? "低压" + " " : "");
        buffer.append(date.getTemp() > 65 ? "高温" + " " : "");
//        buffer.append(date.getTemp() < 20 ? "低温" + " " : "");
        ManageDevice.status[] statusData = ManageDevice.status.values();
        //状态检测
        if(date.getStatus()==8||date.getStatus()==0)
        {

        }else{
            buffer.append(statusData[date.getStatus()] + " ");
        }
        if (buffer.toString().contains("快漏气")||date.getPress() > 3.2 || date.getPress() < 1.8 || date.getTemp() >= 65 ? true: false)
        {
            //高压
            bleIsException(str,buffer.toString());
        }else
        {
            //正常
            bleIsFind(str,buffer.toString());
        }
    }

    /**
     * 发现蓝牙模块发出的广播 UI变化初始化
     * @param strAddress
     * @param noticeStr
     */
    private void bleIsFind(String strAddress,String noticeStr)
    {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false))
        {
            dicoverBlueDeviceForDay(strAddress,noticeStr);
        }else {
            dicoverBlueDeviceForNight(strAddress,noticeStr);
        }
    }

    /**
     * 汽车轮胎异常情况报警
     * @param strAddress
     * @param noticeStr
     */
    private void bleIsException(String strAddress,String noticeStr)
    {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false))
        {
            bleIsExceptionForDay(strAddress,noticeStr);
        }else
        {
            bleIsExceptionForNight(strAddress,noticeStr);
        }

    }

    /**
     * 电池变化情况指示
     * @param img
     * @param voltage
     */
    private void handleVoltageShow(ImageView img,float voltage)
    {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false))
        {
            if(voltage>=Constants.vol)
            {
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_100));
            }else if(voltage>Constants.vol*0.8&&voltage<Constants.vol){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_80));
            }else if(voltage>Constants.vol*0.5&&voltage<Constants.vol*0.8){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_50));
            }else if(voltage>Constants.vol*0.2&&voltage<Constants.vol*0.5){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_20));
            }else if(voltage>0&&voltage<Constants.vol*0.2){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.am_battle_0));
            }

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
    }

    /**
     * 白天模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    private void getDataTimeOutForDay(RecordData recordData, int state)
    {
        switch (state)
        {
            case 1001:
                imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_left1));
                topleft_voltage.setImageDrawable(null);
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);

                break;
            case 1002:
                imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_right1));
                topright_voltage.setImageDrawable(null);
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);

                break;
            case 1003:
                imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_left2));
                bottomleft_voltage.setImageDrawable(null);
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");
                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);

                break;
            case 1004:
                imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.link_off_right2));
                bottomright_voltage.setImageDrawable(null);
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");

                recordData.setName(mActivity.manageDevice.getRightBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);

                break;
        }
    }

    /**
     * 夜间模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    private void getDataTimeOutForNight(RecordData recordData, int state)
    {
        switch (state)
        {
            case 1001:
                topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_left1));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);

                break;
            case 1002:
                topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_right1));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");

                recordData.setName(mActivity.manageDevice.getRightFDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);

                break;
            case 1003:
                imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_left2));
                bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);

                break;
            case 1004:
                imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pmlink_off_right2));
                bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightBDevice());
                DbHelper.getInstance(getActivity()).update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);
                break;
        }
    }

    /**
     * 白天模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     */
    private void dicoverBlueDeviceForDay(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
//                ll_bottomright.setVisibility(View.VISIBLE);
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_left2));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.phone));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_right2));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.phone));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
//                ll_topleft.setVisibility(View.VISIBLE);
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_left1));
            topleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            topleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.phone));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
//                ll_topright.setVisibility(View.VISIBLE);
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.am_normal_right1));
            topright_preesure.setTextColor(getResources().getColor(R.color.phone));
            topright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.phone));
        }
    }

    /**
     * 夜间模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     */
    private void dicoverBlueDeviceForNight(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
//                ll_bottomright.setVisibility(View.VISIBLE);
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_left2));
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.blue_night));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_right2));
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.blue_night));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
//                ll_topleft.setVisibility(View.VISIBLE);
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_left1));
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.blue_night));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
//                ll_topright.setVisibility(View.VISIBLE);
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_normal_right1));
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.blue_night));
        }
    }

    /**
     * 白天模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    private void bleIsExceptionForDay(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_left2));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));
//                if(noticeStr.contains("快漏气"))
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify)
            {
                mActivity.manageDevice.leftB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftB);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
//                try {
//                    if(!isExit(soundList,SoundManager.leftB))
//                    {
//                        Logger.e("异常播报语音");
//                        soundList.add(SoundManager.leftB);
//                        App.getInstance().playSound(SoundManager.leftB);
////                        App.getInstance().playMutilSounds(soundList);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_right2));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.rightB_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.rightB_notify = true;
//                    App.getInstance().playSound(SoundManager.rightB,noticeStr);
//                }
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify)
            {
                mActivity.manageDevice.rightB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightB);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }

        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_left1));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.leftF_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.leftF_notify = true;
//                    App.getInstance().playSound(SoundManager.leftF,noticeStr);
//                }

            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify)
            {
                mActivity.manageDevice.leftF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftF);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.am_error_right1));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));
//                if(!mActivity.manageDevice.rightF_notify||noticeStr.contains("快漏气")) {
//                    mActivity.manageDevice.rightF_notify = true;
//                    App.getInstance().playSound(SoundManager.rightF,noticeStr);
//                }
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify)
            {
                mActivity.manageDevice.rightF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightF);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
        }
    }

    /**
     * 夜间模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    private void bleIsExceptionForNight(String strAddress,String noticeStr)
    {
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice()))
        {
            imgBottomleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_left2));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.leftB,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify)
            {
                mActivity.manageDevice.leftB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftB);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }

        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice()))
        {
            imgBottomright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_right2));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.rightB,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify)
            {
                mActivity.manageDevice.rightB_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightB);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }

        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice()))
        {
            imgTopleft.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_left1));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.leftF,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify)
            {
                mActivity.manageDevice.leftF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.leftF);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }

        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice()))
        {
            imgTopright.setImageDrawable(getResources().getDrawable(R.mipmap.pm_error_right1));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));

//                App.getInstance().playSound(SoundManager.rightF,noticeStr);
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify)
            {
                mActivity.manageDevice.rightF_notify = true;
//                    soundList.remove(SoundManager.leftB);
                App.getInstance().playSound(SoundManager.rightF);
//                    App.getInstance().playSound(SoundManager.leftB,noticeStr);
            }
        }
    }
}
