package com.example.sid_fu.blecentral.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.utils.Logger;

import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * Created by bob on 2015/1/28.
 */
public class LoadingDialog extends Dialog {

    private final TextView tvCount;
    private final LinearLayout bg_ground;
    private TextView mTextView;
    private Integer count;
    private Timer timer;
    private TimerTask timerTask;
    private OnListenerCallBack callBack;

    public interface OnListenerCallBack{
        public void onListenerCount();
    }
    public LoadingDialog(Context context) {

        super(context, R.style.WinDialog);
        setContentView(R.layout.de_ui_dialog_loading);
        mTextView = (TextView) findViewById(android.R.id.message);
        tvCount = (TextView) findViewById(R.id.tv_count);
        bg_ground = (LinearLayout)findViewById(R.id.bg_ground);
    }
    public void setBackgroundColor() {
        mTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        bg_ground.setBackground(getContext().getResources().getDrawable(R.drawable.bg_shape_white));
    }
    @Override
    public void show() {
        try {
            super.show();
        }catch (IllegalArgumentException e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        }catch (IllegalArgumentException e) {
            Logger.e(e.toString());
        }
    }
    public void setText(String s) {
        if (mTextView != null) {
            mTextView.setText(s);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setText(int res) {
        if (mTextView != null) {
            mTextView.setText(res);
            mTextView.setVisibility(View.VISIBLE);
        }
    }
    public void setCountNum(int count) {
        if (tvCount != null) {
            tvCount.setText(count+"");
            tvCount.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }
    public void startCount(OnListenerCallBack callBack) {
        this.callBack = callBack;
        count = Integer.valueOf(tvCount.getText().toString());
        timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                myHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    public void reStartCount(String str, int i) {
        setText(str);
        setCountNum(i);
        count = Integer.valueOf(tvCount.getText().toString());
    }
    public void stopCount() {
        if(isShowing())
            dismiss();
        if(timer!=null)
            timer.cancel();
        if(timerTask!=null) {
            timerTask.cancel();
            timerTask=null;
        }
        timer = null;
        if(callBack!=null)
            callBack.onListenerCount();
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                tvCount.setText(count+"");
                count--;
                if(count==-1) {
                    stopCount();
                }
            }
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopCount();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
