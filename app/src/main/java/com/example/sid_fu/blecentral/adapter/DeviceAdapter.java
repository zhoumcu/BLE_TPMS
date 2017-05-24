package com.example.sid_fu.blecentral.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sid_fu.blecentral.App;
import com.example.sid_fu.blecentral.ui.activity.HomeActivity;
import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.ui.activity.CarInfoDetailActivity;
import com.example.sid_fu.blecentral.ui.activity.MainFrameForStartServiceActivity;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.utils.BitmapUtils;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SharedPreferences;
import com.example.sid_fu.blecentral.widget.PictureView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by sid-fu on 2016/5/17.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<Device> cList;
    private onCallBack callBack;

    public DeviceAdapter(Context context) {
        this.mContext = context;
    }

    public DeviceAdapter(Context context, List<Device> cList) {
        this.mContext = context;
        this.cList = cList;
    }

    @Override
    public int getCount() {
        return cList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (cList.get(position).getDefult() != null) {
            if (cList.get(position).getDefult().equals("true")) {
                holder.tvNormal.setVisibility(View.VISIBLE);
                holder.tvCurrent.setVisibility(View.VISIBLE);
//                holder.btnConnect.setVisibility(View.VISIBLE);
                holder.btnDetails.setEnabled(false);
                holder.btnDelete.setEnabled(false);
                holder.btnRl.setEnabled(true);
                holder.arrow.setVisibility(View.VISIBLE);
                holder.tvState.setEnabled(true);
                holder.tvState.setText("已连接");
                holder.btnDetails.setText("已默认");
//                holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            } else {
                holder.tvNormal.setVisibility(View.GONE);
                holder.tvCurrent.setVisibility(View.GONE);
//                holder.btnConnect.setVisibility(View.GONE);
                holder.btnDetails.setEnabled(true);
                holder.btnDelete.setEnabled(true);
                holder.btnRl.setEnabled(false);
                holder.arrow.setVisibility(View.GONE);
                holder.tvState.setEnabled(false);
                holder.tvState.setText("未连接");
                holder.btnDetails.setText("设为默认");
//                holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray));
            }
        } else {
            holder.tvNormal.setVisibility(View.GONE);
            holder.tvCurrent.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.GONE);
            holder.tvState.setEnabled(false);
            holder.tvState.setText("未连接");
            holder.btnDetails.setText("设为默认");
//            holder.btnConnect.setVisibility(View.GONE);
//                holder.bgGround.setBackgroundColor(mContext.getResources().getColor(R.color.dark_gray));
        }
        if(cList.get(position).getIsShare().equals("false")) {
            holder.btnBund.setEnabled(true);
            holder.btnNormal.setEnabled(true);
        }else {
            holder.btnBund.setEnabled(false);
            holder.btnNormal.setEnabled(false);
        }
        holder.tvTitle.setText(cList.get(position).getDeviceName());
        holder.tvContent.setText(cList.get(position).getDeviceDescripe());
        if (cList.get(position).getImagePath() != null){
//            Observable<Bitmap> observable = Observable.just("logo/"+cList.get(position).getImagePath()+".png")
//                    .map(new Func1<String, Bitmap>() {
//                        @Override
//                        public Bitmap call(String s) {
//                            return BitmapUtils.getImageFromAssetsFile(mContext,s);
//                        }
//                    });
//            observable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Action1<Bitmap>() {
//                        @Override
//                        public void call(Bitmap bitmap) {
//                            holder.imgIcon.setImageBitmap(bitmap);
//                        }
//                    });
            holder.imgIcon.setImageBitmap(BitmapUtils.getImageFromAssetsFile(mContext,"logo/"+cList.get(position).getImagePath()+".png"));
        }
//            holder.imgIcon.setImageBitmap(BitmapUtils.getImageFromAssetsFile(mContext,"logo/"+cList.get(position).getImagePath()+".png"));
        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<Boolean> observable = Observable.just(App.getDeviceDao().updateDefult(cList.get(position).getId(), "true"));
                observable.unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean b) {
                                if(b){
                                    SharedPreferences.getInstance().putBoolean("isAppOnForeground",false);
//                                    SharedPreferences.getInstance().putInt(Constants.LAST_DEVICE_ID, cList.get(position).getId());
                                    callBack.setOnClick(cList.get(position));
                                    List<Device> cList = HomeActivity.fillterDate(App.getDeviceDao().listByAll());
                                    updateData(cList);
                                }
                            }
                        });
            }
        });

//        if (!contentString.equals("")) {
//            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小（350*350）
//            Bitmap qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
//            holder.btnCode.setCompoundDrawables(qrCodeBitmap.);
//        }
        holder.btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                App.getInstance().speak("是否确定删除，如果删除，数据将会永久丢失，设备信息将无法找回！请慎重选择！");
                new AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题
                        .setMessage("是否确定删除，如果删除，数据将会永久丢失，设备信息将无法找回！请慎重选择！")//设置显示的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                // TODO Auto-generated method stub
                                App.getDeviceDao().delete(cList.get(position));
                                cList.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//响应事件
                        // TODO Auto-generated method stub
                        Logger.e(" 请保存数据！");
                    }
                }).show();//在按键响应事件中显示此对话框
            }
        });
        holder.btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentString = "vlt_tpms_device"
                        + "|" + cList.get(position).getLeft_FD()
                        + "|" + cList.get(position).getRight_FD()
                        + "|" + cList.get(position).getLeft_BD()
                        + "|" + cList.get(position).getRight_BD()
                        + "|" +cList.get(position).getDeviceName()
                        + "|" +cList.get(position).getDeviceDescripe();
                Intent intent = new Intent();
                intent.setClass(mContext, PictureView.class);
                intent.putExtra("macImage", contentString);
                intent.putExtra("device",cList.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.btnRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(mContext, MainFrameForStartServiceActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("DB_ARTICLES", cList.get(position));
                mBundle.putInt("DB_ID", cList.get(position).getId());
                intent.putExtras(mBundle);
                mContext.startActivity(intent);
            }
        });
        holder.btnBund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CarInfoDetailActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("id", cList.get(position).getCarID());
                Logger.e("id",cList.get(position).getCarID()+"");
                mBundle.putInt("state", CarInfoDetailActivity.DETAILS);
                Logger.e(cList.get(position).toString());
                intent.putExtras(mBundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public void updateData(List<Device> cList) {
        this.cList = cList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        @Bind(R.id.img_icon)
        ImageView imgIcon;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_current)
        TextView tvCurrent;
        @Bind(R.id.tv_normal)
        TextView tvNormal;
        @Bind(R.id.btn_connect)
        Button btnConnect;
        @Bind(R.id.img_ecode)
        ImageView imgEcode;
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.tv_cartype)
        TextView tvCartype;
        @Bind(R.id.btn_rl)
        RelativeLayout btnRl;
        @Bind(R.id.btn_bund)
        TextView btnBund;
        @Bind(R.id.btn_normal)
        TextView btnNormal;
        @Bind(R.id.btn_details)
        TextView btnDetails;
        @Bind(R.id.btn_code)
        TextView btnCode;
        @Bind(R.id.btn_delete)
        TextView btnDelete;
        @Bind(R.id.bg_ground)
        LinearLayout bgGround;
        @Bind(R.id.arrow)
        ImageView arrow;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setonCallBack(onCallBack callBack)
    {
        this.callBack = callBack;
    }
    public interface onCallBack{
        public void setOnClick(Device device);
    }
}
