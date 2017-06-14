package com.example.sid_fu.blecentral.ui.frame;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.RecordData;
import com.example.sid_fu.blecentral.ui.BleData;
import com.example.sid_fu.blecentral.ui.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.DimenUtil;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.SoundPlayUtils;
import com.example.sid_fu.blecentral.utils.VibratorUtil;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/7/28.
 */
public abstract class BaseFragment extends Fragment{
    private ImageView imgTopleft;
    private ImageView imgTopright;
    private ImageView imgBottomleft;
    private ImageView imgBottomright;
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
    private TextView pressBottomleft;
    private TextView pressTopright;
    private TextView pressTopleft;
    private TextView pressBottomright;
    private TextView topleft_phone_rssi;
    private TextView topright_phone_rssi;
    private TextView bottomleft_phone_rssi;
    private TextView bottomright_phone_rssi;
    public MainFrameForStartServiceActivity mActivity;
    private DecimalFormat df1;
    private DecimalFormat df;
    private FragmentActivity context;
    private long vibratorTime = 1500;
    private FrameLayout topleftF;
    private FrameLayout toprightF;
    private FrameLayout bottomleftF;
    private FrameLayout bottomrightF;
    private AlphaAnimation alphaAnimation;
//    private Map<View,int[]> recycleViews = new HashMap<>();

    protected abstract void initData();

    protected abstract void initRunnable();

    protected abstract void initConfig();
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mActivity = (MainFrameForStartServiceActivity) activity;
        }catch (IllegalStateException e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_pressure, container, false);
        //DimenUtil.changeViewSize(container,DimenUtil.getScreenWidth(),DimenUtil.getScreenHeight());
        initConfig();
        context = getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRunnable();
        initUI();
        initTime();
        df1=new DecimalFormat("######0.00");
        df=new DecimalFormat("######0.0");
        initData();
        //注册广播
        getActivity().registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void initTime() {
        final Handler mHandler = new Handler();
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

    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    //ToastUtil.show(getActivity(), "home", 1).show();
                    mActivity.isQuiting = true;
                    SharedPreferences.getInstance().putBoolean("isAppOnForeground",true);
                    Logger.e("表示按了home键,程序到了后台");
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
    private void initUI() {
        if(context.isFinishing()) return;

        topleftF = (FrameLayout) getView().findViewById(R.id.topleft);
        toprightF = (FrameLayout) getView().findViewById(R.id.topright);
        bottomleftF = (FrameLayout) getView().findViewById(R.id.bottomleft);
        bottomrightF = (FrameLayout) getView().findViewById(R.id.bottomright);

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

    }

    private boolean isLeftF,isRightF,isRightB,isLeftB;

    public void showRssi(BluetoothDevice device,int rssi) {
        if(context.isFinishing()) return;
        if(device.getAddress().equals(mActivity.manageDevice.getLeftFDevice())) {
            topleft_phone_rssi.setVisibility(View.VISIBLE);
            topleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftF) {
                isLeftF = false;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else {
                isLeftF = true;
                topleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightFDevice())) {
            topright_phone_rssi.setVisibility(View.VISIBLE);
            topright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightF) {
                isRightF = false;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else {
                isRightF = true;
                topright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getLeftBDevice())) {
            bottomleft_phone_rssi.setVisibility(View.VISIBLE);
            bottomleft_phone_rssi.setText("手机RSSI："+rssi);
            if(isLeftB) {
                isLeftB = false;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else {
                isLeftB = true;
                bottomleft_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }else if(device.getAddress().equals(mActivity.manageDevice.getRightBDevice())) {
            bottomright_phone_rssi.setVisibility(View.VISIBLE);
            bottomright_phone_rssi.setText("手机RSSI："+rssi);
            if(isRightB) {
                isRightB = false;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.blue));
            }else {
                isRightB = true;
                bottomright_phone_rssi.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }
    /**
     * 白天模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    public void getDataTimeOutForDay(RecordData recordData, int state) {
        if(context.isFinishing()) return;
        switch (state) {
            case 1001:
                topleftF.setBackground(getResources().getDrawable(R.drawable.day_bg_link_left1_selector));
                topleft_voltage.setImageDrawable(null);
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");
                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);
                break;
            case 1002:
                toprightF.setBackground(getResources().getDrawable(R.drawable.day_bg_link_left1_selector));
                topright_voltage.setImageDrawable(null);
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightFDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);
                break;
            case 1003:
                bottomleftF.setBackground(getResources().getDrawable(R.drawable.day_bg_link_left1_selector));
                bottomleft_voltage.setImageDrawable(null);
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");
                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);
                break;
            case 1004:
                bottomrightF.setBackground(getResources().getDrawable(R.drawable.day_bg_link_left1_selector));
                bottomright_voltage.setImageDrawable(null);
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightBDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);
                break;
        }
    }

    /**
     * 夜间模式下 获取数据超时或者数据丢失 时间为3分钟
     * @param recordData
     * @param state
     */
    public void getDataTimeOutForNight(RecordData recordData, int state) {
        if(context.isFinishing()) return;
        switch (state) {
            case 1001:
//                topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                topleftF.setBackground(getResources().getDrawable(R.drawable.pmlink_off_left1_selctor));
                topleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                topleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topleft_releat.setText("");
                recordData.setName(mActivity.manageDevice.getLeftFDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getLeftFDevice(),recordData);

                break;
            case 1002:
//                topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                toprightF.setBackground(getResources().getDrawable(R.drawable.pmlink_off_left1_selctor));
                topright_temp.setText(getActivity().getString(R.string.defaulttemp));
                topright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                topright_releat.setText("");

                recordData.setName(mActivity.manageDevice.getRightFDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getRightFDevice(),recordData);

                break;
            case 1003:
                bottomleftF.setBackground(getResources().getDrawable(R.drawable.pmlink_off_left1_selctor));
//                bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomleft_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomleft_releat.setText("");

                recordData.setName(mActivity.manageDevice.getLeftBDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getLeftBDevice(),recordData);

                break;
            case 1004:
                bottomrightF.setBackground(getResources().getDrawable(R.drawable.pmlink_off_left1_selctor));
//                bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_off));
                bottomright_preesure.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_temp.setText(getActivity().getString(R.string.defaulttemp));
                bottomright_releat.setText("");
                recordData.setName(mActivity.manageDevice.getRightBDevice());
                App.getInstance().dbHelper.update(mActivity.deviceId,mActivity.manageDevice.getRightBDevice(),recordData);
                break;
        }
    }

    /**
     * 白天模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    public void dicoverBlueDeviceForDay(String strAddress, String noticeStr, float date) {
        if(context.isFinishing()) return;
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice())) {
//                ll_bottomright.setVisibility(View.VISIBLE);
            bottomleftF.setBackground(getResources().getDrawable(R.drawable.day_bg_normal_selector));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(bottomleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice())) {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            bottomrightF.setBackground(getResources().getDrawable(R.drawable.day_bg_normal_selector));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.phone));
            bottomright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(bottomright_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice())) {
//                ll_topleft.setVisibility(View.VISIBLE);
            topleftF.setBackground(getResources().getDrawable(R.drawable.day_bg_normal_selector));
            topleft_preesure.setTextColor(getResources().getColor(R.color.phone));
            topleft_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(topleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice())) {
//                ll_topright.setVisibility(View.VISIBLE);
            toprightF.setBackground(getResources().getDrawable(R.drawable.day_bg_normal_selector));
            topright_preesure.setTextColor(getResources().getColor(R.color.phone));
            topright_releat.setTextColor(getResources().getColor(R.color.himtphone));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.phone));
            handleVoltageShow(topright_voltage,date);
        }
    }

    /**
     * 夜间模式下，发现设备广播，UI初始化
     * @param strAddress
     * @param noticeStr
     * @param date
     */
    public void dicoverBlueDeviceForNight(String strAddress, String noticeStr, float date) {
        if(context.isFinishing()) return;
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice())) {
//                ll_bottomright.setVisibility(View.VISIBLE);
            bottomleftF.setBackground(getResources().getDrawable(R.drawable.pm_normal_left2_selector));
//            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomleft_releat.setText(noticeStr);
            pressBottomleft.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(bottomleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice())) {
//                ll_bottomleft.setVisibility(View.VISIBLE);
            bottomrightF.setBackground(getResources().getDrawable(R.drawable.pm_normal_right2_selector));
//            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            bottomright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            bottomright_releat.setText(noticeStr);
            pressBottomright.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(bottomright_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice())) {
//                ll_topleft.setVisibility(View.VISIBLE);
            topleftF.setBackground(getResources().getDrawable(R.drawable.pm_normal_left1_selector));
//            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topleft_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topleft_releat.setText(noticeStr);
            pressTopleft.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(topleft_voltage,date);
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice())) {
//                ll_topright.setVisibility(View.VISIBLE);
            toprightF.setBackground(getResources().getDrawable(R.drawable.pm_normal_right1_selector));
//            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_on));
            topright_preesure.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setTextColor(getResources().getColor(R.color.blue_night));
            topright_releat.setText(noticeStr);
            pressTopright.setTextColor(getResources().getColor(R.color.blue_night));
            handleVoltageShow(topright_voltage,date);
        }
    }

    /**
     * 白天模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    public void bleIsExceptionForDay(String strAddress,String noticeStr) {
        if(context.isFinishing()) return;
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice())) {
            bottomleftF.setBackground(getResources().getDrawable(R.drawable.am_error_selector));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify) {
                mActivity.manageDevice.leftB_notify = true;
                SoundPlayUtils.play(5);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice())) {
            bottomrightF.setBackground(getResources().getDrawable(R.drawable.am_error_selector));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify) {
                mActivity.manageDevice.rightB_notify = true;
                SoundPlayUtils.play(3);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice())) {
            topleftF.setBackground(getResources().getDrawable(R.drawable.am_error_selector));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify) {
                mActivity.manageDevice.leftF_notify = true;
                SoundPlayUtils.play(6);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice())) {
            toprightF.setBackground(getResources().getDrawable(R.drawable.am_error_selector));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify) {
                mActivity.manageDevice.rightF_notify = true;
                SoundPlayUtils.play(4);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }
    }

    /**
     * 夜间模式下，接收到蓝牙发送数据，进行异常报警UI
     * @param strAddress
     * @param noticeStr
     */
    public void bleIsExceptionForNight(String strAddress,String noticeStr) {
        if(context.isFinishing()) return;
        if(strAddress.equals(mActivity.manageDevice.getLeftBDevice())) {
            bottomleftF.setBackground(getResources().getDrawable(R.drawable.pm_error_selector));
            bottomleft_releat.setTextColor(getResources().getColor(R.color.red));
            bottomleft_releat.setText(noticeStr);
            bottomleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomleft.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.leftB_preContent))
                mActivity.manageDevice.leftB_notify = false;
            mActivity.manageDevice.leftB_preContent = noticeStr;
            if(!mActivity.manageDevice.leftB_notify) {
                mActivity.manageDevice.leftB_notify = true;
                SoundPlayUtils.play(5);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            bottomleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightBDevice())) {
            bottomrightF.setBackground(getResources().getDrawable(R.drawable.pm_error_selector));
            bottomright_releat.setTextColor(getResources().getColor(R.color.red));
            bottomright_releat.setText(noticeStr);
            bottomright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressBottomright.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.rightB_preContent))
                mActivity.manageDevice.rightB_notify = false;
            mActivity.manageDevice.rightB_preContent = noticeStr;
            if(!mActivity.manageDevice.rightB_notify) {
                mActivity.manageDevice.rightB_notify = true;
                SoundPlayUtils.play(3);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            bottomright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getLeftFDevice())) {
            topleftF.setBackground(getResources().getDrawable(R.drawable.pm_error_selector));
            topleft_releat.setTextColor(getResources().getColor(R.color.red));
            topleft_releat.setText(noticeStr);
            topleft_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopleft.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.leftF_preContent))
                mActivity.manageDevice.leftF_notify = false;
            mActivity.manageDevice.leftF_preContent = noticeStr;
            if(!mActivity.manageDevice.leftF_notify) {
                mActivity.manageDevice.leftF_notify = true;
                SoundPlayUtils.play(6);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
            }
            topleft_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }else if(strAddress.equals(mActivity.manageDevice.getRightFDevice())) {
            toprightF.setBackground(getResources().getDrawable(R.drawable.pm_error_selector));
            topright_releat.setTextColor(getResources().getColor(R.color.red));
            topright_releat.setText(noticeStr);
            topright_preesure.setTextColor(getResources().getColor(R.color.red));
            pressTopright.setTextColor(getResources().getColor(R.color.red));
            if(!noticeStr.equals(mActivity.manageDevice.rightF_preContent))
                mActivity.manageDevice.rightF_notify = false;
            mActivity.manageDevice.rightF_preContent = noticeStr;
            if(!mActivity.manageDevice.rightF_notify) {
                mActivity.manageDevice.rightF_notify = true;
                SoundPlayUtils.play(4);
                VibratorUtil.Vibrate(getActivity(), vibratorTime);   //震动100ms
//                startAlphaAnimation(toprightF);
            }else {
//                cancelAlphaAnimation(toprightF);
            }
            topright_voltage.setImageDrawable(getResources().getDrawable(R.mipmap.pm_warning));
        }
    }

    /**
     *  数据显示
     * @param device
     * @param bleData
     */
    public void showDataForUI(String device , BleData bleData) {
        if(context.isFinishing()) return;
        setUnitTextSize();
        String barData = bleData.getStringPress();
        if(device.equals(mActivity.manageDevice.getLeftBDevice())) {
            bottomleft_preesure.setText(barData);
            bottomleft_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(bottomleft_voltage,bleData.getVoltage());
        }else if(device.equals(mActivity.manageDevice.getRightBDevice())) {
            bottomright_preesure.setText(barData);
            bottomright_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(bottomright_voltage,bleData.getVoltage());
        }else if(device.equals(mActivity.manageDevice.getLeftFDevice())) {
            topleft_preesure.setText(barData);
            topleft_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(topleft_voltage,bleData.getVoltage());

        }else if(device.equals(mActivity.manageDevice.getRightFDevice())) {
            topright_preesure.setText(barData);
            topright_temp.setText(String.valueOf(bleData.getTemp()));
            handleVoltageShow(topright_voltage,bleData.getVoltage());
        }
    }
    /**
     * 电池变化情况指示
     * @param img
     * @param voltage
     */
    public void handleVoltageShow(ImageView img,float voltage) {
        if(Constants.NO_SHOW_VOL) {
            img.setImageDrawable(null);
            return;
        }
        if(context.isFinishing()) return;
        if (!SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT,false)) {
            if(voltage>=Constants.vol) {
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
        }else{
            if(voltage>=Constants.vol) {
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_100));
            }else if(voltage>Constants.vol*0.8&&voltage<Constants.vol){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_80));
            }else if(voltage>Constants.vol*0.5&&voltage<Constants.vol*0.8){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_50));
            }else if(voltage>Constants.vol*0.2&&voltage<Constants.vol*0.5){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_20));
            }else if(voltage>0&&voltage<Constants.vol*0.2){
                img.setImageDrawable(getResources().getDrawable(R.mipmap.pm_battle_0));
            }
        }
    }
    public void setUnitTextSize() {
        if(context.isFinishing()) return;
        int rate = DimenUtil.adjustFontSize(context);
        Logger.e("字体大小："+rate);
        if(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar").equals("Kpa")) {
            topleft_preesure.setTextSize(rate);
            topright_preesure.setTextSize(rate);
            bottomleft_preesure.setTextSize(rate);
            bottomright_preesure.setTextSize(rate);
        }else {
            topleft_preesure.setTextSize(DimenUtil.getDimension(R.dimen.press_size_kpa));
            topright_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
            bottomleft_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
            bottomright_preesure.setTextSize(getResources().getDimension(R.dimen.press_size_kpa));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mHomeKeyEventReceiver);
        System.gc();
        Logger.e("onDestroy");
    }

    public void startAlphaAnimation(FrameLayout bg){
        /**
         * @param fromAlpha 开始的透明度，取值是0.0f~1.0f，0.0f表示完全透明， 1.0f表示和原来一样
         * @param toAlpha 结束的透明度，同上
         */
        if(alphaAnimation==null)
            alphaAnimation = new AlphaAnimation(1.0f, 0.2f);
        //设置动画持续时长
        alphaAnimation.setDuration(3000);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        alphaAnimation.setFillAfter(true);
        //设置动画结束之后的状态是否是动画开始时的状态，true，表示是保持动画开始时的状态
        alphaAnimation.setFillBefore(true);
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        alphaAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        //设置动画播放次数
        alphaAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        //开始动画
        bg.startAnimation(alphaAnimation);

    }
    private void cancelAlphaAnimation(FrameLayout bg){
        //清除动画
        bg.clearAnimation();
        //同样cancel()也能取消掉动画
        alphaAnimation.cancel();
        alphaAnimation = null;
    }
}
