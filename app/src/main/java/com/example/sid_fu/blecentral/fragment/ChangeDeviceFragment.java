package com.example.sid_fu.blecentral.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.MyBluetoothDevice;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.ToastUtil;

/**
 * Created by Administrator on 2016/6/6.
 */
public class ChangeDeviceFragment extends Fragment implements View.OnClickListener{
    private MainFrameForStartServiceActivity mActivity;
    private TextView editConfig;
    private TextView topleftAdjust;
    private TextView toprightAdjust;
    private TextView bottomleftAdjust;
    private TextView bottomrightAdjust;
    private String curStrAddr;
    private String preStrAddr;
    private String preStr;
    private String curStr;
    private DeviceDao deviceDaoUtils;
    private TextView imgTopleft;
    private TextView imgTopright;
    private TextView imgBottomleft;
    private TextView imgBottomright;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainFrameForStartServiceActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_change_position, container, false);
//        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        App.getInstance().speak("您正在使用轮胎转位功能");
        deviceDaoUtils = App.getDeviceDao();
    }
    private void initUI() {
        editConfig = (TextView) getView().findViewById(R.id.edit_config);
        imgTopleft = (TextView)getView(). findViewById(R.id.img_topleft);
        imgTopright = (TextView) getView().findViewById(R.id.img_topright);
        imgBottomleft = (TextView)getView(). findViewById(R.id.img_bottomleft);
        imgBottomright = (TextView)getView(). findViewById(R.id.img_bottomright);
        topleftAdjust = (TextView)getView().findViewById(R.id.topleft_adjust);
        toprightAdjust = (TextView) getView().findViewById(R.id.topright_adjust);
        bottomleftAdjust = (TextView) getView().findViewById(R.id.bottomleft_adjust);
        bottomrightAdjust = (TextView) getView().findViewById(R.id.bottomright_adjust);

//        topleft_preesure = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
//        topleft_temp = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
//        topleft_note = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);
//        topleft_voltage = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_voltage);
//        topleft_releat = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_releat);
//
//        topright_preesure = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
//        topright_temp = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
//        topright_note = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_note);
//        topright_voltage = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_voltage);
//        topright_releat = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_releat);
//
//        bottomleft_preesure = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
//        bottomleft_temp = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
//        bottomleft_note = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);
//        bottomleft_voltage = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_voltage);
//        bottomleft_releat = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_releat);
//
//        bottomright_preesure = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
//        bottomright_temp = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
//        bottomright_note = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);
//        bottomright_voltage = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_voltage);
//        bottomright_releat = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_releat);

        bottomleftAdjust.setOnClickListener(this);
        bottomrightAdjust.setOnClickListener(this);
        topleftAdjust.setOnClickListener(this);
        toprightAdjust.setOnClickListener(this);
        editConfig.setOnClickListener(this);

//        initData(leftBDevice,topleftAdjust);
//        initData(rightBDevice,toprightAdjust);
//        initData(leftFDevice,bottomleftAdjust);
//        initData(rightFDevice,bottomrightAdjust);

    }
    private void initData(MyBluetoothDevice device, TextView btn)
    {
        if(device==null)
        {
            btn.setText(getResources().getString(R.string.erronofind));
            btn.setEnabled(false);
        }else if(!device.isSuccessComm())
        {
            btn.setText(getResources().getString(R.string.erroinfo));
            btn.setEnabled(false);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topleft_adjust:
                if (topleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    topleftAdjust.setVisibility(View.GONE);
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "左前轮"/*+ mActivity.manageDevice.getLeftFDevice()*/;
                    preStrAddr = mActivity.manageDevice.getLeftFDevice();
                    //App.getInstance().speak("您选择了调换左前轮");
                } else {
                    reset();
                    curStr = "左前轮"/*+mActivity.manageDevice.getLeftFDevice()*/;
                    curStrAddr = mActivity.manageDevice.getLeftFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }

                break;
            case R.id.topright_adjust:
                if (toprightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    toprightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "右前轮"/*+mActivity.manageDevice.getRightFDevice()*/;
                    preStrAddr = mActivity.manageDevice.getRightFDevice();
                } else {
                    reset();
                    curStr = "右前轮"/*+mActivity.manageDevice.getRightFDevice()*/;
                    curStrAddr = mActivity.manageDevice.getRightFDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }
                break;
            case R.id.bottomleft_adjust:

                if (bottomleftAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    bottomleftAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomrightAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "左后轮"/*+mActivity.manageDevice.getLeftBDevice()*/;
                    preStrAddr = mActivity.manageDevice.getLeftBDevice();
                } else {
                    reset();
                    curStr = "左后轮"/*+mActivity.manageDevice.getLeftBDevice()*/;
                    curStrAddr = mActivity.manageDevice.getLeftBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }
                break;
            case R.id.bottomright_adjust:

                if (bottomrightAdjust.getText().equals(getResources().getString(R.string.adjust))) {
                    bottomrightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    toprightAdjust.setText(getResources().getString(R.string.adjustTo));
                    bottomleftAdjust.setText(getResources().getString(R.string.adjustTo));
                    preStr = "右后轮"/*+mActivity.manageDevice.getRightBDevice()*/;
                    preStrAddr = mActivity.manageDevice.getRightBDevice();
                } else {
                    reset();
                    curStr = "右后轮"/*+mActivity.manageDevice.getRightBDevice()*/;
                    curStrAddr = mActivity.manageDevice.getRightBDevice();
//                    ToastUtil.show(preStr + "与" + curStr + "对调");
                    showDialog(preStr,curStr,preStrAddr,curStrAddr);
                }
                break;
            case R.id.edit_config:
                if (editConfig.getText().equals("校准")) {
                    showEditUI();
                    editConfig.setText("保存");
                } else if (editConfig.getText().equals("保存")) {
                    editConfig.setText("校准");
                    dissmEditUI();
                }
                break;
        }
    }
    /**
     *
     * @param preStr 被调换的位置
     * @param curStr
     */
    private void showDialog(final String preStr, final String curStr,final String preStrAddr,final String curStrAddr)
    {
//        App.getInstance().speak(preStr+"与"+curStr+"进行对调，请选择确定或者取消");
        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题
                .setMessage(preStr+"  与  "+curStr+"  进行对调？")//设置显示的内容
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        // TODO Auto-generated method stub
//                         finish();
                        dialog.dismiss();
                        if(preStr.contains("左前轮"))
                        {
//                            mActivity.manageDevice.setLeftFDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_F,mActivity.deviceId,curStrAddr);
                            imgTopleft.setText(curStr);
                        }else if(preStr.contains("右前轮"))
                        {
//                            mActivity.manageDevice.setRightFDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_F,mActivity.deviceId,curStrAddr);
                            imgTopright.setText(curStr);
                        }else if(preStr.contains("左后轮"))
                        {
//                            mActivity.manageDevice.setLeftBDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_B,mActivity.deviceId,curStrAddr);
                            imgBottomleft.setText(curStr);
                        }else if(preStr.contains("右后轮"))
                        {
//                            mActivity.manageDevice.setRightBDevice(curStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_B,mActivity.deviceId,curStrAddr);
                            imgBottomright.setText(curStr);
                        }
                        if(curStr.contains("左前轮"))
                        {
//                            mActivity.manageDevice.setLeftFDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_F,mActivity.deviceId,preStrAddr);
                            imgTopleft.setText(preStr);
                        }else if(curStr.contains("右前轮"))
                        {
//                            mActivity.manageDevice.setRightFDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_F,mActivity.deviceId,preStrAddr);
                            imgTopright.setText(preStr);
                        }else if(curStr.contains("左后轮"))
                        {
//                            mActivity.manageDevice.setLeftBDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.LEFT_B,mActivity.deviceId,preStrAddr);
                            imgBottomleft.setText(preStr);
                        }else if(curStr.contains("右后轮"))
                        {
//                            mActivity.manageDevice.setRightBDevice(preStrAddr);
                            deviceDaoUtils.update(Constants.RIGHT_B,mActivity.deviceId,preStrAddr);
                            imgBottomright.setText(preStr);
                        }
                        ToastUtil.show(preStr + "与" + curStr + "已经对调");
                    }
                }).setNegativeButton("不用了",new DialogInterface.OnClickListener() {//添加返回按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {//响应事件
                // TODO Auto-generated method stub
//                    Log.i("alertdialog"," 请保存数据！");
//                    finish();
                dialog.dismiss();
            }
        }).show();//在按键响应事件中显示此对话框
    }
    private void showEditUI() {
        reset();
    }

    private void dissmEditUI() {
        topleftAdjust.setVisibility(View.GONE);
        toprightAdjust.setVisibility(View.GONE);
        bottomleftAdjust.setVisibility(View.GONE);
        bottomrightAdjust.setVisibility(View.GONE);
    }

    private void reset() {
        bottomleftAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setText(getResources().getString(R.string.adjust));
        toprightAdjust.setText(getResources().getString(R.string.adjust));
        bottomrightAdjust.setText(getResources().getString(R.string.adjust));
        topleftAdjust.setVisibility(View.VISIBLE);
        toprightAdjust.setVisibility(View.VISIBLE);
        bottomleftAdjust.setVisibility(View.VISIBLE);
        bottomrightAdjust.setVisibility(View.VISIBLE);
    }
}
