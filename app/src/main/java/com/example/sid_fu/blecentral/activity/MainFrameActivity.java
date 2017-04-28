package com.example.sid_fu.blecentral.activity;

import android.annotation.TargetApi;
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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.ManageDevice;
import com.example.sid_fu.blecentral.MusicService;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.ShakeListener;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.fragment.BundDeviceFragment;
import com.example.sid_fu.blecentral.fragment.ChangeDeviceFragment;
import com.example.sid_fu.blecentral.fragment.InsteadDeviceFragment;
import com.example.sid_fu.blecentral.fragment.MainPagerFragment;
import com.example.sid_fu.blecentral.ui.activity.BaseActionBarActivity;
import com.example.sid_fu.blecentral.utils.BitmapUtils;
import com.example.sid_fu.blecentral.utils.CommonUtils;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.PopupMeumWindow;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainFrameActivity extends BaseActionBarActivity implements View.OnClickListener ,SensorEventListener {
    private final static String TAG = MainFrameActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000000;

    public BluetoothLeService mBluetoothLeService = null;
    public  BluetoothAdapter mBluetoothAdapter = null;
    private boolean mScanning;

    public static MainFrameActivity mContext;


    private Handler mHandler;


    private List<MyBluetoothDevice> mDiconnectDeviceList = new ArrayList<>();



    public static ShakeListener mShakeListener;
    private int timeOfyundongCount = 1800;

    private FragmentManager fragmentManager;
    private int currIndex = 0;
    private ArrayList<String> fragmentTags;

    public  MyBluetoothDevice leftBDevice =null;
    public  MyBluetoothDevice leftFDevice =null ;
    public  MyBluetoothDevice rightFDevice =null;
    public  MyBluetoothDevice rightBDevice =null;

    public  List<MyBluetoothDevice> mDeviceList = new ArrayList<>();
    public  ManageDevice manageDevice;
    private boolean isDisconnect;
    private int count;
    public Device deviceDetails;
    public int deviceId;
    private int scanCount = 4;
    private PopupMeumWindow menuWindow;
    public FrameLayout background;
    public boolean isQuiting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化Colorful
        changeThemeWithColorful();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            setContentView(R.layout.de_activity_main);
        }catch (InflateException e)
        {

        }
        mContext =MainFrameActivity.this;
        deviceId = getIntent().getExtras().getInt("DB_ID");
        /*显示App icon左侧的back键*/
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        initUI();
        intiShake();
        initData();
        initConfig();
        refreashData();
        iniBle();
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);
        showFragment();
    }
    /**
     * 切换主题
     */
    private void changeThemeWithColorful() {
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false)) {
            setTheme(R.style.DayTheme);
        } else {
            setTheme(R.style.NightTheme);
        }
    }

    private void initUI() {

        background = (FrameLayout)findViewById(R.id.bg_ground);
        try{
            findViewById(R.id.back).setOnClickListener(this);
            findViewById(R.id.img_set).setOnClickListener(this);
        }catch (NullPointerException e)
        {

        }
//        if(CommonUtils.isScreenChange(mContext))
//        {
//            findViewById(R.id.bg_ground).setBackgroundDrawable(new BitmapDrawable(BitmapUtils.readBitMap(mContext,R.mipmap.am_bg_g)));
//        }else {
//            findViewById(R.id.bg_ground).setBackgroundDrawable(new BitmapDrawable(BitmapUtils.readBitMap(mContext,R.mipmap.g_bg)));
//        }

    }

    private void initConfig()
    {
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==1)
                {
                    if(mShakeListener!=null&&mBluetoothLeService!=null)
                    {
                        mBluetoothLeService.showDialog();
                    }
                }
            }
        };
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        Logger.d("Try to bindService=" + bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE));
//        Intent musicServiceIntent = new Intent(this, BluetoothLeService.class);
//        startService(musicServiceIntent);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        manageDevice =  new ManageDevice();
    }
    @Override
    public void onResume() {
        super.onResume();
//        deviceDetails = App.getDeviceDao().get(deviceId);
//        initBlueDevice();
//        refreashData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // land do nothing is ok
            changeThemeWithColorful();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            try {
                setContentView(R.layout.de_activity_main);
            }catch (InflateException e)
            {

            }
            mContext =MainFrameActivity.this;
            deviceId = getIntent().getExtras().getInt("DB_ID");
        /*显示App icon左侧的back键*/
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
            initUI();
            intiShake();
            initData();
            initConfig();
//        refreashData();
            iniBle();
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);
            showFragmentBeforeRemove();
            Logger.e("land");
        }else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // port do nothing is ok
            changeThemeWithColorful();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            try {
                setContentView(R.layout.de_activity_main);
            }catch (InflateException e)
            {

            }
            mContext =MainFrameActivity.this;
            deviceId = getIntent().getExtras().getInt("DB_ID");
        /*显示App icon左侧的back键*/
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
            initUI();
            intiShake();
            initData();
            initConfig();
//        refreashData();
            iniBle();
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);
            showFragmentBeforeRemove();
            Logger.e("port");
        }
        Logger.e("onConfigurationChanged");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        Logger.e("onRestart");
    }

    private void initData() {
        currIndex = 0;
        fragmentTags = new ArrayList<>(Arrays.asList("HomeFragment", "ImFragment", "InterestFragment", "MemberFragment"));
    }
    private void showFragment() {
        if(fragmentManager==null)
            fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if(fragment == null) {
            Logger.e("fragment is null");
            fragment = instantFragment(currIndex);
        }
        for (int i = 1; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if(f != null && f.isAdded()) {
                fragmentTransaction.remove(f);
                Logger.e("fragment is remove"+fragmentTags.get(i));
            }
        }
        if (fragment.isAdded()) {
            Logger.e("fragment is Added");
            fragmentTransaction.show(fragment);
        } else {
            Logger.e("fragment is not Added");
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }
    private void showFragmentBeforeRemove() {
        if(fragmentManager==null)
            fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
//        if(fragment == null) {
//            Logger.e("fragment is null");
//            fragment = instantFragment(currIndex);
//        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if(f != null && f.isAdded()) {
                fragmentTransaction.remove(f);
                Logger.e("fragment is remove"+fragmentTags.get(i));
            }
        }
//        if (fragment.isAdded()) {
//            Logger.e("fragment is Added");
//            fragmentTransaction.show(fragment);
//        } else {
//            Logger.e("fragment is not Added");
//            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
//        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
        showFragment();
    }
    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0: return new MainPagerFragment();
            case 1: return new ChangeDeviceFragment();
            case 2: return new BundDeviceFragment();
            case 3: return new InsteadDeviceFragment();
            default: return null;
        }
    }

    private  void intiShake()
    {
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            public void onShake() {
                //Toast.makeText(getApplicationContext(), "抱歉，暂时没有找到在同一时刻摇一摇的人。\n再试一次吧！", Toast.LENGTH_SHORT).show();
                //startAnim();  //开始 摇一摇手掌动画
                if(mShakeListener!=null)
                {
                    mShakeListener.stop();
                    //sndPool.play(soundPoolMap.get(0), (float) 1, (float) 1, 0, 0,(float) 1.2);
                    mHandler.postDelayed(new Runnable(){
                        public void run(){
                            //Toast.makeText(getApplicationContext(), "抱歉，暂时没有找到\n在同一时刻摇一摇的人。\n再试一次吧！", 500).setGravity(Gravity.CENTER,0,0).show();
                            //sndPool.play(soundPoolMap.get(1), (float) 1, (float) 1, 0, 0,(float) 1.0);
//                            Toast mtoast;
//                            mtoast = Toast.makeText(getApplicationContext(), "手机长时间待机不动测试,手机动了！", Toast.LENGTH_SHORT);
//                            mtoast.setGravity(Gravity.CENTER, 0, 0);
//                            mtoast.show();
                            timeOfyundongCount = 1800;
                            //mVibrator.cancel();
                            if(mShakeListener!=null)
                                mShakeListener.start();

                        }
                    }, 2000);
                }
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(timeOfyundongCount--==0)
                {
                    timeOfyundongCount = 1800;
                    mHandler.sendEmptyMessage(1);
                }
            }
        },1000,1000);
    }
   // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
         runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Logger.e("发现新设备"+device.getAddress());
                    if(isNull(device,manageDevice.getLeftBDevice())||isNull(device,manageDevice.getLeftFDevice())||isNull(device,manageDevice.getRightBDevice())
                        ||isNull(device,manageDevice.getRightFDevice()))
                    {
                        bleIsFind(device);
                        broadcastUpdate(BluetoothLeService.ACTION_CHANGE_RESULT,device,rssi,scanRecord);
                    }
                    if(!isNull(device,manageDevice.getLeftBDevice())||!isNull(device,manageDevice.getLeftFDevice())||!isNull(device,manageDevice.getRightBDevice())
                            ||!isNull(device,manageDevice.getRightFDevice()))
                    {
                        broadcastUpdate(BluetoothLeService.ACTION_RETURN_OK,device,rssi,scanRecord);
                    }
                    //scanBleForResult(device);
                    // 发现小米3必须加以下的这3个语句，否则不更新数据，而三星的机子s3则没有这个问题
                    /*if (mScanning == true) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        mBluetoothAdapter.startLeScan(mLeScanCallback);
                    }*/
                }
             });
        }
    };
    private boolean isNull(BluetoothDevice device,String str)
    {
        if(str==null) return false;
        if(device.getAddress().contains(str))
        {
            return true;
        }
        return false;
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);

        return intentFilter;
    }
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra("DEVICE_ADDRESS");
                //断开
                onFailed(device);
                Logger.e("Disconneted GATT Services" + device.getAddress());
            }
        }
    };

    private void onFailed(BluetoothDevice device) {
        if(isDisconnect)
        {
            Logger.e("主动退出应用");
            for(MyBluetoothDevice ble : mDeviceList)
            {
                if(ble.getDevice().equals(device))
                {
                    if(!ble.isSuccessComm())
                        count++;
                }
            }
            if(count==mDeviceList.size())
            //if(!rightFDevice.isSuccessComm()&&!rightBDevice.isSuccessComm()&&!leftBDevice.isSuccessComm()&&!leftFDevice.isSuccessComm())
            {
                Logger.e("已经关闭所有蓝牙设备了！！！！！！！！");
                finish();
            }
        }
    }

    private void initBlueDevice()
    {
        manageDevice.setLeftFDevice(deviceDetails.getLeft_FD());
        manageDevice.setRightFDevice(deviceDetails.getRight_FD());
        manageDevice.setLeftBDevice(deviceDetails.getLeft_BD());
        manageDevice.setRightBDevice(deviceDetails.getRight_BD());
    }
    private void bleIsFind(BluetoothDevice device)
    {
        String strAddress = device.getAddress();
        if(strAddress.equals(manageDevice.getLeftBDevice()))
        {
            if(leftBDevice==null)
                leftBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftBDevice);
            leftBDevice.setBleScaned(true);
        }else if(strAddress.equals(manageDevice.getRightBDevice()))
        {
            if(rightBDevice==null)
                rightBDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(rightBDevice);
            rightBDevice.setBleScaned(true);
        }else if(strAddress.equals(manageDevice.getLeftFDevice()))
        {
            if(leftFDevice==null)
                leftFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(leftFDevice);
            leftFDevice.setBleScaned(true);
        }else if(strAddress.equals(manageDevice.getRightFDevice()))
        {
            if(rightFDevice==null)
                rightFDevice = MyBluetoothDevice.getInstance(device, mBluetoothLeService);
            if(!ManageDevice.isEquals(mDeviceList,device))
                mDeviceList.add(rightFDevice);
            rightFDevice.setBleScaned(true);
        }
    }
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

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        Thread.sleep(SCAN_PERIOD);
//                        if (mScanning) {
//                            Logger.e(TAG,"断开扫描");
//                            mScanning = false;
//                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                            invalidateOptionsMenu();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
            //WinToast.makeText(mContext,"start scan");
            App.getInstance().speak("正在打开蓝牙设备");
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        scanLeDevice(true);
        mScanning = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void broadcastUpdate(final String action, BluetoothDevice gatt,int rssi,byte[] scanResult) {
        final Intent intent = new Intent(action);
        intent.putExtra("DEVICE_ADDRESS", gatt);
        intent.putExtra("RSSI", rssi);
        intent.putExtra("SCAN_RECORD", scanResult);
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.back:
                exit();
                break;
            case R.id.img_set:
                menuWindow = new PopupMeumWindow(mContext,itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(mContext.findViewById(R.id.img_set), Gravity.TOP|Gravity.RIGHT, 0, CommonUtils.getStatusBarHeight(mContext)+110); //设置layout在PopupWindow中显示的位置
                break;
        }
    }
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
//                    ToastUtil.show("详情");
                    showNext(DeviceDetailActivity.class);
                    break;
                case R.id.btn_pick_photo:
//                    ToastUtil.show("解绑/绑定");
//                showNext(UnBundBlueActivity.class);
                    switchFragment(2);
                    break;
                case R.id.btn_cancel:
//                    ToastUtil.show("轮胎转位");
//                showNext(ChangeDevicePosition.class);
                    switchFragment(1);
                    break;
                default:
                    break;
            }
        }

    };
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exit();
                break;
            case R.id.menu_change:
                ToastUtil.show("详情");
                showNext(DeviceDetailActivity.class);
//                switchFragment(3);
                break;
            case R.id.menu_close:
                ToastUtil.show("解绑/绑定");
//                showNext(UnBundBlueActivity.class);
                switchFragment(2);
                break;
            case R.id.menu_luntai:
                ToastUtil.show("轮胎转位");
//                showNext(ChangeDevicePosition.class);
                switchFragment(1);
                break;
        }
        return true;
    }
    private void switchFragment(int index)
    {
        currIndex = index;
        stopScan();
        showFragment();
        Logger.e("切换："+index);
    }
    private void showNext(Class cl)
    {
        Intent intent = new Intent();
        intent.setClass(MainFrameActivity.this,cl);
        Bundle mBundle = new Bundle();
        mBundle.putInt("DB_ID",deviceId);
        intent.putExtras(mBundle);
        startActivity(intent);
    }
    public void startScan()
    {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    public void stopScan()
    {
        //App.getInstance().speak("正在关闭蓝牙设备");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService = null;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
        if(mShakeListener!=null)
            mShakeListener.stop();
        mShakeListener = null;

        System.gc();
        App.getInstance().pauseSound();
//        exit();
        Logger.e("MainActivity closed!!!");
    }

    /**
     * 检测四个模块是否断开，彻底断开后退出
     */
    private void exit() {
        if(currIndex==0)
        {
            isQuiting = true;
            finish();
        }else
        {
            refreashData();
            switchFragment(0);
            startScan();
        }
    }
    private void refreashData()
    {
        //刷新数据库
        deviceDetails = App.getDeviceDao().get(deviceId);
        initBlueDevice();
        Logger.e("刷新数据库："+deviceDetails.toString());
    }
    private SensorManager sensorManager;
    private Sensor magneticSensor;

    private Sensor accelerometerSensor;

    private Sensor gyroscopeSensor;
    // 将纳秒转化为秒
    private static final
    float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private float angle[] =new float[3];

    private void initSensor()
    {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor =sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor =sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
//SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
//SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
//SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
//SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this,gyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,magneticSensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,accelerometerSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }
//坐标轴都是手机从左侧到右侧的水平方向为x轴正向，从手机下部到上部为y轴正向，垂直于手机屏幕向上为z轴正向
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // x,y,z分别存储坐标轴x,y,z上的加速度
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // 根据三个方向上的加速度值得到总的加速度值a
            float a = (float) Math.sqrt(x * x + y * y + z * z);
            System.out.println("a---------->" + a);
            // 传感器从外界采集数据的时间间隔为10000微秒
            System.out.println("magneticSensor.getMinDelay()-------->"
                    + magneticSensor.getMinDelay());
            // 加速度传感器的最大量程
            System.out.println("event.sensor.getMaximumRange()-------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);
            Log.d("jarlen","x------------->" + x);
            Log.d("jarlen","y------------>" + y);
            Log.d("jarlen","z----------->" + z);
        // showTextView.setText("x---------->" + x + "\ny-------------->" +
        // y + "\nz----------->" + z);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        // 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
        // 手机的磁场感应器从外部采集数据的时间间隔是10000微秒
            System.out.println("magneticSensor.getMinDelay()-------->"
                    + magneticSensor.getMinDelay());
        // 磁场感应器的最大量程
            System.out.println("event.sensor.getMaximumRange()----------->"
                    + event.sensor.getMaximumRange());
            System.out.println("x------------->" + x);
            System.out.println("y------------->" + y);
            System.out.println("z------------->" + z);
            // Log.d("TAG","x------------->" + x);
            // Log.d("TAG", "y------------>" + y);
            // Log.d("TAG", "z----------->" + z);
            // showTextView.setText("x---------->" + x + "\ny-------------->" +
            // y + "\nz----------->" + z);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            //从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if(timestamp != 0){
            // 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                final float dT = (event.timestamp - timestamp) * NS2S;
            // 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;
            // 将弧度转化为角度
                float anglex = (float) Math.toDegrees(angle[0]);
                float angley = (float) Math.toDegrees(angle[1]);
                float anglez = (float) Math.toDegrees(angle[2]);
                System.out.println("anglex------------>" + anglex);
                System.out.println("angley------------>" + angley);
                System.out.println("anglez------------>" + anglez);
                System.out.println("gyroscopeSensor.getMinDelay()----------->"
                        + gyroscopeSensor.getMinDelay());
            }
            //将当前时间赋值给timestamp
            timestamp = event.timestamp;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO Auto-generated method stub
    }
    @Override
    public void onPause() {
        //TODO Auto-generated method stub
        super.onPause();
        //sensorManager.unregisterListener(this);
    }
}
