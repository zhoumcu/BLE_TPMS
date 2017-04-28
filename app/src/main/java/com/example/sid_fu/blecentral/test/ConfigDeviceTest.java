package com.example.sid_fu.blecentral.test;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.NotifyDialog;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.utils.DateUtils;
import com.example.sid_fu.blecentral.utils.DigitalTrans;
import com.example.sid_fu.blecentral.utils.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class ConfigDeviceTest extends ActionBarActivity implements View.OnClickListener {
    private static final String TAG = "ConfigDevice";
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;
    public static final String PAIRED_OK = "paired_ok";

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private int ScanTimeOut = 10000;
    private final int maxLenght = -65;
//    private Device deviceDate;
//    private LoadingDialog loadDialog;
    private boolean isConneting = false;
    private TextView topleft_preesure;
    private TextView topleft_temp;
    private TextView topleft_note;
    private TextView topleft_voltage;
    private TextView topleft_releat;
    private TextView tv_note;
    private DecimalFormat df1;
    private DecimalFormat df;
    private int timeCount = 3;
    private Timer timer;
    private TextView tv_complete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_config_test);
         /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initConfig();
        iniBle();
        initUI();
    }

    private void initConfig() {
//        deviceDate = (Device) getIntent().getExtras().getSerializable("device");
//        User user = new UserDao(this).get(1);
//        deviceDate = new Device();
//        deviceDate.setDeviceName("宝马3系列");
//        deviceDate.setDeviceDescripe("宝马3系列是一款.....");
//        deviceDate.setUser(user);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void initUI() {
//        loadDialog = new LoadingDialog(this);
        tv_note = (TextView) findViewById(R.id.tv_notifyMsg);
        topleft_preesure = (TextView) findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) findViewById(R.id.tv_temp);
        topleft_note = (TextView) findViewById(R.id.tv_note);
        topleft_voltage = (TextView) findViewById(R.id.tv_voltage);
        topleft_releat = (TextView) findViewById(R.id.tv_releat);
        tv_complete = (TextView)findViewById(R.id.tv_complete);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toWeChatScan();
            }
        });
        df1=new DecimalFormat("#.##");
        df=new DecimalFormat("#.#");
        //默认开启左前边扫描
//        handler.postDelayed(leftFRunable,ScanTimeOut);
        showDialog(getResources().getString(R.string.step1), false);

    }
    private void toWeChatScan() {
        try {
            //利用Intent打开微信
            Uri uri = Uri.parse("weixin://dl/scan");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivityForResult(intent,100);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(ConfigDeviceTest.this, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
        }
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
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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

    private int countComp = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
            {
                tv_note.append(msg.obj+"\n");
            }else if(msg.what==2)
            {
                tv_note.setText(null);
                countComp++;
                tv_complete.setText("已完成 "+countComp+" 组测试！");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //animation.cancel();
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
                        Logger.e("列表中存在的设备：" + ble.getAddress());
                    }
                    Logger.e("扫描到的设备：" + device.getAddress() + state);
                    if (!isFirst && !ManageDevice.isConfigEquals(mDeviceList, device) && rssi > maxLenght) {
                        isFirst = true;
                        bleIsFind(device, rssi, scanRecord, state);
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
        Logger.e("开始连接：" + device.getAddress());
        showDialog(device.getAddress() + "正在连接。。。", false);
        //开始连接蓝牙
        if (mBluetoothLeService != null)
            mBluetoothLeService.connect(device.getAddress());

    }

    private void bleIsFind(BluetoothDevice device, int rssi, byte[] data, int state) {
        Logger.e("发现新设备" + device.getAddress());
        Logger.e("信号强度" + rssi + data.toString());
        topleft_releat.setText(rssi+"");
        scanForResult(device);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            } else {
                scanLeDevice(true);
                mScanning = true;
            }
        }else if(requestCode==100)
        {
            Toast.makeText(ConfigDeviceTest.this, "关注成功！", Toast.LENGTH_SHORT).show();
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
                            Logger.e(TAG, "断开扫描");
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

    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
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
                Logger.e("connected:" + device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                showDialog("已完成", false);
                App.getInstance().speak("已测试完毕，开始测试下一个");
//                loadDialog.dismiss();
                //mBluetoothAdapter.stopLeScan(mLeScanCallback);
                Logger.e("Disconneted GATT Services" + device.getAddress());
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //获取数据
                mBluetoothLeService.writeChar6("AT+GET");
                //mBluetoothLeService.writeChar6("AT+GET");
                showDialog("正在配置传感器。。。", true);
                timeCount = 3;
                Logger.e("Discover GATT Services" + device.getAddress());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                        BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                        //通讯成功 返回OK
                        if (data != null) {
                            Logger.e(data.toString());
//                            onSuccess(device);
                            bleStringToDouble(device,true,data);
                        }
                    }
                });
            }
        }
    };

    private void setUsedToTrue(BluetoothDevice device, Button currentBtn, ProgressBar currentPb, TextView currentTv) {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        Intent it = new Intent(ConfigDeviceTest.this, NotifyDialog.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(PAIRED_OK, "完成");
        startActivity(it);
        Intent broackIntent = new Intent(NotifyDialog.ACTION_CHANGE_STATE);
        broackIntent.putExtra(NotifyDialog.BTN_STATE, "完成");
        sendBroadcast(broackIntent);

        currentBtn.setText("确定");
        currentBtn.setVisibility(View.VISIBLE);
        currentPb.setVisibility(View.GONE);
        //保存数据到本地
        currentTv.setText("左前轮：\n" + device.getAddress());
        showDialog(getResources().getString(R.string.step4), true);
        isFirst = false;
        mDeviceList.add(device);
        mBluetoothLeService.disconnect();
    }

    private void showDialog(String str, boolean isConnect) {
//        if (!loadDialog.isShowing()) {
//            loadDialog.setText(getResources().getStringArray(R.array.staticText)[state - 1] + str);
//            loadDialog.show();
//            loadDialog.setCountNum(30);
//            loadDialog.startCount(new LoadingDialog.OnListenerCallBack() {
//                @Override
//                public void onListenerCount() {
//                    handler.sendEmptyMessage(state);
//                }
//            });
//            App.getInstance().speak(getResources().getStringArray(R.array.staticText)[state - 1] + str);
//        } else {
//            loadDialog.reStartCount(getResources().getStringArray(R.array.staticText)[state - 1] + str, 30);
            App.getInstance().speak(str);
//        }
        Message msg = new Message();
        msg.what=1;
        msg.obj = str;
        handler.sendMessage(msg);
//        tvNote.append(str+"\n");
//        if (mBluetoothAdapter != null)
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_NAME_RSSI);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
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
    private void bleStringToDouble(BluetoothDevice device, boolean isNotify,byte[] data)
    {
        String voltage = null;
        String press = null;
        String temp = null;
        String state = null;
        int rssi = 0;
        BleData bleData = new BleData();
        String[] stringData = DigitalTrans.bytetoString(data).split(" ");
        Logger.e("接收数据："+DigitalTrans.bytetoString(data));
        if(stringData==null) return;
        if(isNotify&&stringData.length==4)
        {
            press = DigitalTrans.bytetoString(data).split(" ")[2].substring(0,DigitalTrans.bytetoString(data).split(" ")[2].length()-1);
            voltage = DigitalTrans.bytetoString(data).split(" ")[0].substring(0,DigitalTrans.bytetoString(data).split(" ")[0].length()-1);
            temp = DigitalTrans.bytetoString(data).split(" ")[1].substring(0,DigitalTrans.bytetoString(data).split(" ")[1].length()-1);
            state = DigitalTrans.bytetoString(data).split(" ")[3];
        }else
        {
            showDialog("写入成功",false);
            return;
        }
        Logger.e("状态："+state+"\n");
        Logger.e("压力值："+press+"\n");
        Logger.e("温度："+temp+"\n");
        Logger.e("电压"+voltage+"");

        //bleData.setTemp (temp);
        //bleData.setPress(press);
        //bleData.setStatus(state);

        topleft_preesure.setText(press);
        topleft_temp.setText(temp);
        topleft_voltage.setText(voltage);
        topleft_releat.setText("-"+rssi);

        if(Float.valueOf(press)<0.1)
        {
            isFirst = false;
            mBluetoothLeService.disconnect();
            timer.cancel();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);

                    handler.sendEmptyMessage(2);
                    showDialog(getResources().getString(R.string.step1)+"下一个", false);
                }
            },1500);
        }
        if(Float.valueOf(press)>0.3)
        {
            Logger.e("发送更新生产日期命令");
            if(timeCount>0)
            {
                showDialog("正在写入出厂日期",false);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(timeCount-->0)
                        {
                            Logger.e("AT+PDATE="+DateUtils.getNowDate());
                            mBluetoothLeService.writeChar6("AT+PDATE="+DateUtils.getNowDate());
                        }
                    }
                },0,200);
            }
        }
        //handleException(bleData, "左前轮\n"+ device+"\n", topleft_note, null);
        //测试完成断开蓝牙
        //isFirst = false;
        //mBluetoothLeService.disconnect();
        //mBluetoothAdapter.startLeScan(mLeScanCallback);

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
}
