package com.example.sid_fu.blecentral.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.activity.LoginActivity;
import com.example.sid_fu.blecentral.activity.PrivateActivity;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.utils.Constants;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.widget.PictureView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class PersonSetting extends BaseActionBarActivity implements View.OnClickListener {
    private static final String TAG = "ConfigDevice";
    @Bind(R.id.img_qcode)
    ImageView imgQcode;
    @Bind(R.id.tv_email)
    TextView tvEmail;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_changetheme)
    TextView tvChangetheme;
    @Bind(R.id.tv_preesure)
    TextView tvPreesure;
    @Bind(R.id.tv_temp)
    TextView tvTemp;
    @Bind(R.id.switch2)
    Switch switch2;
    @Bind(R.id.tv_land_port)
    TextView tvLandPort;
    private String contentString;
    //    private ManageDevice manageDevice;
    private List<Device> articles;
    private PersonSetting mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_person_config);
        mContext = PersonSetting.this;
        ButterKnife.bind(this);
        articles = new DeviceDao(this).listByUserId(1);
        initUI();
        /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        App.getInstance().addActivity(this);
    }

    private void initUI() {
//        manageDevice = new ManageDevice();
        findViewById(R.id.access).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonSetting.this,PrivateActivity.class);
                startActivity(intent);
            }
        });
//        findViewById(R.id.bg_ground).setBackgroundDrawable(new BitmapDrawable(BitmapUtils.readBitMap(mContext,R.mipmap.g_bg)));
        tvEmail.setText(SharedPreferences.getInstance().getString("telephone", "10086"));
        tvPreesure.setText(SharedPreferences.getInstance().getString(Constants.PRESSUER_DW, "Bar"));
        tvTemp.setText(SharedPreferences.getInstance().getString(Constants.TEMP_DW, "℃"));
        tvLandPort.setText(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED));
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.getInstance().putBoolean(Constants.DAY_NIGHT, isChecked);
            }
        });
        switch2.setChecked(SharedPreferences.getInstance().getBoolean(Constants.DAY_NIGHT, false));
    }

    @OnClick(R.id.img_qcode)
    public void setImgQcode() {
        Intent intent = new Intent();
        intent.setClass(PersonSetting.this, PictureView.class);
        intent.putExtra("macImage", contentString);
        startActivity(intent);
    }

    @OnClick(R.id.tv_preesure)
    public void choocesPre() {
        new AlertDialog.Builder(this)
                .setTitle("气压单位")
                .setSingleChoiceItems(R.array.pressure, SharedPreferences.getInstance().getInt(Constants.PRESSUER_DW_NUM,0), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.pressure)[which];
                        tvPreesure.setText(hoddy);
                        SharedPreferences.getInstance().putInt(Constants.PRESSUER_DW_NUM, which);
                        SharedPreferences.getInstance().putString(Constants.PRESSUER_DW, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvPreesure.setText(getResources().getStringArray(R.array.pressure)[which]);
//                    }
//                })
                .show();
    }
    @OnClick(R.id.tv_land_port)
    public void choocesLandOrPort() {
        new AlertDialog.Builder(this)
                .setTitle("切换屏幕模式")
                .setSingleChoiceItems(R.array.land, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.land)[which];
                        tvLandPort.setText(hoddy);
                        SharedPreferences.getInstance().putString(Constants.LANDORPORT, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvPreesure.setText(getResources().getStringArray(R.array.pressure)[which]);
//                    }
//                })
                .show();
    }
    @OnClick(R.id.tv_temp)
    public void choocesTemp() {
        new AlertDialog.Builder(this)
                .setTitle("温度单位")
                .setSingleChoiceItems(R.array.temp, SharedPreferences.getInstance().getInt(Constants.TEMP_DW_NUM,0), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String hoddy = getResources().getStringArray(R.array.temp)[which];
                        tvTemp.setText(hoddy);
                        SharedPreferences.getInstance().putInt(Constants.TEMP_DW_NUM, which);
                        SharedPreferences.getInstance().putString(Constants.TEMP_DW, hoddy);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        tvTemp.setText(getResources().getStringArray(R.array.temp)[which]);
//                    }
//                })
                .show();
    }

    @OnClick(R.id.btn_login)
    public void logout() {
        Intent intent = new Intent();
        intent.setClass(PersonSetting.this, LoginActivity.class);
        startActivity(intent);
        //禁止后台推送
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
        SharedPreferences.getInstance().putBoolean(Constants.IS_LOGIN, false);
        App.getInstance().exit();
    }

    @Override
    public void onClick(View v) {

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.getInstance().putBoolean("isAppOnForeground",true);
    }
}
