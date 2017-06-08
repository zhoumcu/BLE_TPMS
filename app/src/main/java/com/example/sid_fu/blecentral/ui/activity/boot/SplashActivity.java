package com.example.sid_fu.blecentral.ui.activity.boot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.HomeActivity;
import com.example.sid_fu.blecentral.ui.activity.base.BaseFragmentActivity;
import com.example.sid_fu.blecentral.ui.activity.login.LoginActivity;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.utils.SoundPlayUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tiansj on 15/7/29.
 */
public class SplashActivity extends BaseFragmentActivity {

    @Bind(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_splash);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT>=23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
        SoundPlayUtils.play(SoundPlayUtils.WELCOME_SOUND);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initActivity();
            }
        }, 4000);
    }

    private void initActivity() {
        boolean firstTimeUse = SharedPreferences.getInstance().getBoolean(Constants.IS_LOGIN, false);
        if (firstTimeUse) {
            this.goActivity(HomeActivity.class,null);
            finish();
        } else {
            this.goActivity(LoginActivity.class,null);
            finish();
        }
    }
}
