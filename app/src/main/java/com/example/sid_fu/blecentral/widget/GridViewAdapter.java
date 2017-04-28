package com.example.sid_fu.blecentral.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.sid_fu.blecentral.R;

import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> strList;
    private int hidePosition = AdapterView.INVALID_POSITION;

    public GridViewAdapter(Context context, List<String> strList) {
        this.context = context;
        this.strList = strList;
    }

    @Override
    public int getCount() {
        return strList.size();
    }

    @Override
    public String getItem(int position) {
        return strList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if(convertView == null) {
            view = new TextView(context);
            view.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        else {
            view = (TextView)convertView;
        }

        //hide时隐藏Text
        if(position != hidePosition) {
            view.setText(strList.get(position));
        }
        else {
            view.setText("");
        }
        view.setBackgroundResource(R.drawable.monitor_1);
        view.setId(position);

        return view;
    }

    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        strList.remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            strList.add(destPos+1, getItem(draggedPos));
            strList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            strList.add(destPos, getItem(draggedPos));
            strList.remove(draggedPos+1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }
}