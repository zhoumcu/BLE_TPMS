package com.example.sid_fu.blecentral;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sid_fu.blecentral.activity.ConfigDevice;
import com.example.sid_fu.blecentral.ui.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/5/27.
 */
public class NotifyDialog extends BaseActivity {
    public static final String ACTION_BTN_STATE = "button_state";
    public static final String ACTION_BTN_NEXT = "button_next";
    public static final String BTN_STATE = "btn_state";
    public static final String ACTION_CHANGE_STATE = "action_change_state";
    @Bind(R.id.tv_notify)
    TextView tvNotify;
    @Bind(R.id.btn_state)
    Button btnState;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.img_icon)
    ImageView imgIcon;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    @Bind(R.id.ln_finish)
    LinearLayout lnFinish;
    @Bind(R.id.btn_nofinish)
    LinearLayout btnNofinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_notify_dialog);
        ButterKnife.bind(this);
        String pairedOk = getIntent().getExtras().getString(ConfigDevice.PAIRED_OK);
        boolean noneNext = getIntent().getExtras().getBoolean(ConfigDevice.NONE_NEXT);
        btnState.setText(pairedOk);
        if (pairedOk.equals("完成")) {
            onFinish();
            tvNotify.setText("完成配置，请点击完成，进行下一个配置");
        }else{
            tvNotify.setText("配置超时，请点击重试按钮进行重新配置;如果多次配置均已无法配置，请点击跳过,进行配置下一个，" +
                    "之后在解绑/绑定功能中进行绑定即可");
        }
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

//        tvNotify.setText("完成配置！");
    }
    private void onFinish()
    {
        lnFinish.setVisibility(View.VISIBLE);
        btnNofinish.setVisibility(View.GONE);
        imgIcon.setImageDrawable(getResources().getDrawable(R.mipmap.b_right));
    }
    @OnClick(R.id.btn_finish)
    public void btnFinish() {
        sendResultForFrom(ACTION_BTN_NEXT, 100);
        App.getInstance().speak("完成");
    }
    @OnClick(R.id.ln_finish)
    public void llfinish() {
        sendResultForFrom(ACTION_BTN_NEXT, 100);
        App.getInstance().speak("完成");
    }
    @OnClick(R.id.btn_state)
    public void btnState() {
        if (btnState.getText().equals("完成")) {
            sendResultForFrom(ACTION_BTN_NEXT, 100);
            App.getInstance().speak("完成");
        } else {
            sendResultForFrom(ACTION_BTN_STATE, 102);
            App.getInstance().speak("请重试");
        }
    }

    @OnClick(R.id.btn_next)
    public void btnNext() {
        sendResultForFrom(ACTION_BTN_NEXT, 101);
    }

    private void sendResultForFrom(String action, int responseCode) {
        Intent intent = new Intent(action);
        intent.putExtra(BTN_STATE, responseCode);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHANGE_STATE);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String action = intent.getAction();
            if (ACTION_CHANGE_STATE.equals(action)) {
                btnState.setText(intent.getExtras().getString(BTN_STATE));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
