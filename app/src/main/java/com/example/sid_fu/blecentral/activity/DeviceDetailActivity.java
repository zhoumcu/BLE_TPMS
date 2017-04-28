package com.example.sid_fu.blecentral.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.ui.activity.BaseActionBarActivity;
import com.example.sid_fu.blecentral.widget.PictureView;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sid-fu on 2016/5/17.
 */
public class DeviceDetailActivity extends BaseActionBarActivity {

    @Bind(R.id.img_photo)
    ImageView imgPhoto;
    @Bind(R.id.editText)
    EditText editText;
    @Bind(R.id.ed_name)
    EditText edName;
    @Bind(R.id.btn_add)
    Button btnAdd;
    @Bind(R.id.img_qcode)
    ImageView imgQcode;
    private String contentString;
    private int deviceId;
    private Device articles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(SharedPreferences.getInstance().getString(Constants.LANDORPORT,Constants.DEFIED).equals("横屏"))
//        {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
//        }
        setContentView(R.layout.aty_detail);
        ButterKnife.bind(this);
        deviceId = getIntent().getExtras().getInt("DB_ID");
        //刷新数据库
        articles = App.getDeviceDao().get(deviceId);
        intView();
         /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void intView() {
        try {
            contentString = "vlt_tpms_device" + "|" + articles.getLeft_FD() + "|" + articles.getRight_FD() + "|" + articles.getLeft_BD() + "|" + articles.getRight_BD();//"mac1:4564564654+mac2:78132132";
            if (!contentString.equals("")) {
                //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                imgQcode.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(DeviceDetailActivity.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @OnClick(R.id.img_qcode)
    public void setImgQcode() {
        Intent intent = new Intent();
        intent.setClass(DeviceDetailActivity.this, PictureView.class);
        intent.putExtra("macImage", contentString);
        startActivity(intent);
    }
    @OnClick (R.id.btn_add)
    public void addDate() {
        if(!TextUtils.isEmpty(editText.getText().toString())&&!TextUtils.isEmpty(edName.getText().toString()))
        {
            User u = new User();
            u.setName(edName.getText().toString());
            new UserDao(this).add(u);

            Intent intent = new Intent();
            intent.setClass(DeviceDetailActivity.this,ConfigDevice.class);
            startActivity(intent);
            finish();
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
