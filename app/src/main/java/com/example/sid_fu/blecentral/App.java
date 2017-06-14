package com.example.sid_fu.blecentral;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;

import com.example.sid_fu.blecentral.db.DbHelper;
import com.example.sid_fu.blecentral.db.dao.DeviceDao;
import com.example.sid_fu.blecentral.utils.Logger;
import com.example.sid_fu.blecentral.utils.SoundPlayUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by bob on 2015/1/30.
 */
public class App extends Application implements Thread.UncaughtExceptionHandler{

    public static App app;
    public TextToSpeech textToSpeech;
    public DbHelper dbHelper;
    private Context mContext;

    public static DeviceDao getDeviceDao() {
        return deviceDao;
    }

    private static DeviceDao deviceDao;
    private List<Activity> mList = new LinkedList<Activity>();

    public App() {
        app = this;
    }

    public static synchronized App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        deviceDao= new DeviceDao(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        String device_token = UmengRegistrar.getRegistrationId(this);
        Logger.e(device_token);
        SoundPlayUtils.init(this);
//        LeakCanary.install(this);
        dbHelper = DbHelper.getInstance(this);
        //第一：默认初始化
        Bmob.initialize(this, "96fac7e033ee07d482b599280dd49f8c");
        BmobUpdateAgent.initAppVersion();
        //设置Thread Exception Handler
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    public void add(String str) {
        textToSpeech.addSpeech(str,"test");
        speak(str);
    }
    public void speak(String text) {

    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = app.getPackageManager();
            PackageInfo info = manager.getPackageInfo(app.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("uncaughtException");
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }).start();
    }
}
