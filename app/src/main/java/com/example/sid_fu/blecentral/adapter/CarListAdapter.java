package com.example.sid_fu.blecentral.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.sid_fu.blecentral.R;
import com.example.sid_fu.blecentral.db.entity.CarBrand;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.pinyin.SortModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CarListAdapter extends BaseAdapter  {
    private List<CarBrand> list = null;
    private Context mContext;

    public CarListAdapter(Context mContext, List<CarBrand> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<CarBrand> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final CarBrand mContent = list.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.car_type_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvCarderailleur.setText("变速器类型:"+mContent.getDerailleur());
        viewHolder.tvCarweight.setText("车重:"+mContent.getWeight()+"KG");
        viewHolder.tvCarrealtime.setText("发布时间:"+mContent.getReleaseTime()+"年");
        viewHolder.tvCardisplace.setText("排量:"+mContent.getDisplacement()+"L");
        viewHolder.tvCarname.setText(mContent.getCname());
        viewHolder.tvCarname.setText(mContent.getCname());
        return view;
    }


    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    /**
     * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
     * <p/>
     * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
     * <p/>
     * B.本地路径:url="file://mnt/sdcard/photo/image.png";
     * <p/>
     * C.支持的图片格式 ,png, jpg,bmp,gif等等
     *
     * @param url
     * @return
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    private Bitmap getDiskBitmap(String pathString) {
        Logger.e(pathString);
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }


        return bitmap;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'car_type_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.tv_carname)
        TextView tvCarname;
        @Bind(R.id.tv_cardisplace)
        TextView tvCardisplace;
        @Bind(R.id.tv_carrealtime)
        TextView tvCarrealtime;
        @Bind(R.id.tv_carweight)
        TextView tvCarweight;
        @Bind(R.id.tv_carderailleur)
        TextView tvCarderailleur;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}