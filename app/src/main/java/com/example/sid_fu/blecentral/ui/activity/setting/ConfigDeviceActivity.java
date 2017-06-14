package com.example.sid_fu.blecentral.ui.activity.setting;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.HomeActivity;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.model.ManageDevice;
import com.example.sid_fu.blecentral.model.ParsedAd;
import com.example.sid_fu.blecentral.model.SampleGattAttributes;
import com.example.sid_fu.blecentral.service.BaseBluetoothLeService;
import com.example.sid_fu.blecentral.ui.activity.base.BaseActionBarActivity;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DataUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.widget.LoadingDialog;
import com.example.sid_fu.blecentral.widget.NotifyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class ConfigDeviceActivity extends BaseActionBarActivity implements View.OnClickListener{
    private static final String TAG = "ConfigDevice";
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    private static final int NOTIFY_REQUEST_CODE = 2;
    public static final String PAIRED_OK = "paired_ok";
    public static final String NONE_NEXT = "nono_next";
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
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private int ScanTimeOut = 10000;
    public static int leftF = 1;
    public static int rightF = 2;
    public static int leftB = 3;
    public static int rightB =4;
    public static int none =5;
    private AlphaAnimation animation;
    private ProgressBar pb_left_from;
    private ProgressBar pb_right_from;
    private ProgressBar pb_left_back;
    private ProgressBar pb_right_back;
    private final int maxLenght = -65;
    private TextView tv_note_left_from;
    private TextView tv_note_right_from;
    private TextView tv_note_left_back;
    private TextView tv_note_right_back;
//    private ScanDeviceRunnable leftFRunable;
//    private ScanDeviceRunnable rightFRunable;
//    private ScanDeviceRunnable leftBRunable;
//    private ScanDeviceRunnable rightBRunable;
    private Device deviceDate;
    private LoadingDialog loadDialog;
    private boolean isConneting = false;
    private boolean isRecevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_config);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initConfig();
        //停止service下蓝牙扫描
        stopScan();
        iniBle();
        initUI();
    }
    public void startScan() {
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        mBluetoothAdapter.startLeScan(mLeScanCallback);
        Intent intent = new Intent(SampleGattAttributes.ACTION_START_SCAN);
        sendBroadcast(intent);
    }
    public void stopScan() {
        //App.getInstance().speak("正在关闭蓝牙设备");
//        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Intent intent = new Intent(SampleGattAttributes.ACTION_STOP_SCAN);
        sendBroadcast(intent);
    }
    private void initConfig() {
        deviceDate = (Device) getIntent().getExtras().getSerializable("device");
        User user = new UserDao(this).get(1);
//        deviceDate = new Device();
//        deviceDate.setDeviceName("宝马3系列");
//        deviceDate.setDeviceDescripe("宝马3系列是一款.....");
        deviceDate.setUser(user);
        Intent gattServiceIntent = new Intent(this, BaseBluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }
    private void initUI() {
        loadDialog = new LoadingDialog(this);
        loadDialog.setBackgroundColor();
        imgTopleft = (ImageView) findViewById(R.id.img_topleft);
        imgTopright = (ImageView) findViewById(R.id.img_topright);
        imgBottomleft = (ImageView) findViewById(R.id.img_bottomleft);
        imgBottomright = (ImageView) findViewById(R.id.img_bottomright);

        pb_left_from = (ProgressBar) findViewById(R.id.pb_left_from);
        pb_left_from.setVisibility(View.GONE);
        pb_right_from = (ProgressBar) findViewById(R.id.pb_right_from);
        pb_left_back = (ProgressBar) findViewById(R.id.pb_left_back);
        pb_right_back = (ProgressBar) findViewById(R.id.pb_right_back);

        tv_note_left_from = (TextView) findViewById(R.id.tv_note_left_from);
        tv_note_right_from = (TextView) findViewById(R.id.tv_note_right_from);
        tv_note_left_back = (TextView) findViewById(R.id.tv_note_left_back);
        tv_note_right_back = (TextView) findViewById(R.id.tv_note_right_back);

        topleft_ok = (Button) findViewById(R.id.ll_topleft).findViewById(R.id.btn_ok);
        topleft_next = (Button) findViewById(R.id.ll_topleft).findViewById(R.id.btn_next);

        topright_ok = (Button) findViewById(R.id.ll_topright).findViewById(R.id.btn_ok);
        topright_next = (Button) findViewById(R.id.ll_topright).findViewById(R.id.btn_next);

        bottomleft_ok = (Button) findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_ok);
        bottomleft_next = (Button) findViewById(R.id.ll_bottomleft).findViewById(R.id.btn_next);

        bottomright_ok = (Button) findViewById(R.id.ll_bottomright).findViewById(R.id.btn_ok);
        bottomright_next = (Button) findViewById(R.id.ll_bottomright).findViewById(R.id.btn_next);

//        leftFRunable = new ScanDeviceRunnable(handler,leftF);
//        rightFRunable = new ScanDeviceRunnable(handler,rightF);
//        leftBRunable = new ScanDeviceRunnable(handler,leftB);
//        rightBRunable = new ScanDeviceRunnable(handler,rightB);

        //默认开启左前边扫描
//        handler.postDelayed(leftFRunable,ScanTimeOut);
        showDialog(getResources().getString(R.string.step1),false);

        topleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(topleft_ok,topleft_next,pb_left_from,pb_right_from,rightF);
            }
        });
        topright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(topright_ok,topright_next,pb_right_from,pb_right_back,rightB);
            }
        });
        bottomleft_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDeviceToNextActivity(bottomleft_ok,null,pb_left_back,null,none);

            }
        });
        bottomright_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevice(bottomright_ok,bottomright_next,pb_right_back,pb_left_back,leftB);
            }
        });
        topleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(topleft_ok,topleft_next,pb_left_from);
                pairedDevice(topleft_ok,topleft_next,pb_left_from,pb_right_from,rightF);
            }
        });
        topright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(topright_ok,topright_next,pb_right_from);
                pairedDevice(topright_ok,topright_next,pb_right_from,pb_right_back,rightB);
            }
        });
        bottomleft_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomleft_next.getText().equals("跳过"))
                {
                    App.getDeviceDao().add(deviceDate);
                    SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
                    boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
                    if(!firstTimeUse) {
                        Intent intent = new Intent();
                        intent.setClass(ConfigDeviceActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    return;
                }
                nextBtn(bottomleft_ok,bottomleft_next,pb_left_from);
                pairedDeviceToNextActivity(bottomleft_ok,null,pb_left_back,null,none);
            }
        });
        bottomright_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn(bottomright_ok,bottomright_next,pb_right_back);
                pairedDevice(bottomright_ok,bottomright_next,pb_right_back,pb_left_back,leftB);
            }
        });
    }
    private void iniBle() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        new Thread(new Runnable() {
            public void run() {
                if (mBluetoothAdapter.isEnabled()) {
                    scanLeDevice(true);
                    mScanning = true;
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        }).start();
    }
    private void nextBtn(Button enterBtn,Button currentBtn,ProgressBar currentPb) {
        enterBtn.setText("确定");
        currentBtn.setVisibility(View.GONE);
        currentPb.setVisibility(View.GONE);
        enterBtn.setVisibility(View.GONE);
        //保存数据到本地
//        currentTv.setText("左前轮：\n"+device.getAddress());
//        state = none;
//        handler.removeCallbacks(currentRunnable);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    private void pairedDeviceToNextActivity(Button currentBtn,Button nextBtn,ProgressBar currentPb,ProgressBar nextPb,int nextState) {
        if(currentBtn.getText().equals("确定")) {
            Intent intent = new Intent();
            intent.setClass(ConfigDeviceActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
            App.getDeviceDao().add(deviceDate);
            SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
        }else {
            //setFlickerAnimation(imgTopright);
            currentBtn.setText("确定");
            currentBtn.setVisibility(View.GONE);
//            currentPb.setVisibility(View.VISIBLE);
            showDialog("正在识别信号强度。。。",false);
//            handler.postDelayed(currentRunnable,ScanTimeOut);
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    private void pairedDevice(Button currentBtn,Button nextBtn,ProgressBar currentPb,ProgressBar nextPb,int nextState) {
        if(currentBtn.getText().equals("确定")) {
            currentBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
//            nextPb.setVisibility(View.VISIBLE);
            state = nextState;
            showDialog("正在识别信号强度。。。",false);
//            handler.postDelayed(nextRunnable,ScanTimeOut);
        }else {
            //setFlickerAnimation(imgTopright);
            currentBtn.setText("确定");
            isFirst = false;
            currentBtn.setVisibility(View.GONE);
//            currentPb.setVisibility(View.VISIBLE);
            showDialog("正在识别信号强度。。。",false);
//            handler.postDelayed(currentRunnable,ScanTimeOut);
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    private void handlerReScan(Button currentBtn,ProgressBar currentPb) {
        if(currentBtn.getText().equals("确定")) {
//            currentBtn.setText("重试");
////            currentPb.setVisibility(View.GONE);
//            currentBtn.setVisibility(View.VISIBLE);
//            if(mBluetoothLeService!=null)
//                mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(state==none) return;
            Logger.e("重试");
            timeout = true;
            isFirst = false;
            loadDialog.dismiss();
            Intent it =new Intent(ConfigDeviceActivity.this,NotifyDialog.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.putExtra(PAIRED_OK,"重试");
            startActivity(it);
            if(mBluetoothAdapter!=null)
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            switch (msg.what) {
                case 1:
                    handlerReScan(topleft_ok,pb_left_from);
                    break;
                case 2:
                    handlerReScan(topright_ok,pb_right_from);
                    break;
                case 4:
                    handlerReScan(bottomleft_ok,pb_left_back);
                    break;
                case 3:
                    handlerReScan(bottomright_ok,pb_right_back);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //开启service下蓝牙扫描
        startScan();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter = null;
    }

    private int state = 1;
    private boolean isFirst = false;
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (BluetoothDevice ble : mDeviceList) {
                        Logger.e("列表中存在的设备："+ble.getAddress());
                    }
                    Logger.e("扫描到的设备："+device.getAddress()+state);
                    if(!isFirst&&!ManageDevice.isConfigEquals(mDeviceList,device)&&rssi>maxLenght) {
                        isFirst = true;
                        bleIsFind(device,rssi,scanRecord,state);
                    }

                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
                    /*if (mScanning == true) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                    */
                }
            });
        }
    };

    private void scanForResult(BluetoothDevice device) {
//        handler.removeCallbacks(runnable);
        isConneting = true;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Logger.e("开始连接："+device.getAddress());
        showDialog(device.getAddress()+"正在连接。。。",false);
        //开始连接蓝牙
        if(mBluetoothLeService!=null)
            mBluetoothLeService.connect(device.getAddress());
    }
    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data,int state) {
        Logger.e("发现新设备"+device.getAddress()+":"+DataUtils.bytesToHexString(data));
        ParsedAd ad = DataUtils.parseData(data);
        if(ad == null||ad.datas == null) return;
        if(ad.datas.length == 0) return;
        Logger.e("信号强度"+rssi+ad.datas.toString());
        if(ad.datas.length>=4) {
            int states = DigitalTrans.byteToBin0x0F(ad.datas[0]);
            ManageDevice.status[] statusData = ManageDevice.status.values();
            Logger.e(statusData[states]+"");
            //状态检测
            String buff = statusData[states]+"";
            if(buff.contains("快漏气")||buff.contains("加气")) {
                onSuccess(device);
            } else {
                isFirst = false;
            }
        }else if(ad.datas.length>=9) {
            int states = DigitalTrans.byteToBin0x0F(ad.datas[0]);
            ManageDevice.status[] statusData = ManageDevice.status.values();
            Logger.e(statusData[states]+"");
            //状态检测
            String buff = statusData[states]+"";
            if(buff.contains("快漏气")||buff.contains("加气")) {
                onSuccess(device);
            } else {
                isFirst = false;
            }
        }else{
            scanForResult(device);//首次配对，需要连接
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT ) {
            if(resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }else {
                scanLeDevice(true);
                mScanning = true;
            }
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(SCAN_PERIOD);
                        if (mScanning) {
                            Logger.e(TAG,"断开扫描");
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            //invalidateOptionsMenu();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });//.start();
            //WinToast.makeText(mContext,"start scan");
            App.getInstance().speak("正在打开蓝牙设备");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            App.getInstance().speak("关闭蓝牙设备");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        //invalidateOptionsMenu();
    }

    @Override
    public void onClick(View v) {

    }

    private BaseBluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BaseBluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "mBluetoothLeService is okay");
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private boolean isWrite = false;
    private boolean timeout;
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
            if (SampleGattAttributes.ACTION_GATT_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                Logger.e("connected:"+device.getAddress());
            } else if (SampleGattAttributes.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                Logger.e("Disconneted GATT Services"+device.getAddress());
                if(!isWrite)
                    mBluetoothLeService.connect(device.getAddress());
            } else if (SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //onSuccess(device);
//                mBluetoothLeService.writeChar6("AT+USED=1");
//                mBluetoothLeService.writeChar6("AT+USED=1");
                if(timeout) {
                    timeout = false;
                    isWrite = true;
                    mBluetoothLeService.disconnect();
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothLeService.writeChar6("AT+USED=1");
                    }
                },2000);
                if(!isWrite)
                    showDialog("正在配置传感器。。。",false);
                Logger.e("Discover GATT Services"+device.getAddress());
            } else if (SampleGattAttributes.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(SampleGattAttributes.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功 返回OK
                        if (data != null&&data.length==2&&!isWrite) {
                            Logger.e(data.toString());
                            onSuccess(device);
                            isWrite = true;
                        }
                    }
                });
            }else if (NotifyDialog.ACTION_BTN_STATE.equals(action)) {
                intent.getExtras().getInt(NotifyDialog.BTN_STATE);
                Logger.e("重试");
                isFirst = false;
                isWrite = false;
                timeout = false;
                showDialog(getResources().getString(R.string.step1),false);
                if(mBluetoothAdapter!=null)
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }else if (NotifyDialog.ACTION_BTN_NEXT.equals(action)) {
                intent.getExtras().getInt(NotifyDialog.BTN_STATE);
                state++;
                Logger.e("下一步"+state);
                isFirst = false;
                isWrite = false;
                timeout = false;
                if(state==5) {
                    boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
                    if(!firstTimeUse) {
                        Intent intentH = new Intent();
                        intentH.setClass(ConfigDeviceActivity.this,HomeActivity.class);
                        startActivity(intentH);
                    }
                    App.getDeviceDao().add(deviceDate);
                    SharedPreferences.getInstance().putBoolean(Constants.FIRST_CONFIG,true);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(loadDialog.isShowing())
                                loadDialog.dismiss();
                            finish();
                        }
                    },200);
                   return;
                }else {
                    showDialog(getResources().getString(R.string.step1),false);
                }
                if(mBluetoothAdapter!=null){
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                }
            }
        }
    };


    private void onSuccess(BluetoothDevice device) {
        switch (state) {
            case 1:
                setUsedToTrue(device,tv_note_left_from);
                //保存数据库
                deviceDate.setLeft_FD(device.getAddress());
                break;
            case 2:
                setUsedToTrue(device,tv_note_right_from);
                //保存数据库
                deviceDate.setRight_FD(device.getAddress());
                break;
            case 4:
                setUsedToTrue(device,tv_note_left_back);
                //保存数据库
                deviceDate.setLeft_BD(device.getAddress());
                break;
            case 3:
                setUsedToTrue(device,tv_note_right_back);
                //保存数据库
                deviceDate.setRight_BD(device.getAddress());
                break;
        }
    }
    private void setUsedToTrue(BluetoothDevice device,TextView currentTv) {
        //如果出现超时，任何数据都放弃
        if(timeout&&mBluetoothLeService!=null) {
            timeout = false;
            isWrite = true;
            mBluetoothLeService.disconnect();
            return;
        }
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Intent it =new Intent(ConfigDeviceActivity.this,NotifyDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PAIRED_OK,"完成");
        startActivity(it);
        //保存数据到本地
        currentTv.setText(device.getAddress());
        isFirst = false;
        mDeviceList.add(device);
        if(mBluetoothLeService!=null)
            mBluetoothLeService.disconnect();
    }

    private void showDialog(String str,boolean isConnect) {
        if(!this.isFinishing()&&!loadDialog.isShowing()) {
            loadDialog.setText(getResources().getStringArray(R.array.staticText)[state-1]+str);
            loadDialog.show();
            loadDialog.setCountNum(20);
            loadDialog.startCount(new LoadingDialog.OnListenerCallBack() {
                @Override
                public void onListenerCount() {
                    handler.sendEmptyMessage(state);
                }
            });
//            App.getInstance().speak   (getResources().getStringArray(R.array.staticText)[state-1]+str);
        }else{
            loadDialog.reStartCount(getResources().getStringArray(R.array.staticText)[state-1]+str,20);
            App.getInstance().speak(str);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_CONNECTED);
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(SampleGattAttributes.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(SampleGattAttributes.ACTION_NAME_RSSI);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_STATE);
        intentFilter.addAction(NotifyDialog.ACTION_BTN_NEXT);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        intentFilter.addAction(SampleGattAttributes.ACTION_GATT_SERVICES_DISCOVERED);
        return intentFilter;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
