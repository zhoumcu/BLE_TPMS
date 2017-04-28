package com.example.sid_fu.blecentral.widget;

/**
 * Created by Administrator on 2016/4/6.
 */
public interface DragGridBaseAdapter {
    /**
     * 重新排列数据
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);


}
