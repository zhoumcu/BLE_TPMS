package com.example.sid_fu.blecentral.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SelectPicPopupWindow;
import com.example.sid_fu.blecentral.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sid-fu on 2016/5/17.
 */
public class AddDeviceActivity extends ActionBarActivity {

    @Bind(R.id.img_photo)
    ImageView imgPhoto;
    @Bind(R.id.editText)
    EditText editText;
    @Bind(R.id.ed_name)
    EditText edName;
    @Bind(R.id.btn_add)
    Button btnAdd;
    private SelectPicPopupWindow menuWindow;
    /*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 3023;

    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    /*拍照的照片存储位置*/
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    /* 裁剪后图片存储位置*/
    private static final File PHOTO_CUED_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/Small");

    private File mCurrentPhotoFile;//照相机拍照得到的图片
    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_add_detail);
        ButterKnife.bind(this);
        intView();
         /*显示App icon左侧的back键*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void intView() {

    }

    @OnClick (R.id.btn_add)
    public void addDate()
    {
        if(!TextUtils.isEmpty(editText.getText().toString())&&!TextUtils.isEmpty(edName.getText().toString()))
        {
            User u = new User();
            u.setName(edName.getText().toString());
            new UserDao(this).add(u);
            Device deviceDate = new Device();
            deviceDate.setDeviceName(editText.getText().toString());
            deviceDate.setDeviceDescripe(edName.getText().toString());
            deviceDate.setImagePath(imagePath);
            Intent intent = new Intent();
            intent.setClass(AddDeviceActivity.this,ConfigDevice.class);
            intent.putExtra("device",deviceDate);
            startActivity(intent);
            finish();
        }
    }
    @OnClick(R.id.img_photo)
    public void addPhoto()
    {
        //实例化SelectPicPopupWindow
        menuWindow = new SelectPicPopupWindow(AddDeviceActivity.this, itemsOnClick);
        //显示窗口
        menuWindow.showAtLocation(AddDeviceActivity.this.findViewById(R.id.scrollView), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    String status=Environment.getExternalStorageState();
                    if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡
                        doTakePhoto();// 用户点击了从照相机获取
                    }
                    else{
                        ToastUtil.show("没有SD卡");
                    }
                    break;
                case R.id.btn_pick_photo:
                    doPickPhotoFromGallery();// 从相册中去获取
                    break;
                default:
                    break;
            }


        }

    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
                final Bitmap photo = data.getParcelableExtra("data");
                // 下面就是显示照片了
                System.out.println(photo);
                //缓存用户选择的图片
                imagePath = saveMyBitmap(photo);
//                img = getBitmapByte(photo);
                imgPhoto.setImageBitmap(photo);
                System.out.println("set new photo"+imagePath);
                break;
            }
            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                doCropPhoto(mCurrentPhotoFile);
                break;
            }
        }
    }
    public String saveMyBitmap(Bitmap mBitmap){
        File f = new File(PHOTO_CUED_DIR + getPhotoFileName());
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.e("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getPath();
    }
    /**
     * 用当前时间给取得的图片命名
     *
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }
    /**
     * 拍照获取图片
     *
     */
    protected void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            Logger.e(mCurrentPhotoFile.getPath());
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText,
                    Toast.LENGTH_LONG).show();
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }




    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText1,
                    Toast.LENGTH_LONG).show();
        }
    }

    // 封装请求Gallery的intent
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        return intent;
    }
    protected void doCropPhoto(File f) {
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (Exception e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 80);
        intent.putExtra("outputY", 80);
        intent.putExtra("return-data", true);
        return intent;
    }
}
