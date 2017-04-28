package com.example.sid_fu.blecentral.helper;

import android.content.Context;

import com.example.sid_fu.blecentral.db.dao.UserDao;
import com.example.sid_fu.blecentral.db.entity.Device;
import com.example.sid_fu.blecentral.db.entity.User;
import com.example.sid_fu.blecentral.utils.Logger;

/**
 * author：Administrator on 2016/9/30 11:58
 * company: xxxx
 * email：1032324589@qq.com
 */

public class HomeDataHelper {
    public static Device getData(Context contexts,String[]date){
        Logger.e(date.length+"");
        if (date[0].equals("vlt_tpms_device")&&date.length==11) {
            Logger.e(date[0]);
            //添加数据
            User user = new UserDao(contexts).get(1);
            Device deviceDate = new Device();
            deviceDate.setDeviceName(date[5]+"--授权");
            deviceDate.setDeviceDescripe(date[6]);
            deviceDate.setImagePath(date[7]);
            deviceDate.setRight_BD(date[4]);
            deviceDate.setLeft_BD(date[3]);
            deviceDate.setRight_FD(date[2]);
            deviceDate.setLeft_FD(date[1]);
            deviceDate.setIsShare("true");
            deviceDate.setBackMinValues(Float.valueOf(date[8]));
            deviceDate.setBackmMaxValues(Float.valueOf(date[9]));
            deviceDate.setFromMinValues(Integer.valueOf(date[10]));
            deviceDate.setUser(user);
            return deviceDate;
        }else if (date[0].equals("vlt_tpms_device")&&date.length==8) {
            Logger.e(date[0]);
            //添加数据
            User user = new UserDao(contexts).get(1);
            Device deviceDate = new Device();
            deviceDate.setDeviceName(date[5]+"--授权");
            deviceDate.setDeviceDescripe(date[6]);
            deviceDate.setImagePath(date[7]);
            deviceDate.setRight_BD(date[4]);
            deviceDate.setLeft_BD(date[3]);
            deviceDate.setRight_FD(date[2]);
            deviceDate.setLeft_FD(date[1]);
            deviceDate.setIsShare("true");
            deviceDate.setUser(user);
            return deviceDate;
        }else if (date[0].equals("vlt_tpms_device")&&date.length==7) {
            Logger.e(date[0]);
            //添加数据
            User user = new UserDao(contexts).get(1);
            Device deviceDate = new Device();
            deviceDate.setDeviceName(date[5]+"--授权");
            deviceDate.setDeviceDescripe(date[6]);
//                deviceDate.setImagePath(date[7]);
            deviceDate.setRight_BD(date[4]);
            deviceDate.setLeft_BD(date[3]);
            deviceDate.setRight_FD(date[2]);
            deviceDate.setLeft_FD(date[1]);
            deviceDate.setIsShare("true");
            deviceDate.setUser(user);
            return deviceDate;
        }
        return null;
    }
}
