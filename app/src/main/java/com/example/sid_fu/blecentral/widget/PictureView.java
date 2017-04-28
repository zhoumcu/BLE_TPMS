package com.example.sid_fu.blecentral.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sid-fu on 2016/5/16.
 */
public class PictureView extends Activity implements View.OnClickListener {
    private static final String TAG = "ConfigDevice";
    @Bind(R.id.img_qcode)
    ImageView imgQcode;
    @Bind(R.id.img_icon)
    ImageView imgIcon;
    @Bind(R.id.tv_name)
    TextView tvName;
    private String contentString;
    private Device device;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_picture_view);
        ButterKnife.bind(this);
        contentString = getIntent().getExtras().getString("macImage");
        device = (Device) getIntent().getExtras().getSerializable("device");
        initUI();
        /*显示App icon左侧的back键*/
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        App.getInstance().speak("扫我！扫我！可以轻松授权！");
    }

    private void initUI() {
        tvName.setText(device.getDeviceName());
        imgIcon.setImageBitmap(BitmapFactory.decodeFile("sdcard/DCIM/logo/" + device.getImagePath() + ".png"));
        try {
//            String contentString = "mac1:4564564654+mac2:78132132";
            if (!contentString.equals("")) {
                //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
                Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                imgQcode.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(PictureView.this, "Text can not be empty", Toast.LENGTH_SHORT).show();
            }
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @OnClick(R.id.img_qcode)
    public void setImgQcode() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
}
