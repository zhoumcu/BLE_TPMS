package com.example.sid_fu.blecentral.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.ui.activity.HomeActivity;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.http.HttpContext;
import com.example.sid_fu.blecentral.http.base.HttpResponseHandler;
import com.example.sid_fu.blecentral.ui.activity.BaseActionBarActivity;
import com.example.sid_fu.blecentral.utils.Constants;
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
public class RegisterNextActivity extends BaseActionBarActivity {

    @Bind(R.id.pwd)
    EditText pwd;
    @Bind(R.id.repwd)
    EditText repwd;
    @Bind(R.id.btnSure)
    Button btnSure;
    private LoadingDialog loadingDialog;
    private String phoneNumber;
    private String state;

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
        setContentView(R.layout.de_activity_register1);
        ButterKnife.bind(this);

        App.getInstance().addActivity(RegisterNextActivity.this);

        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        state = getIntent().getExtras().getString("state");
        if(state.equals("findPwd"))
        {
            actionBar.setTitle(R.string.findpwd);
        }else
        {
            actionBar.setTitle(R.string.registerpwd);
        }
        loadingDialog = new LoadingDialog(this);
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btnSure)
    public void btnLogin() {
        if (TextUtils.isEmpty(pwd.getText().toString()) || TextUtils.isEmpty(repwd.getText().toString())) {
//            loadingDialog.dismiss();
            ToastUtil.show("密码不能为空！");
            return;
        }else{
            if(!pwd.getText().toString().equals(repwd.getText().toString()))
            {
//                loadingDialog.dismiss();
                ToastUtil.show("两次输入密码不相同！");
                return;
            }
        }
        if(state.equals("findpwd"))
        {
            loadingDialog.setText("正在找回密码中。。。");
            loadingDialog.show();
            User user = new User();
            user.setPhotoNumber(phoneNumber);
            user.setPassWord(pwd.getText().toString());
            HttpContext.getInstance().findPwd(user, new HttpResponseHandler() {
                @Override
                public void onFailure(Request request, IOException e) {
                    super.onFailure(request, e);
                }

                @Override
                public void onSuccess(int statusCode, String content) {
                    super.onSuccess(statusCode, content);
                    finish();
                    App.getInstance().speak("密码找回");
                    loadingDialog.dismiss();
                    ToastUtil.show("密码找回");
                }
            });
        }else
        {
            loadingDialog.setText("正在注册中。。。");
            loadingDialog.show();
            User user = new User();
            user.setName(phoneNumber);
            user.setPassWord(pwd.getText().toString());
            HttpContext.getInstance().register(user, new HttpResponseHandler() {
                @Override
                public void onFailure(Request request, IOException e) {
                    super.onFailure(request, e);
                }

                @Override
                public void onSuccess(int statusCode, String content) {
                    super.onSuccess(statusCode, content);
                    App.getInstance().speak("注册成功");
                    loadingDialog.dismiss();
                    ToastUtil.show("注册成功");
                    SharedPreferences.getInstance().putString("telephone",phoneNumber);
                    boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.FIRST_CONFIG, false);
                    if(firstTimeUse) {
                        //initGuideGallery();
                        Intent intent = new Intent();
                        intent.setClass(RegisterNextActivity.this, HomeActivity.class);
                        startActivity(intent);
                        App.getInstance().exit();
                    } else {
                        //initLaunchLogo();
                        Intent intent = new Intent();
                        intent.setClass(RegisterNextActivity.this, CarListViewActivity.class);
                        startActivity(intent);
                        finish();
                        App.getInstance().exit();
                    }
                }
            });
        }
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
