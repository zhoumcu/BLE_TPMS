package com.example.sid_fu.blecentral.db.entity;

import com.example.sid_fu.blecentral.utils.DigitalTrans;

/**
 * Created by Administrator on 2016/6/27.
 */
public class RecordData {

    private int deviceId;
    private String name;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private String data;
    private String createDate;
    private String state;

    public String toString()
    {
        return "deviceId"+deviceId + "name"+name + "data"+ data+ "createDate"+ createDate+  "state"+state ;
    }
}
