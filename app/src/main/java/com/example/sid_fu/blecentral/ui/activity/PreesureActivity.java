package com.example.sid_fu.blecentral.ui.activity;


import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.sid_fu.blecentral.BluetoothLeService;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.ui.frame.TitleBarActivity;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.TimerDialog;
import com.example.sid_fu.blecentral.widget.WiperSwitch;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/4/5.
 */
public class PreesureActivity extends TitleBarActivity {

    private static final int HIGHT = 1;
    private static final int LOW = 2;
    private static final int NORMAL = 3;
    @Bind(R.id.img_topleft)
    ImageView imgTopleft;
    @Bind(R.id.img_topright)
    ImageView imgTopright;
    @Bind(R.id.img_bottomleft)
    ImageView imgBottomleft;
    @Bind(R.id.img_bottomright)
    ImageView imgBottomright;
    @Bind(R.id.topleft_adjust)
    TextView topleftAdjust;
    @Bind(R.id.topright_adjust)
    TextView toprightAdjust;
    @Bind(R.id.bottomleft_adjust)
    TextView bottomleftAdjust;
    @Bind(R.id.bottomright_adjust)
    TextView bottomrightAdjust;
    @Bind(R.id.current_time)
    TextView currentTime;
    @Bind(R.id.edit_config)
    TextView editConfig;
    @Bind(R.id.link_time)
    TextView linkTime;

    private TextView topleft_preesure;
    private TextView topleft_temp;
    private TextView topright_preesure;
    private TextView topright_temp;
    private TextView bottomleft_preesure;
    private TextView bottomleft_temp;
    private TextView bottomright_preesure;
    private TextView bottomright_temp;

    private double data[][] = {{12.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {32.5, 12, 1, 0, 1, 1}, {30.5, 40, 0, 1, 1, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {14.5, 25.0, 1, 1, 1, 1}, {34.5, 40, 0, 0, 1, 0}, {39.5, 40, 1, 0, 1, 0},
            {16.5, 25.0, 1, 0, 0, 1}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 40, 1, 0, 1, 0}, {25.5, 15, 1, 1, 1, 0},
            {22.5, 2, 0, 0, 1, 0}, {12.5, 25, 1, 0, 1, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {32.5, 12, 1, 1, 1, 0}, {30.5, 40, 1, 0, 0, 0}, {14.5, 25.0, 0, 0, 0, 0}, {34.5, 40, 0, 0, 0, 0}, {39.5, 40, 1, 0, 1, 0},
            {16.5, 25.0, 1, 0, 0, 0}, {30.5, 40, 0, 0, 1, 0},
            {22.5, 2, 1, 0, 1, 1}, {50.5, 40, 1, 1, 1, 0}, {30.5, 40, 1, 1, 0, 0}, {25.5, 15, 1, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {50.5, 40, 1, 0, 1, 0}, {30.5, 40, 1, 0, 1, 0}, {30.5, 40, 1, 0, 1, 0}
            , {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}, {30.5, 25, 0, 0, 0, 0}
            , {30.5, 25, 0, 0, 0, 0},};
    private int count = 0;
    private Random random;
    private TextView topleft_note;
    private TextView topright_note;
    private TextView bottomleft_note;
    private TextView bottomright_note;
    private boolean isClick = false;
    private Timer timer;
    private Handler handler;
    private MyTimerTask myTimerTask;
    private String preStr;
    private String curStr;
    private AlphaAnimation alphaAnimation1;
    private TimerDialog dialog;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.aty_pressure);
    }

    @Override
    public void setWidget() {
        setTitleBarStyle(0);

        getTitleBarLayout().setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!isClick)
//                {
//                    isClick = true;
//                    showEditUI();
//                }else {
//                    isClick = false;
//                    dissmEditUI();
//                }
//                alphaAnimation1.cancel();
                if(myTimerTask!=null&&alphaAnimation1!=null)
                {
                    myTimerTask.cancel();
                    alphaAnimation1.cancel();
                }
//                showActivity(PreesureActivity.this, DeviceScanActivity.class);
            }
        });
        WiperSwitch switch1 = (WiperSwitch) findViewById(R.id.switch1);
        //switch1.setChecked(true);
        switch1.setOnChangeListener(new WiperSwitch.OnChangeListener() {
            @Override
            public void onChange(WiperSwitch sb, boolean state) {
                if(state)
                {
                    myTimerTask = new MyTimerTask();
                    timer.schedule(myTimerTask, 1000, 1000);
                }else
                {
                    if(myTimerTask!=null)
                        myTimerTask.cancel();
                    alphaAnimation1.cancel();
                }
            }
        });

        topleft_preesure = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_preesure);
        topleft_temp = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_temp);
        topleft_note = (TextView) findViewById(R.id.ll_topleft).findViewById(R.id.tv_note);

        topright_preesure = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_preesure);
        topright_temp = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_temp);
        topright_note = (TextView) findViewById(R.id.ll_topright).findViewById(R.id.tv_note);

        bottomleft_preesure = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_preesure);
        bottomleft_temp = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_temp);
        bottomleft_note = (TextView) findViewById(R.id.ll_bottomleft).findViewById(R.id.tv_note);

        bottomright_preesure = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_preesure);
        bottomright_temp = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_temp);
        bottomright_note = (TextView) findViewById(R.id.ll_bottomright).findViewById(R.id.tv_note);

    }

    @Override
    public void setData() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    int count = (Integer) msg.obj;
//                    int count = random.nextInt(data.length);
//                    if (count >= data.length - 4) count = 0;
                    topleft_preesure.setText(String.valueOf(data[count][0]));
                    topleft_temp.setText(String.valueOf(data[count][1]));
                    topright_preesure.setText(String.valueOf(data[count + 1][0]));
                    topright_temp.setText(String.valueOf(data[count + 1][1]));
                    bottomleft_preesure.setText(String.valueOf(data[count + 2][0]));
                    bottomleft_temp.setText(String.valueOf(data[count + 2][1]));
                    bottomright_preesure.setText(String.valueOf(data[count + 3][0]));
                    bottomright_temp.setText(String.valueOf(data[count + 3][1]));
                    handleException(data[count], "左前轮", topleft_note, imgTopleft);
                    handleException(data[count + 1], "右前轮", topright_note, imgTopright);
                    handleException(data[count + 2], "左后轮", bottomleft_note, imgBottomleft);
                    handleException(data[count + 3], "右后轮", bottomright_note, imgBottomright);
//                    switch (count1)
//                    {
//                        case 1:
//                            handleException(data[count], "左前轮", topleft_note, imgTopleft);
//                            break;
//                        case 2:
//                            handleException(data[count + 1], "右前轮", topright_note, imgTopright);
//                            break;
//                        case 3:
//                            handleException(data[count + 2], "左后轮", bottomleft_note, imgBottomleft);
//                            break;
//                        case 4:
//                            handleException(data[count + 3], "右后轮", bottomright_note, imgBottomright);
//                            break;
//                    }
                }
                return false;
            }
        });
        timer = new Timer();
        random = new Random();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);

        //闪烁
        alphaAnimation1 = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation1.setDuration(300);
        alphaAnimation1.setRepeatCount(2);
        alphaAnimation1.setRepeatCount(Animation.INFINITE);
        alphaAnimation1.setRepeatMode(Animation.REVERSE);

        dialog = new TimerDialog(PreesureActivity.this);
        dialog.setTitle("异常报警");
        dialog.setPositiveButton("sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "pppp", 500).show();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 1000, 1000);
            }
        }
        , 0);
        dialog.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 1000, 1000);
                dialog.dismiss();
            }
        },20);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            count = random.nextInt(data.length-4);
            //if (count >= data.length - 4) count = 0;
            Message msg = new Message();
            msg.obj = count;
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.topleft_adjust, R.id.topright_adjust, R.id.bottomleft_adjust, R.id.bottomright_adjust,R.id.edit_config})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topleft_adjust:
                if (topleftAdjust.getText().equals("校验")) {
                    topleftAdjust.setVisibility(View.GONE);
                    toprightAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "左前轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "左前轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }

                break;
            case R.id.topright_adjust:

                if (toprightAdjust.getText().equals("校验")) {
                    toprightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "右前轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "右前轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.bottomleft_adjust:

                if (bottomleftAdjust.getText().equals("校验")) {
                    bottomleftAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    toprightAdjust.setText("对调");
                    bottomrightAdjust.setText("对调");
                    preStr = "左后轮";
                } else {
                    reset();
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    curStr = "左后轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.bottomright_adjust:

                if (bottomrightAdjust.getText().equals("校验")) {
                    bottomrightAdjust.setVisibility(View.GONE);
                    topleftAdjust.setText("对调");
                    toprightAdjust.setText("对调");
                    bottomleftAdjust.setText("对调");
                    preStr = "右后轮";
                } else {
//                    topleftAdjust.setVisibility(View.GONE);
//                    toprightAdjust.setText("对调");
//                    bottomleftAdjust.setText("对调");
//                    bottomrightAdjust.setText("对调");
                    reset();
                    curStr = "右后轮";
                    ToastUtil.show(preStr + "与" + curStr + "对调");
                }
                break;
            case R.id.edit_config:
                if (editConfig.getText().equals("校准")) {
                    showEditUI();
                    editConfig.setText("保存");
                }else if (editConfig.getText().equals("保存"))
                {
                    editConfig.setText("校准");
                    dissmEditUI();
                }
                break;
        }
    }

    private void showEditUI() {
        if (myTimerTask != null)
            myTimerTask.cancel();
        reset();
        alphaAnimation1.cancel();
    }

    private void dissmEditUI() {
//        if(myTimerTask==null)
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        topleftAdjust.setVisibility(View.GONE);
        toprightAdjust.setVisibility(View.GONE);
        bottomleftAdjust.setVisibility(View.GONE);
        bottomrightAdjust.setVisibility(View.GONE);
    }

    private void reset() {
        bottomleftAdjust.setText("校验");
        topleftAdjust.setText("校验");
        toprightAdjust.setText("校验");
        bottomrightAdjust.setText("校验");
        topleftAdjust.setVisibility(View.VISIBLE);
        toprightAdjust.setVisibility(View.VISIBLE);
        bottomleftAdjust.setVisibility(View.VISIBLE);
        bottomrightAdjust.setVisibility(View.VISIBLE);
    }

    private void calculationPressuerHight(TextView v, ImageView img, String str) {
        img.setBackgroundColor(getResources().getColor(R.color.white));
        v.setVisibility(View.VISIBLE);
        v.setText("高压");
        ToastUtil.show(str + "高压警报");
    }

    private void calculationPressuerLow(TextView v, ImageView img, String str) {
        img.setBackgroundColor(getResources().getColor(R.color.white));
        v.setVisibility(View.VISIBLE);
        v.setText("低压");
        ToastUtil.show(str + "低压警报");
    }

    private void calculationPressuer(int status, TextView v, ImageView img, String str) {
        switch (status) {
            case HIGHT:
                img.setBackgroundColor(getResources().getColor(R.color.white));
                //v.setVisibility(View.VISIBLE);
                //v.setText("高压");
                ToastUtil.show(str + "高压警报");
                break;
            case LOW:
                img.setBackgroundColor(getResources().getColor(R.color.white));
                //v.setVisibility(View.VISIBLE);
                //v.setText("低压");
                ToastUtil.show(str + "低压警报");
                break;
            case NORMAL:
                img.setBackground(null);
                //v.setVisibility(View.GONE);
                break;
        }
    }

    private void handleException(double[] date, String str, TextView v, ImageView img) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(str + ":");
        buffer.append(date[0] > 35 ? "高压" + "\n" : "");
        buffer.append(date[0] < 20 ? "低压" + "\n" : "");
        buffer.append(date[1] > 35 ? "高温" + "\n" : "");
        buffer.append(date[1] < 20 ? "低温" + "\n" : "");
        buffer.append(date[2] == 1 ? "传感器失效" + "\n" : "");
        buffer.append(date[3] == 1 ? "传感器低电" + "\n" : "");
        buffer.append(date[4] == 1 ? "慢漏气" + "\n" : "");
        buffer.append(date[5] == 1 ? "快漏气" + "\n" : "");

        if (!TextUtils.isEmpty(buffer) && buffer != null && !buffer.toString().equals(str + ":")) {
            img.setBackgroundColor(getResources().getColor(R.color.white));
            img.setAnimation(alphaAnimation1);
            alphaAnimation1.start();
            v.setVisibility(View.VISIBLE);
            v.setText(buffer.toString());
            ToastUtil.show(buffer.toString() + "异常警报");
//            showDialog(buffer.toString());
        } else {
            if (alphaAnimation1.isFillEnabled())
                alphaAnimation1.cancel();
            img.setBackground(null);
            v.setVisibility(View.GONE);
        }

    }
    private void showDialog(String msg)
    {
        if(dialog!=null)
        {
            if(myTimerTask!=null&&alphaAnimation1!=null)
            {
                myTimerTask.cancel();
                alphaAnimation1.cancel();
            }
            dialog.setMessage(msg);
            dialog.show();
            dialog.setButtonType(Dialog.BUTTON_NEGATIVE, 10, true);
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

}
