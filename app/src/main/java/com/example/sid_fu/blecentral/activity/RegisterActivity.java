package com.example.sid_fu.blecentral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.http.HttpContext;
import com.example.sid_fu.blecentral.http.base.HttpResponseHandler;
import com.example.sid_fu.blecentral.ui.activity.BaseActionBarActivity;
import com.example.sid_fu.blecentral.utils.CommonUtils;
import com.example.sid_fu.blecentral.utils.ToastUtil;
import com.example.sid_fu.blecentral.widget.LoadingDialog;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;


/**
 * Created by tiansj on 15/7/31.
 */
public class RegisterActivity extends BaseActionBarActivity {

    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.code)
    EditText code;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.btn_code)
    Button btnCode;
    @Bind(R.id.checkBox)
    CheckBox checkBox;
    private LoadingDialog loadingDialog;
    private String state;
    private String geCode;
    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
//        {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        }
        setContentView(R.layout.de_activity_register);
        ButterKnife.bind(this);
        App.getInstance().addActivity(RegisterActivity.this);
        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        state = getIntent().getStringExtra("state");
        if (state.equals("findPwd")) {
            actionBar.setTitle(R.string.findpwd);
        } else {
            actionBar.setTitle(R.string.register);
        }
        loadingDialog = new LoadingDialog(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    ToastUtil.show("手机号码不能为空");
                    return;
                }
                if(phone.getText().toString().length()!=11) {
                    ToastUtil.show("手机号码位数不正确");
                    return;
                }
                time.start();
                geCode = CommonUtils.generateCheckPass();
                User user = new User();
                user.setPhotoNumber(phone.getText().toString());
                user.setPassWord(geCode);
                HttpContext.getInstance().sendCode(user, new HttpResponseHandler() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        super.onFailure(request, e);
                    }
                    @Override
                    public void onSuccess(int statusCode, String content) {
                        super.onSuccess(statusCode, content);
                        App.getInstance().speak("发送验证码成功");
                        loadingDialog.dismiss();
                        ToastUtil.show("发送验证码成功");
                    }
                });
            }
        });
        phone.addTextChangedListener(mEditText);
        tvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("已经阅读！");
                checkBox.setChecked(true);
                Intent intent = new Intent(RegisterActivity.this,PrivateActivity.class);
                startActivity(intent);
            }
        });
        time = new TimeCount(60000, 1000);
    }

    TextWatcher mEditText = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(phone.getText().length() == 11){
               btnCode.setEnabled(true);
            }
        }
    };
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            btnCode.setText("获取验证码");
            btnCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            btnCode.setClickable(false);//防止重复点击
            btnCode.setText(millisUntilFinished / 1000 + "s");
        }
    }
    @OnClick(R.id.btnSure)
    public void btnLogin() {
        loadingDialog.setText("校验验证码。。。");
        loadingDialog.show();
        if (TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(code.getText().toString())) {
            loadingDialog.dismiss();
            ToastUtil.show("手机号码和验证码不能为空");
            return;
        }else{
            if(!code.getText().toString().equals(geCode)) {
                loadingDialog.dismiss();
                ToastUtil.show("验证码不正确！");
                return;
            }
        }
        if(!checkBox.isChecked()) {
            ToastUtil.show("请先阅读用户使用协议及隐私条款");
            return;
        }
        Intent intent = new Intent(RegisterActivity.this, RegisterNextActivity.class);
        intent.putExtra("phoneNumber", phone.getText().toString());
        intent.putExtra("state", state);
        startActivity(intent);
//        finish();
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
