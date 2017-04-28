package com.example.sid_fu.blecentral.model;

import java.io.Serializable;

/**
 * author：Administrator on 2016/10/12 15:19
 * company: xxxx
 * email：1032324589@qq.com
 */

public class TyreModel implements Serializable{

    /**
     * 长时间未收到数据或者模块异常
     */
    public int Count = 0;
    /**
     * 重复报警标志
     */
    public boolean isReNotify;
    /**
     * 指定轮胎的蓝牙address
     */
    public  String typeDevice ;
    /**
     *  记录前1s异常状况内容
     */
    public String preErrorContent;

    public TyreModel(){

    }

    public static enum  status{
        正常(0),加气(1), 慢漏气(2),保留3(3),快漏气(4),保留5(5),保留6(6),保留7(7),保留(8);
        private final int mValue;
        status(int value)
        {
            mValue = value;
        }
    }
}
