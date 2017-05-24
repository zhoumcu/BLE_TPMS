package com.example.sid_fu.blecentral.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.Result;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.http.HttpContext;
import com.example.sid_fu.blecentral.http.base.HttpResponseHandler;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
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
public class LoginActivity extends BaseActivity {

    @Bind(R.id.phone)
    EditText phone;
    @Bind(R.id.tv_pwd)
    EditText tvPwd;

    private LoadingDialog loadingDialog;

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
        setContentView(R.layout.de_activity_login);
        ButterKnife.bind(this);

        App.getInstance().addActivity(LoginActivity.this);

        loadingDialog = new LoadingDialog(this);
//        phone.setText("13480562458");
//        tvPwd.setText("123456");
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(SharedPreferences.getInstance().getString("telephone",null))){
            phone.setText(SharedPreferences.getInstance().getString("telephone",null));
        }
    }

    @OnClick(R.id.btnRegister)
    public void btnRegister() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("state","register");
        startActivity(intent);
    }
    @OnClick(R.id.btn_pwd)
    public void btnPwd() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("state","findPwd");
        startActivity(intent);
    }
    @OnClick(R.id.btnSure)
    public void btnLogin() {
        loadingDialog.setText("正在登录中。。。");
        loadingDialog.show();
        if (TextUtils.isEmpty(phone.getText().toString()) || TextUtils.isEmpty(tvPwd.getText().toString())) {
            loadingDialog.dismiss();
            ToastUtil.show("手机号码或者密码不能为空！");
            return;
        }
        User user = new User();
        user.setName(phone.getText().toString());
        user.setPassWord(tvPwd.getText().toString());
        HttpContext.getInstance().login(user, new HttpResponseHandler() {
            @Override
            public void onFailure(Request request, IOException e) {
                super.onFailure(request, e);
            }

            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Result result = Result.objectFromData(content);
                if(result.getStatus().getStatus().equals("0")){
                    Logger.e(content);
                    App.getInstance().speak("登录成功");
                    loadingDialog.dismiss();
                    ToastUtil.show("登录成功");
                    SharedPreferences.getInstance().putString("telephone",phone.getText().toString());
                    SharedPreferences.getInstance().putBoolean(Constants.IS_LOGIN, true);
                    boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
                    if(firstTimeUse) {
                        //initGuideGallery();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        App.getInstance().exit();
                    } else {
                        //initLaunchLogo();
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, CarListViewActivity.class);
                        startActivity(intent);
                        App.getInstance().exit();
                    }
                }else {
                    loadingDialog.dismiss();
                    ToastUtil.show("登录情况："+result.getStatus().getMsg());
                }
            }
        });
    }
}
