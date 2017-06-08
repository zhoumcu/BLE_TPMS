package com.example.sid_fu.blecentral;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.adapter.DeviceAdapter;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.helper.HomeDataHelper;
import com.example.sid_fu.blecentral.model.SampleGattAttributes;
import com.example.sid_fu.blecentral.ui.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.ui.activity.base.BaseActivity;
import com.example.sid_fu.blecentral.ui.activity.car.CarInfoDetailActivity;
import com.example.sid_fu.blecentral.ui.activity.car.CarListViewActivity;
import com.example.sid_fu.blecentral.ui.activity.setting.PersonSettingActivity;
import com.example.sid_fu.blecentral.utils.BitmapUtils;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.widget.PictureView;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sid-fu on 2016/5/17.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.img_scan)
    ImageView imgScan;
    @Bind(R.id.img_set)
    ImageView imgSet;

    private ListView listView;
    private static boolean isExit = false;
    private List<Device> articles = new ArrayList<>();
    private DeviceAdapter adapter;
    private HomeActivity mContext;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_cartype;
    private ImageView img_icon;
    private TextView tv_normal;
    private TextView tv_current;
    private TextView tv_state;
    private View view;
    private TextView btn_details;
    private TextView btn_bund;
    private Device currentDevice;
    private TextView btn_normal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);
        mContext = HomeActivity.this;
        ButterKnife.bind(this);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                //根据updateStatus来判断更新是否成功
                Logger.e("更新："+updateStatus + updateInfo.toString());
            }
        });
        intView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreash();
    }
    private void refreash() {
        Observable<List<Device>> observable = Observable.just(App.getDeviceDao().listByAll());
        observable.unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Device>>() {
            @Override
            public void call(List<Device> devices) {
                articles = devices;
                if(devices==null)return;
                if(devices.size()==0) view.setVisibility(View.GONE);
                adapter.updateData(fillterDate(devices));
                initDate();
            }
        });
    }
    private void initDate() {
        for (Device device : articles) {
            if(device.getDefult()!=null) {
                if(device.getDefult().equals("true")) {
                    initData(device);
                }
            }
        }
    }

    private void intView() {
        listView = (ListView) findViewById(R.id.listView);
        ImageView img_add = (ImageView) findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setClass(HomeActivity.this, CarListViewActivity.class);
                startActivity(intent1);
            }
        });
        adapter = new DeviceAdapter(this, articles);
        listView.setAdapter(adapter);
        view  = findViewById(R.id.normal);
        view.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_content);
        tv_cartype = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_cartype);
        img_icon = (ImageView) findViewById(R.id.normal).findViewById(R.id.img_icon);
        tv_normal = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_normal);
        tv_current = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_current);
        tv_state = (TextView) findViewById(R.id.normal).findViewById(R.id.tv_state);
        ImageView img_ecode = (ImageView) findViewById(R.id.normal).findViewById(R.id.img_ecode);
        btn_bund = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_bund);
        btn_normal = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_normal);
        btn_details = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_details);
        TextView btn_delete = (TextView) findViewById(R.id.normal).findViewById(R.id.btn_delete);
        RelativeLayout btn_rl = (RelativeLayout) findViewById(R.id.normal).findViewById(R.id.btn_rl);
        img_ecode.setOnClickListener(this);
        btn_bund.setOnClickListener(this);
        btn_normal.setOnClickListener(this);
        btn_details.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_rl.setOnClickListener(this);
        btn_delete.setEnabled(false);
        btn_details.setEnabled(false);
        tv_state.setEnabled(true);
        adapter.setonCallBack(new DeviceAdapter.onCallBack() {
            @Override
            public void setOnClick(Device device) {
                Intent intent = new Intent(SampleGattAttributes.ACTION_REFREASH_DEVICE);
                Bundle mBundle = new Bundle();
                mBundle.putInt("id", device.getId());
                intent.putExtras(mBundle);
                mContext.sendBroadcast(intent);
                currentDevice = device;
                initData(device);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_ecode:
                break;
            case R.id.btn_bund:
                Bundle mBundle = new Bundle();
                mBundle.putInt("id", currentDevice.getCarID());
                mBundle.putInt("state", CarInfoDetailActivity.DETAILS);
                goActivity(CarInfoDetailActivity.class,mBundle);
                break;
            case R.id.btn_normal:
                String contentString = "vlt_tpms_device"
                        + "|" + currentDevice.getLeft_FD()
                        + "|" +currentDevice.getRight_FD()
                        + "|" + currentDevice.getLeft_BD()
                        + "|" + currentDevice.getRight_BD()
                        + "|" +currentDevice.getDeviceName()
                        + "|" +currentDevice.getDeviceDescripe()
                        + "|" +currentDevice.getImagePath()
                        + "|" +currentDevice.getBackMinValues()
                        + "|" +currentDevice.getBackmMaxValues()
                        + "|" +currentDevice.getFromMinValues();
                Intent intent = new Intent();
                intent.setClass(mContext, PictureView.class);
                intent.putExtra("macImage", contentString);
                intent.putExtra("device",currentDevice);
                mContext.startActivity(intent);
                break;
            case R.id.btn_details:
                break;
            case R.id.btn_rl:
                Logger.e("onStartCommand put"+currentDevice.getId());
                SharedPreferences.getInstance().remove(Constants.LAST_DEVICE_ID);
                SharedPreferences.getInstance().putInt(Constants.LAST_DEVICE_ID, currentDevice.getId());
                SharedPreferences.getInstance().putBoolean("isAppOnForeground",true);
                Logger.e("onStartCommand put after"+SharedPreferences.getInstance().getInt(Constants.LAST_DEVICE_ID,0));
                Intent intent1 = new Intent();
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setClass(mContext, MainFrameForStartServiceActivity.class);
                Bundle mBundle1 = new Bundle();
                mBundle1.putSerializable("DB_ARTICLES", currentDevice);
                mBundle1.putInt("DB_ID",currentDevice.getId());
                intent1.putExtras(mBundle1);
                mContext.startActivity(intent1);
                break;
        }
    }
    @OnClick(R.id.img_scan)
    public void scan() {
//        ToastUtil.show("扫一扫");
        //打开扫描界面扫描条形码或二维码
        Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }
    @OnClick(R.id.img_set)
    public void goSetting() {
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this, PersonSettingActivity.class);
        startActivity(intent);
        App.getInstance().addActivity(this);
//        ToastUtil.show("设置");
    }

    private void initData(Device device) {
        currentDevice = device;
        view.setVisibility(View.VISIBLE);
        tv_title.setText(device.getDeviceName());
        tv_content.setText(device.getDeviceDescripe());
        btn_details.setText("已默认");
        Logger.e(device.toString());
        if(device.getIsShare().equals("false")) {
            btn_bund.setEnabled(true);
            btn_normal.setEnabled(true);
        }else {
            btn_bund.setEnabled(false);
            btn_normal.setEnabled(false);
        }
        img_icon.setImageBitmap(BitmapUtils.getImageFromAssetsFile(mContext,"logo/"+device.getImagePath()+".png"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set:
                SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this, PersonSettingActivity.class);
                startActivity(intent);
//                ToastUtil.show("个人");
                break;
            case R.id.menu_add:
//                ToastUtil.show("添加");
                SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
                Intent intent1 = new Intent();
                intent1.setClass(HomeActivity.this, CarListViewActivity.class);
                startActivity(intent1);
                break;
            case R.id.menu_sacan:
//                ToastUtil.show("扫一扫");
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(HomeActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Logger.e("结果为："+scanResult);
            String[] date = scanResult.split("\\|");
            new DeviceDao(this).add(HomeDataHelper.getData(this,date));
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            Logger.e("exit application");
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public static List<Device> fillterDate(List<Device> devices) {
        List<Device> articles = new ArrayList<>();
        for (Device device :devices) {
            if (device.getDefult() != null) {
                if (device.getDefult().equals("true")) {
                    continue;
                }
            }
            articles.add(device);
        }
        return articles;
    }
}
